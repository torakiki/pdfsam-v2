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

import it.pdfsam.abstracts.AbstractLogWriter;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnvActionListener;
import it.pdfsam.listeners.ExitActionListener;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * Buttons bar for the main GUI
 * 
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class ButtonsBar extends AbstractLogWriter {

	private static final long serialVersionUID = 7325659962880983934L;

	private JToolBar toolBar;
	private Configuration config;
	private JButton buttonSave;
	private JButton buttonLoad;
	private JButton buttonSaveLog;
	private JButton buttonClearLog;
	private JButton buttonExit;
	
	public ButtonsBar(String toolBarTitle){
		super();
		config = Configuration.getInstance();
		this.toolBar = new JToolBar(toolBarTitle, JToolBar.HORIZONTAL);		
		init();
	}
	
	/**
	 * Add a listener to the ToolBar environment buttons
	 * @param al
	 */
	public void addEnvButtonsActionListener(ActionListener al){
		try{
			buttonSave.addActionListener(al);        
			buttonLoad.addActionListener(al);
		}catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable add listeners.", LogPanel.LOG_ERROR);
		}
	}
	
	/**
	 * Add a listener to the ToolBar log buttons
	 * @param al
	 */
	public void addLogButtonsActionListener(ActionListener al){
		try{
			buttonSaveLog.addActionListener(al);        
			buttonClearLog.addActionListener(al);
		}catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable add listeners.", LogPanel.LOG_ERROR);
		}
	}
	
	/**
	 * Add a listener to the ToolBar exit button
	 * @param al
	 */
	public void addExitActionListener(ActionListener al){
		try{
			buttonExit.addActionListener(al);        
		}catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(config.getI18nResourceBundle(), "Error:")
					+ " Unable add listeners.", LogPanel.LOG_ERROR);
		}
	}
	/**
	 * Initialize the panel
	 *
	 */
	private void init(){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPreferredSize(new Dimension(600, 30));
        toolBar.setFloatable(true);
        toolBar.setRollover(true);
        toolBar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        buttonSave = new JButton(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
        buttonSave.setActionCommand(EnvActionListener.SAVE_COMMAND);
        buttonSave.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Save environment"));

        buttonLoad = new JButton(new ImageIcon(this.getClass().getResource("/images/fileopen.png")));
        buttonLoad.setActionCommand(EnvActionListener.LOAD_COMMAND);
        buttonLoad.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Load environment"));
        
        buttonSaveLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-save.png")));
        buttonSaveLog.setActionCommand(LogPanel.SAVELOG_ACTION);
        buttonSaveLog.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Save log"));

        buttonClearLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-clear.png")));
        buttonClearLog.setActionCommand(LogPanel.CLEAR_ACTION);
        buttonClearLog.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear log"));

        buttonExit = new JButton(new ImageIcon(this.getClass().getResource("/images/exit.png")));
        buttonExit.setActionCommand(ExitActionListener.EXIT_COMMAND);
        buttonExit.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Exit"));

        toolBar.add(buttonSave);
        toolBar.add(buttonLoad);
        toolBar.addSeparator();
        toolBar.add(buttonSaveLog);
        toolBar.add(buttonClearLog);
        toolBar.addSeparator();
        toolBar.add(buttonExit);
        add(toolBar);		
	}	
}
