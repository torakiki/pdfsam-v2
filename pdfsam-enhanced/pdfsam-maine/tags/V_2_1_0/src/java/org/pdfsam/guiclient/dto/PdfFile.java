/*
 * Created on 22-Apr-2009
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

import java.io.File;
import java.io.Serializable;
/**
 * A pdf file
 * @author Andrea Vacondio
 *
 */
public class PdfFile implements Serializable {

	private static final long serialVersionUID = -4896906812403711273L;
	
	private File inputFile = null;
	private String password = null;
	
	public PdfFile() {
	}
	/**
	 * @param inputFile
	 * @param password
	 */
	public PdfFile(File inputFile, String password) {
		super();
		this.inputFile = inputFile;
		this.password = password;
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
	
}
