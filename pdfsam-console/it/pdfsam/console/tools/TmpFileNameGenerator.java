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
/**
 * 
 * Class used to create a temporary file.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * 
 */
public class TmpFileNameGenerator {

    /**
     * Used to create the name for a temporary file
     * @param number increment
     * @return temporary buffer name
     */
    private static String generateTmpFileName(int number){
        return "PDFsamTMPbuffer"+Integer.toString(number+1)+".pdf";
    }

    /**
     * Generates a not existing temporary file
     * @param file_path path where the temporary file is created
     * @return a temporary file
     */
    static public File generateTmpFile(String file_path){
        boolean already_exist = true;
        int enthropy = 0;
        String file_name = "";
        while(already_exist){
            enthropy++;
            file_name = TmpFileNameGenerator.generateTmpFileName(enthropy);
            File tmp_file = new File(file_path+File.separator+file_name);
            already_exist = tmp_file.exists();
            if (!already_exist) return tmp_file;
        }
        return null;
    }
    
}
