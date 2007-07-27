/*
 * Created on 08-Jan-2007
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
import it.pdfsam.console.exception.AlternateMixException;
import it.pdfsam.console.tools.LogFormatter;
import it.pdfsam.console.tools.TmpFileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * 
 * Class used to manage alternate mix section. It takes input args and execute the mix command.
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * @see it.pdfsam.console.tools.pdf.PdfSplit
 * @see it.pdfsam.console.tools.pdf.PdfEncrypt
 */
public class PdfAlternateMix extends GenericPdfTool{
	 
	private File o_file;
	private File input_file1;
	private File input_file2;
    private boolean reverseFirst;
    private boolean reverseSecond;
    private boolean overwrite_boolean;
    private boolean compressed_boolean;
    private int[] limits1 = {1,1};
    private int[] limits2 = {1,1};
    
    /**
     * Creates the object used to mix 2 pdf documents
     * @param o_file 
     * @param input_file1
     * @param input_file2
     * @param reverseFirst
     * @param reverseSecond
     * @param overwrite
     * @param compressed
     * @param source_console
     */
	public PdfAlternateMix(File o_file, File input_file1, File input_file2, boolean reverseFirst, boolean reverseSecond, boolean overwrite, boolean compressed, MainConsole source_console) {
         super(source_console);
  	   	 this.o_file = o_file;
		 
  	   	 this.input_file1 = input_file1;   
		 this.input_file2 = input_file2;
		 
		 this.reverseFirst = reverseFirst;
		 this.reverseSecond = reverseSecond;
		 this.overwrite_boolean = overwrite;
		 compressed_boolean = compressed;
         out_message = "";
	}
	
	/**
	 * Default value compressed set <code>true</code>
	 */
	public PdfAlternateMix(File o_file, File input_file1, File input_file2, boolean reverseFirst, boolean reverseSecond, boolean overwrite, MainConsole source_console) {
		this(o_file, input_file1, input_file2, reverseFirst, reverseSecond, overwrite, true, source_console);
	}
	
	/**
	 * Default value overwrite set <code>true</code>
	 * Default value compressed set <code>true</code>
	 */
	public PdfAlternateMix(File o_file, File input_file1, File input_file2, boolean reverseFirst, boolean reverseSecond, MainConsole source_console) {
		this(o_file, input_file1, input_file2, reverseFirst, reverseSecond, true, source_console);
	}	
	
	 /**
     * Execute the mix command. On error an exception is thrown.
     * @throws AlternateMixException
     * @deprecated use <code>execute()</code> 
     */
	public void doAlternateMix() throws AlternateMixException{
		execute();
    }
    
	/**
     * Execute the mix command. On error an exception is thrown.
     * @throws AlternateMixException
     */
    public void execute() throws AlternateMixException{
		try{
			workingIndeterminate();
			out_message = "";
			Document pdf_document = null;
			PdfCopy  pdf_writer = null;
			File tmp_o_file = TmpFileNameGenerator.generateTmpFile(o_file.getParent());
			PdfReader pdf_reader1;
			PdfReader pdf_reader2;

			pdf_reader1 = new PdfReader(new RandomAccessFileOrArray(input_file1.getAbsolutePath()),null);
			pdf_reader1.consolidateNamedDestinations();
			limits1[1] = pdf_reader1.getNumberOfPages();

			pdf_reader2 = new PdfReader(new RandomAccessFileOrArray(input_file2.getAbsolutePath()),null);
			pdf_reader2.consolidateNamedDestinations();
			limits2[1] = pdf_reader2.getNumberOfPages();


			pdf_document = new Document(pdf_reader1.getPageSizeWithRotation(1));
			pdf_writer = new PdfCopy(pdf_document, new FileOutputStream(tmp_o_file));
	        if(compressed_boolean){
	        	pdf_writer.setFullCompression();
	        }				
			out_message += LogFormatter.formatMessage("Temporary file created-\n");
			MainConsole.setDocumentCreator(pdf_document);
			pdf_document.open();

			PdfImportedPage page;
			//importo
			boolean finished1 = false;
			boolean finished2 = false;
			int current1 = (reverseFirst)? limits1[1] :limits1[0];
			int current2 = (reverseSecond)? limits2[1] :limits2[0];
			while(!finished1 || !finished2){
				if(!finished1){
					if(current1>=limits1[0] && current1<=limits1[1]){
						page = pdf_writer.getImportedPage(pdf_reader1, current1);
						pdf_writer.addPage(page);
						current1 = (reverseFirst)? (current1-1) :(current1+1);
					}else{
						out_message += LogFormatter.formatMessage("First file processed-\n");
						finished1 = true;
					}
				}
				if(!finished2){
					if(current2>=limits2[0] && current2<=limits2[1] && !finished2){
						page = pdf_writer.getImportedPage(pdf_reader2, current2);
						pdf_writer.addPage(page);
						current2 = (reverseSecond)? (current2-1) :(current2+1);
					}else{
						out_message += LogFormatter.formatMessage("Second file processed-\n");
						finished2 = true;
					}
				}

			}

			pdf_reader1.close();
			pdf_writer.freeReader(pdf_reader1);
			pdf_reader2.close();
			pdf_writer.freeReader(pdf_reader2);

			pdf_document.close();
			// step 6: temporary buffer moved to output file
			renameTemporaryFile(tmp_o_file, o_file, overwrite_boolean);
			out_message += LogFormatter.formatMessage("Alternate mix completed-\n");

		}catch(Exception e){    		
			throw new AlternateMixException(e);
		}finally{
			workCompleted();
		}
	}
}
