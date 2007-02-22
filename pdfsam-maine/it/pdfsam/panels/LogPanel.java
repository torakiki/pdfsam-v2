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
package it.pdfsam.panels;

import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.types.ListItem;
import it.pdfsam.util.HtmlFilter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultEditorKit;

/**
 * Log panel
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class LogPanel extends JPanel implements MouseListener, ActionListener{

	private static final long serialVersionUID = 3012902729120710132L;
//	consts
	public static final int LOG_ERROR = 0;
	public static final int LOG_INFO = 1;
	public static final int LOG_DETAILEDINFO = 2;
	public static final int LOG_DEBUG = 3;
	private static final String LOG_ERROR_COLOR = "FF0000";
	private static final String LOG_INFO_COLOR = "000000";
	private static final String LOG_DEBUG_COLOR = "0000FF";
	
	public static final String CLEAR_ACTION = "1";
	public static final String SAVELOG_ACTION = "2";
	public static final String SELECTALL_ACTION = "3";
	
	private static ArrayList logValuesDescrition = getStaticLogLevels();
	private Configuration config;
	private String log_text = "";
    private final JTextPane log_text_area = new JTextPane();
    private final JLabel log_level = new JLabel();
	final private JScrollPane log_panel = new JScrollPane();
	final private JPopupMenu popupMenu = new JPopupMenu();
	final private JFileChooser file_chooser = new JFileChooser();

    public LogPanel(){
    	super();
    	config = Configuration.getInstance();
    	logValuesDescrition.add(LogPanel.LOG_ERROR, "Errors");
    	logValuesDescrition.add(LogPanel.LOG_INFO, "Informations");
    	logValuesDescrition.add(LogPanel.LOG_DETAILEDINFO, "Detailed informations");
    	logValuesDescrition.add(LogPanel.LOG_DEBUG, "Debug");
    	setLogLeveLabel();
    	init();
    }
    
    private void init(){
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(500, 130));
		setMinimumSize(new Dimension(0, 0));
		
		log_level.setIcon(new ImageIcon(this.getClass().getResource("/images/log.png")));
		log_level.setPreferredSize(new Dimension(0,30));
		
        log_text_area.setContentType("text/html");
        log_text_area.setEditable(false);
        log_text_area.setPreferredSize(new Dimension(0, 155));
        log_text_area.setDragEnabled(true);
        
        JMenuItem menuCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuCopy.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Copy"));
        menuCopy.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-copy.png")));
        popupMenu.add(menuCopy);
        
        JMenuItem menuClear = new JMenuItem();
        menuClear.setActionCommand(LogPanel.CLEAR_ACTION);
        menuClear.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear"));
        menuClear.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-clear.png")));
        menuClear.addActionListener(this);
        popupMenu.add(menuClear);

        JMenuItem menuSelectAll = new JMenuItem();
        menuSelectAll.setActionCommand(LogPanel.SELECTALL_ACTION);
        menuSelectAll.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Select all"));
        menuSelectAll.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-select-all.png")));
        menuSelectAll.addActionListener(this);
        popupMenu.add(menuSelectAll);
        
        popupMenu.addSeparator();
        
        JMenuItem menuSave = new JMenuItem();
        menuSave.setActionCommand(LogPanel.SAVELOG_ACTION);
        menuSave.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Save log"));
        menuSave.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-save.png")));
        menuSave.addActionListener(this);
        popupMenu.add(menuSave);

        log_text_area.addMouseListener(this);
        
        log_panel.setMinimumSize(new Dimension(0, 0));
        log_panel.setViewportView(log_text_area);
        
        file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        file_chooser.setFileFilter(new HtmlFilter());
        
        add(Box.createRigidArea(new Dimension(3, 0)));
        add(log_level);
		add(log_panel);
    }
    
    /**
     * Sets the log level label taken from the configuration
     */
    private void setLogLeveLabel(){
    	    	log_level.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Log level:"+" "+(String)logValuesDescrition.get(config.getLogLevel())));
    }
    
	/**
	*@return Color HEX code based on message level
	*/
	private String getLogColor(int message_level){
		String color;
		switch(message_level){
			case LogPanel.LOG_ERROR:
				 color = LogPanel.LOG_ERROR_COLOR;
				 break;
			
			case LogPanel.LOG_INFO:
				 color = LogPanel.LOG_INFO_COLOR;
				 break;
				 
			case LogPanel.LOG_DETAILEDINFO:
				 color = LogPanel.LOG_INFO_COLOR;
				 break;
				 
			case LogPanel.LOG_DEBUG:
				 color = LogPanel.LOG_DEBUG_COLOR;
				 break;
			default:
				color = LogPanel.LOG_INFO_COLOR; 
		}
		return color;
	}
	
    /**
     * Add input string to the log console
     * @param logmessage Log message to show in the log console
     */
    public void addLogText(String logmessage){
        log_text += logmessage+"<br>";
        log_text_area.setText("<html><head></head><body>"+log_text+"</body></html>");
		//fix by Aniket Dutta
		log_text_area.setCaretPosition(log_text_area.getDocument().getLength());		
    }
    
    /**
     * Add input string of the given color to the log console 
     * @param logmessage Log message to show in the log console
     * @param log_level Log Level 
     */
    public void addLogText(String logmessage, int log_level){
        log_text += "<font color="+"#"+getLogColor(log_level)+">"+logmessage+"</font><br>";
        log_text_area.setText("<html><head></head><body>"+log_text+"</body></html>");
		//fix by Aniket Dutta
		log_text_area.setCaretPosition(log_text_area.getDocument().getLength());
    } 
    
    /**
	* returns List of log type
	*/
	public static LinkedList getLogList(){
	    LinkedList retval = new LinkedList();
	    retval.add(new ListItem(LogPanel.LOG_ERROR,(String)logValuesDescrition.get(LogPanel.LOG_ERROR)));
	    retval.add(new ListItem(LogPanel.LOG_INFO,(String)logValuesDescrition.get(LogPanel.LOG_INFO)));
	    retval.add(new ListItem(LogPanel.LOG_DETAILEDINFO,(String)logValuesDescrition.get(LogPanel.LOG_DETAILEDINFO)));
	    retval.add(new ListItem(LogPanel.LOG_DEBUG,(String)logValuesDescrition.get(LogPanel.LOG_DEBUG)));
	    return retval;	    
	}
	
	public void mouseReleased(MouseEvent e){
		if(e.isPopupTrigger()){
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}		
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger()){
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0 != null){
			if(arg0.getActionCommand().equals(LogPanel.CLEAR_ACTION)){
				log_text = "";
				log_text_area.setText("");
			}else if(arg0.getActionCommand().equals(LogPanel.SELECTALL_ACTION)){
				log_text_area.selectAll();
				log_text_area.requestFocus();
			}else if(arg0.getActionCommand().equals(LogPanel.SAVELOG_ACTION)){
				saveLog();
			}else{
				addLogText(GettextResource.gettext(config.getI18nResourceBundle(), "Unknown action."), LogPanel.LOG_ERROR);
			}
		}
	}
	
	
	public void saveLog() {
		try{
			file_chooser.setApproveButtonText(GettextResource.gettext(config.getI18nResourceBundle(), "Save"));
			int return_val = file_chooser.showOpenDialog(this);
			File chosen_file = null;
			if (return_val == JFileChooser.APPROVE_OPTION) {
				chosen_file = file_chooser.getSelectedFile();
				if (chosen_file != null) {
					try {
						
						FileWriter file_writer = new FileWriter(chosen_file);
						file_writer.write(log_text_area.getText());
						file_writer.flush();
						file_writer.close();
						addLogText(GettextResource.gettext(config.getI18nResourceBundle(), "Log saved."),
								LogPanel.LOG_INFO);
					} catch (Exception ex) {
						addLogText(GettextResource.gettext(config.getI18nResourceBundle(), "Error: ") + ex.getMessage(),
								LogPanel.LOG_ERROR);

					}
				}
			}
		}
		catch(RuntimeException re){
			addLogText(GettextResource.gettext(config.getI18nResourceBundle(), "RuntimeError:")
					+ " Unable to save log. "+re.getMessage(), LogPanel.LOG_ERROR);			
		}
		catch(Exception e){
			addLogText(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable to save log. "+e.getMessage(), LogPanel.LOG_ERROR);
		}
	}
	
	/**
	 * help method to get the log list
	 * @return static arrayList
	 */
	private static ArrayList getStaticLogLevels() {
		ArrayList staticList = new ArrayList(4);
		staticList.add(LogPanel.LOG_ERROR, "Errors");
		staticList.add(LogPanel.LOG_INFO, "Informations");
		staticList.add(LogPanel.LOG_DETAILEDINFO, "Detailed informations");
		staticList.add(LogPanel.LOG_DEBUG, "Debug");
		return staticList;
	}
}
