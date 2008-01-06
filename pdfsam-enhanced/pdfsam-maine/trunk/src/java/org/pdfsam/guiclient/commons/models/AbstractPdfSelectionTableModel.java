/*
 * Created on 20-Dec-2007
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
package org.pdfsam.guiclient.commons.models;

import java.io.Serializable;

import javax.swing.table.AbstractTableModel;

import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
/**
 * Abstract model for the selection table
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractPdfSelectionTableModel extends AbstractTableModel {
	//colums order
    public final static int FILENAME = 0;
    public final static int PATH = 1;
    public final static int PAGES = 2;
    public final static int PASSWORD = 3;
    public final static int PDF_DOCUMENT_VERSION = 4;
    public final static int PAGESELECTION = 5;
    
    public final static int MAX_COLUMNS_NUMBER = 6;
    public final static int DEFAULT_SHOWED_COLUMNS_NUMBER = 5;
    
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;
    
    //colums names
    private String[] columnNames;
    
    //tooltips
    private String[] toolTips ;
    
    private int showedColumns = DEFAULT_SHOWED_COLUMNS_NUMBER;
    private int maxRowsNumber = JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER;
    private boolean sortable = false;

    public AbstractPdfSelectionTableModel(){
    	this.sortable = false;
    }
	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}
	/**
	 * @param toolTips the toolTips to set
	 */
	public void setToolTips(String[] toolTips) {
		this.toolTips = toolTips;
	}
	/**
     * @return Number of showed columns
     */
    public int getColumnCount() {
        return showedColumns;
    }
    /**
	 * @return the showedColumns
	 */
	public int getShowedColumns() {
		return showedColumns;
	}

	public Class getColumnClass(int columnIndex) {
		return String.class;
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
     * Return true if the cell is editable
     */
    public boolean isCellEditable(int row, int column) {
        return ((PAGESELECTION==column)||(PASSWORD==column));
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
	 * @param sortable the sortable to set
	 */
	protected void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	/**
	 * @return the sortable
	 */
	public boolean isSortable() {
		return sortable;
	}
	/**
     * @return rows of the model
     */
    public abstract PdfSelectionTableItem[] getRows();
    /**
     * <p>Remove a set of rows from the table data source and fire to Listeners
     * 
     * @param rows rows number to remove from the data source
     * @throws Exception if an exception occurs
     * */   
    public abstract void deleteRows(int[] rows) throws IndexOutOfBoundsException;
    /**
     * <p>Remove a row from the table data source and fire to Listeners
     * 
     * @param row row number to remove from the data source
     * @throws Exception if an exception occurs
     * */
    public abstract void deleteRow(int row) throws IndexOutOfBoundsException;    
    /**
     * Moves down a set of rows to the table data source and fire to Listeners
     * @param rows Row numbers to move from the data source
     */
    public abstract void moveDownRows(int[] rows)throws IndexOutOfBoundsException;
    /**
     * Moves down a row to the table data source and fire to Listeners
     * @param row Row number to remove from the data source
     */
    public abstract void moveDownRow(int row) throws IndexOutOfBoundsException;
    /**
     * Moves up a set of rows to the table data source and fire to Listeners
     * @param rows Row numbers to move from the data source
     */
    public abstract void moveUpRows(int[] rows)throws IndexOutOfBoundsException;
    /**
     * Moves up a row to the table data source and fire to Listeners
     * @param row Row number to move from the data source
     */
    public abstract void moveUpRow(int row)throws IndexOutOfBoundsException;
    /**
     * Add a row to the table data source if maxRowsNumber is not reached and fire to Listeners
     * @param inputData <code>PdfSelectionTableItem</code> to add to the data source
     */
    public abstract void addRowAt(int index, PdfSelectionTableItem inputData);
    /**
     * Add a row to the table data source if maxRowsNumber is not reached and fire to Listeners
     * @param inputData <code>PdfSelectionTableItem</code> to add to the data source
     */
    public abstract void addRow(PdfSelectionTableItem inputData);
    /**
     * Removes any data source for the model
     */
    public abstract void clearData();
    /**
     * set data source for the model
     * @param inputData array <code>PdfSelectionTableItem[]</code> as data source
     */
    public abstract void setData(PdfSelectionTableItem[] inputData);
    /**
     * Return the value at row
     */
    public abstract PdfSelectionTableItem getRow(int row);
    
    /*********************sort features ***********/
    
    /**
     * Sort the data 
     */
    public abstract void sort();
    /**
	 * @return the sortingState
	 */
	public abstract SortingState getSortingState();
	/**
	 * sets the sorting state
	 * @param sortingState
	 */
	public abstract void setSortingState(SortingState sortingState);
	
    /**
     * Model of a sorting state (column and sort type)
     * @author Andrea Vacondio
     *
     */
    public class SortingState implements Serializable{

		private static final long serialVersionUID = 3051421044350063901L;

		private int col = -1;
    	private int sortType = NOT_SORTED;
		
    	public SortingState(){    		
    	}
    	
    	/**
		 * @param col
		 * @param sortType
		 */
		public SortingState(int col, int sortType) {
			this.col = col;
			this.sortType = sortType;
		}
		/**
		 * @return the col
		 */
		public int getCol() {
			return col;
		}
		/**
		 * @param col the col to set
		 */
		public void setCol(int col) {
			this.col = col;
		}
		/**
		 * @return the sortType
		 */
		public int getSortType() {
			return sortType;
		}
		/**
		 * @param sortType the sortType to set
		 */
		public void setSortType(int sortType) {
			this.sortType = sortType;
		}    	
		/**
		 * @return true if sorted
		 */
	    public boolean isSorted(){
	    	return (sortType==DESCENDING || sortType==ASCENDING);
	    }
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + sortType;
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SortingState other = (SortingState) obj;
			if (col != other.col)
				return false;
			if (sortType != other.sortType)
				return false;
			return true;
		}
	    
	    public String toString(){
	    	return "[col="+col+" sortType="+sortType+"]";
	    }
    }
}
