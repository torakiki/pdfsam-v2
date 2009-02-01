/*
 * Created on 06-Mar-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
 * Exception thrown while setting viewer options to pdf files
 * @author Andrea Vacondio
 */
public class SetViewerException extends ConsoleException {

	private static final long serialVersionUID = -5870074661855916725L;

	public SetViewerException(int exceptionErrorCode, String[] args, Throwable e) {
		super(exceptionErrorCode, args, e);
	}

	public SetViewerException(int exceptionErrorCode, Throwable e) {
		super(exceptionErrorCode, e);
	}

	public SetViewerException(int exceptionErrorCode) {
		super(exceptionErrorCode);
	}

	public SetViewerException(Throwable e) {
		super(e);
	}

	public SetViewerException(int exceptionErrorCode, String[] args) {
		super(exceptionErrorCode, args);
	}
}
