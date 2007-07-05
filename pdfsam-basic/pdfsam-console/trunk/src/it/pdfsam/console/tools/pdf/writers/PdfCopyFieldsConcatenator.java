/*
 * Created on 20-Apr-2007
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

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
/**
 * Copy Fields concatenator. Uses PdfCopyFields.
 * @author a.vacondio
 * @see it.pdfsam.console.interfaces.PdfConcatenator
 */
public class PdfCopyFieldsConcatenator implements PdfConcatenator {

	private PdfCopyFields writer;
	
	public PdfCopyFieldsConcatenator(OutputStream os) throws DocumentException{
		writer = new PdfCopyFields(os);
	}
	
	/**
	 * @param os
	 * @param compressed If true creates a compressed pdf document
	 * @throws DocumentException
	 */
	public PdfCopyFieldsConcatenator(OutputStream os, boolean compressed) throws DocumentException{
		this(os);
		if(compressed){
			writer.setFullCompression();
		}
	}	
	
	public void addDocument(PdfReader reader, String ranges) throws Exception {
		if(reader != null){
			reader.selectPages(ranges);
			writer.addDocument(reader);
		}else{
			throw new DocumentException("Reader is null");
		}
	}

	public void addDocument(PdfReader reader) throws Exception {
		if(reader != null){
			writer.addDocument(reader);
		}else{
			throw new DocumentException("Reader is null");
		}
	}

	public void freeReader(PdfReader reader) throws Exception {
		writer.getWriter().freeReader(reader);
	}

	public void setOutlines(List outlines) {
		writer.setOutlines(outlines);
	}
	
	public void close(){
		writer.close();
	}
}
