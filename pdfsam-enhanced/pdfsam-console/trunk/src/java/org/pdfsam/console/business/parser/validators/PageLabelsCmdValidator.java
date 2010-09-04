/*
 * Created on 19-Aug-2009
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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.PageLabel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.PageLabelsParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.exceptions.console.ValidationException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;

public class PageLabelsCmdValidator extends AbstractCmdValidator {

	private final Logger log = Logger.getLogger(PageLabelsCmdValidator.class.getPackage().getName());
	
	protected AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		PageLabelsParsedCommand parsedCommandDTO = new PageLabelsParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(PageLabelsParsedCommand.O_ARG);
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            //checking extension
	            ValidationUtility.assertValidPdfExtension(outFile.getName());
            	parsedCommandDTO.setOutputFile(outFile);	
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }

	        //-f
	        PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption("f");           
	        if(fOption.isSet()){
	        	PdfFile inputFile = fOption.getPdfFile();
	        	ValidationUtility.assertValidPdfExtension(inputFile.getFile().getName());
	            parsedCommandDTO.setInputFile(FileUtility.getPdfFile(inputFile));                  
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_F);	
	        }
	        
	        Map labels = new TreeMap();
	        //-l
	        StringParam lOption = (StringParam) cmdLineHandler.getOption(PageLabelsParsedCommand.L_ARG);
	        if(lOption.isSet()){
	        	for(Iterator lIterator = lOption.getValues().iterator(); lIterator.hasNext();){
	        		String currentLabel = (String) lIterator.next();
	        		PageLabel currentPageLabel = ValidationUtility.getPageLabel(currentLabel);
	        		if(labels.containsKey(new Integer(currentPageLabel.getPageNumber()))){
	        			log.warn("Duplicate label starting at page "+currentPageLabel.getPageNumber()+" will be ignored.");
	        		}else{
	        			labels.put(new Integer(currentPageLabel.getPageNumber()), currentPageLabel);
	        		}
	        	}
	        }
	        
	        //-lp
	        StringParam lpOption = (StringParam) cmdLineHandler.getOption(PageLabelsParsedCommand.LP_ARG);
	        if(lpOption.isSet()){
	        	for(Iterator lpIterator = lpOption.getValues().iterator(); lpIterator.hasNext();){
	        		String currentPrefix = (String) lpIterator.next();
	        		String[] values = currentPrefix.split(":", 2);
	        		if(values.length == 2){
	        			Integer page = null;
	        			try{
	        				page = Integer.valueOf(values[0]);
	    				}catch(Exception e){
	    					throw new ValidationException(ValidationException.ERR_NOT_VALID_LABEL_PREFIX, new String[]{currentPrefix}, e);
	    				}
	    				Object obj = labels.get(page);
	    				if(obj != null){
	    					((PageLabel)obj).setPrefix(values[1]);
	    				}else{
	    					log.warn("Unable to find label starting at page "+page.intValue()+", prefix will be ignored.");
	    				}
	        		}else{
	        			throw new ValidationException(ValidationException.ERR_NOT_VALID_LABEL_PREFIX, new String[]{currentPrefix});
	        		}
	        	}
	        }
	        
	        parsedCommandDTO.setLabels((PageLabel[])labels.values().toArray(new PageLabel[labels.size()]));
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;	
	}	
}
