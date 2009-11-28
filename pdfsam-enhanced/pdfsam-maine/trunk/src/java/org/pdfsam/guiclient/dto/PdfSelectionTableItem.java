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
import org.pdfsam.guiclient.utils.PdfVersionUtility;

/**
 * Item of the table in {@link JPdfSelectionPanel}
 * @author Andrea Vacondio
 *
 */
public class PdfSelectionTableItem implements Serializable{

	private static final long serialVersionUID = 7190628784577648811L;

	private File inputFile;
	private String pagesNumber;
	private String pageSelection;
	private boolean encrypted;
	private boolean fullPermission;
	private String encryptionAlgorithm;
	private String permissions;
	private long fileSize = 0;
	private DocumentMetaData documentMetaData = new DocumentMetaData();
	private String password;
	private char pdfVersion;
	private String pdfVersionDescription;
	private boolean loadedWithErrors = false;
	private boolean syntaxErrors = false;
	/**
	 * Default values
	 */
	public PdfSelectionTableItem() {
		this(null, "0", "All", false, true, ' ');
	}
	
	/**
	 * @param inputFile
	 * @param pagesNumber
	 * @param pageSelection
	 * @param encrypted
	 * @param fullPermission
	 * @param password
	 * @param loadedWithErrors
	 * @param syntaxErrors
	 */
	public PdfSelectionTableItem(File inputFile, String pagesNumber, String pageSelection, boolean encrypted, boolean fullPermission, char pdfVersion, String password, boolean loadedWithErrors, boolean syntaxErrors) {
		super();
		this.inputFile = inputFile;
		this.pagesNumber = pagesNumber;
		this.pageSelection = pageSelection;
		this.encrypted = encrypted;
		this.pdfVersion = pdfVersion;
		this.pdfVersionDescription = PdfVersionUtility.getVersionDescription(pdfVersion);
		this.password = password;
		this.loadedWithErrors = loadedWithErrors;
		this.syntaxErrors = syntaxErrors;
		this.fullPermission = fullPermission;
	}

	/**
	 * loadedWithErrors is false
	 * @param inputFile
	 * @param pagesNumber
	 * @param pageSelection
	 * @param encrypted
	 * @param password
	 */
	public PdfSelectionTableItem(File inputFile, String pagesNumber, String pageSelection, boolean encrypted, boolean fullPermission, char pdfVersion, String password) {
		this(inputFile, pagesNumber, pageSelection, encrypted, fullPermission, pdfVersion, password, false, false);
	}
	/**
	 * No password given
	 * loadedWithErrors is false
	 * @param inputFile
	 * @param pagesNumber
	 * @param pageSelection
	 * @param encrypted
	 */
	public PdfSelectionTableItem(File inputFile, String pagesNumber,String pageSelection, boolean encrypted, boolean fullPermission, char pdfVersion) {
		this(inputFile, pagesNumber, pageSelection, encrypted, fullPermission, pdfVersion, null);
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

	
	/**
	 * @return the pdfVersion
	 */
	public char getPdfVersion() {
		return pdfVersion;
	}

	/**
	 * @param pdfVersion the pdfVersion to set
	 */
	public void setPdfVersion(char pdfVersion) {
		this.pdfVersion = pdfVersion;
		this.pdfVersionDescription = PdfVersionUtility.getVersionDescription(pdfVersion);
	}

	/**
	 * @return the loadedWithErrors
	 */
	public boolean isLoadedWithErrors() {
		return loadedWithErrors;
	}

	/**
	 * @param loadedWithErrors the loadedWithErrors to set
	 */
	public void setLoadedWithErrors(boolean loadedWithErrors) {
		this.loadedWithErrors = loadedWithErrors;
	}	

	/**
	 * @return the pdfVersionDescription
	 */
	public String getPdfVersionDescription() {
		return pdfVersionDescription;
	}

	/**
	 * @return the syntaxErrors
	 */
	public boolean isSyntaxErrors() {
		return syntaxErrors;
	}

	/**
	 * @param syntaxErrors the syntaxErrors to set
	 */
	public void setSyntaxErrors(boolean syntaxErrors) {
		this.syntaxErrors = syntaxErrors;
	}

	/**
	 * @return the encryptionAlgorithm
	 */
	public String getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	/**
	 * @param encryptionAlgorithm the encryptionAlgorithm to set
	 */
	public void setEncryptionAlgorithm(String encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	/**
	 * @return the permissions
	 */
	public String getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize the fileSize to set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the DocumentMetaData
	 */
	public DocumentMetaData getDocumentMetaData() { 
		return documentMetaData;
	}

	/**
	 * @param documentMetaData the DocumentMetaData to set
	 */
	public void setDocumentInfo(DocumentMetaData documentMetaData) {
		this.documentMetaData = documentMetaData;
	}
		

	/**
	 * @return the fullPermission
	 */
	public boolean isFullPermission() {
		return fullPermission;
	}

	/**
	 * @param fullPermission the fullPermission to set
	 */
	public void setFullPermission(boolean fullPermission) {
		this.fullPermission = fullPermission;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentMetaData == null) ? 0 : documentMetaData.hashCode());
		result = prime * result + (encrypted ? 1231 : 1237);
		result = prime * result + ((encryptionAlgorithm == null) ? 0 : encryptionAlgorithm.hashCode());
		result = prime * result + (int) (fileSize ^ (fileSize >>> 32));
		result = prime * result + (fullPermission ? 1231 : 1237);
		result = prime * result + ((inputFile == null) ? 0 : inputFile.hashCode());
		result = prime * result + (loadedWithErrors ? 1231 : 1237);
		result = prime * result + ((pageSelection == null) ? 0 : pageSelection.hashCode());
		result = prime * result + ((pagesNumber == null) ? 0 : pagesNumber.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + pdfVersion;
		result = prime * result + ((pdfVersionDescription == null) ? 0 : pdfVersionDescription.hashCode());
		result = prime * result + ((permissions == null) ? 0 : permissions.hashCode());
		result = prime * result + (syntaxErrors ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PdfSelectionTableItem other = (PdfSelectionTableItem) obj;
		if (documentMetaData == null) {
			if (other.documentMetaData != null)
				return false;
		} else if (!documentMetaData.equals(other.documentMetaData))
			return false;
		if (encrypted != other.encrypted)
			return false;
		if (encryptionAlgorithm == null) {
			if (other.encryptionAlgorithm != null)
				return false;
		} else if (!encryptionAlgorithm.equals(other.encryptionAlgorithm))
			return false;
		if (fileSize != other.fileSize)
			return false;
		if (fullPermission != other.fullPermission)
			return false;
		if (inputFile == null) {
			if (other.inputFile != null)
				return false;
		} else if (!inputFile.equals(other.inputFile))
			return false;
		if (loadedWithErrors != other.loadedWithErrors)
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
		if (pdfVersion != other.pdfVersion)
			return false;
		if (pdfVersionDescription == null) {
			if (other.pdfVersionDescription != null)
				return false;
		} else if (!pdfVersionDescription.equals(other.pdfVersionDescription))
			return false;
		if (permissions == null) {
			if (other.permissions != null)
				return false;
		} else if (!permissions.equals(other.permissions))
			return false;
		if (syntaxErrors != other.syntaxErrors)
			return false;
		return true;
	}
}
