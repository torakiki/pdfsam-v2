/*
 * Created on 07-Mar-2010 
 * Copyright (C) 2010 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.pdfsam.guiclient.business.environment.Environment;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Load Environment Action
 * 
 * @author Andrea Vacondio
 * 
 */
public class LoadEnvironmentAction extends AbstractAction {

	private static final long serialVersionUID = -2827024871975864143L;

	private Environment environment;

	private JFrame parent;

	public LoadEnvironmentAction(Environment environment, JFrame parent) {
		super(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Load environment"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Load environment"));
		this.putValue(Action.SMALL_ICON, new ImageIcon(this.getClass().getResource("/images/fileopen.png")));
		this.environment = environment;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.XML_FILE,
				JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			environment.loadJobs(selectedFile);
		}

	}

}
