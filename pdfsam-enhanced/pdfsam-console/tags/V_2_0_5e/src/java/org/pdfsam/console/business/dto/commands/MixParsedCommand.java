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
 */
package org.pdfsam.console.business.dto.commands;

import java.io.File;

import org.pdfsam.console.business.dto.PdfFile;

/**
 * Mix parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class MixParsedCommand extends AbstractParsedCommand {
	
	private static final long serialVersionUID = -2646601665244663267L;
	
	public static final int DEFAULT_STEP = 1;
	
	public static final String F1_ARG = "f1";
	public static final String REVERSE_FIRST_ARG = "reversefirst";
	public static final String F2_ARG = "f2";
	public static final String REVERSE_SECOND_ARG = "reversesecond";
	public static final String O_ARG = "o";
	public static final String STEP_ARG = "step";
	
	private File outputFile;	
	private PdfFile firstInputFile;	
	private PdfFile secondInputFile;	
	private boolean reverseFirst = false;
	private boolean reverseSecond = false;
	private int step = DEFAULT_STEP;
	
	public MixParsedCommand(){		
	};
	
	public MixParsedCommand(File outputFile, PdfFile firstInputFile,
			PdfFile secondInputFile, boolean reverseFirst, boolean reverseSecond, int step) {
		super();
		this.outputFile = outputFile;
		this.firstInputFile = firstInputFile;
		this.secondInputFile = secondInputFile;
		this.reverseFirst = reverseFirst;
		this.reverseSecond = reverseSecond;
		this.step = step;
	}
	
	/**
	 * @deprecated use the constructor without the logFile parameter
	 */
	public MixParsedCommand(File outputFile, PdfFile firstInputFile,PdfFile secondInputFile, boolean reverseFirst, boolean reverseSecond, int step, boolean overwrite, boolean compress, File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.firstInputFile = firstInputFile;
		this.secondInputFile = secondInputFile;
		this.reverseFirst = reverseFirst;
		this.reverseSecond = reverseSecond;
		this.step = step;
	}
	
	public MixParsedCommand(File outputFile, PdfFile firstInputFile,PdfFile secondInputFile, boolean reverseFirst, boolean reverseSecond, int step, boolean overwrite, boolean compress, char outputPdfVersion) {
		super(overwrite, compress, outputPdfVersion);
		this.outputFile = outputFile;
		this.firstInputFile = firstInputFile;
		this.secondInputFile = secondInputFile;
		this.reverseFirst = reverseFirst;
		this.reverseSecond = reverseSecond;
		this.step = step;
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
	 * @return the firstInputFile
	 */
	public PdfFile getFirstInputFile() {
		return firstInputFile;
	}

	/**
	 * @param firstInputFile the firstInputFile to set
	 */
	public void setFirstInputFile(PdfFile firstInputFile) {
		this.firstInputFile = firstInputFile;
	}

	/**
	 * @return the secondInputFile
	 */
	public PdfFile getSecondInputFile() {
		return secondInputFile;
	}

	/**
	 * @param secondInputFile the secondInputFile to set
	 */
	public void setSecondInputFile(PdfFile secondInputFile) {
		this.secondInputFile = secondInputFile;
	}

	/**
	 * @return the reverseFirst
	 */
	public boolean isReverseFirst() {
		return reverseFirst;
	}

	/**
	 * @param reverseFirst the reverseFirst to set
	 */
	public void setReverseFirst(boolean reverseFirst) {
		this.reverseFirst = reverseFirst;
	}

	/**
	 * @return the reverseSecond
	 */
	public boolean isReverseSecond() {
		return reverseSecond;
	}

	/**
	 * @param reverseSecond the reverseSecond to set
	 */
	public void setReverseSecond(boolean reverseSecond) {
		this.reverseSecond = reverseSecond;
	}

	
	public final String getCommand() {
		return COMMAND_MIX;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step the step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}
	
	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((firstInputFile== null)?"":"[firstInputFile="+firstInputFile.getFile().getAbsolutePath()+"]");
		retVal.append((secondInputFile== null)?"":"[secondInputFile="+secondInputFile.getFile().getAbsolutePath()+"]");
		retVal.append("[reverseFirst="+reverseFirst+"]");
		retVal.append("[reverseSecond="+reverseSecond+"]");
		retVal.append("[step="+step+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}

}
