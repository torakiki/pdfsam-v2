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
package org.pdfsam.plugin.setviewer.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.SetViewerParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
/** 
 * Plugable JPanel provides a GUI for set viewer functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class SetViewerMainGUI extends AbstractPlugablePanel implements PropertyChangeListener {


	private static final Logger log = Logger.getLogger(SetViewerMainGUI.class.getPackage().getName());
	
	private JTextField outPrefixTextField;
	private SpringLayout optionsPanelLayout;
    private SpringLayout setviewerSpringLayout;
    private SpringLayout destinationPanelLayout;
    private SpringLayout setviewerOptionPanelLayout;
    private JTextField destFolderText;
    private JHelpLabel prefixHelpLabel;
    private JHelpLabel destinationHelpLabel;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);

    private Configuration config;

    private final JFileChooser browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
	
    //button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);       
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.RUN_BUTTON_TYPE);
    //key_listeners
    private final EnterDoClickListener browseEnterKeyListener = new EnterDoClickListener(browseDestButton);
    private final EnterDoClickListener runEnterKeyListener = new EnterDoClickListener(runButton); 

    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
	private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(CommonComponentsFactory.PDF_VERSION_LABEL);	

	private final JCheckBox hideMenuBar = new JCheckBox();
	private final JCheckBox hideToolBar = new JCheckBox();
	private final JCheckBox hideUIElements = new JCheckBox();
	private final JCheckBox resizeToFit = new JCheckBox();
	private final JCheckBox centerScreen = new JCheckBox();
	private final JCheckBox displayTitle = new JCheckBox();
	private final JCheckBox noPageScaling = new JCheckBox();
	
	private JComboBox viewerLayout;
	private final JLabel viewerLayoutLabel = new JLabel();
	private JComboBox viewerOpenMode;
	private final JLabel viewerOpenModeLabel = new JLabel();
	private JComboBox nonFullScreenMode;
	private final JLabel nonFullScreenModeLabel = new JLabel();
	private JComboBox directionCombo ;
	private final JLabel directionLabel= new JLabel();

	//focus policy 
    //private final EncryptFocusPolicy encryptfocusPolicy = new EncryptFocusPolicy();

    //panels
    private final JPanel setviewerOptionsPanel = new JPanel();
    private final JPanel destinationPanel = new JPanel();
    private final JPanel outputOptionsPanel = new JPanel();

    //labels    
    final JLabel destFolderLabel = new JLabel();
    final JLabel outputOptionsLabel = new JLabel();
    final JLabel outPrefixLabel = new JLabel();
    final JLabel setviewerOptionsLabel= new JLabel();
    
	private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_VERSION = "0.0.1e";  
    
    public SetViewerMainGUI(){
    	initialize();
    }
    
    /**
     * initialization
     */
    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/encrypt.png");
        setPreferredSize(new Dimension(500,700));  
        
        setviewerSpringLayout = new SpringLayout();
        setLayout(setviewerSpringLayout);
        add(selectionPanel);
		selectionPanel.enableSetOutputPathMenuItem();
        selectionPanel.addPropertyChangeListener(this);

//FILE_CHOOSER
        browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//END_FILE_CHOOSER
        
      //SETVIEWER_SECTION
        optionsPanelLayout = new SpringLayout();
        setviewerOptionsPanel.setLayout(optionsPanelLayout);
        setviewerOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(setviewerOptionsPanel);
//checks        
        hideMenuBar.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide the menubar"));
        hideMenuBar.setSelected(false);
        setviewerOptionsPanel.add(hideMenuBar);
        
        hideToolBar.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide the toolbar"));
        hideToolBar.setSelected(false);
        setviewerOptionsPanel.add(hideToolBar);

        hideUIElements.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide user interface elements"));
        hideUIElements.setSelected(false);
        setviewerOptionsPanel.add(hideUIElements);

        resizeToFit.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Rezise the window to fit the page size"));
        resizeToFit.setSelected(false);
        setviewerOptionsPanel.add(resizeToFit);

        centerScreen.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Center of the screen"));
        centerScreen.setSelected(false);
        setviewerOptionsPanel.add(centerScreen);

        displayTitle.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Display document title as window title"));
        displayTitle.setSelected(false);
        setviewerOptionsPanel.add(displayTitle);

        noPageScaling.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"No page scaling in print dialog"));
        noPageScaling.setSelected(false);
        setviewerOptionsPanel.add(noPageScaling);
//end_check
//combos
        viewerLayoutLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Viewer layout:"));
        setviewerOptionsPanel.add(viewerLayoutLabel);		
        viewerLayout = new JComboBox(getViewerLayoutItems());
        setviewerOptionsPanel.add(viewerLayout);
        
        viewerOpenModeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Viewer open mode:"));
        setviewerOptionsPanel.add(viewerOpenModeLabel);
        viewerOpenMode = new JComboBox(getViewerOpenModeItems());
        setviewerOptionsPanel.add(viewerOpenMode);

        nonFullScreenModeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Non fullscreen mode:"));
        setviewerOptionsPanel.add(nonFullScreenModeLabel);
        nonFullScreenMode = new JComboBox(getViewerNonFullScreenItems());
        setviewerOptionsPanel.add(nonFullScreenMode);

        directionLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Direction:"));
        setviewerOptionsPanel.add(directionLabel);
        directionCombo = new JComboBox(getDirectionComboItems());
        setviewerOptionsPanel.add(directionCombo);
       
        setviewerOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Set viewer options:"));
        add(setviewerOptionsLabel);
//end_combos       
//DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destinationPanel);
//END_DESTINATION_PANEL        
        destFolderLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder:"));
        add(destFolderLabel);
        
        destFolderText = new JTextField();
        destFolderText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destinationPanel.add(destFolderText);

//CHECK_BOX
        overwriteCheckbox.setSelected(true);
        destinationPanel.add(overwriteCheckbox);
        
        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        destinationPanel.add(outputCompressedCheck);
        
        destinationPanel.add(versionCombo);        
//END_CHECK_BOX  
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
                    	destFolderText.setText(chosenFile.getAbsolutePath());
                    }
                    catch (Exception ex){
                    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                        
                    }
                }
                
            }
        });
        destinationPanel.add(browseDestButton);
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check the box if you want compressed output files (Pdf version 1.5 or higher).")+"</p>"+
    		"<p>"+GettextResource.gettext(config.getI18nResourceBundle(),"Set the pdf version of the ouput document.")+"</p>"+
    		"</body></html>";
	    destinationHelpLabel = new JHelpLabel(helpTextDest, true);
	    destinationPanel.add(destinationHelpLabel);
        outputOptionsLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output options:"));
        add(outputOptionsLabel);
       
//S_PANEL
        outputOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        setviewerOptionPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(setviewerOptionPanelLayout);
        add(outputOptionsPanel);

        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);
       
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Set options"));
        add(runButton);
        
        outPrefixTextField = new JTextField();
        outPrefixTextField.setText("pdfsam_");
        outPrefixTextField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        outputOptionsPanel.add(outPrefixTextField);
//END_S_PANEL
//      HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it contains \"[TIMESTAMP]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"If it doesn't contain \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(config.getI18nResourceBundle(),"Available variables: [TIMESTAMP], [BASENAME].")+"</p>"+
    		"</body></html>";
	    prefixHelpLabel = new JHelpLabel(helpTextPrefix, true);
	    outputOptionsPanel.add(prefixHelpLabel);
//END_HELP_LABEL_PREFIX  
	    
//KEY_LISTENER
        runButton.addKeyListener(runEnterKeyListener);
        browseDestButton.addKeyListener(browseEnterKeyListener);
        
        destFolderText.addKeyListener(runEnterKeyListener);

//LAYOUT
        setLayout();
    }
    
    /**
     * @return the direction combo items
     */
    private Vector getDirectionComboItems(){
    	Vector retVal = new Vector(2);
    	retVal.add(new StringItem(SetViewerParsedCommand.D_L2R, GettextResource.gettext(config.getI18nResourceBundle(),"Left to right")));
    	retVal.add(new StringItem(SetViewerParsedCommand.D_R2L, GettextResource.gettext(config.getI18nResourceBundle(),"Right to left")));
    	return retVal;
    }
    
    /**
     * @return the viewer open mode combo items
     */
    private Vector getViewerOpenModeItems(){
    	Vector retVal = new Vector(6);
    	retVal.add(new StringItem(SetViewerParsedCommand.M_NONE, GettextResource.gettext(config.getI18nResourceBundle(),"None")));
    	retVal.add(new StringItem(SetViewerParsedCommand.M_FULLSCREEN, GettextResource.gettext(config.getI18nResourceBundle(),"Fullscreen")));
    	retVal.add(new StringItem(SetViewerParsedCommand.M_ATTACHMENTS, GettextResource.gettext(config.getI18nResourceBundle(),"Attachments")));
    	retVal.add(new StringItem(SetViewerParsedCommand.M_OCONTENT, GettextResource.gettext(config.getI18nResourceBundle(),"Optional content group panel")));
    	retVal.add(new StringItem(SetViewerParsedCommand.M_OUTLINES, GettextResource.gettext(config.getI18nResourceBundle(),"Document outline")));
    	retVal.add(new StringItem(SetViewerParsedCommand.M_THUMBS, GettextResource.gettext(config.getI18nResourceBundle(),"Thumbnail images")));
    	return retVal;
    }

    /**
     * @return the non full screen combo items
     */
    private Vector getViewerNonFullScreenItems(){
    	Vector retVal = new Vector(4);
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_NONE, GettextResource.gettext(config.getI18nResourceBundle(),"None")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_OCONTENT, GettextResource.gettext(config.getI18nResourceBundle(),"Optional content group panel")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_OUTLINES, GettextResource.gettext(config.getI18nResourceBundle(),"Document outline")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_THUMBS, GettextResource.gettext(config.getI18nResourceBundle(),"Thumbnail images")));
    	return retVal;
    }

    /**
     * @return the viewer layout combo items
     */
    private Vector getViewerLayoutItems(){
    	Vector retVal = new Vector(6);
    	retVal.add(new StringItem(SetViewerParsedCommand.L_SINGLEPAGE, GettextResource.gettext(config.getI18nResourceBundle(),"One page at a time")));
    	retVal.add(new StringItem(SetViewerParsedCommand.L_ONECOLUMN, GettextResource.gettext(config.getI18nResourceBundle(),"Pages in one column")));
    	retVal.add(new StringItem(SetViewerParsedCommand.L_TWOCOLUMNLEFT, GettextResource.gettext(config.getI18nResourceBundle(),"Pages in two columns (odd on the left)")));
    	retVal.add(new StringItem(SetViewerParsedCommand.L_TWOCOLUMNRIGHT, GettextResource.gettext(config.getI18nResourceBundle(),"Pages in two columns (odd on the right)")));
    	retVal.add(new StringItem(SetViewerParsedCommand.L_TWOPAGELEFT, GettextResource.gettext(config.getI18nResourceBundle(),"Two pages at a time (odd on the left)")));
    	retVal.add(new StringItem(SetViewerParsedCommand.L_TWOPAGERIGHT, GettextResource.gettext(config.getI18nResourceBundle(),"Two pages at a time (odd on the right)")));
    	return retVal;
    }

    public void propertyChange(PropertyChangeEvent evt) {
		if(JPdfSelectionPanel.OUTPUT_PATH_PROPERTY.equals(evt.getPropertyName())){
			destFolderText.setText(((String)evt.getNewValue()));
		}		
	}
	
    private void setLayout(){
    	setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, selectionPanel, 220, SpringLayout.NORTH, this);
    	setviewerSpringLayout.putConstraint(SpringLayout.EAST, selectionPanel, -5, SpringLayout.EAST, this);
    	setviewerSpringLayout.putConstraint(SpringLayout.NORTH, selectionPanel, 5, SpringLayout.NORTH, this);
    	setviewerSpringLayout.putConstraint(SpringLayout.WEST, selectionPanel, 5, SpringLayout.WEST, this);
		
    	setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, setviewerOptionsPanel, 170, SpringLayout.NORTH, setviewerOptionsPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.EAST, setviewerOptionsPanel, -5, SpringLayout.EAST, this);
    	setviewerSpringLayout.putConstraint(SpringLayout.NORTH, setviewerOptionsPanel, 20, SpringLayout.SOUTH, selectionPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.WEST, setviewerOptionsPanel, 0, SpringLayout.WEST, selectionPanel);

    	setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, setviewerOptionsLabel, 0, SpringLayout.NORTH, setviewerOptionsPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.WEST, setviewerOptionsLabel, 0, SpringLayout.WEST, setviewerOptionsPanel);
        
    	setviewerSpringLayout.putConstraint(SpringLayout.NORTH, destFolderLabel, 5, SpringLayout.SOUTH, setviewerOptionsPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.WEST, destFolderLabel, 0, SpringLayout.WEST, setviewerOptionsPanel);

    	setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, destinationPanel, 120, SpringLayout.NORTH, destinationPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.EAST, destinationPanel, 0, SpringLayout.EAST, setviewerOptionsPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.NORTH, destinationPanel, 20, SpringLayout.SOUTH, setviewerOptionsPanel);
    	setviewerSpringLayout.putConstraint(SpringLayout.WEST, destinationPanel, 0, SpringLayout.WEST, selectionPanel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destFolderText, 30, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destFolderText, 10, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destFolderText, -105, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destFolderText, 5, SpringLayout.WEST, destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destFolderText);   
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH, overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST, destFolderText);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 5, SpringLayout.SOUTH, outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST, destFolderText);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, versionCombo, 0, SpringLayout.NORTH, outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 0, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, -25, SpringLayout.SOUTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -98, SpringLayout.EAST, destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);

        setviewerSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsLabel, 0, SpringLayout.EAST, destinationPanel);
        setviewerSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsLabel, 0, SpringLayout.WEST, destinationPanel);
        setviewerSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsLabel, 5, SpringLayout.SOUTH, destinationPanel);
        
        setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, outputOptionsPanel, 48, SpringLayout.NORTH, outputOptionsPanel);
        setviewerSpringLayout.putConstraint(SpringLayout.EAST, outputOptionsPanel, 0, SpringLayout.EAST, destinationPanel);
        setviewerSpringLayout.putConstraint(SpringLayout.NORTH, outputOptionsPanel, 0, SpringLayout.SOUTH, outputOptionsLabel);
        setviewerSpringLayout.putConstraint(SpringLayout.WEST, outputOptionsPanel, 0, SpringLayout.WEST, outputOptionsLabel);
      
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 25, SpringLayout.NORTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.EAST, outPrefixTextField, -10, SpringLayout.EAST, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixTextField, 0, SpringLayout.SOUTH, outPrefixLabel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixTextField, 0, SpringLayout.NORTH, outPrefixLabel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.WEST, outPrefixTextField, 15, SpringLayout.EAST, outPrefixLabel);
        
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
        
        setviewerSpringLayout.putConstraint(SpringLayout.SOUTH, runButton, 25, SpringLayout.NORTH, runButton);
        setviewerSpringLayout.putConstraint(SpringLayout.EAST, runButton, -10, SpringLayout.EAST, this);
        setviewerSpringLayout.putConstraint(SpringLayout.WEST, runButton, -88, SpringLayout.EAST, runButton);
        setviewerSpringLayout.putConstraint(SpringLayout.NORTH, runButton, 10, SpringLayout.SOUTH, outputOptionsPanel);
        

        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, viewerLayoutLabel, 20, SpringLayout.NORTH, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, viewerLayoutLabel, 160, SpringLayout.WEST, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, viewerLayoutLabel, 10, SpringLayout.NORTH, setviewerOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, viewerLayoutLabel, 10, SpringLayout.WEST, setviewerOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, viewerLayout, 0, SpringLayout.SOUTH, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, viewerLayout, 200, SpringLayout.WEST, viewerLayout);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, viewerLayout, 0, SpringLayout.NORTH, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, viewerLayout, 5, SpringLayout.EAST, viewerLayoutLabel);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, viewerOpenModeLabel, 20, SpringLayout.NORTH, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, viewerOpenModeLabel, 0, SpringLayout.EAST, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, viewerOpenModeLabel, 5, SpringLayout.SOUTH, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, viewerOpenModeLabel, 0, SpringLayout.WEST, viewerLayoutLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, viewerOpenMode, 0, SpringLayout.SOUTH, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, viewerOpenMode, 0, SpringLayout.EAST, viewerLayout);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, viewerOpenMode, 0, SpringLayout.NORTH, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, viewerOpenMode, 0, SpringLayout.WEST, viewerLayout);

        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, nonFullScreenModeLabel, 20, SpringLayout.NORTH, nonFullScreenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, nonFullScreenModeLabel, 160, SpringLayout.WEST, nonFullScreenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, nonFullScreenModeLabel, 0, SpringLayout.NORTH, viewerOpenMode);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, nonFullScreenModeLabel, 10, SpringLayout.EAST, viewerOpenMode);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, nonFullScreenMode, 0, SpringLayout.SOUTH, nonFullScreenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, nonFullScreenMode, 200, SpringLayout.WEST, nonFullScreenMode);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, nonFullScreenMode, 0, SpringLayout.NORTH, nonFullScreenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, nonFullScreenMode, 5, SpringLayout.EAST, nonFullScreenModeLabel);

        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, directionLabel, 20, SpringLayout.NORTH, directionLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, directionLabel, 0, SpringLayout.EAST, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, directionLabel, 5, SpringLayout.SOUTH, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, directionLabel, 0, SpringLayout.WEST, viewerOpenModeLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, directionCombo, 0, SpringLayout.SOUTH, directionLabel);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, directionCombo, 0, SpringLayout.EAST, viewerOpenMode);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, directionCombo, 0, SpringLayout.NORTH, directionLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, directionCombo, 0, SpringLayout.WEST, viewerOpenMode);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, hideMenuBar, 20, SpringLayout.NORTH, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, hideMenuBar, 230, SpringLayout.WEST, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, hideMenuBar, 10, SpringLayout.SOUTH, directionLabel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, hideMenuBar, 0, SpringLayout.WEST, directionLabel);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, hideToolBar, 0, SpringLayout.SOUTH, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, hideToolBar, 230, SpringLayout.WEST, hideToolBar);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, hideToolBar, 0, SpringLayout.NORTH, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, hideToolBar, 5, SpringLayout.EAST, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, hideUIElements, 0, SpringLayout.SOUTH, hideToolBar);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, hideUIElements, 230, SpringLayout.WEST, hideUIElements);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, hideUIElements, 0, SpringLayout.NORTH, hideToolBar);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, hideUIElements, 0, SpringLayout.EAST, hideToolBar);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, centerScreen, 20, SpringLayout.NORTH, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, centerScreen, 0, SpringLayout.EAST, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, centerScreen, 0, SpringLayout.SOUTH, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, centerScreen, 0, SpringLayout.WEST, hideMenuBar);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, noPageScaling, 0, SpringLayout.SOUTH, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, noPageScaling, 0, SpringLayout.EAST, hideToolBar);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, noPageScaling, 0, SpringLayout.NORTH, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, noPageScaling, 5, SpringLayout.EAST, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, resizeToFit, 0, SpringLayout.SOUTH, noPageScaling);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, resizeToFit, 0, SpringLayout.EAST, hideUIElements);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, resizeToFit, 0, SpringLayout.NORTH, noPageScaling);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, resizeToFit, 0, SpringLayout.EAST, noPageScaling);
        
        optionsPanelLayout.putConstraint(SpringLayout.SOUTH, displayTitle, 20, SpringLayout.NORTH, displayTitle);
        optionsPanelLayout.putConstraint(SpringLayout.EAST, displayTitle, 0, SpringLayout.EAST, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, displayTitle, 0, SpringLayout.SOUTH, centerScreen);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, displayTitle, 0, SpringLayout.WEST, centerScreen);

}
    
	public FocusTraversalPolicy getFocusPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getJobNode(Node arg0, boolean savePasswords)
			throws SaveJobException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPluginAuthor() {
		return PLUGIN_AUTHOR;
	}

	public String getPluginName() {
		 return GettextResource.gettext(config.getI18nResourceBundle(),"Viewer options");
	}

	public String getVersion() {
		return PLUGIN_VERSION;
	}

	public void loadJobNode(Node arg0) throws LoadJobException {
		// TODO Auto-generated method stub

	}

	public void resetPanel() {
		// TODO Auto-generated method stub

	}

}
