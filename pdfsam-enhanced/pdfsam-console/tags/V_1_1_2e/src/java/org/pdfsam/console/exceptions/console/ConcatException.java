/*
 * Created on 20-Jun-2007
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
 * Exception thrown while merging pdf files
 * @author Andrea Vacondio
 *
 */
public class ConcatException extends ConsoleException {

	public final static int ERR_SYNTAX = 0x01;
	public final static int ERR_DEL_TEMP_FILE = 0x02;
	public final static int ERR_NOT_POSITIVE = 0x03;
	public final static int ERR_READING_CSV_OR_XML = 0x04;
	public final static int ERR_CANNOT_MERGE = 0x05;
	public final static int ERR_START_BIGGER_THAN_END = 0x06;
	
    private static final long serialVersionUID = -8242739056279169571L;

	public ConcatException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public ConcatException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public ConcatException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public ConcatException(Throwable e) {
		super(e);
	}

	public ConcatException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}
	
}
