/*
 * Created on 10-Feb-2006
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
package it.pdfsam.console.exception;
/**
 * Exception thrown while parsing input arguments
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.CmdParser
 *
 */
public class ParseException extends ConsoleException {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3982153307443637295L;

    public ParseException() {
        super("GenericParseException");
    }

    public ParseException(String message) {
        super(message);
    }
    
    public ParseException(Exception e) {
        super(e);
    }
    
    public String toString(){
        return super.toString();
    }

}
