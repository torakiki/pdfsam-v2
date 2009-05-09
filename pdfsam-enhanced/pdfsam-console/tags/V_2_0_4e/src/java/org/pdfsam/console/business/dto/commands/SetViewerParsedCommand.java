/*
 * Created on 06-Mar-2008
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
 * SetViewer parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class SetViewerParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = -8634290084366871560L;

	//arguments
	public static final String F_ARG = "f";
	public static final String O_ARG = "o";
	public static final String P_ARG = "p";
	public static final String D_ARG = "d";

	public static final String M_ARG = "mode";
	public static final String NFSM_ARG = "nfsmode";
	public static final String L_ARG = "layout";
	public static final String DIRECTION_ARG = "direction";
	public static final String HIDETOOLBAR_ARG = "hidetoolbar";
	public static final String HIDEMENU_ARG = "hidemenu";
	public static final String HIDEWINDOWUI_ARG = "hidewindowui";
	public static final String FITWINDOW_ARG = "fitwindow";
	public static final String CENTERWINDOW_ARG = "centerwindow";
	public static final String DOCTITLE_ARG = "displaydoctitle";
	public static final String NOPRINTSCALING_ARG = "noprintscaling";
	
	//layouts
	public static final String L_SINGLEPAGE = "singlepage";
	public static final String L_ONECOLUMN = "onecolumn";
	public static final String L_TWOCOLUMNLEFT = "twocolumnl";
	public static final String L_TWOCOLUMNRIGHT = "twocolumnr";
	public static final String L_TWOPAGELEFT = "twopagel";
	public static final String L_TWOPAGERIGHT = "twopager";
	
	//modes
	public static final String M_NONE = "none";
	public static final String M_OUTLINES = "outlines";
	public static final String M_THUMBS = "thumbs";
	public static final String M_FULLSCREEN = "fullscreen";
	public static final String M_OCONTENT = "ocontent";
	public static final String M_ATTACHMENTS = "attachments";
	
	//non full screen modes
	public static final String NFSM_NONE = "nfsnone";
	public static final String NFSM_OUTLINES = "nfsoutlines";
	public static final String NFSM_THUMBS = "nfsthumbs";
	public static final String NFSM_OCONTENT = "nfsocontent";
	
	//directions
	public static final String D_L2R = "l2r";
	public static final String D_R2L = "r2l";
	
	private File outputFile;
	private int mode = 0;
	private int layout = 0;
	private int nfsmode = 0;
	private int direction = 0;
    private PdfFile[] inputFileList;
	private File inputDirectory;
	private String outputFilesPrefix = "";
	private boolean hideToolBar = false;   
	private boolean hideMenu = false;   
	private boolean hideWindowUI = false;   
	private boolean fitWindow = false;   
	private boolean centerWindow = false;   
	private boolean displayDocTitle = false;   
	private boolean noPrintScaling = false;   
    
    
    public SetViewerParsedCommand(){    	
    } 
       
	public SetViewerParsedCommand(File outputFile, int mode, int layout, int nfsmode, int direction, PdfFile[] inputFileList, String outputFilesPrefix,
			boolean hideToolBar, boolean hideMenu, boolean hideWindowUI,boolean fitWindow, boolean centerWindow, boolean displayDocTitle,
			boolean noPrintScaling, File inputDirectory) {
		super();
		this.outputFile = outputFile;
		this.mode = mode;
		this.layout = layout;
		this.nfsmode = nfsmode;
		this.direction = direction;
		this.inputFileList = inputFileList;
		this.outputFilesPrefix = outputFilesPrefix;
		this.hideToolBar = hideToolBar;
		this.hideMenu = hideMenu;
		this.hideWindowUI = hideWindowUI;
		this.fitWindow = fitWindow;
		this.centerWindow = centerWindow;
		this.displayDocTitle = displayDocTitle;
		this.noPrintScaling = noPrintScaling;
		this.inputDirectory = inputDirectory;
	}

	public SetViewerParsedCommand(File outputFile, int mode, int layout, int nfsmode, int direction, PdfFile[] inputFileList, String outputFilesPrefix,
			boolean hideToolBar, boolean hideMenu, boolean hideWindowUI,boolean fitWindow, boolean centerWindow, boolean displayDocTitle,
			boolean noPrintScaling, File inputDirectory, boolean overwrite, boolean compress,
			File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.mode = mode;
		this.layout = layout;
		this.nfsmode = nfsmode;
		this.direction = direction;
		this.inputFileList = inputFileList;
		this.outputFilesPrefix = outputFilesPrefix;
		this.hideToolBar = hideToolBar;
		this.hideMenu = hideMenu;
		this.hideWindowUI = hideWindowUI;
		this.fitWindow = fitWindow;
		this.centerWindow = centerWindow;
		this.displayDocTitle = displayDocTitle;
		this.noPrintScaling = noPrintScaling;
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
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * @return the layout
	 */
	public int getLayout() {
		return layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(int layout) {
		this.layout = layout;
	}

	/**
	 * @return the nfsmode
	 */
	public int getNfsmode() {
		return nfsmode;
	}

	/**
	 * @param nfsmode the nfsmode to set
	 */
	public void setNfsmode(int nfsmode) {
		this.nfsmode = nfsmode;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
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

	/**
	 * @return the hideToolBar
	 */
	public boolean isHideToolBar() {
		return hideToolBar;
	}

	/**
	 * @param hideToolBar the hideToolBar to set
	 */
	public void setHideToolBar(boolean hideToolBar) {
		this.hideToolBar = hideToolBar;
	}

	/**
	 * @return the hideMenu
	 */
	public boolean isHideMenu() {
		return hideMenu;
	}

	/**
	 * @param hideMenu the hideMenu to set
	 */
	public void setHideMenu(boolean hideMenu) {
		this.hideMenu = hideMenu;
	}

	/**
	 * @return the hideWindowUI
	 */
	public boolean isHideWindowUI() {
		return hideWindowUI;
	}

	/**
	 * @param hideWindowUI the hideWindowUI to set
	 */
	public void setHideWindowUI(boolean hideWindowUI) {
		this.hideWindowUI = hideWindowUI;
	}

	/**
	 * @return the fitWindow
	 */
	public boolean isFitWindow() {
		return fitWindow;
	}

	/**
	 * @param fitWindow the fitWindow to set
	 */
	public void setFitWindow(boolean fitWindow) {
		this.fitWindow = fitWindow;
	}

	/**
	 * @return the centerWindow
	 */
	public boolean isCenterWindow() {
		return centerWindow;
	}

	/**
	 * @param centerWindow the centerWindow to set
	 */
	public void setCenterWindow(boolean centerWindow) {
		this.centerWindow = centerWindow;
	}

	/**
	 * @return the displayDocTitle
	 */
	public boolean isDisplayDocTitle() {
		return displayDocTitle;
	}

	/**
	 * @param displayDocTitle the displayDocTitle to set
	 */
	public void setDisplayDocTitle(boolean displayDocTitle) {
		this.displayDocTitle = displayDocTitle;
	}

	/**
	 * @return the noPrintScaling
	 */
	public boolean isNoPrintScaling() {
		return noPrintScaling;
	}

	/**
	 * @param noPrintScaling the noPrintScaling to set
	 */
	public void setNoPrintScaling(boolean noPrintScaling) {
		this.noPrintScaling = noPrintScaling;
	}

	public String getCommand() {
		return COMMAND_SETVIEWER;
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
		retVal.append("[mode="+mode+"]");
		retVal.append("[layout="+layout+"]");
		retVal.append("[nfsmode="+nfsmode+"]");
		retVal.append("[direction="+direction+"]");
		retVal.append("[hideToolBar="+hideToolBar+"]");
		retVal.append("[hideWindowUI="+hideWindowUI+"]");
		retVal.append("[fitWindow="+fitWindow+"]");
		retVal.append("[centerWindow="+centerWindow+"]");
		retVal.append("[displayDocTitle="+displayDocTitle+"]");
		retVal.append("[noPrintScaling="+noPrintScaling+"]");
		retVal.append((inputDirectory== null)?"":"[inputDirectory="+inputDirectory.getAbsolutePath()+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
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

}
