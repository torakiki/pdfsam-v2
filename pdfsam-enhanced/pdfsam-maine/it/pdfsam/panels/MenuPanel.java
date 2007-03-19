/*
 * Created on 19-Dec-2006
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

import it.pdfsam.abstracts.LogWriter;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnvActionListener;
import it.pdfsam.listeners.ExitActionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
/**
 * pdfsame menu
 * @author a.vacondio
 *
 */
public class MenuPanel extends JMenuBar implements LogWriter{

	private static final long serialVersionUID = 4835761003762685117L;
	
	private String log_msg = "";
	private Configuration config;
	private final JMenu menu_file = new JMenu();
	private final JMenuItem save_job_item = new JMenuItem();
	private final JMenuItem load_job_item = new JMenuItem();
	private final JMenuItem exit_item = new JMenuItem();

	public MenuPanel(){
		config = Configuration.getInstance();
		init();
	}
	
	private void init(){
		add(menu_file);
		menu_file.setText(GettextResource.gettext(config.getI18nResourceBundle(),"File"));
		menu_file.setMnemonic(KeyEvent.VK_F);

		save_job_item.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Save environment"));
		save_job_item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.ALT_MASK));
		save_job_item.setActionCommand(EnvActionListener.SAVE_COMMAND);
		save_job_item.setIcon(new ImageIcon(this.getClass().getResource("/images/filesave.png")));

		load_job_item.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Load environment"));
		load_job_item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_L, ActionEvent.ALT_MASK));
		load_job_item.setActionCommand(EnvActionListener.LOAD_COMMAND);
		load_job_item.setIcon(new ImageIcon(this.getClass().getResource("/images/fileopen.png")));

		exit_item.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Exit"));
		exit_item.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exit_item.setActionCommand(ExitActionListener.EXIT_COMMAND);
		exit_item.setIcon(new ImageIcon(this.getClass().getResource("/images/exit.png")));

		menu_file.add(save_job_item);
		menu_file.add(load_job_item);
		menu_file.addSeparator();
		menu_file.add(exit_item);
		
	
	}
	
	/**
	 * Add a listener to the ToolBar exit button
	 * @param al
	 */
	public void addExitActionListener(ActionListener al){
		try{	exit_item.addActionListener(al);        
		}catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable add listeners.", LogPanel.LOG_ERROR);
		}
	}
	
	/**
	 * Add a listener to the ToolBar environment buttons
	 * @param al
	 */
	public void addEnvButtonsActionListener(ActionListener al){
		try{			
			save_job_item.addActionListener(al);        
			load_job_item.addActionListener(al);
		}catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable add listeners.", LogPanel.LOG_ERROR);
		}
	}
	
	
	public void fireLogPropertyChanged(String log_msg, int log_level) {
        this.log_msg = log_msg;
        firePropertyChange("LOG", -1, log_level);
    }

	/**
     * @return Returns the log_msg.
     */
    public String getLogMsg() {
        return log_msg;
    }
}
