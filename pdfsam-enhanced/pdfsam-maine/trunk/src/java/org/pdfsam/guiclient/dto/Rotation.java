/*
 * Created on 13-Jan-2009
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
package org.pdfsam.guiclient.dto;

/**
 * Enum to model a rotations set
 * @author Andrea Vacondio
 *
 */
public enum Rotation {
	
	DEGREES_0 (0), 
	DEGREES_90 (90), 
	DEGREES_180 (180), 
	DEGREES_270 (270);
	
	private int degrees;
	
	Rotation(int degrees){
		this.degrees = degrees;
	}

	/**
	 * @return the degrees
	 */
	public int getDegrees() {
		return degrees;
	}
	
	/**
	 * @param degrees rotation degrees
	 * @return corresponding rotation
	 */
	public static Rotation getRotation(int degrees){
		Rotation retVal = null;
		switch(degrees) {
	        case 90:   
	        		retVal = DEGREES_90;
	        		break;
	        case 180:  
	        		retVal = DEGREES_180;
	        		break;
	        case 270:  
	        		retVal = DEGREES_270;
	        		break;
	        case 0: 
	        		retVal = DEGREES_0;
	        		break;
	        default:
        			retVal = DEGREES_0;
	        		break;
	        	
		}
		return retVal;
	}
	
	/**
	 * @return a clockwise rotation
	 */
	public Rotation rotateClockwise(){
		Rotation retVal = null;
		switch(this) {
	        case DEGREES_0:   
	        		retVal = DEGREES_90;
	        		break;
	        case DEGREES_90:  
	        		retVal = DEGREES_180;
	        		break;
	        case DEGREES_180:  
	        		retVal = DEGREES_270;
	        		break;
	        case DEGREES_270: 
	        		retVal = DEGREES_0;
	        		break;
		}
		return retVal;
	}
	
	/**
	 * @return an anticlockwise rotation
	 */
	public Rotation rotateAnticlockwise (){
		Rotation retVal = null;
		switch(this) {
	        case DEGREES_0:   
	        		retVal = DEGREES_270;
	        		break;
	        case DEGREES_90:  
	        		retVal = DEGREES_0;
	        		break;
	        case DEGREES_180:  
	        		retVal = DEGREES_90;
	        		break;
	        case DEGREES_270: 
	        		retVal = DEGREES_180;
	        		break;
		}
		return retVal;
	}
}
