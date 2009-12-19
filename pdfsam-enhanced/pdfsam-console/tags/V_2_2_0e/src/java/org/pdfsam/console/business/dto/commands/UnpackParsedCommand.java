/*
 * Created on 30-Jan-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.console.business.dto.commands;

import java.io.File;
import org.pdfsam.console.business.dto.PdfFile;
/**
 * Unpack parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class UnpackParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = -8889630614812210583L;

	public static final String F_ARG = "f";
	public static final String D_ARG = "d";
	public static final String O_ARG = "o";

	private File outputFile;	
	private File inputDirectory;	
	private PdfFile[] inputFileList;

	public UnpackParsedCommand() {
	}

	/**
	 * @param outputFile
	 * @param inputDirectory
	 * @param inputFileList
	 */
	public UnpackParsedCommand(File outputFile, File inputDirectory,
			PdfFile[] inputFileList) {
		this.outputFile = outputFile;
		this.inputDirectory = inputDirectory;
		this.inputFileList = inputFileList;
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

	public final String getCommand() {
		return COMMAND_UNPACK;
	}
	
	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((outputFile== null)?"":"[outputDir="+outputFile.getAbsolutePath()+"]");
		if(inputFileList != null){
			for(int i = 0; i<inputFileList.length; i++){
				retVal.append((inputFileList[i]== null)?"":"[inputFileList["+i+"]="+inputFileList[i].getFile().getAbsolutePath()+"]");				
			}
		}
		retVal.append((inputDirectory== null)?"":"[inputDirectory="+inputDirectory.getAbsolutePath()+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}
}
