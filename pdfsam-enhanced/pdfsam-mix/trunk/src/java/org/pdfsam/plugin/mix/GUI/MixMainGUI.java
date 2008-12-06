/*
 * Created on 12-Jan-2007
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
package org.pdfsam.plugin.mix.GUI;

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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
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
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

/** 
 * Plugable JPanel provides a GUI for alternate mix functions.
 * @author Andrea Vacondio
 * @see org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel
 * @see javax.swing.JPanel
 */
public class MixMainGUI extends AbstractPlugablePanel implements PropertyChangeListener{

	private static final long serialVersionUID = -4353488705164373490L;

	private static final Logger log = Logger.getLogger(MixMainGUI.class.getPackage().getName());
	private static final String DEFAULT_OUPUT_NAME = "mixed_document.pdf";
	
	private SpringLayout destinationPanelLayout;
	private JPanel destinationPanel = new JPanel();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.DOUBLE_SELECTABLE_FILE, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER);
	private JPanel topPanel = new JPanel();
	private JPanel mixOptionsPanel = new JPanel();
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
	private final JCheckBox reverseFirstCheckbox = new JCheckBox();
	private final JCheckBox reverseSecondCheckbox = new JCheckBox();
	private JTextField destinationTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
	private JHelpLabel destinationHelpLabel;
	private Configuration config;
	private JFileChooser browseFileChooser;

	private final MixFocusPolicy mixFocusPolicy = new MixFocusPolicy();
	//buttons
	private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
	private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);
	
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	

	private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
	private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_VERSION = "0.1.7e";

	
	/**
	 * Constructor
	 */
	public MixMainGUI() {
		super();          
		initialize();

	}
	
	
	private void initialize() {
		config = Configuration.getInstance();
		setPanelIcon("/images/mix.png");
        setPreferredSize(new Dimension(500,450));
		
        setLayout(new GridBagLayout());
        
   
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topConst = new GridBagConstraints();
        topConst.fill = GridBagConstraints.BOTH;
        topConst.ipady = 5;
        topConst.weightx = 1.0;
        topConst.weighty = 1.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 2;
        topConst.gridx = 0;
        topConst.gridy = 0;
		topPanel.add(selectionPanel, topConst);
		
//CHECK_BOX	
		mixOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Mix options")));
		mixOptionsPanel.setLayout(new BoxLayout(mixOptionsPanel, BoxLayout.LINE_AXIS));
		mixOptionsPanel.add(Box.createRigidArea(new Dimension(5,0)));

		reverseFirstCheckbox.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse first document"));
		reverseFirstCheckbox.setSelected(false);		
		mixOptionsPanel.add(reverseFirstCheckbox);
		mixOptionsPanel.add(Box.createRigidArea(new Dimension(10,0)));

		reverseSecondCheckbox.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse second document"));
		reverseSecondCheckbox.setSelected(true);
		mixOptionsPanel.add(reverseSecondCheckbox);


        topConst.fill = GridBagConstraints.HORIZONTAL;
        topConst.weightx = 0.0;
        topConst.weighty = 0.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 2;
        topPanel.add(mixOptionsPanel, topConst);
		
//END_CHECK_BOX
		
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);
		add(topPanel, c);
		
		selectionPanel.addPropertyChangeListener(this); 

//		DESTINATION_PANEL
		destinationPanelLayout = new SpringLayout();
		destinationPanel.setLayout(destinationPanelLayout);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file"));
		destinationPanel.setBorder(titledBorder);
		destinationPanel.setPreferredSize(new Dimension(200, 130));
		destinationPanel.setMinimumSize(new Dimension(150, 125));
		
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.ipady = 5;
	    c.weightx = 1.0;
	    c.weighty = 0.0;
	    c.gridwidth = 3;
	    c.gridx = 0;
	    c.gridy = 1;	
	    c.insets = new Insets(0, 0, 0, 0);
		add(destinationPanel, c);
//		END_DESTINATION_PANEL   
	       
		destinationPanel.add(destinationTextField);
		
//		BROWSE_BUTTON        
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				if(browseFileChooser==null){
					browseFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
					browseFileChooser.setFileFilter(new PdfFilter());
				}
				File chosenFile = null;   
                if(destinationTextField.getText().length()>0){
                	browseFileChooser.setCurrentDirectory(new File(destinationTextField.getText()));
                }
				if (browseFileChooser.showOpenDialog(browseButton.getParent()) == JFileChooser.APPROVE_OPTION){
					chosenFile = browseFileChooser.getSelectedFile();
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
		
//		CHECK_BOX
		destinationPanel.add(overwriteCheckbox);
		
		outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
		outputCompressedCheck.setSelected(true);
        destinationPanel.add(outputCompressedCheck);
        destinationPanel.add(versionCombo);
        
        destinationPanel.add(outputVersionLabel);
        
//		END_CHECK_BOX 
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output file if it already exists.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files (Pdf version 1.5 or higher).")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION 		
//		RUN_BUTTON
		runButton.addActionListener(new ActionListener() {            
			public void actionPerformed(ActionEvent e) {
				if (WorkExecutor.getInstance().getRunningThreads() > 0 || selectionPanel.isAdding()){
                    log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Please wait while all files are processed.."));
                    return;
                }
				final LinkedList args = new LinkedList(); 
				try{
					PdfSelectionTableItem[] items = selectionPanel.getTableRows();
					if(items != null && items.length == 2){

						args.add("-"+MixParsedCommand.F1_ARG);
						String f1 = items[0].getInputFile().getAbsolutePath();
						if((items[0].getPassword()) != null && (items[0].getPassword()).length()>0){
							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for first file."));
							f1 +=":"+items[0].getPassword();
						}
						args.add(f1);
						
						args.add("-"+MixParsedCommand.F2_ARG);
						String f2 = items[1].getInputFile().getAbsolutePath();
						if((items[1].getPassword()) != null && (items[1].getPassword()).length()>0){
							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for second file."));
							f2 +=":"+items[1].getPassword();
						}
						args.add(f2);

						args.add("-"+MixParsedCommand.O_ARG);
						//if no extension given
	                    if ((destinationTextField.getText().length() > 0) && !(destinationTextField.getText().matches(PDF_EXTENSION_REGEXP))){
							destinationTextField.setText(destinationTextField.getText()+".pdf");
						}
	                    if(destinationTextField.getText().length()>0){
	                    	File destinationDir = new File(destinationTextField.getText());
	                    	File parent = destinationDir.getParentFile();
	                    	if(!(parent!=null && parent.exists())){
	                    		String suggestedDir = null;
	                    		if(Configuration.getInstance().getDefaultWorkingDir()!=null && Configuration.getInstance().getDefaultWorkingDir().length()>0){
	                    			suggestedDir = new File(Configuration.getInstance().getDefaultWorkingDir(), destinationDir.getName()).getAbsolutePath();
	                    		}else{	                    			
                    				PdfSelectionTableItem item = items[1];
                    				if(item!=null && item.getInputFile()!=null){
                    					suggestedDir = new File(item.getInputFile().getParent(), destinationDir.getName()).getAbsolutePath();
                    				}	                    			
	                    		}
	                    		if(suggestedDir != null){
	                    			int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(getParent(),suggestedDir);
	                    			if(JOptionPane.YES_OPTION == chosenOpt){
	                    				destinationTextField.setText(suggestedDir);
				        			}else if(JOptionPane.CANCEL_OPTION == chosenOpt){
				        				return;
				        			}
	                    		}
	                    	}
	                    }
						args.add(destinationTextField.getText());

						if (overwriteCheckbox.isSelected()) args.add("-"+MixParsedCommand.OVERWRITE_ARG);
	                    if (outputCompressedCheck.isSelected()) args.add("-"+MixParsedCommand.COMPRESSED_ARG); 
						if (reverseFirstCheckbox.isSelected()) args.add("-"+MixParsedCommand.REVERSE_FIRST_ARG);
						if (reverseSecondCheckbox.isSelected()) args.add("-"+MixParsedCommand.REVERSE_SECOND_ARG);
						
						args.add("-"+MixParsedCommand.PDFVERSION_ARG);
						args.add(((StringItem)versionCombo.getSelectedItem()).getId());
						
						args.add (AbstractParsedCommand.COMMAND_MIX);
						final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
						WorkExecutor.getInstance().execute(new WorkThread(myStringArray));
					}else{
						JOptionPane.showMessageDialog(getParent(),
								GettextResource.gettext(config.getI18nResourceBundle(),"Please select two pdf documents."),
								GettextResource.gettext(config.getI18nResourceBundle(),"Warning"),
							    JOptionPane.WARNING_MESSAGE);
					}
				}catch(Exception any_ex){   
					log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), any_ex);
				} 

			}
		});
	    runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Execute pdf alternate mix"));
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
		return GettextResource.gettext(config.getI18nResourceBundle(),"Alternate Mix");
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
		return (FocusTraversalPolicy)mixFocusPolicy;

	}

	public Node getJobNode(Node arg0, boolean savePasswords) throws SaveJobException {
		try{
			if (arg0 != null){
				PdfSelectionTableItem[] items = selectionPanel.getTableRows();
				if(items != null && items.length>0){
					Element firstNode = ((Element)arg0).addElement("first");
					firstNode.addAttribute("value", items[0].getInputFile().getAbsolutePath());	
					if(savePasswords){
						firstNode.addAttribute("password",items[0].getPassword());
					}
					
					Element secondNode = ((Element)arg0).addElement("second");
					if(items.length>1){
						secondNode.addAttribute("value", items[1].getInputFile().getAbsolutePath());	
						if(savePasswords){
							secondNode.addAttribute("password",items[1].getPassword());
						}
					}
				}		
		
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationTextField.getText());			

				Element reverseFirst = ((Element)arg0).addElement("reverse_first");
				reverseFirst.addAttribute("value", reverseFirstCheckbox.isSelected()?TRUE:FALSE);

				Element reverseSecond = ((Element)arg0).addElement("reverse_second");
				reverseSecond.addAttribute("value", reverseSecondCheckbox.isSelected()?TRUE:FALSE);
				
				Element fileOverwrite = ((Element)arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected()?TRUE:FALSE);

				Element fileCompress = ((Element)arg0).addElement("compressed");
				fileCompress.addAttribute("value", outputCompressedCheck.isSelected()?TRUE:FALSE);

				Element pdfVersion = ((Element)arg0).addElement("pdfversion");
				pdfVersion.addAttribute("value", ((StringItem)versionCombo.getSelectedItem()).getId());
			}
			return arg0;
		}
		catch (Exception ex){
			throw new SaveJobException(ex);                     
		}
	}

	public void loadJobNode(Node arg0) throws LoadJobException {
		if(arg0 != null){
			try{
				resetPanel();
				Node firstNode = (Node) arg0.selectSingleNode("first/@value");
				if (firstNode != null && firstNode.getText().length()>0){
					Node firstPwd = (Node) arg0.selectSingleNode("first/@password");	
					selectionPanel.getLoader().addFile(new File(firstNode.getText()), (firstPwd!=null)?firstPwd.getText():null);
				}
				Node secondNode = (Node) arg0.selectSingleNode("second/@value");
				if (secondNode != null && secondNode.getText().length()>0){
					Node secondPwd = (Node) arg0.selectSingleNode("second/@password");
					selectionPanel.getLoader().addFile(new File(secondNode.getText()), (secondPwd!=null)?secondPwd.getText():null);
				}
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null){
					destinationTextField.setText(fileDestination.getText());
				}
				Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
				if (fileOverwrite != null){
					overwriteCheckbox.setSelected(TRUE.equals(fileOverwrite.getText()));
				}
				Node reverseFirst = (Node) arg0.selectSingleNode("reverse_first/@value");
				if (reverseFirst != null){
					reverseFirstCheckbox.setSelected(TRUE.equals(reverseFirst.getText()));
				}
				Node reverseSecond = (Node) arg0.selectSingleNode("reverse_second/@value");
				if (reverseSecond != null){
					reverseSecondCheckbox.setSelected(TRUE.equals(reverseSecond.getText()));
				}
				
				Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
				if (fileCompressed != null && TRUE.equals(fileCompressed.getText())){
					outputCompressedCheck.doClick();
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
				log.info(GettextResource.gettext(config.getI18nResourceBundle(),"AlternateMix section loaded."));                     
			}
			catch (Exception ex){
				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                     
			}
		}
	}


	/**
	 * 
	 * @author Andrea Vacondio
	 * Focus policy for alternateMix panel
	 *
	 */
	public class MixFocusPolicy extends FocusTraversalPolicy {
		public MixFocusPolicy(){
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
				return reverseFirstCheckbox;
			}
			else if (aComponent.equals(reverseFirstCheckbox)){
				return reverseSecondCheckbox;
			}
			else if (aComponent.equals(reverseSecondCheckbox)){
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
				return runButton;
			}
			else if (aComponent.equals(runButton)){
				return selectionPanel.getAddFileButton();
			}
			return selectionPanel.getAddFileButton();
		}

		public Component getComponentBefore(Container CycleRootComp, Component aComponent){

			if (aComponent.equals(runButton)){
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
				return reverseSecondCheckbox;
			}
			else if (aComponent.equals(reverseSecondCheckbox)){
				return reverseFirstCheckbox;
			}
			else if (aComponent.equals(reverseFirstCheckbox)){
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
			String newVal = (String)evt.getNewValue();
			if(newVal.endsWith(File.separator)){
				destinationTextField.setText(newVal+DEFAULT_OUPUT_NAME);
			}else{
				destinationTextField.setText(newVal+File.separator+DEFAULT_OUPUT_NAME);
			}
		}
		
	}


	public void resetPanel() {
		((AbstractPdfSelectionTableModel)selectionPanel.getMainTable().getModel()).clearData();
		versionCombo.resetComponent();
		reverseFirstCheckbox.setSelected(false);
		reverseSecondCheckbox.setSelected(true);
		destinationTextField.setText("");
		outputCompressedCheck.setSelected(false);
		overwriteCheckbox.setSelected(false);
	}

}
