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
package it.pdfsam.console.interfaces;

import java.util.List;

import com.lowagie.text.pdf.PdfReader;
/**
 * Interface for the writers used to concat pdf files
 * @author Andrea Vacondio
 */
public interface PdfConcatenator {
	/**
	 * Concatenates a PDF document selecting the pages to keep. The pages are described as
     * ranges. 
	 * @param reader PdfReader
	 * @param ranges Pages range (Ex. 2-23)
	 * @throws Exception
	 */
	public void addDocument(PdfReader reader, String ranges) throws Exception;
	/**
	 * Concatenates a PDF document.
	 * @param reader
	 * @throws Exception
	 */
	public void addDocument(PdfReader reader) throws Exception;
	
	/**
	 * 
	 * @param reader
	 * @throws Exception
	 */
	public void freeReader(PdfReader reader) throws Exception;
	/**
	 * Sets the bookmarks
	 * @param outlines
	 */
	public void setOutlines(List outlines);

	/**
	 * close
	 */
	public void close();
}
