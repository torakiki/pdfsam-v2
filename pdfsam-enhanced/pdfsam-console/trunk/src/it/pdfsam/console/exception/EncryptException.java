/*
 * Created on 28-Dec-2006
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
 * Exception thrown while encrypting pdf files
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfEncrypt
 *
 */

public class EncryptException extends ConsoleException {

	private static final long serialVersionUID = 6742497449979418765L;

		public EncryptException() {
	        super("GenericEncryptException");
	    }

	    public EncryptException(String message) {
	        super(message);
	    }

	    public EncryptException(Exception e) {
	        super(e);
	    }

	    public String toString(){
	        return super.toString();
	    }
}

