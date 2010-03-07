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
package org.pdfsam.guiclient.gui.panels;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.actions.LoadEnvironmentAction;
import org.pdfsam.guiclient.business.actions.SaveEnvironmentAction;
import org.pdfsam.guiclient.business.listeners.LogActionListener;
import org.pdfsam.guiclient.business.listeners.mediators.ApplicationExitMediator;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Buttons bar for the main GUI
 * 
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class JButtonsPanel extends JPanel {

	private static final long serialVersionUID = 203934401531647182L;

	private static final Logger log = Logger.getLogger(JButtonsPanel.class.getPackage().getName());

	private JToolBar toolBar;

	private JButton buttonSaveEnv;

	private JButton buttonLoadEnv;

	private JButton buttonSaveLog;

	private JButton buttonClearLog;

	private JButton buttonExit;

	private LogActionListener logActionListener;

	private ApplicationExitMediator exitMediator;

	public JButtonsPanel(SaveEnvironmentAction saveAction, LoadEnvironmentAction loadAction,
			ApplicationExitMediator exitMediator, LogActionListener logActionListener) {
		this.logActionListener = logActionListener;
		this.exitMediator = exitMediator;
		init(saveAction, loadAction);
	}

	/**
	 * Initialize the panel
	 */
	private void init(SaveEnvironmentAction saveAction, LoadEnvironmentAction loadAction) {
		try {
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setPreferredSize(new Dimension(600, 30));

			toolBar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
			toolBar.setFloatable(true);
			toolBar.setRollover(true);
			toolBar.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

			buttonSaveEnv = new JButton();
			buttonSaveEnv.setAction(saveAction);
			buttonSaveEnv.setText("");

			buttonLoadEnv = new JButton();
			buttonLoadEnv.setAction(loadAction);
			buttonLoadEnv.setText("");

			buttonSaveLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-save.png")));
			buttonSaveLog.setActionCommand(LogActionListener.SAVE_LOG_ACTION);
			buttonSaveLog.setToolTipText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Save log"));
			buttonSaveLog.addActionListener(logActionListener);

			buttonClearLog = new JButton(new ImageIcon(this.getClass().getResource("/images/edit-clear.png")));
			buttonClearLog.setActionCommand(LogActionListener.CLEAR_LOG_ACTION);
			buttonClearLog.setToolTipText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Clear log"));
			buttonClearLog.addActionListener(logActionListener);

			buttonExit = new JButton(new ImageIcon(this.getClass().getResource("/images/exit.png")));
			buttonExit.setActionCommand(ApplicationExitMediator.SAVE_AND_EXIT_COMMAND);
			buttonExit.setToolTipText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Exit"));
			buttonExit.addActionListener(exitMediator);

			toolBar.add(buttonSaveEnv);
			toolBar.add(buttonLoadEnv);
			toolBar.addSeparator();
			toolBar.add(buttonSaveLog);
			toolBar.add(buttonClearLog);
			toolBar.addSeparator();
			toolBar.add(buttonExit);
			add(toolBar);
		} catch (Exception e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Unable to initialize button bar."), e);
		}
	}
}
