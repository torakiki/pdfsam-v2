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


package it.pdfsam.panels;

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnterDoClickListener;
import it.pdfsam.render.JComboListItemRender;
import it.pdfsam.types.ListItem;
import it.pdfsam.util.ThemeSelector;
import it.pdfsam.util.XMLConfig;
import it.pdfsam.util.XmlFilter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

/**
 * Plugable JPanel provides a GUI for split functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class JSettingsPanel extends AbstractPlugIn{        

	private static final long serialVersionUID = 3349859566062243695L;
	private XMLConfig xml_config_object;
	private JTextField load_default_job_text;
    private JComboBox language_combo;	
    private JComboBox combo_log;	
    private JComboBox combo_laf;
    private JComboBox combo_theme;
    private JHelpLabel env_help_label;
	private JFileChooser browse_file_chooser = new JFileChooser();
    private ResourceBundle i18n_messages;
    private SpringLayout settings_spring_layout;
    private SpringLayout s_panel_layout;
    
    private final JButton browse_button = new JButton();
    private final JButton save_button = new JButton();

    private final EnterDoClickListener browse_enterkey_listener = new EnterDoClickListener(browse_button);
    private final EnterDoClickListener save_enterkey_listener = new EnterDoClickListener(save_button);

	private final JPanel settings_options_panel = new JPanel();
//  focus policy 
    private final SettingsFocusPolicy settings_focus_policy = new SettingsFocusPolicy();	
//labels    
    private final JLabel theme_label = new JLabel();
    private final JLabel sub_theme_label = new JLabel();
    private final JLabel language_label = new JLabel();
    private final JLabel log_label = new JLabel();
    private final JLabel load_default_job_label = new JLabel();
    private Configuration config;

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private static final String PLUGIN_NAME = "Settings";
    private static final String PLUGIN_VERSION = "0.0.5e";
    
/**
 * Constructor
 *
 */    
    public JSettingsPanel() {
        super();
        initialize();
    }

    private void initialize() {     
    	setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	config = Configuration.getInstance();
        i18n_messages = config.getI18nResourceBundle();
        xml_config_object = config.getXmlConfigObject();
        setPanelIcon("/images/settings.png");

        settings_spring_layout = new SpringLayout();
        setLayout(settings_spring_layout);
		
		settings_options_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        s_panel_layout = new SpringLayout();
        settings_options_panel.setLayout(s_panel_layout);
        add(settings_options_panel);
		
		theme_label.setText(GettextResource.gettext(i18n_messages,"Look and feel:"));
        settings_options_panel.add(theme_label);
		
		sub_theme_label.setText(GettextResource.gettext(i18n_messages,"Theme:"));
        settings_options_panel.add(sub_theme_label);
		
		language_label.setText(GettextResource.gettext(i18n_messages,"Language:"));
        settings_options_panel.add(language_label);

		log_label.setText(GettextResource.gettext(i18n_messages,"Log level:"));
        settings_options_panel.add(log_label);

		load_default_job_label.setText(GettextResource.gettext(i18n_messages,"Load default environment at startup:"));
        settings_options_panel.add(load_default_job_label);
        
        try{
        	load_default_job_text= new JTextField();
        	load_default_job_text.setText(xml_config_object.getXMLConfigValue("/pdfsam/settings/defaultjob"));
	    }catch (Exception e){
	    	fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR); 
	    }        
        load_default_job_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        settings_options_panel.add(load_default_job_text);
		
//FILE_CHOOSER
        browse_file_chooser.setFileFilter(new XmlFilter());
        browse_file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//END_FILE_CHOOSER
//JCOMBO
    	language_combo = new JComboBox(config.getLanguagesList().toArray());
    	language_combo.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	language_combo.setRenderer(new JComboListItemRender());
        settings_options_panel.add(language_combo);
        try{
	 		for (int i=0; i<language_combo.getItemCount(); i++){
	   			if (((ListItem)language_combo.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/i18n"))){
	   				language_combo.setSelectedItem(language_combo.getItemAt(i));
	             break;
	   			}
	 		}

        }catch (Exception e){
        	fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR); 
        }
        
        combo_laf = new JComboBox(ThemeSelector.getLAFList().toArray());
        combo_laf.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        combo_laf.setRenderer(new JComboListItemRender());
        settings_options_panel.add(combo_laf);
        try{
	 		for (int i=0; i<combo_laf.getItemCount(); i++){
	   			if (((ListItem)combo_laf.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"))){
	   			 combo_laf.setSelectedItem(combo_laf.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR); 
	    }       
	    
        combo_theme = new JComboBox(ThemeSelector.getThemeList().toArray());
        combo_theme.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        combo_theme.setRenderer(new JComboListItemRender());
        settings_options_panel.add(combo_theme);
        try{
        	for (int i=0; i<combo_theme.getItemCount(); i++){
	            if (((ListItem)combo_theme.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme"))){
	                combo_theme.setSelectedItem(combo_theme.getItemAt(i));
	                break;
	            }
        	}
	    }catch (Exception e){
	    	fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR); 
	    }       
        if (!ThemeSelector.isPlastic(((ListItem)(combo_laf.getSelectedItem())).getId())){
            combo_theme.setEnabled(false);
        }
        combo_laf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    if (!(ThemeSelector.isPlastic(((ListItem)combo_laf.getSelectedItem()).getId()))){
                        combo_theme.setEnabled(false);
                    }
                    else{
                        combo_theme.setEnabled(true);
                    }
                }
                catch(Exception exc){
                    combo_theme.setFocusable(false);
                }
            }
        }); 
       
	    combo_log = new JComboBox(LogPanel.getLogList().toArray());
        combo_log.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        combo_log.setRenderer(new JComboListItemRender());
        settings_options_panel.add(combo_log);
        try{
	 		for (int i=0; i<combo_log.getItemCount(); i++){
	   			if (((ListItem)combo_log.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/loglevel"))){
	   			 combo_log.setSelectedItem(combo_log.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR); 
	    }          
//END_JCOMBO
//ENV_LABEL_PREFIX       
        String helpTextEnv = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Settings ")+"</b><ul>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Language")+":</b> "+GettextResource.gettext(i18n_messages,"Set your preferred language (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Look and feel")+":</b> "+GettextResource.gettext(i18n_messages,"Set your preferred look and feel and your preferred theme (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Log level")+":</b> "+GettextResource.gettext(i18n_messages,"Set a log detail level (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Default env.")+":</b> "+GettextResource.gettext(i18n_messages,"Select a previously saved env. file that will be automatically loaded at startup")+".</li>" +
    		"</ul></body></html>";
	    env_help_label = new JHelpLabel(helpTextEnv, true);
	    settings_options_panel.add(env_help_label);
//ENV_LABEL_PREFIX 
        browse_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
        browse_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
        browse_button.setMargin(new Insets(2, 2, 2, 2));
        browse_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int return_val = browse_file_chooser.showOpenDialog(browse_button.getParent());
                File chosen_file = null;                
                if (return_val == JFileChooser.APPROVE_OPTION){
                    chosen_file = browse_file_chooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosen_file != null){
                    try{
                    	load_default_job_text.setText(chosen_file.getAbsolutePath());
                    }
                    catch (Exception ex){
                        fireLogPropertyChanged("Error: "+ex.getMessage(), LogPanel.LOG_ERROR);                         
                    }
                }
                
            }
        });         
        settings_options_panel.add(browse_button);

        save_button.setIcon(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
        save_button.setText(GettextResource.gettext(i18n_messages,"Save"));
        save_button.setMargin(new Insets(2, 2, 2, 2));
		save_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				try{
					xml_config_object.setXMLConfigValue("/pdfsam/settings/i18n", ((ListItem)language_combo.getSelectedItem()).getId());
					xml_config_object.setXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF", ((ListItem)combo_laf.getSelectedItem()).getId());
					xml_config_object.setXMLConfigValue("/pdfsam/settings/lookAndfeel/theme", ((ListItem)combo_theme.getSelectedItem()).getId());
					xml_config_object.setXMLConfigValue("/pdfsam/settings/loglevel", ((ListItem)combo_log.getSelectedItem()).getId());
					if (load_default_job_text != null){
						xml_config_object.setXMLConfigValue("/pdfsam/settings/defaultjob", load_default_job_text.getText());
					}else{
						xml_config_object.setXMLConfigValue("/pdfsam/settings/defaultjob","");
					}
					xml_config_object.saveXMLfile();
					fireLogPropertyChanged("Configuration saved.", LogPanel.LOG_INFO);
					}
                catch (Exception ex){
                    fireLogPropertyChanged("Error: "+ex.getMessage(), LogPanel.LOG_ERROR);                         
                }
			}
        }); 
		add(save_button);		
		
//ENTER_KEY_LISTENERS
        browse_button.addKeyListener(browse_enterkey_listener);
        save_button.addKeyListener(save_enterkey_listener);
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
        return PLUGIN_NAME;
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
        settings_spring_layout.putConstraint(SpringLayout.SOUTH, settings_options_panel, 180, SpringLayout.NORTH, this);
        settings_spring_layout.putConstraint(SpringLayout.EAST, settings_options_panel, -5, SpringLayout.EAST, this);
        settings_spring_layout.putConstraint(SpringLayout.NORTH, settings_options_panel, 5, SpringLayout.NORTH, this);
        settings_spring_layout.putConstraint(SpringLayout.WEST, settings_options_panel, 5, SpringLayout.WEST, this);

        s_panel_layout.putConstraint(SpringLayout.SOUTH, language_label, 40, SpringLayout.NORTH, this);
        s_panel_layout.putConstraint(SpringLayout.EAST, language_label, 100, SpringLayout.WEST, this);
        s_panel_layout.putConstraint(SpringLayout.NORTH, language_label, 20, SpringLayout.NORTH, this);
        s_panel_layout.putConstraint(SpringLayout.WEST, language_label, 5, SpringLayout.WEST, this);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, language_combo, 0, SpringLayout.SOUTH, language_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, language_combo, 120, SpringLayout.WEST, language_combo);
        s_panel_layout.putConstraint(SpringLayout.NORTH, language_combo, 0, SpringLayout.NORTH, language_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, language_combo, 5, SpringLayout.EAST, language_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, theme_label, 20, SpringLayout.NORTH, theme_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, theme_label, 0, SpringLayout.EAST, language_label);
        s_panel_layout.putConstraint(SpringLayout.NORTH, theme_label, 5, SpringLayout.SOUTH, language_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, theme_label, 0, SpringLayout.WEST, language_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, combo_laf, 0, SpringLayout.SOUTH, theme_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, combo_laf, 120, SpringLayout.WEST, combo_laf);
        s_panel_layout.putConstraint(SpringLayout.NORTH, combo_laf, 0, SpringLayout.NORTH, theme_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, combo_laf, 5, SpringLayout.EAST, theme_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, sub_theme_label, 0, SpringLayout.SOUTH, combo_laf);
        s_panel_layout.putConstraint(SpringLayout.EAST, sub_theme_label, 90, SpringLayout.WEST, sub_theme_label);
        s_panel_layout.putConstraint(SpringLayout.NORTH, sub_theme_label, 0, SpringLayout.NORTH, combo_laf);
        s_panel_layout.putConstraint(SpringLayout.WEST, sub_theme_label, 5, SpringLayout.EAST, combo_laf);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, combo_theme, 0, SpringLayout.SOUTH, sub_theme_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, combo_theme, 120, SpringLayout.WEST, combo_theme);
        s_panel_layout.putConstraint(SpringLayout.NORTH, combo_theme, 0, SpringLayout.NORTH, sub_theme_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, combo_theme, 5, SpringLayout.EAST, sub_theme_label);
        
        s_panel_layout.putConstraint(SpringLayout.SOUTH, log_label, 20, SpringLayout.NORTH, log_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, log_label, -5, SpringLayout.EAST, settings_options_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, log_label, 5, SpringLayout.SOUTH, theme_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, log_label, 0, SpringLayout.WEST, theme_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, combo_log, 0, SpringLayout.SOUTH, log_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, combo_log, 0, SpringLayout.EAST, combo_laf);
        s_panel_layout.putConstraint(SpringLayout.NORTH, combo_log, 0, SpringLayout.NORTH, log_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, combo_log, 0, SpringLayout.WEST, combo_laf);

        s_panel_layout.putConstraint(SpringLayout.SOUTH, load_default_job_label, 20, SpringLayout.NORTH, load_default_job_label);
        s_panel_layout.putConstraint(SpringLayout.EAST, load_default_job_label, -5, SpringLayout.EAST, settings_options_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, load_default_job_label, 5, SpringLayout.SOUTH, log_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, load_default_job_label, 0, SpringLayout.WEST, log_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, load_default_job_text, 20, SpringLayout.NORTH, load_default_job_text);
        s_panel_layout.putConstraint(SpringLayout.EAST, load_default_job_text, -100, SpringLayout.EAST, settings_options_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, load_default_job_text, 5, SpringLayout.SOUTH, load_default_job_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, load_default_job_text, 0, SpringLayout.WEST, load_default_job_label);
        
        s_panel_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
        s_panel_layout.putConstraint(SpringLayout.EAST, browse_button, -5, SpringLayout.EAST, settings_options_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, load_default_job_text);
        s_panel_layout.putConstraint(SpringLayout.WEST, browse_button, -90, SpringLayout.EAST, settings_options_panel);        

        s_panel_layout.putConstraint(SpringLayout.SOUTH, env_help_label, -1, SpringLayout.SOUTH, settings_options_panel);
        s_panel_layout.putConstraint(SpringLayout.EAST, env_help_label, -1, SpringLayout.EAST, settings_options_panel);
		
        settings_spring_layout.putConstraint(SpringLayout.SOUTH, save_button, 25, SpringLayout.NORTH, save_button);
        settings_spring_layout.putConstraint(SpringLayout.EAST, save_button, -10, SpringLayout.EAST, this);
        settings_spring_layout.putConstraint(SpringLayout.NORTH, save_button, 5, SpringLayout.SOUTH, settings_options_panel);
        settings_spring_layout.putConstraint(SpringLayout.WEST, save_button, -95, SpringLayout.EAST, this);
		           
    }    
    
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)settings_focus_policy;
        
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
            if (aComponent.equals(language_combo)){
                return combo_laf;
            }
            else if (aComponent.equals(combo_laf)){
                return combo_theme;
            }
            else if (aComponent.equals(combo_theme)){
                return combo_log;
            }
            else if (aComponent.equals(combo_log)){
                return load_default_job_text;
            }
            else if (aComponent.equals(load_default_job_text)){
                    return browse_button;
            }        
            else if (aComponent.equals(browse_button)){
                return save_button;
            }
            else if (aComponent.equals(save_button)){
                return language_combo;
            }
            return language_combo;
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(browse_button)){
                return load_default_job_text;
            }
            else if (aComponent.equals(load_default_job_text)){
                return combo_log;
            }
            else if (aComponent.equals(combo_log)){
                return combo_theme;
            }
            else if (aComponent.equals(combo_theme)){
                    return combo_laf;
            }
            else if (aComponent.equals(combo_laf)){
                return language_combo;
            }
            else if (aComponent.equals(save_button)){
                return browse_button;
            }
            else if (aComponent.equals(language_combo)){
                return save_button;
            }
            return language_combo;
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return language_combo;
        }

        public Component getLastComponent(Container CycleRootComp){
            return browse_button;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return language_combo;
        }
    }


	public org.dom4j.Node getJobNode(org.dom4j.Node arg0) throws SaveJobException {
		return arg0;
	}

	public void loadJobNode(org.dom4j.Node arg0) throws LoadJobException {
		fireLogPropertyChanged("Unimplemented method for JSettingsPanel" , LogPanel.LOG_DEBUG);
	}
}

