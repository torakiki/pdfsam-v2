/*
 * Created on 31-Jan-2008
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
 *  
 *  This class is based on the ExtractAttachments.java part of the iText toolbox 0.0.2  
 *  $Id: ExtractAttachments.java 45 2007-05-15 22:02:53Z chammer $
 * 	Copyright (c) 2005-2007 Paulo Soares, Bruno Lowagie, Carsten Hammer 
 */
package org.pdfsam.console.business.pdf.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.PdfFile;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.UnpackParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.UnpackException;
import org.pdfsam.console.utils.PdfFilter;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Command executor for the unpack command
 * @author Andrea Vacondio
 */
public class UnpackCmdExecutor extends AbstractCmdExecutor {

	private final Logger log = Logger.getLogger(UnpackCmdExecutor.class.getPackage().getName());
	
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		if((parsedCommand != null) && (parsedCommand instanceof UnpackParsedCommand)){
			
			UnpackParsedCommand inputCommand = (UnpackParsedCommand) parsedCommand;
			setWorkIndeterminate();
			PdfReader pdfReader;
			try{	
				ArrayList fileLists = new ArrayList();
				if(inputCommand.getInputFileList() != null){
					fileLists.add(inputCommand.getInputFileList());
				}
				if(inputCommand.getInputDirectory() != null){
					PdfFile[] foundFiles = getPdfFiles(inputCommand.getInputDirectory());
					if(foundFiles != null){
						fileLists.add(foundFiles);
					}else{
						log.warn("No pdf documents found in "+inputCommand.getInputDirectory());
					}
				}
				for(Iterator listsIter = fileLists.iterator(); listsIter.hasNext();){
					PdfFile[] fileList = (PdfFile[])listsIter.next();
					for(int i = 0; i<fileList.length; i++){
						int unpackedFiles = 0;
						try{
							pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),fileList[i].getPasswordBytes());
							PdfDictionary catalog = pdfReader.getCatalog();
							PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES));
							if (names != null) {
								PdfDictionary embFiles = (PdfDictionary) PdfReader.getPdfObject(names.get(new PdfName("EmbeddedFiles")));
								if (embFiles != null) {
									HashMap embMap = PdfNameTree.readTree(embFiles);
									for (Iterator iter = embMap.values().iterator(); iter.hasNext();) {
										PdfDictionary filespec = (PdfDictionary) PdfReader.getPdfObject((PdfObject) iter.next());
										unpackedFiles += unpackFile(filespec, inputCommand.getOutputFile(), inputCommand.isOverwrite());
									}
								}
							}
							for (int k = 1; k <= pdfReader.getNumberOfPages(); ++k) {
								PdfArray annots = (PdfArray) PdfReader.getPdfObject(pdfReader.getPageN(k).get(PdfName.ANNOTS));
								if (annots != null){
									for (Iterator iter = annots.listIterator(); iter.hasNext();) {
										PdfDictionary annot = (PdfDictionary) PdfReader.getPdfObject((PdfObject) iter.next());
										PdfName subType = (PdfName) PdfReader.getPdfObject(annot.get(PdfName.SUBTYPE));
										if (PdfName.FILEATTACHMENT.equals(subType)){
											PdfDictionary filespec = (PdfDictionary) PdfReader.getPdfObject(annot.get(PdfName.FS));
											unpackedFiles += unpackFile(filespec, inputCommand.getOutputFile(), inputCommand.isOverwrite());
										}
									}
								}
							}
							pdfReader.close();
							if(unpackedFiles >0){
								log.info("File "+fileList[i].getFile().getName()+" unpacked, found "+unpackedFiles+" attachments.");
							}else{
								log.info("No attachments in "+fileList[i].getFile().getName()+".");
							}
						}catch(Exception e){
			    			log.error("Error unpacking file "+fileList[i].getFile().getName(), e);
			    		}
					}
				}
			}catch(Exception e){    		
				throw new UnpackException(e);
			}finally{
				setWorkCompleted();
			}
		}
		else{
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}
	}


	/**
	 * Unpack
	 * @param filespec the dictionary
	 * @param outPath output directory
	 * @param overwrite if true overwrite if already exists
	 * @return number of unpacked files
	 * @throws IOException
	 */
	private int unpackFile(PdfDictionary filespec, File outPath, boolean overwrite) throws IOException {
		int retVal = 0;
		if (filespec != null){
			PdfName type = (PdfName) PdfReader.getPdfObject(filespec.get(PdfName.TYPE));
			if (PdfName.F.equals(type) || PdfName.FILESPEC.equals(type)){
				PdfDictionary ef = (PdfDictionary) PdfReader.getPdfObject(filespec.get(PdfName.EF));
				PdfString fn = (PdfString) PdfReader.getPdfObject(filespec.get(PdfName.F));
				if (fn != null && ef != null){
					log.debug("Unpacking file " + fn + " to " + outPath);
					File fLast = new File(fn.toUnicodeString());
					File fullPath = new File(outPath, fLast.getName());
					if (fullPath.exists()){
				          //check if overwrite is allowed
				          if (overwrite){
				              if(!fullPath.delete()){
						    	 log.warn("Unable to overwrite "+fullPath.getAbsolutePath()+", unable to unpack.");
				              }
				          }else{
				        	  log.warn("Cannot overwrite "+fullPath.getAbsolutePath()+" (overwrite is false), unable to unpack.");
				          }		
				    }        
					else{
						PRStream prs = (PRStream) PdfReader.getPdfObject(ef.get(PdfName.F));
						if (prs != null){
							byte b[] = PdfReader.getStreamBytes(prs);
							FileOutputStream fout = new FileOutputStream(fullPath);
							fout.write(b);
							fout.close();
							retVal = 1;
						}	
					}			
				}
			}
		}
		return retVal;
	}
	
	/**
	 * get an array of PdfFile in the input directory
	 * @param directory
	 * @return PdfFile array from the input directory
	 */
	private PdfFile[] getPdfFiles(File directory){
		File[] fileList = directory.listFiles(new PdfFilter());
		ArrayList list = new ArrayList();
		for (int i=0; i<fileList.length; i++){
			list.add(new PdfFile(fileList[i], null));
		}
		return (PdfFile[]) list.toArray(new PdfFile[list.size()]);
	}
}
