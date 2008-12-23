/*
 * Created on 02-Apr-2007
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
package org.pdfsam.console.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pdfsam.console.exceptions.console.ConsoleException;
/**
 * Used to parse the prefix and generate the output filename.
 * If the prefix doesn't contain "[CURRENTPAGE]" or "[TIMESTAMP]" it generates oldstyle file name (Ex. 005_prefixFileName.pdf. 
 * If it contains "[CURRENTPAGE]", "[TIMESTAMP]" or [FILENUMBER] and depending on the generateFileName method you call, it performs variable substitution. (Ex. [BASENAME]_prefix_[CURRENTPAGE] generates FileName_prefix_005.pdf)
 * Available variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME], [FILENUMBER]. [CURRENTPAGE] and [FILENUMBER] accept the notation [FILENUMBER####] to specify the output pattern (Ex if the prefix is [FILENUMBER####] and file number is 5 the resulting prefix will be 0005)
 * @author a.vacondio
 *
 */
public class PrefixParser {
	
	
	private static final int SIMPLE_PREFIX = 0x00;
	private static final int CURRENT_PAGE = 0x01;
	private static final int TIMESTAMP = 0x02;
	private static final int BASENAME = 0x04;
	private static final int FILE_NUMBER = 0x08;
	
	private int currentPrefixType = SIMPLE_PREFIX;
	private static final String PDF_EXTENSION = ".pdf";
	
	//regexp to match
	private final String CURRENT_PAGE_REGX = "(.)*(\\[CURRENTPAGE(#*)\\])+(.)*";
	private final String FILE_NUMBER_REGX = "(.)*(\\[FILENUMBER(#*)\\])+(.)*";
	private final String TIMESTAMP_STRING = "[TIMESTAMP]";
	private final String BASE_NAME_STRING  = "[BASENAME]";
	
	
	//regexp to replace
	private final String CURRENT_PAGE_REPLACE_REGX = "\\[CURRENTPAGE(#+)*\\]";
	private final String FILE_NUMBER_REPLACE_REGX = "\\[FILENUMBER(#+)*\\]";
	private final String TIMESTAMP_REPLACE_RGX = "\\[TIMESTAMP\\]";
	private final String BASE_NAME_REPLACE_REGX = "\\[BASENAME\\]";

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
			if(prefix.indexOf(TIMESTAMP_STRING) > -1){
				currentPrefixType |= TIMESTAMP;
			}
			if(prefix.indexOf(BASE_NAME_STRING) > -1){
				currentPrefixType |= BASENAME;
			}
			if(prefix.matches(CURRENT_PAGE_REGX)){
				currentPrefixType |= CURRENT_PAGE;
			}
			if(prefix.matches(FILE_NUMBER_REGX)){
				currentPrefixType |= FILE_NUMBER;
			}
		}
		if(fileName != null && fileName.length() > 0){
			//check if the filename contains '.' and it's at least in second position (Ex. a.pdf)
			if(fileName.lastIndexOf('.') > 1){
				this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			}else{
				this.fileName = fileName;
			}
		}else{
			throw new ConsoleException(ConsoleException.EMPTY_FILENAME);
		}		
	}
	
	/**
	 * Generates the filename depending on the type of prefix. If it contains "[CURRENTPAGE]","[TIMESTAMP]" or "[FILENUMBER]" it performs variable substitution.
	 * @param pageNumber The page number used in variable substitution or in simple prefix (can be null)
	 * @param fileNumber The file number used in variable substitution or in simple prefix
	 * @return filename generated 
	 */
	public String generateFileName(Integer pageNumber, Integer fileNumber){
		String retVal = "";
		if(pageNumber == null && fileNumber == null){
			retVal = generateFileName();
		}else if(fileNumber==null){
			retVal = generateFileName(pageNumber);
		}else if(pageNumber == null){
			retVal = prefix;
			if(((currentPrefixType & TIMESTAMP)==TIMESTAMP)||((currentPrefixType & FILE_NUMBER)==FILE_NUMBER)){
				if((currentPrefixType & TIMESTAMP)==TIMESTAMP){
					retVal = applyTimestamp(retVal);
				}
				if((currentPrefixType & FILE_NUMBER)==FILE_NUMBER){
					retVal = applyFilenumber(retVal, fileNumber);
				}
				if((currentPrefixType & BASENAME)==BASENAME){
					retVal = applyFilename(retVal, fileName);
				}
				retVal += PDF_EXTENSION;
			}else{
				retVal = retVal+fileName+".pdf";
			}			
		}else{
			retVal = prefix;
			if(((currentPrefixType & TIMESTAMP)==TIMESTAMP)||((currentPrefixType & FILE_NUMBER)==FILE_NUMBER)||((currentPrefixType & CURRENT_PAGE)==CURRENT_PAGE)){
				if((currentPrefixType & TIMESTAMP)==TIMESTAMP){
					retVal = applyTimestamp(retVal);
				}
				if((currentPrefixType & FILE_NUMBER)==FILE_NUMBER){
					retVal = applyFilenumber(retVal, fileNumber);
				}
				if((currentPrefixType & BASENAME)==BASENAME){
					retVal = applyFilename(retVal, fileName);
				}
				if((currentPrefixType & CURRENT_PAGE)==CURRENT_PAGE){
					retVal = applyPagenumber(retVal, pageNumber);
				}
				retVal += PDF_EXTENSION;
			}else{
				retVal = getFileNumberFormatter(pageNumber.intValue()).format(pageNumber.intValue())+"_"+retVal+PDF_EXTENSION;
			}	
		}
		return retVal;
	}
	
	/**
	 * Generates the filename depending on the type of prefix. If it contains "[CURRENTPAGE]" or "[TIMESTAMP]" it performs variable substitution.
	 * @param pageNumber The page number used in variable substitution or in simple prefix
	 * @return filename generated 
	 */
	public String generateFileName(Integer pageNumber){
		String retVal = "";
		if(pageNumber == null){
			retVal = generateFileName();
		}else{
			retVal = prefix;
			if(((currentPrefixType & TIMESTAMP)==TIMESTAMP)||((currentPrefixType & CURRENT_PAGE)==CURRENT_PAGE)){
				if((currentPrefixType & TIMESTAMP)==TIMESTAMP){
					retVal = applyTimestamp(retVal);
				}
				if((currentPrefixType & CURRENT_PAGE)==CURRENT_PAGE){
					retVal = applyPagenumber(retVal, pageNumber);
				}
				if((currentPrefixType & BASENAME)==BASENAME){
					retVal = applyFilename(retVal, fileName);
				}
				retVal += PDF_EXTENSION;
			}else{
				retVal = getFileNumberFormatter(pageNumber.intValue()).format(pageNumber.intValue())+"_"+retVal;
			}		
		}
		return retVal;
	}
	
	/**
	 * Generates the filename depending on the type of prefix. If it contains "[TIMESTAMP]" it performs variable substitution.
	 * @return filename generated 
	 */
	public String generateFileName(){
		String retVal = "";
		if((currentPrefixType & TIMESTAMP)==TIMESTAMP){			
			retVal = applyTimestamp(prefix);
			if((currentPrefixType & BASENAME)==BASENAME){
				retVal = applyFilename(retVal, fileName);
			}
			retVal += PDF_EXTENSION;
		}else{
			retVal = prefix+fileName+PDF_EXTENSION;
		}
		return retVal;
	}
	
	/**
	 * Apply FILE_NUMBER_REGX variable substitution to the input argument
	 * @param arg0
	 * @param pageNumber
	 * @return
	 */
	private String applyFilenumber(String arg0, Integer fileNumber){
		String retVal = arg0;
		if(fileNumber!=null){
			String numberPatter = "";
			Matcher m = Pattern.compile(FILE_NUMBER_REGX).matcher(arg0);
			if(m.matches()){
				numberPatter = m.group(3);
			}
			String replacement = "";
			if(numberPatter!=null && numberPatter.length()>0){
				replacement = getFileNumberFormatter(numberPatter).format(fileNumber.intValue());
			}else{
				replacement = getFileNumberFormatter(fileNumber.intValue()).format(fileNumber.intValue());
			}
			retVal = arg0.replaceAll(FILE_NUMBER_REPLACE_REGX, replacement);
		}
		return retVal;
	}
	
	/**
	 * Apply CURRENT_PAGE_REPLACE_REGX variable substitution to the input argument 
	 * @param arg0
	 * @param pageNumber
	 * @return
	 */
	private String applyPagenumber(String arg0, Integer pageNumber){
		String retVal = arg0;
		if(pageNumber!=null){
			String numberPatter = "";
			Matcher m = Pattern.compile(CURRENT_PAGE_REGX).matcher(arg0);
			if(m.matches()){
				numberPatter = m.group(3);
			}
			String replacement = "";
			if(numberPatter!=null && numberPatter.length()>0){
				replacement = getFileNumberFormatter(numberPatter).format(pageNumber.intValue());
			}else{
				replacement = getFileNumberFormatter(pageNumber.intValue()).format(pageNumber.intValue());
			}
			retVal = arg0.replaceAll(CURRENT_PAGE_REPLACE_REGX, replacement);
		}
		return retVal;		
	}
	
	/**
	 * Apply BASE_NAME_REPLACE_REGX variable substitution to the input argument 
	 * @param arg0
	 * @param fileName
	 * @return
	 */
	private String applyFilename(String arg0, String fileName){
		String retVal = arg0;
		if(fileName!=null){
			retVal = arg0.replaceAll(BASE_NAME_REPLACE_REGX, fileName);
		}
		return retVal;	
	}
	
	/**
	 * Apply TIMESTAMP_REPLACE_RGX variable substitution to the input argument 
	 * @param arg0
	 * @return 
	 */
	private String applyTimestamp(String arg0){
		String retVal = arg0;
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(new Date());
		retVal = retVal.replaceAll(TIMESTAMP_REPLACE_RGX, timestamp);
		return retVal;
	}	
	
    /**
     * @param n number of pages
     * @return the DecimalFormat
     */
    private DecimalFormat getFileNumberFormatter(int n){
    	DecimalFormat retVal = new DecimalFormat();
    	try {
    		retVal.applyPattern(Integer.toString(n).replaceAll("\\d", "0"));
		} catch (Exception fe) {
			retVal.applyPattern("00000");
		}
		return retVal;
    }

    /**
     * @param arg0 the input string of the type "####"
     * @return
     */
    private DecimalFormat getFileNumberFormatter(String arg0){
    	DecimalFormat retVal = new DecimalFormat();
    	try {
    		if(arg0!=null && arg0.length()>0){
    			retVal.applyPattern(arg0.replaceAll("#", "0"));
    		}else{
    			retVal.applyPattern("00000");
    		}
		} catch (Exception fe) {
			retVal.applyPattern("00000");
		}
		return retVal;
    }
}
