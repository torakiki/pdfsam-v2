/*
 * Created on 14-Dec-2006
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.plugin.encrypt.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.encrypt.listeners.EncryptionTypeComboActionListener;
/** 
 * Plugable JPanel provides a GUI for encrypt functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class EncryptMainGUI extends AbstractPlugablePanel implements PropertyChangeListener{

	private static final long serialVersionUID = -190757698317016422L;

	private static final Logger log = Logger.getLogger(EncryptMainGUI.class.getPackage().getName());
	
	private JTextField outPrefixTextField;
    private SpringLayout encryptPanelLayout;
    private SpringLayout destinationPanelLayout;
    private JTextField destFolderText;
    private SpringLayout optionsPanelLayout;
    private SpringLayout encryptSpringLayout;
    private JTextField userPwdField;
	private JTextField ownerPwdField; 
	private JComboBox encryptType;
    private JHelpLabel prefixHelpLabel;
    private JHelpLabel destinationHelpLabel; 	
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER);

	private Configuration config;
	
//file_chooser    
    private final JFileChooser browseDestFileChooser = new JFileChooser();

//button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
//key_listeners
    private final EnterDoClickListener browseEnterKeyListener = new EnterDoClickListener(browseDestButton);
    private final EnterDoClickListener runEnterKeyListener = new EnterDoClickListener(runButton);
    
//encrypt_check
	private final JCheckBox[] permissionsCheck = new JCheckBox[8];
    private final JCheckBox allowAllCheck = new JCheckBox();
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);

//focus policy 
    private final EncryptFocusPolicy encryptfocusPolicy = new EncryptFocusPolicy();

//panels
    private final JPanel encryptOptionsPanel = new JPanel();
    private final JPanel destinationPanel = new JPanel();
    private final JPanel outputOptionsPanel = new JPanel();

//labels    
    final JLabel encryptOptionsLabel = new JLabel();
    final JLabel destFolderLabel = new JLabel();
    final JLabel outputOptionsLabel = new JLabel();
    final JLabel outPrefixLabel = new JLabel();
    final JLabel ownerPwdLabel = new JLabel();
    final JLabel userPwdLabel = new JLabel();
    final JLabel encryptTypeLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
    
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_VERSION = "0.2.1e";
	
    public final static String RC4_40 = "RC4-40b";
	public final static String RC4_128 = "RC4-128b";
	public final static String AES_128 = "AES-128b";
	
	public final static int DPRINT = 0;
	public final static int PRINT = 1;
	public final static int COPY = 2;
	public final static int MODIFY = 3;
	public final static int FILL = 4;
	public final static int SCREEN = 5;
	public final static int ASSEMBLY = 6;
	public final static int ANNOTATION = 7;
    
/**
 * Constructor
 *
 */    
    public EncryptMainGUI() {
        super();
        initialize();
        
    }

    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/encrypt.png");
        setPreferredSize(new Dimension(500,700));        
//        
        encryptSpringLayout = new SpringLayout();
        setLayout(encryptSpringLayout);
        add(selectionPanel);

//FILE_CHOOSER
        browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//END_FILE_CHOOSER

//ENCRYPT_SECTION
        optionsPanelLayout = new SpringLayout();
        encryptOptionsPanel.setLayout(optionsPanelLayout);
        encryptOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(encryptOptionsPanel);

		ownerPwdLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Owner password:"));
		encryptOptionsPanel.add(ownerPwdLabel);
		
        ownerPwdField = new JTextField();
        ownerPwdField.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Owner password (Max 32 chars long)"));
        ownerPwdField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        encryptOptionsPanel.add(ownerPwdField);

		userPwdLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"User password:"));
		encryptOptionsPanel.add(userPwdLabel);

        userPwdField = new JTextField();
        userPwdField.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"User password (Max 32 chars long)"));
        userPwdField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        encryptOptionsPanel.add(userPwdField);

        encryptTypeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Encryption angorithm:"));
		encryptOptionsPanel.add(encryptTypeLabel);		

        String[] eTypes = {EncryptMainGUI.RC4_40, EncryptMainGUI.RC4_128, EncryptMainGUI.AES_128};
        encryptType = new JComboBox(eTypes);
		encryptOptionsPanel.add(encryptType);		

        permissionsCheck[EncryptMainGUI.PRINT] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Print"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.PRINT]);

        permissionsCheck[EncryptMainGUI.DPRINT] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Low quality print"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.DPRINT]);

        permissionsCheck[EncryptMainGUI.COPY] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Copy or extract"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.COPY]);

        permissionsCheck[EncryptMainGUI.MODIFY] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Modify"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.MODIFY]);

        permissionsCheck[EncryptMainGUI.ANNOTATION] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Add or modify text annotations"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.ANNOTATION]);

        permissionsCheck[EncryptMainGUI.FILL] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Fill form fields"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.FILL]);

        permissionsCheck[EncryptMainGUI.SCREEN] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Extract for use by accessibility dev."));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.SCREEN]);

        permissionsCheck[EncryptMainGUI.ASSEMBLY] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Manipulate pages and add bookmarks"));
        encryptOptionsPanel.add(permissionsCheck[EncryptMainGUI.ASSEMBLY]);

        allowAllCheck.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Allow all"));
        encryptOptionsPanel.add(allowAllCheck);
//END_ENCRYPT_SECTION
        encryptOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Encrypt options:"));
        add(encryptOptionsLabel);

//UNSELECT_OTHERS_LISTENER
		allowAllCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
					if(allowAllCheck.isSelected()){
						for(int i=0; i<permissionsCheck.length; i++){
							permissionsCheck[i].setEnabled(false);
						}
					}else{					
						String encType = (String)encryptType.getSelectedItem();
	        			if(encType.equals(EncryptMainGUI.RC4_40)){
	        		        permissionsCheck[EncryptMainGUI.PRINT].setEnabled(true);
	        		        permissionsCheck[EncryptMainGUI.DPRINT].setEnabled(false);
	        		        permissionsCheck[EncryptMainGUI.COPY].setEnabled(true);
	        		        permissionsCheck[EncryptMainGUI.MODIFY].setEnabled(true);
	        		        permissionsCheck[EncryptMainGUI.ANNOTATION].setEnabled(true);
	        		        permissionsCheck[EncryptMainGUI.FILL].setEnabled(false);
	        		        permissionsCheck[EncryptMainGUI.SCREEN].setEnabled(false);
	        		        permissionsCheck[EncryptMainGUI.ASSEMBLY].setEnabled(false);
	        			}else{
							for(int i=0; i<permissionsCheck.length; i++){
								permissionsCheck[i].setEnabled(true);
							}
						}
					}
			}
        });
//END_RADIO_LISTENERS 
//DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL        
        destFolderLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder:"));
        add(destFolderLabel);
        
        destFolderText = new JTextField();
        destFolderText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destinationPanel.add(destFolderText);

//CHECK_BOX
        overwriteCheckbox.setSelected(true);
        destinationPanel.add(overwriteCheckbox);
        
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        destinationPanel.add(outputCompressedCheck);
        
        destinationPanel.add(versionCombo);        
//END_CHECK_BOX  
        destinationPanel.add(outputVersionLabel);
        browseDestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File chosenFile = null;                
                if (browseDestFileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION){
                    chosenFile = browseDestFileChooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosenFile != null && chosenFile.isDirectory()){
                    try{
                    	destFolderText.setText(chosenFile.getAbsolutePath());
                    }
                    catch (Exception ex){
                    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                        
                    }
                }
                
            }
        });
        destinationPanel.add(browseDestButton);
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Use the same output folder as the input file or choose a folder.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files.")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"PDF version 1.5 or above.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION         
        outputOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output options:"));
        add(outputOptionsLabel);
        encryptType.addItemListener(new EncryptionTypeComboActionListener(allowAllCheck, permissionsCheck, versionCombo));    
        encryptType.setSelectedIndex(0);
//S_PANEL
        outputOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        encryptPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(encryptPanelLayout);
        add(outputOptionsPanel);

        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);

        outPrefixTextField = new JTextField();
        outPrefixTextField.setText("pdfsam_");
        outPrefixTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        outputOptionsPanel.add(outPrefixTextField);
//END_S_PANEL
//      HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it contains \"[TIMESTAMP]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it doesn't contain \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Available variables: [TIMESTAMP], [BASENAME].")+"</p>"+
    		"</body></html>";
	    prefixHelpLabel = new JHelpLabel(helpTextPrefix, true);
	    outputOptionsPanel.add(prefixHelpLabel);
//END_HELP_LABEL_PREFIX        
//RUN_BUTTON
        //listener
	    runButton.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
            	if (WorkExecutor.getInstance().getRunningThreads() > 0 || selectionPanel.isAdding()){
                    log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Please wait while all files are processed.."));
                    return;
                }      
				final LinkedList args = new LinkedList();
                //validation and permission check are demanded to the CmdParser object
                try{
					args.addAll(getEncPermissions(permissionsCheck, allowAllCheck));

					PdfSelectionTableItem item = null;
                	PdfSelectionTableItem[] items = selectionPanel.getTableRows();
                	for (int i = 0; i < items.length; i++){
						item = items[i];
						args.add("-"+EncryptParsedCommand.F_ARG);
                        String f = item.getInputFile().getAbsolutePath();
                        if((item.getPassword()) != null && (item.getPassword()).length()>0){
    						log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
    						f +=":"+item.getPassword();
    					}
    					args.add(f);                        						
					}
                	
                    args.add("-"+EncryptParsedCommand.P_ARG);
                    args.add(outPrefixTextField.getText());
                    args.add("-"+EncryptParsedCommand.APWD_ARG);
                    args.add(ownerPwdField.getText());
                    args.add("-"+EncryptParsedCommand.UPWD_ARG);
                    args.add(userPwdField.getText());
                    //check if is needed page option
					args.add("-"+EncryptParsedCommand.ETYPE_ARG);
					args.add(getEncAlg((String)encryptType.getSelectedItem()));
                    args.add("-"+EncryptParsedCommand.O_ARG);                   
                    args.add(destFolderText.getText());

                    if (overwriteCheckbox.isSelected()) args.add("-"+EncryptParsedCommand.OVERWRITE_ARG);
                    if (outputCompressedCheck.isSelected()) args.add("-"+EncryptParsedCommand.COMPRESSED_ARG); 

                    args.add("-"+EncryptParsedCommand.PDFVERSION_ARG);
					args.add(((StringItem)versionCombo.getSelectedItem()).getId());

					args.add (AbstractParsedCommand.COMMAND_ECRYPT);

	                final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
	                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));               
                }catch(Exception ex){    
                	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
                }     
            }
        });
	    runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Encrypt selected files"));
        add(runButton);
       
//END_RUN_BUTTON
//KEY_LISTENER
        runButton.addKeyListener(runEnterKeyListener);
        browseDestButton.addKeyListener(browseEnterKeyListener);
        
        destFolderText.addKeyListener(runEnterKeyListener);

//LAYOUT
        setLayout();
    }

 
    /**
     * @return Returns the Plugin author.
     */
    public String getPluginAuthor() {
        return PLUGIN_AUTHOR;
    }


    /**
     * @return Returns the Plugin name.
     */
    public String getPluginName() {
    	 return GettextResource.gettext(config.getI18nResourceBundle(),"Encrypt");
    }

	   /**
     * @return Returns the version.
     */
    public String getVersion() {
        return PLUGIN_VERSION;
    }
	
    public Node getJobNode(Node arg0, boolean savePasswords) throws SaveJobException {
		try{
			if (arg0 != null){
				Element filelist = ((Element)arg0).addElement("filelist");
				PdfSelectionTableItem[] items = selectionPanel.getTableRows();        
				for (int i = 0; i < items.length; i++){
					Element fileNode = ((Element)filelist).addElement("file");
					fileNode.addAttribute("name",items[i].getInputFile().getAbsolutePath());
					if(savePasswords){
						fileNode.addAttribute("password",items[i].getPassword());
					}
				}
				
				Element allowall = ((Element)arg0).addElement("allowall");
				if(allowAllCheck.isSelected()){
					allowall.addAttribute("value","true");
				}
				else{
					Element permissions = ((Element)arg0).addElement("permissions");
					for (int i=0; i<=EncryptMainGUI.ANNOTATION; i++){
						if(permissionsCheck[i].isSelected()){
							Element enabled = permissions.addElement("enabled");
							enabled.addAttribute("value", Integer.toString(i));
						}
					}
				}
				
				Element ownerPwd = ((Element)arg0).addElement("ownerpwd");
				ownerPwd.addAttribute("value", ownerPwdField.getText());			

				Element encType = ((Element)arg0).addElement("enctype");
				encType.addAttribute("value", (String)encryptType.getSelectedItem());			
				
				Element userPwd = ((Element)arg0).addElement("usrpwd");
				userPwd.addAttribute("value", userPwdField.getText());			

				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destFolderText.getText());				
				
				Element file_prefix = ((Element)arg0).addElement("prefix");
				file_prefix.addAttribute("value", outPrefixTextField.getText());
				
				Element fileOverwrite = ((Element)arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected()?"true":"false");

				Element fileCompress = ((Element)arg0).addElement("compressed");
				fileCompress.addAttribute("value", outputCompressedCheck.isSelected()?"true":"false");
				
				Element pdfVersion = ((Element)arg0).addElement("pdfversion");
				pdfVersion.addAttribute("value", ((StringItem)versionCombo.getSelectedItem()).getId());
			}
			return arg0;
		}
		catch (Exception ex){
            throw new SaveJobException(ex.getMessage(), ex);                     
        }
	}

    public void loadJobNode(Node arg0) throws LoadJobException {		
			try{	
				selectionPanel.getClearButton().doClick();
				List fileList = arg0.selectNodes("filelist/file");
				for (int i = 0; fileList != null && i < fileList.size(); i++) {
					Node fileNode = (Node) fileList.get(i);
					if(fileNode != null){
						Node fileName = (Node) fileNode.selectSingleNode("@name");
						if (fileName != null && fileName.getText().length()>0){
							Node filePwd = (Node) fileNode.selectSingleNode("@password");	
							selectionPanel.getLoader().addFile(new File(fileName.getText()), (filePwd!=null)?filePwd.getText():null);							
						}
					}
                }

				Node allowAll = (Node) arg0.selectSingleNode("allowall/@value");
				if(allowAll != null && allowAll.getText().equals("true")){
					allowAllCheck.doClick();
				}else{
					Node permissions = (Node) arg0.selectSingleNode("permissions");
					if (permissions != null) {
						List listEnab = permissions.selectNodes("enabled");
						for(int j=0; listEnab!= null && j<listEnab.size(); j++){
							Node enabledNode = (Node) listEnab.get(j);
							if(enabledNode != null){
								permissionsCheck[Integer.parseInt(enabledNode.selectSingleNode("@value").getText())].doClick();
							}
						}
					}
				}				
				Node encType = (Node) arg0.selectSingleNode("enctype/@value");
				if (encType != null){
					encryptType.setSelectedItem((String)encType.getText());
				}

				Node userPwd = (Node) arg0.selectSingleNode("usrpwd/@value");
				if (userPwd != null){
					userPwdField.setText(userPwd.getText());
				}

				Node ownerPwd = (Node) arg0.selectSingleNode("ownerpwd/@value");
				if (ownerPwd != null){
					ownerPwdField.setText(ownerPwd.getText());
				}
				
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null){
					destFolderText.setText(fileDestination.getText());
				}
								
				Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
				if (fileOverwrite != null){
					overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
				}

				Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
				if (fileCompressed != null){
					outputCompressedCheck.setSelected(fileCompressed.getText().equals("true"));
				}
				
				Node filePrefix = (Node) arg0.selectSingleNode("prefix/@value");
				if (filePrefix != null){
					outPrefixTextField.setText(filePrefix.getText());
				}
				Node pdfVersion = (Node) arg0.selectSingleNode("pdfversion/@value");
				if (pdfVersion != null){
					for (int i = 0; i<versionCombo.getItemCount(); i++){
						if(((StringItem)versionCombo.getItemAt(i)).getId().equals(pdfVersion.getText())){
							versionCombo.setSelectedIndex(i);
							break;
						}
					}
				}
				log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Encrypt section loaded."));  
            }
			catch (Exception ex){
				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                             
			}	 				
	}
	  
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
//      LAYOUT
    	encryptSpringLayout.putConstraint(SpringLayout.SOUTH, selectionPanel, 220, SpringLayout.NORTH, this);
    	encryptSpringLayout.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
    	encryptSpringLayout.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
		encryptSpringLayout.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);
		
		encryptSpringLayout.putConstraint(SpringLayout.SOUTH, encryptOptionsPanel, 150, SpringLayout.NORTH, encryptOptionsPanel);
		encryptSpringLayout.putConstraint(SpringLayout.EAST, encryptOptionsPanel, -5, SpringLayout.EAST, this);
	    encryptSpringLayout.putConstraint(SpringLayout.NORTH, encryptOptionsPanel, 20, SpringLayout.SOUTH, selectionPanel);
	    encryptSpringLayout.putConstraint(SpringLayout.WEST, encryptOptionsPanel, 0, SpringLayout.WEST, selectionPanel);

	    encryptSpringLayout.putConstraint(SpringLayout.SOUTH, encryptOptionsLabel, 0, SpringLayout.NORTH, encryptOptionsPanel);
	    encryptSpringLayout.putConstraint(SpringLayout.WEST, encryptOptionsLabel, 0, SpringLayout.WEST, encryptOptionsPanel);
        
        encryptSpringLayout.putConstraint(SpringLayout.NORTH, destFolderLabel, 5, SpringLayout.SOUTH, encryptOptionsPanel);
        encryptSpringLayout.putConstraint(SpringLayout.WEST, destFolderLabel, 0, SpringLayout.WEST, encryptOptionsPanel);

        encryptSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 120, SpringLayout.NORTH, destinationPanel);
        encryptSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, encryptOptionsPanel);
        encryptSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, encryptOptionsPanel);
        encryptSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, selectionPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destFolderText, 30, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destFolderText, 10, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destFolderText, -105, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destFolderText, 5, SpringLayout.WEST, destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destFolderText);   
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destFolderText);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destFolderText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);

        encryptSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsLabel, 0, SpringLayout.EAST, destinationPanel);
        encryptSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsLabel, 0, SpringLayout.WEST, destinationPanel);
        encryptSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsLabel, 5, SpringLayout.SOUTH, destinationPanel);
        
        encryptSpringLayout.putConstraint(SpringLayout.SOUTH, outputOptionsPanel, 48, SpringLayout.NORTH, outputOptionsPanel);
        encryptSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsPanel, 0, SpringLayout.EAST, destinationPanel);
        encryptSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsPanel, 0, SpringLayout.SOUTH, outputOptionsLabel);
        encryptSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsPanel, 0, SpringLayout.WEST, outputOptionsLabel);
        
        encryptPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 25, SpringLayout.NORTH, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.EAST, outPrefixTextField, -10, SpringLayout.EAST, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixTextField, 0, SpringLayout.SOUTH, outPrefixLabel);
        encryptPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixTextField, 0, SpringLayout.NORTH, outPrefixLabel);
        encryptPanelLayout.putConstraint(SpringLayout.WEST, outPrefixTextField, 15, SpringLayout.EAST, outPrefixLabel);
        
        encryptPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
        
        encryptSpringLayout.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        encryptSpringLayout.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        encryptSpringLayout.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        encryptSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, outputOptionsPanel);

//      RADIO_LAYOUT
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, ownerPwdLabel, 20, SpringLayout.NORTH, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, ownerPwdLabel, 140, SpringLayout.WEST, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, ownerPwdLabel, 10, SpringLayout.NORTH, encryptOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, ownerPwdLabel, 10, SpringLayout.WEST, encryptOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, ownerPwdField, 0, SpringLayout.SOUTH, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, ownerPwdField, 140, SpringLayout.WEST, ownerPwdField);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, ownerPwdField, 0, SpringLayout.NORTH, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, ownerPwdField, 5, SpringLayout.EAST, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, userPwdLabel, 0, SpringLayout.SOUTH, ownerPwdField);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, userPwdLabel, 100, SpringLayout.WEST, userPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, userPwdLabel, 0, SpringLayout.NORTH, ownerPwdField);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, userPwdLabel, 5, SpringLayout.EAST, ownerPwdField);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, userPwdField, 0, SpringLayout.SOUTH, userPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, userPwdField, 140, SpringLayout.WEST, userPwdField);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, userPwdField, 0, SpringLayout.NORTH, userPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, userPwdField, 5, SpringLayout.EAST, userPwdLabel);

        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, encryptTypeLabel, 20, SpringLayout.NORTH, encryptTypeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, encryptTypeLabel, 0, SpringLayout.EAST, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, encryptTypeLabel, 5, SpringLayout.SOUTH, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, encryptTypeLabel, 0, SpringLayout.WEST, ownerPwdLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, encryptType, 0, SpringLayout.SOUTH, encryptTypeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, encryptType, 100, SpringLayout.WEST, encryptType);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, encryptType, 0, SpringLayout.NORTH, encryptTypeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, encryptType, 5, SpringLayout.EAST, encryptTypeLabel);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, allowAllCheck, 20, SpringLayout.NORTH, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, allowAllCheck, 170, SpringLayout.WEST, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, allowAllCheck, 5, SpringLayout.SOUTH, encryptTypeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, allowAllCheck, 0, SpringLayout.WEST, encryptTypeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[3], 0, SpringLayout.SOUTH, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[3], 230, SpringLayout.WEST, permissionsCheck[3]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[3], 0, SpringLayout.NORTH, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[3], 5, SpringLayout.EAST, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[7], 0, SpringLayout.SOUTH, permissionsCheck[3]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[7], 230, SpringLayout.WEST, permissionsCheck[7]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[7], 0, SpringLayout.NORTH, permissionsCheck[3]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[7], 0, SpringLayout.EAST, permissionsCheck[3]);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[0], 20, SpringLayout.NORTH, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[0], 0, SpringLayout.EAST, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[0], 0, SpringLayout.SOUTH, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[0], 0, SpringLayout.WEST, allowAllCheck);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[1], 0, SpringLayout.SOUTH, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[1], 0, SpringLayout.EAST, permissionsCheck[3]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[1], 0, SpringLayout.NORTH, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[1], 5, SpringLayout.EAST, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[2], 0, SpringLayout.SOUTH, permissionsCheck[1]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[2], 0, SpringLayout.EAST, permissionsCheck[7]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[2], 0, SpringLayout.NORTH, permissionsCheck[1]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[2], 0, SpringLayout.EAST, permissionsCheck[1]);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[4], 20, SpringLayout.NORTH, permissionsCheck[4]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[4], 0, SpringLayout.EAST, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[4], 0, SpringLayout.SOUTH, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[4], 0, SpringLayout.WEST, permissionsCheck[0]);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[5], 0, SpringLayout.SOUTH, permissionsCheck[4]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[5], 0, SpringLayout.EAST, permissionsCheck[1]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[5], 0, SpringLayout.NORTH, permissionsCheck[4]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[5], 0, SpringLayout.WEST, permissionsCheck[1]);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, permissionsCheck[6], 0, SpringLayout.SOUTH, permissionsCheck[4]);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, permissionsCheck[6], 0, SpringLayout.EAST, permissionsCheck[2]);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, permissionsCheck[6], 0, SpringLayout.NORTH, permissionsCheck[4]);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, permissionsCheck[6], 0, SpringLayout.WEST, permissionsCheck[2]);

    }
    
	/**
	*@return Console parameter for the selected encryption algorithm from the JComboBox
	*/
	private String getEncAlg(String comboEnc){
		String retval = EncryptParsedCommand.E_RC4_40;
		if(comboEnc != null){
			if(comboEnc.equals(EncryptMainGUI.RC4_40)){
				retval = EncryptParsedCommand.E_RC4_40;
			}else if(comboEnc.equals(EncryptMainGUI.RC4_128)){
				retval = EncryptParsedCommand.E_RC4_128;
			}else if(comboEnc.equals(EncryptMainGUI.AES_128)){
				retval = EncryptParsedCommand.E_AES_128;
			}
		}
		return retval;			
	}	
	
	/**
	*@return <code>LinkedList</code> containing permissions parameters
	*/
	private LinkedList getEncPermissions(JCheckBox[] pChecks, JCheckBox allowAll){
		LinkedList ret = new LinkedList();
		if(allowAll.isSelected()){
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_PRINT);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_MODIFY);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_COPY);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_ANNOTATION);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_SCREEN);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_FILL);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_ASSEMBLY);
			ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
			ret.add(EncryptParsedCommand.E_DPRINT);
		}
		else{
			if(	pChecks[EncryptMainGUI.PRINT].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_PRINT);
			}
			if(	pChecks[EncryptMainGUI.DPRINT].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_DPRINT);
			}
			if(	pChecks[EncryptMainGUI.COPY].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_COPY);
			}
			if(	pChecks[EncryptMainGUI.MODIFY].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_MODIFY);
			}
			if(	pChecks[EncryptMainGUI.FILL].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_FILL);
			}
			if(	pChecks[EncryptMainGUI.SCREEN].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_SCREEN);
			}
			if(	pChecks[EncryptMainGUI.ASSEMBLY].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_ASSEMBLY);
			}
			if(	pChecks[EncryptMainGUI.ANNOTATION].isSelected()){
				ret.add("-"+EncryptParsedCommand.ALLOW_ARG);
				ret.add(EncryptParsedCommand.E_ANNOTATION);
			}
		}
		return ret;
	}
    
    public FocusTraversalPolicy getFocusPolicy(){
        return encryptfocusPolicy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for encrypt panel
     *
     */
    public class EncryptFocusPolicy extends FocusTraversalPolicy {
        public EncryptFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
        	if (aComponent.equals(selectionPanel.getAddFileButton())){
                return selectionPanel.getRemoveFileButton();
            }
            else if (aComponent.equals(selectionPanel.getRemoveFileButton())){
                return selectionPanel.getMoveUpButton();
            }
            else if (aComponent.equals(selectionPanel.getMoveUpButton())){
                return selectionPanel.getMoveDownButton();
            }
            else if (aComponent.equals(selectionPanel.getMoveDownButton())){
                return selectionPanel.getClearButton();
            }  
            else if (aComponent.equals(selectionPanel.getClearButton())){
                return ownerPwdField;
            }
            else if (aComponent.equals(ownerPwdField)){
                return userPwdField;
            }
            else if (aComponent.equals(userPwdField)){
                return encryptType;
            }
            else if (aComponent.equals(encryptType)){
                return allowAllCheck;
            }
            else if (aComponent.equals(allowAllCheck)){
                if (allowAllCheck.isSelected()){
                    return destFolderText;
                }else{
                    return permissionsCheck[EncryptMainGUI.MODIFY];
                }
            }        
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.MODIFY])){
                return permissionsCheck[EncryptMainGUI.ANNOTATION];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ANNOTATION])){
                return permissionsCheck[EncryptMainGUI.DPRINT];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.DPRINT])){
                return permissionsCheck[EncryptMainGUI.PRINT];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.PRINT])){
                return permissionsCheck[EncryptMainGUI.COPY];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.COPY])){
                return permissionsCheck[EncryptMainGUI.FILL];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.FILL])){
                return permissionsCheck[EncryptMainGUI.SCREEN];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.SCREEN])){
                return permissionsCheck[EncryptMainGUI.ASSEMBLY];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ASSEMBLY])){
                return destFolderText;
            }            
            else if (aComponent.equals(destFolderText)){
                return browseDestButton;
            }
            else if (aComponent.equals(browseDestButton)){
                return overwriteCheckbox;
            }   
			else if (aComponent.equals(overwriteCheckbox)){
				return outputCompressedCheck;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return versionCombo;
			}            
			else if (aComponent.equals(versionCombo)){
				return runButton;
			}            
            else if (aComponent.equals(runButton)){
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
        	if (aComponent.equals(runButton)){
				return outPrefixTextField;
			}
			else if (aComponent.equals(outPrefixTextField)){
				return versionCombo;
			}
			else if (aComponent.equals(versionCombo)){
				return overwriteCheckbox;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return overwriteCheckbox;
			}           
            else if (aComponent.equals(overwriteCheckbox)){            	
                return browseDestButton;              
            }
            else if (aComponent.equals(browseDestButton)){
                return destFolderText;
            } 
            else if (aComponent.equals(browseDestButton)){
                if (allowAllCheck.isSelected()){
                    return allowAllCheck;
                }else{
                    return permissionsCheck[EncryptMainGUI.ASSEMBLY];
                }
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ASSEMBLY])){
                return permissionsCheck[EncryptMainGUI.SCREEN];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.SCREEN])){
                return permissionsCheck[EncryptMainGUI.FILL];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.FILL])){
                return permissionsCheck[EncryptMainGUI.COPY];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.COPY])){
                return permissionsCheck[EncryptMainGUI.PRINT];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.PRINT])){
                return permissionsCheck[EncryptMainGUI.DPRINT];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.DPRINT])){
                return permissionsCheck[EncryptMainGUI.ANNOTATION];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ANNOTATION])){
                return permissionsCheck[EncryptMainGUI.MODIFY];
            }
            else if (aComponent.equals(permissionsCheck[EncryptMainGUI.MODIFY])){
                return allowAllCheck;
            }
            else if (aComponent.equals(allowAllCheck)){
                return encryptType;
            }
            else if (aComponent.equals(encryptType)){
                return userPwdField;
            }
            else if (aComponent.equals(userPwdField)){
                return ownerPwdField;
            }
            else if (aComponent.equals(ownerPwdField)){
                return selectionPanel.getClearButton();
            }
            else if (aComponent.equals(selectionPanel.getClearButton())){
                return selectionPanel.getMoveDownButton();
            }
            else if (aComponent.equals(selectionPanel.getMoveDownButton())){
                return selectionPanel.getMoveUpButton();
            }
            else if (aComponent.equals(selectionPanel.getMoveUpButton())){
                return selectionPanel.getRemoveFileButton();
            }        
            else if (aComponent.equals(selectionPanel.getRemoveFileButton())){
                return selectionPanel.getAddFileButton();
            }
            else if (aComponent.equals(selectionPanel.getAddFileButton())){
                return runButton;
            }
            return selectionPanel.getAddFileButton();
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return selectionPanel.getAddFileButton();
        }

        public Component getLastComponent(Container CycleRootComp){
            return runButton;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return selectionPanel.getAddFileButton();
        }
    }   
    
    /**
	 * The menu item to set the output path has been clicked
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(JPdfSelectionPanel.OUTPUT_PATH_PROPERTY.equals(evt.getPropertyName())){
			destFolderText.setText(((String)evt.getNewValue()));
		}		
	}
	
	public void resetPanel() {
		((AbstractPdfSelectionTableModel)selectionPanel.getMainTable().getModel()).clearData();	
		destFolderText.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(true);
		overwriteCheckbox.setSelected(false);
		destFolderText.setText("");
		userPwdField.setText("");
		outPrefixTextField.setText("");
		encryptType.setSelectedItem(EncryptMainGUI.RC4_40);
		allowAllCheck.setSelected(false);
		for(int i = 0; i<permissionsCheck.length; i++){
			permissionsCheck[i].setSelected(false);
		}
	}
}
