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
package org.pdfsam.guiclient.commons.models;

import java.util.Collections;
import java.util.Comparator;

import org.pdfsam.guiclient.dto.PdfSelectionTableItem;

/**
 * Model for the JPdfSelectionTable with sort features
 * @author Andrea Vacondio
 *
 */
public class SortablePdfSelectionTableModel extends SimplePdfSelectionTableModel {
    
  
	private static final long serialVersionUID = -3947614487255713123L;
	
	private SortingState sortingState = new SortingState();
    
	public final PdfSelectionTableItemComparator comparator = new PdfSelectionTableItemComparator();

	public SortablePdfSelectionTableModel() {
		super();
		setSortable(true);
	}

	/**
	 * @param showedColumns
	 * @param maxRowsNumber
	 */
	public SortablePdfSelectionTableModel(int showedColumns, int maxRowsNumber) {
		super(showedColumns, maxRowsNumber);
		setSortable(true);
	}

	/**
	 * @return the sortingState
	 */
	public SortingState getSortingState() {
		return sortingState;
	}

	/**
	 * sets the sorting states and sort
	 * @param sortingState the sortingState to set
	 */
	public void setSortingState(SortingState sortingState) {
		this.sortingState = sortingState;
		sort();
	}

	/**
	 * sets the sorting states and sort
	 * @param col column
	 * @param sortType sortType
	 */
	public void setSortingState(int col, int sortType) {
		this.sortingState.setCol(col);
		this.sortingState.setSortType(sortType);
		sort();
	}
	
	/**
	 * set the sorting state to NOT_SORTING
	 */
	public void clearSortingState(){
		this.sortingState.setCol(-1);
		this.sortingState.setSortType(NOT_SORTED);
	}
	
    /**
     * Sort the data 
     */
    public void sort(){
    	if(NOT_SORTED != sortingState.getSortType()){
    		comparator.setSortingState(sortingState);
    		Collections.sort(data, comparator);
    		this.fireTableDataChanged();
    	}
    }
    
    /**
     * reset sorting informations
     */
    private void cancelSorting(){
    	if(sortingState!=null && NOT_SORTED != sortingState.getSortType()){
    		sortingState.setSortType(NOT_SORTED);
    		this.fireTableStructureChanged();
    	}
    }

    public void addRow(PdfSelectionTableItem inputData){
    	cancelSorting();
    	super.addRow(inputData);
    }
    
    public void addRowAt(int index, PdfSelectionTableItem inputData){
    	cancelSorting();
    	super.addRowAt(index, inputData);
    }
   

    public void moveUpRow(int row)throws IndexOutOfBoundsException{
    	cancelSorting();
    	super.moveUpRow(row);
    }
    

    public void moveUpRows(int[] rows)throws IndexOutOfBoundsException{
    	cancelSorting();
    	super.moveUpRows(rows);
    } 
    
    public void moveDownRow(int row) throws IndexOutOfBoundsException{
    	cancelSorting();
    	super.moveDownRow(row);
    } 
    
   
    public void moveDownRows(int[] rows)throws IndexOutOfBoundsException{
    	cancelSorting();
    	super.moveDownRows(rows);        
    }
    
    /**
     * comparator for the PdfSelectionTableItem
     * @author Andrea Vacondio
     *
     */
    public class  PdfSelectionTableItemComparator implements Comparator{
    	
    	private SortingState sortingState = new SortingState();
    	
        public int compare(Object o1, Object o2) {
        	int retVal = 0;
        	if(sortingState.getCol() == -1){
        		retVal = o1.toString().compareTo(o2.toString());
        	}else{
        		Object first;
        		Object second;
        		switch(sortingState.getCol()){
    			case FILENAME:
    				first = (((PdfSelectionTableItem)o1).getInputFile() != null)? ((PdfSelectionTableItem)o1).getInputFile().getName(): "";
    				second = (((PdfSelectionTableItem)o2).getInputFile() != null)? ((PdfSelectionTableItem)o2).getInputFile().getName(): "";
    				break;
    			case PATH:
    				first =  (((PdfSelectionTableItem)o1).getInputFile() != null)? ((PdfSelectionTableItem)o1).getInputFile().getAbsolutePath(): "";
    				second = (((PdfSelectionTableItem)o2).getInputFile() != null)? ((PdfSelectionTableItem)o2).getInputFile().getAbsolutePath(): "";
    				break;
    			case PAGES:
    				first = (((PdfSelectionTableItem)o1).getPagesNumber() != null)? new Integer(((PdfSelectionTableItem)o1).getPagesNumber()): new Integer("0");
    				second = (((PdfSelectionTableItem)o2).getPagesNumber() != null)? new Integer(((PdfSelectionTableItem)o2).getPagesNumber()): new Integer("0");
    				break;
    			case PASSWORD:
    				first = (((PdfSelectionTableItem)o1).getPassword() != null)? ((PdfSelectionTableItem)o1).getPassword(): "";
    				second = (((PdfSelectionTableItem)o2).getPassword() != null)? ((PdfSelectionTableItem)o2).getPassword(): "";
    				break;
    			case PAGESELECTION:
    				first = (((PdfSelectionTableItem)o1).getPageSelection() != null)? ((PdfSelectionTableItem)o1).getPageSelection(): "";
    				second = (((PdfSelectionTableItem)o2).getPageSelection() != null)? ((PdfSelectionTableItem)o2).getPageSelection(): "";
    				break;
    			default:
    				first = o1;
    				second = o2;
    				break;
        		}
        		if (Comparable.class.isAssignableFrom(first.getClass())) {
        			if(sortingState.getSortType() == DESCENDING){
        				retVal = ((Comparable) first).compareTo(second);
        			}else{
        				retVal = ((Comparable) second).compareTo(first);
        			}
        		}else{
	        		if(sortingState.getSortType() == DESCENDING){
	        			retVal = first.toString().compareTo(second.toString());
	        		}else{
	        			retVal = second.toString().compareTo(first.toString());
	        		}
        		}
            }
        	return retVal;
        }

		/**
		 * @return the sortingState
		 */
		public SortingState getSortingState() {
			return sortingState;
		}

		/**
		 * @param sortingState the sortingState to set
		 */
		public void setSortingState(SortingState sortingState) {
			this.sortingState = sortingState;
		}   
    }
    
}
