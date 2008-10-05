/*
 * Created on 19-Oct-2006
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

package org.pdfsam.guiclient.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.business.listeners.mediators.UpdateCheckerMediator;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.utils.ThemeUtility;
import org.pdfsam.guiclient.utils.UpdatesUtility;
import org.pdfsam.guiclient.utils.filters.XmlFilter;
import org.pdfsam.guiclient.utils.xml.XMLParser;
import org.pdfsam.i18n.GettextResource;

/**
 * Plugable JPanel provides a GUI for pdfsam settings.
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class JSettingsPanel extends AbstractPlugablePanel{        

	private static final long serialVersionUID = -8466940940495530909L;

	private static final Logger log = Logger.getLogger(JSettingsPanel.class.getPackage().getName());
	
	private JTextField loadDefaultEnv;
	private JTextField defaultDirectory;
    private JComboBox languageCombo;	
    private JComboBox comboLog;	
    private JComboBox thumbLibraryCombo;	
    private JComboBox comboLaf;
    private JComboBox comboTheme;
    private JComboBox comboCheckNewVersion;
    private JCheckBox playSounds;

    private JHelpLabel envHelpLabel;
	private JFileChooser fileChooser ;

	private SpringLayout settingsLayout;
    private SpringLayout grayBorderSettingsLayout;
    
    private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);
    private final JButton browseDestDirButton = CommonComponentsFactory.getInstance().createButton(CommonComponentsFactory.BROWSE_BUTTON_TYPE);
    private final JButton saveButton = new JButton();
    private final JButton checkNowButton = new JButton();

    private final EnterDoClickListener browseDestDirEnterKeyListener = new EnterDoClickListener(browseDestDirButton);
    private final EnterDoClickListener browseEnterKeyListener = new EnterDoClickListener(browseButton);
    private final EnterDoClickListener saveEnterKeyListener = new EnterDoClickListener(saveButton);

	private final JPanel settingsOptionsPanel = new JPanel();
//  focus policy 
    private final SettingsFocusPolicy settingsFocusPolicy = new SettingsFocusPolicy();	
//labels    
    private final JLabel themeLabel = new JLabel();
    private final JLabel subThemeLabel = new JLabel();
    private final JLabel languageLabel = new JLabel();
    private final JLabel logLabel = new JLabel();
    private final JLabel checkNewVersionLabel = new JLabel();
    private final JLabel loadDefaultEnvLabel = new JLabel();
    private final JLabel defaultDirLabel = new JLabel();
    private final JLabel thumbnailsLibrary = new JLabel();
    private Configuration config;

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private static final String PLUGIN_VERSION = "0.0.9e";
    
/**
 * Constructor
 *
 */    
    public JSettingsPanel() {
        super();
        initialize();
    }

    private void initialize() {     
    	config = Configuration.getInstance();
    	fileChooser = new JFileChooser(config.getDefaultWorkingDir());
        setPanelIcon("/images/settings.png");
        setPreferredSize(new Dimension(400,470));

        settingsLayout = new SpringLayout();
        setLayout(settingsLayout);
		
		settingsOptionsPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        grayBorderSettingsLayout = new SpringLayout();
        settingsOptionsPanel.setLayout(grayBorderSettingsLayout);
        add(settingsOptionsPanel);
		
		themeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Look and feel:"));
        settingsOptionsPanel.add(themeLabel);
		
		subThemeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Theme:"));
        settingsOptionsPanel.add(subThemeLabel);
		
		languageLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Language:"));
        settingsOptionsPanel.add(languageLabel);

		logLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Log level:"));
        settingsOptionsPanel.add(logLabel);

        checkNewVersionLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Check for updates:"));
        settingsOptionsPanel.add(checkNewVersionLabel);
        
		loadDefaultEnvLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Load default environment at startup:"));
        settingsOptionsPanel.add(loadDefaultEnvLabel);
        
        defaultDirLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Default working directory:"));
        settingsOptionsPanel.add(defaultDirLabel);
               
        thumbnailsLibrary.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Thumbnails generator:"));
        settingsOptionsPanel.add(thumbnailsLibrary);

        try{
        	loadDefaultEnv= new JTextField();
        	loadDefaultEnv.setText(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/defaultjob"));
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error getting default environment."), e);
	    }        
        loadDefaultEnv.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(loadDefaultEnv);
		
        defaultDirectory= new JTextField();
        defaultDirectory.setText(config.getDefaultWorkingDir());
        defaultDirectory.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(defaultDirectory);

//JCOMBO
        languageCombo = new JComboBox(loadLanguages().toArray());
    	languageCombo.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(languageCombo);
        try{
	 		for (int i=0; i<languageCombo.getItemCount(); i++){
	   			if (((StringItem)languageCombo.getItemAt(i)).getId().equals(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/i18n"))){
	   				languageCombo.setSelectedItem(languageCombo.getItemAt(i));
	             break;
	   			}
	 		}

        }catch (Exception e){
        	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
        }
        
        comboLaf = new JComboBox(ThemeUtility.getLAFList().toArray());
        comboLaf.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(comboLaf);
        try{
	 		for (int i=0; i<comboLaf.getItemCount(); i++){
	   			if (((StringItem)comboLaf.getItemAt(i)).getId().equals(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"))){
	   			 comboLaf.setSelectedItem(comboLaf.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
	    }       
	    
        comboTheme = new JComboBox(ThemeUtility.getThemeList().toArray());
        comboTheme.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(comboTheme);
        try{
        	for (int i=0; i<comboTheme.getItemCount(); i++){
	            if (((StringItem)comboTheme.getItemAt(i)).getId().equals(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme"))){
	                comboTheme.setSelectedItem(comboTheme.getItemAt(i));
	                break;
	            }
        	}
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e); 
	    }       
        if (!ThemeUtility.isPlastic(Integer.parseInt(((StringItem)(comboLaf.getSelectedItem())).getId()))){
            comboTheme.setEnabled(false);
        }
        comboLaf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    if (!(ThemeUtility.isPlastic(Integer.parseInt(((StringItem)comboLaf.getSelectedItem()).getId())))){
                        comboTheme.setEnabled(false);
                    }
                    else{
                        comboTheme.setEnabled(true);
                    }
                }
                catch(Exception exc){
                    comboTheme.setFocusable(false);
                }
            }
        }); 
       
        //log levels
	    comboLog = new JComboBox(loadLogLevels().toArray());
        comboLog.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(comboLog);
        try{
	 		for (int i=0; i<comboLog.getItemCount(); i++){
	   			if (((StringItem)comboLog.getItemAt(i)).getId().equals(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/loglevel"))){
	   			 comboLog.setSelectedItem(comboLog.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
	    }  	    	  

	    //thumb library
	    thumbLibraryCombo = new JComboBox(loadThumblibraries().toArray());
	    thumbLibraryCombo.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(thumbLibraryCombo);
        try{
	 		for (int i=0; i<thumbLibraryCombo.getItemCount(); i++){
	   			if (((StringItem)thumbLibraryCombo.getItemAt(i)).getId().equals(config.getThumbnailsGeneratorLibrary())){
	   				thumbLibraryCombo.setSelectedItem(thumbLibraryCombo.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
	    } 
	    
	    comboCheckNewVersion = new JComboBox(UpdatesUtility.getCheckNewVersionItems().toArray());
	    comboCheckNewVersion.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settingsOptionsPanel.add(comboCheckNewVersion);
        try{	 		
   			if (config.isCheckForUpdates()){
   				comboCheckNewVersion.setSelectedItem(comboCheckNewVersion.getItemAt(1));
   			}	 		
	    }catch (Exception e){
	    	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
	    }        	    
//END_JCOMBO
	    
	    playSounds = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),"Play sound notifications"));
	    playSounds.setSelected(config.isPlaySounds());
	    
//ENV_LABEL_PREFIX       
        String helpTextEnv = 
    		"<html><body><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Settings ")+"</b><ul>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Language")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set your preferred language (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Look and feel")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set your preferred look and feel and your preferred theme (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Log level")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set a log detail level (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Thumbnails quality")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set the thumbnails quality")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Check for updates")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Set when new version availability should be checked (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Play sounds")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Play sound notifications")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Default env.")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Select a previously saved env. file that will be automatically loaded at startup")+".</li>" +
    		"<li><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Default working directory")+":</b> "+GettextResource.gettext(config.getI18nResourceBundle(),"Select a directory where documents will be saved and loaded by default")+".</li>" +
    		"</ul></body></html>";
	    envHelpLabel = new JHelpLabel(helpTextEnv, true);
	    settingsOptionsPanel.add(envHelpLabel);
//ENV_LABEL_PREFIX        
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileChooser.setFileFilter(new XmlFilter());
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (fileChooser.showOpenDialog(browseButton.getParent()) == JFileChooser.APPROVE_OPTION){
                    if (fileChooser.getSelectedFile() != null){
                        try{
                        	loadDefaultEnv.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                        catch (Exception ex){
                        	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                       
                        }
                    }
                }              
            }
        });         
        settingsOptionsPanel.add(browseButton);
        
        browseDestDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	fileChooser.setFileFilter(null);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showOpenDialog(browseButton.getParent()) == JFileChooser.APPROVE_OPTION){
                    if (fileChooser.getSelectedFile() != null){
                        try{
                        	defaultDirectory.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        }
                        catch (Exception ex){
                        	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), ex);                       
                        }
                    }
                }              
            }
        });         
        settingsOptionsPanel.add(browseDestDirButton);

        checkNowButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Check now"));
        checkNowButton.setMargin(new Insets(2, 2, 2, 2));       
        settingsOptionsPanel.add(checkNowButton);
        
        saveButton.setIcon(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
        saveButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Save"));
        saveButton.setMargin(new Insets(2, 2, 2, 2));
		saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				try{
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/i18n", ((StringItem)languageCombo.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF", ((StringItem)comboLaf.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/lookAndfeel/theme", ((StringItem)comboTheme.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/info/version", GuiClient.getApplicationName()+" "+GuiClient.getVersion());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/loglevel", ((StringItem)comboLog.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/thumbnailsgenerator", ((StringItem)thumbLibraryCombo.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/checkupdates", ((StringItem)comboCheckNewVersion.getSelectedItem()).getId());
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/playsounds", (playSounds.isSelected()?"1":"0"));
					if (loadDefaultEnv != null){
						config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/defaultjob", loadDefaultEnv.getText());
					}else{
						config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/defaultjob","");
					}
					config.getXmlConfigObject().setXMLConfigValue("/pdfsam/settings/default_working_dir", defaultDirectory.getText());
					config.getXmlConfigObject().saveXMLfile();
					config.setThumbnailsGeneratorLibrary(((StringItem)thumbLibraryCombo.getSelectedItem()).getId());
					log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Configuration saved."));
				}
                catch (Exception ex){
                    log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "),ex);                       
                }
			}
        }); 
		add(saveButton);		
		
//ENTER_KEY_LISTENERS
        browseDestDirButton.addKeyListener(browseDestDirEnterKeyListener);
        browseButton.addKeyListener(browseEnterKeyListener);
        saveButton.addKeyListener(saveEnterKeyListener);
//END_ENTER_KEY_LISTENERS
		
		
        setLayout();
    }

    /**
     * Loads the available languages
     */
    @SuppressWarnings("unchecked")
	private Vector<StringItem> loadLanguages(){
    	Vector<StringItem> langs = new Vector<StringItem>(40,5);
    	try{
			Document document = XMLParser.parseXmlFile(this.getClass().getResource("/org/pdfsam/i18n/languages.xml"));
			List<Node> nodeList = document.selectNodes("/languages/language");
			for (int i = 0; nodeList != null && i < nodeList.size(); i++){ 
				Node langNode  =((Node) nodeList.get(i));
				if (langNode != null){
					langs.add(new StringItem(langNode.selectSingleNode("@value").getText(), langNode.selectSingleNode("@description").getText()));
				}
			}
		}catch(Exception e){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
			langs.add(new StringItem("en_GB", "English (UK)"));
		}
		Collections.sort(langs);
		return langs;
    }
    
    /**
     * Loads the available log levels
     * @return logs level list
     */
    private Vector<StringItem> loadLogLevels(){
    	Vector<StringItem> logs = new Vector<StringItem>(10,5);
    	logs.add(new StringItem(Integer.toString(Level.DEBUG_INT), Level.DEBUG.toString()));
    	logs.add(new StringItem(Integer.toString(Level.INFO_INT), Level.INFO.toString()));
    	logs.add(new StringItem(Integer.toString(Level.WARN_INT), Level.WARN.toString()));
    	logs.add(new StringItem(Integer.toString(Level.ERROR_INT), Level.ERROR.toString()));
    	logs.add(new StringItem(Integer.toString(Level.FATAL_INT), Level.FATAL.toString()));
    	logs.add(new StringItem(Integer.toString(Level.OFF_INT), Level.OFF.toString()));
		return logs;
    }   
   
    /**
     * 
     * @return generators list
     */
    private Vector<StringItem> loadThumblibraries(){
    	Vector<StringItem> logs = new Vector<StringItem>(2,2);
    	logs.add(new StringItem(Configuration.JPEDAL, GettextResource.gettext(config.getI18nResourceBundle(),"IDRsolutions JPedal")));
    	logs.add(new StringItem(Configuration.JPOD, GettextResource.gettext(config.getI18nResourceBundle(),"Intarsys JPod")));
		return logs;
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
        return GettextResource.gettext(config.getI18nResourceBundle(),"Settings");
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return PLUGIN_VERSION;
    }  
    
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
//      LAYOUT
        settingsLayout.putConstraint(SpringLayout.SOUTH, settingsOptionsPanel, 300, SpringLayout.NORTH, this);
        settingsLayout.putConstraint(SpringLayout.EAST, settingsOptionsPanel, -5, SpringLayout.EAST, this);
        settingsLayout.putConstraint(SpringLayout.NORTH, settingsOptionsPanel, 5, SpringLayout.NORTH, this);
        settingsLayout.putConstraint(SpringLayout.WEST, settingsOptionsPanel, 5, SpringLayout.WEST, this);

        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, languageLabel, 40, SpringLayout.NORTH, this);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, languageLabel, 120, SpringLayout.WEST, this);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, languageLabel, 20, SpringLayout.NORTH, this);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, languageLabel, 5, SpringLayout.WEST, this);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, languageCombo, 0, SpringLayout.SOUTH, languageLabel);
     //   grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, languageCombo, 120, SpringLayout.WEST, languageCombo);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, languageCombo, 0, SpringLayout.NORTH, languageLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, languageCombo, 5, SpringLayout.EAST, languageLabel);
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, themeLabel, 20, SpringLayout.NORTH, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, themeLabel, 0, SpringLayout.EAST, languageLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, themeLabel, 5, SpringLayout.SOUTH, languageLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, themeLabel, 0, SpringLayout.WEST, languageLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, comboLaf, 0, SpringLayout.SOUTH, themeLabel);
   //     grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, comboLaf, 120, SpringLayout.WEST, comboLaf);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, comboLaf, 0, SpringLayout.NORTH, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, comboLaf, 5, SpringLayout.EAST, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, subThemeLabel, 0, SpringLayout.SOUTH, comboLaf);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, subThemeLabel, 90, SpringLayout.WEST, subThemeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, subThemeLabel, 0, SpringLayout.NORTH, comboLaf);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, subThemeLabel, 5, SpringLayout.EAST, comboLaf);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, comboTheme, 0, SpringLayout.SOUTH, subThemeLabel);
  //      grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, comboTheme, 120, SpringLayout.WEST, comboTheme);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, comboTheme, 0, SpringLayout.NORTH, subThemeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, comboTheme, 5, SpringLayout.EAST, subThemeLabel);
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, logLabel, 20, SpringLayout.NORTH, logLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, logLabel, 0, SpringLayout.EAST, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, logLabel, 5, SpringLayout.SOUTH, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, logLabel, 0, SpringLayout.WEST, themeLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, comboLog, 0, SpringLayout.SOUTH, logLabel);
      //  grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, comboLog, 0, SpringLayout.EAST, comboLaf);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, comboLog, 0, SpringLayout.NORTH, logLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, comboLog, 0, SpringLayout.WEST, comboLaf);
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, thumbnailsLibrary, 20, SpringLayout.NORTH, thumbnailsLibrary);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, thumbnailsLibrary, 0, SpringLayout.EAST, logLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, thumbnailsLibrary, 5, SpringLayout.SOUTH, logLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, thumbnailsLibrary, 0, SpringLayout.WEST, logLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, thumbLibraryCombo, 0, SpringLayout.SOUTH, thumbnailsLibrary);
  //      grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, thumbQualityCombo, 0, SpringLayout.EAST, comboLog);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, thumbLibraryCombo, 0, SpringLayout.NORTH, thumbnailsLibrary);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, thumbLibraryCombo, 0, SpringLayout.WEST, comboLog);

        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, checkNewVersionLabel, 20, SpringLayout.NORTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, checkNewVersionLabel, 0, SpringLayout.EAST, thumbnailsLibrary);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, checkNewVersionLabel, 5, SpringLayout.SOUTH, thumbnailsLibrary);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, checkNewVersionLabel, 0, SpringLayout.WEST, thumbnailsLibrary);       
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, comboCheckNewVersion, 0, SpringLayout.SOUTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, comboCheckNewVersion, 120, SpringLayout.WEST, comboCheckNewVersion);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, comboCheckNewVersion, 0, SpringLayout.NORTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, comboCheckNewVersion, 5, SpringLayout.EAST, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, checkNowButton, 0, SpringLayout.SOUTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, checkNowButton, 0, SpringLayout.EAST, comboTheme);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, checkNowButton, 0, SpringLayout.NORTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, checkNowButton, 0, SpringLayout.WEST, comboTheme);

        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, playSounds, 20, SpringLayout.NORTH, playSounds);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, playSounds, 5, SpringLayout.SOUTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, playSounds, 0, SpringLayout.WEST, checkNewVersionLabel); 
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, loadDefaultEnvLabel, 20, SpringLayout.NORTH, loadDefaultEnvLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, loadDefaultEnvLabel, -5, SpringLayout.EAST, settingsOptionsPanel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, loadDefaultEnvLabel, 5, SpringLayout.SOUTH, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, loadDefaultEnvLabel, 0, SpringLayout.WEST, checkNewVersionLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, loadDefaultEnv, 20, SpringLayout.NORTH, loadDefaultEnv);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, loadDefaultEnv, -100, SpringLayout.EAST, settingsOptionsPanel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, loadDefaultEnv, 5, SpringLayout.SOUTH, loadDefaultEnvLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, loadDefaultEnv, 0, SpringLayout.WEST, loadDefaultEnvLabel);
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, browseButton, -5, SpringLayout.EAST, settingsOptionsPanel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH, loadDefaultEnv);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, browseButton, -90, SpringLayout.EAST, settingsOptionsPanel);        

        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, defaultDirLabel, 20, SpringLayout.NORTH, defaultDirLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, defaultDirLabel, 0, SpringLayout.EAST, loadDefaultEnvLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, defaultDirLabel, 5, SpringLayout.SOUTH, loadDefaultEnv);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, defaultDirLabel, 0, SpringLayout.WEST, loadDefaultEnvLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, defaultDirectory, 20, SpringLayout.NORTH, defaultDirectory);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, defaultDirectory, 0, SpringLayout.EAST, loadDefaultEnv);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, defaultDirectory, 5, SpringLayout.SOUTH, defaultDirLabel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, defaultDirectory, 0, SpringLayout.WEST, loadDefaultEnv);
        
        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, browseDestDirButton, 25, SpringLayout.NORTH, browseDestDirButton);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, browseDestDirButton, 0, SpringLayout.EAST, browseButton);
        grayBorderSettingsLayout.putConstraint(SpringLayout.NORTH, browseDestDirButton, 0, SpringLayout.NORTH, defaultDirectory);
        grayBorderSettingsLayout.putConstraint(SpringLayout.WEST, browseDestDirButton, 0, SpringLayout.WEST, browseButton);        

        grayBorderSettingsLayout.putConstraint(SpringLayout.SOUTH, envHelpLabel, -1, SpringLayout.SOUTH, settingsOptionsPanel);
        grayBorderSettingsLayout.putConstraint(SpringLayout.EAST, envHelpLabel, -1, SpringLayout.EAST, settingsOptionsPanel);
		
        settingsLayout.putConstraint(SpringLayout.SOUTH, saveButton, 25, SpringLayout.NORTH, saveButton);
        settingsLayout.putConstraint(SpringLayout.EAST, saveButton, -10, SpringLayout.EAST, this);
        settingsLayout.putConstraint(SpringLayout.NORTH, saveButton, 5, SpringLayout.SOUTH, settingsOptionsPanel);
        settingsLayout.putConstraint(SpringLayout.WEST, saveButton, -95, SpringLayout.EAST, this);

    }    
    
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)settingsFocusPolicy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for split panel
     *
     */
    public class SettingsFocusPolicy extends FocusTraversalPolicy {
        public SettingsFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(languageCombo)){
                return comboLaf;
            }
            else if (aComponent.equals(comboLaf)){
                return comboTheme;
            }
            else if (aComponent.equals(comboTheme)){
                return comboLog;
            }
            else if (aComponent.equals(comboLog)){
                return thumbLibraryCombo;
            }                        
            else if (aComponent.equals(thumbLibraryCombo)){
                return comboCheckNewVersion;
            }                        
            else if (aComponent.equals(comboCheckNewVersion)){
                return checkNowButton;
            }
            else if (aComponent.equals(checkNowButton)){
                return playSounds;
            }
            else if (aComponent.equals(playSounds)){
                return loadDefaultEnv;
            }
            else if (aComponent.equals(loadDefaultEnv)){
                    return browseButton;
            }        
            else if (aComponent.equals(browseButton)){
                return defaultDirectory;
            }        
            else if (aComponent.equals(defaultDirectory)){
                return browseDestDirButton;
            }        
            else if (aComponent.equals(browseDestDirButton)){
                return saveButton;
            }
            else if (aComponent.equals(saveButton)){
                return languageCombo;
            }
            return languageCombo;
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(browseButton)){
                return loadDefaultEnv;
            }
            else if (aComponent.equals(loadDefaultEnv)){
                return playSounds;
            }
            else if (aComponent.equals(playSounds)){
                return checkNowButton;
            }
            else if (aComponent.equals(checkNowButton)){
                return comboCheckNewVersion;
            }
            else if (aComponent.equals(comboCheckNewVersion)){
                return thumbLibraryCombo;
            }
            else if (aComponent.equals(thumbLibraryCombo)){
                return comboLog;
            }            
            else if (aComponent.equals(comboLog)){
                return comboTheme;
            }
            else if (aComponent.equals(comboTheme)){
                    return comboLaf;
            }
            else if (aComponent.equals(comboLaf)){
                return languageCombo;
            }
            else if (aComponent.equals(saveButton)){
                return browseDestDirButton;
            }
            else if (aComponent.equals(browseDestDirButton)){
                return defaultDirectory;
            }
            else if (aComponent.equals(defaultDirectory)){
                return browseButton;
            }
            else if (aComponent.equals(languageCombo)){
                return saveButton;
            }
            return languageCombo;
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return languageCombo;
        }

        public Component getLastComponent(Container CycleRootComp){
            return browseButton;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return languageCombo;
        }
    }


	public org.dom4j.Node getJobNode(org.dom4j.Node arg0, boolean savePasswords) throws SaveJobException {
		return arg0;
	}

	public void loadJobNode(org.dom4j.Node arg0) throws LoadJobException {
		log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Unimplemented method for JSettingsPanel"));
	}

	public void resetPanel() {
		
	}
	
	/**
	 * sets the check update mediator
	 * @param updateCheckerMediator
	 */
	public void setCheckUpdateMediator(UpdateCheckerMediator updateCheckerMediator){
		checkNowButton.addActionListener(updateCheckerMediator);
	}
}

