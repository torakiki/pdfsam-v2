/*
 * Created on 31-Mar-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business;

/**
 * singleton to generates a unique id
 * @author Andrea Vacondio
 *
 */
public class IdGenerator {

	private static IdGenerator instance = null;
	private long id = 0;
	
	private IdGenerator(){
	
	}

	public static synchronized IdGenerator getInstance() { 
		if (instance == null){
			instance = new IdGenerator();
		}
		return instance;
	}

	/**
	 * @return a newly generated id
	 */
	public synchronized long getNewId(){
		id++;
		return id;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone IdGenerator object.");
	}
}
