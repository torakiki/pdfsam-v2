/*
 * Created on 23-Feb-2006
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

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.events.WorkDoneEvent;
import it.pdfsam.console.exception.SplitException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * 
 * Class used to manage split section. It takes input args and execute the right split command.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.PdfConcat
 * 
 */
public class PdfSplit{

    private File o_file;
    private File f_file;
    private String prefix_value;
    private String split_type;
    private String snumber_page;
    private String out_message;
    private DecimalFormat file_number_formatter = new DecimalFormat();
    private MainConsole source;
    private PrefixParser prefixParser;
    //total number pages
    private int n = 0;

 
   
/**
 * Create the object used to split pdf files
 * 
 * @param out_dir Output path for the pdf files
 * @param input_file Input file to be splitted
 * @param prefix Optional prefix for output files name
 * @param splittype Type of split
 * @param n_page Optional. Required if type is "split" or "nsplit"
 */
    public PdfSplit(File out_dir, File input_file, String prefix, String splittype, String n_page, MainConsole source_console){
        o_file = out_dir;
        f_file = input_file;
        split_type = splittype;
        source = source_console;

        //prevent prefix_value to be null
        if (prefix != null){
            prefix_value = prefix.trim();
        }else{
            prefix_value = "";
        }
        
        //prevent snumber_page to be null
        if (n_page != null){
            snumber_page = n_page;
        }else{
            snumber_page = "";
        }
    }
   
    /**
     * Execute the right split command. On error an exception is thrown.
     * @throws Exception
     */
    public void doSplit() throws Exception{
        out_message = "";
        PdfReader pdf_reader = new PdfReader(new RandomAccessFileOrArray(f_file.getCanonicalPath()),null);
        prefixParser = new PrefixParser(prefix_value, f_file.getName());
        //we retrieve the total number of pages
        n = pdf_reader.getNumberOfPages();
        //apply format for output files name with leading zero(s)
        try{
            file_number_formatter.applyPattern(Integer.toString(n).replaceAll("\\d", "0"));
        }
        catch(Exception fe){
            file_number_formatter.applyPattern("00000");
        }
        out_message += LogFormatter.FormatMessage("There are "+n+" pages in this document\n");
        //-s ODD EVEN
        if(split_type.equals(it.pdfsam.console.MainConsole.S_ODD) || split_type.equals(it.pdfsam.console.MainConsole.S_EVEN)){          
            doSplitOddEven(pdf_reader);
        }else
        //-s BURST
        if(split_type.equals(it.pdfsam.console.MainConsole.S_BURST)){          
            doSplitBurst(pdf_reader);
        }else
        //-s SPLIT
        if(split_type.equals( it.pdfsam.console.MainConsole.S_SPLIT)){
           doSplitSplit(pdf_reader);
        }else
            //-s NSPLIT
            if(split_type.equals(it.pdfsam.console.MainConsole.S_NSPLIT)){                
                doSplitNSplit(pdf_reader);
            }
        pdf_reader.close();
           
        }       
    
    /**
     * @return Returns the out_message.
     */
    public String getOutMessage() {
        return out_message;
    }
    /**
     * @return Returns the out_message with <br> html tag.
     */
    public String getOutHTMLMessage() {
        return HtmlTags.disable(out_message).replaceAll("\n", "<br>");
    }
    /**
     * fire a work done event with the new percentage of work done
     * @param percentage
     */
    private void percentageChanged(int percentage){
        //source must not be null 
        if (source != null){
            if (source instanceof MainConsole){
                source.fireWorkDoneEvent(new WorkDoneEvent(this, WorkDoneEvent.PERCENTAGE_CHANGE, percentage));
            }
            
        }
    }
    
    /**
     * Validate the limit list in "split after these pages"
     * @param limits limits array containig page numbers
     * @param upper_limit max page number
     * @return epurated list
     */
    private LinkedList validateSplitLimits(String[] limits, int upper_limit){
        int current = 0;
        LinkedList limits_list = new LinkedList();

        /*
         * limits shoud contain only numeric values 'cause its validated by CmdParser
         * Now i check if it contains zero values or double values and i remove thm
         */
        for (int j=0; j < limits.length; j++){
            if((Integer.parseInt(limits[j]) <= 0)||(Integer.parseInt(limits[j])>= upper_limit)){ 
                    out_message += LogFormatter.FormatMessage("WARNING: cannot split before page number "+limits[j]+", limit removed.\n");
            }
            else{
                 if (current == Integer.parseInt(limits[j])){
                        out_message += LogFormatter.FormatMessage("WARNING: "+limits[j]+" found more than once in the page limits list, limit removed.\n");
                 }else{
                        limits_list.add(limits[j]);                            
                        current = Integer.parseInt(limits[j]);
                 }                    
            }
        }
        return limits_list;
    }
    
    /**
     * it creates a pdf file adding bookmarks
     * @param master bookmarks to add
     * @param tmp_o_file pdf file without bookmarks
     * @param out_file output file obtained merging bookmarks and pdf file
     * @throws Exception
     */
    private void addBookmarks(ArrayList master, File tmp_o_file, File out_file) throws Exception{       
        if (master.size() > 0){
            PdfReader tmp_pdf_reader = new PdfReader(new RandomAccessFileOrArray(tmp_o_file.getCanonicalPath()),null);
            PdfStamper stamp = new PdfStamper(tmp_pdf_reader, new FileOutputStream(out_file));
            //adding bookmarks
            stamp.setOutlines(master);
            tmp_pdf_reader.close();
            stamp.close();
            try{
                tmp_o_file.delete();
            }
            catch (Exception se){
                throw new SplitException("IOError: Unable to delete temporary output file "+tmp_o_file.getName()+".");
            }
        }
        //i've no bookmarks, just to rename temporary file
        else{
            try{
                if (!(tmp_o_file.renameTo(out_file))){
                    out_message += LogFormatter.FormatMessage("IOError: Unable to rename temporary output file "+tmp_o_file.getName()+".\n");
                    return;
                }
            }
            catch (Exception se){
                throw new SplitException("IOError: Unable to rename temporary output file "+tmp_o_file.getName()+".");
            }
        }
        out_message += LogFormatter.FormatMessage("Document "+out_file.getName()+" created.\n");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_ODD or S_EVEN
     * @param pdf_reader pdfreader of the original pdf document
     * @param out_files_name output file name
     * @throws Exception
     */
    private void doSplitOddEven(PdfReader pdf_reader) throws Exception{
        int current_page;
        Document current_document = new Document(pdf_reader.getPageSizeWithRotation(1));
        boolean time_to_close = false;
        PdfWriter pdf_writer = null;
        PdfContentByte content_byte = null;
        PdfImportedPage imported_page;
        for (current_page = 1; current_page <= n; current_page++) {
            //check if i've to read one more page or to open a new doc
            if ((current_page!=1) && ((split_type.equals(it.pdfsam.console.MainConsole.S_ODD) && ((current_page %2) == 1)) ||
                    (split_type.equals(it.pdfsam.console.MainConsole.S_EVEN) && ((current_page %2) == 0)))){
                time_to_close = true;
            }else{
                time_to_close = false;
            }
            if (time_to_close){
                current_document.setPageSize(pdf_reader.getPageSizeWithRotation(current_page));
                current_document.newPage();
            }
            else{
                // step 1: creation of a document-object
                current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
                // step 2: we create a writer that listens to the document
                pdf_writer = PdfWriter.getInstance(current_document, new FileOutputStream(new File(o_file,prefixParser.generateFileName(file_number_formatter.format(current_page)))));
                MainConsole.setDocumentCreator(current_document);
                // step 3: we open the document
                current_document.open();
                content_byte = pdf_writer.getDirectContent();
            }
            imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
            int rotation = pdf_reader.getPageRotation(current_page);
            if (rotation == 90 || rotation == 270) {
                content_byte.addTemplate(imported_page, 0, -1f, 1f, 0, 0, pdf_reader.getPageSizeWithRotation(current_page).height());
            }
            else {
                content_byte.addTemplate(imported_page, 1f, 0, 0, 1f, 0, 0);
            }
            //if it's time to close the document
            if ((time_to_close) || (current_page == n) || ((current_page==1) && (split_type.equals(it.pdfsam.console.MainConsole.S_ODD)))){
                current_document.close();
            }
            percentageChanged(java.lang.Math.round((current_page*100)/n));
        }
        out_message += LogFormatter.FormatMessage("Split "+split_type+" done.\n");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_BURST
     * @param pdf_reader pdfreader of the original pdf document
     * @param out_files_name output file name
     * @throws Exception
     */
    private void doSplitBurst(PdfReader pdf_reader) throws Exception{
        int current_page;
        Document current_document;
        for (current_page = 1; current_page <= n; current_page++) {
            // step 1: creation of a document-object
            current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
            // step 2: we create a writer that listens to the document
            PdfWriter pdf_writer = PdfWriter.getInstance(current_document, new FileOutputStream(new File(o_file,prefixParser.generateFileName(file_number_formatter.format(current_page)))));
            // step 3: we open the document
            MainConsole.setDocumentCreator(current_document);
            current_document.open();
            PdfContentByte content_byte = pdf_writer.getDirectContent();
            PdfImportedPage imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
            int rotation = pdf_reader.getPageRotation(current_page);
            if (rotation == 90 || rotation == 270) {
                content_byte.addTemplate(imported_page, 0, -1f, 1f, 0, 0, pdf_reader.getPageSizeWithRotation(current_page).height());
            }
            else {
                content_byte.addTemplate(imported_page, 1f, 0, 0, 1f, 0, 0);
            }
            current_document.close();
            percentageChanged(java.lang.Math.round((current_page*100)/n));
        }
        out_message += LogFormatter.FormatMessage("Burst done.\n");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_SPLIT
     * @param pdf_reader pdfreader of the original pdf document
     * @param out_files_name output file name
     * @throws Exception
     */
    private void doSplitSplit(PdfReader pdf_reader) throws Exception{
        String[] limits = snumber_page.split("-");
        Arrays.sort(limits,new StrNumComparator());
        //limits list validation end clean
        LinkedList limits_list = validateSplitLimits(limits, n);
        if (limits_list == null){
            throw new SplitException("PageNumberError: Cannot find page limits, please check input value.");
        }
        if (limits_list.isEmpty()){
            throw new SplitException("PageNumberError: Cannot find page limits, please check input value.");
        }
//      HERE I'M SURE I'VE A LIMIT LIST WITH VALUES, I CAN START
//BOOKMARKS            
        Iterator itr = limits_list.iterator();
        int current_page;
        int relative_current_page = 0;
        int end_page = n;
        int start_page = 1;
        Document current_document = new Document(pdf_reader.getPageSizeWithRotation(1));
        boolean go_on = false;
        PdfWriter pdf_writer = null;
        PdfContentByte content_byte = null;
        PdfImportedPage imported_page;
        String current_name = "";
        File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_file.getAbsolutePath());
        if (itr.hasNext()){
            end_page = Integer.parseInt((String)itr.next());
        }
        for (current_page = 1; current_page <= n; current_page++) {
             relative_current_page++;
             //check if i've to read one more page or to open a new doc
             if (relative_current_page == 1){
                 go_on = false;
             }else{
                 go_on = true;
             }
             if (go_on){
                 current_document.setPageSize(pdf_reader.getPageSizeWithRotation(current_page));
                 current_document.newPage();
             }
             //opening a new temporary pdf
             else{
                 tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_file.getAbsolutePath());
                 //i save the first page number of this doc to get bookmarks
                 start_page = current_page;
                 current_name = prefixParser.generateFileName(file_number_formatter.format(current_page));
                 // step 1: creation of a document-object
                 current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
                 // step 2: we create a writer that listens to the document
                 //pdf_writer = PdfWriter.getInstance(current_document, new FileOutputStream(new File(o_file,current_name)));
                 pdf_writer = PdfWriter.getInstance(current_document, new FileOutputStream(tmp_o_file));
                 // step 3: we open the document
                 MainConsole.setDocumentCreator(current_document);
                 current_document.open();
                 content_byte = pdf_writer.getDirectContent();
             }
             imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
             int rotation = pdf_reader.getPageRotation(current_page);
             if (rotation == 90 || rotation == 270) {
                 content_byte.addTemplate(imported_page, 0, -1f, 1f, 0, 0, pdf_reader.getPageSizeWithRotation(current_page).height());
             }
             else {
                 content_byte.addTemplate(imported_page, 1f, 0, 0, 1f, 0, 0);
             }
             //if it's time to close the document
             if (current_page == end_page){
                 relative_current_page = 0;                    
                 current_document.close();
                 out_message += LogFormatter.FormatMessage("Temporary document "+tmp_o_file.getName()+" done, now adding bookmarks...\n");
                 //manage bookmarks
                 ArrayList master = new ArrayList();
                 List this_book = SimpleBookmark.getBookmark(pdf_reader);
                 if (this_book != null) {
                    //ArrayList this_book = new ArrayList(bookmarks);
                    SimpleBookmark.eliminatePages(this_book, new int[]{end_page+1, n});
                    if (start_page > 1){
                        SimpleBookmark.eliminatePages(this_book, new int[]{1,start_page-1});
                        SimpleBookmark.shiftPageNumbers(this_book, -(start_page-1), null);
                    }
                    master.addAll(this_book);
                 }        
                 addBookmarks(master, tmp_o_file, new File(o_file,current_name));                 
                 if (itr.hasNext()){
                     end_page = Integer.parseInt((String)itr.next());                            
                 }else{
                     end_page = n;
                 }
             }
             percentageChanged(java.lang.Math.round((current_page*100)/n)); 
         }
        out_message += LogFormatter.FormatMessage("Split "+split_type+" done.\n");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_NSPLIT
     * @param pdf_reader pdfreader of the original pdf document
     * @param out_files_name output file name
     * @throws Exception
     */
    private void doSplitNSplit(PdfReader pdf_reader) throws Exception{
        int number_page = Integer.parseInt(snumber_page);
        if (number_page < 1 || number_page > n) {
            throw new SplitException("PageNumberError: Cannot split this document at page " + number_page + ". No such page.");
        }
        //snumber_page has already the first number thus i start from the second
        for (int i = (number_page*2); i < n; i+=number_page) {
            snumber_page = snumber_page + "-" + Integer.toString(i);
        }
        doSplitSplit(pdf_reader);
    }
}
