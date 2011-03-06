/*
 * Created on 25-Jul-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
 * Exception during parameters validation
 * @author Andrea Vacondio
 *
 */
public class ValidationException extends ConsoleException {

	public static final int ERR_WRONG_ROTATION = 0x01;
	public static final int ERR_DEGREES_NOT_ALLOWED = 0x02;
	public static final int ERR_PARAM_ROTATION = 0x03;
	public static final int ERR_NOT_PDF = 0x04;	
	public static final int ERR_NOT_DIR = 0x05;	
	public static final int ERR_WRONG_PAGE_LABEL = 0x06;
	public static final int ERR_UNK_LABEL_STYLE = 0x07;
	public static final int ERR_NOT_VALID_LABEL_PREFIX = 0x08;
	public static final int ERR_ILLEGAL_U = 0x09;
	public static final int ERR_NOT_POSITIVE = 0x0A;
	public static final int ERR_CANNOT_MERGE = 0x0B;
	public static final int ERR_START_BIGGER_THAN_END = 0x0C;
    public static final int ERR_BOUNDS_INTERSECTS = 0x0D;
	
	private static final long serialVersionUID = 8695556302543417406L;
	
	public ValidationException(final int exceptionErrorCode, final String[] args, final Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public ValidationException(final int exceptionErrorCode, final Throwable e) {
		super(exceptionErrorCode, e);
	}

	public ValidationException(final int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public ValidationException(final Throwable e) {
		super(e);
	}

	public ValidationException(final int exceptionErrorCode, final String[] args) {
		super(exceptionErrorCode, args);
	}
}
