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
import org.pdfsam.console.business.dto.PageRotation;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;
/**
 * CmdValidator for the concat command
 * @author Andrea Vacondio
 */
public class ConcatCmdValidator extends AbstractCmdValidator {
		
	private static final String ALL_STRING = "all";
	
	public static final String SELECTION_REGEXP = "(((([\\d]+[-][\\d]*)|([\\d]+))(,(([\\d]+[-][\\d]*)|([\\d]+)))*[:])|(" + ALL_STRING + ":))+";

	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		ConcatParsedCommand parsedCommandDTO = new ConcatParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(ConcatParsedCommand.O_ARG);
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            //checking extension
	            ValidationUtility.checkValidPdfExtension(outFile.getName());
            	parsedCommandDTO.setOutputFile(outFile);	
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
	
			//-f -l -d
			FileParam lOption = (FileParam) cmdLineHandler.getOption(ConcatParsedCommand.L_ARG);
			PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(ConcatParsedCommand.F_ARG);
			FileParam dOption = (FileParam) cmdLineHandler.getOption(ConcatParsedCommand.D_ARG);
			if(lOption.isSet() || fOption.isSet() || dOption.isSet()){
				if(lOption.isSet() ^ fOption.isSet() ^ dOption.isSet()){
					if(fOption.isSet()){
						//validate file extensions
						for(Iterator fIterator = fOption.getPdfFiles().iterator(); fIterator.hasNext();){
							PdfFile currentFile = (PdfFile) fIterator.next();
			        		if (!((currentFile.getFile().getName().toLowerCase().endsWith(PDF_EXTENSION)) && (currentFile.getFile().getName().length()>PDF_EXTENSION.length()))){
			        			throw new ParseException(ParseException.ERR_IN_NOT_PDF, new String[]{currentFile.getFile().getPath()});
			        		}
			        	}
						parsedCommandDTO.setInputFileList(FileUtility.getPdfFiles(fOption.getPdfFiles()));
					}else if(lOption.isSet()){
						if(lOption.getFile().getPath().toLowerCase().endsWith(CSV_EXTENSION) || lOption.getFile().getPath().toLowerCase().endsWith(XML_EXTENSION)){
							parsedCommandDTO.setInputCvsOrXmlFile(lOption.getFile());
						}else{
							throw new ParseException(ParseException.ERR_NOT_CSV_OR_XML);
						}
					}else{
						if ((dOption.isSet())){
				            File inputDir = dOption.getFile();
				            ValidationUtility.checkValidDirectory(inputDir);
				            parsedCommandDTO.setInputDirectory(inputDir);	
				        }
					}
				}else{
					throw new ParseException(ParseException.ERR_BOTH_F_OR_L_OR_D);
				}    			
			}else{
				throw new ParseException(ParseException.ERR_NO_F_OR_L_OR_D);
			}
	
			//-u
			StringParam uOption = (StringParam) cmdLineHandler.getOption(ConcatParsedCommand.U_ARG);            
	        //if it's set we proceed with validation
	        if (uOption.isSet()){
	            Pattern p = Pattern.compile(SELECTION_REGEXP, Pattern.CASE_INSENSITIVE);
	            if ((p.matcher(uOption.getValue()).matches())){
	            	parsedCommandDTO.setPageSelection(uOption.getValue());
	            }
	            else{
	            	throw new ParseException(ParseException.ERR_ILLEGAL_U, new String[]{uOption.getValue()});
	            }
	        }
	
	        //-copyfields
	        parsedCommandDTO.setCopyFields(((BooleanParam) cmdLineHandler.getOption(ConcatParsedCommand.COPYFIELDS_ARG)).isTrue());
	        
	        //-r
	        StringParam rOption = (StringParam) cmdLineHandler.getOption(ConcatParsedCommand.R_ARG);
	        if(rOption.isSet()){
	        	PageRotation[] rotations = ValidationUtility.getPagesRotation(rOption.getValue());
	        	parsedCommandDTO.setRotations(rotations);
	        }
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;
	}
	
}
