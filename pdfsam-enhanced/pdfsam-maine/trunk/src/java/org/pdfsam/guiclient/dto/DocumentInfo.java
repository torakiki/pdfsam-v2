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
	private String title;
	private String author;
	private String creator;
	private String producer;
	private boolean isEncrypted;
	
	/**
	 * 
	 */
	public DocumentInfo() {
		super();
	}
	/**
	 * @param author
	 * @param creator
	 * @param fileName
	 * @param isEncrypted
	 * @param pages
	 * @param pdfVersion
	 * @param producer
	 * @param title
	 */
	public DocumentInfo(String author, String creator, String fileName, boolean isEncrypted, int pages,
			String pdfVersion, String producer, String title) {
		super();
		this.author = author;
		this.creator = creator;
		this.fileName = fileName;
		this.isEncrypted = isEncrypted;
		this.pages = pages;
		this.pdfVersion = pdfVersion;
		this.producer = producer;
		this.title = title;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @return the producer
	 */
	public String getProducer() {
		return producer;
	}
	/**
	 * @param producer the producer to set
	 */
	public void setProducer(String producer) {
		this.producer = producer;
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
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String OPEN = "[";
	    final String CLOSE = "]";
	    
	    StringBuffer retValue = new StringBuffer();
	    
	    retValue.append("DocumentInfo ( ")
	        .append(super.toString())
	        .append(OPEN).append("fileName=").append(this.fileName).append(CLOSE)
	        .append(OPEN).append("pages=").append(this.pages).append(CLOSE)
	        .append(OPEN).append("pdfVersion=").append(this.pdfVersion).append(CLOSE)
	        .append(OPEN).append("title=").append(this.title).append(CLOSE)
	        .append(OPEN).append("author=").append(this.author).append(CLOSE)
	        .append(OPEN).append("creator=").append(this.creator).append(CLOSE)
	        .append(OPEN).append("producer=").append(this.producer).append(CLOSE)
	        .append(OPEN).append("isEncrypted=").append(this.isEncrypted).append(CLOSE)
	        .append(" )");
	    
	    return retValue.toString();
	}
	
	
}
