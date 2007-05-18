/*
 * Created on 27-Jun-2006
 * Copyright notice: this code is based on Split and Burst classes by Mark Thompson. Copyright (c) 2002 Mark Thompson.
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
package it.pdfsam.console.tools;

import java.text.DateFormat;
import java.util.Date;

public class LogFormatter {
    /**
     * Format the input message for the log TextBox
     * @param message
     * @return formatted message
     */
    public static String formatMessage(String message){
        return "["+DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(new Date())+"] "+message;
    }
}
