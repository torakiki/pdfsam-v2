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
 * Mix parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class MixParsedCommand extends AbstractParsedCommand implements Serializable {
	
	private static final long serialVersionUID = -2646601665244663267L;
	
	private File outputFile;	
	private File firstInputFile;	
	private File secondInputFile;	
	private boolean reverseFirst = false;
	private boolean reverseSecond = false;
	
	public MixParsedCommand(){		
	};
	
	public MixParsedCommand(File outputFile, File firstInputFile,
			File secondInputFile, boolean reverseFirst, boolean reverseSecond) {
		super();
		this.outputFile = outputFile;
		this.firstInputFile = firstInputFile;
		this.secondInputFile = secondInputFile;
		this.reverseFirst = reverseFirst;
		this.reverseSecond = reverseSecond;
	}

	public MixParsedCommand(File outputFile, File firstInputFile,File secondInputFile, boolean reverseFirst, boolean reverseSecond, boolean overwrite, boolean compress, File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.firstInputFile = firstInputFile;
		this.secondInputFile = secondInputFile;
		this.reverseFirst = reverseFirst;
		this.reverseSecond = reverseSecond;
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
	public File getFirstInputFile() {
		return firstInputFile;
	}

	/**
	 * @param firstInputFile the firstInputFile to set
	 */
	public void setFirstInputFile(File firstInputFile) {
		this.firstInputFile = firstInputFile;
	}

	/**
	 * @return the secondInputFile
	 */
	public File getSecondInputFile() {
		return secondInputFile;
	}

	/**
	 * @param secondInputFile the secondInputFile to set
	 */
	public void setSecondInputFile(File secondInputFile) {
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
	
	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((firstInputFile== null)?"":"[firstInputFile="+firstInputFile.getAbsolutePath()+"]");
		retVal.append((secondInputFile== null)?"":"[secondInputFile="+secondInputFile.getAbsolutePath()+"]");
		retVal.append("[reverseFirst="+reverseFirst+"]");
		retVal.append("[reverseSecond="+reverseSecond+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}
}
