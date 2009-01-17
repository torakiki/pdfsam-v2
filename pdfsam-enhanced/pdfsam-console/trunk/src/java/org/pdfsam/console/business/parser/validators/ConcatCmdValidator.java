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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.PageRotation;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConcatException;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
/**
 * CmdValidator for the concat command
 * @author Andrea Vacondio
 */
public class ConcatCmdValidator extends AbstractCmdValidator {

	private final Logger log = Logger.getLogger(ConcatCmdValidator.class.getPackage().getName());
	
	private final static String ALL_STRING = "all";
	private final static String ODD_STRING = "odd";
	private final static String EVEN_STRING = "even";
	
	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		ConcatParsedCommand parsedCommandDTO = new ConcatParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(ConcatParsedCommand.O_ARG);
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
			FileParam lOption = (FileParam) cmdLineHandler.getOption(ConcatParsedCommand.L_ARG);
			PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(ConcatParsedCommand.F_ARG);
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
			StringParam uOption = (StringParam) cmdLineHandler.getOption(ConcatParsedCommand.U_ARG);            
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
	        parsedCommandDTO.setCopyFields(((BooleanParam) cmdLineHandler.getOption(ConcatParsedCommand.COPYFIELDS_ARG)).isTrue());
	        
	        //-replaceRotations
	        parsedCommandDTO.setReplaceDocumentRotations(((BooleanParam) cmdLineHandler.getOption(ConcatParsedCommand.REPLACE_ROTATIONS_ARG)).isTrue());

	        //-r
	        StringParam rOption = (StringParam) cmdLineHandler.getOption(ConcatParsedCommand.R_ARG);
	        if(rOption.isSet()){
	        	PageRotation[] rotations = getPagesRotation(rOption.getValue());
	        	parsedCommandDTO.setRotations(rotations);
	        }
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;
	}


	/**
	 * all, odd and even pages rotation cannot be mixed together or with single pages rotations
	 * @param inputString the input command line string for the -r param
	 * @return the rotations array
	 * @throws ConcatException
	 */
	private PageRotation[] getPagesRotation(String inputString) throws ConcatException{
		ArrayList retVal = new ArrayList();
		try{
			if(inputString!= null && inputString.length()>0){
				String[] rotateParams = inputString.split(",");
				for(int i = 0; i<rotateParams.length; i++){
					String currentRotation = rotateParams[i];
					if(currentRotation.length()>3){
						String[] rotationParams = currentRotation.split(":");
						if(rotationParams.length == 2){
							String pageNumber = rotationParams[0].trim();
							int degrees = new Integer(rotationParams[1]).intValue()%360;
							//must be a multiple of 90
							if((degrees % 90)!=0){
								throw new ConcatException(ConcatException.ERR_DEGREES_NOT_ALLOWED, new String[]{degrees+""});
							}
							//rotate all
							if(ALL_STRING.equals(pageNumber)){
								if(retVal.size()>0){
									log.warn("Page rotation for every page found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.ALL_PAGES));
								break;
							}else if(ODD_STRING.equals(pageNumber)){
								if(retVal.size()>0){
									log.warn("Page rotation for even pages found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.ODD_PAGES));
								break;
							}else if(EVEN_STRING.equals(pageNumber)){
								if(retVal.size()>0){
									log.warn("Page rotation for odd pages found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.EVEN_PAGES));
								break;
							}else{
								retVal.add(new PageRotation(new Integer(pageNumber).intValue(), degrees));								
							}
						}else{
							throw new ConcatException(ConcatException.ERR_PARAM_ROTATION, new String[]{currentRotation});
						}
					}else{
						throw new ConcatException(ConcatException.ERR_PARAM_ROTATION, new String[]{currentRotation});
					}
				}
			}
		}catch(Exception e){
			throw new ConcatException(ConcatException.ERR_WRONG_ROTATION,e);
		}
		return (PageRotation[])retVal.toArray(new PageRotation[retVal.size()]);		
	}
	
}
