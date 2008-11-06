/*
 * Created on 01-Oct-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
import java.util.regex.Pattern;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
/**
 * CmdValidator for the concat command
 * @author Andrea Vacondio
 */
public class ConcatCmdValidator extends AbstractCmdValidator {

	private final static String ALL_STRING = "all";
	
	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		ConcatParsedCommand parsedCommandDTO = new ConcatParsedCommand();
		
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
	
			//-f -l
			FileParam lOption = (FileParam) cmdLineHandler.getOption("l");
			PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption("f");
			if(lOption.isSet() || fOption.isSet()){
				if(!(lOption.isSet() && fOption.isSet())){
					if(fOption.isSet()){
						//validate file extensions
						for(Iterator fIterator = fOption.getPdfFiles().iterator(); fIterator.hasNext();){
							PdfFile currentFile = (PdfFile) fIterator.next();
			        		if (!((currentFile.getFile().getName().toLowerCase().endsWith(PDF_EXTENSION)) && (currentFile.getFile().getName().length()>PDF_EXTENSION.length()))){
			        			throw new ParseException(ParseException.ERR_IN_NOT_PDF, new String[]{currentFile.getFile().getPath()});
			        		}
			        	}
						parsedCommandDTO.setInputFileList(FileUtility.getPdfFiles(fOption.getPdfFiles()));
					}else{
						if(lOption.getFile().getPath().toLowerCase().endsWith(CSV_EXTENSION) || lOption.getFile().getPath().toLowerCase().endsWith(XML_EXTENSION)){
							parsedCommandDTO.setInputCvsOrXmlFile(lOption.getFile());
						}else{
							throw new ParseException(ParseException.ERR_NOT_CSV_OR_XML);
						}
					}
				}else{
					throw new ParseException(ParseException.ERR_BOTH_F_OR_L);
				}    			
			}else{
				throw new ParseException(ParseException.ERR_NO_F_OR_L);
			}
	
			//-u
			StringParam uOption = (StringParam) cmdLineHandler.getOption("u");            
	        //if it's set we proceed with validation
	        if (uOption.isSet()){
	            Pattern p = Pattern.compile("(((([\\d]+[-][\\d]*)|([\\d]+))(,(([\\d]+[-][\\d]*)|([\\d]+)))*[:])|("+ALL_STRING+":))+", Pattern.CASE_INSENSITIVE);
	            if ((p.matcher(uOption.getValue()).matches())){
	            	parsedCommandDTO.setPageSelection(uOption.getValue());
	            }
	            else{
	            	throw new ParseException(ParseException.ERR_ILLEGAL_U, new String[]{uOption.getValue()});
	            }
	        }
	
	        //-copyfields
	        parsedCommandDTO.setCopyFields(((BooleanParam) cmdLineHandler.getOption("copyfields")).isTrue());
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;
	}


	
}
