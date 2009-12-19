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
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.pdfsam.guiclient.commons.panels.JVisualMultiSelectionPanel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfFile;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.vcomposer.listeners.RunButtonActionListener;
import com.lowagie.text.pdf.codec.Base64;

/**
 * Visual document composer plugin  main panel
 * @author Andrea Vacondio
 *
 */
public class VComposerMainGUI extends AbstractPlugablePanel implements PropertyChangeListener{

	private static final long serialVersionUID = -3265981976255542241L;
	
	private static final String DEFAULT_OUPUT_NAME = "composed_file.pdf";
	
	private static final Logger log = Logger.getLogger(VComposerMainGUI.class.getPackage().getName());
	
    private JTextField destinationFileText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JHelpLabel destinationHelpLabel;
    private Configuration config;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo(false);
    private JVisualPdfPageSelectionPanel composerPanel = new JVisualPdfPageSelectionPanel(JVisualPdfPageSelectionPanel.HORIZONTAL_ORIENTATION, false, true, true, JVisualPdfPageSelectionPanel.STYLE_TOP_PANEL_MEDIUM, JVisualPdfPageSelectionPanel.DND_SUPPORT_JAVAOBJECTS, JVisualPdfPageSelectionPanel.SINGLE_INTERVAL_SELECTION);	
    private JSplitPane lowerSplitPanel = null;
    private JSplitPane higherSplitPanel = null;
	
    //layouts
    private SpringLayout destinationPanelLayout;
    
	//button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
	private final JButton moveToBottomButton = new JButton();
	private final JButton moveOnTopButton = new JButton();
  
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
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
	private final VisualComposerPolicy visualComposerFocusPolicy = new VisualComposerPolicy();
	 
    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.0.7";
    
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
        
        setLayout(new GridBagLayout());
               
        inputPanel.setOutputPathPropertyChangeListener(this);
        inputPanel.setPreferredSize(new Dimension(180, 180));
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));

		moveOnTopButton.setMargin(new Insets(2, 2, 2, 2));
		moveOnTopButton.setIcon(new ImageIcon(this.getClass().getResource("/images/movetop.png")));
		moveOnTopButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move on top"));
		moveOnTopButton.setHorizontalTextPosition(AbstractButton.LEADING);
		moveOnTopButton.addKeyListener(new EnterDoClickListener(moveOnTopButton));
		moveOnTopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveOnTopButton.setMinimumSize(new Dimension(90, 25));
		moveOnTopButton.setPreferredSize(new Dimension(120, 25));
		moveOnTopButton.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
            	 VisualPageListItem[] elements = inputPanel.getSelectedElements();
            	 if(elements!=null && elements.length>0){
            		 Vector<VisualPageListItem> newList = new Vector<VisualPageListItem>(elements.length);
            		 for(VisualPageListItem currItem : elements){
            			 newList.add((VisualPageListItem) currItem.clone());
            		 }
            		 composerPanel.prependElements(newList);
            	 }
             }
		 });
        
		moveToBottomButton.setMargin(new Insets(2, 2, 2, 2));
		moveToBottomButton.setIcon(new ImageIcon(this.getClass().getResource("/images/movebottom.png")));
		moveToBottomButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move to bottom"));
		moveToBottomButton.addKeyListener(new EnterDoClickListener(moveOnTopButton));
		moveToBottomButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveToBottomButton.setMinimumSize(new Dimension(90, 25));
		moveToBottomButton.setPreferredSize(new Dimension(120, 25));
		moveToBottomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VisualPageListItem[] elements = inputPanel.getSelectedElements();
				if (elements != null && elements.length > 0) {
					Vector<VisualPageListItem> newList = new Vector<VisualPageListItem>(elements.length);
					for (VisualPageListItem currItem : elements) {
						newList.add((VisualPageListItem) currItem.clone());
					}
					composerPanel.appendElements(newList);
				}
			}
		});
		
		buttonsPanel.add(moveOnTopButton);		
		buttonsPanel.add(Box.createRigidArea(new Dimension(10,5)));
		buttonsPanel.add(moveToBottomButton);
		
        composerPanel.addToTopPanel(buttonsPanel);
        composerPanel.disableSetOutputPathMenuItem();        
        JScrollPane composerScroll = new JScrollPane(composerPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        composerScroll.setPreferredSize(new Dimension(180, 180));

        higherSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,inputPanel, composerScroll);
        higherSplitPanel.setDividerSize(2);
        higherSplitPanel.setDividerLocation(0.5);
        higherSplitPanel.setResizeWeight(0.5);
		
      //DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file"));
		destinationPanel.setBorder(titledBorder);
		destinationPanel.setPreferredSize(new Dimension(200, 160));
		//END_DESTINATION_PANEL        
                
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
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a file browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files.")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"PDF version 1.5 or above.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION 
	    
	    lowerSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,higherSplitPanel, new JScrollPane(destinationPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        lowerSplitPanel.setOneTouchExpandable(true);
        lowerSplitPanel.setResizeWeight(1.0);


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
        
  		add(lowerSplitPanel, c);	 
	  //ENTER_KEY_LISTENERS
	          browseDestButton.addKeyListener(browsedEnterkeyListener);
	          runButton.addKeyListener(runEnterkeyListener);
	  //END_ENTER_KEY_LISTENERS
	        runButton.addActionListener(new RunButtonActionListener(this));
	        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Execute pages reorder"));
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
		return visualComposerFocusPolicy;
	}

	public Node getJobNode(Node arg0, boolean savePasswords)
			throws SaveJobException {
		try {
			if (arg0 != null) {
				Element inputDocuments = ((Element)arg0).addElement("input-documents");
				PdfFile[] openFiles = inputPanel.getInputDocuments();
				if(openFiles!=null && openFiles.length>0){
					for(PdfFile openFile: openFiles){
						if(openFile != null && openFile.getInputFile()!=null){
							Element fileSource = ((Element)inputDocuments).addElement("source");							
							fileSource.addAttribute("value",openFile.getInputFile().getAbsolutePath());
							if(savePasswords){
								fileSource.addAttribute("password",(openFile.getPassword()!=null?openFile.getPassword():""));
							}
						}
					}
				}
				
				Element outputDocument = ((Element)arg0).addElement("output-document");
				VisualPageListItem[] pages = ((VisualListModel)composerPanel.getThumbnailList().getModel()).getElements();
				if(pages!=null && pages.length>0){
					
					for(VisualPageListItem page: pages){
						Element outputFile = ((Element)outputDocument).addElement("page");
						outputFile.setText(Base64.encodeObject(page));						
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

	@SuppressWarnings("unchecked")
	public void loadJobNode(Node arg0) throws LoadJobException {
		try{
			List<Node> filesSource = arg0.selectNodes("input-documents/source");
			if(filesSource!=null && filesSource.size()>0){
				for(Node fileNode : filesSource){
					Node fileSource = (Node) fileNode.selectSingleNode("@value");
					if (fileSource != null && fileSource.getText().length()>0){
						Node filePwd = (Node) fileNode.selectSingleNode("@password");
						String password = null;
						if (filePwd != null && filePwd.getText().length()>0){
							password = filePwd.getText();
						}
						inputPanel.addTab(new File(fileSource.getText()), password);
					}
				}
			}				
			
			List<Node> outputPages = arg0.selectNodes("output-document/page");
			if(outputPages!=null && outputPages.size()>0){
				for(Node pageNode : outputPages){
					String serializedPage = pageNode.getText();
					if(serializedPage!=null && serializedPage.length()>0){
						Object pageObject = Base64.decodeToObject(serializedPage);
						if(pageObject!=null){
							((VisualListModel)composerPanel.getThumbnailList().getModel()).addElement((VisualPageListItem) pageObject);
						}
					}
				}
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
			
			log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Visual Composer section loaded."));  
        }
		catch (Exception ex){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                     
		}
	}

	public void resetPanel() {
		inputPanel.removeAllTabs();
		composerPanel.resetPanel();
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
	
	/**
	 * Focus policy for the visual composer plugin
	 * @author Andrea Vacondio
	 *
	 */
    public class VisualComposerPolicy extends FocusTraversalPolicy {
        public VisualComposerPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){ 
        	JButton zoomIn = inputPanel.getCurrentZoomInButton();
        	JButton zoomOut = inputPanel.getCurrentZoomOutButton();
        	if (aComponent.equals(inputPanel.getOpenButton())){
        		if(zoomIn!=null){
        			return zoomIn;
        		}else{
            		return moveOnTopButton;             	        			
        		}
            }else if(zoomIn!=null &&  aComponent.equals(zoomIn)){
            	return zoomOut;
            }else if(zoomOut!=null &&  aComponent.equals(zoomOut)){
	        	return moveOnTopButton;
	        }
        	else if (aComponent.equals(moveOnTopButton)){
        		return moveToBottomButton; 
        	}
        	else if (aComponent.equals(moveToBottomButton)){
        		if(composerPanel.getClearButton()!=null){
            		return composerPanel.getClearButton();
            	}else{
            		return composerPanel.getZoomInButton();
            	}
        	}
            else if (aComponent.equals(composerPanel.getClearButton())){
                return composerPanel.getZoomInButton();
            }
            else if (aComponent.equals(composerPanel.getZoomInButton())){
                return composerPanel.getZoomOutButton();
            }
            else if (aComponent.equals(composerPanel.getZoomOutButton())){
                return composerPanel.getMoveUpButton();
            }        
            else if (aComponent.equals(composerPanel.getMoveUpButton())){
                return composerPanel.getMoveDownButton();
            }        
            else if (aComponent.equals(composerPanel.getMoveDownButton())){
                return composerPanel.getRemoveButton();
            }        
            else if (aComponent.equals(composerPanel.getRemoveButton())){
            	if(composerPanel.getUndeleteButton()!=null){
            		return composerPanel.getUndeleteButton();
            	}else{
            		return composerPanel.getRotateButton();
            	}
            }
            else if (aComponent.equals(composerPanel.getUndeleteButton())){
                return composerPanel.getRotateButton();
            }  
            else if (aComponent.equals(composerPanel.getRotateButton())){
                return composerPanel.getRotateAntiButton();
            }  
            else if (aComponent.equals(composerPanel.getRotateAntiButton())){
            	 return composerPanel.getReverseButton();
            }
            else if (aComponent.equals(composerPanel.getReverseButton())){
           	 return destinationFileText;
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
                return inputPanel.getOpenButton();
            }
            return inputPanel.getOpenButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
        	JButton zoomIn = inputPanel.getCurrentZoomInButton();
        	JButton zoomOut = inputPanel.getCurrentZoomOutButton();
        	if (aComponent.equals(inputPanel.getOpenButton())){
 				return runButton;
 			 }
             else if (aComponent.equals(composerPanel.getClearButton())){
                 return moveToBottomButton;
             }
             else if (aComponent.equals(moveToBottomButton)){
                 return moveOnTopButton;
             }
             else if (aComponent.equals(moveOnTopButton)){
            	if(zoomOut!=null){
         			return zoomOut;
         		}else{
         			return inputPanel.getOpenButton();
                }
             }    	
		     else if(zoomOut!=null && aComponent.equals(zoomOut)){
		    	return zoomIn;
		     }
		     else if(zoomIn!=null && aComponent.equals(zoomIn)){
		     	return inputPanel.getOpenButton();
		     }
             else if (aComponent.equals(composerPanel.getZoomInButton())){
            	 if(composerPanel.getClearButton()!=null){
             		return composerPanel.getClearButton();
             	}else{
             		return moveToBottomButton;
             	}
             }
             else if (aComponent.equals(composerPanel.getZoomOutButton())){
                 return composerPanel.getZoomInButton();
             }        
             else if (aComponent.equals(composerPanel.getMoveUpButton())){
                 return composerPanel.getZoomOutButton();
             }        
             else if (aComponent.equals(composerPanel.getMoveDownButton())){
                 return composerPanel.getMoveUpButton();
             }        
             else if (aComponent.equals(composerPanel.getRemoveButton())){
           		return composerPanel.getMoveDownButton();
             }
             else if (aComponent.equals(composerPanel.getUndeleteButton())){
                 return composerPanel.getRemoveButton();
             }  
             else if (aComponent.equals(composerPanel.getRotateButton())){
            	 if(composerPanel.getUndeleteButton()!=null){
              		return composerPanel.getUndeleteButton();
              	}else{
              		return composerPanel.getRemoveButton();
              	}
             }  
             else if (aComponent.equals(composerPanel.getRotateAntiButton())){
                 return composerPanel.getRotateButton();
             }  
             else if (aComponent.equals(destinationFileText)){
                 return composerPanel.getReverseButton();
             }            
             else if (aComponent.equals(composerPanel.getReverseButton())){
                 return composerPanel.getRotateAntiButton();
             }            
             else if (aComponent.equals(browseDestButton)){
                 return destinationFileText;
             }
             else if (aComponent.equals(overwriteCheckbox)){
            	  return browseDestButton; 
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
            return inputPanel.getOpenButton();
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return inputPanel.getOpenButton();
        }

        public Component getLastComponent(Container CycleRootComp){
            return runButton;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return inputPanel.getOpenButton();
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
	 * @return the composerPanel
	 */
	public JVisualPdfPageSelectionPanel getComposerPanel() {
		return composerPanel;
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
    
    
}