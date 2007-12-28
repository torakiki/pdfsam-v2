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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	
	private SpringLayout destinationPanelLayout;
	private JPanel destinationPanel = new JPanel();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.DOUBLE_SELECTABLE_FILE, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER);
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
	private final JCheckBox reverseFirstCheckbox = new JCheckBox();
	private final JCheckBox reverseSecondCheckbox = new JCheckBox();
	private JTextField destinationTextField;
	private JHelpLabel destinationHelpLabel;
	private SpringLayout springLayoutMixPanel;
	private Configuration config;
	private JFileChooser browseFileChooser;

	private final MixFocusPolicy mixFocusPolicy = new MixFocusPolicy();
	//buttons
	private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
	private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);
	
	private final JLabel destinationLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	

	private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
	private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_VERSION = "0.1.0e";

	
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
		//set focus  policy
		springLayoutMixPanel = new SpringLayout();
		setLayout(springLayoutMixPanel);
		
		add(selectionPanel);
		selectionPanel.addPropertyChangeListener(this);
		
//		BROWSE_FILE_CHOOSER        
		browseFileChooser = new JFileChooser();
		browseFileChooser.setFileFilter(new PdfFilter());
		browseFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);


		destinationLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file:"));
		add(destinationLabel);
		
		
//CHECK_BOX		
		reverseFirstCheckbox.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse first document"));
		reverseFirstCheckbox.setSelected(false);
		add(reverseFirstCheckbox);

		reverseSecondCheckbox.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse second document"));
		reverseSecondCheckbox.setSelected(true);
		add(reverseSecondCheckbox);
		
//END_CHECK_BOX

//		DESTINATION_PANEL
		destinationPanelLayout = new SpringLayout();
		destinationPanel.setLayout(destinationPanelLayout);
		destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
		add(destinationPanel);
//		END_DESTINATION_PANEL   
	
		destinationTextField = new JTextField();
		destinationTextField.setDropTarget(null);
		destinationTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		destinationPanel.add(destinationTextField);
		
//		BROWSE_BUTTON        
		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browseFileChooser.showOpenDialog(browseButton.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browseFileChooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosen_file != null){
					try{
						destinationTextField.setText(chosen_file.getAbsolutePath());
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
						if (destinationTextField.getText().lastIndexOf('.') == -1){
							destinationTextField.setText(destinationTextField.getText()+".pdf");
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
						log.warn(GettextResource.gettext(config.getI18nResourceBundle(),"Please select two pdf documents."));
					}
				}catch(Exception any_ex){   
					log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), any_ex);
				} 

			}
		});
		
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
		springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, selectionPanel, 200, SpringLayout.NORTH, this);
		springLayoutMixPanel.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
		springLayoutMixPanel.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);

		springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, reverseFirstCheckbox, 20, SpringLayout.NORTH, reverseFirstCheckbox);
		springLayoutMixPanel.putConstraint(SpringLayout.EAST, reverseFirstCheckbox, -5, SpringLayout.EAST, this);
		springLayoutMixPanel.putConstraint(SpringLayout.NORTH, reverseFirstCheckbox, 2, SpringLayout.SOUTH, selectionPanel);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, reverseFirstCheckbox, 5, SpringLayout.WEST, this);        

		springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, reverseSecondCheckbox, 20, SpringLayout.NORTH, reverseSecondCheckbox);
		springLayoutMixPanel.putConstraint(SpringLayout.EAST, reverseSecondCheckbox, -5, SpringLayout.EAST, this);
		springLayoutMixPanel.putConstraint(SpringLayout.NORTH, reverseSecondCheckbox, 2, SpringLayout.SOUTH, reverseFirstCheckbox);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, reverseSecondCheckbox, 5, SpringLayout.WEST, this);        

		springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, destinationPanel, 110, SpringLayout.NORTH, destinationPanel);
		springLayoutMixPanel.putConstraint(SpringLayout.EAST, destinationPanel, -7, SpringLayout.EAST, this);
		springLayoutMixPanel.putConstraint(SpringLayout.NORTH, destinationPanel, 35, SpringLayout.SOUTH, reverseSecondCheckbox);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, reverseSecondCheckbox);

		destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationTextField, 5, SpringLayout.NORTH, destinationPanel);
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

        springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, destinationLabel, 0, SpringLayout.NORTH, destinationPanel);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, destinationLabel, 0, SpringLayout.WEST, destinationPanel);

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
		destinationPanelLayout.putConstraint(SpringLayout.EAST, browseButton, -10, SpringLayout.EAST, destinationPanel);
		destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH, destinationTextField);
		destinationPanelLayout.putConstraint(SpringLayout.WEST, browseButton, -88, SpringLayout.EAST, browseButton);        

		destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);                

		springLayoutMixPanel.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
		springLayoutMixPanel.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
		springLayoutMixPanel.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
		springLayoutMixPanel.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, destinationPanel);

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
				reverseFirst.addAttribute("value", reverseFirstCheckbox.isSelected()?"true":"false");

				Element reverseSecond = ((Element)arg0).addElement("reverse_second");
				reverseSecond.addAttribute("value", reverseSecondCheckbox.isSelected()?"true":"false");
				
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
			throw new SaveJobException(ex);                     
		}
	}

	public void loadJobNode(Node arg0) throws LoadJobException {
		if(arg0 != null){
			try{
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
					overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
				}
				Node reverseFirst = (Node) arg0.selectSingleNode("reverse_first/@value");
				if (reverseFirst != null){
					reverseFirstCheckbox.setSelected(reverseFirst.getText().equals("true"));
				}
				Node reverseSecond = (Node) arg0.selectSingleNode("reverse_second/@value");
				if (reverseSecond != null){
					reverseSecondCheckbox.setSelected(reverseSecond.getText().equals("true"));
				}
				
				Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
				if (fileCompressed != null){
					outputCompressedCheck.setSelected(fileCompressed.getText().equals("true"));
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
			destinationTextField.setText(((String)evt.getNewValue())+File.separatorChar+"mixed_document.pdf");
		}
		
	}

}
