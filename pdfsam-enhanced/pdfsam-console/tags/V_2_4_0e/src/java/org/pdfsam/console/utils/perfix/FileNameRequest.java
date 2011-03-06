/*
 * Created on 11-Apr-2009
 * Copyright (C) 2009 by Andrea Vacondio.
 *
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
package org.pdfsam.console.utils.perfix;

import java.io.Serializable;

public class FileNameRequest implements Serializable{

	private static final long serialVersionUID = -4901506757147449856L;
	
	private Integer pageNumber = null;
	private Integer fileNumber = null;
	private String bookmarkName = null;
	
	/**
	 * @param pageNumber
	 * @param fileNumber
	 * @param bookmarkName
	 */
	public FileNameRequest(Integer pageNumber, Integer fileNumber, String bookmarkName) {
		super();
		this.pageNumber = pageNumber;
		this.fileNumber = fileNumber;
		this.bookmarkName = bookmarkName;
	}
	
	public FileNameRequest(int pageNumber, int fileNumber, String bookmarkName) {
		this(new Integer(pageNumber),new Integer(fileNumber), bookmarkName);
	}

	public FileNameRequest() {
	}

	/**
	 * @return the pageNumber
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @return the fileNumber
	 */
	public Integer getFileNumber() {
		return fileNumber;
	}

	/**
	 * @param fileNumber the fileNumber to set
	 */
	public void setFileNumber(Integer fileNumber) {
		this.fileNumber = fileNumber;
	}

	/**
	 * @return the bookmarkName
	 */
	public String getBookmarkName() {
		return bookmarkName;
	}

	/**
	 * @param bookmarkName the bookmarkName to set
	 */
	public void setBookmarkName(String bookmarkName) {
		this.bookmarkName = bookmarkName;
	}
	
	/**
	 * @return true if all the instance variables are null or empty
	 */
	public boolean isEmpty(){
		return ((bookmarkName==null || bookmarkName.length()==0) && (fileNumber==null) && (pageNumber==null));
	}
}
