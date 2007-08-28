/*
 * Created on 03-Feb-2006
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
import it.pdfsam.interfaces.PlugablePanel;
import it.pdfsam.panels.JSettingsPanel;
import it.pdfsam.types.ListItem;
import it.pdfsam.utils.LanguageLoader;
import it.pdfsam.utils.PlugInsLoader;
import it.pdfsam.utils.ThemeSelector;
import it.pdfsam.utils.XMLConfig;
import it.pdfsam.utils.XMLParser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.Document;
import org.dom4j.Node;

/**
 * Main GUI
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class MainGUI implements PropertyChangeListener {

    private JTextPane log_text_area;
    private SpringLayout springLayout;
    private JFrame main_gui;
    private ThemeSelector theme_sel;
    private String look_and_feel;
    private String log_text = "";
    private InfoGUI info_panel;
    public String application_path;
    private String language;
    private JPanel[] pl_panel;
    private ResourceBundle i18n_messages;
    private Configuration config;
   
    //consts
    public static final String AUTHOR = "Andrea Vacondio";
    public static final String NAME = "PDF Split and Merge";
    public static final String APP_VERSION = "0.7 stable release 1"; 
    
    public static void main(String args[]) {
        MainGUI window = new MainGUI();        
        try {
            window.main_gui.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add input string to the log console
     * @param logmessage Log message to show in the log console
     */
    protected void addLogText(String logmessage){
        log_text += logmessage+"<br>";
        log_text_area.setText("<html><head></head><body>"+log_text+"</body></html>");
		//fix by Aniket Dutta
		log_text_area.setCaretPosition(log_text_area.getDocument().getLength());		

    }
    
    /**
     * Add input string of the given color to the log console 
     * @param logmessage Log message to show in the log console
     * @param textcolor Color of the text (Ex. FF0000)
     */
    protected void addLogText(String logmessage, String textcolor){
        log_text += "<font color="+"#"+textcolor+">"+logmessage+"</font><br>";
        log_text_area.setText("<html><head></head><body>"+log_text+"</body></html>");
		//fix by Aniket Dutta
		log_text_area.setCaretPosition(log_text_area.getDocument().getLength());		

    }   

    
/**
 * Constructor
 */
    public MainGUI() {
		config = Configuration.getInstance();
        theme_sel = new ThemeSelector();
        try {
            //tryes to get config.xml path
            File app_path = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8"));
            application_path = app_path.getParent();
            config.setXmlConfigObject(new XMLConfig(application_path));

            look_and_feel = theme_sel.getLAF(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"));            
			if (ThemeSelector.isPlastic(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"))){            
				theme_sel.setTheme(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme"));
			}
            UIManager.setLookAndFeel(look_and_feel);
            Vector langs = new Vector(10,5);
			Document document = XMLParser.parseXmlFile(this.getClass().getResource("/it/pdfsam/i18n/languages.xml"));
			List nodeList = document.selectNodes("/languages/language");
			for (int i = 0; nodeList != null && i < nodeList.size(); i++){ 
				Node langNode  =((Node) nodeList.get(i));
				if (langNode != null){
					langs.add(new ListItem(langNode.selectSingleNode("@value").getText(), langNode.selectSingleNode("@description").getText()));
				}
			}
			config.setLanguageList(langs);
        }catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
        }
        ToolTipManager.sharedInstance().setDismissDelay (300000);
        initialize();
    }
/**
 * Provides GUI initialization
 */
    private void initialize() {
    	try{
			language = config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/i18n");
		}catch(Exception ge){
			language = LanguageLoader.DEFAULT_LANGUAGE;
		}

		//get bundle
		config.setI18nResourceBundle(new LanguageLoader(language, "it.pdfsam.i18n.resources.Messages").getBundle());
		i18n_messages = config.getI18nResourceBundle();

        main_gui = new JFrame();
        springLayout = new SpringLayout();
        
        //
        main_gui.getContentPane().setLayout(springLayout);
        main_gui.setIconImage(new ImageIcon(this.getClass().getResource("/images/pdfsam.png")).getImage());
        main_gui.setTitle(MainGUI.NAME+" Ver. "+MainGUI.APP_VERSION);        
        main_gui.setName("main_gui");
        main_gui.setBounds(100, 100, 600, 650);
        main_gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main_gui.setFocusable(true);
        center(main_gui, 600, 650);

        final JTabbedPane tabbed_main_panel = new JTabbedPane();
        tabbed_main_panel.setPreferredSize(new Dimension(0, 450));
        tabbed_main_panel.setMinimumSize(new Dimension(0, 330));
        main_gui.getContentPane().add(tabbed_main_panel,BorderLayout.CENTER);
        //if tab is change than the right FocusPolicy is applied
        tabbed_main_panel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int i = ((JTabbedPane)e.getSource()).getSelectedIndex();
                main_gui.setFocusTraversalPolicy(((PlugablePanel)pl_panel[i]).getFocusPolicy());        
            }
        });

//MENU  
        final JMenuBar menu_bar = new JMenuBar();
        main_gui.setJMenuBar(menu_bar);

        final JMenu menu_file = new JMenu();
        menu_bar.add(menu_file);
        menu_file.setName("menu_file");
        menu_file.setText(GettextResource.gettext(i18n_messages,"File"));
        menu_file.setMnemonic(KeyEvent.VK_F);
        final JMenu menu_info = new JMenu();
        menu_bar.add(menu_info);
        menu_info.setName("menu_info");
        menu_info.setText(GettextResource.gettext(i18n_messages,"About"));
        menu_info.setMnemonic(KeyEvent.VK_A);
        
        final JMenuItem clear_log_item = new JMenuItem();
        clear_log_item.setText(GettextResource.gettext(i18n_messages,"Clear log"));
        menu_file.add(clear_log_item);
        clear_log_item.setIcon(new ImageIcon(this.getClass().getResource("/images/clear.png")));
        clear_log_item.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
               log_text = "";
               addLogText((MainGUI.NAME+" Ver. "+MainGUI.APP_VERSION));            
           }
        });

        final JMenuItem exit_item = new JMenuItem();
        exit_item.setText(GettextResource.gettext(i18n_messages,"Exit"));
        menu_file.add(exit_item);
        exit_item.setIcon(new ImageIcon(this.getClass().getResource("/images/exit.png")));
        exit_item.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
               System.exit(1);
           }
        });
        
        
        final JMenuItem about_item = new JMenuItem();
        about_item.setText(GettextResource.gettext(i18n_messages,"Info"));
        menu_info.add(about_item);
        about_item.setIcon(new ImageIcon(this.getClass().getResource("/images/info.png")));
        about_item.addMouseListener(new MouseAdapter() {
           public void mousePressed(MouseEvent e) {
               info_panel.show();
           }
       });        
//LOG        
        final JScrollPane log_panel = new JScrollPane();
        log_panel.setMinimumSize(new Dimension(0, 0));
        main_gui.getContentPane().add(log_panel,BorderLayout.SOUTH);        
        log_text_area = new JTextPane();      
        log_text_area.setContentType("text/html");
        log_text_area.setEditable(false);
        log_text_area.setPreferredSize(new Dimension(0, 85));
        log_panel.setViewportView(log_text_area);
        log_text_area.setDragEnabled(true);
        log_text_area.setName("pdfsam_log");
        addLogText(MainGUI.NAME+" Ver. "+MainGUI.APP_VERSION);            
//END_LOG    
//SPLIT_PANEL
        final JSplitPane pannello_split = new JSplitPane();
        pannello_split.setBottomComponent(log_panel);
        pannello_split.setTopComponent(tabbed_main_panel);
        pannello_split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        pannello_split.setOpaque(false);
        pannello_split.setOneTouchExpandable(true);
        pannello_split.setResizeWeight(0.60);
        main_gui.getContentPane().add(pannello_split);
//END_SPLIT_PANEL       
//LAYOUT
        springLayout.putConstraint(SpringLayout.SOUTH, tabbed_main_panel, -75, SpringLayout.SOUTH, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, tabbed_main_panel, 0, SpringLayout.EAST, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, tabbed_main_panel, 0, SpringLayout.NORTH, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, tabbed_main_panel, 0, SpringLayout.WEST, main_gui.getContentPane());
        
        springLayout.putConstraint(SpringLayout.SOUTH, log_panel, 0, SpringLayout.SOUTH, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, log_panel, 0, SpringLayout.EAST, tabbed_main_panel);
        springLayout.putConstraint(SpringLayout.NORTH, log_panel, 0, SpringLayout.SOUTH, tabbed_main_panel);
        springLayout.putConstraint(SpringLayout.WEST, log_panel, 0, SpringLayout.WEST, tabbed_main_panel);
        
        springLayout.putConstraint(SpringLayout.SOUTH, pannello_split, 0, SpringLayout.SOUTH, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, pannello_split, 0, SpringLayout.NORTH, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, pannello_split, 0, SpringLayout.EAST, main_gui.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, pannello_split, 0, SpringLayout.WEST, main_gui.getContentPane());        
//END_LAYOUT
//SCANS_FOR_PLUGINS        
        int i = 0;
        ArrayList p_table_data = new ArrayList();
        try   {
            final PlugInsLoader pl = new PlugInsLoader(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/plugs_absolute_dir"));
            pl_panel = new JPanel[pl.getPNumber()+1];
            for (i = 0 ; i < pl.getPNumber() ; i ++ ) {
                    //Load plugin
                    pl_panel[i] = (JPanel) pl.loadPlugin(i);
                    String p_name = ((PlugablePanel)pl_panel[i]).getPluginName();
                    //TODO if more than 9 plugs??
                    tabbed_main_panel.addTab(p_name, null, pl_panel[i], "ALT+"+Integer.toString(i+1));
                    tabbed_main_panel.setMnemonicAt(i, 0x30 + i +1);
                    Icon panel_icon = ((PlugablePanel)pl_panel[i]).getIcon();
                    if (panel_icon != null){
                        tabbed_main_panel.setIconAt(i, (panel_icon));
                    }                    
                    pl_panel[i].addPropertyChangeListener(this);
                    pl_panel[i].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
                    //Plugin info on info panel
                    Object[] row_data = new Object[3]; 
                    row_data[0] = p_name;
                    row_data[1] = ((PlugablePanel)pl_panel[i]).getVersion();
                    row_data[2] = ((PlugablePanel)pl_panel[i]).getPluginAuthor();
                    p_table_data.add(row_data);
                    info_panel = new InfoGUI(p_table_data);
            }
            pl_panel[i] = new JSettingsPanel();
            String p_name = ((PlugablePanel)pl_panel[i]).getPluginName();
            tabbed_main_panel.addTab(p_name, null, pl_panel[i], "ALT+"+Integer.toString(i+1));
            tabbed_main_panel.setMnemonicAt(i, 0x30 + i +1);
            Icon panel_icon = ((PlugablePanel)pl_panel[i]).getIcon();
            if (panel_icon != null){
                tabbed_main_panel.setIconAt(i, (panel_icon));
            }                    
            pl_panel[i].addPropertyChangeListener(this);
            pl_panel[i].setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            
        }
        catch (Exception lpi){
            lpi.printStackTrace();
            addLogText("Exception loading plugin: "+lpi.getMessage(), "FF0000");
        }
//END_SCANS_FOR_PLUGINS                
}  
    /**
     * Catch propertyChangeEvent and if it's a log message from one of the installed plugins
     * the log panel is updated.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if ((e.getPropertyName() == "LOG") && (e.getNewValue() == "LOG_UPDATED")){
            try{
                addLogText(((PlugablePanel)e.getSource()).getLogMsg(),((PlugablePanel)e.getSource()).getLogColor());
            }
            catch(ClassCastException cce){                
            }
        }
    }
    
    /**
     * Used to center the mai window on the screen
     * @param frame JFrame to center
     * @param width 
     * @param height
     */
    private void center(JFrame frame, int width, int height){
        Dimension framedimension = new Dimension(width,height);        
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

        Double centreX = new Double((screensize.getWidth() / 2) - (framedimension.getWidth()  / 2));
        Double centreY = new Double((screensize.getHeight() / 2) - (framedimension.getHeight()  / 2));
        
        frame.setBounds(centreX.intValue(), centreY.intValue(), 600, 650);
    }
    
    
    /**
     * @return the Plugin author
     */
    public String getPluginAuthor(){
        return MainGUI.AUTHOR;
    }

    /**
     * @return the Plugin name
     */    
    public String getPluginName(){
        return MainGUI.NAME;
    }
 
    /**
     * @return the Plugin version
     */    
    public String getVersion(){
        return MainGUI.APP_VERSION;
    }

}

