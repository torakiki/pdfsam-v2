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

import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;



/**
 * Model for the table in JPdfSlectionPanel
 * @author  Andrea Vacondio
 * @see     javax.swing.table.AbstractTableModel
 */
public class SimplePdfSelectionTableModel extends AbstractPdfSelectionTableModel {

	private static final long serialVersionUID = 1655126010246744193L;


    //data array
    protected Vector data = new Vector();

    protected Configuration config;
    
	/**
	 * default constructor with default number of showed columns
	 */
	public SimplePdfSelectionTableModel() {
		this(DEFAULT_SHOWED_COLUMNS_NUMBER, JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER);
	}

	/**
	 * @param showedColumns
	 * @param maxRowsNumber
	 */
	public SimplePdfSelectionTableModel(int showedColumns, int maxRowsNumber) {
		config = Configuration.getInstance();
		String[] i18nColumnNames = {
				"#",
				GettextResource.gettext(config.getI18nResourceBundle(),"File name"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Path"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Pages"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Password"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Version"),
                GettextResource.gettext(config.getI18nResourceBundle(),"Page Selection")};
		setColumnNames(i18nColumnNames);
		
		String[] i18nToolTips ={
			"",
			"",
            "",
            GettextResource.gettext(config.getI18nResourceBundle(),"Total pages of the document"),
            GettextResource.gettext(config.getI18nResourceBundle(),"Password to open the document (if needed)"),
            GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version of the document"),
            GettextResource.gettext(config.getI18nResourceBundle(),"Double click to set pages you want to merge (ex: 2 or 5-23 or 2,5-7,12-)")};
		setToolTips(i18nToolTips);
		
		setShowedColumns(showedColumns);
		setMaxRowsNumber(maxRowsNumber);
	}



    /**
     * @return Rows number
     */
    public int getRowCount() {        
        return (data != null)? data.size(): 0;
    }

    

	/**
     * Return the value at row, col
     */
    public Object getValueAt(int row, int col) {
    	String retVal = "";
    	if(row < data.size() && col < getShowedColumns()){
    		PdfSelectionTableItem tmpElement = (PdfSelectionTableItem)(data.get(row));
    		switch(col){
    			case ROW_NUM:
    				retVal = (row+1)+"";
    				break;
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
    			case PDF_DOCUMENT_VERSION:
    				retVal = tmpElement.getPdfVersionDescription();
    				break;
    			case PAGESELECTION:
    				retVal = (tmpElement.getPageSelection() != null)? tmpElement.getPageSelection(): "";
    				break;
				default: 
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
        for(int i=0; (i<inputData.length && data.size()<getMaxRowsNumber()); i++){
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
            if (inputData != null && data.size()<getMaxRowsNumber()){
                data.add(inputData);
                this.fireTableRowsInserted(data.size(),data.size());
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
        	data.subList(rows[0], rows[rows.length-1]+1).clear();           
            this.fireTableRowsDeleted(rows[0], rows[rows.length -1]);
        }
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

	/**
     * Add a row to the table data source if maxRowsNumber is not reached and fire to Listeners
     * @param index index to add to
     * @param inputData <code>PdfSelectionTableItem</code> to add to the data source
     */
    public void addRowAt(int index, PdfSelectionTableItem inputData){
            if (inputData != null && data.size()<getMaxRowsNumber() && index>=0 && index<=data.size()){
                data.add(index, inputData);
                this.fireTableRowsInserted(index,index);
            }
    }
   
    /**
     * Replace a row to the table data source and fire to Listeners
     * @param index index to be replaced
     * @param inputData new <code>PdfSelectionTableItem</code> to replace the data source
     */
    public void updateRowAt(int index, PdfSelectionTableItem inputData){
        if (inputData != null && index>=0 && index<data.size()){
            data.set(index, inputData);
            this.fireTableRowsUpdated(index,index);
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
    
    //sorting features not implemeneted
    
    public void sort(){
    	
    }
    
    public SortingState getSortingState(){
    	return null;
    }

	public void setSortingState(SortingState sortingState) {
		
	}
}
