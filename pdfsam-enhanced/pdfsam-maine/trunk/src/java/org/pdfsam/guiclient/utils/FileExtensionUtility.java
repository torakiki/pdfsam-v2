/*
 * Created on 09-Mar-2010
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
package org.pdfsam.guiclient.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for the file extensions related tasks
 * 
 * @author Andrea Vacondio
 * 
 */
public final class FileExtensionUtility {

	public static final String PDF_EXTENSION = "pdf";

	public static final String XML_EXTENSION = "xml";

	public static final String TXT_EXTENSION = "txt";

	private FileExtensionUtility() {
		// on purpose
	}

	/**
	 * Ensures that the file path in input has the input file extension, if not
	 * it adds the extension.
	 * 
	 * @param filePath
	 * @param fileExtension
	 *            the file extension without leading dot (Ex. pdf)
	 * @return the correct file path (with file extension added if necessasy)
	 */
	public static String ensureExtension(String filePath, String fileExtension) {
		String retVal = filePath;
		if (StringUtils.isNotEmpty(filePath)) {
			if (!(filePath.toLowerCase().endsWith('.' + fileExtension.toLowerCase()))) {
				retVal = new StringBuilder(filePath).append('.').append(fileExtension).toString();
			}
		}
		return retVal;
	}

	/**
	 * Ensures that the file in input has the input file extension, if not it
	 * adds the extension.
	 * 
	 * @param file
	 * @param fileExtension
	 *            the file extension without leading dot (Ex. pdf)
	 * @return the correct file path (with file extension added if necessasy)
	 */
	public static File ensureExtension(File file, String fileExtension) {
		return new File(ensureExtension(file.getAbsolutePath(), fileExtension));
	}

}
