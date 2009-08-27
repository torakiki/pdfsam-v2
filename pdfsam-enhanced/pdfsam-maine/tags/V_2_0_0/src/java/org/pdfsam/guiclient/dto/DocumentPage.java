/*
 * Created on 22-Jul-2008
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
 * Document page model used when loading the environment
 * @author Andrea Vacondio
 *
 */
public class DocumentPage implements Serializable {

	private static final long serialVersionUID = -5641009544900617998L;
	
	private int pageNumber;
	private boolean deleted;
	private Rotation rotation = Rotation.DEGREES_0;
	
	public DocumentPage() {
	}

	/**
	 * @param pageNumber
	 * @param deleted
	 * @param rotation
	 */
	public DocumentPage(int pageNumber, boolean deleted, Rotation rotation) {
		super();
		this.pageNumber = pageNumber;
		this.deleted = deleted;
		this.rotation = rotation;
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
	 * @return the rotation
	 */
	public Rotation getRotation() {
		return rotation;
	}
	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}
	
	
}
