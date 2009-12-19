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
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.XmlUtility;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.vpagereorder.listeners.RunButtonActionListener;
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
    private final PageReorderPolicy pageReorderFocusPolicy = new PageReorderPolicy();
    
    //layouts
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
    private JSplitPane splitPanel = null;
  
    //labels
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
	
  //radio
    private final JRadioButton sameAsSourceRadio = new JRadioButton();
    private final JRadioButton chooseAFileRadio = new JRadioButton();
    
    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.0.6";
    
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
        
        setLayout(new GridBagLayout());        		
        selectionPanel.addPropertyChangeListener(this);

      //DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file"));
		destinationPanel.setBorder(titledBorder);
		destinationPanel.setPreferredSize(new Dimension(180, 160));
        add(destinationPanel);
//END_DESTINATION_PANEL        
//DESTINATION_RADIOS
        sameAsSourceRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Same as source"));
        destinationPanel.add(sameAsSourceRadio);

        chooseAFileRadio.setSelected(true);
        chooseAFileRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Choose a file"));
        destinationPanel.add(chooseAFileRadio);
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
				if (browseDestFileChooser == null) {
					browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
					browseDestFileChooser.setFileFilter(new PdfFilter());
				}
				if (destinationFileText.getText().length() > 0) {
					browseDestFileChooser.setCurrentDirectory(new File(destinationFileText.getText()));
				}
				if (browseDestFileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION) {
					File chosenFile = browseDestFileChooser.getSelectedFile();
					if (chosenFile != null) {
						destinationFileText.setText(chosenFile.getAbsolutePath());
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
        
	    splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,selectionPanel, new JScrollPane(destinationPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));	 
        splitPanel.setOneTouchExpandable(true);
        splitPanel.setResizeWeight(1.0);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);
  		add(splitPanel, c);

	    
        final ButtonGroup outputRadioGroup = new ButtonGroup();
        outputRadioGroup.add(sameAsSourceRadio);
        outputRadioGroup.add(chooseAFileRadio); 
	  //RADIO_LISTENERS
		sameAsSourceRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destinationFileText.setEnabled(false);
				browseDestButton.setEnabled(false);
			}
		});

		chooseAFileRadio.addActionListener(new ActionListener() {
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
		runButton.addActionListener(new RunButtonActionListener(this));
		runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(), "Execute pages reorder"));
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
		setLayout();
	}
    
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
    	
    	destinationPanelLayout.putConstraint(SpringLayout.SOUTH, sameAsSourceRadio, 25, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, sameAsSourceRadio, 1, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, sameAsSourceRadio, 10, SpringLayout.WEST, destinationPanel);        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, chooseAFileRadio, 0, SpringLayout.SOUTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, chooseAFileRadio, 0, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, chooseAFileRadio, 20, SpringLayout.EAST, sameAsSourceRadio);
        
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
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 8, SpringLayout.SOUTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destinationFileText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destinationFileText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);

    }
    
	public FocusTraversalPolicy getFocusPolicy() {
		return pageReorderFocusPolicy;
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
						VisualPageListItem[] pages = ((VisualListModel)selectionPanel.getThumbnailList().getModel()).getElements();
						if(pages!=null && pages.length>0){
							for(VisualPageListItem page: pages){
								Element currentPage = fileSource.addElement("page");
								currentPage.addAttribute("number", Integer.toString(page.getPageNumber()));
								currentPage.addAttribute("deleted", String.valueOf(page.isDeleted()));
								currentPage.addAttribute("rotation", Integer.toString(page.getRotation().getDegrees()));
							}
						}
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

	@SuppressWarnings("unchecked")
	public void loadJobNode(Node arg0) throws LoadJobException {
		try{
			Node fileSource = (Node) arg0.selectSingleNode("source/@value");
			if (fileSource != null && fileSource.getText().length()>0){
				Node filePwd = (Node) arg0.selectSingleNode("source/@password");
				String password = null;
				if (filePwd != null && filePwd.getText().length()>0){
					password = filePwd.getText();
				}
				List<DocumentPage> template = null;
				List<Node> pages = arg0.selectNodes("source/page");
				if(pages!=null && pages.size()>0){
					for(Node pageNode : pages){
						DocumentPage currentPage = XmlUtility.getDocumentPage(pageNode);
						if(currentPage != null){
							if(template==null){
								template = new ArrayList<DocumentPage>();
							}
							template.add(currentPage);
						}
					}
				}
				selectionPanel.getPdfLoader().addFile(new File(fileSource.getText()), password, template);
			}
			
			Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
			if (fileDestination != null && fileDestination.getText().length()>0){
				destinationFileText.setText(fileDestination.getText());
				chooseAFileRadio.doClick();
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
			
			log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Visual Reorder section loaded."));  
        }
		catch (Exception ex){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                     
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
			chooseAFileRadio.doClick();
			destinationFileText.setText(((String)evt.getNewValue())+File.separatorChar+DEFAULT_OUPUT_NAME);
		}		
	}
	
	/**
	 * Focus policy for the page reorder plugin
	 * @author Andrea Vacondio
	 *
	 */
    public class PageReorderPolicy extends FocusTraversalPolicy {
        public PageReorderPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(selectionPanel.getLoadFileButton())){
            	if(selectionPanel.getClearButton()!=null){
            		return selectionPanel.getClearButton();
            	}else{
            		return selectionPanel.getZoomInButton();
            	}
            }
            else if (aComponent.equals(selectionPanel.getClearButton())){
                return selectionPanel.getZoomInButton();
            }
            else if (aComponent.equals(selectionPanel.getZoomInButton())){
                return selectionPanel.getZoomOutButton();
            }
            else if (aComponent.equals(selectionPanel.getZoomOutButton())){
                return selectionPanel.getMoveUpButton();
            }        
            else if (aComponent.equals(selectionPanel.getMoveUpButton())){
                return selectionPanel.getMoveDownButton();
            }        
            else if (aComponent.equals(selectionPanel.getMoveDownButton())){
                return selectionPanel.getRemoveButton();
            }        
            else if (aComponent.equals(selectionPanel.getRemoveButton())){
            	if(selectionPanel.getUndeleteButton()!=null){
            		return selectionPanel.getUndeleteButton();
            	}else{
            		return selectionPanel.getRotateButton();
            	}
            }
            else if (aComponent.equals(selectionPanel.getUndeleteButton())){
                return selectionPanel.getRotateButton();
            }  
            else if (aComponent.equals(selectionPanel.getRotateButton())){
                return selectionPanel.getRotateAntiButton();
            }  
            else if (aComponent.equals(selectionPanel.getRotateAntiButton())){
                return sameAsSourceRadio;
            }  
            else if (aComponent.equals(sameAsSourceRadio)){
                return chooseAFileRadio;
            }
            else if (aComponent.equals(chooseAFileRadio)){
                if (destinationFileText.isEnabled()){
                    return destinationFileText;
                }else{
                    return overwriteCheckbox;
                }                
            }
            else if (aComponent.equals(destinationFileText)){
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
                return selectionPanel.getLoadFileButton();
            }
            return selectionPanel.getLoadFileButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
        	 if (aComponent.equals(selectionPanel.getLoadFileButton())){
 				return runButton;
 			 }
             else if (aComponent.equals(selectionPanel.getClearButton())){
                 return selectionPanel.getLoadFileButton();
             }
             else if (aComponent.equals(selectionPanel.getZoomInButton())){
            	 if(selectionPanel.getClearButton()!=null){
              		return selectionPanel.getClearButton();
              	}else{
              		return selectionPanel.getLoadFileButton();
              	}
             }
             else if (aComponent.equals(selectionPanel.getZoomOutButton())){
                 return selectionPanel.getZoomInButton();
             }        
             else if (aComponent.equals(selectionPanel.getMoveUpButton())){
                 return selectionPanel.getZoomOutButton();
             }        
             else if (aComponent.equals(selectionPanel.getMoveDownButton())){
                 return selectionPanel.getMoveUpButton();
             }        
             else if (aComponent.equals(selectionPanel.getRemoveButton())){
           		return selectionPanel.getMoveDownButton();
             }
             else if (aComponent.equals(selectionPanel.getUndeleteButton())){
                 return selectionPanel.getRemoveButton();
             }  
             else if (aComponent.equals(selectionPanel.getRotateButton())){
            	 if(selectionPanel.getUndeleteButton()!=null){
              		return selectionPanel.getUndeleteButton();
              	}else{
              		return selectionPanel.getRemoveButton();
              	}
             }  
             else if (aComponent.equals(selectionPanel.getRotateAntiButton())){
                 return selectionPanel.getRotateButton();
             }  
             else if (aComponent.equals(sameAsSourceRadio)){
                 return selectionPanel.getRotateAntiButton();
             }
             else if (aComponent.equals(chooseAFileRadio)){
            	 return sameAsSourceRadio;             
             }
             else if (aComponent.equals(destinationFileText)){
                 return chooseAFileRadio;
             }
             else if (aComponent.equals(browseDestButton)){
                 return destinationFileText;
             }
             else if (aComponent.equals(overwriteCheckbox)){
            	  if (destinationFileText.isEnabled()){
                      return browseDestButton;
                  }else{
                      return chooseAFileRadio;
                  }   
             }   
             else if (aComponent.equals(outputCompressedCheck)){
                 return overwriteCheckbox;
             }   
 			else if (aComponent.equals(versionCombo)){
 				return outputCompressedCheck;
 			}
 			else if (aComponent.equals(runButton)){
 				return versionCombo;
 			}            
            return selectionPanel.getLoadFileButton();
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return selectionPanel.getLoadFileButton();
        }

        public Component getLastComponent(Container CycleRootComp){
            return runButton;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return selectionPanel.getLoadFileButton();
        }
    }

	/**
	 * @return the destinationFileText
	 */
	public JTextField getDestinationFileText() {
		return destinationFileText;
	}

	/**
	 * @return the versionCombo
	 */
	public JPdfVersionCombo getVersionCombo() {
		return versionCombo;
	}

	/**
	 * @return the selectionPanel
	 */
	public JVisualPdfPageSelectionPanel getSelectionPanel() {
		return selectionPanel;
	}

	/**
	 * @return the overwriteCheckbox
	 */
	public JCheckBox getOverwriteCheckbox() {
		return overwriteCheckbox;
	}

	/**
	 * @return the outputCompressedCheck
	 */
	public JCheckBox getOutputCompressedCheck() {
		return outputCompressedCheck;
	}

	/**
	 * @return the sameAsSourceRadio
	 */
	public JRadioButton getSameAsSourceRadio() {
		return sameAsSourceRadio;
	}
    
    
}
