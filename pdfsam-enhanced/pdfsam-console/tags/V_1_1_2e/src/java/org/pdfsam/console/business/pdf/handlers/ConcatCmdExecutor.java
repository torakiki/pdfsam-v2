/*
 * Created on 28-Oct-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.PdfFile;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.business.pdf.writers.PdfCopyFieldsConcatenator;
import org.pdfsam.console.business.pdf.writers.PdfSimpleConcatenator;
import org.pdfsam.console.business.pdf.writers.interfaces.PdfConcatenator;
import org.pdfsam.console.exceptions.console.ConcatException;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.utils.FileUtility;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * Command executor for the alternate concat command
 * @author Andrea Vacondio
 */
public class ConcatCmdExecutor extends AbstractCmdExecutor {
	
	private final Logger log = Logger.getLogger(ConcatCmdExecutor.class.getPackage().getName());
	
	private final static String ALL_STRING = "all"; 
	
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		if((parsedCommand != null) && (parsedCommand instanceof ConcatParsedCommand)){
			ConcatParsedCommand inputCommand = (ConcatParsedCommand) parsedCommand;
			setPercentageOfWorkDone(0);
			//xml or csv parsing
			PdfFile[] fileList = inputCommand.getInputFileList();
			if (fileList== null || !(fileList.length >0)){
				File listFile = inputCommand.getInputCvsOrXmlFile();
				if(listFile != null && listFile.exists()){
					fileList = parseListFile(listFile);
				}                               
	        }
			//init
            int pageOffset = 0;
            ArrayList master = new ArrayList();
            Document pdfDocument = null;
            PdfConcatenator  pdfWriter = null;
            int totalProcessedPages = 0;        
            PdfReader pdfReader;
            
			try{
	            String[] pageSelection = inputCommand.getPageSelection().split(":");
				File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
				
				for(int i=0; i<fileList.length; i++){
	    			//get page selection. If arrayoutofbounds default behaviour is "all" 
					String currentPageSelection;
	    			try{
	    				currentPageSelection = ("".equals(pageSelection[i].toLowerCase()))? ALL_STRING: pageSelection[i].toLowerCase();
	    			}catch(Exception e){
	    				currentPageSelection = ALL_STRING;
	    			}
	    			
	    			//validation
	    			if (!(Pattern.compile("([0-9]+[-][0-9]*)|("+ALL_STRING+")", Pattern.CASE_INSENSITIVE).matcher(currentPageSelection).matches())){
	    				FileUtility.deleteFile(tmpFile);
						throw new ConcatException(ConcatException.ERR_SYNTAX, new String[]{""+currentPageSelection});
					} 
	    			
	    			pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),fileList[i].getPasswordBytes());
					pdfReader.consolidateNamedDestinations();
					int pdfNumberOfPages = pdfReader.getNumberOfPages();
					//default behaviour
	    			int start = 1;
	    			int endPage =  pdfNumberOfPages;
	    			
	    			if (!(ALL_STRING.equals(currentPageSelection))){
		            	boolean valid = true;
		                String[] limits = currentPageSelection.split("-");
		                try{
		                    start = Integer.parseInt(limits[0]);
		                    //if there's and end limit
		                    if(limits.length > 1){
		                    	endPage = Integer.parseInt(limits[1]);
		                    }else{
		                    	endPage = pdfNumberOfPages;
		                    }
		                }catch(NumberFormatException nfe){
							valid = false;
							FileUtility.deleteFile(tmpFile);
							throw new ConcatException(ConcatException.ERR_SYNTAX, new String[]{""+currentPageSelection},nfe);							
		                }
		                if(valid){
			                //validation
			                if (start <= 0){
								valid = false;
								FileUtility.deleteFile(tmpFile);
								throw new ConcatException(ConcatException.ERR_NOT_POSITIVE, new String[]{""+start, ""+currentPageSelection});
			                }
			                else if (endPage > pdfNumberOfPages){
			                	valid = false;
								FileUtility.deleteFile(tmpFile);
								throw new ConcatException(ConcatException.ERR_CANNOT_MERGE, new String[]{""+endPage});
			                }
			                else if (start > endPage){
			                	valid = false;
								FileUtility.deleteFile(tmpFile);
								throw new ConcatException(ConcatException.ERR_START_BIGGER_THAN_END, new String[]{""+start,""+endPage,""+currentPageSelection});
			                }
		                }
	    			}
	    			//bookmarks
	    			List bookmarks = SimpleBookmark.getBookmark(pdfReader);
	    			if (bookmarks != null) {
	    				//if the end page is not the end of the doc, delete bookmarks after it
	    				if (endPage < pdfNumberOfPages){
	    					SimpleBookmark.eliminatePages(bookmarks, new int[]{endPage+1, pdfNumberOfPages});
	    				}
	    				// if start page isn't the first page of the document, delete bookmarks before it
	    				if (start > 1){
	    					SimpleBookmark.eliminatePages(bookmarks, new int[]{1,start-1});
	    					//bookmarks references must be taken back
	    					SimpleBookmark.shiftPageNumbers(bookmarks, -(start-1), null);
	    				}
	    				if (pageOffset != 0){
	    					SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
	    				}
	    				master.addAll(bookmarks);
	    			}
	    			int relativeOffset = (endPage - start)+1;
	    			pageOffset += relativeOffset;
	    			log.info(fileList[i].getFile().getAbsolutePath()+ ": " + relativeOffset + " pages to be added.");
	    			if (i == 0) {
	                    if(inputCommand.isCopyFields()){
	                        // step 1: we create a writer 
	                    	pdfWriter = new PdfCopyFieldsConcatenator(new FileOutputStream(tmpFile), inputCommand.isCompress());
	                    	log.debug("PdfCopyFieldsConcatenator created.");
	        				//output document version
	        				if(inputCommand.getOutputPdfVersion() != null){
	        					pdfWriter.setPdfVersion(inputCommand.getOutputPdfVersion().charValue());
	        				}
	                    	HashMap meta = pdfReader.getInfo();
	                    	meta.put("Creator", ConsoleServicesFacade.CREATOR);
	                    }else{
	                        // step 1: creation of a document-object
	                        pdfDocument = new Document(pdfReader.getPageSizeWithRotation(1));
	                        // step 2: we create a writer that listens to the document
	                        pdfWriter = new PdfSimpleConcatenator(pdfDocument, new FileOutputStream(tmpFile), inputCommand.isCompress());
	                    	log.debug("PdfSimpleConcatenator created.");
	        				//output document version
	                        if(inputCommand.getOutputPdfVersion() != null){
	        					pdfWriter.setPdfVersion(inputCommand.getOutputPdfVersion().charValue());
	        				}
	                        // step 3: we open the document
	                        pdfDocument.addCreator(ConsoleServicesFacade.CREATOR);
	                        pdfDocument.open();
	                    }
	                }
	    			// step 4: we add content
	    			pdfReader.selectPages(start+"-"+endPage);
	    			pdfWriter.addDocument(pdfReader); 
	    			//fix 03/07
	    			//pdfReader = null;
	    	        pdfReader.close();
	    	        pdfWriter.freeReader(pdfReader);
	    	        totalProcessedPages += relativeOffset;
	    			log.info(relativeOffset + " pages processed correctly.");
    				setPercentageOfWorkDone(((i+1)*WorkDoneDataModel.MAX_PERGENTAGE)/fileList.length);		    		
				}
				if (master.size() > 0){
	    			pdfWriter.setOutlines(master);
	    		}
	    		log.info("Total processed pages: " + totalProcessedPages + ".");
	    		// step 5: we close the document
	    		if(pdfDocument != null){
	    			pdfDocument.close();        	
	            }
	    		pdfWriter.close();
	    		if(FileUtility.renameTemporaryFile(tmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite())){
                	log.debug("File "+inputCommand.getOutputFile().getCanonicalPath()+" created.");
                }  		
			}catch(Exception e){    		
				throw new ConcatException(e);
			}finally{
				setWorkCompleted();
			}
		}else{
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}
	
	}
	
	   /**
     * Reads the input cvs file and return a File[] of input files
     * @param inputFile CSV input file (separator ",")
     * @return PdfFile[] of files 
     */
    private PdfFile[] parseCsvFile(File inputFile)throws ConcatException{
        ArrayList retVal = new ArrayList();
        try {
            BufferedReader bufferReader = new BufferedReader(new FileReader(inputFile));
            String temp = "";
            //read file
            while ((temp = bufferReader.readLine()) != null){
                String[] tmpContent = temp.split(",");
                for (int i = 0; i<tmpContent.length; i++){
                	if(tmpContent[i].trim().length() > 0){
                		retVal.add(new PdfFile(tmpContent[i], null));
                	}
                }
            }
            bufferReader.close();
          }
       catch (IOException e) {
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,e);
       }
       return (PdfFile[])retVal.toArray(new PdfFile[0]);            
   }
   
    /**
     * Reads the input xml file and return a File[] of input files
     * @param inputFile XML input file 
     * @return PdfFile[] of files
     */
    private PdfFile[] parseXmlFile(File inputFile)throws ConcatException{
		List fileList = new ArrayList();
        try {
			SAXReader reader = new SAXReader();
			org.dom4j.Document document = reader.read(inputFile);
            List pdfFileList = document.selectNodes("/filelist/file");
			for (int i = 0; pdfFileList != null && i < pdfFileList.size(); i++) {
				Node pdfNode = (Node) pdfFileList.get(i);
				String pwd = null;
				String fileName = null;
				//get filename
				Node fileNode = pdfNode.selectSingleNode("@value");
				if(fileNode != null){
					fileName = fileNode.getText().trim();
				}else{
					throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[]{"Empty file name"});
				}
				//get pwd value
				Node pwdNode = pdfNode.selectSingleNode("@password");
				if(pwdNode != null){
					pwd = pwdNode.getText();
				}
				fileList.add(new PdfFile(fileName, pwd));
			}
        }catch (Exception e) {
        	throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,e);
       }
        return (PdfFile[])fileList.toArray(new PdfFile[0]);
   }
    
    /**
     * Reads the input file and return a File[] of input files
     * @param inputFile XML or CSV input file 
     * @return File[] of files
     */
    private PdfFile[] parseListFile(File listFile) throws ConcatException{
    	PdfFile[] retVal = new PdfFile[0];
    	if(listFile != null && listFile.exists()){
    		if (getExtension(listFile).equals("xml")){
    			retVal = parseXmlFile(listFile);
			}else if (getExtension(listFile).equals("csv")){
				retVal = parseCsvFile(listFile);
			}else {
				throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML);
			}
    	}else{
    		throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML);
    	}
    	return retVal;
    }
    
    /**
     * get the exctension of the input file
     * @param f
     * @return
     */
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

}
