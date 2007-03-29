/*
 * Created on 21-Dec-2006
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

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.abstracts.LogWriter;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.events.WorkDoneEvent;
import it.pdfsam.console.interfaces.WorkDoneListener;
import it.pdfsam.env.EnvWorker;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnvActionListener;
import it.pdfsam.listeners.ExitActionListener;
import it.pdfsam.panels.ButtonsBar;
import it.pdfsam.panels.JInfoPanel;
import it.pdfsam.panels.JSettingsPanel;
import it.pdfsam.panels.JStatusPanel;
import it.pdfsam.panels.LogPanel;
import it.pdfsam.panels.MenuPanel;
import it.pdfsam.types.NodeInfo;
import it.pdfsam.util.LanguageLoader;
import it.pdfsam.util.PlugInsLoader;
import it.pdfsam.util.ThemeSelector;
import it.pdfsam.util.XMLConfig;
import it.pdfsam.util.XMLParser;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.dom4j.Document;
import org.dom4j.Node;
/**
 * Main GUI
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class MainGUI extends JFrame implements TreeSelectionListener, PropertyChangeListener, WorkDoneListener{


	private static final long serialVersionUID = 4213259584377123941L;

	private ThemeSelector theme_sel;
	private String look_and_feel;
	public String application_path;
	private String language;
    private Configuration config;
    private JTree tree;
    private JPanel plugs_panel = new JPanel(new CardLayout());
    private AbstractPlugIn[] pl_panel;
    private JStatusPanel status_bar;
    private LogPanel log_panel;
    private JScrollPane main_scroll_panel;
    private JSplashScreen screen;
    private ResourceBundle i18n_messages;
    private EnvWorker envWorker;
    
    public static final String AUTHOR = "Andrea Vacondio";
	public static final String NAME = "PDF Split and Merge enhanced";
	public static final String UNIXNAME = "pdfsam";
	public static final String APP_VERSION = "1.2.0e beta"; 
	
	private final ExitActionListener exitListener = new ExitActionListener();
	//i set this true while i'm developing.. false when releasing
	private static final boolean IDE = false;

	public MainGUI() {
		runSplash();
		config = Configuration.getInstance();
		config.getMainConsole().addWorkDoneListener(this);
		
		theme_sel = new ThemeSelector();
		try {
			//tryes to get config.xml path
			if (IDE){
				application_path = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8");                            
			}else{
				File app_path = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8"));
				application_path = app_path.getParent();
			}
			setSplashStep("Loading configuration..");
			config.setXmlConfigObject(new XMLConfig(application_path));
			look_and_feel = theme_sel.getLAF(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"));            
			if (ThemeSelector.isPlastic(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"))){            
				theme_sel.setTheme(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme"));
			}
			UIManager.setLookAndFeel(look_and_feel);
			String tmp_log_level = config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/loglevel");
			if(tmp_log_level != null && !tmp_log_level.equals("")){
				config.setLogLevel(Integer.parseInt(tmp_log_level));
			}else{
				config.setLogLevel(LogPanel.LOG_INFO);
			}
			setSplashStep("Loading i18n..");
			Vector langs = new Vector(10,5);
			Document document = XMLParser.parseXmlFile(this.getClass().getResource("/it/pdfsam/i18n/languages.xml"));
			List nodeList = document.selectNodes("/languages/language");
			for (int i = 0; nodeList != null && i < nodeList.size(); i++){ 
				langs.add(((Node) nodeList.get(i)).selectSingleNode("@value").getText());
			}
			config.setLanguageList(langs);

		}catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		setSplashStep("Getting language..");
		try{
			language = config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/i18n");
		}catch(Exception ge){
			language = LanguageLoader.DEFAULT_LANGUAGE;
		}

		//get bundle
		config.setI18nResourceBundle(new LanguageLoader(language, "it.pdfsam.i18n.Messages").getBundle());
		i18n_messages = config.getI18nResourceBundle();

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		center(this,800,600);
		setIconImage(new ImageIcon(this.getClass().getResource("/images/pdf.png")).getImage());
        try{
            setTitle(MainGUI.NAME+" Ver. "+MainGUI.APP_VERSION);
            
        }catch(Exception ge){
            setTitle("pdfsam");
        }
        setSplashStep(GettextResource.gettext(i18n_messages,"Building menus.."));
		//menu
		final MenuPanel menu_bar = new MenuPanel();
		menu_bar.addPropertyChangeListener(this);
		menu_bar.addExitActionListener(exitListener);
        setJMenuBar(menu_bar);
        setSplashStep(GettextResource.gettext(i18n_messages,"Building buttons bar.."));
        //buttons bar
        final ButtonsBar buttons_bar = new ButtonsBar(MainGUI.NAME + " ToolBar");
        getContentPane().add(buttons_bar,BorderLayout.PAGE_START);      
        buttons_bar.addPropertyChangeListener(this);
        buttons_bar.addExitActionListener(exitListener);
               
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(MainGUI.UNIXNAME+" "+MainGUI.APP_VERSION);
        DefaultMutableTreeNode plugsNode = new DefaultMutableTreeNode("Plugins");
        rootNode.add(plugsNode);        

        log_panel =  new LogPanel();        
        buttons_bar.addLogButtonsActionListener(log_panel);
        plugs_panel.setPreferredSize(new Dimension(670,500));
//      SCANS_FOR_PLUGINS     
        setSplashStep(GettextResource.gettext(i18n_messages,"Loading plugins.."));
        int i = 0;
        ArrayList p_table_data = new ArrayList();
        try   {
            final PlugInsLoader pl = new PlugInsLoader(config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/plugs_absolute_dir"));
            pl_panel = new AbstractPlugIn[pl.getPNumber()+2];                       
            for (i = 0 ; i < pl.getPNumber() ; i ++ ) {
                    //Load plugin
                    pl_panel[i] = (AbstractPlugIn) pl.loadPlugin(i);
                    String p_name = pl_panel[i].getPluginName();
                    plugs_panel.add(pl_panel[i],p_name);
                    plugsNode.add(new DefaultMutableTreeNode(new NodeInfo(p_name,i)));
                    pl_panel[i].addPropertyChangeListener(this);
                    //Plugin info on info panel
                    Object[] row_data = new Object[3]; 
                    row_data[0] = p_name;
                    row_data[1] = pl_panel[i].getVersion();
                    row_data[2] = pl_panel[i].getPluginAuthor();
                    p_table_data.add(row_data);
                    screen.setText(p_name+" loaded.");
            }
            pl_panel[i] = new JSettingsPanel();
            plugs_panel.add(pl_panel[i],pl_panel[i].getPluginName());
            rootNode.add(new DefaultMutableTreeNode(new NodeInfo(pl_panel[i].getPluginName(),i)));
            pl_panel[i].addPropertyChangeListener(this);
            screen.setText(pl_panel[i].getPluginName()+" loaded.");
            
            pl_panel[i+1] = new JInfoPanel(p_table_data);
            plugs_panel.add(pl_panel[i+1],pl_panel[i+1].getPluginName());
            rootNode.add(new DefaultMutableTreeNode(new NodeInfo(pl_panel[i+1].getPluginName(),i+1)));
            screen.setText(pl_panel[i+1].getPluginName()+" loaded.");
                   
        }
        catch(RuntimeException re){
            log_panel.addLogText("RuntimeException loading plugin: "+re.getMessage(), LogPanel.LOG_ERROR);
        }
        catch (Exception lpi){
            lpi.printStackTrace();
            log_panel.addLogText("Exception loading plugin: "+lpi.getMessage(), LogPanel.LOG_ERROR);
        }
        envWorker = new EnvWorker(pl_panel);
        envWorker.addPropertyChangeListener(this);
        
        setSplashStep(GettextResource.gettext(i18n_messages,"Building tree.."));
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.addTreeSelectionListener(this);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(false);
        tree.expandPath(new TreePath(plugsNode.getPath()));
        
//END_SCANS_FOR_PLUGINS 
        main_scroll_panel = new JScrollPane(plugs_panel);
        main_scroll_panel.setMinimumSize(new Dimension(300, 400));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, main_scroll_panel,log_panel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerLocation(0.75);

        JScrollPane scrollPane = new JScrollPane(tree);
        
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        		scrollPane, splitPane);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setDividerLocation(155);
        
        getContentPane().add(splitPane2,BorderLayout.CENTER);
                
        //status bar
        setSplashStep(GettextResource.gettext(i18n_messages,"Building status bar.."));
        status_bar = new JStatusPanel(new ImageIcon(this.getClass().getResource("/images/pdf.png")),MainGUI.NAME);
        status_bar.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        getContentPane().add(status_bar,BorderLayout.PAGE_END); 
        
        //after status bar has been created
        
        if(rootNode.getChildCount() > 2 && rootNode.getChildAt(2) != null){
        	tree.setSelectionPath(new TreePath(rootNode.getChildAt(2)));
        }
        //after plugs have been loaded
        buttons_bar.addEnvButtonsActionListener(new EnvActionListener(buttons_bar,envWorker));
		menu_bar.addEnvButtonsActionListener(new EnvActionListener(menu_bar,envWorker));

		//check and load default env
		try{
			String defaultEnv = config.getXmlConfigObject().getXMLConfigValue("/pdfsam/settings/defaultjob");
			if (defaultEnv != null && defaultEnv.length() > 0){
				envWorker.loadJobs(defaultEnv);
			}
		}
		catch (Exception dje){
            log_panel.addLogText("Exception loading default environment: "+dje.getMessage(), LogPanel.LOG_ERROR);
        }
		closeSplash();
	}

	public void valueChanged(TreeSelectionEvent e) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	                       tree.getLastSelectedPathComponent();
	    if (node != null && node.isLeaf()) {
	    	NodeInfo selectedPlug = (NodeInfo)node.getUserObject();
	    	status_bar.setText(selectedPlug.getName());
	    	status_bar.setIcon(pl_panel[selectedPlug.getIndex()].getIcon());
	    	CardLayout cl = (CardLayout)(plugs_panel.getLayout());
	        cl.show(plugs_panel, selectedPlug.getName());	   
	    	
	    } 
	}
	/**
     * Catch propertyChangeEvent and if it's a log message from one of the installed plugins
     * the log panel is updated.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName() == "LOG"){
        	try{
				int message_level = ((Integer)e.getNewValue()).intValue();
				//log message is shown
				if(message_level <= config.getLogLevel()){
					log_panel.addLogText(((LogWriter)e.getSource()).getLogMsg(),message_level);
				}
            }
            catch(ClassCastException cce){  
				System.out.println(cce.getMessage());
            }
        }
    }
    /**
     * @return Returns the Plugin name.
     */
    public String getName() {
        return MainGUI.NAME;
    }

	 /**
     * @return Returns the version.
     */
    public static String getVersion() {
        return MainGUI.APP_VERSION;
    }

    /**
     * 
     * @return Author
     */
	public static String getAuthor() {
		return MainGUI.AUTHOR;
	}


    /**
     * mange and event that notify a change in percentage of the work done
     */
    public void percentageOfWorkDoneChanged(WorkDoneEvent wde) {
        final int perc = wde.getPercentageDone();
        if (wde.getType() == WorkDoneEvent.PERCENTAGE_CHANGE){
            
            Runnable runner = new Runnable() {
                public void run() {
                    status_bar.setBarValue(perc);
                    status_bar.setBarString(Integer.toString(perc)+" %");
                }
            };
            SwingUtilities.invokeLater(runner);
        }
    }
    
    public void workingIndeterminate(WorkDoneEvent wde){
    	if (wde.getType() == WorkDoneEvent.WORK_INDETERMINATE){
    		status_bar.setBarIndeterminate(true);
        }
    }
    
    public void workCompleted(WorkDoneEvent wde) {
        if (wde.getType() == WorkDoneEvent.WORK_DONE){
        	status_bar.setBarIndeterminate(false);
        	status_bar.setBarValue(0);
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
        
        frame.setBounds(centreX.intValue(), centreY.intValue(), 640, 480);
    }

    /**
     * Run a splash screen
     */
    private void runSplash(){
    		screen = new JSplashScreen("pdfsam loader", "Initialization..");
			screen.setMaximumBarValue(7);
    		Runnable runner = new Runnable() {
    			public void run() {    				
    				screen.setVisible(true);
    			}
    		};
    		SwingUtilities.invokeLater(runner);
    }
    
    /**
     * close the splash screen
     */
    private void closeSplash(){
        if(screen != null){
	    	screen.setVisible(false);	                   
	    	screen.dispose();
	    }
    }
    
    /**
     * Sets the splash text and increment the progress bar
     * @param message
     */
    private void setSplashStep(String message){
    	if(screen != null){
    		screen.setText(message);
    		screen.addBarValue();
    	}
    }
    
    public static void main(String args[]) {
		try {
			
			MainGUI window = new MainGUI();

			window.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
