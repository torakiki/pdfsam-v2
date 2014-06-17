/*
 * Created on 19-Aug-2009
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
package org.pdfsam.console.business.dto.commands;

import java.io.File;

import org.pdfsam.console.business.dto.PageLabel;
import org.pdfsam.console.business.dto.PdfFile;

/**
 * PageLabels parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class PageLabelsParsedCommand extends AbstractParsedCommand {
	
	private static final long serialVersionUID = -906202390144413350L;

	public static final String F_ARG = "f";
	public static final String O_ARG = "o";
	public static final String L_ARG = "l";
	public static final String LP_ARG = "lp";
	
	private File outputFile;	
	private PdfFile inputFile;
	private PageLabel[] labels = null;
	
	public PageLabelsParsedCommand(){
		super();
	}
	
	/**
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param labels
	 */
	public PageLabelsParsedCommand(PdfFile inputFile, File outputFile, PageLabel[] labels) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.labels = labels;
	}


	public PageLabelsParsedCommand(PdfFile inputFile, File outputFile, PageLabel[] labels, boolean overwrite, boolean compress, char outputPdfVersion) {
		super(overwrite, compress, outputPdfVersion);
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.labels = labels;
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
	 * @return the inputFile
	 */
	public PdfFile getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	public void setInputFile(PdfFile inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the labels
	 */
	public PageLabel[] getLabels() {
		return labels;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(PageLabel[] labels) {
		this.labels = labels;
	}

	public String getCommand() {
		return COMMAND_PAGELABELS;
	}

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((inputFile== null)?"":"[inputFile="+inputFile+"]");
		retVal.append((outputFile== null)?"":"[outputDir="+outputFile.getAbsolutePath()+"]");
		if(labels != null){
			for(int i = 0; i<labels.length; i++){
				retVal.append((labels[i]== null)?"":"[inputFileList["+i+"]="+labels[i]+"]");				
			}
		}
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}
}
