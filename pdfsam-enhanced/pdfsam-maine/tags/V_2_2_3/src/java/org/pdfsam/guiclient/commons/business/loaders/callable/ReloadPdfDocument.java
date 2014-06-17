/*
 * Created on 29-Nov-2009
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
package org.pdfsam.guiclient.commons.business.loaders.callable;

import java.io.File;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

/**
 * Callable to reload a pdf document
 * 
 * @author Andrea Vacondio
 * 
 */
public class ReloadPdfDocument extends AddPdfDocument {

	private static final Logger LOG = Logger.getLogger(ReloadPdfDocument.class.getPackage().getName());

	private int index = 0;

	public ReloadPdfDocument(File inputFile, JPdfSelectionPanel panel, String password, String pageSelection, int index) {
		super(inputFile, panel, password, pageSelection);
		this.index = index;
	}

	@Override
	public Boolean call() {
		Boolean retVal = Boolean.FALSE;
		try {
			if (inputFile != null) {
				if (new PdfFilter(false).accept(inputFile)) {
					wipText = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
							"Please wait while reading")
							+ " " + inputFile.getName() + " ...";
					panel.addWipText(wipText);
					panel.updateTableRow(index, getPdfSelectionTableItem(inputFile, password, pageSelection));
					panel.removeWipText(wipText);
					retVal = Boolean.TRUE;
				} else {
					LOG.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
							"Selected file is not a pdf document.")
							+ " " + inputFile.getName());
				}
			}
		} catch (Throwable e) {
			LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), e);
		}
		return retVal;
	}
}
