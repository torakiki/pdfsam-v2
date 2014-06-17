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

import java.util.HashSet;

/**
 * singleton to generates a unique id
 * @author Andrea Vacondio
 *
 */
public class IdManager {

	private static IdManager instance = null;
	
	private long id = 0;
	private  HashSet<Long> cancelledExecutions = null;
	
	private IdManager(){
		cancelledExecutions =  new HashSet<Long>();
	}

	public static synchronized IdManager getInstance() { 
		if (instance == null){
			instance = new IdManager();
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
		throw new CloneNotSupportedException("Cannot clone IdManager object.");
	}
	
	
	/**
	 * Threads with the given id wont generate thumbnails
	 * @param id
	 */
	public void cancelExecution(final long id){
		synchronized(cancelledExecutions){
			cancelledExecutions.add(id);
		}
	}
	
	/**
	 * @param id
	 * @return true if the execution is cancelled
	 */
	public boolean  isCancelledExecution(final long id){
		boolean retVal = false;
		if(cancelledExecutions.size()>0){
			retVal = cancelledExecutions.contains(id);
		}
		return retVal;
	}
	
	/**
	 * remove a cancelled execution id from the table
	 * @param id
	 */
	public void removeCancelledExecution(final long id){
		synchronized(cancelledExecutions){
			if(cancelledExecutions.size()>0 && cancelledExecutions.contains(id)){
				cancelledExecutions.remove(id);
			}
		}
	}
}
