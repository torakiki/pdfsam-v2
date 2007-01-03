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
package it.pdfsam.listeners;

import it.pdfsam.abstracts.LogWriter;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.env.EnvWorker;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.panels.LogPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * Listener for the ToolBar
 * @author a.vacondio
 * @see it.pdfsam.panles.JButtonBar
 */
public class EnvActionListener implements ActionListener {
	
	public static final String SAVE_COMMAND = "save";
	public static final String LOAD_COMMAND = "load";	
	
	private LogWriter parent;
	private EnvWorker ew;
	private ResourceBundle i18n_messages;
	
	public EnvActionListener(LogWriter parent, EnvWorker ew){
		this.parent = parent;
		this.ew = ew;
		this.i18n_messages = Configuration.getInstance().getI18nResourceBundle();
	}
	
	/**
	 * Perform command validation and execute the EnvWorker method.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e != null){
			if(e.getActionCommand().equals(EnvActionListener.SAVE_COMMAND)){
				ew.saveJob();
			}else if(e.getActionCommand().equals(EnvActionListener.LOAD_COMMAND)){
				ew.loadJobs();
			}else{
				parent.fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error executing Env action."), LogPanel.LOG_ERROR);
			}
		}else{
			parent.fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error: event is null."), LogPanel.LOG_ERROR);
		}
	}

}
