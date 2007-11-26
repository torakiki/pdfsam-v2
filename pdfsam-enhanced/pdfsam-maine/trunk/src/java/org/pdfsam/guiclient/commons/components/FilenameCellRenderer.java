/*
 * Created on 18-Nov-2007
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
package org.pdfsam.guiclient.commons.components;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.pdfsam.guiclient.commons.models.PdfSelectionTableModel;
/**
 * Renderer for the filename cell, shows a lock if the file is encrypted
 * @author Andrea Vacondio
 *
 */
public class FilenameCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1276471348276524210L;

	public Component getTableCellRendererComponent(JTable table, Object value,
	        boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == PdfSelectionTableModel.PAGES){
		          if(((PdfSelectionTableModel)table.getModel()).getRow(row).isEncrypted()){
		              setIcon(new ImageIcon(this.getClass().getResource("/images/encrypted.png")));
		          }
		    }
	        return this;
	    }

}
