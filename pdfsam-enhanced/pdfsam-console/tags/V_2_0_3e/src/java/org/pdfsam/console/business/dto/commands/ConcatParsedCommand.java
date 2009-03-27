/*
 * Created on 1-Oct-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
 *   */
package org.pdfsam.console.business.dto.commands;

import java.io.File;

import org.pdfsam.console.business.dto.PageRotation;
import org.pdfsam.console.business.dto.PdfFile;


/**
 * Concat parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class ConcatParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = 2204294454175542123L;
	
	public static final String F_ARG = "f";
	public static final String COPYFIELDS_ARG = "copyfields";
	public static final String L_ARG = "l";
	public static final String U_ARG = "u";
	public static final String O_ARG = "o";
	public static final String R_ARG = "r";
	public static final String D_ARG = "d";
	
	private File outputFile;
	private File inputCvsOrXmlFile;
	private File inputDirectory;
	private PdfFile[] inputFileList;
	private String pageSelection = "";
	private PageRotation[] rotations = null;
	private boolean copyFields = false;

	public ConcatParsedCommand(){		
	}
	
	public ConcatParsedCommand(File outputFile, File inputCvsOrXmlFile,
			PdfFile[] inputFileList, String pageSelection, boolean copyFields, PageRotation[] rotations, File inputDirectory) {
		super();
		this.outputFile = outputFile;
		this.inputCvsOrXmlFile = inputCvsOrXmlFile;
		this.inputFileList = inputFileList;
		this.pageSelection = pageSelection;
		this.copyFields = copyFields;
		this.rotations = rotations;
		this.inputDirectory = inputDirectory;
	}
	
	public ConcatParsedCommand(File outputFile, File inputCvsOrXmlFile,
			PdfFile[] inputFileList, String pageSelection,  boolean copyFields, PageRotation[] rotations, File inputDirectory, boolean overwrite, boolean compress, File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.inputCvsOrXmlFile = inputCvsOrXmlFile;
		this.inputFileList = inputFileList;
		this.pageSelection = pageSelection;
		this.copyFields = copyFields;
		this.rotations = rotations;
		this.inputDirectory = inputDirectory;
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
	public PdfFile[] getInputFileList() {
		return inputFileList;
	}


	/**
	 * @param inputFileList the inputFileList to set
	 */
	public void setInputFileList(PdfFile[] inputFileList) {
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

	/**
	 * @return the rotations
	 */
	public PageRotation[] getRotations() {
		return rotations;
	}

	/**
	 * @param rotations the rotations to set
	 */
	public void setRotations(PageRotation[] rotations) {
		this.rotations = rotations;
	}

	/**
	 * @return the inputDirectory
	 */
	public File getInputDirectory() {
		return inputDirectory;
	}

	/**
	 * @param inputDirectory the inputDirectory to set
	 */
	public void setInputDirectory(File inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public final String getCommand() {
		return COMMAND_CONCAT;
	}

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((outputFile== null)?"":"[outputFile="+outputFile.getAbsolutePath()+"]");
		retVal.append((inputDirectory== null)?"":"[inputDirectory="+inputDirectory.getAbsolutePath()+"]");
		if(inputFileList != null){
			for(int i = 0; i<inputFileList.length; i++){
				retVal.append((inputFileList[i]== null)?"":"[inputFileList["+i+"]="+inputFileList[i].getFile().getAbsolutePath()+"]");				
			}
		}
		retVal.append("[rotations="+rotations+"]");
		retVal.append((inputCvsOrXmlFile== null)?"":"[inputCvsOrXmlFile="+inputCvsOrXmlFile.getAbsolutePath()+"]");
		retVal.append("[pageSelection="+pageSelection+"]");
		retVal.append("[copyFields="+copyFields+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}

}
