/*
 * Created on 07-Nov-2007
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
package org.pdfsam.guiclient.business.listeners.mediators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.Environment;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.utils.filters.XmlFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * environment mediator
 * @author Andrea Vacondio
 *
 */
public class EnvironmentMediator implements ActionListener {
	
	private static final Logger log = Logger.getLogger(EnvironmentMediator.class.getPackage().getName());
	
	public static final String SAVE_ENV_ACTION = "saveenv";
	public static final String LOAD_ENV_ACTION = "loadenv";
	
	private Environment environment;		
	private ResourceBundle i18nMessages;
	private JFrame parent;
	private JFileChooser fileChooser;
	
	public EnvironmentMediator(Environment environment, JFrame parent) {
		super();
		this.environment = environment;
		this.parent = parent;
		this.i18nMessages = Configuration.getInstance().getI18nResourceBundle();

	}

	/**
	 * @return the environment
	 */
	public Environment getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}



	public void actionPerformed(ActionEvent e) {		
		if(environment != null && e != null){
			if(fileChooser==null){
				fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
				fileChooser.setFileFilter(new XmlFilter());
			}
			int state = JFileChooser.CANCEL_OPTION;
			if(SAVE_ENV_ACTION.equals(e.getActionCommand())){
				state = fileChooser.showSaveDialog(parent);
			}else{
				state = fileChooser.showOpenDialog(parent);
			}
			if (state == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();					
				if(SAVE_ENV_ACTION.equals(e.getActionCommand())){
					if(selectedFile.getName().toLowerCase().lastIndexOf(".xml") == -1){
						selectedFile = new File(selectedFile.getParent(), selectedFile.getName()+".xml");
					}
					int savePwd = JOptionPane.showConfirmDialog(
							parent,
						    GettextResource.gettext(i18nMessages,"Save passwords informations (they will be readable opening the output file)?"),
						    GettextResource.gettext(i18nMessages,"Confirm password saving"),
						    JOptionPane.YES_NO_OPTION);
					environment.saveEnvironment(selectedFile, (savePwd == JOptionPane.YES_OPTION));
				}else if(LOAD_ENV_ACTION.equals(e.getActionCommand())){
					environment.loadJobs(selectedFile);
				}else {
					log.warn(GettextResource.gettext(i18nMessages, "Unknown action."));
				}
			}
		}
	}

}
