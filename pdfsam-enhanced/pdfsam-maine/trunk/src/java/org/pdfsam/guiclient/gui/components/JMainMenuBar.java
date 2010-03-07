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

import org.pdfsam.guiclient.business.actions.LoadEnvironmentAction;
import org.pdfsam.guiclient.business.actions.SaveEnvironmentAction;
import org.pdfsam.guiclient.business.listeners.mediators.ApplicationExitMediator;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Menu bar
 * 
 * @author Andrea Vacondio
 * 
 */
public class JMainMenuBar extends JMenuBar {

	private static final long serialVersionUID = -818197133636053691L;

	private ApplicationExitMediator exitMediator;

	public JMainMenuBar(SaveEnvironmentAction saveAction, LoadEnvironmentAction loadAction,
			ApplicationExitMediator exitMediator) {
		this.exitMediator = exitMediator;
		init(saveAction, loadAction);
	}

	/**
	 * Initialize the bar
	 */
	private void init(SaveEnvironmentAction saveAction, LoadEnvironmentAction loadAction) {
		JMenu menuFile = new JMenu();
		menuFile.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File"));
		menuFile.setMnemonic(KeyEvent.VK_F);

		JMenuItem saveEnvItem = new JMenuItem();
		saveEnvItem.setAction(saveAction);

		JMenuItem loadEnvItem = new JMenuItem();
		loadEnvItem.setAction(loadAction);

		JMenuItem exitItem = new JMenuItem();
		exitItem.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Exit"));
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exitItem.setActionCommand(ApplicationExitMediator.SAVE_AND_EXIT_COMMAND);
		exitItem.setIcon(new ImageIcon(this.getClass().getResource("/images/exit.png")));
		exitItem.addActionListener(exitMediator);

		menuFile.add(saveEnvItem);
		menuFile.add(loadEnvItem);
		menuFile.addSeparator();
		menuFile.add(exitItem);
		add(menuFile);
	}
}
