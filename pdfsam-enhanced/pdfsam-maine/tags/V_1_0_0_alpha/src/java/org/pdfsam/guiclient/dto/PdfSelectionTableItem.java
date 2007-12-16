/*
 * Created on 18-Nov-2007
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
package org.pdfsam.guiclient.dto;

import java.io.File;
import java.io.Serializable;

import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;

/**
 * Item of the table in {@link JPdfSelectionPanel}
 * @author Andrea Vacondio
 *
 */
public class PdfSelectionTableItem implements Serializable{

	private static final long serialVersionUID = 2654013156896850862L;

	private File inputFile;
	private String pagesNumber;
	private String pageSelection;
	private boolean encrypted;
	private String password;
	/**
	 * 
	 */
	public PdfSelectionTableItem() {
		inputFile = null;
		pagesNumber = "0";
		pageSelection = "All";
		encrypted = false;
		password = null;
	}
	
	/**
	 * @param inputFile
	 * @param pagesNumber
	 * @param pageSelection
	 * @param encrypted
	 * @param password
	 */
	public PdfSelectionTableItem(File inputFile, String pagesNumber, String pageSelection, boolean encrypted, String password) {
		super();
		this.inputFile = inputFile;
		this.pagesNumber = pagesNumber;
		this.pageSelection = pageSelection;
		this.encrypted = encrypted;
		this.password = password;
	}

	/**
	 * No password given
	 * @param inputFile
	 * @param pagesNumber
	 * @param pageSelection
	 * @param encrypted
	 */
	public PdfSelectionTableItem(File inputFile, String pagesNumber,String pageSelection, boolean encrypted) {
		this(inputFile, pagesNumber, pageSelection, encrypted, null);
	}

	/**
	 * @return the inputFile
	 */
	public File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the pagesNumber
	 */
	public String getPagesNumber() {
		return pagesNumber;
	}

	/**
	 * @param pagesNumber the pagesNumber to set
	 */
	public void setPagesNumber(String pagesNumber) {
		this.pagesNumber = pagesNumber;
	}

	/**
	 * @return the pageSelection
	 */
	public String getPageSelection() {
		return pageSelection;
	}

	/**
	 * @param pageSelection the pageSelection to set
	 */
	public void setPageSelection(String pageSelection) {
		this.pageSelection = pageSelection;
	}

	/**
	 * @return the encrypted
	 */
	public boolean isEncrypted() {
		return encrypted;
	}

	/**
	 * @param encrypted the encrypted to set
	 */
	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (encrypted ? 1231 : 1237);
		result = prime * result
				+ ((inputFile == null) ? 0 : inputFile.hashCode());
		result = prime * result
				+ ((pageSelection == null) ? 0 : pageSelection.hashCode());
		result = prime * result
				+ ((pagesNumber == null) ? 0 : pagesNumber.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
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
		final PdfSelectionTableItem other = (PdfSelectionTableItem) obj;
		if (encrypted != other.encrypted)
			return false;
		if (inputFile == null) {
			if (other.inputFile != null)
				return false;
		} else if (!inputFile.equals(other.inputFile))
			return false;
		if (pageSelection == null) {
			if (other.pageSelection != null)
				return false;
		} else if (!pageSelection.equals(other.pageSelection))
			return false;
		if (pagesNumber == null) {
			if (other.pagesNumber != null)
				return false;
		} else if (!pagesNumber.equals(other.pagesNumber))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	
	
	
}
