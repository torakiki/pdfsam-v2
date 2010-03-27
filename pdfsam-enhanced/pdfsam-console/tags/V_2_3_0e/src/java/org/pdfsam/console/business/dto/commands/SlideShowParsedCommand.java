/*
 * Created on 07-Mar-2008
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
import org.pdfsam.console.business.dto.Transition;

/**
 * SlideShow parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class SlideShowParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = 498418601794673447L;
	
	public static final String F_ARG = "f";
	public static final String P_ARG = "p";
	public static final String O_ARG = "o";
	public static final String L_ARG = "l";
	public static final String T_ARG = "t";
	public static final String DT_ARG = "dt";
	public static final String FULLSCREEN_ARG = "fullscreen";
	
	private File outputFile;
    private PdfFile inputFile;
    private boolean fullScreen = false;
    private Transition defaultTransition;
    private Transition[] transitions;
	private File inputXmlFile;
	private String outputFilesPrefix = "";

	public SlideShowParsedCommand() {
	}

	public SlideShowParsedCommand(File outputFile, PdfFile inputFile, boolean fullScreen, Transition defaultTransition,
			Transition[] transitions, File inputXmlFile) {
		super();
		this.outputFile = outputFile;
		this.inputFile = inputFile;
		this.fullScreen = fullScreen;
		this.defaultTransition = defaultTransition;
		this.transitions = transitions;
		this.inputXmlFile = inputXmlFile;
	}

	/**
	 * @deprecated use the constructor without the logFile parameter
	 */
	public SlideShowParsedCommand(File outputFile, PdfFile inputFile, boolean fullScreen, Transition defaultTransition,
			Transition[] transitions, File inputXmlFile, boolean overwrite, boolean compress,
			File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.inputFile = inputFile;
		this.fullScreen = fullScreen;
		this.defaultTransition = defaultTransition;
		this.transitions = transitions;
		this.inputXmlFile = inputXmlFile;
	}	
	
	public SlideShowParsedCommand(File outputFile, PdfFile inputFile, boolean fullScreen, Transition defaultTransition,
			Transition[] transitions, File inputXmlFile, boolean overwrite, boolean compress,
			char outputPdfVersion) {
		super(overwrite, compress, outputPdfVersion);
		this.outputFile = outputFile;
		this.inputFile = inputFile;
		this.fullScreen = fullScreen;
		this.defaultTransition = defaultTransition;
		this.transitions = transitions;
		this.inputXmlFile = inputXmlFile;
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
	 * @return the fullScreen
	 */
	public boolean isFullScreen() {
		return fullScreen;
	}

	/**
	 * @param fullScreen the fullScreen to set
	 */
	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	/**
	 * @return the defaultTransition
	 */
	public Transition getDefaultTransition() {
		return defaultTransition;
	}

	/**
	 * @param defaultTransition the defaultTransition to set
	 */
	public void setDefaultTransition(Transition defaultTransition) {
		this.defaultTransition = defaultTransition;
	}

	/**
	 * @return the transitions
	 */
	public Transition[] getTransitions() {
		return transitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	public void setTransitions(Transition[] transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the inputXmlFile
	 */
	public File getInputXmlFile() {
		return inputXmlFile;
	}

	/**
	 * @param inputXmlFile the inputXmlFile to set
	 */
	public void setInputXmlFile(File inputXmlFile) {
		this.inputXmlFile = inputXmlFile;
	}

	public String getCommand() {
		return SlideShowParsedCommand.COMMAND_SLIDESHOW;
	}

	/**
	 * @return the outputFilesPrefix
	 */
	public String getOutputFilesPrefix() {
		return outputFilesPrefix;
	}

	/**
	 * @param outputFilesPrefix the outputFilesPrefix to set
	 */
	public void setOutputFilesPrefix(String outputFilesPrefix) {
		this.outputFilesPrefix = outputFilesPrefix;
	}

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((outputFile== null)?"":"[outputFile="+outputFile.getAbsolutePath()+"]");
		retVal.append((inputFile== null)?"":"[outputFile="+outputFile.getAbsolutePath()+"]");
		if(transitions!=null){
		retVal.append("[transitions.length="+transitions.length+"]");
		}
		retVal.append("[defaultTransition="+defaultTransition+"]");
		retVal.append("[fullScreen="+fullScreen+"]");
		retVal.append("[inputXmlFile="+inputXmlFile+"]");
		retVal.append("[outputFilesPrefix="+outputFilesPrefix+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}
}
