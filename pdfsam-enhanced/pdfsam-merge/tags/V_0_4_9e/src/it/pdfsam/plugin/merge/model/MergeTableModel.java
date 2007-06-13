/*
 * Created on 03-Feb-2006
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
package it.pdfsam.plugin.merge.model;

import it.pdfsam.plugin.merge.type.MergeItemType;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;



/**
 * Model for the Merge table
 * @author  Andrea Vacondio
 * @see     javax.swing.table.AbstractTableModel
 */
public class MergeTableModel extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3829562657030134592L;

	private final int SHOWED_COLS = 4;
    
    //colums order
    public final static int FILENAME = 0;
    public final static int PATH = 1;
    public final static int PAGES = 2;
    public final static int PAGESELECTION = 3;
    //colums names
    private String[] columnNames = {"File name",
                                    "Path",
                                    "Pages",
                                    "Page Selection"};
    
    //tooltips
    private String[] toolTips = {"",
                                 "",
                                 "Total pages of the document",
                                 "Double click to set pages you want to merge (ex: 2 or All or 5-23 or 2,5-7,12)"
                                };
    //data array
    private ArrayList data = new ArrayList();
   
    /**
     * @return Number of showed columns in Merge table
     */
    public int getColumnCount() {
        return SHOWED_COLS;
    }

    /**
     * @return Rows number in Merge table
     */
    public int getRowCount() {
        try{
            return data.size();
        }
        catch(NullPointerException e){
            return 0;
        }   
    }

    /**
     * Return the value at row, col
     */
    public Object getValueAt(int row, int col) {
        return ((MergeItemType) data.get(row)).getValue(col);
    }

    /**
     * Return the value at row, col
     */
    public MergeItemType getRow(int row) {
        return ((MergeItemType) data.get(row));
    }
    
    /**
     * <p>set data source for the model
     * 
     * @param input_data array <code>MergeItemType[]</code> as data source
     * */
    public void setData(MergeItemType[] input_data){
        data.clear();
        for(int i=0; i<input_data.length; i++){
            data.add(input_data[i]);
        }

    }
    
    /**
     * <p>Remove any data source for the model
     *      
     * */
    public void clearData(){
        data.clear();
        this.fireTableDataChanged();
    }

    /**
     * Return true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        if (PAGESELECTION==column){
            return true;
        }else{
            return false;
        }
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
        switch(column){
                case FILENAME:
                    ((MergeItemType)data.get(row)).setFileName(value.toString());
                    break;
        
                case PATH:
                    ((MergeItemType)data.get(row)).setFilePath(value.toString());
                    break;
                    
                case PAGES:
                    ((MergeItemType)data.get(row)).setNumPages(value.toString());
                    break;
        
                case PAGESELECTION:
                    ((MergeItemType)data.get(row)).setPageSelection(value.toString());
                    break;
        
        } 
   }
    
    
    /**
     * <p>Add a row to the table data source and fire to Listeners
     * 
     * @param input_data <code>MergeItemType</code> to add to the data source
     * */
    public void addRow(MergeItemType input_data){
            if (input_data != null){
                data.add(input_data);
                int row_num = data.size();
                this.fireTableRowsInserted(row_num,row_num);
            }
    }
    
    /**
     * <p>Add a row to the table data source and fire to Listeners
     * 
     * @param input_data <code>MergeItemType</code> to add to the data source
     * */
    public void addRowAt(int index, MergeItemType input_data){
            if (input_data != null){
                data.add(index, input_data);
                this.fireTableRowsInserted(index,index);
            }
    }
   
    /**
     * <p>Moves up a row to the table data source and fire to Listeners
     * 
     * @param row Row number to move from the data source
     * */
    public void moveUpRow(int row)throws IndexOutOfBoundsException{
            if (row >= 1){
                MergeItemType tmp_element = (MergeItemType)data.get(row);
                data.set(row, data.get((row-1)));
                data.set((row-1), tmp_element);
                fireTableRowsUpdated(row-1, row);
            }
    }
    
    /**
     * <p>Moves up a set of rows to the table data source and fire to Listeners
     * 
     * @param rows Row numbers to move from the data source
     * */
    public void moveUpRows(int[] rows)throws IndexOutOfBoundsException{
        if (rows.length > 0){
            MergeItemType tmp_element;
           //no moveup if i'm selecting the first elemento in the table
           if (rows[0] > 0){
               tmp_element = (MergeItemType)data.get(rows[0]-1);
               for (int i=0; i<rows.length; i++){    
                   if (rows[i] > 0){
                           data.set(rows[i]-1, data.get(rows[i]));
                   }
               }
               data.set(rows[rows.length-1], tmp_element);
           }
           if (rows[0] >= 1){
                fireTableRowsUpdated(rows[0]-1, rows[rows.length-1]);
           }
       }
    } 
    
    /**
     * <p>Moves down a row to the table data source and fire to Listeners
     * 
     * @param row Row number to remove from the data source
     * */
    public void moveDownRow(int row)throws IndexOutOfBoundsException{
            if (row < (data.size()-1)){                
                MergeItemType tmp_element = (MergeItemType)data.get(row);
                data.set(row, data.get((row+1)));
                data.set((row+1), tmp_element);
                fireTableRowsUpdated(row, row+1);
            }
    } 
    
    /**
     * <p>Moves up a set of rows to the table data source and fire to Listeners
     * 
     * @param rows Row numbers to move from the data source
     * */
    public void moveDownRows(int[] rows)throws IndexOutOfBoundsException{
        if (rows.length > 0){
            MergeItemType tmp_element;
            //no moveup if i'm selecting the first elemento in the table
            if (rows[rows.length-1] < (data.size()-1)){
                tmp_element = (MergeItemType)data.get(rows[rows.length-1]+1);
                for (int i=(rows.length-1); i>=0; i--){    
                    if (rows[rows.length-1] < (data.size()-1)){
                            data.set(rows[i]+1, data.get(rows[i]));
                    }
                }
                data.set(rows[0], tmp_element);
            }
            if (rows[rows.length-1] < (data.size()-1)){
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
        if (rows.length > 0){
            LinkedList data_to_remove = new LinkedList();
            for (int i=0; i<rows.length; i++){
                data_to_remove.add(data.get(rows[i]));
            }
            for (int i=0; i<data_to_remove.size(); i++){
                data.remove(data_to_remove.get(i));            
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
        try{
            return columnNames[col];
        }
        catch (Exception e){
            return null;
        }
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
}
