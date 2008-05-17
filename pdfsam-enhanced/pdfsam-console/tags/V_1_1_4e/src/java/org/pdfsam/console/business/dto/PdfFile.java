/*
 * Created on 11-Dec-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

package org.pdfsam.console.business.dto;

import java.io.File;
import java.io.Serializable;
/**
 * Model of a password protected pdf file
 * @author Andrea Vacondio
 *
 */
public class PdfFile implements Serializable{

	private static final long serialVersionUID = -389852483075260271L;

	private File file;
	private String password;
	
	public PdfFile(){		
	}
	
	
	/**
	 * @param file
	 * @param password
	 */
	public PdfFile(File file, String password) {
		this.file = file;
		this.password = password;
	}

	/**
	 * @param filePath
	 * @param password
	 */
	public PdfFile(String filePath, String password) {
		this.file = new File(filePath);
		this.password = password;
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the password in bytes or null
	 */
	public byte[] getPasswordBytes() {
		return (password!=null)? password.getBytes():null;
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
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		final PdfFile other = (PdfFile) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
	

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((file== null)?"":"[file="+file.getAbsolutePath()+"]");
		retVal.append("[password="+password+"]");
		return retVal.toString();
	}
	
}
