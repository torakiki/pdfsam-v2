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
import it.pdfsam.console.exception.ConcatException;
import it.pdfsam.console.interfaces.PdfConcatenator;
import it.pdfsam.console.tools.LogFormatter;
import it.pdfsam.console.tools.TmpFileNameGenerator;
import it.pdfsam.console.tools.pdf.writers.PdfCopyFieldsConcatenator;
import it.pdfsam.console.tools.pdf.writers.PdfSimpleConcatenator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * 
 * Class used to manage concat section. It takes input args and execute the concat command.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfSplit
 */
public class PdfConcat extends GenericPdfTool{

    private Collection f_list;
    private File o_file;
    private String u_string;
    private boolean overwrite_boolean;
    private boolean copyfields_boolean;
    private boolean compressed_boolean;
    
    
    /**
     * Creates the object used to concat pdf files
     * @param file_list List of pdf files to be concatenated
     * @param out_file Output pdf file
     * @param uopts_string String with page selection. (Ex: 10-13:all:12-19:all:all:) 
     * @param overwrite if true overwrite existing file
     * @param compressed compress output pdf file
     * @param copyfields if true use the copyField writer
     * @param source_console console used to perform the action
     */
    public PdfConcat(Collection file_list, File out_file, String uopts_string, boolean overwrite, boolean copyfields, boolean compressed, MainConsole source_console) {
           super(source_console);
           f_list = file_list;
           o_file = out_file;
           //this prevent u_string to be null when using the console
           if (uopts_string != null){
               u_string = uopts_string;
           }else{
               u_string = "";
           }
           overwrite_boolean = overwrite;
           copyfields_boolean = copyfields;
           compressed_boolean = compressed;
           out_message = "";
    }
    
    /**
	 * Default value overwrite set <code>true</code>
	 * Default value compressed set <code>true</code>
	 */
    public PdfConcat(Collection file_list, File out_file, String uopts_string, boolean copyfields, MainConsole source_console) {
    	this(file_list, out_file, uopts_string, true, copyfields, source_console);
    }
   
    /**
	 * Default value compressed set <code>true</code>
	 */
    public PdfConcat(Collection file_list, File out_file, String uopts_string, boolean overwrite, boolean copyfields, MainConsole source_console) {
    	this(file_list, out_file, uopts_string, overwrite, copyfields, true, source_console);
    }
    
    /**
     * Execute the concat command. On error an exception is thrown.
     * @throws Exception
     * @deprecated use <code>execute()</code> 
     */
    public void doConcat() throws ConcatException{
    	execute();
    }
    /**
     * Execute the concat command. On error an exception is thrown.
     * @throws ConcatException
     */
    public void execute() throws ConcatException{
    	try{
    		percentageChanged(0,0);
    		out_message = "";
            String file_name;
            int pageOffset = 0;
            ArrayList master = new ArrayList();
            int f = 0;
            Document pdf_document = null;
            PdfConcatenator  pdf_writer = null;
            int total_processed_pages = 0;        
            String[] page_selection = u_string.split(":");
            File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_file.getParent());
            PdfReader pdf_reader;
    		for (Iterator f_list_itr = f_list.iterator(); f_list_itr.hasNext(); ) {            
    			String current_p_selection;
    			//get page selection. If arrayoutofbounds default behaviour is "all" 
    			try{
    				current_p_selection= page_selection[f].toLowerCase();
    				if (current_p_selection.equals("")) {
    					current_p_selection = "all";
    				}
    			}catch(Exception e){
    				current_p_selection = "all";
    			}
    			//validation
    			if (!(Pattern.compile("([0-9]+[-][0-9]+)|(all)", Pattern.CASE_INSENSITIVE).matcher(current_p_selection).matches())){
    				String errorMsg = "";
	            	try{
						tmp_o_file.delete();
					}
					catch(Exception e){
						errorMsg = " Unable to delete temporary file.";
					}
					throw new ConcatException("ValidationError: Syntax error on " + current_p_selection + "."+errorMsg);
				}                
    			file_name = f_list_itr.next().toString();
    			//reader creation
    			pdf_reader = new PdfReader(new RandomAccessFileOrArray(file_name),null);
    			pdf_reader.consolidateNamedDestinations();
    			int pdf_number_of_pages = pdf_reader.getNumberOfPages();
    			//default behaviour
    			int start = 0;
    			int end_page =  pdf_number_of_pages;           
    			if (!(current_p_selection.equals("all"))){
	            	boolean valid = true;
	            	String exceptionMsg = "";
	                String[] limits = current_p_selection.split("-");
	                try{
	                    start = Integer.parseInt(limits[0]);
	                    end_page = Integer.parseInt(limits[1]);                    
	                }catch(Exception ex){
						valid = false;
						exceptionMsg += "ValidationError: Syntax error on " + current_p_selection + ".";
						try{
							tmp_o_file.delete();
						}
						catch(Exception e){
							exceptionMsg += " Unable to delete temporary file.";
						}
	                }
	                if(valid){
		                //validation
		                if (start < 0){
							valid = false;
							exceptionMsg = "ValidationError: Syntax error. " + (start) + " must be positive in " + current_p_selection + ".";
							try{
								tmp_o_file.delete();
							}
							catch(Exception e){
								exceptionMsg += " Unable to delete temporary file.";
							}
		                }
		                else if (end_page > pdf_number_of_pages){
		                	valid = false;
							exceptionMsg = "ValidationError: Cannot merge at page " + end_page + ". No such page.";
							try{
								tmp_o_file.delete();
							}
							catch(Exception e){
								exceptionMsg += " Unable to delete temporary file.";
							}
		                }
		                else if (start > end_page){
		                	valid = false;
							exceptionMsg = "ValidationError: Syntax error. " + (start) + " is bigger than " + end_page + " in " + current_p_selection + ".";
							try{
								tmp_o_file.delete();
							}
							catch(Exception e){
								exceptionMsg += " Unable to delete temporary file.";
							}
		                }
	                }
	                if(!valid){
	                	throw new ConcatException(exceptionMsg);
	                }
    			}
    			List bookmarks = SimpleBookmark.getBookmark(pdf_reader);
    			if (bookmarks != null) {
    				//if the end page is not the end of the doc, delete bookmarks after it
    				if (end_page < pdf_number_of_pages){
    					SimpleBookmark.eliminatePages(bookmarks, new int[]{end_page+1, pdf_number_of_pages});
    				}
    				// if start page isn't the first page of the document, delete bookmarks before it
    				if (start > 0){
    					SimpleBookmark.eliminatePages(bookmarks, new int[]{1,start});
    					//bookmarks references must be taken back
    					SimpleBookmark.shiftPageNumbers(bookmarks, -start, null);
    				}
    				if (pageOffset != 0){
    					SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
    				}
    				master.addAll(bookmarks);
    			}
    			pageOffset += (end_page - start);
    			out_message += LogFormatter.formatMessage(file_name+ ": " + end_page + " pages-\n");
    			if (f == 0) {
                    if(copyfields_boolean){
                        // step 1: we create a writer 
                    	pdf_writer = new PdfCopyFieldsConcatenator(new FileOutputStream(tmp_o_file), compressed_boolean);
                    	HashMap meta = pdf_reader.getInfo();
        				meta.put("Creator", MainConsole.CREATOR);
                    }else{
                        // step 1: creation of a document-object
                        pdf_document = new Document(pdf_reader.getPageSizeWithRotation(1));
                        // step 2: we create a writer that listens to the document
                    	pdf_writer = new PdfSimpleConcatenator(pdf_document, new FileOutputStream(tmp_o_file), compressed_boolean);
                        // step 3: we open the document
                        MainConsole.setDocumentCreator(pdf_document);
                        pdf_document.open();
                    }
                    out_message += LogFormatter.formatMessage("Temporary file created-\n");
                }
    			// step 4: we add content
    			pdf_reader.selectPages(start+"-"+end_page);
    	        pdf_writer.addDocument(pdf_reader); 
    			//fix 03/07
    			//pdf_reader = null;
    			pdf_reader.close();
    			pdf_writer.freeReader(pdf_reader);
    			total_processed_pages += end_page - start;
    			out_message += LogFormatter.formatMessage((end_page - start) + " pages processed correctly-\n");
    			f++;
    			try{	
    				percentageChanged((f*100)/f_list.size(), (end_page - start));
	    		}catch(RuntimeException re){
	                out_message += LogFormatter.formatMessage("RuntimeException: "+re.getMessage()+"\n");            	
	            }
    		}
    		if (master.size() > 0){
    			pdf_writer.setOutlines(master);
    		}
    		out_message += LogFormatter.formatMessage("Total processed pages: " + total_processed_pages + "-\n");
    		// step 5: we close the document
    		if(pdf_document != null){
                pdf_document.close();        	
            }
    		pdf_writer.close();
    		// step 6: temporary buffer moved to output file
    		renameTemporaryFile(tmp_o_file, o_file, overwrite_boolean);    		
    	}catch(Exception e){    		
    		throw new ConcatException(e);
    	}finally{
    		workCompleted();
    	} 
    }
  
}
