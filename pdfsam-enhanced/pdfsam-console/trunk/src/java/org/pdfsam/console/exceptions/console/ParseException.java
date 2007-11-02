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
 * Exception thrown while parsing input arguments
 * @author Andrea Vacondio
 *
 */
public class ParseException extends ConsoleException {

	public final static int ERR_PARSE = 0x01;
	public final static int ERR_OUT_NOT_PDF = 0x02;
	public final static int ERR_NO_OUT = 0x03;
	public final static int ERR_NO_F_OR_L = 0x04;
	public final static int ERR_BOTH_F_OR_L = 0x05;
	public final static int ERR_NOT_CSV_OR_XML = 0x06;
	public final static int ERR_IN_NOT_PDF = 0x07;
	public final static int ERR_ILLEGAL_U = 0x08;
	public final static int ERR_OUT_NOT_DIR = 0x09;
	public final static int ERR_NO_S = 0x0A;
	public final static int ERR_N_NOT_NUM = 0x0B;
	public final static int ERR_N_NOT_NUM_OR_SEQ = 0x0C;
	public final static int ERR_NO_N = 0x0D;
	public final static int ERR_N_NOT_NEEDED = 0x0E;
	public final static int ERR_NO_O = 0x0F;
	public final static int ERR_NO_F1 = 0x10;
	public final static int ERR_NO_F2 = 0x11;
	public final static int ERR_B_NOT_NEEDED = 0x12;
	public final static int ERR_NO_B = 0x13;
	public final static int ERR_NO_F = 0x14;
	
    private static final long serialVersionUID = -3982153307443637295L;

	public ParseException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public ParseException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public ParseException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public ParseException(Throwable e) {
		super(e);
	}
	
	public ParseException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}

}
