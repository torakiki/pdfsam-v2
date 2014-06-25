/*
 * Created on 20/mar/2010
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
package org.pdfsam.guiclient.commons.business.listeners.adapters;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Shows the popup on a JTable when right click is performed, selecting the row.
 * 
 * @author Andrea Vacondio
 * 
 */
public class TableShowPopupMouseAdapter extends MouseAdapter {

	private final JPopupMenu popupMenu;

	private final JTable mainTable;

	/**
	 * @param popupMenu
	 * @param mainTable
	 */
	public TableShowPopupMouseAdapter(JPopupMenu popupMenu, JTable mainTable) {
		super();
		this.popupMenu = popupMenu;
		this.mainTable = mainTable;
	}

	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showMenu(e);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showMenu(e);
		}
	}

	private void showMenu(MouseEvent e) {
		popupMenu.show(mainTable, e.getX(), e.getY());
		Point p = e.getPoint();
		mainTable.setRowSelectionInterval(mainTable.rowAtPoint(p), mainTable.rowAtPoint(p));
	}
}
