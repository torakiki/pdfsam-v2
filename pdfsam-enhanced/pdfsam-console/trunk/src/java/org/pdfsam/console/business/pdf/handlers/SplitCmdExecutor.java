/*
 * Created on 22-Oct-2007
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
package org.pdfsam.console.business.pdf.handlers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.SplitException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.PrefixParser;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * Command executor for the split command
 * @author Andrea Vacondio
 */
public class SplitCmdExecutor extends AbstractCmdExecutor {

	private final Logger log = Logger.getLogger(SplitCmdExecutor.class.getPackage().getName());
	private PrefixParser prefixParser;
	
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		
		if((parsedCommand != null) && (parsedCommand instanceof SplitParsedCommand)){
			SplitParsedCommand inputCommand = (SplitParsedCommand) parsedCommand;
			setPercentageOfWorkDone(0);
			try{
				prefixParser = new PrefixParser(inputCommand.getOutputFilesPrefix(), inputCommand.getInputFile().getFile().getName());
				if(SplitParsedCommand.S_BURST.equals(inputCommand.getSplitType())){
					executeBurst(inputCommand);
				}else if(SplitParsedCommand.S_NSPLIT.equals(inputCommand.getSplitType())){
					executeNSplit(inputCommand);
				}else if(SplitParsedCommand.S_SPLIT.equals(inputCommand.getSplitType())){
					executeSplit(inputCommand);
				}else if(SplitParsedCommand.S_EVEN.equals(inputCommand.getSplitType()) || SplitParsedCommand.S_ODD.equals(inputCommand.getSplitType())){
					executeSplitOddEven(inputCommand);
				}else if(SplitParsedCommand.S_SIZE.equals(inputCommand.getSplitType())){
					executeSizeSplit(inputCommand);
				}else{
					throw new SplitException(SplitException.ERR_NOT_VALID_SPLIT_TYPE, new String[]{inputCommand.getSplitType()});
				}
			}catch(Exception e){    		
				throw new SplitException(e);
			}finally{
				setWorkCompleted();
			}
		}else{
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}
	}
	

	/**
	 *  Execute the split of a pdf document when split type is S_BURST
	 * @param inputCommand
	 * @throws Exception
	 */
    private void executeBurst(SplitParsedCommand inputCommand) throws Exception{
        int currentPage;
        Document currentDocument;
        PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
        //we retrieve the total number of pages
        int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        log.info("Found "+n+" pages in input pdf document.");
        for (currentPage = 1; currentPage <= n; currentPage++) {
			log.debug("Creating a new document.");
			fileNum++;
        	File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
        	File outFile = new File(inputCommand.getOutputFile().getCanonicalPath(),prefixParser.generateFileName(new Integer(currentPage), new Integer(fileNum)));
        	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
        	
            PdfSmartCopy pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));

        	//set creator
        	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);

            //set compressed
            if(inputCommand.isCompress()){
            	pdfWriter.setFullCompression();
            }    
            
            //set pdf version
            Character pdfVersion = inputCommand.getOutputPdfVersion(); 
			if(pdfVersion != null){
				pdfWriter.setPdfVersion(pdfVersion.charValue());
			}else{
				pdfWriter.setPdfVersion(pdfReader.getPdfVersion());
			}

			currentDocument.open();
            PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            currentDocument.close();
            if(FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite())){
            	log.debug("File "+outFile.getCanonicalPath()+" created.");
            }
            setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
        }
        pdfReader.close();
        log.info("Burst done.");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_ODD or S_EVEN
     * @param inputCommand
     * @throws Exception
     */
    private void executeSplitOddEven(SplitParsedCommand inputCommand) throws Exception{
         PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
         //we retrieve the total number of pages
         int n = pdfReader.getNumberOfPages();
         int fileNum = 0;
         log.info("Found "+n+" pages in input pdf document.");
         int currentPage;
         Document currentDocument = new Document(pdfReader.getPageSizeWithRotation(1));
         boolean isTimeToClose = false;
         PdfSmartCopy pdfWriter = null;
         PdfImportedPage importedPage;
         File tmpFile = null;
         File outFile = null;
         for (currentPage = 1; currentPage <= n; currentPage++) {
            //check if i've to read one more page or to open a new doc
        	 isTimeToClose = 
        		 (
        			 (currentPage!=1) && 
        			 (
        					 (SplitParsedCommand.S_ODD.equals(inputCommand.getSplitType()) && ((currentPage %2) != 0)) 
        					 ||
        					 (SplitParsedCommand.S_EVEN.equals(inputCommand.getSplitType()) && ((currentPage %2) == 0))
        			 )
                 );
            
           
            if (!isTimeToClose){
				log.debug("Creating a new document.");
				fileNum++;
            	tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
            	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(new Integer(currentPage), new Integer(fileNum)));
            	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));

            	pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));
            	//set creator
            	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);

            	//set compressed
                if(inputCommand.isCompress()){
                	pdfWriter.setFullCompression();
                }
                
                //set pdf version
                Character pdfVersion = inputCommand.getOutputPdfVersion(); 
    			if(pdfVersion != null){
    				pdfWriter.setPdfVersion(pdfVersion.charValue());
    			}else{
    				pdfWriter.setPdfVersion(pdfReader.getPdfVersion());
    			}
                currentDocument.open();
            }
            importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            //if it's time to close the document
            if ((isTimeToClose) || (currentPage == n) || ((currentPage==1) && (SplitParsedCommand.S_ODD.equals(inputCommand.getSplitType())))){
                currentDocument.close();
                if(FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite())){
                	log.debug("File "+outFile.getCanonicalPath()+" created.");
                }
            }
            setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
        }
        pdfReader.close();
        log.info("Split "+inputCommand.getSplitType()+" done.");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_SPLIT
     * @param inputCommand
     * @throws Exception
     */
    private void executeSplit(SplitParsedCommand inputCommand) throws Exception{
        PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
        int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        log.info("Found "+n+" pages in input pdf document.");
        
        Integer[] limits = inputCommand.getSplitPageNumbers();
		// limits list validation end clean
		TreeSet limitsList = validateSplitLimits(limits, n);
		if (limitsList.isEmpty()) {
			throw new SplitException(SplitException.ERR_NO_PAGE_LIMITS);
		}
		
		// HERE I'M SURE I'VE A LIMIT LIST WITH VALUES, I CAN START BOOKMARKS		
		int currentPage;        
		Document currentDocument = new Document(pdfReader.getPageSizeWithRotation(1));
		int relativeCurrentPage = 0;
		int endPage = n;
		int startPage = 1;
        PdfSmartCopy pdfWriter = null;
        PdfImportedPage importedPage;
        File tmpFile = null;
        File outFile = null;

        Iterator itr = limitsList.iterator();
	    if (itr.hasNext()){
	    	endPage = ((Integer)itr.next()).intValue();
	    }
		for (currentPage = 1; currentPage <= n; currentPage++) {
			relativeCurrentPage++;
            //check if i've to read one more page or to open a new doc
            if (relativeCurrentPage == 1){            	
				log.debug("Creating a new document.");
            	fileNum++;
				tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());            	
            	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(new Integer(currentPage), new Integer(fileNum)));
            	startPage = currentPage;
            	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
                
            	pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));
            	
            	//set creator
            	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
                
            	//compression
            	if(inputCommand.isCompress()){
                	pdfWriter.setFullCompression();
                }
            	
            	//set pdf version
                Character pdfVersion = inputCommand.getOutputPdfVersion(); 
    			if(pdfVersion != null){
    				pdfWriter.setPdfVersion(pdfVersion.charValue());
    			}else{
    				pdfWriter.setPdfVersion(pdfReader.getPdfVersion());
    			}
                currentDocument.open();           	 
            }
            
            importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            
			// if it's time to close the document
			if (currentPage == endPage) {
                log.info("Temporary document "+tmpFile.getName()+" done, now adding bookmarks...");
                //manage bookmarks
                ArrayList master = new ArrayList();
                List thisBook = SimpleBookmark.getBookmark(pdfReader);
                if (thisBook != null) {
                   SimpleBookmark.eliminatePages(thisBook, new int[]{endPage+1, n});
                   if (startPage > 1){
                       SimpleBookmark.eliminatePages(thisBook, new int[]{1,startPage-1});
                       SimpleBookmark.shiftPageNumbers(thisBook, -(startPage-1), null);
                   }
                   master.addAll(thisBook);
                   pdfWriter.setOutlines(master);
                }
                relativeCurrentPage = 0;                    
                currentDocument.close();
                if(FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite())){
                	log.debug("File "+outFile.getCanonicalPath()+" created.");
                }
                endPage = (itr.hasNext())? ((Integer)itr.next()).intValue(): n;
			}
			setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
		}
		pdfReader.close();
		log.info("Split "+inputCommand.getSplitType()+" done.");
    }
    
  /**
   *  Execute the split of a pdf document when split type is S_NSPLIT
   * @param inputCommand
   * @throws Exception
   */
	private void executeNSplit(SplitParsedCommand inputCommand) throws Exception {
		Integer[] numberPages = inputCommand.getSplitPageNumbers();
		if(numberPages != null && numberPages.length == 1){
			PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
	        int n = pdfReader.getNumberOfPages();
			int numberPage = numberPages[0].intValue();
			if (numberPage < 1 || numberPage > n) {
				throw new SplitException(SplitException.ERR_NO_SUCH_PAGE, new String[]{""+numberPage});
			}else{
				ArrayList retVal = new ArrayList();
				for (int i = numberPage; i < n; i += numberPage) {
					retVal.add(new Integer(i));
				}
				inputCommand.setSplitPageNumbers((Integer[])retVal.toArray(new Integer[0]));
			}
			pdfReader.close();
		}		
		executeSplit(inputCommand);
	}
	
	/**
   *  Execute the split of a pdf document when split type is S_SIZE
   * @param inputCommand
   * @throws Exception
   */
	private void executeSizeSplit(SplitParsedCommand inputCommand) throws Exception{
        PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
        int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        log.info("Found "+n+" pages in input pdf document.");
        int currentPage;        
		Document currentDocument = new Document(pdfReader.getPageSizeWithRotation(1));
        PdfSmartCopy pdfWriter = null;
        PdfImportedPage importedPage;
        File tmpFile = null;
        File outFile = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int startPage = 0;
        int relativeCurrentPage = 0;
		for (currentPage = 1; currentPage <= n; currentPage++) {
			relativeCurrentPage++;
			//time to open a new document?
			if (relativeCurrentPage == 1){
				log.debug("Creating a new document.");
				startPage = currentPage;
				fileNum++;
				tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
	        	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(new Integer(currentPage), new Integer(fileNum)));
	        	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
	        	baos = new ByteArrayOutputStream(); 
	        	pdfWriter = new PdfSmartCopy(currentDocument, baos);
	        	//set creator
	        	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
	            
	        	//compression
	        	if(inputCommand.isCompress()){
	            	pdfWriter.setFullCompression();
	            }
	        	
	        	//set pdf version
	            Character pdfVersion = inputCommand.getOutputPdfVersion(); 
				if(pdfVersion != null){
					pdfWriter.setPdfVersion(pdfVersion.charValue());
				}else{
					pdfWriter.setPdfVersion(pdfReader.getPdfVersion());
				}
	            currentDocument.open();  
			}
			
			importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);            
            //if it's time to close the document
			if ((currentPage == n) || ((relativeCurrentPage>1) && ((baos.size()/relativeCurrentPage)*(1+relativeCurrentPage) > inputCommand.getSplitSize().longValue()))){
				log.debug("Current stream size: "+baos.size()+" bytes.");
                //manage bookmarks
                ArrayList master = new ArrayList();
                List thisBook = SimpleBookmark.getBookmark(pdfReader);
                if (thisBook != null) {
                   SimpleBookmark.eliminatePages(thisBook, new int[]{currentPage+1, n});
                   if (startPage > 1){
                       SimpleBookmark.eliminatePages(thisBook, new int[]{1,startPage-1});
                       SimpleBookmark.shiftPageNumbers(thisBook, -(startPage-1), null);
                   }
                   master.addAll(thisBook);
                   pdfWriter.setOutlines(master);
                }
                relativeCurrentPage = 0; 
                currentDocument.close();
				FileOutputStream fos = new FileOutputStream(tmpFile);
				baos.writeTo(fos);
				fos.close();
				baos.close();				
                log.info("Temporary document "+tmpFile.getName()+" done.");
                if(FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite())){
                	log.debug("File "+outFile.getCanonicalPath()+" created.");
                }
			}
			setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
		}
		pdfReader.close();  
		log.info("Split "+inputCommand.getSplitType()+" done.");
	}

    /**
	 * Validate the limit list in "split after these pages"
	 * 
	 * @param limits limits array containing page numbers
	 * @param upperLimit max page number
	 * @return purged list
	 */
	private TreeSet validateSplitLimits(Integer[] limits, int upperLimit) {
		TreeSet limitsList = new TreeSet();
		/*
		 * I check if it contains zero values or double values and
		 * i remove them
		 */
		for (int j = 0; j < limits.length; j++) {
			if ((limits[j].intValue() <= 0) || (limits[j].intValue() >= upperLimit)) {
				log.warn("Cannot split before page number " + limits[j].intValue()+ ", limit removed.");
			} else{
					if(!limitsList.add(limits[j])){
						log.warn(limits[j].intValue()+ " found more than once in the page limits list, limit removed.");
					}				
			}
		}
		return limitsList;
	}
}
