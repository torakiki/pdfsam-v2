/*
 * Created on 19-Sep-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Utility in support of the DnD
 * @author Andrea Vacondio
 *
 */
public final class DnDSupportUtility {

	private static final Logger log = Logger.getLogger(DnDSupportUtility.class.getPackage().getName());
	
	public static DataFlavor VISUAL_LIST_FLAVOR = null;
	public static DataFlavor URI_LIST_FLAVOR = null;
	
	private static final String URI_DELIMITER = "\r\n";
	
	static {
		try {
			if (VISUAL_LIST_FLAVOR == null) {
				VISUAL_LIST_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
			}
		} catch (ClassNotFoundException e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Unable to initializate drag and drop support."), e);
		}
	}
	
	
	static {
		try {
			if (URI_LIST_FLAVOR == null) {
				URI_LIST_FLAVOR = new DataFlavor("text/uri-list;class=java.lang.String"); 
			}
		} catch (ClassNotFoundException e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Unable to initializate drag and drop support."), e);
		}
	}
	
	private DnDSupportUtility(){
		//no constructor
	}
	
	/**
	 * 
	 * @param data
	 *            the input URI sting
	 * @return a list of file
	 */
	public static List<File> textURIListToFileList(String data) {
		List<File> retVal = new java.util.ArrayList<File>(1);
		Scanner scanner = new Scanner(data);
		scanner.useDelimiter(URI_DELIMITER);
		while (scanner.hasNext()) {
			String token = scanner.next();
			if (token != null && !token.startsWith("#")) {
				try {
					File currentFile = new File(new URI(token));
					if (currentFile.exists()) {
						retVal.add(currentFile);
					}
				} catch (java.net.URISyntaxException e) {
					// empty on purpose
				} catch (IllegalArgumentException e) {
					// empty on purpose
				}
			}
		}
		return retVal;
	}

}
