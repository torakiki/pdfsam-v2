/*
 * Created on 25-Sep-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.dto;

import java.io.Serializable;
/**
 * Pdf document informations
 * @author Andrea Vacondio
 *
 */
public class DocumentInfo implements Serializable {

	private static final long serialVersionUID = 311953478483580551L;
	
	private String fileName;
	private int pages;
	private String pdfVersion;
	private DocumentMetaData documentMetaData;
	private boolean isEncrypted;
	
	/**
	 * Creates and empty object with an empty DocumentMetaData
	 */
	public DocumentInfo() {
		super();
		this.documentMetaData = new DocumentMetaData();
	}

	/**
	 * @param fileName
	 * @param pages
	 * @param pdfVersion
	 * @param documentMetaData
	 * @param isEncrypted
	 */
	public DocumentInfo(String fileName, int pages, String pdfVersion, DocumentMetaData documentMetaData,
			boolean isEncrypted) {
		super();
		this.fileName = fileName;
		this.pages = pages;
		this.pdfVersion = pdfVersion;
		this.documentMetaData = documentMetaData;
		this.isEncrypted = isEncrypted;
	}

	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the pages
	 */
	public int getPages() {
		return pages;
	}

	/**
	 * @param pages the pages to set
	 */
	public void setPages(int pages) {
		this.pages = pages;
	}

	/**
	 * @return the pdfVersion
	 */
	public String getPdfVersion() {
		return pdfVersion;
	}

	/**
	 * @param pdfVersion the pdfVersion to set
	 */
	public void setPdfVersion(String pdfVersion) {
		this.pdfVersion = pdfVersion;
	}

	/**
	 * @return the documentMetaData
	 */
	public DocumentMetaData getDocumentMetaData() {
		return documentMetaData;
	}

	/**
	 * @param documentMetaData the documentMetaData to set
	 */
	public void setDocumentMetaData(DocumentMetaData documentMetaData) {
		this.documentMetaData = documentMetaData;
	}

	/**
	 * @return the isEncrypted
	 */
	public boolean isEncrypted() {
		return isEncrypted;
	}

	/**
	 * @param isEncrypted the isEncrypted to set
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

}
