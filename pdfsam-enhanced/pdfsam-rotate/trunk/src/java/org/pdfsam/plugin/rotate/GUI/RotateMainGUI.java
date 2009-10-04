/*
 * Created on 27-Jul-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.plugin.rotate.GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.RotateParsedCommand;
import org.pdfsam.console.utils.ValidationUtility;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
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
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
/** 
 * Plugable JPanel provides a GUI for rotate functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class RotateMainGUI extends AbstractPlugablePanel implements PropertyChangeListener {


	private static final long serialVersionUID = -3486940947325560929L;

	private static final Logger log = Logger.getLogger(RotateMainGUI.class.getPackage().getName());	
	
	private SpringLayout destinationPanelLayout;
	private SpringLayout outputOptionsPanelLayout;
	private SpringLayout rotationPanelLayout;
	private final JPanel destinationPanel = new JPanel();
	private final JPanel topPanel = new JPanel();
	private final JPanel outputOptionsPanel = new JPanel();
	private final JPanel rotationPanel = new JPanel();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);
	private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
	private JTextField destinationTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
	private JTextField outPrefixTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.PREFIX_TEXT_FIELD_TYPE);
	private JHelpLabel destinationHelpLabel;
	private JHelpLabel prefixHelpLabel;
	private JHelpLabel rotateHelpLabel;
	private JComboBox rotationBox;
	private JComboBox rotationPagesBox;
	private Configuration config;
	private JFileChooser browseDirChooser;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
    private final JLabel outPrefixLabel = new JLabel();
    private final JLabel rotateComboLabel = new JLabel();
    private final JLabel rotatePagesComboLabel = new JLabel();

	private final RotateFocusPolicy rotateFocusPolicy = new RotateFocusPolicy();
	//buttons
	private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
	private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);

	private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
	private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_VERSION = "0.0.2";
	
	/**
	 * Constructor
	 */
	public RotateMainGUI() {
		super();          
		initialize();
	}
	
	private void initialize() {
		config = Configuration.getInstance();
		setPanelIcon("/images/rotate.png");
        setPreferredSize(new Dimension(500,450));
        
        setLayout(new GridBagLayout());
        
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topConst = new GridBagConstraints();
        topConst.fill = GridBagConstraints.BOTH;
        topConst.ipady = 5;
        topConst.weightx = 1.0;
        topConst.weighty = 1.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 0;
		topPanel.add(selectionPanel, topConst);
		
		selectionPanel.addPropertyChangeListener(this);
		selectionPanel.enableSetOutputPathMenuItem();

//		ROTATION PANEL	
		rotationPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Rotation")));
		rotationPanel.setPreferredSize(new Dimension(200, 95));
		rotationPanel.setMinimumSize(new Dimension(160, 90));
		rotationPanelLayout = new SpringLayout();
		rotationPanel.setLayout(rotationPanelLayout);
		
		rotationBox = new JComboBox();
		rotationBox.addItem("90");
		rotationBox.addItem("180");
		rotationBox.addItem("270");
		rotationPagesBox = new JComboBox();
		rotationPagesBox.addItem(new StringItem(ValidationUtility.ALL_STRING, GettextResource.gettext(config.getI18nResourceBundle(),"All")));
		rotationPagesBox.addItem(new StringItem(ValidationUtility.EVEN_STRING, GettextResource.gettext(config.getI18nResourceBundle(),"Even")));
		rotationPagesBox.addItem(new StringItem(ValidationUtility.ODD_STRING, GettextResource.gettext(config.getI18nResourceBundle(),"Odd")));
		
		rotateComboLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Clockwise rotation (degrees):"));
		rotatePagesComboLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Pages:"));
		
		StringBuffer sb1 = new StringBuffer();
        sb1.append("<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Rotation")+"</b>");
        sb1.append("<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set the rotation degrees (clockwise).")+"</p>");
        sb1.append("<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pages that will be rotated.")+"</p>");
        sb1.append("</body></html>");        
	    rotateHelpLabel = new JHelpLabel(sb1.toString(), true);
	    
	    rotationPanel.add(rotateHelpLabel);
		rotationPanel.add(rotateComboLabel);
		rotationPanel.add(rotationBox);
		rotationPanel.add(rotatePagesComboLabel);
		rotationPanel.add(rotationPagesBox);
		rotationPanel.add(rotateHelpLabel);

		topConst.fill = GridBagConstraints.HORIZONTAL;
        topConst.weightx = 0.0;
        topConst.weighty = 0.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 1;
        topPanel.add(rotationPanel, topConst);
        
//		DESTINATION_PANEL
		destinationPanelLayout = new SpringLayout();
		destinationPanel.setLayout(destinationPanelLayout);		 
		TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder"));
		destinationPanel.setBorder(titledBorder);
		destinationPanel.setPreferredSize(new Dimension(200, 160));
		destinationPanel.setMinimumSize(new Dimension(160, 150));

//		END_DESTINATION_PANEL   
      
		destinationPanel.add(destinationTextField);
		topConst.fill = GridBagConstraints.HORIZONTAL;
        topConst.weightx = 0.0;
        topConst.weighty = 0.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 2;
        topPanel.add(destinationPanel, topConst);
        
		
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
		add(topPanel, c);
//		BROWSE_BUTTON        
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	if(browseDirChooser==null){
            		browseDirChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
    		        browseDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	}
                File chosenFile = null; 
                if(destinationTextField.getText().length()>0){
                	browseDirChooser.setCurrentDirectory(new File(destinationTextField.getText()));
                }
				if (browseDirChooser.showOpenDialog(browseButton.getParent()) == JFileChooser.APPROVE_OPTION){
					chosenFile = browseDirChooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosenFile != null){
					try{
						destinationTextField.setText(chosenFile.getAbsolutePath());
					}
					catch (Exception ex){
						log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
					}
				}

			}
		});        
		destinationPanel.add(browseButton);
//		END_BROWSE_BUTTON		

		overwriteCheckbox.setSelected(true);
		destinationPanel.add(overwriteCheckbox);

		outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
		outputCompressedCheck.setSelected(true);
		
		destinationPanel.add(outputCompressedCheck);
		destinationPanel.add(versionCombo);
		destinationPanel.add(outputVersionLabel);
		
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION 	
	    outputOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Output options")));
        outputOptionsPanel.setPreferredSize(new Dimension(200, 55));
        outputOptionsPanel.setMinimumSize(new Dimension(160, 50));
        outputOptionsPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(outputOptionsPanelLayout);
        
        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);
       
        outPrefixTextField.setPreferredSize(new Dimension(180,20));
        outputOptionsPanel.add(outPrefixTextField);
//END_S_PANEL
//      HELP_LABEL_PREFIX       
        StringBuffer sb = new StringBuffer();
        sb.append("<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Output files prefix")+"</b>");
        sb.append("<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it contains \"[TIMESTAMP]\" it performs variable substitution.")+"</p>");
        sb.append("<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf.")+"</p>");
        sb.append("<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it doesn't contain \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>");
        sb.append("<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Available variables: [TIMESTAMP], [BASENAME].")+"</p>");
        sb.append("</body></html>");
	    prefixHelpLabel = new JHelpLabel(sb.toString(), true);
	    outputOptionsPanel.add(prefixHelpLabel);
//END_HELP_LABEL_PREFIX   
		
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.ipady = 5;
	    c.weightx = 1.0;
	    c.weighty = 0.0;
	    c.gridwidth = 3;
	    c.gridx = 0;
	    c.gridy = 1;		
		add(outputOptionsPanel, c);
		
//		RUN_BUTTON
		runButton.addActionListener(new ActionListener() {            
			public void actionPerformed(ActionEvent e) {
				if (WorkExecutor.getInstance().getRunningThreads() > 0 || selectionPanel.isAdding()){
                    log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Please wait while all files are processed.."));
                    return;
                }      
				final LinkedList args = new LinkedList();
                //validation and permission check are demanded to the CmdParser object
                try{					

					PdfSelectionTableItem item = null;
                	PdfSelectionTableItem[] items = selectionPanel.getTableRows();
                	if(items != null && items.length >= 1){	
	                	for (int i = 0; i < items.length; i++){
							item = items[i];
							args.add("-"+RotateParsedCommand.F_ARG);
	                        String f = item.getInputFile().getAbsolutePath();
	                        if((item.getPassword()) != null && (item.getPassword()).length()>0){
	    						log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
	    						f +=":"+item.getPassword();
	    					}
	    					args.add(f);                        						
						}
	                	
	                    args.add("-"+RotateParsedCommand.O_ARG);
	                    if(destinationTextField.getText()==null || destinationTextField.getText().length()==0){                    
	                		String suggestedDir = Configuration.getInstance().getDefaultWorkingDirectory();                    		
	                		if(suggestedDir != null){
	                			int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(getParent(),suggestedDir);
	                			if(JOptionPane.YES_OPTION == chosenOpt){
	                				destinationTextField.setText(suggestedDir);
			        			}else if(JOptionPane.CANCEL_OPTION == chosenOpt){
			        				return;
			        			}
	                		}                    	
	                    }
	                    args.add(destinationTextField.getText());
	
	                    if (overwriteCheckbox.isSelected()) {
							args.add("-"+RotateParsedCommand.OVERWRITE_ARG);
						}
	                    if (outputCompressedCheck.isSelected()) {
	                    	args.add("-"+RotateParsedCommand.COMPRESSED_ARG); 
	                    }
	                    
	                    args.add("-"+RotateParsedCommand.R_ARG);
	                    args.add(((StringItem)rotationPagesBox.getSelectedItem()).getId()+":"+rotationBox.getSelectedItem());
	                    
	                    args.add("-"+RotateParsedCommand.P_ARG);
	                    args.add(outPrefixTextField.getText());
	                    args.add("-"+RotateParsedCommand.PDFVERSION_ARG);
						args.add(((StringItem)versionCombo.getSelectedItem()).getId());
						
						args.add (AbstractParsedCommand.COMMAND_ROTATE);
	
		                final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
		                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));     
                	}else{
						JOptionPane.showMessageDialog(getParent(),
								GettextResource.gettext(config.getI18nResourceBundle(),"Please select at least one pdf document."),
								GettextResource.gettext(config.getI18nResourceBundle(),"Warning"),
							    JOptionPane.WARNING_MESSAGE);
					}
				}catch(Exception ex){   
					log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
					SoundPlayer.getInstance().playErrorSound();
				} 

			}
		});
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate selected files"));
        runButton.setSize(new Dimension(88,25));
        
        c.fill = GridBagConstraints.NONE;
	    c.ipadx = 5;
	    c.weightx = 0.0;
	    c.weighty = 0.0;
	    c.anchor = GridBagConstraints.LAST_LINE_END;
	    c.gridwidth = 1;
	    c.gridx = 2;
	    c.gridy = 2;
	    c.insets = new Insets(10,10,10,10);  
        add(runButton, c);
//		END_RUN_BUTTON		
		
		destinationTextField.addKeyListener(runEnterkeyListener);
		runButton.addKeyListener(runEnterkeyListener);
		browseButton.addKeyListener(browseEnterkeyListener);		
		setLayout();
	}
	
	/**
	 * Set plugin layout for each component
	 *
	 */
	private void setLayout(){
		destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationTextField, 10, SpringLayout.NORTH, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationTextField, 30, SpringLayout.NORTH, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationTextField, 5, SpringLayout.WEST, destinationPanel);

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destinationTextField);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destinationTextField);
		
		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destinationTextField);

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destinationTextField);
        
		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
		destinationPanelLayout.putConstraint(SpringLayout.EAST, browseButton, -10, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH, destinationTextField);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, browseButton, -88, SpringLayout.EAST, browseButton);        

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);   
        
        outputOptionsPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 20, SpringLayout.NORTH, outputOptionsPanel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixTextField, 0, SpringLayout.SOUTH, outPrefixLabel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.WEST, outPrefixTextField, 10, SpringLayout.EAST, outPrefixLabel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.EAST, outPrefixTextField, -30, SpringLayout.EAST, outputOptionsPanel);
        
        outputOptionsPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        outputOptionsPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
        
        rotationPanelLayout.putConstraint(SpringLayout.SOUTH, rotateComboLabel, 20, SpringLayout.NORTH, rotationPanel);
        rotationPanelLayout.putConstraint(SpringLayout.NORTH, rotateComboLabel, 5, SpringLayout.NORTH, rotationPanel);
        rotationPanelLayout.putConstraint(SpringLayout.WEST, rotateComboLabel, 5, SpringLayout.WEST, rotationPanel);
        rotationPanelLayout.putConstraint(SpringLayout.SOUTH, rotationBox, 0, SpringLayout.SOUTH, rotateComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.WEST, rotationBox, 10, SpringLayout.EAST, rotateComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.SOUTH, rotatePagesComboLabel, 15, SpringLayout.NORTH, rotatePagesComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.NORTH, rotatePagesComboLabel, 10, SpringLayout.SOUTH, rotateComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.WEST, rotatePagesComboLabel, 0, SpringLayout.WEST, rotateComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.SOUTH, rotationPagesBox, 0, SpringLayout.SOUTH, rotatePagesComboLabel);
        rotationPanelLayout.putConstraint(SpringLayout.WEST, rotationPagesBox, 10, SpringLayout.EAST, rotatePagesComboLabel);
        
        rotationPanelLayout.putConstraint(SpringLayout.SOUTH, rotateHelpLabel, -1, SpringLayout.SOUTH, rotationPanel);
        rotationPanelLayout.putConstraint(SpringLayout.EAST, rotateHelpLabel, -1, SpringLayout.EAST, rotationPanel);

	}

	/**
	 * @return the Plugin author
	 */
	public String getPluginAuthor(){
		return PLUGIN_AUTHOR;
	}

	/**
	 * @return the Plugin name
	 */    
	public String getPluginName(){
		return GettextResource.gettext(config.getI18nResourceBundle(),"Rotate");
	}

	/**
	 * @return the Plugin version
	 */    
	public String getVersion(){
		return PLUGIN_VERSION;
	}
	
	
	/**
	 * @return the FocusTraversalPolicy associated with the plugin
	 */
	public FocusTraversalPolicy getFocusPolicy(){
		return (FocusTraversalPolicy)rotateFocusPolicy;

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

				Element rotationElement = ((Element)arg0).addElement("rotation");
				rotationElement.addAttribute("value", rotationBox.getSelectedItem().toString());
				
				Element rotationPagesElement = ((Element)arg0).addElement("pages");
				rotationPagesElement.addAttribute("value", ((StringItem)rotationPagesBox.getSelectedItem()).getId());
				
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationTextField.getText());				
				
				Element fileOverwrite = ((Element)arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected()?TRUE:FALSE);

				Element fileCompress = ((Element)arg0).addElement("compressed");
				fileCompress.addAttribute("value", outputCompressedCheck.isSelected()?TRUE:FALSE);
				
				Element pdfVersion = ((Element)arg0).addElement("pdfversion");
				pdfVersion.addAttribute("value", ((StringItem)versionCombo.getSelectedItem()).getId());
				
				Element filePrefix = ((Element)arg0).addElement("prefix");
				filePrefix.addAttribute("value", outPrefixTextField.getText());

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
			Node rotationNode = (Node) arg0.selectSingleNode("rotation/@value");
			if (rotationNode != null){
				rotationBox.setSelectedItem((String)rotationNode.getText());
			}
			
			Node rotationPageNode = (Node) arg0.selectSingleNode("pages/@value");
			if (rotationPageNode != null){
				for (int i = 0; i<rotationPagesBox.getItemCount(); i++){
					if(((StringItem)rotationPagesBox.getItemAt(i)).getId().equals(rotationPageNode.getText())){
						rotationPagesBox.setSelectedIndex(i);
						break;
					}
				}
			}
			
			Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
			if (fileDestination != null){
				destinationTextField.setText(fileDestination.getText());
			}
							
			Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
			if (fileOverwrite != null){
				overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
			}

			Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
			if (fileCompressed != null && TRUE.equals(fileCompressed.getText())){
				outputCompressedCheck.doClick();
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
			
			log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate section loaded."));  
        }
		catch (Exception ex){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                             
		}	 				
}
	/**
	 * Focus policy for rotate panel
	 * @author Andrea Vacondio
	 *
	 */
	public class RotateFocusPolicy extends FocusTraversalPolicy {
		public RotateFocusPolicy(){
			super();
		}

		public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
			if (aComponent.equals(selectionPanel.getAddFileButton())){
				return selectionPanel.getRemoveFileButton();
			}
			else if (aComponent.equals(selectionPanel.getRemoveFileButton())){
				return selectionPanel.getClearButton();
			} 
			else if (aComponent.equals(selectionPanel.getClearButton())){
				return rotationBox;
			} 
			else if (aComponent.equals(rotationBox)){
				return rotationPagesBox;
			} 
			else if (aComponent.equals(rotationPagesBox)){
				return destinationTextField;
			}
			else if (aComponent.equals(destinationTextField)){
				return browseButton;
			}
			else if (aComponent.equals(browseButton)){
				return overwriteCheckbox;
			}             
			else if (aComponent.equals(overwriteCheckbox)){
				return outputCompressedCheck;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return versionCombo;
			}            
			else if (aComponent.equals(versionCombo)){
				return outPrefixTextField;
			}            
			else if (aComponent.equals(outPrefixTextField)){
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
				return outputCompressedCheck;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return overwriteCheckbox;
			}
			else if (aComponent.equals(overwriteCheckbox)){
				return browseButton;
			}
			else if (aComponent.equals(browseButton)){
				return destinationTextField;
			}
			else if (aComponent.equals(destinationTextField)){
				return rotationPagesBox;
			}
			else if (aComponent.equals(rotationPagesBox)){
				return rotationBox;
			}
			else if (aComponent.equals(rotationBox)){
				return selectionPanel.getClearButton();
			}
			else if (aComponent.equals(selectionPanel.getClearButton())){
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
			destinationTextField.setText(((String)evt.getNewValue()));
		}		
	}
	
	public void resetPanel() {
		((AbstractPdfSelectionTableModel)selectionPanel.getMainTable().getModel()).clearData();	
		destinationTextField.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(false);
		overwriteCheckbox.setSelected(false);
	}

}
