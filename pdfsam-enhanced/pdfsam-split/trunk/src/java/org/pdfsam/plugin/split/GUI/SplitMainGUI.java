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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
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
import org.pdfsam.plugin.split.components.JSplitRadioButton;
import org.pdfsam.plugin.split.components.JSplitRadioButtonModel;
import org.pdfsam.plugin.split.components.JSplitSizeCombo;
import org.pdfsam.plugin.split.listeners.EnterDoClickListener;
import org.pdfsam.plugin.split.listeners.RadioListener;
/**
 * Plugable JPanel provides a GUI for split functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class SplitMainGUI  extends AbstractPlugablePanel{
    
	private static final long serialVersionUID = -5907189950338614835L;

	private static final Logger log = Logger.getLogger(SplitMainGUI.class.getPackage().getName());
	
	private JTextField outPrefixText;
    private SpringLayout outputPanelLayout;
    private SpringLayout destinationPanelLayout;
    private JTextField destinationFolderText;
    private JTextField thisPageTextField;
    private JTextField nPagesTextField;
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
    
//file_chooser    
    private final JFileChooser browseDestFileChooser = new JFileChooser();

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

//labels    
    private final JLabel splitOptionsLabel = new JLabel();
    private final JLabel destFolderLabel = new JLabel();
    private final JLabel outputOptionsLabel = new JLabel();
    private final JLabel outPrefixLabel = new JLabel();
	private final JLabel outputVersionLabel = new JLabel();	
  
    private final ThreadGroup runThreads = new ThreadGroup("run threads");
   
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_VERSION = "0.4.1";
    
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
//        
        splitSpringLayout = new SpringLayout();
        setLayout(splitSpringLayout);
        add(selectionPanel);
        
        browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//SPLIT_SECTION
        optionsPaneLayout = new SpringLayout();
        splitOptionsPanel.setLayout(optionsPaneLayout);
        splitOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(splitOptionsPanel);

        burstRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Burst (split into single pages)"));
        splitOptionsPanel.add(burstRadio);

        everyNRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split every \"n\" pages"));
        splitOptionsPanel.add(everyNRadio);
        
        nPagesTextField = new JTextField();
        nPagesTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        nPagesTextField.setEnabled(false);
        splitOptionsPanel.add(nPagesTextField);        
        
        evenRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split even pages"));      
        splitOptionsPanel.add(evenRadio);
        
        oddRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split odd pages"));
        splitOptionsPanel.add(oddRadio);
        
        thisPageRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split after these pages"));
        splitOptionsPanel.add(thisPageRadio);
        
        sizeRadio.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split at this size"));
        splitOptionsPanel.add(sizeRadio);
        
        thisPageTextField = new JTextField();
        thisPageTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        thisPageTextField.setEnabled(false);
        splitOptionsPanel.add(thisPageTextField);
        
        splitSizeCombo.setEnabled(false);
        splitOptionsPanel.add(splitSizeCombo);
        
        String helpText = 
        		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split options")+"</b><ul>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Burst")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Explode the pdf document into single pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split every \"n\" pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every \"n\" pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split even pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every even page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split odd pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document every odd page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split after these pages")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document after page numbers (num1-num2-num3..)")+".</li>" +
        		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Split at this size")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Split the document in files of the given size (roughly).")+".</li>" +
        		"</ul></body></html>";
        checksHelpLabel = new JHelpLabel(helpText, true);
        splitOptionsPanel.add(checksHelpLabel);        
//END_SPLIT_SECTION
        
        
        splitOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Split options:"));
        add(splitOptionsLabel);
//RADIO_LISTENERS
        /*This listeners enable or disable text field based on what you select*/
        radioListener = new RadioListener(this, nPagesTextField, thisPageTextField, splitSizeCombo); 
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
//END_RADIO_LISTENERS 
//DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL        
//DESTINATION_RADIOS
        
        destFolderLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder:"));
        add(destFolderLabel);

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
        final ButtonGroup outputRadioGroup = new ButtonGroup();
        outputRadioGroup.add(sameAsSourceRadio);
        outputRadioGroup.add(chooseAFolderRadio);  
//END_RADIOGROUP

        destinationFolderText = new JTextField();
        destinationFolderText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destinationPanel.add(destinationFolderText);
        destinationPanel.add(overwriteCheckbox);
        
        destinationPanel.add(outputCompressedCheck); 
		
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        
        destinationPanel.add(versionCombo);
        
        outputVersionLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output document pdf version:"));
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
	    
        outputOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output options:"));
        add(outputOptionsLabel);
//S_PANEL
        outputOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        outputPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(outputPanelLayout);
        add(outputOptionsPanel);

        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);

        outPrefixText = new JTextField();
        outPrefixText.setText("pdfsam_");
        outPrefixText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        outputOptionsPanel.add(outPrefixText);
//END_S_PANEL
//HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it contains \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Ex. prefix_[BASENAME]_[CURRENTPAGE] generates prefix_FileName_005.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it doesn't contain \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Available variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME].")+"</p>"+
    		"</body></html>";
	    prefixHelpLabel = new JHelpLabel(helpTextPrefix, true);
	    outputOptionsPanel.add(prefixHelpLabel);
//END_HELP_LABEL_PREFIX
//RUN_BUTTON
        //listener
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (runThreads.activeCount() > 0 || selectionPanel.isAdding()){
                    log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Please wait while all files are processed.."));
                    return;
                }
                final LinkedList args = new LinkedList();
                try{
                	PdfSelectionTableItem item = null;
                	PdfSelectionTableItem[] items = selectionPanel.getTableRows();
					if(items != null && items.length == 1){
						item = items[0];
						args.add("-"+SplitParsedCommand.F_ARG);
						String f = item.getInputFile().getAbsolutePath();
						if((item.getPassword()) != null && (item.getPassword()).length()>0){
							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
							f +=":"+item.getPassword();
						}
						args.add(f);
					}else{
						throw new Exception(GettextResource.gettext(config.getI18nResourceBundle(),"Select an input file"));
					}
                    args.add("-"+SplitParsedCommand.P_ARG);
                    args.add(outPrefixText.getText());
                    args.add("-"+SplitParsedCommand.S_ARG);
                    args.add(splitType);
                    //check if is needed page option
                    if (splitType.equals(SplitParsedCommand.S_SPLIT)){
                        args.add("-"+SplitParsedCommand.N_ARG);
                        args.add(thisPageTextField.getText());                        
                    }else if (splitType.equals(SplitParsedCommand.S_NSPLIT)){
                            args.add("-"+SplitParsedCommand.N_ARG);
                            args.add(nPagesTextField.getText());                        
                    }else if (splitType.equals(SplitParsedCommand.S_SIZE)){
                        args.add("-"+SplitParsedCommand.B_ARG);
                        if(splitSizeCombo.isSelectedItem() && splitSizeCombo.isValidSelectedItem()){
                        	args.add(Long.toString(splitSizeCombo.getSelectedBytes()));                        
                        }else{
                        	throw new Exception(GettextResource.gettext(config.getI18nResourceBundle(),"Invalid split size"));
                        }
                    }                      
                                        
                    args.add("-"+SplitParsedCommand.O_ARG);
                    //check radio for output options
                    if (sameAsSourceRadio.isSelected()){
                    	if(item != null){
                    		args.add(item.getInputFile().getParent());
                    	}
                    }else{
                        args.add(destinationFolderText.getText());
                    }
                    if (overwriteCheckbox.isSelected()) args.add("-"+SplitParsedCommand.OVERWRITE_ARG);
                    if (outputCompressedCheck.isSelected()) args.add("-"+SplitParsedCommand.COMPRESSED_ARG); 
                    args.add("-"+MixParsedCommand.PDFVERSION_ARG);
                    if(JPdfVersionCombo.SAME_AS_SOURCE.equals(((StringItem)versionCombo.getSelectedItem()).getId())){
                    	args.add(Character.toString(item.getPdfVersion()));
                    }else{
                    	args.add(((StringItem)versionCombo.getSelectedItem()).getId());
                    }
                    args.add(AbstractParsedCommand.COMMAND_SPLIT); 
                    
		        	final String[] myStringArray = (String[])args.toArray(new String[args.size()]);
		            //run split in its own thread              
		            final Thread runThread = new Thread(runThreads, "run") {
		                 public void run() {
		                	 try{
								AbstractParsedCommand cmd = config.getConsoleServicesFacade().parseAndValidate(myStringArray);
								if(cmd != null){
									config.getConsoleServicesFacade().execute(cmd);							
								}else{
									log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Command validation returned an empty value."));
								}
								log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Command executed."));
							}catch(Exception ex){    
								log.error("Command Line: "+args.toString(), ex);
							}                            
		                 }
		            };
		            runThread.start();   
                }catch(Exception ex){    
                	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
                }      
            }
        });
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Split selected file"));
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

				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationFolderText.getText());			
				
				Element filePrefix = ((Element)arg0).addElement("prefix");
				filePrefix.addAttribute("value", outPrefixText.getText());	
				
				Element file_overwrite = ((Element)arg0).addElement("overwrite");
				file_overwrite.addAttribute("value", overwriteCheckbox.isSelected()?"true":"false");

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
    	if(arg0!=null){
			try{	
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
				
				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null){
					destinationFolderText.setText(fileDestination.getText());
					chooseAFolderRadio.doClick();
				}else{
					sameAsSourceRadio.doClick();
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
		splitSpringLayout.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
		splitSpringLayout.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
		splitSpringLayout.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);
//      LAYOUT
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, splitOptionsPanel, 103, SpringLayout.NORTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, splitOptionsPanel, -5, SpringLayout.EAST, this);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, splitOptionsPanel, 20, SpringLayout.SOUTH, selectionPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, splitOptionsPanel, 0, SpringLayout.WEST, selectionPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, splitOptionsLabel, 0, SpringLayout.NORTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, splitOptionsLabel, 5, SpringLayout.SOUTH, selectionPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, splitOptionsLabel, 0, SpringLayout.WEST, selectionPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.NORTH, destFolderLabel, 5, SpringLayout.SOUTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, destFolderLabel, 0, SpringLayout.WEST, splitOptionsPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 130, SpringLayout.NORTH, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, splitOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, splitOptionsPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, sameAsSourceRadio, 25, SpringLayout.NORTH, sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, sameAsSourceRadio, 1, SpringLayout.NORTH, destinationPanel);
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
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destinationFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destinationFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsLabel, 0, SpringLayout.EAST, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsLabel, 0, SpringLayout.WEST, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsLabel, 5, SpringLayout.SOUTH, destinationPanel);
		
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, outputOptionsPanel, 48, SpringLayout.NORTH, outputOptionsPanel);
        splitSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsPanel, 0, SpringLayout.EAST, destinationPanel);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsPanel, 0, SpringLayout.SOUTH, outputOptionsLabel);
        splitSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsPanel, 0, SpringLayout.WEST, outputOptionsLabel);
        outputPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 25, SpringLayout.NORTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.EAST, outPrefixText, -10, SpringLayout.EAST, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixText, 0, SpringLayout.SOUTH, outPrefixLabel);
        outputPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixText, 0, SpringLayout.NORTH, outPrefixLabel);
        outputPanelLayout.putConstraint(SpringLayout.WEST, outPrefixText, 15, SpringLayout.EAST, outPrefixLabel);

        outputPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        outputPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
        
        splitSpringLayout.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        splitSpringLayout.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        splitSpringLayout.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        splitSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, outputOptionsPanel);


//      RADIO_LAYOUT
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, burstRadio, 10, SpringLayout.NORTH, splitOptionsPanel);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, burstRadio, 10, SpringLayout.WEST, splitOptionsPanel);
                
        optionsPaneLayout.putConstraint(SpringLayout.WEST, evenRadio, 0, SpringLayout.WEST, burstRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, evenRadio, 2, SpringLayout.SOUTH, burstRadio);
        
        optionsPaneLayout.putConstraint(SpringLayout.WEST, oddRadio, 0, SpringLayout.WEST, evenRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, oddRadio, 2, SpringLayout.SOUTH, evenRadio);
        
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, thisPageRadio, 0, SpringLayout.NORTH, burstRadio);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, thisPageRadio, 2, SpringLayout.EAST, burstRadio);
        optionsPaneLayout.putConstraint(SpringLayout.SOUTH, thisPageTextField, 17, SpringLayout.NORTH, thisPageTextField);
        optionsPaneLayout.putConstraint(SpringLayout.EAST, thisPageTextField, 75, SpringLayout.EAST, thisPageRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, thisPageTextField, 0, SpringLayout.NORTH, thisPageRadio);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, thisPageTextField, 5, SpringLayout.EAST, thisPageRadio);
        
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, everyNRadio, 2, SpringLayout.SOUTH, thisPageRadio);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, everyNRadio, 2, SpringLayout.EAST, evenRadio);
        
        optionsPaneLayout.putConstraint(SpringLayout.SOUTH, nPagesTextField, 17, SpringLayout.NORTH, everyNRadio);
        optionsPaneLayout.putConstraint(SpringLayout.EAST, nPagesTextField, 45, SpringLayout.EAST, everyNRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, nPagesTextField, 0, SpringLayout.NORTH, everyNRadio);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, nPagesTextField, 5, SpringLayout.EAST, everyNRadio);

        optionsPaneLayout.putConstraint(SpringLayout.WEST, sizeRadio, 2, SpringLayout.EAST, oddRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, sizeRadio, 2, SpringLayout.SOUTH, everyNRadio);
        optionsPaneLayout.putConstraint(SpringLayout.SOUTH, splitSizeCombo, 17, SpringLayout.NORTH, splitSizeCombo);
        optionsPaneLayout.putConstraint(SpringLayout.EAST, splitSizeCombo, 100, SpringLayout.EAST, sizeRadio);
        optionsPaneLayout.putConstraint(SpringLayout.NORTH, splitSizeCombo, 0, SpringLayout.NORTH, sizeRadio);
        optionsPaneLayout.putConstraint(SpringLayout.WEST, splitSizeCombo, 5, SpringLayout.EAST, sizeRadio);
        
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
                    return sameAsSourceRadio;
                }
            }
            else if (aComponent.equals(splitSizeCombo) || aComponent.equals(splitSizeCombo.getEditor())){
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
                if (splitSizeCombo.isEnabled()){
                    return splitSizeCombo;
                }else{
                    return sizeRadio;
                }
            }
            else if (aComponent.equals(splitSizeCombo) || aComponent.equals(splitSizeCombo.getEditor())){
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
 
   
}
