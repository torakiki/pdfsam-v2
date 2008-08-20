/*
 * Created on 18-Oct-2006
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
package org.pdfsam.guiclient.exceptions;
/**
 * Exception thrown while saving job
 * @author Andrea Vacondio
 *
 */
public class SaveJobException extends Exception {

	private static final long serialVersionUID = -4149496837957062920L;

	public SaveJobException() {
		super();
	}

	public SaveJobException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SaveJobException(String arg0) {
		super(arg0);
	}

	public SaveJobException(Throwable arg0) {
		super(arg0);
	}

}