/*
 * Created on 06-Nov-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.DocumentInfoParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;

/**
 * CmdValidator for the document info command
 * @author Andrea Vacondio
 */
public class DocumentInfoCmdValidator extends AbstractCmdValidator {

	protected AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {

		DocumentInfoParsedCommand parsedCommandDTO = new DocumentInfoParsedCommand();

		if (cmdLineHandler != null) {
			// -o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.O_ARG);
			if ((oOption.isSet())) {
				File outFile = oOption.getFile();
				// checking extension
				ValidationUtility.assertValidPdfExtension(outFile.getName());
				parsedCommandDTO.setOutputFile(outFile);

			} else {
				throw new ParseException(ParseException.ERR_NO_O);
			}
			
			//-f
	        PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.F_ARG);           
	        if(fOption.isSet()){
	        	PdfFile inputFile = fOption.getPdfFile();
	        	ValidationUtility.assertValidPdfExtension(inputFile.getFile().getName());
	            parsedCommandDTO.setInputFile(FileUtility.getPdfFile(inputFile));                  
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F);	
	        }
	        
	        //-author
	        StringParam authorOption = (StringParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.AUTHOR_ARG);
	        if(authorOption.isSet()){
	        	parsedCommandDTO.setAuthor(authorOption.getValue());		     
	        }
	        
	        //-title
	        StringParam titleOption = (StringParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.TITLE_ARG);
	        if(titleOption.isSet()){
	        	parsedCommandDTO.setTitle(titleOption.getValue());		     
	        }
	        
	        //-subject
	        StringParam subjectOption = (StringParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.SUBJECT_ARG);
	        if(subjectOption.isSet()){
	        	parsedCommandDTO.setSubject(subjectOption.getValue());		     
	        }
	        
	        //-keywords
	        StringParam keywordsOption = (StringParam) cmdLineHandler.getOption(DocumentInfoParsedCommand.KEYWORDS_ARG);
	        if(keywordsOption.isSet()){
	        	parsedCommandDTO.setKeywords(keywordsOption.getValue());		     
	        }
	        
		} else {
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;
	}

}
