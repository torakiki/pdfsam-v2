/*
 * Created on 1-Oct-2007
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
package org.pdfsam.console.business.dto.commands;

import java.io.File;
import java.io.Serializable;

/**
 * Concat parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class ConcatParsedCommand extends AbstractParsedCommand implements Serializable {

	private static final long serialVersionUID = 2204294454175542123L;
	
	public static final String F_ARG = "f";
	public static final String COPYFIELDS_ARG = "copyfields";
	public static final String L_ARG = "l";
	public static final String U_ARG = "u";
	public static final String O_ARG = "o";
	
	private File outputFile;
	private File inputCvsOrXmlFile;
	private File[] inputFileList;
	private String pageSelection = "";
	private boolean copyFields = false;

	public ConcatParsedCommand(){		
	}
	
	public ConcatParsedCommand(File outputFile, File inputCvsOrXmlFile,
			File[] inputFileList, String pageSelection, boolean copyFields) {
		super();
		this.outputFile = outputFile;
		this.inputCvsOrXmlFile = inputCvsOrXmlFile;
		this.inputFileList = inputFileList;
		this.pageSelection = pageSelection;
		this.copyFields = copyFields;
	}
	
	public ConcatParsedCommand(File outputFile, File inputCvsOrXmlFile,
			File[] inputFileList, String pageSelection,  boolean copyFields, boolean overwrite, boolean compress, File logFile, char outputPdfVersion) {
			super(overwrite, compress, logFile, outputPdfVersion);
			this.outputFile = outputFile;
			this.inputCvsOrXmlFile = inputCvsOrXmlFile;
			this.inputFileList = inputFileList;
			this.pageSelection = pageSelection;
			this.copyFields = copyFields;
	}

	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}


	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}


	/**
	 * @return the inputCvsOrXmlFile
	 */
	public File getInputCvsOrXmlFile() {
		return inputCvsOrXmlFile;
	}


	/**
	 * @param inputCvsOrXmlFile the inputCvsOrXmlFile to set
	 */
	public void setInputCvsOrXmlFile(File inputCvsOrXmlFile) {
		this.inputCvsOrXmlFile = inputCvsOrXmlFile;
	}


	/**
	 * @return the inputFileList
	 */
	public File[] getInputFileList() {
		return inputFileList;
	}


	/**
	 * @param inputFileList the inputFileList to set
	 */
	public void setInputFileList(File[] inputFileList) {
		this.inputFileList = inputFileList;
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
	 * @return the copyFields
	 */
	public boolean isCopyFields() {
		return copyFields;
	}

	/**
	 * @param copyFields the copyFields to set
	 */
	public void setCopyFields(boolean copyFields) {
		this.copyFields = copyFields;
	}

	public final String getCommand() {
		return COMMAND_CONCAT;
	}

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((outputFile== null)?"":"[outputFile="+outputFile.getAbsolutePath()+"]");
		if(inputFileList != null){
			for(int i = 0; i<inputFileList.length; i++){
				retVal.append((inputFileList[i]== null)?"":"[inputFileList["+i+"]="+inputFileList[i].getAbsolutePath()+"]");				
			}
		}
		retVal.append((inputCvsOrXmlFile== null)?"":"[inputCvsOrXmlFile="+inputCvsOrXmlFile.getAbsolutePath()+"]");
		retVal.append("[pageSelection="+pageSelection+"]");
		retVal.append("[copyFields="+copyFields+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}
}
