/*
 * Created on 21-Sep-2007
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
package org.pdfsam.console.business.parser.validators;

import java.io.File;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.dto.PdfFile;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
/**
 * CmdValidator for the mix command
 * @author Andrea Vacondio
 */
public class MixCmdValidator extends AbstractCmdValidator {

	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		MixParsedCommand parsedCommandDTO = new MixParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption("o");
	        if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            //checking extension
	            if ((outFile.getName().toLowerCase().endsWith(PDF_EXTENSION)) && (outFile.getName().length()>PDF_EXTENSION.length())){
	            	parsedCommandDTO.setOutputFile(outFile);	
	    		}           
	            else{
	            	throw new ParseException(ParseException.ERR_OUT_NOT_PDF, new String[]{outFile.getPath()});
	            }
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
	        
	        //-f1
			PdfFileParam f1Option = (PdfFileParam) cmdLineHandler.getOption("f1");           
	        if(f1Option.isSet()){
	        	PdfFile firstFile = f1Option.getPdfFile();
	            if ((firstFile.getFile().getPath().toLowerCase().endsWith(PDF_EXTENSION))){
	            	parsedCommandDTO.setFirstInputFile(FileUtility.getPdfFile(firstFile));                  
	            }else{
	            	throw new ParseException(ParseException.ERR_OUT_NOT_PDF, new String[]{firstFile.getFile().getName()});
	            }
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F1);	
	        }
	        
	        //-f2
	        PdfFileParam f2Option = (PdfFileParam) cmdLineHandler.getOption("f2");           
	        if(f2Option.isSet()){
	        	PdfFile secondFile = f2Option.getPdfFile();
	            if ((secondFile.getFile().getPath().toLowerCase().endsWith(PDF_EXTENSION))){
	            	parsedCommandDTO.setSecondInputFile(FileUtility.getPdfFile(secondFile));
	            }else{
	            	throw new ParseException(ParseException.ERR_OUT_NOT_PDF, new String[]{secondFile.getFile().getName()});
	            }
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F2);	
	        }           
	
	        //-reversefirst
	        parsedCommandDTO.setReverseFirst(((BooleanParam) cmdLineHandler.getOption("reversefirst")).isTrue());
	        //-reversesecond
	        parsedCommandDTO.setReverseSecond(((BooleanParam) cmdLineHandler.getOption("reversesecond")).isTrue());
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
        return parsedCommandDTO;
	}		

}
