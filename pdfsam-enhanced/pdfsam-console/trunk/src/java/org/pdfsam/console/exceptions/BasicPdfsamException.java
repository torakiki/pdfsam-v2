/*
 * Created on 20-June-2007
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
package org.pdfsam.console.exceptions;

import org.pdfsam.emp4j.exceptions.ClassNameKeyException;

/**
 * Basic pdfsam Exception
 * @author Andrea Vacondio
 */
public class BasicPdfsamException extends ClassNameKeyException {
	
	public final static int ERR_UNKNOWN = 0x01;
	
	private static final long serialVersionUID = -4603132507348839358L;

	public BasicPdfsamException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public BasicPdfsamException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public BasicPdfsamException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public BasicPdfsamException(Throwable e) {
		super(e);
	}

	public BasicPdfsamException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}

}
