/*
 * Created on 03-Oct-2008
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.crypt.COSSecurityException;
import de.intarsys.pdf.crypt.PasswordProvider;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDPageTree;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
import de.intarsys.tools.authenticate.IPasswordProvider;
import de.intarsys.tools.locator.FileLocator;
/**
 * Thumbnail creator using JPod
 * @author Andrea Vacondio
 *
 */
public class JPodThumbnailCreator extends AbstractThumbnailCreator {

	private static final Logger log = Logger.getLogger(JPodThumbnailCreator.class.getPackage().getName());
	private ExecutorService pool = null;
	private Thread closer = null;
	
	public BufferedImage getPageImage(File inputFile, String password, int page) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		IGraphicsContext graphics = null;
		PDDocument pdfDoc = null;
		if (inputFile.exists() && inputFile.isFile()){
			try {
				pdfDoc = openDoc(inputFile, password);
				PDPage pdPage = pdfDoc.getPageTree().getPageAt(page-1);			
				Rectangle2D rect = pdPage.getCropBox().toNormalizedRectangle();
	
				retVal = new BufferedImage((int) rect.getWidth(), (int) rect.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g2 = (Graphics2D) retVal.getGraphics();
				graphics = new CwtAwtGraphicsContext(g2);
				// setup user space
				AffineTransform imgTransform = graphics.getTransform();
				imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
				graphics.setTransform(imgTransform);
				graphics.setBackgroundColor(Color.WHITE);
				graphics.fill(rect);
				CSContent content = pdPage.getContentStream();
				if (content != null) {
					CSPlatformRenderer renderer = new CSPlatformRenderer(null,graphics);
					renderer.process(content, pdPage.getResources());
				}	
				if(pdfDoc!=null){
					pdfDoc.close();
				}
			}catch(Throwable t){
				throw new ThumbnailCreationException(t);
			}		
			finally {
				if (graphics != null) {
					graphics.dispose();
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
		String providedPwd = password;
		try{			
			PDDocument pdfDoc = null;
			if (inputFile!=null && inputFile.exists() && inputFile.isFile()){
				try {
					//create doc
					try{
						pdfDoc = openDoc(inputFile, providedPwd);
					}catch(IOException ioe){
						if(ioe.getCause() instanceof COSSecurityException){
							providedPwd = DialogUtility.askForDocumentPasswordDialog(panel);
							if(providedPwd != null && providedPwd.length()>0){
								pdfDoc = openDoc(inputFile, providedPwd);
							}else{
								pdfDoc = null;
							}
						}else{
							throw ioe;
						}
					}
					if(pdfDoc != null){
						panel.setSelectedPdfDocument(inputFile);              	
	                	//set file informations
						PDPageTree pageTree = pdfDoc.getPageTree();
						int pages = pageTree.getCount();
	                	DocumentInfo documentInfo = new DocumentInfo();
	            		documentInfo.setCreator(pdfDoc.getCreator());
	                	documentInfo.setFileName(inputFile.getAbsolutePath());
	                	documentInfo.setPages(pages);
	        			documentInfo.setAuthor(pdfDoc.getAuthor());
	               		documentInfo.setPdfVersion(pdfDoc.cosGetDoc().stGetDoc().getDocType().getVersion());
	               		documentInfo.setEncrypted(pdfDoc.isEncrypted());
	               		documentInfo.setTitle(pdfDoc.getTitle());
	           			documentInfo.setProducer(pdfDoc.getProducer());
	                	panel.setDocumentProperties(documentInfo);		            		
	                	panel.setDocumentPropertiesVisible(true);
	            		if(pages > 0){
	            			ArrayList<VisualPageListItem> modelList = new ArrayList<VisualPageListItem>(pages);
	            			for (int i = 1; i<=pages; i++){
	            				modelList.add(new VisualPageListItem(i, inputFile.getCanonicalPath(), providedPwd));
	            			}
	            			((VisualListModel)panel.getThumbnailList().getModel()).setData((VisualPageListItem[])modelList.toArray(new VisualPageListItem[modelList.size()]));                		
	            			initThumbnails(pdfDoc, pageTree, panel, modelList);
	            			closer = new Thread(new CreatorCloser(pool, pdfDoc));
	            			closer.start();
	            		}	
					}
        		}catch(Throwable t){
        			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error opening pdf document."), t);
        		}
    		}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Input file doesn't exists or is a directory"));
			}						
        }catch(Exception e){
        	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), e);
        }
	}
	
	/**
	 * Creates thumbnails
	 * @param decoder
	 * @param panel
	 * @param modelList
	 */
	private void initThumbnails(final PDDocument pdfDoc, final PDPageTree pageTree, final JVisualPdfPageSelectionPanel panel,final ArrayList<VisualPageListItem> modelList){	
		if(pageTree!=null && panel != null && modelList!=null && modelList.size()>0){
			for(VisualPageListItem pageItem : modelList){
				PDPage pdPage = pageTree.getPageAt(pageItem.getPageNumber()-1);
				execute(new ThumnailCreator(pdPage, pageItem, panel));	
			}
		}		 		
	}
	/**
	 * executes r
	 * @param r
	 */
	private void execute(Runnable r){
		if(pool==null || pool.isShutdown()){
			pool = Executors.newFixedThreadPool(3);
		}
		pool.execute(r);
	}
	
	public void clean(){
		if(pool!=null){
			pool.shutdownNow();
			pool = null;
			if(closer != null){
				closer = null;
			}
		}		
	}
	
	/**
	 * Creates the thumbnail
	 * @author Andrea Vacondio
	 *
	 */
	private class ThumnailCreator implements Runnable{
		
		private PDPage pdPage;
		private JVisualPdfPageSelectionPanel panel;
		private VisualPageListItem pageItem;
			
		/**
		 * @param pdPage
		 * @param pageItem
		 * @param panel
		 */
		public ThumnailCreator(PDPage pdPage, VisualPageListItem pageItem, JVisualPdfPageSelectionPanel panel) {
			super();
			this.pdPage = pdPage;
			this.pageItem = pageItem;
			this.panel = panel;
		}


		public void run() {		
			IGraphicsContext graphics = null;
			try{
				long t = System.currentTimeMillis();
				Rectangle2D rect = pdPage.getCropBox().toNormalizedRectangle();
				int height = Math.round(((int) rect.getHeight())*DEFAULT_RESIZE_PERCENTAGE);
				int width = Math.round(((int) rect.getWidth())*DEFAULT_RESIZE_PERCENTAGE);
				
				BufferedImage scaledInstance = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				Graphics2D g2 = (Graphics2D) scaledInstance.getGraphics();
				graphics = new CwtAwtGraphicsContext(g2);
				// setup user space
				AffineTransform imgTransform = graphics.getTransform();
				imgTransform.scale(DEFAULT_RESIZE_PERCENTAGE, -DEFAULT_RESIZE_PERCENTAGE);
				imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
				graphics.setTransform(imgTransform);
				graphics.setBackgroundColor(Color.WHITE);
				graphics.fill(rect);
				CSContent content = pdPage.getContentStream();
				if (content != null) {
					CSPlatformRenderer renderer = new CSPlatformRenderer(null,graphics);
					renderer.process(content, pdPage.getResources());
				}
				              	
              	pageItem.setThumbnail(scaledInstance);
              	long t2 = System.currentTimeMillis()-t;
              	log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnail generated in ")+t2+"ms");
            }catch (Throwable t) {
            	pageItem.setThumbnail(ERROR_IMAGE);
        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to generate thumbnail"),t);
        	}finally{
        		graphics.dispose();
        	}
            ((VisualListModel)panel.getThumbnailList().getModel()).elementChanged(pageItem);
		}    	
	}
	
	/**
	 * Used to close the PdfDoc
	 * @author Andrea Vacondio
	 *
	 */
	private class CreatorCloser implements Runnable{
		
		private PDDocument pdfDoc;
		private ExecutorService pool;
		/**
		 * @param decoder
		 */
		public CreatorCloser(ExecutorService pool, PDDocument pdfDoc) {
			super();
			this.pdfDoc = pdfDoc;
			this.pool = pool;
		}
		
		public void run() {				
			try{
				if(pool!=null){
					pool.shutdown();
					if(pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)){
						if(pdfDoc!=null){
							pdfDoc.close();
							log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnails generator closed"));
						}
					}
				}
            }catch (Exception e) {
        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to close thumbnail creator"),e);
        	}				           
		}    	
		
	}
	
	/**
	 * Creates the PDDocument
	 * @param inputFile
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws COSLoadException
	 */
	@SuppressWarnings("unchecked")
	private PDDocument openDoc(File inputFile, String password) throws IOException, COSLoadException{
		PDDocument retVal = null;
		FileLocator locator = new FileLocator(inputFile);
		if(password!=null){					
			Map options = new HashMap();					
			final char[] pwd = password.toCharArray();
			PasswordProvider.setPasswordProvider(options,new IPasswordProvider() {
				public char[] getPassword() {
					return pwd;
				}
			});
			retVal = PDDocument.createFromLocator(locator, options);
		}else{
			retVal = PDDocument.createFromLocator(locator);					
		}
		return retVal;
	}
}
