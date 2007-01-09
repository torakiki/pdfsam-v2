/*
 * Created on 13-Feb-2006
 *
 * Copyright notice: this code is based on concat_pdf class by Mark Thompson. Copyright (c) 2002 Mark Thompson.
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
 
 
package it.pdfsam.console.tools.pdf;

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.exception.EncryptException;
import it.pdfsam.console.tools.CmdParser;
import it.pdfsam.console.tools.LogFormatter;
import it.pdfsam.console.tools.TmpFileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * 
 * Class used to manage concat section. It takes input args and execute the concat command.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfSplit
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * @see it.pdfsam.console.tools.pdf.PdfAlternateMix
 */
public class PdfEncrypt extends GenericPdfTool{

    private int user_permissions = PdfWriter.AllowPrinting;
    private File o_dir;
    private Collection f_list;	
    private String u_pwd = "";
    private String a_pwd = "";
    private int etype;
	private String prefix_value = "";
    
    
    public PdfEncrypt(File o_dir, Collection file_list, int user_permissions, String u_pwd, String a_pwd, String prefix, String etype, MainConsole source_console) {
           super(source_console);
    	   this.o_dir = o_dir;
		   this.f_list = file_list;
		   this.user_permissions = user_permissions;
		   if (u_pwd != null){
				this.u_pwd = u_pwd;
			}
		   if (a_pwd != null){
				this.a_pwd = a_pwd;
			}
	        //prevent prefix_value to be null
	        if (prefix != null){
	            prefix_value = prefix.trim();
	        }else{
	            prefix_value = "";
	        }
        	this.etype = PdfWriter.ENCRYPTION_RC4_128;
	        if(etype != null){
	        	if (etype.equals(CmdParser.E_AES_128)){
		        	this.etype = PdfWriter.ENCRYPTION_AES_128;
		        }else if (etype.equals(CmdParser.E_RC4_40)){
		        	this.etype = PdfWriter.ENCRYPTION_RC4_40;
		        }		   
        	}
           out_message = "";
    }
    
    /**
     * Execute the concat command. On error an exception is thrown.
     * @throws Exception
     */
    public void doEncrypt() throws Exception{        
    	try{
    		workingIndeterminate();
	    	out_message = "";
			int f = 0;
			for (Iterator f_list_itr = f_list.iterator(); f_list_itr.hasNext(); ) {            
				String file_name = f_list_itr.next().toString();
				String out_file_name = prefix_value+(new File(file_name).getName());
		        File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_dir.getAbsolutePath());
				out_message += LogFormatter.formatMessage("Temporary file created-\n");
	
				PdfReader pdf_reader = new PdfReader(new RandomAccessFileOrArray(file_name),null);							
				HashMap meta = pdf_reader.getInfo();
				meta.put("Creator", MainConsole.CREATOR);
				PdfEncryptor.encrypt(pdf_reader,new FileOutputStream(tmp_o_file), etype, u_pwd, a_pwd,user_permissions, meta);
				File out_file = new File(o_dir ,out_file_name);
				if (renameTmpFile(tmp_o_file, out_file)){
					out_message += LogFormatter.formatMessage("File encrypted: "+out_file+"-\n");
				}else{
					out_message += LogFormatter.formatMessage("File encrypted but not renamed: "+tmp_o_file+"-\n");
				}
				 f++;
			}
			workCompleted();
			out_message += LogFormatter.formatMessage("Pdf files encrypted -\n"+PdfEncryptor.getPermissionsVerbose(user_permissions));
    	}catch(Exception e){
    		workCompleted();
    		throw new EncryptException(e.getMessage());
    	}
    }


    private boolean renameTmpFile(File tmp_o_file, File out_file){       
            boolean retVal = true;
    		try{
                if (!(tmp_o_file.renameTo(out_file))){
                	retVal = false;
                    out_message += LogFormatter.formatMessage("IOError: Unable to rename temporary output file "+tmp_o_file.getName()+".\n");
                }
            }
            catch (Exception se){
            	retVal = false;
                out_message += LogFormatter.formatMessage("IOError: Unable to rename temporary output file "+tmp_o_file.getName()+": "+se.getMessage()+"\n");
            }
            return retVal;
    }	
  
}
