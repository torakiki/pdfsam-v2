/*
 * Created on 12-MAr-2008
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
import java.util.HashSet;
import java.util.Iterator;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;
import org.pdfsam.console.business.dto.Transition;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SlideShowParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.exceptions.console.SlideShowException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;

/**
 * CmdValidator for the slideshow command
 * @author Andrea Vacondio
 */
public class SlideShowCmdValidator extends AbstractCmdValidator {

	protected AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		SlideShowParsedCommand parsedCommandDTO = new SlideShowParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(SlideShowParsedCommand.O_ARG);
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            ValidationUtility.assertValidDirectory(outFile);
	            parsedCommandDTO.setOutputFile(outFile);		    		
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
			//-p
	        StringParam pOption = (StringParam) cmdLineHandler.getOption(SlideShowParsedCommand.P_ARG);
	        if(pOption.isSet()){
	        	parsedCommandDTO.setOutputFilesPrefix(pOption.getValue());
	        }

	        //-f
	        PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(SlideShowParsedCommand.F_ARG);           
	        if(fOption.isSet()){
	        	PdfFile inputFile = fOption.getPdfFile();
	        	ValidationUtility.assertValidPdfExtension(inputFile.getFile().getName());	
	            parsedCommandDTO.setInputFile(FileUtility.getPdfFile(inputFile));                  
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F);	
	        }
	        
	        //-t
	        StringParam tOption = (StringParam) cmdLineHandler.getOption(SlideShowParsedCommand.T_ARG);
	        if(tOption.isSet()){
	        	HashSet transitionsList = new HashSet(tOption.getValues().size());
	        	for(Iterator tIterator = tOption.getValues().iterator(); tIterator.hasNext();){
	        		String transition = (String) tIterator.next();
	        		Transition transitionObject = stringToTransition(transition);
	        		if(!transitionsList.add(transitionObject)){
	        			throw new SlideShowException(SlideShowException.UNABLE_TO_ADD_TRANSITION, new String[]{transition,transitionObject.toString()});
	        		}
	        	}
	        	parsedCommandDTO.setTransitions((Transition[])transitionsList.toArray(new Transition[transitionsList.size()]));
	        }
	        
	        //-dt
	        StringParam dtOption = (StringParam) cmdLineHandler.getOption(SlideShowParsedCommand.DT_ARG);
	        if(dtOption.isSet()){
	        	parsedCommandDTO.setDefaultTransition(stringToTransition(dtOption.getValue()));
	        }
	        
	        //-l
	        FileParam lOption = (FileParam) cmdLineHandler.getOption(SlideShowParsedCommand.L_ARG);
	        if(lOption.isSet()){
		        if(lOption.getFile().getPath().toLowerCase().endsWith(XML_EXTENSION)){
					parsedCommandDTO.setInputXmlFile(lOption.getFile());
				}else{
					throw new ParseException(ParseException.ERR_NOT_XML);
				}
	        }
	        
	        //-fullscreen
	        parsedCommandDTO.setFullScreen(((BooleanParam) cmdLineHandler.getOption(SlideShowParsedCommand.FULLSCREEN_ARG)).isTrue());

		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;	
	}
	
	/**
	 * converts from the input string to the Transition object
	 * @param inputString
	 * @return corresponding Transition instance
	 * @throws ConsoleException
	 */
	private Transition stringToTransition(String inputString) throws ConsoleException{
		Transition retVal = null;
		if(inputString!= null && inputString.length()>0){
			String[] transParams = inputString.split(":");
			if(transParams.length>2){
				try{
					String transition = transParams[0];
					int transitionDuration = Integer.parseInt(transParams[1]);
					int duration = Integer.parseInt(transParams[2]);
					int pageNumber = (transParams.length>3)? Integer.parseInt(transParams[3]): Transition.EVERY_PAGE;
					retVal = new Transition(pageNumber, transitionDuration, transition, duration);
				}catch(Exception e){
					throw new SlideShowException(SlideShowException.ERR_BAD_INPUT_STRING, new String[]{inputString}, e);
				}
			}else{
				throw new SlideShowException(SlideShowException.ERR_UNCOMPLETE_INPUT_STRING, new String[]{inputString});
			}
		}else{
			throw new SlideShowException(SlideShowException.ERR_EMPTY_INPUT_STRING);
		}
		return retVal;
	}
}

