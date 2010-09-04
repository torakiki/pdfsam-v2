/*
 * Created on 20-Apr-2007
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

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;

/**
 * Copy Fields concatenator. Uses PdfCopyFields.
 * 
 * @author a.vacondio
 */
public class PdfCopyFieldsConcatenator implements PdfConcatenator {

    private PdfCopyFields writer;

    public PdfCopyFieldsConcatenator(OutputStream os) throws DocumentException {
        writer = new PdfCopyFields(os);
    }

    /**
     * @param os
     * @param compressed
     *            If true creates a compressed pdf document
     * @throws DocumentException
     */
    public PdfCopyFieldsConcatenator(OutputStream os, boolean compressed) throws DocumentException {
        this(os);
        if (compressed) {
            writer.setFullCompression();
            writer.getWriter().setCompressionLevel(PdfStream.BEST_COMPRESSION);
        }
    }

    public void addDocument(PdfReader reader, String ranges) throws Exception {
        if (reader != null) {
            reader.selectPages(ranges);
            writer.addDocument(reader);
        } else {
            throw new DocumentException("Reader is null");
        }
    }

    public void addDocument(PdfReader reader) throws Exception {
        if (reader != null) {
            writer.addDocument(reader);
        } else {
            throw new DocumentException("Reader is null");
        }
    }

    public void freeReader(PdfReader reader) throws Exception {
        writer.getWriter().freeReader(reader);
    }

    public void setOutlines(List outlines) {
        writer.setOutlines(outlines);
    }

    public void setPdfVersion(char pdfVersion) {
        writer.getWriter().setPdfVersion(pdfVersion);
    }

    public void close() {
        writer.close();
    }
}
