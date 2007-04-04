/*
 * Created on 08-Feb-2006
 * Model for the list plugins table
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
package it.pdfsam.models;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;
/**
 * Table model for the info GUI table. It shows informations about loaded plugins
 * @author Andrea Vacondio
 * @see it.pdfsam.GUI.InfoGUI
 *
 */
public class PluginsModel extends AbstractTableModel {

    /**
     * 
     */
    private static final long serialVersionUID = 6035234312626809873L;

    //colums names
    private String[] columnNames = {"Name","Version","Author"};

    //data array
    private ArrayList data = new ArrayList();

    public PluginsModel(ArrayList input_data){
        super();
        data=input_data;
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public boolean isCellEditable(int row, int col) {
            return false;
    }    

    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Object[])data.get(rowIndex))[columnIndex];
    }
    /**
     * <p> Return column name
     * 
     * @param col Column number
     * @return Column name
     */
    public String getColumnName(int col) {
        try{
            return columnNames[col];
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * @param columnNames The columnNames to set.
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
}
