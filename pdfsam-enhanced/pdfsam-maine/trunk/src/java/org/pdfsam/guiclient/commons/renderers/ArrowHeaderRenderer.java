/*
 * Created on 24-Dec-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel.SortingState;
/**
 * Renderer for the header of JPdfSelectionTable. It shows the arrow up or down depending on the sorting.
 * Based on the inner class SortableHeaderRenderer of the TableSorter:
 * @author Philip Milne
 * @author Brendon McLean 
 * @author Dan van Enckevort
 * @author Parwinder Sekhon
 * @version 2.0 02/27/04
 * 
 * @author Andrea Vacondio
 *
 */
public class ArrowHeaderRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -1266775322573196447L;
	private AbstractPdfSelectionTableModel tableModel;
	private TableCellRenderer tableCellRenderer;
	
	public ArrowHeaderRenderer(AbstractPdfSelectionTableModel tableModel, TableCellRenderer tableCellRenderer) {
		this.tableModel = tableModel;
		this.tableCellRenderer = tableCellRenderer;
    }

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		 Component c = tableCellRenderer.getTableCellRendererComponent(table, 
                 value, isSelected, hasFocus, row, column);
         if (c instanceof JLabel) {
             JLabel l = (JLabel) c;
	        l.setHorizontalTextPosition(JLabel.LEFT);
	        int modelColumn = table.convertColumnIndexToModel(column);
	        l.setIcon(getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
        }
        return c;
    }
    
	/**
	 * @param column
	 * @param size
	 * @return the arrow icon
	 */
	private Icon getHeaderRendererIcon(int column, int size) {
		Icon retVal = null;
		SortingState sortingState = tableModel.getSortingState();
		if(sortingState != null && (AbstractPdfSelectionTableModel.NOT_SORTED != sortingState.getSortType()) && (column == sortingState.getCol())){
			retVal = new Arrow(sortingState.getSortType() != AbstractPdfSelectionTableModel.DESCENDING, size);
		}
        return retVal;
    }
    
	/**
	 * Arrow icon. Based on the inner class of the TableSorter 
	 * @author Philip Milne
	 * @author Brendon McLean 
	 * @author Dan van Enckevort
	 * @author Parwinder Sekhon
	 * @version 2.0 02/27/04
	 * 
	 * @author Andrea Vacondio
	 *
	 */
	private static class Arrow implements Icon {
        private boolean descending;
        private int size;

        public Arrow(boolean descending, int size) {
            this.descending = descending;
            this.size = size;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color color = c == null ? Color.GRAY : c.getBackground();             
            // In a compound sort, make each succesive triangle 20% 
            // smaller than the previous one. 
            int dx = (int)(size/2);
            int dy = descending ? dx : -dx;
            // Align icon (roughly) with font baseline. 
            y = y + 5*size/6 + (descending ? -dy : 0);
            int shift = descending ? 1 : -1;
            g.translate(x, y);

            // Right diagonal. 
            g.setColor(color.darker());
            g.drawLine(dx / 2, dy, 0, 0);
            g.drawLine(dx / 2, dy + shift, 0, shift);
            
            // Left diagonal. 
            g.setColor(color.brighter());
            g.drawLine(dx / 2, dy, dx, 0);
            g.drawLine(dx / 2, dy + shift, dx, shift);
            
            // Horizontal line. 
            if (descending) {
                g.setColor(color.darker().darker());
            } else {
                g.setColor(color.brighter().brighter());
            }
            g.drawLine(dx, 0, 0, 0);

            g.setColor(color);
            g.translate(-x, -y);
        }

        public int getIconWidth() {
            return size;
        }

        public int getIconHeight() {
            return size;
        }
    }
}
