/*
 * Created on 22-Jun-2008
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
package org.pdfsam.guiclient.commons.business.loaders;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jpedal.PdfDecoder;
import org.jpedal.ThumbnailDecoder;
import org.jpedal.objects.PdfFileInformation;
import org.jpedal.objects.PdfPageData;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Loads a document a create thumbnails
 * @author Andrea Vacondio
 *
 */
public class PdfThumbnailsLoader {
	
	private static final Logger log = Logger.getLogger(PdfThumbnailsLoader.class.getPackage().getName());
	
	private JVisualPdfPageSelectionPanel panel;
	private JFileChooser fileChooser = null;
	private PdfDecoder decoder;
	private WorkQueue workQueue;
	private Configuration config;
	
	public PdfThumbnailsLoader(JVisualPdfPageSelectionPanel panel){
		this.panel = panel;
		config = Configuration.getInstance();
		
        decoder = new PdfDecoder(true);
        workQueue = new WorkQueue(3, decoder);
		
	}
	
	 
    /**
     * adds a file to the JList
     */
    public void showFileChooserAndAddFile(){ 
    	lazyInitFileChooser();
		if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION){
			if(panel.getThumbnailList().getModel().getSize() >= 1){
				//JList has elements and i want to clean
	    		if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(panel,
	       				GettextResource.gettext(config.getI18nResourceBundle(),"Selection list is full, would you like to empty before the new documents is loaded?"),
	    				GettextResource.gettext(config.getI18nResourceBundle(),"List full"),
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.QUESTION_MESSAGE)){
	    			//empty the model
	    			panel.resetPanel();
	    			if((workQueue.getQueueSize()>0)){
	    		        workQueue.cleanQueue();
	    			}
				}else{
					return;
				}
	    	}	    	
        	addFile(fileChooser.getSelectedFile());
		}
    }
    
    /**
     * Lazy JFileChooser initialization
     */
    private void lazyInitFileChooser(){
    	if(fileChooser == null){
    		fileChooser = new JFileChooser(config.getDefaultWorkingDir());
            fileChooser.setFileFilter(new PdfFilter());
            fileChooser.setMultiSelectionEnabled(false); 
    	}
    }
    
    /**
     * add a file to the JList
     * @param file input file
     * @param password password
     */
    public synchronized void addFile(File file, String password){
    	try{
    		panel.setSelectedPdfDocument(file);
    		final File inputFile = file;
    		final String pwd = password;
	        Runnable runner = new Runnable() {	    		
	            public void run() {
	            	try{
	            		if(inputFile!=null){
		            		if(pwd != null && pwd.length()>0){
		            			decoder.openPdfFile(inputFile.getAbsolutePath(), pwd);
		            		}else{
		            			decoder.openPdfFile(inputFile.getAbsolutePath());
		            		}
		            		int pages = decoder.getPageCount();
		            		//set file informations
		            		PdfFileInformation information = decoder.getFileInformationData();
		            		if(information!=null && information.getFieldValues()!=null){
		            			String[] infos = information.getFieldValues();
		            			panel.setDocumentProperties(inputFile.getAbsolutePath(), pages+"", decoder.getPDFVersion(), infos[0], infos[1], infos[4], infos[5]);		            			
		            		}else{
		            			panel.setDocumentProperties(inputFile.getAbsolutePath(), pages+"", decoder.getPDFVersion(), "", "", "", "");
		            		}
		            		panel.setDocumentPropertiesVisible(true);
		            		if(pages > 0){
		            			ArrayList modelList = new ArrayList(pages);
		            			for (int i = 1; i<=pages; i++){
		            				modelList.add(new VisualPageListItem(null, i));
		            			}
		            			((VisualListModel)panel.getThumbnailList().getModel()).setData((VisualPageListItem[])modelList.toArray(new VisualPageListItem[modelList.size()]));
		            			workQueue.addPages(modelList);
		            		}		            				            		
	            		}else{
	            			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Input pdf document is null."));
	            		}
	        		}catch(Exception e){
	        			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error opening pdf document."), e);
	        		}
	            }
	        };
	        SwingUtilities.invokeLater(runner);
        }catch(Exception e){
        	log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "), e);
        }
    }

    /**
     * add a file to the JList
     * @param file input file
     */
    public void addFile(File file){
    	this.addFile(file, null);
    }
    
    /**
     * @param page adds a page to be processed to the execution list
     */
    public void addPage(VisualPageListItem page) {
       workQueue.addPage(page);
    }
    
    /**
     * @param pages adds a pages Collection to be processed to the execution list
     */
    public void addPages(Collection pages) {
       workQueue.addPages(pages);
    }
    
    /**
     * @return number of pages to be processed
     */
    public int getQueueSize(){
    	return workQueue.getQueueSize();
    }    
    
    public class WorkQueue{
    	
        private final ThumbnailWorker[] threads;
        private final LinkedList queue;
    	private PdfDecoder decoder;
    	private ThumbnailDecoder thumbDecoder;
    	private PdfPageData pdfPageData = null;
        private int running = 0;

        /**
         * Default pool size = 1
         */
        public WorkQueue(PdfDecoder decoder){
        	this(1, decoder);
        }
        
        /**
         * @param nThreads pool size 
         */
        public WorkQueue(int nThreads, PdfDecoder decoder){
        	this.decoder = decoder;
        	this.thumbDecoder = new ThumbnailDecoder(decoder);
            this.queue = new LinkedList();
            this.threads = new ThumbnailWorker[nThreads];

            for (int i=0; i<nThreads; i++) {
                threads[i] = new ThumbnailWorker();
                threads[i].start();
            }
        }       
        
        /**
         * @return size of the queue of pages to be processed
         */
        public int getQueueSize(){
        	return queue.size();
        }
        
        /**
         * @param page adds a page to be processed to the execution list
         */
        public void addPage(VisualPageListItem page) {
            synchronized(queue) {
                queue.addLast(page);
                queue.notify();
            }
        }
        
        /**
         * @param pages adds a pages Collection to be processed to the execution list
         */
        public void addPages(Collection pages) {
            synchronized(queue) {
                queue.addAll(pages);
                queue.notifyAll();
            }
        }
        
        /**
         * empty the page list
         */
        public void cleanQueue() {
            synchronized(queue) {
                queue.clear();
                queue.notify();
            }
        }

        /**
         * Worker used to create thumbnails
         * @author Andrea Vacondio
         *
         */
        private class ThumbnailWorker extends Thread {
        	
        	private static final int LANDSCAPE_HEIGHT = 105;
        	private static final int PORTRAIT_HEIGHT = 150;
        	
            public void run() {
            	VisualPageListItem pageItem = null;
                BufferedImage image = null;
                while (true) {
                    synchronized(queue) {
                        while (queue.isEmpty()) {
                            try {
                                queue.wait();
                            }
                            catch (InterruptedException ignored){}
                        }
                        running++;
                        pageItem = (VisualPageListItem) queue.removeFirst();
                        if(pdfPageData==null){
                        	pdfPageData = decoder.getPdfPageData();
                        }
                        if(!decoder.isThumbnailsDrawing()){
                        	decoder.setThumbnailsDrawing(true);
                        }
                    }
                   if(pageItem != null){
	                    try{
	                    	long i = System.currentTimeMillis();
	                      	log.debug("["+this+"]: getting image from decoder.");
	                      	int height = PORTRAIT_HEIGHT;
	                      	if(pdfPageData.getCropBoxHeight(pageItem.getPageNumber())<pdfPageData.getCropBoxWidth(pageItem.getPageNumber())){
	                      		height = LANDSCAPE_HEIGHT;
	                      	}
	                      	image = thumbDecoder.getPageAsThumbnail(pageItem.getPageNumber(), height);	
	                      	pageItem.setThumbnail(image);
	                        ((VisualListModel)panel.getThumbnailList().getModel()).elementChanged(pageItem);
	                        log.debug("["+this+"]: thumbnail generated in "+(System.currentTimeMillis()-i));
                        }catch (Exception e) {
                        	running--;
                    		log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Unable to generate thumbnail"),e);
                    	}finally{
                    		image = null;
                    		pageItem = null;                    		
                    	}
                    }   
                    //close decoder
                    running--;
            		if(queue.isEmpty() && running==0){
            			log.debug("["+this+"]: closing decoder.");
            			decoder.setThumbnailsDrawing(false);
            			decoder.closePdfFile();
            		}
                }
            }
        }
    }
}