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
package org.pdfsam.guiclient.dto;

import java.io.Serializable;
/**
 * Model of an int id and its description
 * @author  Andrea Vacondio
 */
public class IntItem implements Serializable{

	private static final long serialVersionUID = -2846709367724047792L;
	private int id;
	private String description;
	
	/**
	 * Constructor
	 * */
	public IntItem() {
	}
	/**
	 * Constructor. 
	 * @param id
	 * @param description
	 * */	
	public IntItem(int id, String description ) {
		this.id = id;
		this.description = description;
	}	
	
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @return id
	 */	
	public int getId() {
		return id;
	}

    /**
     * sets the <code>description</code> property
     * @param description 
     */
	public void setDescription(String description) {
		this.description = description;
	}
	
    /**
     * sets the <code>inputid</code> property
     * @param inputid
     */	
	public void setId(int id) {
		this.id = id;
	}	
	
	public String toString(){
		return description;
	}
	
}

