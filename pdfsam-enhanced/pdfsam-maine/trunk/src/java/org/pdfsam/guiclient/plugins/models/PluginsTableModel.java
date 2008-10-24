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
package org.pdfsam.guiclient.plugins.models;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
/**
 * Table model for the info GUI table. It shows informations about loaded plugins
 * @author Andrea Vacondio
 *
 */
public class PluginsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7807326212787111828L;

	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_VERSION = 1;
	private static final int COLUMN_AUTHOR = 2;
	
	//colums names
    private String[] columnNames = {"Name","Version","Author"};

    //data array
    private ArrayList<PluginDataModel> pluginsData;

    public PluginsTableModel(ArrayList<PluginDataModel> pluginsData){
        this.pluginsData = pluginsData;
    }
    
    public PluginsTableModel(Hashtable<PluginDataModel, AbstractPlugablePanel> pluginsData){
    	this.pluginsData = new ArrayList<PluginDataModel>();
    	for (Enumeration<PluginDataModel> plugsEnumeration = pluginsData.keys();plugsEnumeration.hasMoreElements();) {
			this.pluginsData.add((PluginDataModel) plugsEnumeration.nextElement());
    	}
        
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return pluginsData.size();
    }

    public boolean isCellEditable(int row, int col) {
            return false;
    }    

    public Object getValueAt(int rowIndex, int columnIndex) {
    	String retVal = "";
    	PluginDataModel panelData = (PluginDataModel)pluginsData.get(rowIndex);
        if(columnIndex == PluginsTableModel.COLUMN_NAME){
        	retVal = panelData.getName();
        }else if(columnIndex == PluginsTableModel.COLUMN_AUTHOR){
        	retVal = panelData.getAuthor();
        }else if(columnIndex == PluginsTableModel.COLUMN_VERSION){
        	retVal = panelData.getVersion();
        }
        return retVal;
    }
    
    /**
     * <p> Return column name
     * 
     * @param col Column number
     * @return Column name
     */
    public String getColumnName(int col) {
        String retVal = "";
    	try{
    		retVal = columnNames[col];
        }
        catch (Exception e){
        	retVal = null;
        }
        return retVal;
    }

    /**
     * @param columnNames The columnNames to set.
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }
}
