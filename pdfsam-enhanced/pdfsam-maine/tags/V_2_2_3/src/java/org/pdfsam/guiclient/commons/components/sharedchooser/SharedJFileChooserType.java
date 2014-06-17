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

import javax.swing.filechooser.FileFilter;

import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.guiclient.utils.filters.TxtFilter;
import org.pdfsam.guiclient.utils.filters.XmlFilter;

/**
 * Types of SharedJFileChooser
 * 
 * @author Andrea Vacondio
 * 
 */
public enum SharedJFileChooserType {

	PDF_FILE(new PdfFilter()), TXT_FILE(new TxtFilter()), XML_FILE(new XmlFilter()), NO_FILTER(null);

	private FileFilter filter;

	/**
	 * @param filter
	 */
	private SharedJFileChooserType(FileFilter filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter
	 */
	public FileFilter getFilter() {
		return filter;
	}

}
