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

public class ListItem {
	private String value;
	private String id;
	
	/**
	 * Costruttore. 
	 * Crea un'istanza di ListItem.
	 * */
	public ListItem() {
		this.value = "";
		this.id = "";
	}
	/**
	 * Costruttore. 
	 * Crea un'istanza di ListItem.
	 * 
	 * @param id Id associato al valore
	 * @param value Valore
	 * */	
	public ListItem(String id, String value ) {
		this.value = value;
		this.id = id;
	}
	
	/**
	 * Costruttore. 
	 * Crea un'istanza di ListItem.
	 * 
	 * @param id Id associato al valore
	 * @param value Valore
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
	 * 
	 * @return Il valore del ListItem
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * 
	 * @return L'id associato al valore
	 */	
	public String getId() {
		return id;
	}

    /**
     * Metodo utilizzato per settare il valore della propriet� <code>inputvalue</code>
     * @param inputvalue Valore da settare.
     */
	public void setValue(String inputvalue) {
		this.value = inputvalue;
	}
	
    /**
     * Metodo utilizzato per settare il valore della propriet� <code>inputid</code>
     * @param inputid ID da settare.
     */	
	public void setId(String inputid) {
		this.id = inputid;
	}
	
}

