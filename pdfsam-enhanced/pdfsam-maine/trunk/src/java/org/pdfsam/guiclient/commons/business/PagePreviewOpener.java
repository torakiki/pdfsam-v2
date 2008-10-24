/*
 * Created on 22-Oct-2008
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
package org.pdfsam.guiclient.commons.business;

import java.io.File;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.thumbnails.creators.JPedalThumbnailCreator;
import org.pdfsam.guiclient.commons.business.thumbnails.creators.JPodThumbnailCreator;
import org.pdfsam.guiclient.commons.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.frames.JPagePreviewFrame;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Open a preview frame with the right page preview
 * @author Andrea Vacondio
 *
 */
public class PagePreviewOpener {
	
	private static final Logger log = Logger.getLogger(PagePreviewOpener.class.getPackage().getName());
	
	private static PagePreviewOpener instance = null;
	private JPagePreviewFrame frame = null;
	private Thread imageOpener = null;
	
	private PagePreviewOpener(){
		init();
	}
	
	private void init(){
		frame = new JPagePreviewFrame();
		frame.setTitle(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Image viewer"));
	}
	
	public static synchronized PagePreviewOpener getInstance(){
		if(instance == null){
			instance = new PagePreviewOpener();
		}
		return instance;
	}
	
	/**
	 * Creates a preview of the page and opens the frame to show it
	 * @param inputFile input file
	 * @param password document password
	 * @param page
	 */
	public synchronized void openPreview(File inputFile, String password, int page){
		execute(new ImageOpener(inputFile, page, password));
	}
	/**
	 * Creates a preview of the page and opens the frame to show it
	 * @param inputFilePath input file
	 * @param password document password
	 * @param page 
	 */
	public synchronized void openPreview(String inputFilePath, String password, int page){
		execute(new ImageOpener(inputFilePath, page, password));
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone PagePreviewOpener object.");
	}
	
	/**
	 * executes r
	 * @param r
	 */
	private void execute(Runnable r){
		if(imageOpener!=null && imageOpener.isAlive()){
			imageOpener.interrupt();
		}
		imageOpener = new Thread(r);
		imageOpener.start();
	}
    /**
     * Page preview creator initialization
     */
    private ThumbnailsCreator getCreator(){
    	ThumbnailsCreator retVal = null;
    	if(Configuration.JPEDAL.equals(Configuration.getInstance().getThumbnailsGeneratorLibrary())){
    		retVal = new JPedalThumbnailCreator();
    	}else{
    		retVal = new JPodThumbnailCreator();
    	}
    	return retVal;
    }
    
    /**
     * generates a preview image and sets visible the frame
     * @author Andrea Vacondio
     *
     */
    private class ImageOpener implements Runnable{
    	
    	private File inputFile;
    	private String password;
    	private int page;    		
		
		/**
		 * @param inputFile input file to be opened
		 * @param page page of the input file
		 * @param password input file password
		 */
		public ImageOpener(File inputFile, int page, String password) {
			super();
			this.inputFile = inputFile;
			this.page = page;
			this.password = password;
		}
		/**
		 * @param inputFilePath input file to be opened
		 * @param page page of the input file
		 * @param password input file password
		 */
		public ImageOpener(String inputFilePath, int page, String password) {
			super();
			this.inputFile = new File(inputFilePath);
			this.page = page;
			this.password = password;
		}

		public void run() {				
			try{
				frame.setPagePreview(getCreator().getPageImage(inputFile, password, page));
				frame.setVisible(true);
            }catch (Exception e) {
        		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to open image preview"),e);
        	}				           
		}    	
		
	}
}
