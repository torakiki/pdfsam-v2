/*
 * Created on 3-mar-2005
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
package it.pdfsam.types;

/**
 * Utility class used in theme selector
 * @author  Andrea Vacondio
 * @see     it.pdfsam.utils.ThemeSelector
 */

public class ListItem implements Comparable{
	private String value;
	private String id;
	
	/**
	 * Constructor
	 * */
	public ListItem() {
		this.value = "";
		this.id = "";
	}
	/**
	 * Constructor. 
	 * @param id
	 * @param value
	 * */	
	public ListItem(String id, String value ) {
		this.value = value;
		this.id = id;
	}
	
	/**
	 * Constructor. 
	 * @param id 
	 * @param value
	 * */	
	public ListItem(int id, String value ) {
		try{
		    this.value = value;
			this.id = Integer.toString(id);
		}
		catch(Exception e){
		    this.value = "";
			this.id = "";		    
		}
	}
	
	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @return id
	 */	
	public String getId() {
		return id;
	}

    /**
     * sets the <code>inputvalue</code> property
     * @param inputvalue
     */
	public void setValue(String inputvalue) {
		this.value = inputvalue;
	}
	
    /**
     * sets the <code>inputid</code> property
     * @param inputid
     */	
	public void setId(String inputid) {
		this.id = inputid;
	}
	
	/**
	 * Compare two objects
	 */
	public int compareTo(Object arg0) {
		int retVal = 0;
		if (!(arg0 instanceof ListItem)){
	      throw new ClassCastException("ListItem object expected.");
	    }else{
	    	retVal = this.getValue().compareTo(((ListItem)arg0).getValue());
	    }
		return retVal;
	}
	
}

