/*
 * Created on 22-Oct-2007
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
package org.pdfsam.console.business.pdf.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.PdfFile;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.EncryptException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.PrefixParser;

import com.lowagie.text.pdf.PdfEncryptor;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Command executor for the encrypt command
 * @author Andrea Vacondio
 */
public class EncryptCmdExecutor extends AbstractCmdExecutor {

	private final Logger log = Logger.getLogger(EncryptCmdExecutor.class.getPackage().getName());
	
	public void execute(AbstractParsedCommand parsedCommand) throws EncryptException {
		
		if((parsedCommand != null) && (parsedCommand instanceof EncryptParsedCommand)){
			
			EncryptParsedCommand inputCommand = (EncryptParsedCommand) parsedCommand;
			setWorkIndeterminate();
			int encType = PdfWriter.STANDARD_ENCRYPTION_40;
			PrefixParser prefixParser;
			PdfReader pdfReader;
			PdfStamper pdfStamper;
			try{
				PdfFile[] fileList = inputCommand.getInputFileList();
				for(int i = 0; i<fileList.length; i++){
					//set the encryption type
		        	if (EncryptParsedCommand.E_AES_128.equals(inputCommand.getEncryptionType())){
			        	encType = PdfWriter.ENCRYPTION_AES_128;
			        }else if (EncryptParsedCommand.E_RC4_128.equals(inputCommand.getEncryptionType())){
			        	encType = PdfWriter.STANDARD_ENCRYPTION_128;
			        }	
		        	
					prefixParser = new PrefixParser(inputCommand.getOutputFilesPrefix(), fileList[i].getFile().getName());
					File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
					pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),fileList[i].getPasswordBytes());
					
					//version
					log.debug("Creating a new document.");
					Character pdfVersion = inputCommand.getOutputPdfVersion(); 
					if(pdfVersion != null){
						pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), inputCommand.getOutputPdfVersion().charValue());
					}else{
						pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), pdfReader.getPdfVersion());
					}

					HashMap meta = pdfReader.getInfo();
					meta.put("Creator", ConsoleServicesFacade.CREATOR);
					
					if(inputCommand.isCompress()){
						pdfStamper.setFullCompression();
			        }
					
					pdfStamper.setMoreInfo(meta);
					pdfStamper.setEncryption(encType, inputCommand.getUserPwd(), inputCommand.getOwnerPwd(), inputCommand.getPermissions());
					pdfStamper.close();
					File outFile = new File(inputCommand.getOutputFile() ,prefixParser.generateFileName());
					FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
				}
				log.info("Pdf files encrypted in "+inputCommand.getOutputFile().getAbsolutePath()+".");
				log.info("Permissions: "+PdfEncryptor.getPermissionsVerbose(inputCommand.getPermissions())+".");
		}catch(Exception e){    		
			throw new EncryptException(e);
		}finally{
			setWorkCompleted();
		}
	}else{
		throw new EncryptException(EncryptException.ERR_BAD_COMMAND);
	}

	}

}
