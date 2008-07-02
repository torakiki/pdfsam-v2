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
/**
 * DTO representing a page of a document 
 * @author Andrea Vacondio
 *
 */
public class VisualPageListItem implements Serializable {

	private static final long serialVersionUID = 7598120284619680606L;

	private Image thumbnail;
	private int pageNumber;
	private boolean deleted = false;

	public VisualPageListItem() {
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
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted) {
		super();
		this.thumbnail = thumbnail;
		this.pageNumber = pageNumber;
		this.deleted = deleted;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + pageNumber;
		result = prime * result + ((thumbnail == null) ? 0 : thumbnail.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VisualPageListItem other = (VisualPageListItem) obj;
		if (deleted != other.deleted)
			return false;
		if (pageNumber != other.pageNumber)
			return false;
		if (thumbnail == null) {
			if (other.thumbnail != null)
				return false;
		} else if (!thumbnail.equals(other.thumbnail))
			return false;
		return true;
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
	        .append(" )");
	    
	    return retValue.toString();
	}
	
	
}
