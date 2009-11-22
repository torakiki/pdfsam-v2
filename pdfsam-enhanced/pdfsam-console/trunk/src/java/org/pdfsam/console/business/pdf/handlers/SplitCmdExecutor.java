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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.SplitException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.PdfUtility;
import org.pdfsam.console.utils.perfix.FileNameRequest;
import org.pdfsam.console.utils.perfix.PrefixParser;
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

	private static final Logger LOG = Logger.getLogger(SplitCmdExecutor.class.getPackage().getName());
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
				}else if(SplitParsedCommand.S_BLEVEL.equals(inputCommand.getSplitType())){
					executeBookmarksSplit(inputCommand);
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
		pdfReader.removeUnusedObjects();
        pdfReader.consolidateNamedDestinations();
		
		//we retrieve the total number of pages
        int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        LOG.info("Found "+n+" pages in input pdf document.");
        for (currentPage = 1; currentPage <= n; currentPage++) {
			LOG.debug("Creating a new document.");
			fileNum++;
        	File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
        	FileNameRequest request = new FileNameRequest(currentPage, fileNum, null);
        	File outFile = new File(inputCommand.getOutputFile().getCanonicalPath(),prefixParser.generateFileName(request));
        	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
        	
            PdfSmartCopy pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));

        	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
			setCompressionSettingOnWriter(inputCommand, pdfWriter);	  
            
            //set pdf version
			setPdfVersionSettingOnWriter(inputCommand, pdfWriter, Character.valueOf(pdfReader.getPdfVersion()));

			currentDocument.open();
            PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            currentDocument.close();
            FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
           	LOG.debug("File "+outFile.getCanonicalPath()+" created.");
            setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
        }
        pdfReader.close();
        LOG.info("Burst done.");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_ODD or S_EVEN
     * @param inputCommand
     * @throws Exception
     */
    private void executeSplitOddEven(SplitParsedCommand inputCommand) throws Exception{
         PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
		 pdfReader.removeUnusedObjects();
         pdfReader.consolidateNamedDestinations();
		
		 //we retrieve the total number of pages
         int n = pdfReader.getNumberOfPages();
         int fileNum = 0;
         LOG.info("Found "+n+" pages in input pdf document.");
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
				LOG.debug("Creating a new document.");
				fileNum++;
            	tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
            	FileNameRequest request = new FileNameRequest(currentPage, fileNum, null);
            	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(request));
            	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));

            	pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));
            	//set creator
            	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);

				setCompressionSettingOnWriter(inputCommand, pdfWriter);	
				setPdfVersionSettingOnWriter(inputCommand, pdfWriter, Character.valueOf(pdfReader.getPdfVersion()));

                currentDocument.open();
            }
            importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            //if it's time to close the document
            if ((isTimeToClose) || (currentPage == n) || ((currentPage==1) && (SplitParsedCommand.S_ODD.equals(inputCommand.getSplitType())))){
                currentDocument.close();
                FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
                LOG.debug("File "+outFile.getCanonicalPath()+" created.");
            }
            setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
        }
        pdfReader.close();
        LOG.info("Split "+inputCommand.getSplitType()+" done.");
    }
    
    /**
     * Execute the split of a pdf document when split type is S_SPLIT or S_NSPLIT
     * @param inputCommand
     * @throws Exception
     */
    private void executeSplit(SplitParsedCommand inputCommand) throws Exception{
    	executeSplit(inputCommand, null);
    }
    
    /**
     * Execute the split of a pdf document when split type is S_BLEVEL
     * @param inputCommand
     * @param bookmarksTable bookmarks table. It's populated only when splitting by bookmarks. If null or empty it's ignored
     * @throws Exception
     */
    private void executeSplit(SplitParsedCommand inputCommand, Hashtable bookmarksTable) throws Exception{
        PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
		pdfReader.removeUnusedObjects();
        pdfReader.consolidateNamedDestinations();
		
        int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        LOG.info("Found "+n+" pages in input pdf document.");
        
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
				LOG.debug("Creating a new document.");
            	fileNum++;
				tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile()); 
				String bookmark = null;
				if(bookmarksTable!=null && bookmarksTable.size()>0){
					bookmark = (String)bookmarksTable.get(new Integer(currentPage));
				}
				FileNameRequest request = new FileNameRequest(currentPage, fileNum, bookmark);
            	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(request));
            	startPage = currentPage;
            	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
                
            	pdfWriter = new PdfSmartCopy(currentDocument, new FileOutputStream(tmpFile));
            	
            	//set creator
            	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
                
				setCompressionSettingOnWriter(inputCommand, pdfWriter);	
				setPdfVersionSettingOnWriter(inputCommand, pdfWriter, Character.valueOf(pdfReader.getPdfVersion()));

                currentDocument.open();           	 
            }
            
            importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);
            
			// if it's time to close the document
			if (currentPage == endPage) {
                LOG.info("Temporary document "+tmpFile.getName()+" done, now adding bookmarks...");
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
                FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
                LOG.debug("File "+outFile.getCanonicalPath()+" created.");
                endPage = (itr.hasNext())? ((Integer)itr.next()).intValue(): n;
			}
			setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
		}
		pdfReader.close();
		LOG.info("Split "+inputCommand.getSplitType()+" done.");
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
			pdfReader.removeUnusedObjects();
			pdfReader.consolidateNamedDestinations();			
			int n = pdfReader.getNumberOfPages();
			int numberPage = numberPages[0].intValue();
			if (numberPage < 1 || numberPage > n) {
				pdfReader.close();
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
	 * Execute the split of a pdf document when split type is S_BLEVEL
	 * 
	 * @param inputCommand
	 * @throws Exception
	 */
	private void executeBookmarksSplit(SplitParsedCommand inputCommand) throws Exception {
		PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
		int bLevel = inputCommand.getBookmarksLevel().intValue();
		Hashtable bookmarksTable = new Hashtable();
		if(bLevel>0){
			pdfReader.removeUnusedObjects();
			pdfReader.consolidateNamedDestinations();			
			List bookmarks = SimpleBookmark.getBookmark(pdfReader);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			SimpleBookmark.exportToXML(bookmarks, out, "UTF-8", false);
			ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());			
			int maxDepth = PdfUtility.getMaxBookmarksDepth(input);
			input.reset();
			if(bLevel<=maxDepth){
				SAXReader reader = new SAXReader();
				org.dom4j.Document document = reader.read(input);
				//head node
				String headBookmarkXQuery = "/Bookmark/Title[@Action=\"GoTo\"]";
				Node headNode = document.selectSingleNode(headBookmarkXQuery);
				if(headNode!=null && headNode.getText()!=null && headNode.getText().trim().length()>0){
					bookmarksTable.put(new Integer(1), headNode.getText().trim());
				}
				//bLevel nodes
				StringBuffer buffer = new StringBuffer("/Bookmark");
				for(int i=0; i<bLevel; i++){
					buffer.append("/Title[@Action=\"GoTo\"]");
				}
				String xQuery = buffer.toString();
				List nodes = document.selectNodes(xQuery);
				input.close();
				input = null;
				if(nodes!=null && nodes.size()>0){
					LinkedHashSet pageSet = new LinkedHashSet(nodes.size());
					for(Iterator nodeIter = nodes.iterator(); nodeIter.hasNext();){
						Node currentNode = (Node) nodeIter.next();
						Node pageAttribute = currentNode.selectSingleNode("@Page");
						if(pageAttribute!=null && pageAttribute.getText().length()>0){
							String attribute = pageAttribute.getText();
							int blankIndex = attribute.indexOf(' ');
							if(blankIndex>0){
								Integer currentNumber = new Integer(attribute.substring(0, blankIndex));
								//fix #2789963
								if(currentNumber.intValue()>0){
									//to split just before the given page
									if((currentNumber.intValue())>1){
										pageSet.add(new Integer(currentNumber.intValue()-1));										
									}
									String bookmarkText = currentNode.getText();
									if(bookmarkText!=null && bookmarkText.trim().length()>0){
										bookmarksTable.put(currentNumber, bookmarkText.trim());
									}
								}								
							}
						}
					}
					if(pageSet.size()>0){
						LOG.debug("Found "+pageSet.size()+" destination pages at level "+bLevel);
						inputCommand.setSplitPageNumbers((Integer[])pageSet.toArray(new Integer[pageSet.size()]));
					}else{
						throw new SplitException(SplitException.ERR_BLEVEL_NO_DEST, new String[] { "" + bLevel });	
					}
				}else{
					throw new SplitException(SplitException.ERR_BLEVEL, new String[] { "" + bLevel });	
				}
			}else{
				input.close();
				pdfReader.close();
				throw new SplitException(SplitException.ERR_BLEVEL_OUTOFBOUNDS, new String[] { "" + bLevel, "" + maxDepth });
				
			}
		}else{
			pdfReader.close();
			throw new SplitException(SplitException.ERR_NOT_VALID_BLEVEL, new String[] { "" + bLevel });				
		}	
		pdfReader.close();
		executeSplit(inputCommand, bookmarksTable);
	}
	
	/**
   *  Execute the split of a pdf document when split type is S_SIZE
   * @param inputCommand
   * @throws Exception
   */
	private void executeSizeSplit(SplitParsedCommand inputCommand) throws Exception{
        PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()),inputCommand.getInputFile().getPasswordBytes());
		pdfReader.removeUnusedObjects();
        pdfReader.consolidateNamedDestinations();
		int n = pdfReader.getNumberOfPages();
        int fileNum = 0;
        LOG.info("Found "+n+" pages in input pdf document.");
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
				LOG.debug("Creating a new document.");
				startPage = currentPage;
				fileNum++;
				tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
				FileNameRequest request = new FileNameRequest(currentPage, fileNum, null);
	        	outFile = new File(inputCommand.getOutputFile(),prefixParser.generateFileName(request));
	        	currentDocument = new Document(pdfReader.getPageSizeWithRotation(currentPage));
	        	baos = new ByteArrayOutputStream(); 
	        	pdfWriter = new PdfSmartCopy(currentDocument, baos);
	        	//set creator
	        	currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
	            
				setCompressionSettingOnWriter(inputCommand, pdfWriter);	
				setPdfVersionSettingOnWriter(inputCommand, pdfWriter, Character.valueOf(pdfReader.getPdfVersion()));

	            currentDocument.open();  
			}
			
			importedPage = pdfWriter.getImportedPage(pdfReader, currentPage);
            pdfWriter.addPage(importedPage);            
            //if it's time to close the document
			if ((currentPage == n) || ((relativeCurrentPage>1) && ((baos.size()/relativeCurrentPage)*(1+relativeCurrentPage) > inputCommand.getSplitSize().longValue()))){
				LOG.debug("Current stream size: "+baos.size()+" bytes.");
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
                LOG.info("Temporary document "+tmpFile.getName()+" done.");
                FileUtility.renameTemporaryFile(tmpFile, outFile, inputCommand.isOverwrite());
               	LOG.debug("File "+outFile.getCanonicalPath()+" created.");
			}
			setPercentageOfWorkDone((currentPage*WorkDoneDataModel.MAX_PERGENTAGE)/n);
		}
		pdfReader.close();  
		LOG.info("Split "+inputCommand.getSplitType()+" done.");
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
				LOG.warn("Cannot split before page number " + limits[j].intValue()+ ", limit removed.");
			} else{
					if(!limitsList.add(limits[j])){
						LOG.warn(limits[j].intValue()+ " found more than once in the page limits list, limit removed.");
					}				
			}
		}
		return limitsList;
	}
}
