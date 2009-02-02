/*
 * Created on 26-Feb-2008
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
package org.pdfsam.guiclient.exceptions;

public class CheckForUpdateException extends Exception {

	private static final long serialVersionUID = -5334324806135020784L;

	public CheckForUpdateException() {
		super();
	}

	public CheckForUpdateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CheckForUpdateException(String arg0) {
		super(arg0);
	}

	public CheckForUpdateException(Throwable arg0) {
		super(arg0);
	}

}
