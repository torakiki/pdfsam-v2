/*
 * Created on 21-oct-2007
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
package org.pdfsam.console.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.PdfFile;
/**
 * Utility class for file handling
 * @author Andrea Vacondio
 *
 */
public class FileUtility {

	private static final Logger log = Logger.getLogger(FileUtility.class.getPackage().getName());
	
	public static final String BUFFER_NAME = "PDFsamTMPbuffer";
	
    /**
     * Generates a not existing temporary file
     * @param filePath path where the temporary file is created
     * @return a temporary file
     */
     public static File generateTmpFile(String filePath){
    	log.debug("Creating temporary file..");
    	File retVal = null;
    	boolean alreadyExists = true;
        int enthropy = 0;
        String fileName = "";
        // generates a random 4 char string
        StringBuffer randomString = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
        	char ascii = (char) ((random.nextInt(26)) + 'A');
        	randomString.append(ascii);
        }
       
        while(alreadyExists){
            fileName = FileUtility.BUFFER_NAME+randomString+Integer.toString(++enthropy)+".pdf";
            File tmpFile = new File(filePath+File.separator+fileName);
            if (!(alreadyExists = tmpFile.exists())){
            	retVal = tmpFile;
            }
        }
        return retVal;
    }
    
     /**
      * @param filename filename or directory name
      * @return a random file generated in directory or in the containing directory of filename
      */
    public static File generateTmpFile(File filename){
    	File retVal = null;
    	if(filename != null){
	    	if(filename.isDirectory()){
	    		retVal = generateTmpFile(filename.getPath());
	    	}else{
	    		retVal =  generateTmpFile(filename.getParent());
	    	}
    	}
    	return retVal;
    }
	
	  /**
     * rename temporary file to output file
     * @param tmpFile temporary file to rename
     * @param outputFile file to rename to
     * @param overwrite overwrite existing file
     */
    public static boolean renameTemporaryFile(File tmpFile, File outputFile, boolean overwrite){
    	boolean retVal = false;
    	if(tmpFile != null && outputFile != null){
	    	try{
			       if (outputFile.exists()){
			          //check if overwrite is allowed
			          if (overwrite){
			                 if(outputFile.delete()){
			                	 retVal = renameFile(tmpFile, outputFile);
					    	 }else{
					    		 log.error("Unable to overwrite output file, a temporary file has been created ("+tmpFile.getName()+").");
					    	 }
			          }else{
			        	  log.error("Cannot overwrite output file (overwrite is false), a temporary file has been created ("+tmpFile.getName()+").");
			          }		                
			       }else{
			    	  retVal = renameFile(tmpFile, outputFile);
			       }
		    	   
	         }
	         catch(Exception e){
	        	 log.error("Exception renaming "+tmpFile.getName()+" to "+outputFile.getName(),e);
	         }
         }else{        	 
        	 log.error("Exception renaming temporary file, source or destination are null.");
         }
    	return retVal;
    }
    
    /**
     * Used to rename 
     * @param tmpFile
     * @param outputFile
     * @return true if renamed correctly 
     */
    private static boolean renameFile(File tmpFile, File outputFile) throws Exception{
    	boolean retVal = tmpFile.renameTo(outputFile);
    	if(!retVal){
  		   log.error("Unable to rename temporary file "+tmpFile.getName()+" to "+outputFile.getName()+".");
	    }
    	return retVal;
    }
    
    /**
     * deletes the file
     * @param tmpFile
     * @return true if file is deleted
     */
    public static boolean deleteFile(File tmpFile){
    	boolean retVal = false;
    	try{
    		if(!tmpFile.delete()){
    			log.error("Unable to delete file "+tmpFile.getName());
    		}
    	}catch(Exception e){
    		log.error("Unable to delete file "+tmpFile.getName(), e);
    	}
    	return retVal;
    }
    
    /**
     * copy source to dest
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest) {
        FileChannel in = null;
        FileChannel out = null;
        try {          
             in = new FileInputStream(source).getChannel();
             out = new FileOutputStream(dest).getChannel();
    
             long size = in.size();
             MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
    
             out.write(buf);
             
             if (in != null){
            	 in.close();
             }
             if (out != null){
            	 out.close();
             }
    
        } catch(Exception e){
        	log.error("Unable to copy "+source+" to "+dest);
        }
   }
    
    /**
     * Mapping from jcmdline.dto.PdfFile to org.pdfsam.console.business.dto.PdfFile
     * @param pdfFile
     * @return a PdfFile
     */
    public static PdfFile getPdfFile(jcmdline.dto.PdfFile pdfFile){
    	return new PdfFile(pdfFile.getFile(), pdfFile.getPassword());
    }
    
    /**
     * Mapping from an array of jcmdline.dto.PdfFile to an array of org.pdfsam.console.business.dto.PdfFile
     * @param pdfFiles
     * @return a PdfFile[]
     */
    public static PdfFile[] getPdfFiles(jcmdline.dto.PdfFile[] pdfFiles){
    	ArrayList retVal = new ArrayList();
    	for (int i = 0; i<pdfFiles.length; i++){
    		retVal.add(new PdfFile(pdfFiles[i].getFile(), pdfFiles[i].getPassword()));
    	}
    	return (PdfFile[]) retVal.toArray(new PdfFile[pdfFiles.length]);
    }
    
    /**
     * Mapping from a Collection of jcmdline.dto.PdfFile to an array of org.pdfsam.console.business.dto.PdfFile
     * @param pdfFiles
     * @return a PdfFile[]
     */
    public static PdfFile[] getPdfFiles(Collection pdfFiles){
    	return getPdfFiles((jcmdline.dto.PdfFile[])pdfFiles.toArray(new jcmdline.dto.PdfFile[pdfFiles.size()]));
    }
}
