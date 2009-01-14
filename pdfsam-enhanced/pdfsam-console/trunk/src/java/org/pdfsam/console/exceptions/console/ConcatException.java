/*
 * Created on 20-Jun-2007
 * Copyright (C) 2007 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
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
	public final static int ERR_WRONG_ROTATION = 0x07;
	public final static int ERR_DEGREES_NOT_ALLOWED = 0x08;
	public final static int ERR_PARAM_ROTATION = 0x09;
	
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
