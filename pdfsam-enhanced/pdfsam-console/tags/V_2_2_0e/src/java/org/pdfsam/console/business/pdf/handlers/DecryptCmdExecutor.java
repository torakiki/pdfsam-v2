/*
 * Created on 30-Oct-2008
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
package org.pdfsam.console.business.pdf.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.PdfFile;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.DecryptParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.perfix.PrefixParser;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

public class DecryptCmdExecutor extends AbstractCmdExecutor {

	private static final Logger LOG = Logger.getLogger(DecryptCmdExecutor.class.getPackage().getName());
	
	private PdfReader pdfReader = null;
	private PdfStamper pdfStamper = null;
	
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		
		if((parsedCommand != null) && (parsedCommand instanceof DecryptParsedCommand)){
			
			DecryptParsedCommand inputCommand = (DecryptParsedCommand) parsedCommand;
			setPercentageOfWorkDone(0);
			PrefixParser prefixParser;
			try{
				PdfFile[] fileList = inputCommand.getInputFileList();
				for(int i = 0; i<fileList.length; i++){
					try{						
			        	
						prefixParser = new PrefixParser(inputCommand.getOutputFilesPrefix(), fileList[i].getFile().getName());
						File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
						pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),fileList[i].getPasswordBytes());
						pdfReader.removeUnusedObjects();
						pdfReader.consolidateNamedDestinations();
						
						//version
						LOG.debug("Creating a new document.");
						Character pdfVersion = inputCommand.getOutputPdfVersion(); 
						if(pdfVersion != null){
							pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), inputCommand.getOutputPdfVersion().charValue());
						}else{
							pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), pdfReader.getPdfVersion());
						}
	
						HashMap meta = pdfReader.getInfo();
						meta.put("Creator", ConsoleServicesFacade.CREATOR);
						
						setCompressionSettingOnStamper(inputCommand, pdfStamper);
						
						pdfStamper.setMoreInfo(meta);
						pdfStamper.close();
						pdfReader.close();
						File outFile = new File(inputCommand.getOutputFile() ,prefixParser.generateFileName());
			    		FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
	                	LOG.debug("Decrypted file "+outFile.getCanonicalPath()+" created.");			    		
			    		setPercentageOfWorkDone(((i+1)*WorkDoneDataModel.MAX_PERGENTAGE)/fileList.length);	
		    		}
		    		catch(Exception e){
		    			LOG.error("Error decrypting file "+fileList[i].getFile().getName(), e);
		    		}
				}
				LOG.info("Pdf files decrypted in "+inputCommand.getOutputFile().getAbsolutePath()+".");
			}finally{
				setWorkCompleted();
			}
		}else{
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}
	}

	
	public void clean(){
		closePdfReader(pdfReader);
		closePdfStamper(pdfStamper);
	}
}
