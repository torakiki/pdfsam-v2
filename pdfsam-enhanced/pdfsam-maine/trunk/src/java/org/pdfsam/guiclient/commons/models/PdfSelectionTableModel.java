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
package org.pdfsam.guiclient.commons.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;



/**
 * Model for the table in JPdfSlectionPanel
 * @author  Andrea Vacondio
 * @see     javax.swing.table.AbstractTableModel
 */
public class PdfSelectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1655126010246744193L;

	//colums order
    public final static int FILENAME = 0;
    public final static int PATH = 1;
    public final static int PAGES = 2;
    public final static int PASSWORD = 3;
    public final static int PAGESELECTION = 4;
    
    public final static int MAX_COLUMNS_NUMBER = 5;
    public final static int DEFAULT_SHOWED_COLUMNS_NUMBER = 4;
    
    //colums names
    private String[] columnNames;
    
    //tooltips
    private String[] toolTips ;
    //data array
    private Vector data = new Vector();
    private int showedColumns = DEFAULT_SHOWED_COLUMNS_NUMBER;
    private int maxRowsNumber = JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER;
    private Configuration config;
    
	/**
	 * default constructor with 4 showed columns
	 */
	public PdfSelectionTableModel() {
		this(DEFAULT_SHOWED_COLUMNS_NUMBER, JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER);
	}

	/**
	 * @param showedColumns
	 * @param maxRowsNumber
	 */
	public PdfSelectionTableModel(int showedColumns, int maxRowsNumber) {
		config = Configuration.getInstance();
		String[] i18nColumnNames = {
				GettextResource.gettext(config.getI18nResourceBundle(),"File name"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Path"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Pages"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Password"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Page Selection")};
		columnNames = i18nColumnNames;
		
		String[] i18nToolTips ={
			"",
            "",
            GettextResource.gettext(config.getI18nResourceBundle(),"Total pages of the document"),
            GettextResource.gettext(config.getI18nResourceBundle(),"Password to open the document (if needed)"),
            GettextResource.gettext(config.getI18nResourceBundle(),"Double click to set pages you want to merge (ex: 2 or All or 5-23 or 2,5-7,12-)")};
		toolTips = i18nToolTips;
		
		setShowedColumns(showedColumns);
		setMaxRowsNumber(maxRowsNumber);
	}


	/**
     * @return Number of showed columns
     */
    public int getColumnCount() {
        return showedColumns;
    }

    /**
     * @return Rows number
     */
    public int getRowCount() {        
        return (data != null)? data.size(): 0;
    }

    
    /**
	 * @return the showedColumns
	 */
	public int getShowedColumns() {
		return showedColumns;
	}

	/**
	 * @param showedColumns the showedColumns to set (must be positive)
	 */
	public void setShowedColumns(int showedColumns) {
		if(showedColumns < 1){
			this.showedColumns = 1;
		}else if (showedColumns > MAX_COLUMNS_NUMBER){
			this.showedColumns = MAX_COLUMNS_NUMBER;
		}else{
			this.showedColumns = showedColumns;
		}
	}

	
	/**
	 * @return the maxRowsNumber
	 */
	public int getMaxRowsNumber() {
		return maxRowsNumber;
	}

	/**
	 * @param maxRowsNumber the maxRowsNumber to set (must be positive)
	 */
	public void setMaxRowsNumber(int maxRowsNumber) {
		if(maxRowsNumber < 1){
			this.maxRowsNumber = 1;
		}else{
			this.maxRowsNumber = maxRowsNumber;
		}
	}

	/**
     * Return the value at row, col
     */
    public Object getValueAt(int row, int col) {
    	String retVal = "";
    	if(row < data.size() && col < showedColumns){
    		PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)(data.get(row));
    		switch(col){
    			case FILENAME:
    				retVal = (tmpElement.getInputFile() != null)? tmpElement.getInputFile().getName(): "";
    				break;
    			case PATH:
    				retVal = (tmpElement.getInputFile() != null)? tmpElement.getInputFile().getAbsolutePath(): "";
    				break;
    			case PAGES:
    				retVal = (tmpElement.getPagesNumber() != null)? tmpElement.getPagesNumber(): "";
    				break;
    			case PASSWORD:
    				retVal = (tmpElement.getPassword() != null)? tmpElement.getPassword(): "";
    				break;
    			case PAGESELECTION:
    				retVal = (tmpElement.getPageSelection() != null)? tmpElement.getPageSelection(): "";
    				break;
    		}
    	}
        return retVal;
    }

    /**
     * Return the value at row
     */
    public PdfSelectionTableItem getRow(int row) {
    	PdfSelectionTableItem retVal = null;
    	if(row <= data.size()){
    		retVal = (PdfSelectionTableItem)(data.get(row));
    	}
        return retVal;
    }
    
    /**
     * set data source for the model
     * @param inputData array <code>PdfSelectionTableItem[]</code> as data source
     */
    public void setData(PdfSelectionTableItem[] inputData){
        data.clear();
        for(int i=0; (i<inputData.length && data.size()<maxRowsNumber); i++){
            data.add(inputData[i]);
        }
        this.fireTableDataChanged();
    }
    
    /**
     * Removes any data source for the model
     */
    public void clearData(){
        data.clear();
        this.fireTableDataChanged();
    }

    /**
     * Return true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        return ((PAGESELECTION==column)||(PASSWORD==column));
    }
    
    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  value   value to assign to cell
     *  @param  row   row of cell
     *  @param  column  column of cell
     */
    public void setValueAt(Object value, int row, int column) {
    	if(value != null && (PAGESELECTION == column || PASSWORD==column)&& row >= 0 && row < (data.size())){
    		if(PAGESELECTION == column){
    			((PdfSelectionTableItem)data.get(row)).setPageSelection(value.toString());
    		}else if(PASSWORD == column){
    			((PdfSelectionTableItem)data.get(row)).setPassword(value.toString());
    		}
    	}        
   }
    
    
    /**
     * Add a row to the table data source if maxRowsNumber is not reached and fire to Listeners
     * @param inputData <code>PdfSelectionTableItem</code> to add to the data source
     */
    public void addRow(PdfSelectionTableItem inputData){
            if (inputData != null && data.size()<maxRowsNumber){
                data.add(inputData);
                this.fireTableRowsInserted(data.size(),data.size());
            }
    }
    
    /**
     * Add a row to the table data source if maxRowsNumber is not reached and fire to Listeners
     * @param inputData <code>PdfSelectionTableItem</code> to add to the data source
     */
    public void addRowAt(int index, PdfSelectionTableItem inputData){
            if (inputData != null && data.size()<maxRowsNumber && index>=0 && index<=data.size()){
                data.add(index, inputData);
                this.fireTableRowsInserted(index,index);
            }
    }
   
    /**
     * Moves up a row to the table data source and fire to Listeners
     * @param row Row number to move from the data source
     */
    public void moveUpRow(int row)throws IndexOutOfBoundsException{
            if (row >= 1 && row < (data.size())){
            	PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)data.get(row);
                data.set(row, data.get((row-1)));
                data.set((row-1), tmpElement);
                fireTableRowsUpdated(row-1, row);
            }
    }
    
    /**
     * Moves up a set of rows to the table data source and fire to Listeners
     * @param rows Row numbers to move from the data source
     */
    public void moveUpRows(int[] rows)throws IndexOutOfBoundsException{
        if (rows.length > 0 && rows.length < data.size()){
           //no move up if i'm selecting the first element of the table
           if (rows[0] > 0){
        	   PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)data.get(rows[0]-1);
               for (int i=0; i<rows.length; i++){    
                   if (rows[i] > 0){
                           data.set(rows[i]-1, data.get(rows[i]));
                   }
               }
               data.set(rows[rows.length-1], tmpElement);
               fireTableRowsUpdated(rows[0]-1, rows[rows.length-1]);
           }
       }
    } 
    
    /**
     * Moves down a row to the table data source and fire to Listeners
     * @param row Row number to remove from the data source
     */
    public void moveDownRow(int row) throws IndexOutOfBoundsException{
            if (row >= 0 && row < (data.size()-1)){                
            	PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)data.get(row);
                data.set(row, data.get((row+1)));
                data.set((row+1), tmpElement);
                fireTableRowsUpdated(row, row+1);
            }
    } 
    
    /**
     * Moves down a set of rows to the table data source and fire to Listeners
     * @param rows Row numbers to move from the data source
     */
    public void moveDownRows(int[] rows)throws IndexOutOfBoundsException{
        if (rows.length > 0 && rows.length < data.size()){
            //no move down if i'm selecting the last element of the table
            if (rows[rows.length-1] < (data.size()-1)){
            	PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)data.get(rows[rows.length-1]+1);
                for (int i=(rows.length-1); i>=0; i--){    
                    if (rows[rows.length-1] < (data.size()-1)){
                            data.set(rows[i]+1, data.get(rows[i]));
                    }
                }
                data.set(rows[0], tmpElement);
                fireTableRowsUpdated(rows[0], rows[rows.length-1]+1);
            }
        }
    }
    
    /**
     * <p>Remove a row from the table data source and fire to Listeners
     * 
     * @param row row number to remove from the data source
     * @throws Exception if an exception occurs
     * */
    public void deleteRow(int row) throws IndexOutOfBoundsException{
         data.remove(row);
         fireTableRowsDeleted(row,row);
    }
    /**
     * <p>Remove a set of rows from the table data source and fire to Listeners
     * 
     * @param rows rows number to remove from the data source
     * @throws Exception if an exception occurs
     * */   
    public void deleteRows(int[] rows) throws IndexOutOfBoundsException{
        if (rows.length > 0 && rows.length <= data.size()){
            for (int i=0; i<rows.length; i++){
                data.remove(data.get(rows[i]));            
            }
            this.fireTableRowsDeleted(rows[0], rows[rows.length -1]);
        }
    }  
    
    /**
     * <p> Return column name
     * 
     * @param col Column number
     * @return Column name
     */
    public String getColumnName(int col) {
    	return (col < columnNames.length)? columnNames[col]: "";
        
    }

    /**
     * @return Returns the toolTips.
     */
    public String[] getToolTips() {
        return toolTips;
    }

    /**
     * @param columnNames The columnNames to set.
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }  
    
    /**
     * @return rows of the model
     */
    public PdfSelectionTableItem[] getRows(){
    	PdfSelectionTableItem[] retVal = null;
    	if (data != null){
    		retVal = (PdfSelectionTableItem[]) data.toArray(new PdfSelectionTableItem[data.size()]);
    	}
    	return retVal;
    }
}
