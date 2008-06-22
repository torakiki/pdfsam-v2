/*
 * Created on 16-Oct-2007
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
package org.pdfsam.console.business.parser.validators.interfaces;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.exceptions.console.ConsoleException;
/**
 * Abstract command validator
 * @author Andrea Vacondio
 *
 */
public abstract class  AbstractCmdValidator implements CmdValidator{

	public AbstractParsedCommand validate(CmdLineHandler cmdLineHandler)
			throws ConsoleException {
		AbstractParsedCommand parsedCommand = validateArguments(cmdLineHandler);
		if(cmdLineHandler.getOption(AbstractParsedCommand.OVERWRITE_ARG) != null){
			parsedCommand.setOverwrite(((BooleanParam) cmdLineHandler.getOption(AbstractParsedCommand.OVERWRITE_ARG)).isTrue());			
		}
		if(cmdLineHandler.getOption(AbstractParsedCommand.COMPRESSED_ARG) != null){
			parsedCommand.setCompress(((BooleanParam) cmdLineHandler.getOption(AbstractParsedCommand.COMPRESSED_ARG)).isTrue());
		}
		if(cmdLineHandler.getOption(AbstractParsedCommand.LOG_ARG) != null){
	        FileParam logOption = (FileParam) cmdLineHandler.getOption(AbstractParsedCommand.LOG_ARG);
	        if (logOption.isSet()){
	        	parsedCommand.setLogFile(logOption.getFile());
	        }
        }
		if(cmdLineHandler.getOption(AbstractParsedCommand.PDFVERSION_ARG) != null){
	        StringParam pdfversionOption = (StringParam) cmdLineHandler.getOption(AbstractParsedCommand.PDFVERSION_ARG);
	        if (pdfversionOption.isSet()){
	        	parsedCommand.setOutputPdfVersion(pdfversionOption.getValue().charAt(0));
	        }
        }
		return parsedCommand;
	}

	/**
	 * Perform validation for the specific CmdHandler
	 * @param cmdLineHandler
	 * @return the parsed command
	 * @throws ConsoleException
	 */
	protected abstract AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler)
			throws ConsoleException ;

}
