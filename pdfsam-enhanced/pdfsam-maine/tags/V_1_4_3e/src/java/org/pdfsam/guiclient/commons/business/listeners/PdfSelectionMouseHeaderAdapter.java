/*
 * Created on 21-Dec-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.listeners;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.models.SortablePdfSelectionTableModel;
/**
 * Listener for the mouse clicks on the table header
 * @author Andrea Vacondio
 *
 */
public class PdfSelectionMouseHeaderAdapter extends MouseAdapter {
	
	private AbstractPdfSelectionTableModel tableModel;
	
	public PdfSelectionMouseHeaderAdapter(AbstractPdfSelectionTableModel tableModel){
		this.tableModel = tableModel;
	}
	
	public void mouseClicked(MouseEvent e) {
		if(tableModel.isSortable()){
	        JTableHeader h = (JTableHeader) e.getSource();
	        TableColumnModel columnModel = h.getColumnModel();
	        int viewColumn = columnModel.getColumnIndexAtX(e.getX());
	        int column = columnModel.getColumn(viewColumn).getModelIndex();
	        if (column != -1 && column != SortablePdfSelectionTableModel.PASSWORD && column != SortablePdfSelectionTableModel.ROW_NUM) {
	            int sortType = (tableModel.getSortingState().getCol() == column)?tableModel.getSortingState().getSortType(): SortablePdfSelectionTableModel.NOT_SORTED;
	          
	            // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING} or 
	            // {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed. 
	            sortType = sortType + (e.isShiftDown() ? -1 : 1);
	            sortType = (sortType + 4) % 3 - 1; // signed mod, returning {-1, 0, 1}
	            tableModel.setSortingState(tableModel.new SortingState(column, sortType));
	            h.repaint();
	        }
        }
    }
}
