/*
 * Created on 31-Jan-2008
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
package org.pdfsam.console.business.parser.validators;

import java.io.File;
import java.util.Iterator;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.dto.PdfFile;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.UnpackParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;
/**
 * CmdValidator for the unpack command
 * @author Andrea Vacondio
 */
public class UnpackCmdValidator extends AbstractCmdValidator {

	protected AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		UnpackParsedCommand parsedCommandDTO = new UnpackParsedCommand();
		
		if(cmdLineHandler != null){	
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(UnpackParsedCommand.O_ARG);
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            ValidationUtility.assertValidDirectory(outFile);
	            parsedCommandDTO.setOutputFile(outFile);	
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
			
			//-f and -d
			PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(UnpackParsedCommand.F_ARG);
	        FileParam dOption = (FileParam) cmdLineHandler.getOption(UnpackParsedCommand.D_ARG);
			if (fOption.isSet() || dOption.isSet()) {
				// -f
				if (fOption.isSet()) {
					// validate file extensions
					for (Iterator fIterator = fOption.getPdfFiles().iterator(); fIterator.hasNext();) {
						PdfFile currentFile = (PdfFile) fIterator.next();
						ValidationUtility.assertValidPdfExtension(currentFile.getFile().getName());						
					}
					parsedCommandDTO.setInputFileList(FileUtility.getPdfFiles(fOption.getPdfFiles()));
				}
				// -d
				if ((dOption.isSet())) {
					File inputDir = dOption.getFile();
					ValidationUtility.assertValidDirectory(inputDir);
					parsedCommandDTO.setInputDirectory(inputDir);
				}
			} else {
				throw new ParseException(ParseException.ERR_NO_F_OR_D);
			}
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;	
	}

}
