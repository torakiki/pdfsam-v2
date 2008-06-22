/*
 * Created on 16-Oct-2007
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
 * Encrypt parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 *
 */
public class EncryptParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = -4179486767271521110L;
	
	//arguments
	public static final String F_ARG = "f";
	public static final String UPWD_ARG = "upwd";
	public static final String APWD_ARG = "apwd";
	public static final String P_ARG = "p";
	public static final String O_ARG = "o";
	public static final String ALLOW_ARG = "allow";
	public static final String ETYPE_ARG = "etype";
	
	//constants used to get the encrypt mode
    public static final String E_PRINT = "print";
    public static final String E_MODIFY = "modify";    
    public static final String E_COPY = "copy";    
    public static final String E_ANNOTATION = "modifyannotations";    
    public static final String E_SCREEN = "screenreaders";
    public static final String E_FILL = "fill";    
    public static final String E_ASSEMBLY = "assembly";
    public static final String E_DPRINT = "degradedprinting";
	
    //constants used to set the encrypt algorithm
    public static final String E_RC4_40 = "rc4_40";    
    public static final String E_RC4_128 = "rc4_128";
    public static final String E_AES_128 = "aes_128";
    
	private File outputFile;
	private String outputFilesPrefix = "";
	private String ownerPwd = "";
	private String userPwd = "";
    private int permissions;
    private String encryptionType = E_RC4_40;    
    private PdfFile[] inputFileList;
    
    
    public EncryptParsedCommand(){    	
    }       
    
	public EncryptParsedCommand(File outputFile, String outputFilesPrefix,
			String ownerPwd, String userPwd, int permissions,
			String encryptionType, PdfFile[] inputFileList) {
		super();
		this.outputFile = outputFile;
		this.outputFilesPrefix = outputFilesPrefix;
		this.ownerPwd = ownerPwd;
		this.userPwd = userPwd;
		this.permissions = permissions;
		this.encryptionType = encryptionType;
		this.inputFileList = inputFileList;
	}

	public EncryptParsedCommand(File outputFile, String outputFilesPrefix,
			String ownerPwd, String userPwd, int permissions,
			String encryptionType, PdfFile[] inputFileList, boolean overwrite, boolean compress, File logFile, char outputPdfVersion) {
		super(overwrite, compress, logFile, outputPdfVersion);
		this.outputFile = outputFile;
		this.outputFilesPrefix = outputFilesPrefix;
		this.ownerPwd = ownerPwd;
		this.userPwd = userPwd;
		this.permissions = permissions;
		this.encryptionType = encryptionType;
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
	 * @return the ownerPwd
	 */
	public String getOwnerPwd() {
		return ownerPwd;
	}

	/**
	 * @param ownerPwd the ownerPwd to set
	 */
	public void setOwnerPwd(String ownerPwd) {
		this.ownerPwd = ownerPwd;
	}

	/**
	 * @return the userPwd
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * @param userPwd the userPwd to set
	 */
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * @return the permissions
	 */
	public int getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the encryptionType
	 */
	public String getEncryptionType() {
		return encryptionType;
	}

	/**
	 * @param encryptionType the encryptionType to set
	 */
	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
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

	public String getCommand() {
		return COMMAND_ECRYPT;
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
		retVal.append("[pageSelection="+outputFilesPrefix+"]");
		retVal.append("[ownerPwd="+ownerPwd+"]");
		retVal.append("[userPwd="+userPwd+"]");
		retVal.append("[encryptionType="+encryptionType+"]");
		retVal.append("[permissions="+permissions+"]");
		retVal.append("[command="+getCommand()+"]");
		return retVal.toString();
	}

}
