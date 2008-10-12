/*
 * Created on 18-Jun-2008
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

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;
/**
 * DTO representing a page of a document 
 * @author Andrea Vacondio
 *
 */
public class VisualPageListItem implements Serializable, Cloneable {

	private static final long serialVersionUID = 7598120284619680606L;

	public static final Image HOURGLASS =  new ImageIcon(VisualPageListItem.class.getResource("/images/hourglass.png")).getImage();
	
	private Image thumbnail = HOURGLASS;
	private int pageNumber;
	private boolean deleted = false;
	private String parentFileCanonicalPath = "";
	private String documentPassword = "";

	public VisualPageListItem() {
	}

	/**
	 * @param pageNumber
	 */
	public VisualPageListItem(int pageNumber) {
		this(HOURGLASS, pageNumber);
	}
	/**
	 * @param pageNumber
	 * @param parentFileCanonicalPath
	 */
	public VisualPageListItem(int pageNumber, String parentFileCanonicalPath) {
		this(HOURGLASS, pageNumber, false, parentFileCanonicalPath, null);
	}
	/**
	 * @param pageNumber
	 * @param parentFileCanonicalPath
	 * @param documentPassword
	 */
	public VisualPageListItem(int pageNumber, String parentFileCanonicalPath, String documentPassword) {
		this(HOURGLASS, pageNumber, false, parentFileCanonicalPath, documentPassword);
	}

	/**
	 * @param thumbnail
	 * @param pageNumber
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber) {
		this(thumbnail, pageNumber, false);
	}
	/**
	 * @param thumbnail
	 * @param pageNumber
	 * @param deleted
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted){
		this(thumbnail, pageNumber, deleted, "", null);
	}
	/**
	 * @param thumbnail
	 * @param pageNumber
	 * @param deleted
	 * @param parentFileCanonicalPath
	 * @param documentPassword 
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted, String parentFileCanonicalPath, String documentPassword) {
		super();
		this.thumbnail = thumbnail;
		this.pageNumber = pageNumber;
		this.deleted = deleted;
		this.parentFileCanonicalPath = parentFileCanonicalPath;
		this.documentPassword = documentPassword;
	}
	/**
	 * @return the thumbnail
	 */
	public Image getThumbnail() {
		return thumbnail;
	}
	/**
	 * @param thumbnail the thumbnails to set
	 */
	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
	}
	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the parentFileCanonicalPath
	 */
	public String getParentFileCanonicalPath() {
		return parentFileCanonicalPath;
	}

	/**
	 * @param parentFileCanonicalPath the parentFileCanonicalPath to set
	 */
	public void setParentFileCanonicalPath(String parentFileCanonicalPath) {
		this.parentFileCanonicalPath = parentFileCanonicalPath;
	}	

	/**
	 * @return the documentPassword
	 */
	public String getDocumentPassword() {
		return documentPassword;
	}

	/**
	 * @param documentPassword the documentPassword to set
	 */
	public void setDocumentPassword(String documentPassword) {
		this.documentPassword = documentPassword;
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
	    
	    retValue.append("VisualPageListItem ( ")
	        .append(super.toString())
	        .append(OPEN).append("thumbnail=").append(this.thumbnail).append(CLOSE)
	        .append(OPEN).append("pageNumber=").append(this.pageNumber).append(CLOSE)
	        .append(OPEN).append("deleted=").append(this.deleted).append(CLOSE)
	        .append(OPEN).append("parentFileCanonicalPath=").append(this.parentFileCanonicalPath).append(CLOSE)
	        .append(OPEN).append("documentPassword=").append(this.documentPassword).append(CLOSE)
	        .append(" )");
	    
	    return retValue.toString();
	}

	
	
	
}
