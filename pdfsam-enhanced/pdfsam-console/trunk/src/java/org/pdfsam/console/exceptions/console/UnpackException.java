/*
 * Created on 31-Jan-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
 * Exception thrown while unpack pdf files
 * @author Andrea Vacondio
 */
public class UnpackException extends ConsoleException {
	
	private static final long serialVersionUID = 6507276853411967680L;

	public UnpackException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public UnpackException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public UnpackException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public UnpackException(Throwable e) {
		super(e);
	}

	public UnpackException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}
}
