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
package org.pdfsam.guiclient.commons.components.sharedchooser;

import java.io.File;

import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.configuration.Configuration;

/**
 * Shared JFileChooser instance used all over the application
 * 
 * @author Andrea Vacondio
 * 
 */
public class SharedJFileChooser {

	private static JFileChooser instance = null;

	private SharedJFileChooser() {

	}

	/**
	 * 
	 * @param type
	 *            type of file chooser
	 * @param mode
	 *            mode of the file chooser
	 * @param currentDirectory
	 *            directory where the file chooser is pointed
	 * @return a shared instance of JFileChooser given the input parameters
	 */
	public static synchronized JFileChooser getInstance(SharedJFileChooserType type, int mode, String currentDirectory) {
		if (instance == null) {
			instance = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
		} else {
			instance.resetChoosableFileFilters();
			instance.setMultiSelectionEnabled(false);
			instance.setSelectedFile(new File(""));
		}
		if (StringUtils.isNotEmpty(currentDirectory)) {
			instance.setCurrentDirectory(new File(currentDirectory));
		}
		instance.setFileFilter(type.getFilter());
		instance.setFileSelectionMode(mode);
		return instance;
	}

	/**
	 * 
	 * @param type
	 *            type of file chooser
	 * @param mode
	 *            mode of the file chooser
	 * @return a shared instance of JFileChooser given the input parameters
	 */
	public static JFileChooser getInstance(SharedJFileChooserType type, int mode) {
		return getInstance(type, mode, null);
	}
}
