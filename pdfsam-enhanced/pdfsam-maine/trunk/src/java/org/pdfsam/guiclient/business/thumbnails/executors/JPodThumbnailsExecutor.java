/*
 * Created on 31-Mar-2009
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
package org.pdfsam.guiclient.business.thumbnails.executors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.thumbnails.creators.JPodThumbnailCreator;
import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.utils.ImageUtility;
import org.pdfsam.i18n.GettextResource;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;

/**
 * Singleton that executes the thumbnails creations
 * @author Andrea Vacondio
 *
 */
public class JPodThumbnailsExecutor {

	private static JPodThumbnailsExecutor instance = null;
	
	private static final Logger log = Logger.getLogger(JPodThumbnailsExecutor.class.getPackage().getName());
	
	private  LinkedList<ExecutorService> pools = null;
	private  HashSet<Long> cancelledExecutions = null;;
	
	private JPodThumbnailsExecutor(){
		cancelledExecutions =  new HashSet<Long>();				
		pools = new LinkedList<ExecutorService>();
		for(int i = 0; i<Configuration.getInstance().getThumbCreatorPoolSize(); i++){
			pools.add(i, Executors.newSingleThreadExecutor());
		}
	}

	public static synchronized JPodThumbnailsExecutor getInstance() { 
		if (instance == null){
			instance = new JPodThumbnailsExecutor();
		}
		return instance;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone JPodThumbnailsExecutor object.");
	}
	
	/**
	 * Executes the thumbnail creation
	 * @param pdPage
	 * @param pageItem
	 * @param panel
	 * @param id
	 */
	public synchronized void execute(PDPage pdPage, VisualPageListItem pageItem , final JVisualPdfPageSelectionPanel panel, final long id){
		getExecutor(id).execute(new ThumnailCreator(pdPage, pageItem, panel, id));	
	}
	
	/**
	 * Executes r
	 * @param r
	 * @param id
	 */
	public synchronized void execute(Runnable r, final long id){
		getExecutor(id).execute(r);
	}
	
	/**
	 * Threads with the given id wont generate thumbnails
	 * @param id
	 */
	public synchronized void cancelExecution(final long id){
		if (cancelledExecutions == null){
			cancelledExecutions =  new HashSet<Long>();
		}
		cancelledExecutions.add(id);
	}
	
	/**
	 * @param id
	 * @return true if the execution is cancelled
	 */
	public boolean  isCancelledExecution(final long id){
		boolean retVal = false;
		if(cancelledExecutions!=null && cancelledExecutions.size()>0){
			retVal = cancelledExecutions.contains(id);
		}
		return retVal;
	}
	
	/**
	 * @param id
	 * @return the executor for the given ID
	 */
	private ExecutorService getExecutor(long id){
		int index = (int) (id % Configuration.getInstance().getThumbCreatorPoolSize());
		ExecutorService executor = pools.get(index);
		if(executor == null || executor.isShutdown()){
			executor = Executors.newSingleThreadExecutor();
			pools.set(index, executor);
		}
		return executor;
	}
	
	/**
	 * Creates the thumbnail
	 * @author Andrea Vacondio
	 *
	 */
	private static class ThumnailCreator implements Runnable{
		
		private PDPage pdPage;
		private JVisualPdfPageSelectionPanel panel;
		private VisualPageListItem pageItem;
		private long id;
			
		/**
		 * @param pdPage
		 * @param pageItem
		 * @param panel
		 */
		public ThumnailCreator(PDPage pdPage, VisualPageListItem pageItem, JVisualPdfPageSelectionPanel panel, long id) {
			super();
			this.pdPage = pdPage;
			this.pageItem = pageItem;
			this.panel = panel;
			this.id = id;
		}


		public void run() {	
			if(!getInstance().isCancelledExecution(id)){
				IGraphicsContext graphics = null;
				try{
					Rectangle2D rect = pdPage.getCropBox().toNormalizedRectangle();
					double rectHeight = rect.getHeight();
					double recWidth = rect.getWidth();				
					double resizePercentage = getResizePercentage(rectHeight, recWidth);
					
					int height = Math.round(((int) rect.getHeight())*(float)resizePercentage);
					int width = Math.round(((int) rect.getWidth())*(float)resizePercentage);
					BufferedImage scaledInstance = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
					Graphics2D g2 = (Graphics2D) scaledInstance.getGraphics();
					graphics = new CwtAwtGraphicsContext(g2);
					// setup user space
					AffineTransform imgTransform = graphics.getTransform();
					imgTransform.scale(resizePercentage, -resizePercentage);
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
	              	pageItem.setPaperFormat(recWidth, rectHeight, JPodThumbnailCreator.JPOD_RESOLUTION);
	              	if(pdPage.getRotate()!=0){
	              		pageItem.setOriginalRotation(Rotation.getRotation(pdPage.getRotate()));
	              	}
	              	if(pageItem.isRotated() && pageItem.getThumbnail()!=null){
	              		pageItem.setRotatedThumbnail(ImageUtility.rotateImage(pageItem.getThumbnail(), pageItem.getCompleteRotation()));	
	        		}
	            }catch (Throwable t) {
	            	pageItem.setThumbnail(ImageUtility.getErrorImage());
	        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to generate thumbnail"),t);
	        	}finally{
	        		if(graphics!=null){
	        			graphics.dispose();
	        		}
	        		pdPage = null;
	        	}
	            ((VisualListModel)panel.getThumbnailList().getModel()).elementChanged(pageItem);
			}
		}
		/**
		 * @param height
		 * @param width
		 * @return percentage resize
		 */
		private double getResizePercentage(double height, double width){
			double retVal = 0;
			if(height>=width){
				retVal = Math.round(((double)ThumbnailsCreator.DEFAULT_SIZE/height)*100.0)/100.0;
			}else{
				retVal = Math.round(((double)ThumbnailsCreator.DEFAULT_SIZE/width)*100.0)/100.0;
			}
			return retVal;
		}
	}
	
}
