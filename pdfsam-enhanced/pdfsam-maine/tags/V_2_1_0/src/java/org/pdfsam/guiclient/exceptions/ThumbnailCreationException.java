/*
 * Created on 27-Sep-2008
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
/**
 * Thumbnail creation exception
 * @author Andrea Vacondio
 *
 */
public class ThumbnailCreationException extends Exception {

	private static final long serialVersionUID = 4787815531944278473L;

	public ThumbnailCreationException() {
		super();
	}

	public ThumbnailCreationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ThumbnailCreationException(String arg0) {
		super(arg0);
	}

	public ThumbnailCreationException(Throwable arg0) {
		super(arg0);
	}

}
