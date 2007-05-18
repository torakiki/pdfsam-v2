/*
 * Created on 4-giu-2006
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

import java.io.File;
import java.util.Random;
/**
 * 
 * Class used to create a temporary file.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * 
 */
public class TmpFileNameGenerator {

	    /**
	     * Generates a not existing temporary file
	     * @param file_path path where the temporary file is created
	     * @return a temporary file
	     */
	     public static File generateTmpFile(String file_path){
	        File retVal = null;
	    	boolean already_exist = true;
	        int enthropy = 0;
	        String file_name = "";
	        // generates a random 4 char string
	        StringBuffer randomString = new StringBuffer();
	        Random random = new Random();
	        for (int i = 0; i < 5; i++) {
	        	char ascii = (char) ((random.nextInt(26)) + 'A');
	        	randomString.append(ascii);
	        }
	       
	        while(already_exist){
	            file_name = "PDFsamTMPbuffer"+randomString+Integer.toString(++enthropy)+".pdf";
	            File tmp_file = new File(file_path+File.separator+file_name);
	            if (!(already_exist = tmp_file.exists())){
	            	retVal = tmp_file;
	            }
	        }
	        return retVal;
	    }
	    
	     /**
	      * @param filename Filename or Dirname
	      * @return a random file generated in Dirname or in the containing directory of Filename
	      */
	    public static File generateTmpFile(File filename){
	    	if(filename != null){
		    	if(filename.isDirectory()){
		    		return generateTmpFile(filename.getPath());
		    	}else{
		    		return generateTmpFile(filename.getParent());
		    	}
	    	}else{
	    		return null;
	    	}	
	    }
    
}
