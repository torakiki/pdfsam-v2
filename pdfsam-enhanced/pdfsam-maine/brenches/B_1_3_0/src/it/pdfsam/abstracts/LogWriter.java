/*
 * Created on 26-Dec-2006
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
package it.pdfsam.abstracts;
/**
 * interface for a log writer
 * @author a.vacondio
 *
 */
public interface LogWriter {
	/**
	 * Fires a log property changed
	 * @param log_msg log message
	 * @param log_level log level
	 */
	public void fireLogPropertyChanged(String log_msg, int log_level);
	
	/**
	 * @return The log message
	 */
	public String getLogMsg() ;
}
