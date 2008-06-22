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
package org.pdfsam.console.business.pdf.writers.interfaces;

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
	 * Sets the output document pdf version
	 * @param pdfVersion
	 */
	public void setPdfVersion(char pdfVersion);
	/**
	 * close
	 */
	public void close();
}
