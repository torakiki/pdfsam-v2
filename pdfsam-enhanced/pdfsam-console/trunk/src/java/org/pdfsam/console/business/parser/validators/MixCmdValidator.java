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
import jcmdline.IntParam;
import jcmdline.PdfFileParam;
import jcmdline.dto.PdfFile;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;
/**
 * CmdValidator for the mix command
 * @author Andrea Vacondio
 */
public class MixCmdValidator extends AbstractCmdValidator {

	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		MixParsedCommand parsedCommandDTO = new MixParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(MixParsedCommand.O_ARG);
	        if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            //checking extension
	            ValidationUtility.assertValidPdfExtension(outFile.getName());
	            parsedCommandDTO.setOutputFile(outFile);	
	    		
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
	        
	        //-f1
			PdfFileParam f1Option = (PdfFileParam) cmdLineHandler.getOption(MixParsedCommand.F1_ARG);           
	        if(f1Option.isSet()){
	        	PdfFile firstFile = f1Option.getPdfFile();
	        	 //checking extension
	            ValidationUtility.assertValidPdfExtension(firstFile.getFile().getName());
            	parsedCommandDTO.setFirstInputFile(FileUtility.getPdfFile(firstFile));                  	            
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F1);	
	        }
	        
	        //-f2
	        PdfFileParam f2Option = (PdfFileParam) cmdLineHandler.getOption(MixParsedCommand.F2_ARG);           
	        if(f2Option.isSet()){
	        	PdfFile secondFile = f2Option.getPdfFile();
	        	 //checking extension
	            ValidationUtility.assertValidPdfExtension(secondFile.getFile().getName());
	           	parsedCommandDTO.setSecondInputFile(FileUtility.getPdfFile(secondFile));
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F2);	
	        }           
	
	        //-step
	        IntParam stepOption = (IntParam) cmdLineHandler.getOption(MixParsedCommand.STEP_ARG);	        	        
        	if(stepOption.isSet()){	  
        		int step = stepOption.intValue();
        		if(step>0){
        			parsedCommandDTO.setStep(stepOption.intValue());
        		}else{
            		throw new ParseException(ParseException.ERR_STEP_ZERO_OR_NEGATIVE);
            	}
        	}else{
        		parsedCommandDTO.setStep(MixParsedCommand.DEFAULT_STEP);
        	}
        	
	        //-secondstep
	        IntParam secondStepOption = (IntParam) cmdLineHandler.getOption(MixParsedCommand.SECOND_STEP_ARG);	        	        
        	if(secondStepOption.isSet()){	  
        		int step = secondStepOption.intValue();
        		if(step>0){
        			parsedCommandDTO.setSecondStep(secondStepOption.intValue());
        		}else{
            		throw new ParseException(ParseException.ERR_STEP_ZERO_OR_NEGATIVE);
            	}
        	}else{
        		parsedCommandDTO.setSecondStep(parsedCommandDTO.getStep());
        	}
	       	        
	        //-reversefirst
	        parsedCommandDTO.setReverseFirst(((BooleanParam) cmdLineHandler.getOption(MixParsedCommand.REVERSE_FIRST_ARG)).isTrue());
	        //-reversesecond
	        parsedCommandDTO.setReverseSecond(((BooleanParam) cmdLineHandler.getOption(MixParsedCommand.REVERSE_SECOND_ARG)).isTrue());	        	    
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
        return parsedCommandDTO;
	}		

}
