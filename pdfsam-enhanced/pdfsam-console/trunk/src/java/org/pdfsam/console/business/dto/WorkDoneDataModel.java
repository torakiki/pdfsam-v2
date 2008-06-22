/*
 * Created on 21-oct-2007
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.dto;

import java.io.Serializable;
/**
 * Percentage of work done
 * @author Andrea Vacondio
 *
 */
public class WorkDoneDataModel implements Serializable {

	private static final long serialVersionUID = 2224240472572871331L;

	public static final int INDETERMINATE = -1;
	public static final int MAX_PERGENTAGE = 1000;
	
	private int percentage = 0;

	public WorkDoneDataModel() {
		this.percentage = 0;
	}

	/**
	 * @return the percentage
	 */
	public int getPercentage() {
		return percentage;
	}
	
	/**
	 * sets the percentage to indeterminate
	 */
	public void setPercentageIndeterminate(){
		percentage = INDETERMINATE;
	}

	/**
	 * sets the percentage to the max value
	 */
	public void setPercentageMax(){
		percentage = MAX_PERGENTAGE;
	}
	
	/**
	 * @param percentage the percentage to set (0 to MAX_PERCENTAGE or -1 to set the percentage indeterminate)
	 */
	public void setPercentage(int percentage) {
		if(percentage > MAX_PERGENTAGE){
			this.percentage = MAX_PERGENTAGE;
		}else if(percentage < INDETERMINATE){
			this.percentage = INDETERMINATE;
		}else{
			this.percentage = percentage;
		}
	}
	
	/**
	 * reset the percentage
	 */
	public void resetPercentage(){
		this.percentage = 0;
	}
	
	public String toString(){
		return percentage+"";
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + percentage;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		boolean retVal = false;
		if(this == obj){
			retVal = true;
		}else{
			if((obj != null) && (getClass() == obj.getClass()) && (percentage == ((WorkDoneDataModel) obj).percentage)){
				retVal = true;
			}
		}
		return retVal;
	}
	
	
}
