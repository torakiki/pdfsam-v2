/*
 * Created on 16-Feb-2006
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
package it.pdfsam.exceptions;
/**
 * Exception thrown while an error occurs loading a plugin
 * @author Andrea Vacondio
 * @see it.pdfsam.utils.PlugInsLoader
 *
 */
public class PluginLoadException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = -6638483917590448696L;
    private String error_msg; 

    public PluginLoadException() {
        super();
        error_msg = "ExceptionLoadingPlugin";
    }

    public PluginLoadException(String message) {
        super(message);
        error_msg = message;
    }

    public PluginLoadException(String message, Throwable t) {
        super(message, t);
        error_msg = message;
    }

    public String toString(){
        return error_msg;
    }

}