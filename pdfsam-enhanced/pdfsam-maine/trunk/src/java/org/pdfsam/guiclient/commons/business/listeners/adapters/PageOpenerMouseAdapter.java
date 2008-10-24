/*
 * Created on 24-Oct-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.listeners.adapters;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.pdfsam.guiclient.commons.business.PagePreviewOpener;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.dto.VisualPageListItem;
/**
 * Listen for double click on the JList and open the preview page on the clicked page
 * @author Andrea Vacondio
 *
 */
public class PageOpenerMouseAdapter  extends MouseAdapter {

	private JVisualSelectionList thumbnailList;	
	
	/**
	 * @param thumbnailList
	 */
	public PageOpenerMouseAdapter(JVisualSelectionList thumbnailList) {
		super();
		this.thumbnailList = thumbnailList;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			int index = thumbnailList.locationToIndex(e.getPoint());
			VisualPageListItem item = (VisualPageListItem) thumbnailList.getModel().getElementAt(index);
			PagePreviewOpener.getInstance().openPreview(item.getParentFileCanonicalPath(), item.getDocumentPassword(), item.getPageNumber());
		}
	}

}
