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
package it.pdfsam.GUI;

import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.models.PluginsModel;
import it.pdfsam.utils.XMLConfig; 

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
/**
 * "About" window. It shows informations about the software
 * @author Andrea Vacondio
 */
public class InfoGUI {

    private JTextPane thanks_to_text;
    private JTable table_plugins;
    private JTextPane text_info_area;
    private SpringLayout springLayout;
    private JFrame info_frame;
    private String author = "";
    private String version = "";
    private String app_name = "";
    private String language = "";
    private String build_date = "";
    private XMLConfig xml_config_object;
    private ArrayList data;
    private ResourceBundle i18n_messages;
    private Configuration config;
    
    /**
     * Constructor. It provides initialization.
     * @param xml_obj XML config object.
     * @param table_data Informations about loaded plugins
     * @deprecated
     */
    public InfoGUI(XMLConfig xml_obj, ArrayList table_data, ResourceBundle i18n) {
        xml_config_object = xml_obj;
        data = table_data;
        i18n_messages = i18n;
        try{
            author = xml_config_object.getXMLConfigValue("adata->author->aname");
            version = xml_config_object.getXMLConfigValue("info->version");
            app_name = xml_config_object.getXMLConfigValue("info->name");
            language = xml_config_object.getXMLConfigValue("info->language");
            build_date = xml_config_object.getXMLConfigValue("info->build_date");
        }catch(Exception e){
            
        }
        initialize();
    }
    
    public InfoGUI(ArrayList table_data) {
        if(table_data != null){
        	this.data = table_data;
        }
        initialize();
    }

    private void initialize() {
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
        	 System.out.print("Error: "+e.getMessage());  
         }
        info_frame = new JFrame();
        springLayout = new SpringLayout();
        info_frame.getContentPane().setLayout(springLayout);
        info_frame.setIconImage(new ImageIcon(this.getClass().getResource("/images/pdfsam.png")).getImage());
        info_frame.setTitle("About pdfsam");
        info_frame.setResizable(false);
        info_frame.setBounds(100, 100, 449, 500);
//LABELINFO
        final JLabel label_info = new JLabel();
        label_info.setText(GettextResource.gettext(i18n_messages,"Informations:"));
        info_frame.getContentPane().add(label_info);
//END_LABELINFO
//TEXT_AREA        
        text_info_area = new JTextPane();
        text_info_area.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        text_info_area.setContentType("text/plain");        
        text_info_area.setText(GettextResource.gettext(i18n_messages,"Application name: ")+app_name+"\n"+GettextResource.gettext(i18n_messages,"Application version: ")+version+"\n"+GettextResource.gettext(i18n_messages,"Language: ")+language+"\nAuthor: "+author+"\n"+GettextResource.gettext(i18n_messages,"Build date: ")+build_date+"\n"+GettextResource.gettext(i18n_messages,"Website: ")+"http://www.pdfsam.org");
        text_info_area.setEditable(false);
        info_frame.getContentPane().add(text_info_area);
//END_TEXT_AREA
//TABLE_PLUGS        
        final JScrollPane scroll_panel_installed_plugins = new JScrollPane();
        info_frame.getContentPane().add(scroll_panel_installed_plugins);

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
//LABEL_PLUGS        
        final JLabel label_installed_plugins = new JLabel();
        label_installed_plugins.setText(GettextResource.gettext(i18n_messages,"Installed plugins:"));
        info_frame.getContentPane().add(label_installed_plugins);
//END_LABEL_PLUGS
//CLOSE_BUTTON        
        final JButton close_button = new JButton();
        close_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               hide();
            }
        });
        close_button.setText(GettextResource.gettext(i18n_messages,"Close"));
        info_frame.getContentPane().add(close_button);
//THANKS_TEXT_AREA
        final JScrollPane thanks_panel = new JScrollPane();
        thanks_panel.setMinimumSize(new Dimension(0, 0));
        thanks_to_text = new JTextPane();
        thanks_to_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        thanks_to_text.setContentType("text/html");
        thanks_to_text.setEditable(false);
        thanks_to_text.setAutoscrolls(true);
        thanks_panel.setViewportView(thanks_to_text);
        setThanksText();
        info_frame.getContentPane().add(thanks_panel);
//END_THANKS_TEXT_AREA        
//END_CLOSE_BUTTON        
//LAYOUT        
        springLayout.putConstraint(SpringLayout.SOUTH, text_info_area, 125, SpringLayout.NORTH, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, text_info_area, -5, SpringLayout.EAST, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, text_info_area, 25, SpringLayout.NORTH, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, text_info_area, 5, SpringLayout.WEST, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, scroll_panel_installed_plugins, 285, SpringLayout.NORTH, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, scroll_panel_installed_plugins, 433, SpringLayout.WEST, text_info_area);
        springLayout.putConstraint(SpringLayout.NORTH, scroll_panel_installed_plugins, 150, SpringLayout.NORTH, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, scroll_panel_installed_plugins, 0, SpringLayout.WEST, text_info_area);
        springLayout.putConstraint(SpringLayout.SOUTH, label_installed_plugins, -5, SpringLayout.NORTH, scroll_panel_installed_plugins);
        springLayout.putConstraint(SpringLayout.WEST, label_installed_plugins, 0, SpringLayout.WEST, text_info_area);
        springLayout.putConstraint(SpringLayout.EAST, close_button, 0, SpringLayout.EAST, scroll_panel_installed_plugins);
        springLayout.putConstraint(SpringLayout.NORTH, close_button, 10, SpringLayout.SOUTH, thanks_panel);
        springLayout.putConstraint(SpringLayout.SOUTH, label_info, -5, SpringLayout.NORTH, text_info_area);
        springLayout.putConstraint(SpringLayout.WEST, label_info, 0, SpringLayout.WEST, text_info_area);
        springLayout.putConstraint(SpringLayout.SOUTH, thanks_panel, 130, SpringLayout.NORTH, thanks_panel);
        springLayout.putConstraint(SpringLayout.EAST, thanks_panel, -5, SpringLayout.EAST, info_frame.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, thanks_panel, 10, SpringLayout.SOUTH, scroll_panel_installed_plugins);
        springLayout.putConstraint(SpringLayout.WEST, thanks_panel, 5, SpringLayout.WEST, info_frame.getContentPane());

        
    }
 /**
  * Shows the frame
  *
  */   
    public void show(){
        info_frame.setVisible(true);
    }
 /**
  * Hides the frame
  *
  */   
    public void hide(){
        info_frame.setVisible(false);
    }

    protected void setThanksText(){
        String[] contributors = new String[]{"SourceForge", "Freshmeat", "Launchpad", "Rosetta translators", "Ubuntu", "iText", "GNU", "OpenOffice", "jcmdline", "JGoodies", "David Vignoni", "Eclipse", "Xenoage Software", "Elisa Bortolotti", "dom4j", "jaxen", "All the donors and contributors"};
        String log_text = "<font size=+1>Thanks to</font><br>";
        for (int i=0; i<contributors.length; i++){
            log_text += contributors[i]+"<br>";
        }
        thanks_to_text.setText("<html><head></head><body><center>"+log_text+"</center></body></html>");
    }
}
