/*
 * Created on 27-Sep-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.thumbnails.creators;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jpedal.PdfDecoder;
import org.jpedal.objects.PdfFileInformation;
import org.jpedal.objects.PdfPageData;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.i18n.GettextResource;
/**
 * Thumbnail creator using JPedal
 * @author Andrea Vacondio
 *
 */
public class JPedalThumbnailCreator extends AbstractThumbnailCreator {

	private static final Logger log = Logger.getLogger(JPedalThumbnailCreator.class.getPackage().getName());
	private ExecutorService pool = null;

	/**
	 * Creates thumbnails
	 * @param decoder
	 * @param panel
	 * @param modelList
	 */
	private void initThumbnails(final PdfDecoder decoder, final JVisualPdfPageSelectionPanel panel,final ArrayList<VisualPageListItem> modelList){	
		PdfPageData pdfPageData = null;	
		if(decoder!=null && panel != null && modelList!=null && modelList.size()>0){
			if(pdfPageData==null){
	         	pdfPageData = decoder.getPdfPageData();
			}
			decoder.setThumbnailsDrawing(true);
			for(VisualPageListItem pageItem : modelList){
				execute(new ThumnailCreator(decoder, pageItem, panel, pdfPageData));		
			}
			//to close the decoder
			execute(new CreatorCloser(decoder));
		}		 		
	}
	

	public BufferedImage getPageImage(File inputFile, String password, int page) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		PdfDecoder decoder = null;
		if (inputFile.exists() && inputFile.isFile()){
			try{
    			decoder = new PdfDecoder();
    			if(password != null && password.length()>0){
        			decoder.openPdfFile(inputFile.getAbsolutePath(), password);
        		}else{
        			decoder.openPdfFile(inputFile.getAbsolutePath());
        		}
    			retVal = decoder.getPageAsImage(page);
			}catch(Exception e){
				throw new ThumbnailCreationException(e);
	        }finally{
	        	if(decoder!=null){
	        		decoder.closePdfFile();
	        	}
	        }
		}else{
			throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Input file doesn't exists or is a directory"));
		}		
		return retVal;
	}

	public BufferedImage getThumbnail(File inputFile, String password, int page, float resizePercentage, String quality) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		BufferedImage tempImage = getPageImage(inputFile, password, page);
		try {
			retVal = getScaledImage(tempImage , Math.round(tempImage.getHeight()*resizePercentage));
		} catch (Exception e) {
			throw new ThumbnailCreationException(e);
		}
		return retVal;
	}

	public void initThumbnailsPanel(File inputFile, String password, JVisualPdfPageSelectionPanel panel) {
		try{			
    		if (inputFile!=null && inputFile.exists() && inputFile.isFile()){
    			PdfDecoder decoder = null;
    			try{
        			decoder = new PdfDecoder();
        			panel.setSelectedPdfDocument(inputFile);
            		if(password != null && password.length()>0){
            			decoder.openPdfFile(inputFile.getAbsolutePath(), password);
            		}else{
            			decoder.openPdfFile(inputFile.getAbsolutePath());
            		}
            		int pages = decoder.getPageCount();
            		//set file informations
            		PdfFileInformation information = decoder.getFileInformationData();
            		DocumentInfo documentInfo = new DocumentInfo();
            		documentInfo.setFileName(inputFile.getAbsolutePath());
            		documentInfo.setPages(pages);
            		documentInfo.setPdfVersion(decoder.getPDFVersion());
            		documentInfo.setEncrypted(decoder.isEncrypted());
            		if(information!=null && information.getFieldValues()!=null){
            			String[] infos = information.getFieldValues();
            			documentInfo.setTitle(infos[0]);
            			documentInfo.setAuthor(infos[1]);
            			documentInfo.setCreator(infos[4]);
            			documentInfo.setProducer(infos[5]);
            		}
            		panel.setDocumentProperties(documentInfo);		            		
            		panel.setDocumentPropertiesVisible(true);
            		if(pages > 0){
            			ArrayList<VisualPageListItem> modelList = new ArrayList<VisualPageListItem>(pages);
            			for (int i = 1; i<=pages; i++){
            				modelList.add(new VisualPageListItem(i, inputFile.getCanonicalPath(), password));
            			}
            			((VisualListModel)panel.getThumbnailList().getModel()).setData((VisualPageListItem[])modelList.toArray(new VisualPageListItem[modelList.size()]));                		
            			initThumbnails(decoder, panel, modelList);				
            			
            		}		            				            		            		
        		}catch(Exception e){
        			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error opening pdf document."), e);
        		}
    		}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Input file doesn't exists or is a directory"));
			}						
        }catch(Exception e){
        	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), e);
        }
		
	}
	
	/**
	 * executes r
	 * @param r
	 */
	private void execute(Runnable r){
		if(pool==null || pool.isShutdown()){
			pool = Executors.newSingleThreadExecutor();
		}
		pool.execute(r);
	}
	
	public void clean(){
		if(pool!=null){
			pool.shutdownNow();
			pool = null;
		}		
	}
	
	/**
	 * Creates the thumbnail
	 * @author Andrea Vacondio
	 *
	 */
	private class ThumnailCreator implements Runnable{
		
		private PdfDecoder decoder;
		private JVisualPdfPageSelectionPanel panel;
		private VisualPageListItem pageItem;
		private PdfPageData pdfPageData;
			
		/**
		 * @param decoder
		 * @param pageItem
		 * @param panel
		 * @param pdfPageData
		 */
		public ThumnailCreator(PdfDecoder decoder, VisualPageListItem pageItem, JVisualPdfPageSelectionPanel panel,
				PdfPageData pdfPageData) {
			super();
			this.decoder = decoder;
			this.pageItem = pageItem;
			this.panel = panel;
			this.pdfPageData = pdfPageData;
		}


		public void run() {				
			try{
				long t = System.currentTimeMillis();
              	int height = Math.round(pdfPageData.getCropBoxHeight(pageItem.getPageNumber())*DEFAULT_RESIZE_PERCENTAGE);
              	BufferedImage page =decoder.getPageAsImage(pageItem.getPageNumber());                  	
              	BufferedImage image = getScaledImage(page, height);
              	pageItem.setThumbnail(image);
              	long t2 = System.currentTimeMillis()-t;
              	log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnail generated in ")+t2+"ms");
            }catch (Exception e) {
            	pageItem.setThumbnail(ERROR_IMAGE);
        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to generate thumbnail"),e);
        	}				
            ((VisualListModel)panel.getThumbnailList().getModel()).elementChanged(pageItem);
		}    	
	}
	
	/**
	 * Used to close the PdfDecoder
	 * @author Andrea Vacondio
	 *
	 */
	private class CreatorCloser implements Runnable{
		
		private PdfDecoder decoder;

		/**
		 * @param decoder
		 */
		public CreatorCloser(PdfDecoder decoder) {
			super();
			this.decoder = decoder;
		}
		
		public void run() {				
			try{
				decoder.closePdfFile();
				log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnails generator closed"));
            }catch (Exception e) {
        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to close thumbnail creator"),e);
        	}				           
		}    	
		
	}
}
