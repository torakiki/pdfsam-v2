/*
 * Created on 16-Oct-2007
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
package org.pdfsam.console.business.parser.handlers.interfaces;

import java.util.Collection;

import jcmdline.CmdLineHandler;

/**
 * Interface for the command line handler
 * @author Andrea Vacondio
 *
 */
public interface CmdHandler {
	
	public static final String COMMAND = "java -jar pdfsam-console-VERSION.jar";
	
	/**
	 * @return Help message for this handler
	 */
	String getHelpMessage();
	
	/**
	 * @return Help examples message for this handler
	 */
	String getHelpExamples();

	/**
	 * @return Options for this handler
	 */
	Collection getOptions();
	
	/**
	 * @return Arguments for this handler
	 */
	Collection getArguments();
	
	/**
	 * @return Description for the command of this handler
	 */
	String getCommandDescription();
	
	/**
	 * @return the command line handler for this handler
	 */
	public CmdLineHandler getCommandLineHandler();
}
