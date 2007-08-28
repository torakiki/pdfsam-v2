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

import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.interfaces.PlugablePanel;
import it.pdfsam.listeners.EnterDoClickListener;
import it.pdfsam.renders.JComboListItemRender;
import it.pdfsam.types.ListItem;
import it.pdfsam.utils.ThemeSelector;
import it.pdfsam.utils.XMLConfig;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

/**
 * Plugable JPanel provides a GUI for split functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class JSettingsPanel extends JPanel implements PlugablePanel{        


	private static final long serialVersionUID = -5511002370888344748L;
	private XMLConfig xml_config_object;
    private JComboBox language_combo;	
    private JComboBox combo_laf;
    private JComboBox combo_theme;
    private JHelpLabel help_label;
    private ResourceBundle i18n_messages;
    private SpringLayout s_panel_layout;
    private String log_msg;
    private String log_color;
    private SpringLayout set_panel_layout;
    private JPanel set_panel = new JPanel();  
    private final JButton save_button = new JButton();

    private final EnterDoClickListener save_enterkey_listener = new EnterDoClickListener(save_button);

//  focus policy 
    private final SettingsFocusPolicy settings_focus_policy = new SettingsFocusPolicy();	
//labels    
    private final JLabel theme_label = new JLabel();
    private final JLabel sub_theme_label = new JLabel();
    private final JLabel language_label = new JLabel();
    private Configuration config;

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private static final String PLUGIN_NAME = "Settings";
    private static final String PLUGIN_VERSION = "0.0.1";
    
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
		
        s_panel_layout = new SpringLayout();
        setLayout(s_panel_layout);
		
//      SETTINGS_PANEL
        set_panel_layout = new SpringLayout();
        set_panel.setLayout(set_panel_layout);
        set_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(set_panel);
//SETTINGS_PANEL 
        
		theme_label.setText(GettextResource.gettext(i18n_messages,"Look and feel:"));
		set_panel.add(theme_label);
		
		sub_theme_label.setText(GettextResource.gettext(i18n_messages,"Theme:"));
		set_panel.add(sub_theme_label);
		
		language_label.setText(GettextResource.gettext(i18n_messages,"Language:"));
		set_panel.add(language_label);
        

//JCOMBO
		language_combo = new JComboBox(config.getLanguagesList().toArray());
    	language_combo.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	language_combo.setRenderer(new JComboListItemRender());
    	set_panel.add(language_combo);
        try{
	 		for (int i=0; i<language_combo.getItemCount(); i++){
	   			if (((ListItem)language_combo.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/i18n"))){
	   				language_combo.setSelectedItem(language_combo.getItemAt(i));
	             break;
	   			}
	 		}

        }catch (Exception e){
        	fireLogActionPerformed("Error: "+e.getMessage(), "FF0000"); 
        }
        
        combo_laf = new JComboBox(ThemeSelector.getLAFList().toArray());
        combo_laf.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        combo_laf.setRenderer(new JComboListItemRender());
        set_panel.add(combo_laf);
        try{
	 		for (int i=0; i<combo_laf.getItemCount(); i++){
	   			if (((ListItem)combo_laf.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"))){
	   			 combo_laf.setSelectedItem(combo_laf.getItemAt(i));
	             break;
	   			}
	 		}
	    }catch (Exception e){
	    	fireLogActionPerformed("Error: "+e.getMessage(), "FF0000"); 
	    }       
	    
        combo_theme = new JComboBox(ThemeSelector.getThemeList().toArray());
        combo_theme.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        combo_theme.setRenderer(new JComboListItemRender());
        set_panel.add(combo_theme);
        try{
        	for (int i=0; i<combo_theme.getItemCount(); i++){
	            if (((ListItem)combo_theme.getItemAt(i)).getId().equals(xml_config_object.getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme"))){
	                combo_theme.setSelectedItem(combo_theme.getItemAt(i));
	                break;
	            }
        	}
	    }catch (Exception e){
	    	fireLogActionPerformed("Error: "+e.getMessage(), "FF0000"); 
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
       
//LABEL_PREFIX       
        String helpText = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Settings ")+"</b><ul>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Language")+":</b> "+GettextResource.gettext(i18n_messages,"Set your preferred language (restart needed)")+".</li>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Look and feel")+":</b> "+GettextResource.gettext(i18n_messages,"Set your preferred look and feel and your preferred theme (restart needed)")+".</li>" +
    		"</ul></body></html>";
	    help_label = new JHelpLabel(helpText, true);
	    set_panel.add(help_label);
//LABEL_PREFIX 	        
//END_JCOMBO
        save_button.setIcon(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
        save_button.setText(GettextResource.gettext(i18n_messages,"Save"));
        save_button.setMargin(new Insets(2, 2, 2, 2));
		save_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				try{
					xml_config_object.setXMLConfigValue("/pdfsam/settings/i18n", ((ListItem)language_combo.getSelectedItem()).getId());
					xml_config_object.setXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF", ((ListItem)combo_laf.getSelectedItem()).getId());
					xml_config_object.setXMLConfigValue("/pdfsam/settings/lookAndfeel/theme", ((ListItem)combo_theme.getSelectedItem()).getId());
					xml_config_object.saveXMLfile();
					fireLogActionPerformed("Configuration saved.", "000000");
					}
                catch (Exception ex){
                	fireLogActionPerformed("Error: "+ex.getMessage(), "FF0000");                         
                }
			}
        }); 
		add(save_button);		
		
//ENTER_KEY_LISTENERS
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
    	s_panel_layout.putConstraint(SpringLayout.SOUTH, set_panel, 135, SpringLayout.NORTH, this);
    	s_panel_layout.putConstraint(SpringLayout.EAST, set_panel, -5, SpringLayout.EAST, this);
    	s_panel_layout.putConstraint(SpringLayout.NORTH, set_panel, 5, SpringLayout.NORTH, this);
    	s_panel_layout.putConstraint(SpringLayout.WEST, set_panel, 5, SpringLayout.WEST, this);
        
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, language_label, 40, SpringLayout.NORTH, this);
    	set_panel_layout.putConstraint(SpringLayout.EAST, language_label, 100, SpringLayout.WEST, this);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, language_label, 20, SpringLayout.NORTH, this);
    	set_panel_layout.putConstraint(SpringLayout.WEST, language_label, 5, SpringLayout.WEST, this);
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, language_combo, 0, SpringLayout.SOUTH, language_label);
    	set_panel_layout.putConstraint(SpringLayout.EAST, language_combo, 120, SpringLayout.WEST, language_combo);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, language_combo, 0, SpringLayout.NORTH, language_label);
    	set_panel_layout.putConstraint(SpringLayout.WEST, language_combo, 5, SpringLayout.EAST, language_label);
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, theme_label, 20, SpringLayout.NORTH, theme_label);
    	set_panel_layout.putConstraint(SpringLayout.EAST, theme_label, 0, SpringLayout.EAST, language_label);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, theme_label, 5, SpringLayout.SOUTH, language_label);
    	set_panel_layout.putConstraint(SpringLayout.WEST, theme_label, 0, SpringLayout.WEST, language_label);
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, combo_laf, 0, SpringLayout.SOUTH, theme_label);
    	set_panel_layout.putConstraint(SpringLayout.EAST, combo_laf, 120, SpringLayout.WEST, combo_laf);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, combo_laf, 0, SpringLayout.NORTH, theme_label);
    	set_panel_layout.putConstraint(SpringLayout.WEST, combo_laf, 5, SpringLayout.EAST, theme_label);
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, sub_theme_label, 0, SpringLayout.SOUTH, combo_laf);
    	set_panel_layout.putConstraint(SpringLayout.EAST, sub_theme_label, 90, SpringLayout.WEST, sub_theme_label);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, sub_theme_label, 0, SpringLayout.NORTH, combo_laf);
    	set_panel_layout.putConstraint(SpringLayout.WEST, sub_theme_label, 5, SpringLayout.EAST, combo_laf);
    	set_panel_layout.putConstraint(SpringLayout.SOUTH, combo_theme, 0, SpringLayout.SOUTH, sub_theme_label);
    	set_panel_layout.putConstraint(SpringLayout.EAST, combo_theme, 120, SpringLayout.WEST, combo_theme);
    	set_panel_layout.putConstraint(SpringLayout.NORTH, combo_theme, 0, SpringLayout.NORTH, sub_theme_label);
    	set_panel_layout.putConstraint(SpringLayout.WEST, combo_theme, 5, SpringLayout.EAST, sub_theme_label);

    	set_panel_layout.putConstraint(SpringLayout.SOUTH, help_label, -1, SpringLayout.SOUTH, set_panel);
    	set_panel_layout.putConstraint(SpringLayout.EAST, help_label, -1, SpringLayout.EAST, set_panel);
		
	
        s_panel_layout.putConstraint(SpringLayout.SOUTH, save_button, 25, SpringLayout.NORTH, save_button);
        s_panel_layout.putConstraint(SpringLayout.EAST, save_button, 0, SpringLayout.EAST, set_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, save_button, 5, SpringLayout.SOUTH, set_panel);
        s_panel_layout.putConstraint(SpringLayout.WEST, save_button, -95, SpringLayout.EAST, this);
		           
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
                return save_button;
            }
            else if (aComponent.equals(save_button)){
                return language_combo;
            }
            return language_combo;
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(save_button)){
                return combo_theme;
            }
            else if (aComponent.equals(combo_theme)){
                    return combo_laf;
            }
            else if (aComponent.equals(combo_laf)){
                return language_combo;
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
            return save_button;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return language_combo;
        }
    }

    protected void fireLogActionPerformed(String log_msg, String log_color) {
        this.log_msg = log_msg;
        this.log_color = log_color;
        this.firePropertyChange("LOG", null, "LOG_UPDATED");
    }
    
    public Icon getIcon() {
        try{
            return new ImageIcon(this.getClass().getResource("/images/settings.png"));
        }catch (Exception e){
            return null;            
        }
    }

    /**
     * @return Returns the log_color.
     */
    public String getLogColor() {
        return log_color;
    }
    /**
     * @return Returns the log_msg.
     */
    public String getLogMsg() {
        return log_msg;
    }

    /**
     * @deprecated
     */
	public void init(String language_code) {
		// TODO Auto-generated method stub
		
	}
}

