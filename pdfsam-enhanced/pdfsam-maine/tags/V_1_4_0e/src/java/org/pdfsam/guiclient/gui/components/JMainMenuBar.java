/*
 * Created on 08-Nov-2007
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
package org.pdfsam.guiclient.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.listeners.ExitActionListener;
import org.pdfsam.guiclient.business.listeners.mediators.EnvironmentMediator;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Menu bar
 * @author Andrea Vacondio
 *
 */
public class JMainMenuBar extends JMenuBar {

	private static final long serialVersionUID = -818197133636053691L;

	private static final Logger log = Logger.getLogger(JMainMenuBar.class.getPackage().getName());
	
	private Configuration config;
	private final JMenu menuFile = new JMenu();
	private final JMenuItem saveEnvItem = new JMenuItem();
	private final JMenuItem loadEnvItem = new JMenuItem();
	private final JMenuItem exitItem = new JMenuItem();
	private EnvironmentMediator envMediator;

	public JMainMenuBar(EnvironmentMediator envMediator){
		config = Configuration.getInstance();
		this.envMediator = envMediator;
		init();
	}
	
	/**
	 * Initialize the bar
	 */
	private void init(){
		try{
			add(menuFile);
			menuFile.setText(GettextResource.gettext(config.getI18nResourceBundle(),"File"));
			menuFile.setMnemonic(KeyEvent.VK_F);
	
			saveEnvItem.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Save environment"));
			saveEnvItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_S, ActionEvent.ALT_MASK));
			saveEnvItem.setActionCommand(EnvironmentMediator.SAVE_ENV_ACTION);
			saveEnvItem.setIcon(new ImageIcon(this.getClass().getResource("/images/filesave.png")));
			saveEnvItem.addActionListener(envMediator);
			
			loadEnvItem.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Load environment"));
			loadEnvItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_L, ActionEvent.ALT_MASK));
			loadEnvItem.setActionCommand(EnvironmentMediator.LOAD_ENV_ACTION);
			loadEnvItem.setIcon(new ImageIcon(this.getClass().getResource("/images/fileopen.png")));
			loadEnvItem.addActionListener(envMediator);
			
			exitItem.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Exit"));
			exitItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_F4, ActionEvent.ALT_MASK));
			exitItem.setActionCommand(ExitActionListener.EXIT_COMMAND);
			exitItem.setIcon(new ImageIcon(this.getClass().getResource("/images/exit.png")));
			exitItem.addActionListener(new ExitActionListener());
			
			menuFile.add(saveEnvItem);
			menuFile.add(loadEnvItem);
			menuFile.addSeparator();
			menuFile.add(exitItem);
		}catch(Exception e){
			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Unable to initialize menu bar."), e);
		}	
	}
}
