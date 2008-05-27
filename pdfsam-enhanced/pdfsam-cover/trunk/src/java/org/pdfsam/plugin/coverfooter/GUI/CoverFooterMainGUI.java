/*
 * Created on 06-Feb-2006
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


package org.pdfsam.plugin.coverfooter.GUI;

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
import javax.swing.border.EtchedBorder;
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
/**
 * Plugable JPanel provides a GUI for merge functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class CoverFooterMainGUI extends AbstractPlugablePanel implements PropertyChangeListener{

	private static final long serialVersionUID = -992513717368544272L;

	private static final Logger log = Logger.getLogger(CoverFooterMainGUI.class.getPackage().getName());

    private JTextField destinationTextField;
    private SpringLayout layoutMergePanel;
    private JFileChooser browseDestFileChooser;
    private SpringLayout layoutDestinationPanel;
    private JPanel destinationPanel = new JPanel();
    private SpringLayout layoutOptionPanel;
    private JPanel optionPanel = new JPanel();    
    private JHelpLabel mergeTypeHelpLabel;
    private JHelpLabel destinationHelpLabel;
    private Configuration config;
    private JCheckBox mergeTypeCheck = new JCheckBox();
    private final JLabel optionLabel = new JLabel();
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, SimplePdfSelectionTableModel.MAX_COLUMNS_NUMBER, true, false);
	private JPdfSelectionPanel coverSelectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.SINGLE_SELECTABLE_FILE, SimplePdfSelectionTableModel.MAX_COLUMNS_NUMBER);
	private JPdfSelectionPanel footerSelectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.SINGLE_SELECTABLE_FILE, SimplePdfSelectionTableModel.MAX_COLUMNS_NUMBER);
	private JLabel coverLabel = new JLabel();
	private JLabel footerLabel = new JLabel();

	//button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
    
  //checks
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
    
//keylisteners
    private final EnterDoClickListener runEnterKeyListener = new EnterDoClickListener(runButton);
    private final EnterDoClickListener browseEnterKeyListener = new EnterDoClickListener(browseDestButton);

//focus policy 
    private final CoverFooterFocusPolicy coverFooterFocusPolicy = new CoverFooterFocusPolicy();
    private final JLabel destinationLabel = new JLabel();
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.2.3e";
	private static final String ALL_STRING = "All";
	
    /**
     * Constructor
     */
    public CoverFooterMainGUI() {
        super();          
        initialize();
              
    }
    
    /**
     * Panel initialization   
     */
    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/cover_footer.png");
        setPreferredSize(new Dimension(500,750));

        layoutMergePanel = new SpringLayout();
        setLayout(layoutMergePanel);
        add(coverSelectionPanel);
        coverSelectionPanel.addPropertyChangeListener(this);
        add(footerSelectionPanel);
        coverLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Cover pdf file:"));
		add(coverLabel);
		
        footerSelectionPanel.addPropertyChangeListener(this);
        add(selectionPanel);
        footerLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Footer pdf file:"));
		add(footerLabel);
        selectionPanel.addPropertyChangeListener(this);

//BROWSE_FILE_CHOOSER        
        browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
        browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//END_BROWSE_FILE_CHOOSER        

//      OPTION_PANEL
        layoutOptionPanel = new SpringLayout();
        optionPanel.setLayout(layoutOptionPanel);
        optionPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(optionPanel);
        
//END_OPTION_PANEL 
        optionLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Merge options:"));
        add(optionLabel);

        mergeTypeCheck.setText(GettextResource.gettext(config.getI18nResourceBundle(),"PDF documents contain forms"));
        mergeTypeCheck.setSelected(false);
        optionPanel.add(mergeTypeCheck);     
        
        String helpText = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Merge type")+"</b><ul>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Unchecked")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Use this merge type for standard pdf documents")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Checked")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Use this merge type for pdf documents containing forms")+"." +
    		"<br><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Note")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Setting this option the documents will be completely loaded in memory")+".</li>" +
    		"</ul></body></html>";
	    mergeTypeHelpLabel = new JHelpLabel(helpText, true);
	    optionPanel.add(mergeTypeHelpLabel);       
//DESTINATION_PANEL
        layoutDestinationPanel = new SpringLayout();
        destinationPanel.setLayout(layoutDestinationPanel);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL   
        
        destinationTextField = new JTextField();
        destinationTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destinationPanel.add(destinationTextField);
        
        destinationLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder:"));
        add(destinationLabel);
//BROWSE_BUTTON        
        browseDestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int retVal = browseDestFileChooser.showOpenDialog(browseDestButton.getParent());
                File chosenFile = null;                
                if (retVal == JFileChooser.APPROVE_OPTION){
                	chosenFile = browseDestFileChooser.getSelectedFile();
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
        destinationPanel.add(browseDestButton);
//END_BROWSE_BUTTON
//CHECK_BOX
        overwriteCheckbox.setSelected(true);
        destinationPanel.add(overwriteCheckbox);
        
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        destinationPanel.add(outputCompressedCheck);
        
        destinationPanel.add(versionCombo);
        destinationPanel.add(outputVersionLabel);
        
//END_CHECK_BOX  
//HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output file")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output file if it already exists.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files.")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"PDF version 1.5 or above.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
//END_HELP_LABEL_DESTINATION        
//RUN_BUTTON
	    runButton.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
            	if (WorkExecutor.getInstance().getRunningThreads() > 0 || selectionPanel.isAdding()){
                    log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Please wait while all files are processed.."));
                    return;
                }                             
                final LinkedList args = new LinkedList();                
                final LinkedList args1 = new LinkedList();
                final LinkedList argsFooter = new LinkedList();
                //validation and permission check are demanded 
                try{
                	if (overwriteCheckbox.isSelected()) args.add("-"+ConcatParsedCommand.OVERWRITE_ARG);
                    if (outputCompressedCheck.isSelected()) args.add("-"+ConcatParsedCommand.COMPRESSED_ARG); 
                    if (mergeTypeCheck.isSelected()) args.add("-"+ConcatParsedCommand.COPYFIELDS_ARG);

                    args.add("-"+ConcatParsedCommand.PDFVERSION_ARG);
					args.add(((StringItem)versionCombo.getSelectedItem()).getId());
					
					PdfSelectionTableItem[] coveritems = coverSelectionPanel.getTableRows();
					PdfSelectionTableItem[] footeritems = footerSelectionPanel.getTableRows();
					String coverSelectionString = "";
					//manage cover 
					if((coveritems==null || coveritems.length != 1) && (footeritems==null || footeritems.length != 1)){
						throw new Exception(GettextResource.gettext(config.getI18nResourceBundle(),"Select at least one cover or one footer"));
					}else{
						if((coveritems!=null && coveritems.length == 1)) {
							PdfSelectionTableItem coveritem = coveritems[0];
							String coverSelection = (coveritem.getPageSelection()!=null && coveritem.getPageSelection().length()>0)?coveritem.getPageSelection():ALL_STRING;
							if(coverSelection.trim().length()>0 && coverSelection.indexOf(",") != 0){
	                            String[] selectionsArray = coverSelection.split(",");
	                            for(int j = 0; j<selectionsArray.length; j++){
	                                String tmpString = selectionsArray[j].trim();
	                                if((tmpString != null)&&(!tmpString.equals(""))){
		                                args.add("-"+ConcatParsedCommand.F_ARG);
		                                String f = coveritem.getInputFile().getAbsolutePath();
		        						if((coveritem.getPassword()) != null && (coveritem.getPassword()).length()>0){
		        							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
		        							f +=":"+coveritem.getPassword();
		        						}
		        						args.add(f);
		        						coverSelectionString += (tmpString.matches("[\\d]+"))? tmpString+"-"+tmpString+":" : tmpString+":";
	                                }                                
	                            }
	                        
	                        }else{
	                        	args.add("-"+ConcatParsedCommand.F_ARG);
	                            String f = coveritem.getInputFile().getAbsolutePath();
	    						if((coveritem.getPassword()) != null && (coveritem.getPassword()).length()>0){
	    							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
	    							f +=":"+coveritem.getPassword();
	    						}
	    						args.add(f);
	    						coverSelectionString += (coverSelection.matches("[\\d]+"))? coverSelection+"-"+coverSelection+":" : coverSelection+":";
	                        }	
						}					
						String footerSelectionString = "";
						//manage footer
						if((footeritems!=null && footeritems.length == 1)) {
							PdfSelectionTableItem footeritem = footeritems[0];
							String footerSelection = (footeritem.getPageSelection()!=null && footeritem.getPageSelection().length()>0)?footeritem.getPageSelection():ALL_STRING;
							if(footerSelection.trim().length()>0 && footerSelection.indexOf(",") != 0){
	                            String[] selectionsArray = footerSelection.split(",");
	                            for(int j = 0; j<selectionsArray.length; j++){
	                                String tmpString = selectionsArray[j].trim();
	                                if((tmpString != null)&&(!tmpString.equals(""))){
	                                	argsFooter.add("-"+ConcatParsedCommand.F_ARG);
		                                String footerItem = footeritem.getInputFile().getAbsolutePath();
		        						if((footeritem.getPassword()) != null && (footeritem.getPassword()).length()>0){
		        							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
		        							footerItem +=":"+footeritem.getPassword();
		        						}
		        						argsFooter.add(footerItem);
		        						footerSelectionString += (tmpString.matches("[\\d]+"))? tmpString+"-"+tmpString+":" : tmpString+":";
	                                }                                
	                            }
	                        
	                        }else{
	                        	argsFooter.add("-"+ConcatParsedCommand.F_ARG);
	                        	String footerItem = footeritem.getInputFile().getAbsolutePath();
	    						if((footeritem.getPassword()) != null && (footeritem.getPassword()).length()>0){
	    							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
	    							footerItem +=":"+footeritem.getPassword();
	    						}
	    						argsFooter.add(footerItem);
	    						footerSelectionString += (footerSelection.matches("[\\d]+"))? footerSelection+"-"+footerSelection+":" : footerSelection+":";
	                        }		
						}					
						 //selection page 
	                    PdfSelectionTableItem item = null;
	                	PdfSelectionTableItem[] items = selectionPanel.getTableRows();
	                	for (int i = 0; i < items.length; i++){
		                	String pageSelectionString = coverSelectionString;
	                		try{
		                		args1.clear();
		                		args1.addAll(args);
		                		
								item = items[i];
								String pageSelection = (item.getPageSelection()!=null && item.getPageSelection().length()>0)?item.getPageSelection():ALL_STRING;
								if(pageSelection.trim().length()>0 && pageSelection.indexOf(",") != 0){
		                            String[] selectionsArray = pageSelection.split(",");
		                            for(int j = 0; j<selectionsArray.length; j++){
		                                String tmpString = selectionsArray[j].trim();
		                                if((tmpString != null)&&(!tmpString.equals(""))){
		                                	args1.add("-"+ConcatParsedCommand.F_ARG);
			                                String f = item.getInputFile().getAbsolutePath();
			        						if((item.getPassword()) != null && (item.getPassword()).length()>0){
			        							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
			        							f +=":"+item.getPassword();
			        						}
			        						args1.add(f);
			                                pageSelectionString += (tmpString.matches("[\\d]+"))? tmpString+"-"+tmpString+":" : tmpString+":";
		                                }                                
		                            }
		                        
		                        }else{
		                        	args1.add("-"+ConcatParsedCommand.F_ARG);
		                            String f = item.getInputFile().getAbsolutePath();
		    						if((item.getPassword()) != null && (item.getPassword()).length()>0){
		    							log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Found a password for input file."));
		    							f +=":"+item.getPassword();
		    						}
		    						args1.add(f);
		                            pageSelectionString += (pageSelection.matches("[\\d]+"))? pageSelection+"-"+pageSelection+":" : pageSelection+":";
		                        }
									                         
	                            args1.addAll(argsFooter);
	                            args1.add("-"+ConcatParsedCommand.U_ARG);
	                            args1.add(pageSelectionString+footerSelectionString);
	                            
								//manage output destination option
								args1.add("-"+ConcatParsedCommand.O_ARG);
							   if(destinationTextField.getText()==null || destinationTextField.getText().length()==0){                    
			                		String suggestedDir = Configuration.getInstance().getDefaultWorkingDir();                    		
			                		if(suggestedDir != null){
			                			if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getParent(),
			        						    GettextResource.gettext(config.getI18nResourceBundle(),"Output location is not correct")+".\n"+GettextResource.gettext(config.getI18nResourceBundle(),"Would you like to change it to")+" "+suggestedDir+" ?",
			        						    GettextResource.gettext(config.getI18nResourceBundle(),"Output location error"),
			        						    JOptionPane.YES_NO_OPTION,
		            						    JOptionPane.QUESTION_MESSAGE)){
			                				destinationTextField.setText(suggestedDir);
					        			}
			                		}                    	
			                    }
								if (destinationTextField.getText().length() > 0){
									args1.add(destinationTextField.getText()+File.separator+item.getInputFile().getName());
								}
								
								args1.add (AbstractParsedCommand.COMMAND_CONCAT);
								
				                WorkExecutor.getInstance().execute(new WorkThread((String[])args1.toArray(new String[args1.size()])));
	                		}catch(Exception ex){    
	                        	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
	                        } 
						}					
                	}					
                }catch(Exception ex){    
                	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);
                }    
            }
        });
	    runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Execute pdf merge"));
        add(runButton);
//END_RUN_BUTTON
//KEY_LISTENER
        runButton.addKeyListener(runEnterKeyListener);
        browseDestButton.addKeyListener(browseEnterKeyListener);
        
        destinationTextField.addKeyListener(runEnterKeyListener);

//LAYOUT
        setLayout();

//END_LAYOUT
    }
  	
   
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
    	layoutMergePanel.putConstraint(SpringLayout.SOUTH, coverLabel, 10, SpringLayout.NORTH, coverLabel);
    	layoutMergePanel.putConstraint(SpringLayout.EAST, coverLabel, -5, SpringLayout.EAST, this);
		layoutMergePanel.putConstraint(SpringLayout.NORTH, coverLabel, 2, SpringLayout.NORTH, this);
		layoutMergePanel.putConstraint(SpringLayout.WEST, coverLabel, 5, SpringLayout.WEST, this);
		
    	layoutMergePanel.putConstraint(SpringLayout.SOUTH, coverSelectionPanel, 100, SpringLayout.NORTH, coverSelectionPanel);
    	layoutMergePanel.putConstraint(SpringLayout.EAST, coverSelectionPanel, -5, SpringLayout.EAST, this);
		layoutMergePanel.putConstraint(SpringLayout.NORTH, coverSelectionPanel, 0, SpringLayout.SOUTH, coverLabel);
		layoutMergePanel.putConstraint(SpringLayout.WEST, coverSelectionPanel, 5, SpringLayout.WEST, this);

		layoutMergePanel.putConstraint(SpringLayout.SOUTH, footerLabel, 10, SpringLayout.NORTH, footerLabel);
    	layoutMergePanel.putConstraint(SpringLayout.EAST, footerLabel, -5, SpringLayout.EAST, this);
		layoutMergePanel.putConstraint(SpringLayout.NORTH, footerLabel, 0, SpringLayout.SOUTH, coverSelectionPanel);
		layoutMergePanel.putConstraint(SpringLayout.WEST, footerLabel, 5, SpringLayout.WEST, this);

		layoutMergePanel.putConstraint(SpringLayout.SOUTH, footerSelectionPanel, 100, SpringLayout.NORTH, footerSelectionPanel);
    	layoutMergePanel.putConstraint(SpringLayout.EAST, footerSelectionPanel, -5, SpringLayout.EAST, this);
		layoutMergePanel.putConstraint(SpringLayout.NORTH, footerSelectionPanel, 0, SpringLayout.SOUTH, footerLabel);
		layoutMergePanel.putConstraint(SpringLayout.WEST, footerSelectionPanel, 5, SpringLayout.WEST, this);

    	layoutMergePanel.putConstraint(SpringLayout.SOUTH, selectionPanel, 200, SpringLayout.NORTH, selectionPanel);
    	layoutMergePanel.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
		layoutMergePanel.putConstraint(SpringLayout.NORTH, selectionPanel, 0, SpringLayout.SOUTH, footerSelectionPanel);
		layoutMergePanel.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);
        
        layoutMergePanel.putConstraint(SpringLayout.SOUTH, optionPanel, 50, SpringLayout.NORTH, optionPanel);
        layoutMergePanel.putConstraint(SpringLayout.EAST, optionPanel, -5, SpringLayout.EAST, this);
        layoutMergePanel.putConstraint(SpringLayout.NORTH, optionPanel, 20, SpringLayout.SOUTH, selectionPanel);
        layoutMergePanel.putConstraint(SpringLayout.WEST, optionPanel, 0, SpringLayout.WEST, selectionPanel);

        layoutMergePanel.putConstraint(SpringLayout.SOUTH, optionLabel, 0, SpringLayout.NORTH, optionPanel);
        layoutMergePanel.putConstraint(SpringLayout.WEST, optionLabel, 0, SpringLayout.WEST, optionPanel);

        layoutOptionPanel.putConstraint(SpringLayout.SOUTH, mergeTypeCheck, 30, SpringLayout.NORTH, optionPanel);
        layoutOptionPanel.putConstraint(SpringLayout.EAST, mergeTypeCheck, -40, SpringLayout.EAST, optionPanel);
        layoutOptionPanel.putConstraint(SpringLayout.NORTH, mergeTypeCheck, 5, SpringLayout.NORTH, optionPanel);
        layoutOptionPanel.putConstraint(SpringLayout.WEST, mergeTypeCheck, 5, SpringLayout.WEST, optionPanel);

        layoutOptionPanel.putConstraint(SpringLayout.SOUTH, mergeTypeHelpLabel, -1, SpringLayout.SOUTH, optionPanel);
        layoutOptionPanel.putConstraint(SpringLayout.EAST, mergeTypeHelpLabel, -1, SpringLayout.EAST, optionPanel);

        layoutMergePanel.putConstraint(SpringLayout.SOUTH, destinationPanel, 120, SpringLayout.NORTH, destinationPanel);
        layoutMergePanel.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, optionPanel);
        layoutMergePanel.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, optionPanel);
        layoutMergePanel.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, selectionPanel);
        
        layoutDestinationPanel.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST, destinationPanel);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, destinationTextField, 10, SpringLayout.NORTH, destinationPanel);
        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, destinationTextField, 30, SpringLayout.NORTH, destinationPanel);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, destinationTextField, 5, SpringLayout.WEST, destinationPanel);

        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destinationTextField);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destinationTextField);

		layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destinationTextField);

        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destinationTextField);
        
        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);

        layoutMergePanel.putConstraint(SpringLayout.SOUTH, destinationLabel, 0, SpringLayout.NORTH, destinationPanel);
        layoutMergePanel.putConstraint(SpringLayout.WEST, destinationLabel, 0, SpringLayout.WEST, destinationPanel);
        
        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, browseDestButton, 25, SpringLayout.NORTH, browseDestButton);
        layoutDestinationPanel.putConstraint(SpringLayout.EAST, browseDestButton, -5, SpringLayout.EAST, destinationPanel);
        layoutDestinationPanel.putConstraint(SpringLayout.NORTH, browseDestButton, 0, SpringLayout.NORTH, destinationTextField);
        layoutDestinationPanel.putConstraint(SpringLayout.WEST, browseDestButton, -93, SpringLayout.EAST, destinationPanel);        
        
        layoutDestinationPanel.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        layoutDestinationPanel.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);        
        
        layoutMergePanel.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        layoutMergePanel.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        layoutMergePanel.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        layoutMergePanel.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, destinationPanel);

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
        return GettextResource.gettext(config.getI18nResourceBundle(),"Cover and Footer");
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
        return (FocusTraversalPolicy)coverFooterFocusPolicy;
        
    }

    public Node getJobNode(Node arg0, boolean savePasswords) throws SaveJobException {
		try{
			if (arg0 != null){
				Element coverSource = ((Element)arg0).addElement("cover");
				PdfSelectionTableItem[] coverItems = coverSelectionPanel.getTableRows();
				if(coverItems != null && coverItems.length>0){
					coverSource.addAttribute("value",coverItems[0].getInputFile().getAbsolutePath());
					coverSource.addAttribute("pageselection",(coverItems[0].getPageSelection()!=null)?coverItems[0].getPageSelection():ALL_STRING);
					if(savePasswords){
						coverSource.addAttribute("password",coverItems[0].getPassword());
					}
				}
				
				Element footerSource = ((Element)arg0).addElement("footer");
				PdfSelectionTableItem[] footerItems = coverSelectionPanel.getTableRows();
				if(coverItems != null && coverItems.length>0){
					footerSource.addAttribute("value",footerItems[0].getInputFile().getAbsolutePath());
					footerSource.addAttribute("pageselection",(footerItems[0].getPageSelection()!=null)?footerItems[0].getPageSelection():ALL_STRING);
					if(savePasswords){
						footerSource.addAttribute("password",footerItems[0].getPassword());
					}
				}
				
				Element filelist = ((Element)arg0).addElement("filelist");
				PdfSelectionTableItem[] items = selectionPanel.getTableRows();        
				for (int i = 0; i < items.length; i++){
					Element fileNode = ((Element)filelist).addElement("file");
					fileNode.addAttribute("name",items[i].getInputFile().getAbsolutePath());
					fileNode.addAttribute("pageselection",(items[i].getPageSelection()!=null)?items[i].getPageSelection():ALL_STRING);
					if(savePasswords){
						fileNode.addAttribute("password",items[i].getPassword());
					}
				}
				
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destinationTextField.getText());			
				
				Element fileOverwrite = ((Element)arg0).addElement("overwrite");
				fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected()?TRUE:FALSE);

				Element mergeType = ((Element)arg0).addElement("merge_type");
				mergeType.addAttribute("value", mergeTypeCheck.isSelected()?TRUE:FALSE);

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
				Node coverSource = (Node) arg0.selectSingleNode("cover/@value");
				if (coverSource != null && coverSource.getText().length()>0){
					Node coverPageSelection = (Node) arg0.selectSingleNode("cover/@pageselection");
					Node coverFilePwd = (Node) arg0.selectSingleNode("cover/@password");
					coverSelectionPanel.getLoader().addFile(new File(coverSource.getText()), (coverFilePwd!=null)?coverFilePwd.getText():null, (coverPageSelection!=null)?coverPageSelection.getText():null);
				}
				
				footerSelectionPanel.getClearButton().doClick();
				Node footerSource = (Node) arg0.selectSingleNode("cover/@value");
				if (footerSource != null && footerSource.getText().length()>0){
					Node footerPageSelection = (Node) arg0.selectSingleNode("cover/@pageselection");
					Node footerFilePwd = (Node) arg0.selectSingleNode("cover/@password");
					footerSelectionPanel.getLoader().addFile(new File(footerSource.getText()), (footerFilePwd!=null)?footerFilePwd.getText():null, (footerPageSelection!=null)?footerPageSelection.getText():null);
				}

				Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
				if (fileDestination != null){
					destinationTextField.setText(fileDestination.getText());
				}
				
				Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
				if (fileOverwrite != null){
					overwriteCheckbox.setSelected(TRUE.equals(fileOverwrite.getText()));
				}
				
				Node mergeType = (Node) arg0.selectSingleNode("merge_type/@value");
				if (mergeType != null){
					mergeTypeCheck.setSelected(TRUE.equals(mergeType.getText()));
				}

				selectionPanel.getClearButton().doClick();
				List fileList = arg0.selectNodes("filelist/file");
				for (int i = 0; fileList != null && i < fileList.size(); i++) {
					Node fileNode = (Node) fileList.get(i);
					if(fileNode != null){
						Node fileName = (Node) fileNode.selectSingleNode("@name");
						if (fileName != null && fileName.getText().length()>0){
							Node pageSelection = (Node) fileNode.selectSingleNode("@pageselection");
							Node filePwd = (Node) fileNode.selectSingleNode("@password");	
							selectionPanel.getLoader().addFile(new File(fileName.getText()), (filePwd!=null)?filePwd.getText():null, (pageSelection!=null)?pageSelection.getText():null);							
						}
					}
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
                log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Cover And Footer section loaded."));  
            }
			catch (Exception ex){
				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                             
			}
		} 	
    }			
	     
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for merge panel
     *
     */
    public class CoverFooterFocusPolicy extends FocusTraversalPolicy {
        public CoverFooterFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){ 
        	if (aComponent.equals(coverSelectionPanel.getAddFileButton())){
				return coverSelectionPanel.getRemoveFileButton();
			}
			else if (aComponent.equals(coverSelectionPanel.getRemoveFileButton())){
				return footerSelectionPanel.getAddFileButton();
			}
			else if (aComponent.equals(footerSelectionPanel.getAddFileButton())){
				return footerSelectionPanel.getRemoveFileButton();
			}
			else if (aComponent.equals(footerSelectionPanel.getRemoveFileButton())){
				return selectionPanel.getAddFileButton();
			}
			else if (aComponent.equals(selectionPanel.getAddFileButton())){
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
                return mergeTypeCheck;
            }
            else if (aComponent.equals(mergeTypeCheck)){
                return destinationTextField;
            }
            else if (aComponent.equals(destinationTextField)){
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
                return coverSelectionPanel.getAddFileButton();
            }
            return coverSelectionPanel.getAddFileButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
			if (aComponent.equals(runButton)){
				return versionCombo;
			}
			else if (aComponent.equals(versionCombo)){
				return overwriteCheckbox;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return overwriteCheckbox;
			}
            else if (aComponent.equals(overwriteCheckbox)){
                return browseDestButton;
            }
            else if (aComponent.equals(browseDestButton)){
                return destinationTextField;
            }
            else if (aComponent.equals(destinationTextField)){
                return mergeTypeCheck;
            }
            else if (aComponent.equals(mergeTypeCheck)){
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
                return footerSelectionPanel.getRemoveFileButton();
            }
            else if (aComponent.equals(footerSelectionPanel.getRemoveFileButton())){
				return footerSelectionPanel.getAddFileButton();
			}
			else if (aComponent.equals(footerSelectionPanel.getAddFileButton())){
				return coverSelectionPanel.getRemoveFileButton();
			}
			else if (aComponent.equals(coverSelectionPanel.getRemoveFileButton())){
				return coverSelectionPanel.getAddFileButton();
			}
			else if (aComponent.equals(coverSelectionPanel.getAddFileButton())){
				return runButton;
			}
            return coverSelectionPanel.getAddFileButton();
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return coverSelectionPanel.getAddFileButton();
        }

        public Component getLastComponent(Container CycleRootComp){
            return runButton;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return coverSelectionPanel.getAddFileButton();
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
		((AbstractPdfSelectionTableModel)coverSelectionPanel.getMainTable().getModel()).clearData();	
		((AbstractPdfSelectionTableModel)footerSelectionPanel.getMainTable().getModel()).clearData();	
		destinationTextField.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(true);
		overwriteCheckbox.setSelected(false);
		mergeTypeCheck.setSelected(false);
	}
  
}
