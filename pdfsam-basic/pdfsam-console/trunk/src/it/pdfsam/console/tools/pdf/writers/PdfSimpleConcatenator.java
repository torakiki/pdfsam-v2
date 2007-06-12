/*
 * Created on 17-Apr-2007
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
package it.pdfsam.console.tools.pdf.writers;

import it.pdfsam.console.interfaces.PdfConcatenator;

import java.io.OutputStream;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

/**
 * Simple concatenator. Uses PdfCopy.
 * @author a.vacondio
 * @see it.pdfsam.console.interfaces.PdfConcatenator
 */
public class PdfSimpleConcatenator implements PdfConcatenator {
	
	private PdfCopy writer;
	
	public PdfSimpleConcatenator(Document document, OutputStream os) throws DocumentException{
		writer = new PdfCopy(document, os);
	}

	public void addDocument(PdfReader reader, String ranges) throws Exception {
		if(reader != null){
			reader.selectPages(ranges);
			addDocument(reader);
		}else{
			throw new DocumentException("Reader is null");
		}
	}

	public void addDocument(PdfReader reader) throws Exception {
		if(reader != null){
			for (int count = 0; count < reader.getNumberOfPages(); ) {
                ++count;
                writer.addPage(writer.getImportedPage(reader, count));                                
            }
		}else{
			throw new DocumentException("Reader is null");
		}
	}

	public void freeReader(PdfReader reader) throws Exception{
		writer.freeReader(reader);
	}

	public void setOutlines(List outlines) {
		writer.setOutlines(outlines);
	}
	
	public void close(){
		writer.close();
	}
	

}
