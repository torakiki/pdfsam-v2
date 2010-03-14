/*
 * Created on 13-Feb-2010 
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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.utils.FileExtensionUtility;

/**
 * Abstract class for the run buttons listeners
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractRunButtonActionListener implements ActionListener {

	/**
	 * If a default working directory is set it returns it as suggested
	 * destination. If no default is set it returns the absolute path to the
	 * directory where the input item is resides.
	 * 
	 * @param item
	 * @return the suggested destination directory
	 */
	protected String getSuggestedDestinationDirectory(PdfSelectionTableItem item) {
		String retVal;
		if (StringUtils.isNotEmpty(Configuration.getInstance().getDefaultWorkingDirectory())) {
			retVal = Configuration.getInstance().getDefaultWorkingDirectory();
		} else {
			retVal = item.getInputFile().getParentFile().getAbsolutePath();
		}
		return retVal;
	}

	/**
	 * If a default working directory is set it returns an absolute path for the
	 * input fileName in the default working directory. If no default is set it
	 * returns an absolute path placing the input fileName in the same directory
	 * of the given {@link PdfSelectionTableItem}
	 * 
	 * @param item
	 * @param fileName
	 *            name of the output file
	 * @return the suggested output file abstract path for the given fileName
	 */
	protected String getSuggestedOutputFile(PdfSelectionTableItem item, String fileName) {
		File retVal = new File(fileName);
		if (StringUtils.isNotEmpty(Configuration.getInstance().getDefaultWorkingDirectory())) {
			retVal = new File(Configuration.getInstance().getDefaultWorkingDirectory(), fileName);
		} else {
			if (item != null) {
				retVal = new File(item.getInputFile().getParentFile(), fileName);
			}
		}
		return retVal.getAbsolutePath();
	}

	/**
	 * Check if there is a text in the TextField and it has a pdf extension, if
	 * not it adds the extension.
	 * 
	 * @param field
	 */
	public static void ensurePdfExtensionOnTextField(JTextField field) {
		String stringWithExtension = FileExtensionUtility.ensureExtension(field.getText(),
				FileExtensionUtility.PDF_EXTENSION);
		if (!StringUtils.equalsIgnoreCase(stringWithExtension, field.getText())) {
			field.setText(stringWithExtension);
		}
	}
}
