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
package org.pdfsam.console.business.parser.validators.interfaces;

import jcmdline.CmdLineHandler;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.exceptions.console.ConsoleException;

/**
 * Interface for the command line validators
 * @author Andrea Vacondio
 *
 */
public interface CmdValidator {
	
	public static final String PDF_EXTENSION = ".pdf";
	public static final String XML_EXTENSION = ".xml";	
	public static final String CSV_EXTENSION = ".csv";	
	
	/**
	 * called by the Manager to validate input arguments for this validator.
	 * @return the dto containing parsed command
	 * @throws ConsoleException
	 */
	public AbstractParsedCommand validate(CmdLineHandler cmdLineHandler) throws ConsoleException;
}
