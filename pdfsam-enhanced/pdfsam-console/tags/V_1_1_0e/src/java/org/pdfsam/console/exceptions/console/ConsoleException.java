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

import org.pdfsam.console.exceptions.BasicPdfsamException;

/**
 * Generic console Exception
 * @author Andrea Vacondio
 */
public class ConsoleException extends BasicPdfsamException {

	public final static int ERR_ZERO_LENGTH = 0x01;
	public final static int CMD_LINE_HANDLER_NULL = 0x02;
	public final static int EMPTY_FILENAME = 0x03;
	public final static int CMD_LINE_VALIDATOR_NULL = 0x04;
	public final static int ERR_BAD_COMMAND = 0x05;
	public final static int CMD_LINE_EXECUTOR_NULL = 0x06;
	public final static int CMD_LINE_NULL = 0x07;
	
	private static final long serialVersionUID = -853792961862291208L;

	public ConsoleException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public ConsoleException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public ConsoleException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public ConsoleException(Throwable e) {
		super(e);
	}

	public ConsoleException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}

}
