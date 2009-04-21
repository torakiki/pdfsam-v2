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
package org.pdfsam.guiclient.business.thumbnails.creators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.thumbnails.executors.JPodThumbnailsExecutor;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.guiclient.utils.ImageUtility;
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

	
	public static final int JPOD_RESOLUTION = 72;
	private static final Logger log = Logger.getLogger(JPodThumbnailCreator.class.getPackage().getName());
	
	public BufferedImage getPageImage(File inputFile, String password, int page) throws ThumbnailCreationException {
		return getPageImage(inputFile, password, page, 0);
	}

	public BufferedImage getPageImage(File inputFile, String password, int page, int rotation) throws ThumbnailCreationException {
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
				imgTransform.scale(1, -1);
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
				int totalRotation = (rotation+pdPage.getRotate())%360;
				if(totalRotation!=0){
              		Image rotated = ImageUtility.rotateImage(retVal, totalRotation);	
              		retVal = new BufferedImage(rotated.getWidth(null), rotated.getHeight(null), BufferedImage.TYPE_INT_RGB);
              		Graphics g = retVal.getGraphics();
              		g.drawImage(rotated, 0, 0, null);
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
			retVal = getScaledImage(tempImage , Math.round(tempImage.getHeight(null)*resizePercentage));
		} catch (Exception e) {
			throw new ThumbnailCreationException(e);
		}
		return retVal;
	}

	public void initThumbnailsPanel(File inputFile, String password, JVisualPdfPageSelectionPanel panel,long id, List<DocumentPage> template) throws ThumbnailCreationException{
		String providedPwd = password;	
			PDDocument pdfDoc = null;
			if (inputFile!=null && inputFile.exists() && inputFile.isFile()){
				try{//create doc
					try{
						pdfDoc = openDoc(inputFile, providedPwd);
					}catch(IOException ioe){
						if(ioe.getCause() instanceof COSSecurityException){
							providedPwd = DialogUtility.askForDocumentPasswordDialog(panel, inputFile.getName());
							if(providedPwd != null && providedPwd.length()>0){
								pdfDoc = openDoc(inputFile, providedPwd);
							}else{
								throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Password not provided."));
							}
						}else{
							throw new ThumbnailCreationException(ioe);
						}
					}
					if(pdfDoc != null){
						//require password if encrypted
						//I already opened the document in visual mode but if encrypted it will need 
						//the password to manipulate
						if(pdfDoc.isEncrypted() && (providedPwd==null || providedPwd.length()==0)){
							providedPwd = DialogUtility.askForDocumentPasswordDialog(panel, inputFile.getName());
						}
						panel.setSelectedPdfDocument(inputFile);              	
						panel.setSelectedPdfDocumentPassword(providedPwd);
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
	            			if(template == null || template.size()<=0){
		            			for (int i = 1; i<=pages; i++){
		            				modelList.add(new VisualPageListItem(i, inputFile.getCanonicalPath(), providedPwd));
		            			}
	            			}else{
	            				for(DocumentPage page: template){
	            					if(page.getPageNumber()>0 && page.getPageNumber()<=pages){
	            						VisualPageListItem currentItem =  new VisualPageListItem(page.getPageNumber(), inputFile.getCanonicalPath(), providedPwd);
	            						currentItem.setDeleted(page.isDeleted());
	            						currentItem.setRotation(page.getRotation());
	            						modelList.add(currentItem);
	            					}
	            				}
	            			}
	            			((VisualListModel)panel.getThumbnailList().getModel()).setData((VisualPageListItem[])modelList.toArray(new VisualPageListItem[modelList.size()]));                		
	            			long startTime = System.currentTimeMillis();
	            			initThumbnails(pdfDoc, pageTree, panel, modelList, id);
	            			pageTree = null;
	            			JPodThumbnailsExecutor.getInstance().execute(new CreatorCloser(pdfDoc, startTime), id);
	            		}	
					}else{
						throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to open the document."));
					}
				}catch(IOException ioe){
					throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error opening pdf document")+" "+inputFile.getAbsolutePath(), ioe);
				}catch(COSLoadException cle){
					throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error opening pdf document")+" "+inputFile.getAbsolutePath(), cle);
				}catch(OutOfMemoryError oom){
					throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Not enough memory to create thumbnails")+" "+inputFile.getAbsolutePath(), oom);
				}
    		}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Input file doesn't exists or is a directory"));
			}						
 
	}
	
	/**
	 * Creates thumbnails
	 * @param decoder
	 * @param panel
	 * @param modelList
	 */
	private void initThumbnails(final PDDocument pdfDoc, final PDPageTree pageTree, final JVisualPdfPageSelectionPanel panel,final ArrayList<VisualPageListItem> modelList, final long id){	
		if(pageTree!=null && panel != null && modelList!=null && modelList.size()>0){
			for(VisualPageListItem pageItem : modelList){
				PDPage pdPage = pageTree.getPageAt(pageItem.getPageNumber()-1);
				JPodThumbnailsExecutor.getInstance().execute(pdPage, pageItem, panel, id);	
			}
		}		 		
	}	
	
	/**
	 * Used to close the PdfDoc
	 * @author Andrea Vacondio
	 *
	 */
	private class CreatorCloser implements Runnable{
		
		private PDDocument pdfDoc;
		private long startTime = 0;
		
		
		public CreatorCloser(PDDocument pdfDoc, long startTime) {
			super();
			this.pdfDoc = pdfDoc;
			this.startTime = startTime;
		}
		
		public void run() {				
			try{			
				if(pdfDoc!=null){
					pdfDoc.close();
					log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnails generated in "+(System.currentTimeMillis() - startTime)+"ms"));
					pdfDoc = null;
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
	
	public int getResolution(){
		return JPOD_RESOLUTION;
	}

	@Override
	public void clean(long id) {
		JPodThumbnailsExecutor.getInstance().cancelExecution(id);
	}

}
