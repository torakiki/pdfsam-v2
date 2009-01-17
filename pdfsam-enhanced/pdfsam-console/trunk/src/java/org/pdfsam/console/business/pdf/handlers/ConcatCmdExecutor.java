/*
 * Created on 28-Oct-2007
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.PageRotation;
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
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * Command executor for the alternate concat command
 * @author Andrea Vacondio
 */
public class ConcatCmdExecutor extends AbstractCmdExecutor {
	
	private final Logger log = Logger.getLogger(ConcatCmdExecutor.class.getPackage().getName());
	
	private final static String ALL_STRING = "all"; 
	
	private final static String FILESET_NODE = "fileset";
	private final static String FILE_NODE = "file";
	
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
	    			//i'm sooo bad in regexp
	    			if (!(Pattern.compile("((([\\d]+[-][\\d]*)|([\\d]+))(,(([\\d]+[-][\\d]*)|([\\d]+)))*)|("+ALL_STRING+")", Pattern.CASE_INSENSITIVE).matcher(currentPageSelection).matches())){
	    				FileUtility.deleteFile(tmpFile);
						throw new ConcatException(ConcatException.ERR_SYNTAX, new String[]{""+currentPageSelection});
					} 
	    			
	    			String[] selectionGroups = null;
	    			if(currentPageSelection.indexOf(",") != 0){
	    				selectionGroups = currentPageSelection.split(",");
	    			}else{
	    				selectionGroups = new String[]{currentPageSelection};
	    			}
	    			//i repeat the same file for every group matched on the selection string 
	    			for(int j=0; j<selectionGroups.length; j++){
		    			pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),fileList[i].getPasswordBytes());
						pdfReader.consolidateNamedDestinations();
						int pdfNumberOfPages = pdfReader.getNumberOfPages();
						Bounds bonuds;
		    			try{
		    				bonuds = getBounds(pdfNumberOfPages, selectionGroups[j]);
		    			}catch(ConcatException ce){
		    				FileUtility.deleteFile(tmpFile);
							throw new ConcatException(ce);
		    			}
		    			
		    			//bookmarks
		    			List bookmarks = SimpleBookmark.getBookmark(pdfReader);
		    			if (bookmarks != null) {
		    				//if the end page is not the end of the doc, delete bookmarks after it
		    				if (bonuds.getEnd() < pdfNumberOfPages){
		    					SimpleBookmark.eliminatePages(bookmarks, new int[]{bonuds.getEnd()+1, pdfNumberOfPages});
		    				}
		    				// if start page isn't the first page of the document, delete bookmarks before it
		    				if (bonuds.getStart() > 1){
		    					SimpleBookmark.eliminatePages(bookmarks, new int[]{1,bonuds.getStart()-1});
		    					//bookmarks references must be taken back
		    					SimpleBookmark.shiftPageNumbers(bookmarks, -(bonuds.getStart()-1), null);
		    				}
		    				if (pageOffset != 0){
		    					SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
		    				}
		    				master.addAll(bookmarks);
		    			}
		    			int relativeOffset = (bonuds.getEnd() - bonuds.getStart())+1;
		    			pageOffset += relativeOffset;
		    			log.info(fileList[i].getFile().getAbsolutePath()+ ": " + relativeOffset + " pages to be added.");
		    			if (pdfWriter == null) {
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
		    			pdfReader.selectPages(bonuds.getStart()+"-"+bonuds.getEnd());
		    			pdfWriter.addDocument(pdfReader); 
		    			//fix 03/07
		    			//pdfReader = null;
		    	        pdfReader.close();
		    	        pdfWriter.freeReader(pdfReader);
		    	        totalProcessedPages += relativeOffset;
		    			log.info(relativeOffset + " pages processed correctly.");
					}
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
	    		//rotations
	    		if(inputCommand.getRotations()!=null && inputCommand.getRotations().length>0){
	    			log.info("Applying pages rotation.");
	    			File rotatedTmpFile = applyRotations(tmpFile, inputCommand);
    				FileUtility.deleteFile(tmpFile);
	    			if(FileUtility.renameTemporaryFile(rotatedTmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite())){
	                	log.debug("File "+inputCommand.getOutputFile().getCanonicalPath()+" created.");
	                }  	
	    		}else if(FileUtility.renameTemporaryFile(tmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite())){
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
	 * Apply pages rotations
	 * @param inputFile
	 * @param inputCommand
	 * @return temporary file with pages rotation
	 */
	private File applyRotations(File inputFile, ConcatParsedCommand inputCommand) throws Exception{
		FileInputStream readerIs = new FileInputStream(inputFile);
		PdfReader tmpPdfReader = new PdfReader(readerIs);
		tmpPdfReader.consolidateNamedDestinations();
		int pdfNumberOfPages = tmpPdfReader.getNumberOfPages();
		PageRotation[] rotations = inputCommand.getRotations();
		if(rotations!=null && rotations.length>0){
			if(rotations.length>1){
				for(int i=0; i<rotations.length; i++){
					if(pdfNumberOfPages>=rotations[i].getPageNumber() && rotations[i].getPageNumber()>0){
						PdfDictionary dictionary = tmpPdfReader.getPageN(rotations[i].getPageNumber());
						dictionary.put(PdfName.ROTATE, new PdfNumber(tmpPdfReader.getPageRotation(rotations[i].getPageNumber()) + rotations[i].getDegrees()));
					}else{
						log.warn("Rotation for page "+rotations[i].getPageNumber()+" ignored.");
					}
				}
			}else{
				//rotate all
				if(rotations[0].getType() == PageRotation.ALL_PAGES){
					for(int i=1; i<=pdfNumberOfPages; i++){
						PdfDictionary dictionary = tmpPdfReader.getPageN(i);
						dictionary.put(PdfName.ROTATE, new PdfNumber(tmpPdfReader.getPageRotation(i) + rotations[0].getDegrees()));				
					}
				}else if(rotations[0].getType() == PageRotation.SINGLE_PAGE){
					//single page rotation
					if(pdfNumberOfPages>=rotations[0].getPageNumber() && rotations[0].getPageNumber()>0){
						PdfDictionary dictionary = tmpPdfReader.getPageN(rotations[0].getPageNumber());
						dictionary.put(PdfName.ROTATE, new PdfNumber(tmpPdfReader.getPageRotation(rotations[0].getPageNumber()) + rotations[0].getDegrees()));
					}else{
						log.warn("Rotation for page "+rotations[0].getPageNumber()+" ignored.");
					}
				}else if(rotations[0].getType() == PageRotation.ODD_PAGES){
					//odd pages rotation
					for(int i=1; i<=pdfNumberOfPages; i=i+2){
						PdfDictionary dictionary = tmpPdfReader.getPageN(i);
						dictionary.put(PdfName.ROTATE, new PdfNumber(tmpPdfReader.getPageRotation(i) + rotations[0].getDegrees()));				
					}
				}else if(rotations[0].getType() == PageRotation.ODD_PAGES){
					//even pages rotation
					for(int i=2; i<=pdfNumberOfPages; i=i+2){
						PdfDictionary dictionary = tmpPdfReader.getPageN(i);
						dictionary.put(PdfName.ROTATE, new PdfNumber(tmpPdfReader.getPageRotation(i) + rotations[0].getDegrees()));				
					}
				}else{
					log.warn("Unable to find the rotation type. "+rotations[0]);
				}
			}
			log.info("Pages rotation applied.");
		}
		File rotatedTmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
		FileOutputStream fos = new FileOutputStream(rotatedTmpFile);
		PdfStamper stamper = new PdfStamper(tmpPdfReader, fos);
		stamper.close();
		fos.close();
		readerIs.close();
		tmpPdfReader.close();
		return rotatedTmpFile;
	}
	
	private Bounds getBounds(int pdfNumberOfPages, String currentPageSelection) throws ConcatException{
		Bounds retVal = new Bounds(pdfNumberOfPages , 1);
		if (!(ALL_STRING.equals(currentPageSelection))){
        	boolean valid = true;
            String[] limits = currentPageSelection.split("-");
            try{
            	retVal.setStart(Integer.parseInt(limits[0]));
                //if there's an end limit
                if(limits.length > 1){
                	retVal.setEnd(Integer.parseInt(limits[1]));
                }else{
                	//difference between '4' and '4-'
                	if(currentPageSelection.indexOf('-')== -1){
                		retVal.setEnd(Integer.parseInt(limits[0]));
                	}else{
                		retVal.setEnd(pdfNumberOfPages);
                	}
                }
            }catch(NumberFormatException nfe){
				throw new ConcatException(ConcatException.ERR_SYNTAX, new String[]{""+currentPageSelection},nfe);							
            }
            if(valid){
                //validation
                if (retVal.getStart() <= 0){
					throw new ConcatException(ConcatException.ERR_NOT_POSITIVE, new String[]{""+retVal.getStart(), ""+currentPageSelection});
                }
                else if (retVal.getEnd() > pdfNumberOfPages){
					throw new ConcatException(ConcatException.ERR_CANNOT_MERGE, new String[]{""+retVal.getEnd()});
                }
                else if (retVal.getStart() > retVal.getEnd()){
					throw new ConcatException(ConcatException.ERR_START_BIGGER_THAN_END, new String[]{""+retVal.getStart(),""+retVal.getEnd(),""+currentPageSelection});
                }
            }
		}
		return retVal;
	}
	
	/**
     * Reads the input cvs file and return a File[] of input files
     * @param inputFile CSV input file (separator ",")
     * @return PdfFile[] of files 
     */
    private PdfFile[] parseCsvFile(File inputFile)throws ConcatException{
        ArrayList retVal = new ArrayList();
        try {
        	log.debug("Parsing CSV file "+inputFile.getAbsolutePath());
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
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,new String[]{inputFile.getAbsolutePath()}, e);
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
		String parentPath = null;
        try {
        	log.debug("Parsing xml file "+inputFile.getAbsolutePath());
			SAXReader reader = new SAXReader();
			org.dom4j.Document document = reader.read(inputFile);        
			List nodes = document.selectNodes("/filelist/*");
			parentPath = inputFile.getParent();
			for(Iterator iter= nodes.iterator(); iter.hasNext();){
				Node domNode = (Node) iter.next();
				String nodeName = domNode.getName();
				if(FILESET_NODE.equals(nodeName)){
					//found a fileset node
					fileList.addAll(getFileNodesFromFileset(domNode, parentPath));
				}else if (FILE_NODE.equals(nodeName)){
					fileList.add(getPdfFileFromNode(domNode, null));
				}else{
					log.warn("Node type not supported: "+nodeName);
				}
			}
        }catch (Exception e) {
        	throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,new String[]{inputFile.getAbsolutePath()}, e);
       }
        return (PdfFile[])fileList.toArray(new PdfFile[0]);
    }
    
    /**
     * given a fileset node returns the PdfFile objects
     * @param fileSetNode
     * @param parentDir
     * @return a list of PdfFile objects
     * @throws Exception
     */
    private List getFileNodesFromFileset(Node fileSetNode, String parentDir) throws Exception{
    	String parentPath=null;
    	Node useCurrentDir = fileSetNode.selectSingleNode("@usecurrentdir");
		Node dir = fileSetNode.selectSingleNode("@dir");
		if(dir != null && dir.getText().trim().length()>0){
			parentPath = dir.getText();
		}else{
			if(useCurrentDir!=null && Boolean.valueOf(useCurrentDir.getText()).booleanValue()){
				parentPath = parentDir;
			}
		}				
		return getPdfFileListFromNode(fileSetNode.selectNodes("file"), parentPath);
    }
    
    /**
     * @param fileList Node list of file nodes
     * @param parentPath parent dir for the files or null
     * @return list of PdfFile
     */
    private List getPdfFileListFromNode(List fileList, String parentPath) throws Exception{
    	List retVal = new ArrayList();
    	for (int i = 0; fileList != null && i < fileList.size(); i++) {
			Node pdfNode = (Node) fileList.get(i);
			retVal.add(getPdfFileFromNode(pdfNode, parentPath));			
		}
    	return retVal;
    }
    
    /**
     * @param pdfNode input node
     * @param parentPath file parent path or null
     * @return a PdfFile object given a file node
     * @throws Exception
     */
    private PdfFile getPdfFileFromNode(Node pdfNode, String parentPath) throws Exception{
    	PdfFile retVal = null;
    	String pwd = null;
		String fileName = null;
		//get filename
		Node fileNode = pdfNode.selectSingleNode("@value");
		if(fileNode != null){
			fileName = fileNode.getText().trim();
		}else{
			throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[]{"Empty file name."});
		}
		//get pwd value
		Node pwdNode = pdfNode.selectSingleNode("@password");
		if(pwdNode != null){
			pwd = pwdNode.getText();
		}
		if(parentPath!=null && parentPath.length()>0){
			retVal = new PdfFile(new File(parentPath,fileName), pwd);
		}else{
			retVal = new PdfFile(fileName, pwd);
		}
    	return retVal;
    }
    
    /**
     * Reads the input file and return a File[] of input files
     * @param listFile XML or CSV input file 
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
				throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[]{"Unsupported extension."});
			}
    	}else{
    		throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[]{"Input file doesn't exists."});
    	}
    	return retVal;
    }
    
    /**
     * get the extension of the input file
     * @param f
     * @return the extension
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
    
    /**
     * Maps the limit of the concat command, start and end
     * @author Andrea Vacondio
     *
     */
    private class Bounds{
    	
    	private int start;
    	private int end;

		public Bounds() {
		}
		/**
		 * @param end
		 * @param start
		 */
		public Bounds(int end, int start) {
			this.end = end;
			this.start = start;
		}
		/**
		 * @return the start
		 */
		public int getStart() {
			return start;
		}
		/**
		 * @param start the start to set
		 */
		public void setStart(int start) {
			this.start = start;
		}
		/**
		 * @return the end
		 */
		public int getEnd() {
			return end;
		}
		/**
		 * @param end the end to set
		 */
		public void setEnd(int end) {
			this.end = end;
		}
    	    	
    }

}
