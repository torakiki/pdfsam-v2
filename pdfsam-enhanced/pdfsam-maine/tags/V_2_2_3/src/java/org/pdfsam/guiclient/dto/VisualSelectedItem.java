/*
 * Created on 24-Nov-2009
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

import java.io.Serializable;

/**
 * Model for a selected file (-f console option) with its selection page string
 * (-u console option)
 * 
 * @author Andrea Vacondio
 * 
 */
public class VisualSelectedItem implements Serializable{

	private static final long serialVersionUID = -2575583754215291656L;

	private String selectedFile;
	
	private String password;

	private String pagesSelection;

	public VisualSelectedItem(){
		super();
	}

	/**
	 * Constructor with no page selection
	 * @param selectedFile
	 * @param password
	 */
	public VisualSelectedItem(String selectedFile, String password) {
		super();
		this.selectedFile = selectedFile;
		this.password = password;
	}

	/**
	 * @return the selectedFile
	 */
	public String getSelectedFile() {
		return selectedFile;
	}

	/**
	 * @param selectedFile
	 *            the selectedFile to set
	 */
	public void setSelectedFile(String selectedFile) {
		this.selectedFile = selectedFile;
	}

	/**
	 * @return the pagesSelection
	 */
	public String getPagesSelection() {
		return pagesSelection;
	}

	/**
	 * @param pagesSelection
	 *            the pagesSelection to set
	 */
	public void setPagesSelection(String pagesSelection) {
		this.pagesSelection = pagesSelection;
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
