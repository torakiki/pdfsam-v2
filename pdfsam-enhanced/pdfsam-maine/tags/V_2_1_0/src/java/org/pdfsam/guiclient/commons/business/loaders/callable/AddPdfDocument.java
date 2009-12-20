/*
 * Created on 29-Nov-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.loaders.callable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.utils.EncryptionUtility;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Callable to add a pdf document to the selection panel
 * @author Andrea Vacondio
 *
 */
public class AddPdfDocument implements Callable<Boolean> {

	private static final Logger LOG = Logger.getLogger(AddPdfDocument.class.getPackage().getName());
	
	/*used to find the document data*/
	private static String TITLE = PdfName.decodeName(PdfName.TITLE.toString());
	private static String PRODUCER = PdfName.decodeName(PdfName.PRODUCER.toString());
	private static String AUTHOR = PdfName.decodeName(PdfName.AUTHOR.toString());
	private static String SUBJECT = PdfName.decodeName(PdfName.SUBJECT.toString());
	private static String CREATOR = PdfName.decodeName(PdfName.CREATOR.toString());
	private static String MODDATE = PdfName.decodeName(PdfName.MODDATE.toString());
	private static String CREATIONDATE = PdfName.decodeName(PdfName.CREATIONDATE.toString());
	private static String KEYWORDS = PdfName.decodeName(PdfName.KEYWORDS.toString());
	
	String wipText;
	File inputFile;
	String password;
	String pageSelection;
	JPdfSelectionPanel panel;
	
	public AddPdfDocument(File inputFile, JPdfSelectionPanel panel){
		this(inputFile, panel, null, null);
	}
	
	public AddPdfDocument(File inputFile, JPdfSelectionPanel panel, String password, String pageSelection){
		this.inputFile = inputFile;
		this.pageSelection = pageSelection;
		this.password = password;
		this.panel = panel;
	}
	
	public Boolean call() {
		Boolean retVal = Boolean.FALSE;
		try{
			 if (inputFile != null){
					if(new PdfFilter(false).accept(inputFile)){
		    			wipText = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please wait while reading")+" "+inputFile.getName()+" ...";
		                panel.addWipText(wipText);			                
		                panel.addTableRow(getPdfSelectionTableItem(inputFile, password, pageSelection));
		                panel.removeWipText(wipText);
		                retVal = Boolean.TRUE;
					}else{
						LOG.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selected file is not a pdf document.")+" "+inputFile.getName());
					}
            }
   		 }catch(Throwable e){
   			 LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "),e); 
   		 }	
   		 return retVal;
	 }
		
	  /**
     * 
     * @param fileToAdd file to add
     * @param password password to open the file
     * @return the item to add to the table
     */
    PdfSelectionTableItem getPdfSelectionTableItem(File fileToAdd, String password, String pageSelection){
    	PdfSelectionTableItem tableItem = null;
    	PdfReader pdfReader = null;
        if (fileToAdd != null){
        	tableItem = new PdfSelectionTableItem();
        	tableItem.setInputFile(fileToAdd);
        	tableItem.setPassword(password);
        	tableItem.setPageSelection(pageSelection);
            try{
                //fix 04/11/08 for memory usage
            	 pdfReader = new PdfReader(new RandomAccessFileOrArray(fileToAdd.getAbsolutePath()), (password != null)?password.getBytes():null);                	
                 tableItem.setEncrypted(pdfReader.isEncrypted());
                 tableItem.setFullPermission(pdfReader.isOpenedWithFullPermissions());
                 if(tableItem.isEncrypted()){
                 	tableItem.setPermissions(getPermissionsVerbose(pdfReader.getPermissions()));
                 	int cMode = pdfReader.getCryptoMode();
                 	switch (cMode){
                 	case PdfWriter.STANDARD_ENCRYPTION_40:
                 		tableItem.setEncryptionAlgorithm(EncryptionUtility.RC4_40);
                 		break;
                 	case PdfWriter.STANDARD_ENCRYPTION_128:
                 		tableItem.setEncryptionAlgorithm(EncryptionUtility.RC4_128);
                 		break;
                 	case PdfWriter.ENCRYPTION_AES_128:
                 		tableItem.setEncryptionAlgorithm(EncryptionUtility.AES_128);
                 		break;
                 	default:
                 		break;                    			
                 	}
                 }
                 tableItem.setPagesNumber(Integer.toString(pdfReader.getNumberOfPages()));
                 tableItem.setFileSize(fileToAdd.length());
                 tableItem.setPdfVersion(pdfReader.getPdfVersion());
                 tableItem.setSyntaxErrors(pdfReader.isRebuilt());
                 initTableItemDocumentData(pdfReader, tableItem);
            }
            catch (Exception e){
            	tableItem.setLoadedWithErrors(true);
            	LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error loading ")+fileToAdd.getAbsolutePath()+" :", e);
            }
            finally{
            	if(pdfReader != null){
            		pdfReader.close();
					pdfReader = null;
            	}
            }               
        }
        return tableItem;    
    } 
    /**
     * It gives a human readable version of the document permissions
     * @param permissions
     * @return
     */
    private String getPermissionsVerbose(int permissions) {
    	StringBuffer buf = new StringBuffer();
    	if ((PdfWriter.ALLOW_PRINTING & permissions) == PdfWriter.ALLOW_PRINTING) buf.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Print"));
        if ((PdfWriter.ALLOW_MODIFY_CONTENTS & permissions) == PdfWriter.ALLOW_MODIFY_CONTENTS) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Modify"));
        if ((PdfWriter.ALLOW_COPY & permissions) == PdfWriter.ALLOW_COPY) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Copy or extract"));
        if ((PdfWriter.ALLOW_MODIFY_ANNOTATIONS & permissions) == PdfWriter.ALLOW_MODIFY_ANNOTATIONS) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Add or modify text annotations"));
        if ((PdfWriter.ALLOW_FILL_IN & permissions) == PdfWriter.ALLOW_FILL_IN) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Fill form fields"));
        if ((PdfWriter.ALLOW_SCREENREADERS & permissions) == PdfWriter.ALLOW_SCREENREADERS) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Extract for use by accessibility dev."));
        if ((PdfWriter.ALLOW_ASSEMBLY & permissions) == PdfWriter.ALLOW_ASSEMBLY) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Manipulate pages and add bookmarks"));
        if ((PdfWriter.ALLOW_DEGRADED_PRINTING & permissions) == PdfWriter.ALLOW_DEGRADED_PRINTING) buf.append(", "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Low quality print"));
        return buf.toString();
    }
    /**
     * initialization of the document data
     * @param reader
     * @param tableItem
     */
    @SuppressWarnings("unchecked")
	private void initTableItemDocumentData(PdfReader reader, PdfSelectionTableItem tableItem){
    	if(reader!=null && tableItem != null){
    		HashMap<String, String> info = reader.getInfo();
    		if(info!=null && info.size()>0){
    			for (Map.Entry<String, String> entry: info.entrySet()) {        				
    				if(entry != null){
    					String key = entry.getKey();
    					String value = StringUtils.trimToEmpty(entry.getValue());
    					if(key.equals(TITLE)){
    						tableItem.getDocumentMetaData().setTitle(value);
    					}else if(key.equals(PRODUCER)){
    						tableItem.getDocumentMetaData().setProducer(value);
    					}else if(key.equals(AUTHOR)){
    						tableItem.getDocumentMetaData().setAuthor(value);
    					}else if(key.equals(SUBJECT)){
    						tableItem.getDocumentMetaData().setSubject(value);
    					}else if(key.equals(CREATOR)){
    						tableItem.getDocumentMetaData().setCreator(value);
    					}else if(key.equals(MODDATE)){
    						tableItem.getDocumentMetaData().setModificationDate(value);
    					}else if(key.equals(CREATIONDATE)){
    						tableItem.getDocumentMetaData().setCreationDate(value);
    					}else if(key.equals(KEYWORDS)){
    						tableItem.getDocumentMetaData().setKeywords(value);
    					}
    				}
    			}
    		}
    	}
    }

}
