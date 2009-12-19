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
package org.pdfsam.plugin.docinfo.GUI;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.commons.business.loaders.PdfDocumentLoadedHook;
import org.pdfsam.guiclient.dto.DocumentMetaData;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
/**
 * Hook called after the document has been loaded
 * @author Andrea Vacondio
 *
 */
public class SetMetaFieldsHook implements PdfDocumentLoadedHook {

	private DocInfoMainGUI panel;
	
	/**
	 * @param panel
	 */
	public SetMetaFieldsHook(DocInfoMainGUI panel) {
		super();
		this.panel = panel;
	}

	@Override
	public void afterDocumentLoaded() {
		PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
		if(items != null && items.length == 1){
			PdfSelectionTableItem item = items[0];
			DocumentMetaData meta = item.getDocumentMetaData();
			panel.getTitleTextField().setText(StringUtils.trimToEmpty(meta.getTitle()));
			panel.getAuthorTextField().setText(StringUtils.trimToEmpty(meta.getAuthor()));
			panel.getSubjectTextField().setText(StringUtils.trimToEmpty(meta.getSubject()));
			panel.getKeywordsTextField().setText(StringUtils.trimToEmpty(meta.getKeywords()));
		}

	}

}
