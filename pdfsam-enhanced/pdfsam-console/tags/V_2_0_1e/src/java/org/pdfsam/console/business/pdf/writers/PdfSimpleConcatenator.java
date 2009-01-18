/*
 * Created on 17-Apr-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.console.business.pdf.writers;

import java.io.OutputStream;
import java.util.List;

import org.pdfsam.console.business.pdf.writers.interfaces.PdfConcatenator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

/**
 * Simple concatenator. Uses PdfCopy.
 * @author a.vacondio
 */
public class PdfSimpleConcatenator implements PdfConcatenator {
	
	private PdfCopy writer;
	
	public PdfSimpleConcatenator(Document document, OutputStream os) throws DocumentException{
		writer = new PdfCopy(document, os);
	}

	/**
	 * 
	 * @param document
	 * @param os
	 * @param compressed If true creates a compressed pdf document
	 * @throws DocumentException
	 */
	public PdfSimpleConcatenator(Document document, OutputStream os, boolean compressed) throws DocumentException{
		this(document, os);
		if(compressed){
			writer.setFullCompression();
		}
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
			int numPages =  reader.getNumberOfPages();
			for (int count = 1; count <= numPages; count++) {
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

	public void setPdfVersion(char pdfVersion) {
		writer.setPdfVersion(pdfVersion);		
	}
	

}
