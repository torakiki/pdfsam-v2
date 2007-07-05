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
package it.pdfsam.console.tools.pdf;

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.exception.SplitException;
import it.pdfsam.console.tools.CmdParser;
import it.pdfsam.console.tools.LogFormatter;
import it.pdfsam.console.tools.PrefixParser;
import it.pdfsam.console.tools.StrNumComparator;
import it.pdfsam.console.tools.TmpFileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * 
 * Class used to manage split section. It takes input args and execute the right
 * split command.
 * 
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * 
 */
public class PdfSplit extends GenericPdfTool {

	private File o_dir;
	private File f_file;
	private String prefix_value;
	private String split_type;
	private String snumber_page;
	private DecimalFormat file_number_formatter = new DecimalFormat();
	private PrefixParser prefixParser;
	private boolean overwrite_boolean;
	private boolean compressed_boolean;
	
	// total number pages
	private int n = 0;

	/**
	 * Create the object used to split pdf files
	 * 
	 * @param out_dir Output path for the pdf files
	 * @param input_file Input file to be splitted
	 * @param prefix Optional prefix for output files name
	 * @param splittype Type of split
	 * @param n_page Optional. Required if type is "split" or "nsplit"
	 * @param source_console Console
	 * @param overwrite overwrite output file
	 * @param compressed compress output pdf file
	 */
	public PdfSplit(File out_dir, File input_file, String prefix, String splittype, String n_page, boolean overwrite, boolean compressed,
			MainConsole source_console) {
		super(source_console);
		o_dir = out_dir;
		f_file = input_file;
		split_type = splittype;

		// prevent prefix_value to be null
		if (prefix != null) {
			prefix_value = prefix.trim();
		} else {
			prefix_value = "";
		}

		// prevent snumber_page to be null
		if (n_page != null) {
			snumber_page = n_page;
		} else {
			snumber_page = "";
		}
		overwrite_boolean = overwrite;
		compressed_boolean = compressed;
	}

	/**
	 * Default value compressed set <code>true</code>
	 */
	public PdfSplit(File out_dir, File input_file, String prefix, String splittype, String n_page, boolean overwrite, MainConsole source_console) {
		this(out_dir, input_file, prefix, splittype, n_page, overwrite, true, source_console);
	}
	
	/**
	 * Default value overwrite set <code>true</code>
	 * Default value compressed set <code>true</code>
	 */
	public PdfSplit(File out_dir, File input_file, String prefix, String splittype, String n_page, MainConsole source_console) {
		this(out_dir, input_file, prefix, splittype, n_page, true, true, source_console);
	}

	/**
	 * Execute the right split command. On error an exception is thrown.
	 * 
	 * @throws Exception
	 * @deprecated use <code>execute()</code>
	 */
	public void doSplit() throws Exception {
		execute();
	}

	/**
	 * Execute the split command. On error an exception is thrown.
	 * 
	 * @throws SplitException
	 */
	public void execute() throws SplitException {
		try {
			percentageChanged(0);
			out_message = "";
	        PdfReader pdf_reader = new PdfReader(new RandomAccessFileOrArray(f_file.getCanonicalPath()),null);
	        prefixParser = new PrefixParser(prefix_value, f_file.getName());
	        //we retrieve the total number of pages
	        n = pdf_reader.getNumberOfPages();
			// apply format for output files name with leading zero(s)
			try {
				file_number_formatter.applyPattern(Integer.toString(n).replaceAll("\\d", "0"));
			} catch (Exception fe) {
				file_number_formatter.applyPattern("00000");
			}
			out_message += LogFormatter.formatMessage("There are " + n + " pages in this document\n");
			// -s ODD EVEN
			if (split_type.equals(CmdParser.S_ODD) || split_type.equals(CmdParser.S_EVEN)) {
				doSplitOddEven(pdf_reader);
			} else
			// -s BURST
			if (split_type.equals(CmdParser.S_BURST)) {
				doSplitBurst(pdf_reader);
			} else
			// -s SPLIT
			if (split_type.equals(CmdParser.S_SPLIT)) {
				doSplitSplit(pdf_reader);
			} else
			// -s NSPLIT
			if (split_type.equals(CmdParser.S_NSPLIT)) {
				doSplitNSplit(pdf_reader);
			}
			pdf_reader.close();
		} catch (Exception e) {
			throw new SplitException(e);
		} finally {
			workCompleted();
		}

	}

	/**
	 * Validate the limit list in "split after these pages"
	 * 
	 * @param limits limits array containig page numbers
	 * @param upper_limit max page number
	 * @return epurated list
	 */
	private LinkedList validateSplitLimits(String[] limits, int upper_limit) {
		int current = 0;
		LinkedList limits_list = new LinkedList();

		/*
		 * limits shoud contain only numeric values 'cause its validated by
		 * CmdParser Now i check if it contains zero values or double values and
		 * i remove thm
		 */
		for (int j = 0; j < limits.length; j++) {
			if ((Integer.parseInt(limits[j]) <= 0) || (Integer.parseInt(limits[j]) >= upper_limit)) {
				out_message += LogFormatter.formatMessage("WARNING: cannot split before page number " + limits[j]
						+ ", limit removed.\n");
			} else {
				if (current == Integer.parseInt(limits[j])) {
					out_message += LogFormatter.formatMessage("WARNING: " + limits[j]
							+ " found more than once in the page limits list, limit removed.\n");
				} else {
					limits_list.add(limits[j]);
					current = Integer.parseInt(limits[j]);
				}
			}
		}
		return limits_list;
	}

	   /**
     * Execute the split of a pdf document when split type is S_ODD or S_EVEN
     * @param pdf_reader pdfreader of the original pdf document
     * @throws Exception
     */
    private void doSplitOddEven(PdfReader pdf_reader) throws Exception{
        int current_page;
        Document current_document = new Document(pdf_reader.getPageSizeWithRotation(1));
        boolean time_to_close = false;
        PdfCopy pdf_writer = null;
        PdfImportedPage imported_page;
        File tmp_o_file = null;
        File o_file = null;
        for (current_page = 1; current_page <= n; current_page++) {
            //check if i've to read one more page or to open a new doc
            if ((current_page!=1) && ((split_type.equals(CmdParser.S_ODD) && ((current_page %2) != 0)) ||
                    (split_type.equals(CmdParser.S_EVEN) && ((current_page %2) == 0)))){
                time_to_close = true;
            }else{
                time_to_close = false;
            }
            if (!time_to_close){
            	tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_dir);
            	o_file = new File(o_dir,prefixParser.generateFileName(file_number_formatter.format(current_page)));
                // step 1: creation of a document-object
            	current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
                // step 2: we create a writer that listens to the document
                pdf_writer = new PdfCopy(current_document, new FileOutputStream(tmp_o_file));
		        if(compressed_boolean){
		        	pdf_writer.setFullCompression();
		        }				
                MainConsole.setDocumentCreator(current_document);
                // step 3: we open the document
                current_document.open();
            }
            imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
            pdf_writer.addPage(imported_page);
            //if it's time to close the document
            if ((time_to_close) || (current_page == n) || ((current_page==1) && (split_type.equals(CmdParser.S_ODD)))){
                current_document.close();
                renameTemporaryFile(tmp_o_file, o_file, overwrite_boolean);
            }
            percentageChanged(java.lang.Math.round((current_page*100)/n));
        }
        out_message += LogFormatter.formatMessage("Split "+split_type+" done.\n");
    }
	
	   /**
     * Execute the split of a pdf document when split type is S_BURST
     * @param pdf_reader pdfreader of the original pdf document
     * @throws Exception
     */
    private void doSplitBurst(PdfReader pdf_reader) throws Exception{
        int current_page;
        Document current_document;
        for (current_page = 1; current_page <= n; current_page++) {
        	File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_dir);
        	File o_file = new File(o_dir,prefixParser.generateFileName(file_number_formatter.format(current_page)));
            // step 1: creation of a document-object
            current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
            // step 2: we create a writer that listens to the document
            PdfCopy pdf_writer = new PdfCopy(current_document, new FileOutputStream(tmp_o_file));
            if(compressed_boolean){
            	pdf_writer.setFullCompression();
            }
            // step 3: we open the document
            MainConsole.setDocumentCreator(current_document);
            current_document.open();
            PdfImportedPage imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
            pdf_writer.addPage(imported_page);
            current_document.close();
            renameTemporaryFile(tmp_o_file, o_file, overwrite_boolean);
            percentageChanged(java.lang.Math.round((current_page*100)/n));
        }
        out_message += LogFormatter.formatMessage("Burst done.\n");
    }

	/**
	 * Execute the split of a pdf document when split type is S_SPLIT
	 * 
	 * @param pdf_reader
	 *                pdfreader of the original pdf document
	 * @param out_files_name
	 *                output file name
	 * @throws Exception
	 */
	private void doSplitSplit(PdfReader pdf_reader) throws Exception {
		String[] limits = snumber_page.split("-");
		Arrays.sort(limits, new StrNumComparator());
		// limits list validation end clean
		LinkedList limits_list = validateSplitLimits(limits, n);
		if (limits_list == null) {
			throw new SplitException("PageNumberError: Cannot find page limits, please check input value.");
		}
		if (limits_list.isEmpty()) {
			throw new SplitException("PageNumberError: Cannot find page limits, please check input value.");
		}
		// HERE I'M SURE I'VE A LIMIT LIST WITH VALUES, I CAN START
		// BOOKMARKS
		Iterator itr = limits_list.iterator();
		int current_page;
		int relative_current_page = 0;
		int end_page = n;
		int start_page = 1;
		Document current_document = new Document(pdf_reader.getPageSizeWithRotation(1));
		PdfCopy pdf_writer = null;
	    PdfImportedPage imported_page;
	    File tmp_o_file = null;
	    File o_file = null;
	    if (itr.hasNext()){
	        end_page = Integer.parseInt((String)itr.next());
	    }
		for (current_page = 1; current_page <= n; current_page++) {
			relative_current_page++;
            //check if i've to read one more page or to open a new doc
            if (relative_current_page == 1){            	
           	 	tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_dir);
            	o_file = new File(o_dir,prefixParser.generateFileName(file_number_formatter.format(current_page)));
            	start_page = current_page;
                // step 1: creation of a document-object
            	current_document = new Document(pdf_reader.getPageSizeWithRotation(current_page));
                // step 2: we create a writer that listens to the document
                pdf_writer = new PdfCopy(current_document, new FileOutputStream(tmp_o_file));
                if(compressed_boolean){
                	pdf_writer.setFullCompression();
                }
                MainConsole.setDocumentCreator(current_document);
                // step 3: we open the document
                current_document.open();            	 
            }
            imported_page = pdf_writer.getImportedPage(pdf_reader, current_page);
            pdf_writer.addPage(imported_page);
			// if it's time to close the document
			if (current_page == end_page) {
                out_message += LogFormatter.formatMessage("Temporary document "+tmp_o_file.getName()+" done, now adding bookmarks...\n");
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
                   pdf_writer.setOutlines(master);
                }
                relative_current_page = 0;                    
                current_document.close();
                renameTemporaryFile(tmp_o_file, o_file, overwrite_boolean);
                end_page = (itr.hasNext())? Integer.parseInt((String)itr.next()): n;
			}
			percentageChanged((current_page * 100) / n);
		}
		out_message += LogFormatter.formatMessage("Split " + split_type + " done.\n");
	}

	/**
	 * Execute the split of a pdf document when split type is S_NSPLIT
	 * 
	 * @param pdf_reader pdfreader of the original pdf document
	 * @param out_files_name output file name
	 * @throws Exception
	 */
	private void doSplitNSplit(PdfReader pdf_reader) throws Exception {
		int number_page = Integer.parseInt(snumber_page);
		if (number_page < 1 || number_page > n) {
			throw new SplitException("PageNumberError: Cannot split this document at page " + number_page+ ". No such page.");
		}
		// snumber_page has already the first number thus i start from the second
		for (int i = (number_page * 2); i < n; i += number_page) {
			snumber_page = snumber_page + "-" + Integer.toString(i);
		}
		doSplitSplit(pdf_reader);
	}
}
