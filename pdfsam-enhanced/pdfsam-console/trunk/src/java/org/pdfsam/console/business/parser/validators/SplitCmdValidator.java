/*
 * Created on 12-Oct-2007
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
package org.pdfsam.console.business.parser.validators;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.LongParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
/**
 * CmdValidator for the split command
 * @author Andrea Vacondio
 */
public class SplitCmdValidator extends AbstractCmdValidator {


	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		SplitParsedCommand parsedCommandDTO = new SplitParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption("o");
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            if (outFile.isDirectory()){
	            	parsedCommandDTO.setOutputFile(outFile);	
	    		}           
	            else{
	            	throw new ParseException(ParseException.ERR_OUT_NOT_DIR);
	            }
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
	         
			//-p
	        StringParam pOption = (StringParam) cmdLineHandler.getOption("p");
	        if(pOption.isSet()){
	        	parsedCommandDTO.setOutputFilesPrefix(pOption.getValue());
	        }

	        //-f
	        FileParam fOption = (FileParam) cmdLineHandler.getOption("f");           
	        if(fOption.isSet()){
	        	File inputFile = fOption.getFile();
	            if ((inputFile.getPath().toLowerCase().endsWith(PDF_EXTENSION))){
	            	parsedCommandDTO.setInputFile(inputFile);                  
	            }else{
	            	throw new ParseException(ParseException.ERR_OUT_NOT_PDF, new String[]{inputFile.getName()});
	            }
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F);	
	        }

	        //-s
	        StringParam sOption = (StringParam) cmdLineHandler.getOption("s");
	        if(sOption.isSet()){
	        	parsedCommandDTO.setSplitType(sOption.getValue());
	        }
	        
	        //-b
	        LongParam bOption = (LongParam) cmdLineHandler.getOption("b");	        
	        if(SplitParsedCommand.S_SIZE.equals(parsedCommandDTO.getSplitType())){
	        	if(bOption.isSet()){	    
	        		parsedCommandDTO.setSplitSize(new Long(bOption.longValue()));
	        	}else{
	        		throw new ParseException(ParseException.ERR_NO_B);
	        	}
	        }else{
	        	if(bOption.isSet()){
	        		throw new ParseException(ParseException.ERR_B_NOT_NEEDED);
	            }
	        }
	        
	        //-n	       
	        StringParam nOption = (StringParam) cmdLineHandler.getOption("n");
	        if(SplitParsedCommand.S_NSPLIT.equals(parsedCommandDTO.getSplitType()) || SplitParsedCommand.S_SPLIT.equals(parsedCommandDTO.getSplitType())){
	        	 if(nOption.isSet()){
	        		 String nValue = nOption.getValue().trim().replaceAll(",","-").replaceAll(" ","-");
	        		 if(SplitParsedCommand.S_NSPLIT.equals(parsedCommandDTO.getSplitType())){	        			 
        				 Pattern p = Pattern.compile("([0-9]+)*");
                         if (!(p.matcher(nValue).matches())){
                        	 throw new ParseException(ParseException.ERR_N_NOT_NUM);
                         }
	                 }
	        		 if(SplitParsedCommand.S_SPLIT.equals(parsedCommandDTO.getSplitType())){	        			 
	        			 Pattern p = Pattern.compile("([0-9]+)([-][0-9]+)*");
                         if (!(p.matcher(nValue).matches())){
                        	 throw new ParseException(ParseException.ERR_N_NOT_NUM_OR_SEQ);
                         }
	                 }
	        		 parsedCommandDTO.setSplitPageNumbers(getSplitPageNumbers(nValue));
		        }else{
	                throw new ParseException(ParseException.ERR_NO_N);
	            }                       
	        }else{
	        	if(nOption.isSet()){
	        		throw new ParseException(ParseException.ERR_N_NOT_NEEDED);
	            }
	        }
		}else{
			throw new ParseException(ParseException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;
	}

	/**
	 * Converts a string like num-num-num... in an Integer array
	 * @param nValue
	 * @return
	 * @throws ParseException
	 */
	private Integer[] getSplitPageNumbers(String nValue) throws ParseException{
		ArrayList retVal = new ArrayList();
		try{
			String[] limits = nValue.split("-");
			for(int i=0; i<limits.length; i++){
				retVal.add(new Integer(limits[i]));
		    }
		}catch(NumberFormatException nfe){			
			new ParseException(ParseException.ERR_N_NOT_NUM_OR_SEQ, nfe);
		}
		return (Integer[]) retVal.toArray(new Integer[0]);
	}
}
