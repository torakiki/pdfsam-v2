/*
 * Created on 02-April-2007
 * Copyright notice: this code is based on Split and Burst classes by Mark Thompson. Copyright (c) 2002 Mark Thompson.
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
package it.pdfsam.console.tools;

import it.pdfsam.console.exception.ConsoleException;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Used to parse the prefix and generate the output filename.
 * If the prefix doesn't contain "[CURRENTPAGE]" or "[TIMESTAMP]" it generates oldstyle file name (Ex. 005_prefixFileName.pdf. 
 * If it contains "[CURRENTPAGE]" or "[TIMESTAMP]" it performs variable substitution. (Ex. [BASENAME]_prefix_[CURRENTPAGE] generates FileName_prefix_005.pdf)
 * Available variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME].
 * @author a.vacondio
 *
 */
public class PrefixParser {

	private final String CURRENT_PAGE = "[CURRENTPAGE]";
	private final String TIMESTAMP = "[TIMESTAMP]";
	//private final String BASE_NAME = "[BASENAME]";
	private final String TIMESTAMP_RGX = "\\[TIMESTAMP\\]";
	private final String CURRENT_PAGE_REGX = "\\[CURRENTPAGE\\]";
	private final String BASE_NAME_REGX = "\\[BASENAME\\]";

	private String prefix = "";
	private String fileName = "";
	
	/**
	 * 
	 * @param prefix prefix to use. (Can be empty)
	 * @param fileName Original file name
	 * @throws ConsoleException if the original fileName in empty or null
	 */
	public PrefixParser(String prefix, String fileName) throws ConsoleException{
		if (prefix != null){
			this.prefix = prefix;
		}
		if(fileName != null && fileName.length() > 0){
			//check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
			if(fileName.lastIndexOf('.') > 1){
				this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			}else{
				this.fileName = fileName;
			}
		}else{
			throw new ConsoleException("Filename length is 0.");
		}		
	}
	
	/**
	 * Generates the filename depending on the type of prefix. If it contains "[CURRENTPAGE]" or "[TIMESTAMP]" it performs variable substitution.
	 * @param page_number The page number used in variable substitution or in simple prefix
	 * @return filename generated 
	 */
	public String generateFileName(String page_number){
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
		String retVal = "";
		if(isComplexPrefix(true)){
			retVal = prefix;
			retVal = retVal.replaceAll(CURRENT_PAGE_REGX, page_number);
			retVal = retVal.replaceAll(BASE_NAME_REGX, fileName);
			retVal = retVal.replaceAll(TIMESTAMP_RGX, timestamp);
			retVal += ".pdf";
		}else{
			retVal = page_number+"_"+prefix+fileName+".pdf";
		}
		return retVal;
	}
	
	/**
	 * Generates the filename depending on the type of prefix. If it contains "[TIMESTAMP]" it performs variable substitution.
	 * @return filename generated 
	 */
	public String generateFileName(){
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
		String retVal = "";
		if(isComplexPrefix(false)){
			retVal = prefix;
			retVal = retVal.replaceAll(BASE_NAME_REGX, fileName);
			retVal = retVal.replaceAll(TIMESTAMP_RGX, timestamp);
			retVal += ".pdf";
		}else{
			retVal = prefix+fileName+".pdf";
		}
		return retVal;
	}

	/**
	 * @return <code>true</code> if it's a complex prefix
	 */
	private boolean isComplexPrefix(boolean havePageNumber) {
		boolean retVal = false;
		if(havePageNumber){
			//it must contain [CURRENTPAGE] or [TIMESTAMP] to be a copmplex prefix
			retVal = ((this.prefix.indexOf(CURRENT_PAGE) > -1) || (this.prefix.indexOf(TIMESTAMP) > -1))? true: false;
		}else{
			//it must contain [TIMESTAMP] to be a copmplex prefix
			retVal = (this.prefix.indexOf(TIMESTAMP) > -1)? true: false;			
		}
		return retVal;
	}
	
	
}
