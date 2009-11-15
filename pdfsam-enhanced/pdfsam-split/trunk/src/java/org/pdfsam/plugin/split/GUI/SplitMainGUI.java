/*
 * Created on 17-Feb-2006
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
package org.pdfsam.plugin.split.GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.models.SimplePdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.split.components.JBLevelCombo;
import org.pdfsam.plugin.split.components.JSplitRadioButton;
import org.pdfsam.plugin.split.components.JSplitRadioButtonModel;
import org.pdfsam.plugin.split.components.JSplitSizeCombo;
import org.pdfsam.plugin.split.listeners.RadioListener;
import org.pdfsam.plugin.split.listeners.RunButtonActionListener;
/**
 * Plugable JPanel provides a GUI for split functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class SplitMainGUI  extends AbstractPlugablePanel{
    
	private static final long serialVersionUID = -5907189950338614835L;

	private static final Logger log = Logger.getLogger(SplitMainGUI.class.getPackage().getName());
	
	private JTextField outPrefixText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.PREFIX_TEXT_FIELD_TYPE_FULL_MENU);
    private SpringLayout outputPanelLayout;
    private SpringLayout destinationPanelLayout;
    private JTextField destinationFolderText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JTextField thisPageTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JTextField nPagesTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JHelpLabel checksHelpLabel;
    private SpringLayout optionsPaneLayout;
    private JHelpLabel prefixHelpLabel;
    private JHelpLabel destinationHelpLabel; 
    private SpringLayout splitSpringLayout;
    private String  splitType = "";
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo(true);
    private Configuration config;
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.SINGLE_SELECTABLE_FILE, SimplePdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER);
	private JSplitSizeCombo splitSizeCombo = new JSplitSizeCombo();
	private JBLevelCombo bLevelCombo = new JBLevelCombo(selectionPanel);
    
//file_chooser    
    private JFileChooser browseDestFileChooser = null;

//button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
//key_listeners
    private final EnterDoClickListener browsedEnterkeyListener = new EnterDoClickListener(browseDestButton);
    private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
    
//split_radio
    private final JSplitRadioButton burstRadio = new JSplitRadioButton(SplitParsedCommand.S_BURST);
    private final JSplitRadioButton everyNRadio = new JSplitRadioButton(SplitParsedCommand.S_NSPLIT);        
    private final JSplitRadioButton evenRadio = new JSplitRadioButton(SplitParsedCommand.S_EVEN);
    private final JSplitRadioButton oddRadio = new JSplitRadioButton(SplitParsedCommand.S_ODD);
    private final JSplitRadioButton thisPageRadio = new JSplitRadioButton(SplitParsedCommand.S_SPLIT);
    private final JSplitRadioButton sizeRadio = new JSplitRadioButton(SplitParsedCommand.S_SIZE);
    private final JSplitRadioButton bookmarksLevel = new JSplitRadioButton(SplitParsedCommand.S_BLEVEL);
    
    private RadioListener radioListener;
//radio
    private final JRadioButton sameAsSourceRadio = new JRadioButton();
    private final JRadioButton chooseAFolderRadio = new JRadioButton();
    private ButtonGroup splitOptionsRadioGroup;
//checks
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
    
//focus policy 
    private final SplitFocusPolicy splitFocusPolicy = new SplitFocusPolicy();

//panels
    private final JPanel splitOptionsPanel = new JPanel();
    private final JPanel destinationPanel = new JPanel();
    private final JPanel outputOptionsPanel = new JPanel();
    private final JPanel splitTypesPanel = new JPanel();
    
//labels    
    private final JLabel outPrefixLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	
	
  
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_VERSION = "0.5.6";
    
/**
 * Constructor
 *
 */    
    public SplitMainGUI() {
        super();
        initialize();
    }

    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/split.png");
        setPreferredSize(new Dimension(500,555));
        
//        
        splitSpringLayout = new SpringLayout();
        setLayout(splitSpringLayout);
        add(selectionPanel);

//SPLIT_SECTION
        optionsPaneLayout = new SpringLayout();
        splitOptionsPanel.setLayout(optionsPaneLayout);
        
        splitOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Split options")));
        add(splitOptionsPanel);

        GridLayout splitOptionsLayout = new GridLayout(0,3,5,5);
        splitTypesPanel.setLayout(splitOptionsLayout);
        
        burstRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Burst (split into single pages)"));        
        everyNRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split every \"n\" pages"));
        evenRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split even pages"));      
        oddRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split odd pages"));
        nPagesTextField.setEnabled(false);
        thisPageRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split after these pages"));
        sizeRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split at this size"));
        bookmarksLevel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split by bookmarks level"));
        thisPageTextField.setEnabled(false);
        splitSizeCombo.setEnabled(false);
        bLevelCombo.setEnabled(false);
        
        splitTypesPanel.add(burstRadio);
        splitTypesPanel.add(thisPageRadio);
        splitTypesPanel.add(thisPageTextField);

        splitTypesPanel.add(evenRadio);
        splitTypesPanel.add(everyNRadio);        
        splitTypesPanel.add(nPagesTextField);        
                
        splitTypesPanel.add(oddRadio);               
        splitTypesPanel.add(sizeRadio);                
        splitTypesPanel.add(splitSizeCombo);
        
        splitTypesPanel.add(new JLabel());               
        splitTypesPanel.add(bookmarksLevel);                
        splitTypesPanel.add(bLevelCombo);
        
        splitOptionsPanel.add(splitTypesPanel);
        
        String helpText = 
        		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split options")+"</b><ul>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Burst")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Explode the pdf document into single pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split every \"n\" pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every \"n\" pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split even pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every even page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split odd pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every odd page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split after these pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document after page numbers (num1-num2-num3..)")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split at this size")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document in files of the given size (roughly)")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split by bookmarks level")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document at pages referred by bookmarks of the given level")+".</li>" +
        		"</ul></body></html>";
        checksHelpLabel = new JHelpLabel(helpText, true);
        splitOptionsPanel.add(checksHelpLabel);        
//END_SPLIT_SECTION

//RADIO_LISTENERS
        /*This listeners enable or disable text field based on what you select*/
        radioListener = new RadioListener(this, nPagesTextField, thisPageTextField, splitSizeCombo, bLevelCombo); 
        burstRadio.setActionCommand(RadioListener.DISABLE_ALL);
        burstRadio.addActionListener(radioListener);
        everyNRadio.setActionCommand(RadioListener.ENABLE_FIRST);
        everyNRadio.addActionListener(radioListener);
        evenRadio.setActionCommand(RadioListener.DISABLE_ALL);
        evenRadio.addActionListener(radioListener);
        oddRadio.setActionCommand(RadioListener.DISABLE_ALL);
        oddRadio.addActionListener(radioListener);
        thisPageRadio.setActionCommand(RadioListener.ENABLE_SECOND);
        thisPageRadio.addActionListener(radioListener);
        sizeRadio.setActionCommand(RadioListener.ENABLE_THIRD);
        sizeRadio.addActionListener(radioListener);
        bookmarksLevel.setActionCommand(RadioListener.ENABLE_FOURTH);
        bookmarksLevel.addActionListener(radioListener);
//END_RADIO_LISTENERS 
//DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder")));
        add(destinationPanel);
//END_DESTINATION_PANEL        
//DESTINATION_RADIOS
        sameAsSourceRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Same as source"));
        destinationPanel.add(sameAsSourceRadio);

        chooseAFolderRadio.setSelected(true);
        chooseAFolderRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Choose a folder"));
        destinationPanel.add(chooseAFolderRadio);
//END_DESTINATION_RADIOS        
//RADIOGROUP
        splitOptionsRadioGroup = new ButtonGroup();
        splitOptionsRadioGroup.add(burstRadio);
        splitOptionsRadioGroup.add(everyNRadio);
        splitOptionsRadioGroup.add(evenRadio);
        splitOptionsRadioGroup.add(oddRadio);
        splitOptionsRadioGroup.add(thisPageRadio);
        splitOptionsRadioGroup.add(sizeRadio);
        splitOptionsRadioGroup.add(bookmarksLevel);        
        final ButtonGroup outputRadioGroup = new ButtonGroup();
        outputRadioGroup.add(sameAsSourceRadio);
        outputRadioGroup.add(chooseAFolderRadio);  
//END_RADIOGROUP

        destinationPanel.add(destinationFolderText);
        destinationPanel.add(overwriteCheckbox);
        
        destinationPanel.add(outputCompressedCheck); 
		
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        
        destinationPanel.add(versionCombo);
        
        destinationPanel.add(outputVersionLabel);
        browseDestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(browseDestFileChooser==null){
                    browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());                    
                    browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            	}
                File chosenFile = null;  
                if(destinationFolderText.getText().length()>0){
                	browseDestFileChooser.setCurrentDirectory(new File(destinationFolderText.getText()));
                }
                if (browseDestFileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION){
                    chosenFile = browseDestFileChooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosenFile != null && chosenFile.isDirectory()){
                    try{
                        destinationFolderText.setText(chosenFile.getAbsolutePath());
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
	    
//S_PANEL
        outputOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Output options")));
        outputPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(outputPanelLayout);
        add(outputOptionsPanel);

        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);

        outPrefixText.setPreferredSize(new Dimension(180,20));
        outputOptionsPanel.add(outPrefixText);
//END_S_PANEL
//HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it contains \"[CURRENTPAGE]\", \"[TIMESTAMP]\", \"[FILENUMBER]\" or \"[BOOKMARK_NAME]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Ex. prefix_[BASENAME]_[CURRENTPAGE] generates prefix_FileName_005.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it doesn't contain \"[CURRENTPAGE]\", \"[TIMESTAMP]\" or \"[FILENUMBER]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Available variables")+": [CURRENTPAGE], [TIMESTAMP], [BASENAME], [FILENUMBER], [BOOKMARK_NAME].</p>"+
    		"</body></html>";
	    prefixHelpLabel = new JHelpLabel(helpTextPrefix, true);
	    outputOptionsPanel.add(prefixHelpLabel);
//END_HELP_LABEL_PREFIX
//RUN_BUTTON
        //listener
        runButton.addActionListener(new RunButtonActionListener(this));
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Split selected file"));
        runButton.setMargin(new Insets(5, 5, 5, 5));
        runButton.setSize(new Dimension(88,25));
        add(runButton);
//END_RUN_BUTTON
        
//RADIO_LISTENERS
        sameAsSourceRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destinationFolderText.setEnabled(false);
                browseDestButton.setEnabled(false);
            }
        });
        
        chooseAFolderRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destinationFolderText.setEnabled(true);
                browseDestButton.setEnabled(true);
            }
        });
//END_RADIO_LISTENERS
//ENTER_KEY_LISTENERS
        browseDestButton.addKeyListener(browsedEnterkeyListener);
        runButton.addKeyListener(runEnterkeyListener);
        outPrefixText.addKeyListener(runEnterkeyListener);
//END_ENTER_KEY_LISTENERS
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
        return GettextResource.gettext(config.getI18nResourceBundle(),"Split");
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
				Element fileSource = ((Element)arg0).addElement("source");
				PdfSelectionTableItem[] items = selectionPanel.getTableRows();
				if(items != null && items.length>0){
					fileSource.addAttribute("value",items[0].getInputFile().getAbsolutePath());
					if(savePasswords){
						fileSource.addAttribute("password",items[0].getPassword());
					}
				}
				
				Element splitOption = ((Element)arg0).addElement("split_option");
				if(splitOptionsRadioGroup.getSelection() != null){
					splitOption.addAttribute("value",((JSplitRadioButtonModel)splitOptionsRadioGroup.getSelection()).getSplitCommand());
				}
				Element splitNpages = ((Element)arg0).addElement("npages");
				splitNpages.addAttribute("value", nPagesTextField.getText());			
			
				Element splitThispage = ((Element)arg0).addElement("thispage");
				splitThispage.addAttribute("value", thisPageTextField.getText());			

				Element splitSize = ((Element)arg0).addElement("splitsize");
				splitSize.addAttribute("value", splitSizeCombo.getSelectedItem().toString());			

				Element bookLevel = ((Element)arg0).addElement("bookmarkslevel");
				if(bLevelCombo.getSelectedItem() != null){
					bookLevel.addAttribute("value", bLevelCombo.getSelectedItem().toString());			
				}
				
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationFolderText.getText());			
				
				Element filePrefix = ((Element)arg0).addElement("prefix");
				filePrefix.addAttribute("value", outPrefixText.getText());	
				
				Element file_overwrite = ((Element)arg0).addElement("overwrite");
				file_overwrite.addAttribute("value", overwriteCheckbox.isSelected()?TRUE:FALSE);

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
					selectionPanel.getLoader().addFile(new File(fileSource.getText()), password);
				}

				Node splitOption = (Node) arg0.selectSingleNode("split_option/@value");
				if (splitOption != null){
					if(splitOption.getText().equals(burstRadio.getSplitCommand()))  burstRadio.doClick();
					else if(splitOption.getText().equals(everyNRadio.getSplitCommand()))  everyNRadio.doClick();
					else if(splitOption.getText().equals(evenRadio.getSplitCommand()))  evenRadio.doClick();
					else if(splitOption.getText().equals(oddRadio.getSplitCommand()))  oddRadio.doClick();
					else if(splitOption.getText().equals(thisPageRadio.getSplitCommand()))  thisPageRadio.doClick();
					else if(splitOption.getText().equals(sizeRadio.getSplitCommand()))  sizeRadio.doClick();
					else if(splitOption.getText().equals(bookmarksLevel.getSplitCommand()))  bookmarksLevel.doClick();					
				}
				Node splitNpages = (Node) arg0.selectSingleNode("npages/@value");
				if (splitNpages != null){
					nPagesTextField.setText(splitNpages.getText());
				}

				Node splitThispage = (Node) arg0.selectSingleNode("thispage/@value");
				if (splitThispage != null){
					thisPageTextField.setText(splitThispage.getText());
				}
				
				Node splitSize = (Node) arg0.selectSingleNode("splitsize/@value");
				if (splitSize != null){
					splitSizeCombo.setSelectedItem(splitSize.getText());
				}
				
				Node bookLevel = (Node) arg0.selectSingleNode("bookmarkslevel/@value");
				if (bookLevel != null){
					bLevelCombo.setSelectedItem(bookLevel.getText());
				}
				
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null && fileDestination.getText().length()>0){
					destinationFolderText.setText(fileDestination.getText());
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
				
				Node filePrefix = (Node) arg0.selectSingleNode("prefix/@value");
				if (filePrefix != null){
					outPrefixText.setText(filePrefix.getText());
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
	  
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
    	splitSpringLayout.putConstraint(SpringLayout.SOUTH, selectionPanel, 115, SpringLayout.NORTH, this);
		splitSpringLayout.putConstraint(SpringLayout.EAST, selectionPanel, 0, SpringLayout.EAST, this);
		splitSpringLayout.putConstraint(SpringLayout.NORTH, selectionPanel, 0, SpringLayout.NORTH, this);
		splitSpringLayout.putConstraint(SpringLayout.WEST, selectionPanel, 0, SpringLayout.WEST, this);
//      LAYOUT
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, splitOptionsPanel, 145, SpringLayout.NORTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, splitOptionsPanel, 0, SpringLayout.EAST, this);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, splitOptionsPanel, 20, SpringLayout.SOUTH, selectionPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, splitOptionsPanel, 0, SpringLayout.WEST, selectionPanel);       
        
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 160, SpringLayout.NORTH, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 10, SpringLayout.SOUTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, splitOptionsPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, sameAsSourceRadio, 25, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, sameAsSourceRadio, 0, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, sameAsSourceRadio, 10, SpringLayout.WEST, destinationPanel);        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, chooseAFolderRadio, 0, SpringLayout.SOUTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, chooseAFolderRadio, 0, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, chooseAFolderRadio, 20, SpringLayout.EAST, sameAsSourceRadio);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationFolderText, 50, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationFolderText, 30, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationFolderText, -105, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationFolderText, 5, SpringLayout.WEST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destinationFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destinationFolderText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destinationFolderText);
		
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destinationFolderText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destinationFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destinationFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);

		
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, outputOptionsPanel, 55, SpringLayout.NORTH, outputOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsPanel, 0, SpringLayout.EAST, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsPanel, 10, SpringLayout.SOUTH, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsPanel, 0, SpringLayout.WEST, destinationPanel);
        outputPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 20, SpringLayout.NORTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 0, SpringLayout.NORTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixText, 0, SpringLayout.SOUTH, outPrefixLabel);
        outputPanelLayout.putConstraint(SpringLayout.WEST, outPrefixText, 10, SpringLayout.EAST, outPrefixLabel);
        outputPanelLayout.putConstraint(SpringLayout.EAST, outPrefixText, -30, SpringLayout.EAST, outputOptionsPanel);
        
        outputPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.EAST, runButton, -11, SpringLayout.EAST, this);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, outputOptionsPanel);


//      RADIO_LAYOUT
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, splitTypesPanel, 0, SpringLayout.NORTH, splitOptionsPanel);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, splitTypesPanel, 10, SpringLayout.WEST, splitOptionsPanel);
                 
        optionsPaneLayout.putConstraint(SpringLayout.SOUTH, checksHelpLabel, -1, SpringLayout.SOUTH, splitOptionsPanel);
        optionsPaneLayout.putConstraint(SpringLayout.EAST, checksHelpLabel, -1, SpringLayout.EAST, splitOptionsPanel);         
    }

    /**
     * @param splitType The splitType to set.
     */
    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }
    
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)splitFocusPolicy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for split panel
     *
     */
    public class SplitFocusPolicy extends FocusTraversalPolicy {
        public SplitFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
        	if (aComponent.equals(selectionPanel.getAddFileButton())){
				return selectionPanel.getRemoveFileButton();
			}
			else if (aComponent.equals(selectionPanel.getRemoveFileButton())){
				return burstRadio;
			}                    
            else if (aComponent.equals(burstRadio)){
                return evenRadio;
            }
            else if (aComponent.equals(evenRadio)){
                return oddRadio;
            }
            else if (aComponent.equals(oddRadio)){
                return thisPageRadio;
            }
            else if (aComponent.equals(thisPageRadio)){
                if (thisPageTextField.isEnabled()){
                    return thisPageTextField;
                }else{
                    return everyNRadio;
                }
            }
            else if (aComponent.equals(thisPageTextField)){
                return everyNRadio;
            }
            else if (aComponent.equals(everyNRadio)){
                if (nPagesTextField.isEnabled()){
                    return nPagesTextField;
                }else{
                    return sizeRadio;
                }
            }        
            else if (aComponent.equals(nPagesTextField)){
                return sizeRadio;
            }        
            else if (aComponent.equals(sizeRadio)){
                if (splitSizeCombo.isEnabled()){
                    return splitSizeCombo;
                }else{
                    return bookmarksLevel;
                }
            }
            else if (aComponent.equals(splitSizeCombo) || aComponent.getParent().equals(splitSizeCombo)){
                return bookmarksLevel;
            }
            else if (aComponent.equals(bookmarksLevel)){
                if (bLevelCombo.isEnabled()){
                    return bLevelCombo.getLevelCombo();
                }else{
                    return sameAsSourceRadio;
                }
            }
            else if (aComponent.equals(bLevelCombo.getLevelCombo()) || aComponent.getParent().equals(bLevelCombo.getLevelCombo())){
            	if (bLevelCombo.getFillCombo().isEnabled()){
                    return bLevelCombo.getFillCombo();
                }else{
                    return sameAsSourceRadio;
                }
            }
            else if (aComponent.equals(bLevelCombo.getFillCombo())){
                return sameAsSourceRadio;
            }
            else if (aComponent.equals(sameAsSourceRadio)){
                return chooseAFolderRadio;
            }
            else if (aComponent.equals(chooseAFolderRadio)){
                if (destinationFolderText.isEnabled()){
                    return destinationFolderText;
                }else{
                    return overwriteCheckbox;
                }                
            }
            else if (aComponent.equals(destinationFolderText)){
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
                return outPrefixText;
            }
            else if (aComponent.equals(outPrefixText)){
                return runButton;
            }
            else if (aComponent.equals(runButton)){
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(selectionPanel.getAddFileButton())){
                return runButton;
            }
            else if (aComponent.equals(runButton)){
                return outPrefixText;
            }
            else if (aComponent.equals(outPrefixText)){
                return versionCombo;
            }
            else if (aComponent.equals(versionCombo)){
                return outputCompressedCheck;
            }
            else if (aComponent.equals(outputCompressedCheck)){
                return overwriteCheckbox;
            }
            else if (aComponent.equals(overwriteCheckbox)){
                if (browseDestButton.isEnabled()){
                    return browseDestButton;
                }else{
                    return chooseAFolderRadio;
                }                
            }
            else if (aComponent.equals(browseDestButton)){
                return destinationFolderText;
            }
            else if (aComponent.equals(destinationFolderText)){
                return chooseAFolderRadio;
            }
            else if (aComponent.equals(chooseAFolderRadio)){
                return sameAsSourceRadio;
            }        
            else if (aComponent.equals(sameAsSourceRadio)){
                if (bLevelCombo.isEnabled()){
                	if(bLevelCombo.getFillCombo().isEnabled()){
                		return bLevelCombo.getFillCombo();
                	}else{
                		return bLevelCombo.getLevelCombo();
                	}                    
                }else{
                    return bookmarksLevel;
                }
            }
            else if (aComponent.equals(bLevelCombo.getLevelCombo()) || aComponent.getParent().equals(bLevelCombo.getLevelCombo())){
                return bookmarksLevel;
            }
            else if (aComponent.equals(bLevelCombo.getFillCombo())){
                return bLevelCombo.getLevelCombo();
            }  
            else if (aComponent.equals(bookmarksLevel)){
                if (splitSizeCombo.isEnabled()){
                    return splitSizeCombo;
                }else{
                    return sizeRadio;
                }
            }
            else if (aComponent.equals(splitSizeCombo) || aComponent.getParent().equals(splitSizeCombo)){
                return sizeRadio;
            }
            else if (aComponent.equals(sizeRadio)){
                if (nPagesTextField.isEnabled()){
                    return nPagesTextField;
                }else{
                    return everyNRadio;
                }
            }
            else if (aComponent.equals(nPagesTextField)){
                return everyNRadio;
            }
            else if (aComponent.equals(everyNRadio)){
                if (thisPageTextField.isEnabled()){
                    return thisPageTextField;
                }else{
                    return thisPageRadio;
                }
            }            
            else if (aComponent.equals(thisPageTextField)){
                return thisPageRadio;
            }
            else if (aComponent.equals(thisPageRadio)){
                return oddRadio;
            }
            else if (aComponent.equals(oddRadio)){
                return evenRadio;
            }           
            else if (aComponent.equals(evenRadio)){
                return burstRadio;
            }
            else if (aComponent.equals(burstRadio)){
                return selectionPanel.getRemoveFileButton();
            }
            else if (aComponent.equals(selectionPanel.getRemoveFileButton())){
                return selectionPanel.getAddFileButton();
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
 
    public void resetPanel() {
		((AbstractPdfSelectionTableModel)selectionPanel.getMainTable().getModel()).clearData();	
		destinationFolderText.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(false);
		overwriteCheckbox.setSelected(false);
		thisPageTextField.setText("");
	    nPagesTextField.setText("");
	    outPrefixText.setText("pdfsam_");
	    bLevelCombo.resetComponent();
	    ButtonModel bmodel=splitOptionsRadioGroup.getSelection();
	    if(bmodel != null){
	    	bmodel.setSelected(false);
	    }	   
	    chooseAFolderRadio.setSelected(true);
	}

	/**
	 * @return the outPrefixText
	 */
	public JTextField getOutPrefixText() {
		return outPrefixText;
	}

	/**
	 * @return the destinationFolderText
	 */
	public JTextField getDestinationFolderText() {
		return destinationFolderText;
	}

	/**
	 * @return the thisPageTextField
	 */
	public JTextField getThisPageTextField() {
		return thisPageTextField;
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
	public JPdfSelectionPanel getSelectionPanel() {
		return selectionPanel;
	}

	/**
	 * @return the splitSizeCombo
	 */
	public JSplitSizeCombo getSplitSizeCombo() {
		return splitSizeCombo;
	}

	/**
	 * @return the bLevelCombo
	 */
	public JBLevelCombo getbLevelCombo() {
		return bLevelCombo;
	}

	/**
	 * @return the bookmarksLevel
	 */
	public JSplitRadioButton getBookmarksLevel() {
		return bookmarksLevel;
	}

	/**
	 * @return the overwriteCheckbox
	 */
	public JCheckBox getOverwriteCheckbox() {
		return overwriteCheckbox;
	}

	/**
	 * @return the splitType
	 */
	public String getSplitType() {
		return splitType;
	}

	/**
	 * @return the sameAsSourceRadio
	 */
	public JRadioButton getSameAsSourceRadio() {
		return sameAsSourceRadio;
	}

	/**
	 * @return the outputCompressedCheck
	 */
	public JCheckBox getOutputCompressedCheck() {
		return outputCompressedCheck;
	}

	/**
	 * @return the nPagesTextField
	 */
	public JTextField getnPagesTextField() {
		return nPagesTextField;
	}
    
    
}
