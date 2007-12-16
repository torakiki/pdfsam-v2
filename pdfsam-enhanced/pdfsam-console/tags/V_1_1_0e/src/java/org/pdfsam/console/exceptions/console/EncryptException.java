/*
 * Created on 22-Oct-2007
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
package org.pdfsam.console.exceptions.console;
/**
 * Exception thrown while encrypting pdf files
 * @author Andrea Vacondio
 */

public class EncryptException extends ConsoleException {
	
	private static final long serialVersionUID = 7272105858262362686L;
	
	public EncryptException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public EncryptException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public EncryptException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public EncryptException(Throwable e) {
		super(e);
	}

	public EncryptException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}

}
