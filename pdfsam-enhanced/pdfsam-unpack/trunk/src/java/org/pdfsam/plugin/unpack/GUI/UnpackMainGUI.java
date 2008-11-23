/*
 * Created on 29-Feb-2008
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
package org.pdfsam.plugin.unpack.GUI;

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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.UnpackParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
/** 
 * Plugable JPanel provides a GUI for unpack functions.
 * @author Andrea Vacondio
 * @see org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel
 * @see javax.swing.JPanel
 */
public class UnpackMainGUI extends AbstractPlugablePanel implements PropertyChangeListener{

	private static final long serialVersionUID = -3486940947325560929L;


	private static final Logger log = Logger.getLogger(UnpackMainGUI.class.getPackage().getName());
	
	
	private SpringLayout destinationPanelLayout;
	private JPanel destinationPanel = new JPanel();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);
	private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
	private JTextField destinationTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
	private JHelpLabel destinationHelpLabel;
	private SpringLayout springLayoutUnpackPanel;
	private Configuration config;
	private JFileChooser browseDirChooser;

	private final UnpackFocusPolicy unpackFocusPolicy = new UnpackFocusPolicy();
	//buttons
	private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
	private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);
	
	private final JLabel destinationLabel = new JLabel();

	private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
	private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_VERSION = "0.0.5e";
	
	/**
	 * Constructor
	 */
	public UnpackMainGUI() {
		super();          
		initialize();
	}
	
	private void initialize() {
		config = Configuration.getInstance();
		setPanelIcon("/images/unpack.png");
        setPreferredSize(new Dimension(500,450));
        
		springLayoutUnpackPanel = new SpringLayout();
		setLayout(springLayoutUnpackPanel);
		
		add(selectionPanel);
		selectionPanel.addPropertyChangeListener(this);
		selectionPanel.enableSetOutputPathMenuItem();
		
		destinationLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder:"));
		add(destinationLabel);
		

//		DESTINATION_PANEL
		destinationPanelLayout = new SpringLayout();
		destinationPanel.setLayout(destinationPanelLayout);
		destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
		add(destinationPanel);
//		END_DESTINATION_PANEL   
      
		destinationPanel.add(destinationTextField);
		
//		BROWSE_BUTTON        
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	if(browseDirChooser==null){
            		browseDirChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
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

//		CHECK_BOX
		destinationPanel.add(overwriteCheckbox);
	       
//		END_CHECK_BOX 
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
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
                //validation and permission check are demanded to the CmdParser object
                try{					

					PdfSelectionTableItem item = null;
                	PdfSelectionTableItem[] items = selectionPanel.getTableRows();
                	if(items != null && items.length >= 1){	
	                	for (int i = 0; i < items.length; i++){
							item = items[i];
							args.add("-"+UnpackParsedCommand.F_ARG);
	                        String f = item.getInputFile().getAbsolutePath();
	                        if((item.getPassword()) != null && (item.getPassword()).length()>0){
	    						log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
	    						f +=":"+item.getPassword();
	    					}
	    					args.add(f);                        						
						}
	                	
	                    args.add("-"+UnpackParsedCommand.O_ARG);
	                    if(destinationTextField.getText()==null || destinationTextField.getText().length()==0){                    
	                		String suggestedDir = Configuration.getInstance().getDefaultWorkingDir();                    		
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
							args.add("-"+UnpackParsedCommand.OVERWRITE_ARG);
						}
	
						args.add (AbstractParsedCommand.COMMAND_UNPACK);
	
		                final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
		                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));     
                	}else{
						JOptionPane.showMessageDialog(getParent(),
								GettextResource.gettext(config.getI18nResourceBundle(),"Please select at least one pdf document."),
								GettextResource.gettext(config.getI18nResourceBundle(),"Warning"),
							    JOptionPane.WARNING_MESSAGE);
					}
				}catch(Exception any_ex){   
					log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), any_ex);
				} 

			}
		});
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Unpack selected files"));
		add(runButton);
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
		springLayoutUnpackPanel.putConstraint(SpringLayout.SOUTH, selectionPanel, 200, SpringLayout.NORTH, this);
		springLayoutUnpackPanel.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
		springLayoutUnpackPanel.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
		springLayoutUnpackPanel.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);
      

		springLayoutUnpackPanel.putConstraint(SpringLayout.SOUTH, destinationPanel, 110, SpringLayout.NORTH, destinationPanel);
		springLayoutUnpackPanel.putConstraint(SpringLayout.EAST, destinationPanel, -7, SpringLayout.EAST, this);
		springLayoutUnpackPanel.putConstraint(SpringLayout.NORTH, destinationPanel, 35, SpringLayout.SOUTH, selectionPanel);
		springLayoutUnpackPanel.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, selectionPanel);

		destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationTextField, 10, SpringLayout.NORTH, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationTextField, 30, SpringLayout.NORTH, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationTextField, 5, SpringLayout.WEST, destinationPanel);

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destinationTextField);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destinationTextField);

		springLayoutUnpackPanel.putConstraint(SpringLayout.SOUTH, destinationLabel, 0, SpringLayout.NORTH, destinationPanel);
		springLayoutUnpackPanel.putConstraint(SpringLayout.WEST, destinationLabel, 0, SpringLayout.WEST, destinationPanel);

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
		destinationPanelLayout.putConstraint(SpringLayout.EAST, browseButton, -10, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH, destinationTextField);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, browseButton, -88, SpringLayout.EAST, browseButton);        

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);                

        springLayoutUnpackPanel.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        springLayoutUnpackPanel.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        springLayoutUnpackPanel.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        springLayoutUnpackPanel.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, destinationPanel);

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
		return GettextResource.gettext(config.getI18nResourceBundle(),"Unpack");
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
		return (FocusTraversalPolicy)unpackFocusPolicy;

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

				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationTextField.getText());				
				
				Element fileOverwrite = ((Element)arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected()?"true":"false");

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
			
			Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
			if (fileDestination != null){
				destinationTextField.setText(fileDestination.getText());
			}
							
			Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
			if (fileOverwrite != null){
				overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
			}

			log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Unpack section loaded."));  
        }
		catch (Exception ex){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                             
		}	 				
}
	/**
	 * 
	 * @author Andrea Vacondio
	 * Focus policy for unpack panel
	 *
	 */
	public class UnpackFocusPolicy extends FocusTraversalPolicy {
		public UnpackFocusPolicy(){
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
				return destinationTextField;
			}
			else if (aComponent.equals(destinationTextField)){
				return browseButton;
			}
			else if (aComponent.equals(browseButton)){
				return overwriteCheckbox;
			}            
			else if (aComponent.equals(overwriteCheckbox)){
				return runButton;
			}
			else if (aComponent.equals(runButton)){
				return selectionPanel.getAddFileButton();
			}
			return selectionPanel.getAddFileButton();
		}

		public Component getComponentBefore(Container CycleRootComp, Component aComponent){

			if (aComponent.equals(runButton)){
				return overwriteCheckbox;
			}
			else if (aComponent.equals(overwriteCheckbox)){
				return browseButton;
			}
			else if (aComponent.equals(browseButton)){
				return destinationTextField;
			}
			else if (aComponent.equals(destinationTextField)){
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
		overwriteCheckbox.setSelected(false);
	}
}
