/*
 * Created on 07-Feb-2006
 * About panel. It shows informations about the application.
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

import it.pdfsam.GUI.MainGUI;
import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.model.PluginsModel;
import it.pdfsam.util.XMLConfig;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
/**
 * "About" window. It shows informations about the software
 * @author Andrea Vacondio
 */
public class JInfoPanel extends AbstractPlugIn{   

	private static final long serialVersionUID = 1239951345347468850L;

    private JTable table_plugins;
    private JTextPane text_info_area;
    private String author = "";
    private String version = "";
    private String app_name = "";
    private String language = "";
    private String build_date = "";
    private XMLConfig xml_config_object;
    private ArrayList data = new ArrayList();
    private ResourceBundle i18n_messages;
    private Configuration config;
    
    private final InfoFocusPolicy info_focus_policy = new InfoFocusPolicy();
    
    private final static String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final static String PLUGIN_NAME = "About";
    private final static String PLUGIN_VERSION = "0.0.3e";

    
    /**
     * Constructor. It provides initialization.
     * @param table_data Informations about loaded plugins
     */
    public JInfoPanel(ArrayList table_data) {
        super();
        if(table_data != null){
        	this.data = table_data;
        }
        initialize();
    }

    private void initialize() {
    	setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	setPanelIcon("/images/info.png");
    	config = Configuration.getInstance();
        i18n_messages = config.getI18nResourceBundle();
        xml_config_object = config.getXmlConfigObject();
    	 try{
             author = MainGUI.AUTHOR;
             version = MainGUI.APP_VERSION;
             app_name = xml_config_object.getXMLConfigValue("/pdfsam/info/name");
             language = xml_config_object.getXMLConfigValue("/pdfsam/info/language");
             build_date = xml_config_object.getXMLConfigValue("/pdfsam/info/build_date");
         }catch(Exception e){
        	 fireLogPropertyChanged("Error: "+e.getMessage(), LogPanel.LOG_ERROR);  
         }
         
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//TEXT_AREA        
        final JScrollPane text_info_panel = new JScrollPane();
        text_info_panel.setPreferredSize(new Dimension(300, 100));

        text_info_area = new JTextPane();
        text_info_area.setFont(new Font("Dialog", Font.PLAIN, 9));
        text_info_area.setPreferredSize(new Dimension(300,100));
        text_info_area.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        text_info_area.setContentType("text/html");   
        text_info_area.setText("<html><head></head><body>"+app_name+"<br><br>"+GettextResource.gettext(i18n_messages,"Version: ")+version+"<br>"+GettextResource.gettext(i18n_messages,"Language: ")+language+"<br>Author: "+author+"<br>"+GettextResource.gettext(i18n_messages,"Build date: ")+build_date+"<br>"+GettextResource.gettext(i18n_messages,"Website: ")+"<a href=\"http://www.pdfsam.org\" title=\"pdfsam\">http://www.pdfsam.org</a>"+"<br><br>"+getThanksText()+"</body></html>");
        text_info_area.setEditable(false);
        text_info_panel.setViewportView(text_info_area);

//END_TEXT_AREA
//TABLE_PLUGS        
        final JScrollPane scroll_panel_installed_plugins = new JScrollPane();
        scroll_panel_installed_plugins.setPreferredSize(new Dimension(300,100));

        table_plugins = new JTable();
        PluginsModel table_plugins_model = new PluginsModel(data);
        String[] i18n_column_names = {GettextResource.gettext(i18n_messages,"Name"),GettextResource.gettext(i18n_messages,"Version"),GettextResource.gettext(i18n_messages,"Author")};
        table_plugins_model.setColumnNames(i18n_column_names);
        table_plugins.setModel(table_plugins_model);
        table_plugins.setGridColor(Color.LIGHT_GRAY);
        table_plugins.setFocusable(false);
        table_plugins.setRowSelectionAllowed(false);
        table_plugins.setIntercellSpacing(new Dimension(2, 2));
        table_plugins.setBorder(new EtchedBorder(EtchedBorder.LOWERED));      
        scroll_panel_installed_plugins.setViewportView(table_plugins);
//END_TABLE_PLUGS
        add(text_info_panel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(scroll_panel_installed_plugins);
//END_THANKS_TEXT_AREA                
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
    
    public org.dom4j.Node getJobNode(org.dom4j.Node arg0) throws SaveJobException {
		return arg0;
	}

	public void loadJobNode(org.dom4j.Node arg0) throws LoadJobException {
		fireLogPropertyChanged("Unimplemented method for JInfoPanel" , LogPanel.LOG_DEBUG);
	}

    protected String getThanksText(){
        String[] translators = new String[]{"Nicolas Le Novere (donor)", "Elisa Bortolotti (donor)", "Robin de Groot (donor)", "wonder (tester/developer)", "Aniket Dutta (contributor)"};
        String log_text = GettextResource.gettext(i18n_messages,"Contributes: ");
        for (int i=0; i<translators.length; i++){
            log_text += translators[i]+" - ";
        }
        return log_text;
    }
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)info_focus_policy;
        
    }
  
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for split panel
     *
     */
    public class InfoFocusPolicy extends FocusTraversalPolicy {
        public InfoFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(text_info_area)){
                return text_info_area;
            }
            return text_info_area;
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(text_info_area)){
                return text_info_area;
            }
            return text_info_area;
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return text_info_area;
        }

        public Component getLastComponent(Container CycleRootComp){
            return text_info_area;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return text_info_area;
        }
    }

}
