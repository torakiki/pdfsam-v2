/*
 * Created on 27-oct-2007
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
package org.pdfsam.console.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formats time in human readable format
 * @author torakiki
 *
 */
public class TimeUtility {

	private static final String MILLIS_FORMAT= "S'ms'";
	private static final String SECONDS_FORMAT= "s's' S'ms'";
	private static final String MINUTES_FORMAT= "m'm' s's' S'ms'";
	private static final String HOURS_FORMAT= "H'h' m'm' s's' S'ms'";
	private static final String DAYS_FORMAT= "D'd' H'h' m'm' s's' S'ms'";
	
	private static final long MILLIS= 1000;
	private static final long SECONDS= MILLIS*60;
	private static final long MINUTES= SECONDS*60;
	private static final long HOURS= MINUTES*24;
	
	/**
	 * formats the millis in human readable format
	 * @param millis
	 * @return formatted String
	 */
	public static String format(long millis){
		String retVal = "";
		if(millis < MILLIS){
			retVal = new SimpleDateFormat(MILLIS_FORMAT).format(new Date(millis));
		}else if(millis < SECONDS){
			retVal = new SimpleDateFormat(SECONDS_FORMAT).format(new Date(millis));
		} else if(millis < MINUTES){
			retVal = new SimpleDateFormat(MINUTES_FORMAT).format(new Date(millis));
		} else if(millis < HOURS){
			retVal = new SimpleDateFormat(HOURS_FORMAT).format(new Date(millis));
		} else {
			retVal = new SimpleDateFormat(DAYS_FORMAT).format(new Date(millis));
		} 
		return retVal;
	}
}
