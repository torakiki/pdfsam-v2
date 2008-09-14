/*
 * Created on 22-Aug-2008
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
package org.pdfsam.plugin.vcomposer.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.panels.JVisualMultiSelectionPanel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

/**
 * Visual document composer plugin  main panel
 * @author Andrea Vacondio
 *
 */
public class VComposerMainGUI extends AbstractPlugablePanel {

	private static final long serialVersionUID = -3265981976255542241L;

	private static final Logger log = Logger.getLogger(VComposerMainGUI.class.getPackage().getName());
	
    private JTextField destinationFileText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JHelpLabel destinationHelpLabel;
    private Configuration config;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo(true);
    private JVisualPdfPageSelectionPanel composerPanel = new JVisualPdfPageSelectionPanel(JVisualPdfPageSelectionPanel.HORIZONTAL_ORIENTATION, false, true, true, true, JVisualPdfPageSelectionPanel.STYLE_TOP_PANEL_MEDIUM, false, true, JVisualPdfPageSelectionPanel.SINGLE_INTERVAL_SELECTION);
    
    //layouts
    private SpringLayout vcomposerSpringLayout;
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
    private final JVisualMultiSelectionPanel inputPanel = new JVisualMultiSelectionPanel();
  
    //labels
    private final JLabel destinationLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
	
    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.0.1";
    
    /**
     * Constructor
     */
    public VComposerMainGUI() {
        super();          
        initialize();              
    }
    
    /**
     * Panel initialization   
     */
    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/vcomposer.png");
        setPreferredSize(new Dimension(500,700));
        
        vcomposerSpringLayout = new SpringLayout();
        setLayout(vcomposerSpringLayout);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		
        JButton moveOnTopButton = new JButton();
		moveOnTopButton.setMargin(new Insets(2, 2, 2, 2));
		moveOnTopButton.setIcon(new ImageIcon(this.getClass().getResource("/images/movetop.png")));
		moveOnTopButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move on top"));
		moveOnTopButton.setHorizontalTextPosition(AbstractButton.LEADING);
		moveOnTopButton.addKeyListener(new EnterDoClickListener(moveOnTopButton));
		moveOnTopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveOnTopButton.setMinimumSize(new Dimension(90, 25));
		moveOnTopButton.setMaximumSize(new Dimension(130, 25));
		moveOnTopButton.setPreferredSize(new Dimension(110, 25));
		moveOnTopButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 VisualPageListItem[] elements = inputPanel.getSelectedElements();
            	 if(elements!=null && elements.length>0){
            		 composerPanel.prependElements(Arrays.asList(elements));
            	 }
             }
		 });
        
		JButton moveToBottomButton = new JButton();
		moveToBottomButton.setMargin(new Insets(2, 2, 2, 2));
		moveToBottomButton.setIcon(new ImageIcon(this.getClass().getResource("/images/movebottom.png")));
		moveToBottomButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move to bottom"));
		moveToBottomButton.addKeyListener(new EnterDoClickListener(moveOnTopButton));
		moveToBottomButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveToBottomButton.setMinimumSize(new Dimension(90, 25));
		moveToBottomButton.setMaximumSize(new Dimension(130, 25));
		moveToBottomButton.setPreferredSize(new Dimension(110, 25));
		moveToBottomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
           	 VisualPageListItem[] elements = inputPanel.getSelectedElements();
           	 if(elements!=null && elements.length>0){
           		 composerPanel.appendElements(Arrays.asList(elements));
           	 }
            }
		 });
		
		buttonsPanel.add(moveOnTopButton);		
		buttonsPanel.add(Box.createRigidArea(new Dimension(10,5)));
		buttonsPanel.add(moveToBottomButton);
		
        composerPanel.addToTopPanel(buttonsPanel);
        add(composerPanel);
        
      //DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL        
        
        destinationLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file:"));
        add(destinationLabel);
                
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
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a file browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files.")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"PDF version 1.5 or above.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION 
	    add(inputPanel);
	 
	  //ENTER_KEY_LISTENERS
	          browseDestButton.addKeyListener(browsedEnterkeyListener);
	          runButton.addKeyListener(runEnterkeyListener);
	  //END_ENTER_KEY_LISTENERS
	          runButton.addActionListener(new ActionListener() {
	              public void actionPerformed(ActionEvent e) {
	            	  String selectionString = composerPanel.getValidElementsString();
	            	  Collection selectedFiles = composerPanel.getValidElementsFiles();
	            	  if(selectionString.length()==0 || selectedFiles == null || selectedFiles.size()<=0){
	                      log.warn(GettextResource.gettext(config.getI18nResourceBundle(),"Please select a pdf document or undelete some page"));
	                      return;
	                  }                             
	                  final LinkedList args = new LinkedList();                
	                  try{		
							args.addAll(selectedFiles);
		
							args.add("-" + ConcatParsedCommand.U_ARG);
							args.add(selectionString);
		
							args.add("-" + ConcatParsedCommand.O_ARG);
		
							if (destinationFileText.getText().length() > 0) {
								File destinationDir = new File(destinationFileText.getText());
								File parent = destinationDir.getParentFile();
								if (!(parent != null && parent.exists())) {
									String suggestedDir = null;
									if (Configuration.getInstance().getDefaultWorkingDir() != null
											&& Configuration.getInstance().getDefaultWorkingDir().length() > 0) {
										suggestedDir = new File(Configuration.getInstance().getDefaultWorkingDir(),
												destinationDir.getName()).getAbsolutePath();
									}
									if (suggestedDir != null) {
										if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getParent(),
												GettextResource.gettext(config.getI18nResourceBundle(),"Output file location is not correct")
														+ ".\n"+GettextResource.gettext(config.getI18nResourceBundle(),"Would you like to change it to") + " " + suggestedDir + " ?",
												GettextResource.gettext(config.getI18nResourceBundle(), "Output location error"),
												JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
											destinationFileText.setText(suggestedDir);
										}
									}
								}
							}
							args.add(destinationFileText.getText());
		
							if (overwriteCheckbox.isSelected())
								args.add("-" + ConcatParsedCommand.OVERWRITE_ARG);
							if (outputCompressedCheck.isSelected())
								args.add("-" + ConcatParsedCommand.COMPRESSED_ARG);
		
							args.add("-" + ConcatParsedCommand.PDFVERSION_ARG);
							args.add(((StringItem) versionCombo.getSelectedItem()).getId());
		
							args.add(AbstractParsedCommand.COMMAND_CONCAT);
	                  
	  	                final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
	  	                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));          
	                  }catch(Exception ex){    
	                  	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
	                  }   
	              }
	          });
	          runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Execute pages reorder"));
	          add(runButton);
	          setLayout();
    }
    
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
    	vcomposerSpringLayout.putConstraint(SpringLayout.SOUTH, inputPanel, 260, SpringLayout.NORTH, this);
    	vcomposerSpringLayout.putConstraint(SpringLayout.EAST, inputPanel, -5, SpringLayout.EAST, this);
    	vcomposerSpringLayout.putConstraint(SpringLayout.NORTH, inputPanel, 5, SpringLayout.NORTH, this);
    	vcomposerSpringLayout.putConstraint(SpringLayout.WEST, inputPanel, 5, SpringLayout.WEST, this);
    	
    	vcomposerSpringLayout.putConstraint(SpringLayout.SOUTH, composerPanel, 260, SpringLayout.NORTH, composerPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.EAST, composerPanel, 0, SpringLayout.EAST, inputPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.NORTH, composerPanel, 5, SpringLayout.SOUTH, inputPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.WEST, composerPanel, 0, SpringLayout.WEST, inputPanel);

    	vcomposerSpringLayout.putConstraint(SpringLayout.NORTH, destinationLabel, 5, SpringLayout.SOUTH, composerPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.WEST, destinationLabel, 0, SpringLayout.WEST, composerPanel);
        
    	vcomposerSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 115, SpringLayout.NORTH, destinationPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, composerPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, composerPanel);
    	vcomposerSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, composerPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationFileText, 20, SpringLayout.NORTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationFileText, 10, SpringLayout.NORTH, destinationPanel);
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
        
        
        vcomposerSpringLayout.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        vcomposerSpringLayout.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        vcomposerSpringLayout.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        vcomposerSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, destinationPanel);
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
				File item = composerPanel.getSelectedPdfDocument();
				if(item != null){
					fileSource.addAttribute("value",item.getAbsolutePath());
					if(savePasswords){
						fileSource.addAttribute("password",(composerPanel.getSelectedPdfDocumentPassword()!=null?composerPanel.getSelectedPdfDocumentPassword():""));
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
		return GettextResource.gettext(config.getI18nResourceBundle(),"Visual document composer");
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
					composerPanel.getPdfLoader().addFile(new File(fileSource.getText()), password);
				}
				
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null && fileDestination.getText().length()>0){
					destinationFileText.setText(fileDestination.getText());
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
		composerPanel.resetPanel();
		destinationFileText.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(false);
		overwriteCheckbox.setSelected(false);
	}

}