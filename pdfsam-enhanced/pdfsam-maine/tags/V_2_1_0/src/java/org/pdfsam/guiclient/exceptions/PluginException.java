/*
 * Created on 03-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
 * Plugin Exception
 * @author Andrea Vacondio
 */
public class PluginException extends Exception {

	private static final long serialVersionUID = 4102211401228993071L;

	public PluginException() {
		super();
	}

	public PluginException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PluginException(String arg0) {
		super(arg0);
	}

	public PluginException(Throwable arg0) {
		super(arg0);
	}


}
