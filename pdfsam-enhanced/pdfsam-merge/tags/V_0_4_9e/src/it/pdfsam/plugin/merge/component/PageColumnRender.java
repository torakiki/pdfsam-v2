/*
 * Created on 14-Mar-2006
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
package it.pdfsam.plugin.merge.component;


import it.pdfsam.plugin.merge.model.MergeTableModel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Render for the page column. It show a lock image for encrypted pdf
 * @author  Andrea Vacondio
 * @see     javax.swing.table.TableCellRenderer
 * @see     javax.swing.JLabel
 * @see     it.pdfsam.plugin.merge.model.MergeTableModel
 */
public class PageColumnRender extends JLabel implements TableCellRenderer {

    

    /**
	 * 
	 */
	private static final long serialVersionUID = 1642737654403943584L;

	public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        setOpaque(true);
        try{
        setText(value.toString());
        setIcon(null);
        setForeground(Color.BLACK);
        if (column == MergeTableModel.PAGES){
            if(((MergeTableModel)table.getModel()).getRow(row).isEncrypted()){
                setIcon(new ImageIcon(this.getClass().getResource("/images/encrypted.png")));
            }
        }
        }catch (NullPointerException e){
            setText("");    
        }       
        if (isSelected){
            setBackground(table.getSelectionBackground());
        }
        else{
            setBackground(table.getBackground());
        }
        return this;
    }


}
