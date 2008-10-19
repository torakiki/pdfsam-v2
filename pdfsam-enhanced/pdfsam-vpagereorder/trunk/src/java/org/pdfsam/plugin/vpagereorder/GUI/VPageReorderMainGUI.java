/*
 * Created on 22-Jul-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.plugin.vpagereorder.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Visual reorder plugin  main panel
 * @author Andrea Vacondio
 *
 */
public class VPageReorderMainGUI extends AbstractPlugablePanel  implements PropertyChangeListener{

	private static final long serialVersionUID = -1965981976755542201L;
	
	private static final String DEFAULT_OUPUT_NAME = "reordered_file.pdf";

	private static final Logger log = Logger.getLogger(VPageReorderMainGUI.class.getPackage().getName());
	
    private JTextField destinationFileText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JHelpLabel destinationHelpLabel;
    private Configuration config;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo(true);
    private JVisualPdfPageSelectionPanel selectionPanel = new JVisualPdfPageSelectionPanel();
    
    //layouts
    private SpringLayout vreorderSpringLayout;
    private SpringLayout destinationPanelLayout;
    
	//button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
  
    //file_chooser    
    private JFileChooser browseDestFileChooser = null;
    
  //checks
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
    
  //key_listeners
    private final EnterDoClickListener browsedEnterkeyListener = new EnterDoClickListener(browseDestButton);
    private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);

    //panels
    private final JPanel destinationPanel = new JPanel();
  
    //labels
    private final JLabel destinationLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
	
  //radio
    private final JRadioButton sameAsSourceRadio = new JRadioButton();
    private final JRadioButton chooseAFolderRadio = new JRadioButton();
    
    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.0.2";
    
    /**
     * Constructor
     */
    public VPageReorderMainGUI() {
        super();          
        initialize();              
    }
    
    /**
     * Panel initialization   
     */
    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/vreorder.png");
        setPreferredSize(new Dimension(500,550));
        
        vreorderSpringLayout = new SpringLayout();
        setLayout(vreorderSpringLayout);
        add(selectionPanel);
        selectionPanel.addPropertyChangeListener(this);

      //DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL        
//DESTINATION_RADIOS
        
        destinationLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file:"));
        add(destinationLabel);

        sameAsSourceRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Same as source"));
        destinationPanel.add(sameAsSourceRadio);

        chooseAFolderRadio.setSelected(true);
        chooseAFolderRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Choose a folder"));
        destinationPanel.add(chooseAFolderRadio);
//END_DESTINATION_RADIOS 

        destinationPanel.add(destinationFileText);
        destinationPanel.add(overwriteCheckbox);
        
        destinationPanel.add(outputCompressedCheck); 
		
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        
        destinationPanel.add(versionCombo);
        
        destinationPanel.add(outputVersionLabel);
        browseDestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(browseDestFileChooser==null){
                    browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());                    
                    browseDestFileChooser.setFileFilter(new PdfFilter());
            	}
                File chosenFile = null;                
                if (browseDestFileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION){
                    chosenFile = browseDestFileChooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosenFile != null){
                    try{
                        destinationFileText.setText(chosenFile.getAbsolutePath());
                    }
                    catch (Exception ex){
                    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                        
                    }
                }
                
            }
        });
        destinationPanel.add(browseDestButton);
//HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Use the same output file name as the input file or choose a file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a file browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files.")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"PDF version 1.5 or above.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION  
	    
        final ButtonGroup outputRadioGroup = new ButtonGroup();
        outputRadioGroup.add(sameAsSourceRadio);
        outputRadioGroup.add(chooseAFolderRadio); 
	  //RADIO_LISTENERS
		sameAsSourceRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destinationFileText.setEnabled(false);
				browseDestButton.setEnabled(false);
			}
		});

		chooseAFolderRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destinationFileText.setEnabled(true);
				browseDestButton.setEnabled(true);
			}
		});
		// END_RADIO_LISTENERS
		// ENTER_KEY_LISTENERS
		browseDestButton.addKeyListener(browsedEnterkeyListener);
		runButton.addKeyListener(runEnterkeyListener);
		// END_ENTER_KEY_LISTENERS
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File inputFile = selectionPanel.getSelectedPdfDocument();
				String selectionString = selectionPanel.getValidElementsString();
				if (inputFile == null || selectionString.length() == 0) {
					  JOptionPane.showMessageDialog(getParent(),
          				  	GettextResource.gettext(config.getI18nResourceBundle(),"Please select a pdf document or undelete some page"),
								GettextResource.gettext(config.getI18nResourceBundle(),"Warning"),
							    JOptionPane.WARNING_MESSAGE);
				}else{
					final LinkedList args = new LinkedList();
					try {
	
						args.add("-" + ConcatParsedCommand.F_ARG);
						String f = inputFile.getAbsolutePath();
						if ((selectionPanel.getSelectedPdfDocumentPassword()) != null
								&& selectionPanel.getSelectedPdfDocumentPassword().length() > 0) {
							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),
									"Found a password for input file."));
							f += ":" + selectionPanel.getSelectedPdfDocumentPassword();
						}
						args.add(f);
	
						args.add("-" + ConcatParsedCommand.U_ARG);
						args.add(selectionString);
	
						args.add("-" + ConcatParsedCommand.O_ARG);
						// check radio for output options
						if (sameAsSourceRadio.isSelected()) {
							if (inputFile != null) {
								args.add(inputFile.getAbsolutePath());
							}
						} else {
							if (destinationFileText.getText().length() > 0) {
								File destinationDir = new File(destinationFileText.getText());
								File parent = destinationDir.getParentFile();
								if (!(parent != null && parent.exists())) {
									String suggestedDir = null;
									if (Configuration.getInstance().getDefaultWorkingDir() != null
											&& Configuration.getInstance().getDefaultWorkingDir().length() > 0) {
										suggestedDir = new File(Configuration.getInstance().getDefaultWorkingDir(),
												destinationDir.getName()).getAbsolutePath();
									} else {
										suggestedDir = new File(inputFile.getParent(), destinationDir.getName())
												.getAbsolutePath();
									}
									if (suggestedDir != null) {
		                    			int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(getParent(),suggestedDir);
		                    			if(JOptionPane.YES_OPTION == chosenOpt){
		                    				destinationFileText.setText(suggestedDir);
					        			}else if(JOptionPane.CANCEL_OPTION == chosenOpt){
					        				return;
					        			}
	
									}
								}
							}
							args.add(destinationFileText.getText());
						}
	
						if (overwriteCheckbox.isSelected())
							args.add("-" + ConcatParsedCommand.OVERWRITE_ARG);
						if (outputCompressedCheck.isSelected())
							args.add("-" + ConcatParsedCommand.COMPRESSED_ARG);
	
						args.add("-" + ConcatParsedCommand.PDFVERSION_ARG);
						args.add(((StringItem) versionCombo.getSelectedItem()).getId());
	
						args.add(AbstractParsedCommand.COMMAND_CONCAT);
	
						final String[] myStringArray = (String[]) args.toArray(new String[args.size()]);
						WorkExecutor.getInstance().execute(new WorkThread(myStringArray));
					} catch (Exception ex) {
						log.error(GettextResource.gettext(config.getI18nResourceBundle(), "Error: "), ex);
					}
				}
			}
		});
		runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(), "Execute pages reorder"));
		add(runButton);
		setLayout();
	}
    
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
    	vreorderSpringLayout.putConstraint(SpringLayout.SOUTH, selectionPanel, 315, SpringLayout.NORTH, this);
    	vreorderSpringLayout.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
    	vreorderSpringLayout.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
    	vreorderSpringLayout.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);

    	vreorderSpringLayout.putConstraint(SpringLayout.NORTH, destinationLabel, 5, SpringLayout.SOUTH, selectionPanel);
    	vreorderSpringLayout.putConstraint(SpringLayout.WEST, destinationLabel, 0, SpringLayout.WEST, selectionPanel);
        
    	vreorderSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 130, SpringLayout.NORTH, destinationPanel);
    	vreorderSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, selectionPanel);
    	vreorderSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, selectionPanel);
    	vreorderSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, selectionPanel);
    	
    	destinationPanelLayout.putConstraint(SpringLayout.SOUTH, sameAsSourceRadio, 25, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, sameAsSourceRadio, 1, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, sameAsSourceRadio, 10, SpringLayout.WEST, destinationPanel);        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, chooseAFolderRadio, 0, SpringLayout.SOUTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, chooseAFolderRadio, 0, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, chooseAFolderRadio, 20, SpringLayout.EAST, sameAsSourceRadio);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationFileText, 50, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationFileText, 30, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationFileText, -105, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationFileText, 5, SpringLayout.WEST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destinationFileText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destinationFileText);
		
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destinationFileText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);
        
        
        vreorderSpringLayout.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        vreorderSpringLayout.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        vreorderSpringLayout.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        vreorderSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, destinationPanel);
    }
    
	public FocusTraversalPolicy getFocusPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getJobNode(Node arg0, boolean savePasswords)
			throws SaveJobException {
		try {
			if (arg0 != null) {
				Element fileSource = ((Element)arg0).addElement("source");
				File item = selectionPanel.getSelectedPdfDocument();
				if(item != null){
					fileSource.addAttribute("value",item.getAbsolutePath());
					if(savePasswords){
						fileSource.addAttribute("password",(selectionPanel.getSelectedPdfDocumentPassword()!=null?selectionPanel.getSelectedPdfDocumentPassword():""));
					}
				}
				
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationFileText.getText());	
				
				Element fileOverwrite = ((Element) arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected() ? TRUE : FALSE);

				Element fileCompress = ((Element) arg0).addElement("compressed");
				fileCompress.addAttribute("value", outputCompressedCheck.isSelected() ? TRUE : FALSE);

				Element pdfVersion = ((Element) arg0).addElement("pdfversion");
				pdfVersion.addAttribute("value", ((StringItem) versionCombo.getSelectedItem()).getId());
			}
			return arg0;
		} catch (Exception ex) {
			throw new SaveJobException(ex);
		}
	}

	public String getPluginAuthor() {
		return PLUGIN_AUTHOR;
	}

	public String getPluginName() {
		return GettextResource.gettext(config.getI18nResourceBundle(),"Visual reorder");
	}

	public String getVersion() {
		return PLUGIN_VERSION;
	}

	public void loadJobNode(Node arg0) throws LoadJobException {
		if(arg0!=null){
			try{
				resetPanel();
				Node fileSource = (Node) arg0.selectSingleNode("source/@value");
				if (fileSource != null && fileSource.getText().length()>0){
					Node filePwd = (Node) arg0.selectSingleNode("source/@password");
					String password = null;
					if (filePwd != null && filePwd.getText().length()>0){
						password = filePwd.getText();
					}
					selectionPanel.getPdfLoader().addFile(new File(fileSource.getText()), password);
				}
				
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null && fileDestination.getText().length()>0){
					destinationFileText.setText(fileDestination.getText());
					chooseAFolderRadio.doClick();
				}else{
					sameAsSourceRadio.doClick();
				}

				Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
				if (fileOverwrite != null){
					overwriteCheckbox.setSelected(TRUE.equals(fileOverwrite.getText()));
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
				
				log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Split section loaded."));  
            }
			catch (Exception ex){
				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                     
			}
		}
	}

	public void resetPanel() {
		selectionPanel.resetPanel();
		destinationFileText.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(false);
		overwriteCheckbox.setSelected(false);
	}

	 /**
	 * The menu item to set the output path has been clicked
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(JPdfSelectionPanel.OUTPUT_PATH_PROPERTY.equals(evt.getPropertyName())){
			destinationFileText.setText(((String)evt.getNewValue())+File.separatorChar+DEFAULT_OUPUT_NAME);
		}		
	}
}
