/*
 * Created on 29-Sep-2008
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.i18n.GettextResource;

/**
 * Abstract thumbnail creator
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractThumbnailCreator implements ThumbnailsCreator {
	
	private static final Logger log = Logger.getLogger(AbstractThumbnailCreator.class.getPackage().getName());
	
	public BufferedImage getPageImage(String fileName, String password, int page, int rotation) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		if(fileName != null && fileName.length()>0){
    		File inputFile = new File(fileName);
    		retVal = getPageImage(inputFile, password, page, rotation);
		}else{
			throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create image for a null input document"));
		}
		return retVal;
	}
	
	public BufferedImage getPageImage(String fileName, String password, int page) throws ThumbnailCreationException {
		return getPageImage(fileName, password, page, 0);
	}
	
	public void initThumbnailsPanel(String fileName, String password, JVisualPdfPageSelectionPanel panel, long id) {
		try{
			if(fileName != null && fileName.length()>0){
	    		File inputFile = new File(fileName);
	    		initThumbnailsPanel(inputFile, password, panel, id);			
			}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create thumbnails for a null input document"));
			}
        }catch(Exception e){
        	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), e);
        }
	}
	
	

	public BufferedImage getThumbnail(String fileName, String password, int page, float resizePercentage, String quality) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		if(fileName != null && fileName.length()>0){
			File inputFile = new File(fileName);
    		retVal = getThumbnail(inputFile, password, page, resizePercentage, quality);
		}else{
			throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create image for a null input document"));
		}
		return retVal;		
	}
	
	/**
	 * 
	 * @param page the imput image
	 * @param height new height
	 * @return a scaled version of the image
	 * @throws Exception
	 */
    protected synchronized BufferedImage getScaledImage(BufferedImage page , int height) throws Exception{
    	BufferedImage retVal = null;            	
    	Image scaledInstance = null;
    	scaledInstance = page.getScaledInstance(-1,height,BufferedImage.SCALE_SMOOTH);
    	retVal  = new BufferedImage(scaledInstance.getWidth(null),scaledInstance.getHeight(null) , BufferedImage.TYPE_INT_ARGB);                
        Graphics2D g2 = retVal.createGraphics();                
        g2.drawImage(scaledInstance, 0, 0,null);
    	return retVal;
    }


}
