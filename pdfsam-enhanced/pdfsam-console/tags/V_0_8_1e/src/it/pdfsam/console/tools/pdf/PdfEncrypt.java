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
import it.pdfsam.console.tools.PrefixParser;
import it.pdfsam.console.tools.TmpFileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * 
 * Class used to manage concat section. It takes input args and execute the concat command.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * @see it.pdfsam.console.tools.pdf.PdfSplit
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
    private boolean overwrite_boolean;
    private boolean compressed_boolean;
    private PrefixParser prefixParser;
    
    /**
     * creates the object used to encrypt pdf files
     * @param o_dir output directory
     * @param file_list files to encrypt
     * @param user_permissions permissions
     * @param u_pwd user password
     * @param a_pwd admin password
     * @param prefix output file prefix
     * @param etype encryption algorithm
     * @param overwrite overwrite output file if already exists
     * @param source_console
     */
    public PdfEncrypt(File o_dir, Collection file_list, int user_permissions, String u_pwd, String a_pwd, String prefix, String etype, boolean overwrite, boolean compressed, MainConsole source_console) {
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
        	this.etype = PdfWriter.STANDARD_ENCRYPTION_128;
	        if(etype != null){
	        	if (etype.equals(CmdParser.E_AES_128)){
		        	this.etype = PdfWriter.ENCRYPTION_AES_128;
		        }else if (etype.equals(CmdParser.E_RC4_40)){
		        	this.etype = PdfWriter.STANDARD_ENCRYPTION_40;
		        }		   
        	}
	       overwrite_boolean = overwrite;
	       compressed_boolean = compressed;
           out_message = "";
    }
    
    /**
	 * Default value compressed set <code>true</code>
	 */
    public PdfEncrypt(File o_dir, Collection file_list, int user_permissions, String u_pwd, String a_pwd, String prefix, String etype, boolean overwrite, MainConsole source_console) {
    	this(o_dir, file_list, user_permissions, u_pwd, a_pwd, prefix, etype, overwrite, true, source_console);
    }
    
    /**
	 * Default value overwrite set <code>true</code>
	 * Default value compressed set <code>true</code>
	 */
    public PdfEncrypt(File o_dir, Collection file_list, int user_permissions, String u_pwd, String a_pwd, String prefix, String etype, MainConsole source_console) {
    	this(o_dir, file_list, user_permissions, u_pwd, a_pwd, prefix, etype, true, source_console);
    }

    /**
     * Execute the encrypt command. On error an exception is thrown.
     * @throws Exception
     * @deprecated use <code>execute()</code> 
     */
    public void doEncrypt() throws Exception{
    	execute();
    }
    
    /**
     * Execute the encrypt command. On error an exception is thrown.
     * @throws EncryptException
     */
    public void execute() throws EncryptException{
    	try{
    		workingIndeterminate();
	    	out_message = "";
			int f = 0;
			for (Iterator f_list_itr = f_list.iterator(); f_list_itr.hasNext(); ) {            
				String file_name = f_list_itr.next().toString();
		    	prefixParser = new PrefixParser(prefix_value, new File(file_name).getName());
		        File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_dir.getAbsolutePath());
				out_message += LogFormatter.formatMessage("Temporary file created-\n");
				PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(file_name),null);					
				HashMap meta = pdfReader.getInfo();
				meta.put("Creator", MainConsole.CREATOR);
				PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmp_o_file));
				if(compressed_boolean){
					pdfStamper.setFullCompression();
				}
				pdfStamper.setMoreInfo(meta);
				pdfStamper.setEncryption(etype, u_pwd, a_pwd, user_permissions);
				pdfStamper.close();
				/*PdfEncryptor.encrypt(pdf_reader,new FileOutputStream(tmp_o_file), etype, u_pwd, a_pwd,user_permissions, meta);*/
				File out_file = new File(o_dir ,prefixParser.generateFileName());
				renameTemporaryFile(tmp_o_file, out_file, overwrite_boolean);				
				f++;
			}			
			out_message += LogFormatter.formatMessage("Pdf files encrypted in "+o_dir.getAbsolutePath()+"-\n"+PdfEncryptor.getPermissionsVerbose(user_permissions));
    	}catch(Exception e){
    		throw new EncryptException(e);
    	}finally{
    		workCompleted();
    	}
    }	
  
}
