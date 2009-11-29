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
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SetViewerParsedCommand;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.business.listeners.VersionFilterCheckBoxItemListener;
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
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.setviewer.listeners.RunButtonActionListener;
/** 
 * Plugable JPanel provides a GUI for set viewer functions.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class SetViewerMainGUI extends AbstractPlugablePanel implements PropertyChangeListener {

	private static final long serialVersionUID = -5145250933588350919L;

	private static final Logger log = Logger.getLogger(SetViewerMainGUI.class.getPackage().getName());
	
	private JTextField outPrefixTextField = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.PREFIX_TEXT_FIELD_TYPE);
	private SpringLayout optionsPanelLayout;
    private SpringLayout destinationPanelLayout;
    private SpringLayout setviewerOptionPanelLayout;
    private JTextField destFolderText = CommonComponentsFactory.getInstance().createTextField(CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JHelpLabel prefixHelpLabel;
    private JHelpLabel destinationHelpLabel;
	private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
	private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER, AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);

    private Configuration config;

    private JFileChooser browseDestFileChooser = null;
	
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
    private final SetViewerFocusPolicy setViewerFocusPolicy = new SetViewerFocusPolicy();

    //panels
    private final JPanel setviewerOptionsPanel = new JPanel();
    private final JPanel destinationPanel = new JPanel();
    private final JPanel outputOptionsPanel = new JPanel();
    private final JPanel mainOptionsPanel = new JPanel();
    private final JPanel setviewerOptsCheckPanel = new JPanel();
    private final JPanel setviewerOptsComboPanel = new JPanel();
        
    //labels    
    final JLabel outPrefixLabel = new JLabel();
    
	private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_VERSION = "0.0.7e";  
    
    public SetViewerMainGUI(){
    	initialize();
    }
    
    /**
     * initialization
     */
    private void initialize() {
    	config = Configuration.getInstance();
        setPanelIcon("/images/setviewer.png");
        setPreferredSize(new Dimension(1000,700));  
        setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
		add(selectionPanel, c);
		
		selectionPanel.enableSetOutputPathMenuItem();
        selectionPanel.addPropertyChangeListener(this);
        
        mainOptionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints mainCons = new GridBagConstraints();
        mainCons.fill = GridBagConstraints.BOTH;
        mainCons.ipady = 5;
        mainCons.insets = new Insets(0, 0, 5, 0);
        mainCons.weightx = 1.0;
        mainCons.weighty = 1.0;
        mainCons.gridwidth = 3;
        mainCons.gridx = 0;
        mainCons.gridy = 0;
//END_FILE_CHOOSER
        
      //SETVIEWER_SECTION
        optionsPanelLayout = new SpringLayout();
        setviewerOptionsPanel.setLayout(optionsPanelLayout);
		setviewerOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Set viewer options")));
//checks     
        GroupLayout checksLayout = new GroupLayout(setviewerOptsCheckPanel);
        setviewerOptsCheckPanel.setLayout(checksLayout);

        checksLayout.setAutoCreateGaps(true);
     
        hideMenuBar.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide the menubar"));
        hideMenuBar.setSelected(false);
        
        hideToolBar.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide the toolbar"));
        hideToolBar.setSelected(false);

        hideUIElements.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Hide user interface elements"));
        hideUIElements.setSelected(false);

        resizeToFit.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Rezise the window to fit the page size"));
        resizeToFit.setSelected(false);

        centerScreen.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Center of the screen"));
        centerScreen.setSelected(false);

        displayTitle.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Display document title as window title"));
        displayTitle.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version required:")+" 1.4");
        displayTitle.addItemListener(new VersionFilterCheckBoxItemListener(versionCombo, new Integer(""+AbstractParsedCommand.VERSION_1_4)));
        displayTitle.setSelected(false);

        noPageScaling.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"No page scaling in print dialog"));
        noPageScaling.setSelected(false);
        noPageScaling.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version required:")+" 1.6");
        noPageScaling.addItemListener(new VersionFilterCheckBoxItemListener(versionCombo, new Integer(""+AbstractParsedCommand.VERSION_1_6)));
        
        checksLayout.setHorizontalGroup(
        		checksLayout.createSequentialGroup()      		      
        		      .addGroup(checksLayout.createParallelGroup()
        		           .addComponent(hideMenuBar)
        		           .addComponent(resizeToFit)
        		           .addComponent(noPageScaling))
         		      .addGroup(checksLayout.createParallelGroup()
           		           .addComponent(hideToolBar)
           		           .addComponent(centerScreen))
         		      .addGroup(checksLayout.createParallelGroup()
           		           .addComponent(hideUIElements)
           		           .addComponent(displayTitle))       		          
        		);
        checksLayout.setVerticalGroup(
        		checksLayout.createSequentialGroup()
        		      .addGroup(checksLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		           .addComponent(hideMenuBar)
        		           .addComponent(hideToolBar)
        		           .addComponent(hideUIElements))
        		      .addGroup(checksLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		           .addComponent(resizeToFit)
        		           .addComponent(centerScreen)
        		           .addComponent(displayTitle))
        		      .addGroup(checksLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		           .addComponent(noPageScaling))        		           
        		);        
		
        setviewerOptionsPanel.add(setviewerOptsCheckPanel);
//end_check
//combos        
        
        viewerOpenModeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Viewer open mode:"));
        viewerLayoutLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Viewer layout:"));
        nonFullScreenModeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Non fullscreen mode:"));
        directionLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Direction:"));

        viewerLayout = new JComboBox(getViewerLayoutItems());
        directionCombo = new JComboBox(getDirectionComboItems());
        directionCombo.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version required:")+" 1.3");
        viewerOpenMode = new JComboBox(getViewerOpenModeItems());
        nonFullScreenMode = new JComboBox(getViewerNonFullScreenItems());
        nonFullScreenMode.setEnabled(false);
        viewerOpenMode.addActionListener(new OpenModeComboListener(nonFullScreenMode));
       
        GroupLayout combosLayout = new GroupLayout(setviewerOptsComboPanel);
        setviewerOptsComboPanel.setLayout(combosLayout);

        combosLayout.setAutoCreateGaps(true);
        
        combosLayout.setHorizontalGroup(
        		combosLayout.createSequentialGroup()        		      
        		      .addGroup(combosLayout.createParallelGroup()
        		           .addComponent(viewerLayoutLabel)
        		           .addComponent(directionLabel))
         		      .addGroup(combosLayout.createParallelGroup()
           		           .addComponent(viewerLayout)
           		           .addComponent(directionCombo))
         		      .addGroup(combosLayout.createParallelGroup()
           		           .addComponent(viewerOpenModeLabel)
           		           .addComponent(nonFullScreenModeLabel))
       		          .addGroup(combosLayout.createParallelGroup()
           		           .addComponent(viewerOpenMode)
           		           .addComponent(nonFullScreenMode))
        		);
        combosLayout.setVerticalGroup(
        		combosLayout.createSequentialGroup()
        		      .addGroup(combosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		           .addComponent(viewerLayoutLabel)
        		           .addComponent(viewerLayout)
        		           .addComponent(viewerOpenModeLabel)
        		           .addComponent(viewerOpenMode))
        		      .addGroup(combosLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        		           .addComponent(directionLabel)
        		           .addComponent(directionCombo)
        		           .addComponent(nonFullScreenModeLabel)
        		           .addComponent(nonFullScreenMode))
        		);
        
        setviewerOptionsPanel.add(setviewerOptsComboPanel);
		setviewerOptionsPanel.setMinimumSize(new Dimension(330, 175));
		setviewerOptionsPanel.setPreferredSize(new Dimension(400, 195));

//end_combos       
//DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
		destinationPanel.setPreferredSize(new Dimension(200, 160));
		destinationPanel.setMinimumSize(new Dimension(160, 150));
        destinationPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Destination folder")));      
        destinationPanel.add(destFolderText);
//END_DESTINATION_PANEL        


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
				if (browseDestFileChooser == null) {
					browseDestFileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
					browseDestFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				if (destFolderText.getText().length() > 0) {
					browseDestFileChooser.setCurrentDirectory(new File(destFolderText.getText()));
				}
				if (browseDestFileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION) {
					File chosenFile = browseDestFileChooser.getSelectedFile();
					if (chosenFile != null) {
						destFolderText.setText(chosenFile.getAbsolutePath());
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
       
//S_PANEL
        outputOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config.getI18nResourceBundle(),"Output options")));
        outputOptionsPanel.setPreferredSize(new Dimension(200, 55));
        outputOptionsPanel.setMinimumSize(new Dimension(160, 50));
        setviewerOptionPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(setviewerOptionPanelLayout);
        
        outPrefixTextField.setPreferredSize(new Dimension(180,20));
        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);
       
        mainOptionsPanel.add(setviewerOptionsPanel, mainCons);
        mainCons.fill = GridBagConstraints.HORIZONTAL;
        mainCons.weightx = 0.0;
        mainCons.weighty = 0.0;
        mainCons.gridy = 1;
        mainOptionsPanel.add(destinationPanel, mainCons);
        mainCons.fill = GridBagConstraints.HORIZONTAL;
        mainCons.weightx = 0.0;
        mainCons.weighty = 0.0;
        mainCons.gridy = 2;
        mainOptionsPanel.add(outputOptionsPanel, mainCons);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
		add(mainOptionsPanel, c);
        
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Set options"));
        add(runButton);
      //RUN_BUTTON
        //listener
	    runButton.addActionListener(new RunButtonActionListener(this));
	    runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Set viewer options for selected files"));
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
//END_RUN_BUTTON
       
//END_RUN_BUTTON
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
    private Vector<StringItem> getDirectionComboItems(){
    	Vector<StringItem> retVal = new Vector<StringItem>(3);
    	retVal.add(new StringItem("", ""));
    	retVal.add(new StringItem(SetViewerParsedCommand.D_L2R, GettextResource.gettext(config.getI18nResourceBundle(),"Left to right")));
    	retVal.add(new StringItem(SetViewerParsedCommand.D_R2L, GettextResource.gettext(config.getI18nResourceBundle(),"Right to left")));
    	return retVal;
    }
    
    /**
     * @return the viewer open mode combo items
     */
    private Vector<StringItem> getViewerOpenModeItems(){
    	Vector<StringItem> retVal = new Vector<StringItem>(6);
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
    private Vector<StringItem> getViewerNonFullScreenItems(){
    	Vector<StringItem> retVal = new Vector<StringItem>(4);
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_NONE, GettextResource.gettext(config.getI18nResourceBundle(),"None")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_OCONTENT, GettextResource.gettext(config.getI18nResourceBundle(),"Optional content group panel")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_OUTLINES, GettextResource.gettext(config.getI18nResourceBundle(),"Document outline")));
    	retVal.add(new StringItem(SetViewerParsedCommand.NFSM_THUMBS, GettextResource.gettext(config.getI18nResourceBundle(),"Thumbnail images")));
    	return retVal;
    }

    /**
     * @return the viewer layout combo items
     */
    private Vector<StringItem> getViewerLayoutItems(){
    	Vector<StringItem> retVal = new Vector<StringItem>(6);
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
        
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destFolderText, -105, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destFolderText, 10, SpringLayout.NORTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destFolderText, 30, SpringLayout.NORTH, destinationPanel);
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
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 25, SpringLayout.NORTH, browseDestButton);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -5, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, 0, SpringLayout.NORTH, destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -93, SpringLayout.EAST, destinationPanel);        
        
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST, destinationPanel);
             
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixLabel, 20, SpringLayout.NORTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixTextField, 0, SpringLayout.SOUTH, outPrefixLabel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.WEST, outPrefixTextField, 10, SpringLayout.EAST, outPrefixLabel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.EAST, outPrefixTextField, -30, SpringLayout.EAST, outputOptionsPanel);
        
        setviewerOptionPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH, outputOptionsPanel);
        setviewerOptionPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);
                
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, setviewerOptsComboPanel, 10, SpringLayout.NORTH, setviewerOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, setviewerOptsComboPanel, 5, SpringLayout.WEST, setviewerOptionsPanel);
        
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, setviewerOptsCheckPanel, 10, SpringLayout.SOUTH, setviewerOptsComboPanel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, setviewerOptsCheckPanel, 0, SpringLayout.WEST, setviewerOptsComboPanel);


}
    
	public FocusTraversalPolicy getFocusPolicy() {
		return setViewerFocusPolicy;
	}

	public Node getJobNode(Node arg0, boolean savePasswords)
			throws SaveJobException {
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

				Element viewerLayoutNode = ((Element)arg0).addElement("viewer-layout");
				viewerLayoutNode.addAttribute("value", ((StringItem)viewerLayout.getSelectedItem()).getId());
				
				Element viewerOpenModeNode = ((Element)arg0).addElement("viewer-open-mode");
				viewerOpenModeNode.addAttribute("value", ((StringItem)viewerOpenMode.getSelectedItem()).getId());

				Element nonFullScreenModeNode = ((Element)arg0).addElement("non-fullscreen-mode");
				nonFullScreenModeNode.addAttribute("value", ((StringItem)nonFullScreenMode.getSelectedItem()).getId());

				Element directionComboNode = ((Element)arg0).addElement("direction");
				directionComboNode.addAttribute("value", ((StringItem)directionCombo.getSelectedItem()).getId());

				Element hideMenuBarNode = ((Element)arg0).addElement("hide-menu-bar");
				hideMenuBarNode.addAttribute("value", hideMenuBar.isSelected()?TRUE:FALSE);
				
				Element hideToolBarNode = ((Element)arg0).addElement("hide-tool-bar");
				hideToolBarNode.addAttribute("value", hideToolBar.isSelected()?TRUE:FALSE);
				
				Element hideUIElementsNode = ((Element)arg0).addElement("hide-ui-elements");				
				hideUIElementsNode.addAttribute("value", hideUIElements.isSelected()?TRUE:FALSE);
				
				Element resizeNode = ((Element)arg0).addElement("resize");				
				resizeNode.addAttribute("value", resizeToFit.isSelected()?TRUE:FALSE);
				
				Element centerScreenNode = ((Element)arg0).addElement("center-screen");				
				centerScreenNode.addAttribute("value", centerScreen.isSelected()?TRUE:FALSE);
				
				Element displayTitleNode = ((Element)arg0).addElement("display-title");				
				displayTitleNode.addAttribute("value", displayTitle.isSelected()?TRUE:FALSE);
				
				Element noPageScalingNode = ((Element)arg0).addElement("no-page-scaling");				
				noPageScalingNode.addAttribute("value", noPageScaling.isSelected()?TRUE:FALSE);
				
				Element fileDestination = ((Element)arg0).addElement("destination");
				fileDestination.addAttribute("value", destFolderText.getText());			
				
				Element filePrefix = ((Element)arg0).addElement("prefix");
				filePrefix.addAttribute("value", outPrefixTextField.getText());	
				
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
		try {
			Node fileSource = (Node) arg0.selectSingleNode("source/@value");
			if (fileSource != null && fileSource.getText().length() > 0) {
				Node filePwd = (Node) arg0.selectSingleNode("source/@password");
				String password = null;
				if (filePwd != null && filePwd.getText().length() > 0) {
					password = filePwd.getText();
				}
				selectionPanel.getLoader().addFile(new File(fileSource.getText()), password);
			}

			Node viewerLayoutNode = (Node) arg0.selectSingleNode("viewer-layout/@value");
			if (viewerLayoutNode != null) {
				for (int i = 0; i < viewerLayout.getItemCount(); i++) {
					if (((StringItem) viewerLayout.getItemAt(i)).getId().equals(viewerLayoutNode.getText())) {
						viewerLayout.setSelectedIndex(i);
						break;
					}
				}
			}

			Node viewerOpenModeNode = (Node) arg0.selectSingleNode("viewer-open-mode/@value");
			if (viewerOpenModeNode != null) {
				for (int i = 0; i < viewerOpenMode.getItemCount(); i++) {
					if (((StringItem) viewerOpenMode.getItemAt(i)).getId().equals(viewerOpenModeNode.getText())) {
						viewerOpenMode.setSelectedIndex(i);
						break;
					}
				}
			}

			Node nonFullScreenModeNode = (Node) arg0.selectSingleNode("non-fullscreen-mode/@value");
			if (nonFullScreenModeNode != null) {
				for (int i = 0; i < nonFullScreenMode.getItemCount(); i++) {
					if (((StringItem) nonFullScreenMode.getItemAt(i)).getId().equals(nonFullScreenModeNode.getText())) {
						nonFullScreenMode.setSelectedIndex(i);
						break;
					}
				}
			}

			Node directionComboNode = (Node) arg0.selectSingleNode("direction/@value");
			if (directionComboNode != null) {
				for (int i = 0; i < directionCombo.getItemCount(); i++) {
					if (((StringItem) directionCombo.getItemAt(i)).getId().equals(directionComboNode.getText())) {
						directionCombo.setSelectedIndex(i);
						break;
					}
				}
			}

			Node hideMenuBarNode = (Node) arg0.selectSingleNode("hide-menu-bar/@value");
			if (hideMenuBarNode != null) {
				hideMenuBar.setSelected(TRUE.equals(hideMenuBarNode.getText()));
			}

			Node hideToolBarNode = (Node) arg0.selectSingleNode("hide-tool-bar/@value");
			if (hideToolBarNode != null) {
				hideToolBar.setSelected(TRUE.equals(hideToolBarNode.getText()));
			}

			Node hideUIElementsNode = (Node) arg0.selectSingleNode("hide-ui-elements/@value");
			if (hideUIElementsNode != null) {
				hideUIElements.setSelected(TRUE.equals(hideUIElementsNode.getText()));
			}

			Node resizeNode = (Node) arg0.selectSingleNode("resize/@value");
			if (resizeNode != null) {
				resizeToFit.setSelected(TRUE.equals(resizeNode.getText()));
			}

			Node centerScreenNode = (Node) arg0.selectSingleNode("center-screen/@value");
			if (centerScreenNode != null) {
				centerScreen.setSelected(TRUE.equals(centerScreenNode.getText()));
			}

			Node displayTitleNode = (Node) arg0.selectSingleNode("display-title/@value");
			if (displayTitleNode != null && TRUE.equals(displayTitleNode.getText())) {
				displayTitle.doClick();
			}

			Node noPageScalingNode = (Node) arg0.selectSingleNode("no-page-scaling/@value");
			if (noPageScalingNode != null && TRUE.equals(noPageScalingNode.getText())) {
				noPageScaling.doClick();
			}

			Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
			if (fileDestination != null) {
				destFolderText.setText(fileDestination.getText());
			}

			Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
			if (fileOverwrite != null) {
				overwriteCheckbox.setSelected(TRUE.equals(fileOverwrite.getText()));
			}

			Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
			if (fileCompressed != null && TRUE.equals(fileCompressed.getText())) {
				outputCompressedCheck.doClick();
			}

			Node filePrefix = (Node) arg0.selectSingleNode("prefix/@value");
			if (filePrefix != null) {
				outPrefixTextField.setText(filePrefix.getText());
			}

			Node pdfVersion = (Node) arg0.selectSingleNode("pdfversion/@value");
			if (pdfVersion != null) {
				for (int i = 0; i < versionCombo.getItemCount(); i++) {
					if (((StringItem) versionCombo.getItemAt(i)).getId().equals(pdfVersion.getText())) {
						versionCombo.setSelectedIndex(i);
						break;
					}
				}
			}

			log.info(GettextResource.gettext(config.getI18nResourceBundle(), "Viewer options section loaded."));
		} catch (Exception ex) {
			log.error(GettextResource.gettext(config.getI18nResourceBundle(), "Error: "), ex);
		}
	}


	public void resetPanel() {
		selectionPanel.clearSelectionTable();	
		destFolderText.setText("");
		versionCombo.resetComponent();
		outputCompressedCheck.setSelected(false);
		hideMenuBar.setSelected(false);
		hideToolBar.setSelected(false);
		hideUIElements.setSelected(false);
		resizeToFit.setSelected(false);
		centerScreen.setSelected(false);
		displayTitle.setSelected(false);
		noPageScaling.setSelected(false);
		overwriteCheckbox.setSelected(false);
		viewerLayout.setSelectedIndex(0);
		viewerOpenMode.setSelectedIndex(0);
		nonFullScreenMode.setSelectedIndex(0);
		nonFullScreenMode.setEnabled(false);
		directionCombo.setSelectedIndex(0);
		destFolderText.setText("");
		outPrefixTextField.setText("");
	}
	
	   /**
     * 
     * @author Andrea Vacondio
     * Focus policy for set viewer panel
     *
     */
    public class SetViewerFocusPolicy extends FocusTraversalPolicy {
        public SetViewerFocusPolicy(){
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
                return viewerLayout;
            }           
            else if (aComponent.equals(viewerLayout)){
                return viewerOpenMode;
            }
            else if (aComponent.equals(viewerOpenMode)){
            	if (nonFullScreenMode.isEnabled()){
                    return nonFullScreenMode;
                }else{
                    return directionCombo;
                }
            }
            else if (aComponent.equals(nonFullScreenMode)){
                return directionCombo;
            }
            else if (aComponent.equals(directionCombo)){
                return hideMenuBar;
            }
            else if (aComponent.equals(hideMenuBar)){
                return hideToolBar;
            }
            else if (aComponent.equals(hideToolBar)){
                return hideUIElements;
            }
            else if (aComponent.equals(hideUIElements)){
                return resizeToFit;
            }
            else if (aComponent.equals(resizeToFit)){
                return centerScreen;
            }
            else if (aComponent.equals(centerScreen)){
                return displayTitle;
            }
            else if (aComponent.equals(displayTitle)){
                return noPageScaling;
            }
            else if (aComponent.equals(noPageScaling)){
                return destFolderText;
            }
            else if (aComponent.equals(destFolderText)){
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
				return outPrefixTextField;
			}
			else if (aComponent.equals(outPrefixTextField)){
				return runButton;
			} 
            else if (aComponent.equals(runButton)){
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
        	if (aComponent.equals(runButton)){
				return outPrefixTextField;
			}
			else if (aComponent.equals(outPrefixTextField)){
				return versionCombo;
			}
			else if (aComponent.equals(versionCombo)){
				return outputCompressedCheck;
			}
			else if (aComponent.equals(outputCompressedCheck)){
				return overwriteCheckbox;
			}           
            else if (aComponent.equals(overwriteCheckbox)){            	
                return browseDestButton;              
            }
            else if (aComponent.equals(browseDestButton)){
                return destFolderText;
            }                
            else if (aComponent.equals(destFolderText)){
                return noPageScaling;
            }
            else if (aComponent.equals(noPageScaling)){
                return displayTitle;
            }
            else if (aComponent.equals(displayTitle)){
                return centerScreen;
            }
            else if (aComponent.equals(centerScreen)){
                return resizeToFit;
            }
            else if (aComponent.equals(resizeToFit)){
                return hideUIElements;
            }
            else if (aComponent.equals(hideUIElements)){
                return hideToolBar;
            }
            else if (aComponent.equals(hideToolBar)){
                return hideMenuBar;
            }
            else if (aComponent.equals(hideMenuBar)){
                return directionCombo;
            }
            else if (aComponent.equals(directionCombo)){
            	if (nonFullScreenMode.isEnabled()){
                    return nonFullScreenMode;
                }else{
                    return viewerOpenMode;
                }
            }
            else if (aComponent.equals(nonFullScreenMode)){
                return viewerOpenMode;
            }
            else if (aComponent.equals(viewerOpenMode)){
                return viewerLayout;
            }
            else if (aComponent.equals(viewerLayout)){
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
     * Action listener for the open mode combo. Enable the nonFullScreenCombo only if the full screen mode is selected
     * @author Andrea Vacondio
     *
     */
    private class OpenModeComboListener implements ActionListener {
    	
    	private JComboBox nonFullScreenCombo = null;    	    	

		/**
		 * @param nonFullScreenCombo
		 * @param openModeCombo
		 */
		public OpenModeComboListener(JComboBox nonFullScreenCombo) {
			super();
			this.nonFullScreenCombo = nonFullScreenCombo;
		}


		public void actionPerformed(ActionEvent e) {
			JComboBox openModeCombo = (JComboBox) e.getSource();
    		if(openModeCombo!=null && nonFullScreenCombo!=null && openModeCombo.getSelectedItem()!=null){
    			if(SetViewerParsedCommand.M_FULLSCREEN.equals(((StringItem)openModeCombo.getSelectedItem()).getId())){
    				nonFullScreenCombo.setEnabled(true);
    			}else{
    				nonFullScreenCombo.setEnabled(false);
    			}
    		}

    	}
    }

	/**
	 * @return the outPrefixTextField
	 */
	public JTextField getOutPrefixTextField() {
		return outPrefixTextField;
	}

	/**
	 * @return the destFolderText
	 */
	public JTextField getDestFolderText() {
		return destFolderText;
	}

	/**
	 * @return the versionCombo
	 */
	public JPdfVersionCombo getVersionCombo() {
		return versionCombo;
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
	 * @return the hideToolBar
	 */
	public JCheckBox getHideToolBar() {
		return hideToolBar;
	}

	/**
	 * @return the hideUIElements
	 */
	public JCheckBox getHideUIElements() {
		return hideUIElements;
	}

	/**
	 * @return the resizeToFit
	 */
	public JCheckBox getResizeToFit() {
		return resizeToFit;
	}

	/**
	 * @return the noPageScaling
	 */
	public JCheckBox getNoPageScaling() {
		return noPageScaling;
	}

	/**
	 * @return the viewerOpenMode
	 */
	public JComboBox getViewerOpenMode() {
		return viewerOpenMode;
	}

	/**
	 * @return the nonFullScreenMode
	 */
	public JComboBox getNonFullScreenMode() {
		return nonFullScreenMode;
	}

	/**
	 * @return the setviewerOptsComboPanel
	 */
	public JPanel getSetviewerOptsComboPanel() {
		return setviewerOptsComboPanel;
	}

	/**
	 * @return the hideMenuBar
	 */
	public JCheckBox getHideMenuBar() {
		return hideMenuBar;
	}

	/**
	 * @return the centerScreen
	 */
	public JCheckBox getCenterScreen() {
		return centerScreen;
	}

	/**
	 * @return the displayTitle
	 */
	public JCheckBox getDisplayTitle() {
		return displayTitle;
	}

	/**
	 * @return the viewerLayout
	 */
	public JComboBox getViewerLayout() {
		return viewerLayout;
	}

	/**
	 * @return the directionCombo
	 */
	public JComboBox getDirectionCombo() {
		return directionCombo;
	}

	/**
	 * @return the selectionPanel
	 */
	public JPdfSelectionPanel getSelectionPanel() {
		return selectionPanel;
	}
    
    
}
