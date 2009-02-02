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
package org.pdfsam.guiclient.business.thumbnails.creators;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;

/**
 * Interface for thumbnails creators that can be used to fill thumbnails of the {@link JVisualPdfPageSelectionPanel}. 
 * @author Andrea Vacondio
 *
 */
public interface ThumbnailsCreator {

	/**
	 * Error image
	 */
	Image ERROR_IMAGE =  new ImageIcon(PdfThumbnailsLoader.class.getResource("/images/thumbnailerror.png")).getImage();
	/**
	 * Default resize percentage 20%
	 */
	public static final float DEFAULT_RESIZE_PERCENTAGE = 0.2f;
    /**
     * Default size in pixel
     */
	public static final int DEFAULT_SIZE = 166;
	
	/**
	 * 
	 * @param fileName pdf document
	 * @param password document password or null if no password is needed
	 * @param page page nmber
	 * @param resizePercentage resize level
	 * @param quality quality of the thumbnail
	 * @return a thumbnail of the given page
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getThumbnail(String fileName, String password, int page , float resizePercentage, String quality) throws ThumbnailCreationException;
	/**
	 * 
	 * @param fileName pdf document
	 * @param password document password or null if no password is needed
	 * @param page
	 * @return an image version of the given page
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getPageImage(String fileName, String password, int page) throws ThumbnailCreationException;	
	/**
	 * 
	 * @param fileName
	 * @param password
	 * @param page
	 * @param rotation page rotation in degrees
	 * @return
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getPageImage(String fileName, String password, int page, int rotation) throws ThumbnailCreationException;	
	/**
	 * Initialize the input panel. Set the document properties on the panel and starts previews generation.
	 * @param fileName
	 * @param password document password or null if no password is needed
	 * @param panel
	 */
	public void initThumbnailsPanel(String fileName, String password, JVisualPdfPageSelectionPanel panel);
	
	/**
	 * 
	 * @param inputFile pdf document
	 * @param password document password or null if no password is needed
	 * @param page page nmber
	 * @param resizePercentage resize level
	 * @param quality quality of the thumbnail
	 * @return a thumbnail of the given page
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getThumbnail(File inputFile, String password, int page , float resizePercentage, String quality) throws ThumbnailCreationException;
	/**
	 * 
	 * @param inputFile pdf document
	 * @param password document password or null if no password is needed
	 * @param page
	 * @return an image version of the given page
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getPageImage(File inputFile, String password, int page) throws ThumbnailCreationException;
	/**
	 * @param fileName
	 * @param password
	 * @param page
	 * @param rotation page rotation in degrees
	 * @return
	 * @throws ThumbnailCreationException
	 */
	public BufferedImage getPageImage(File fileName, String password, int page, int rotation) throws ThumbnailCreationException;	
	/**
	 * Initialize the input panel. Set the document properties on the panel and starts previews generation.
	 * @param inputFile pdf document
	 * @param password document password or null if no password is needed
	 * @param panel
	 */
	public void initThumbnailsPanel(File inputFile, String password, JVisualPdfPageSelectionPanel panel);
	
	/**
	 * @return resolution of the created thumbnails
	 */
	public int getResolution();
	/**
	 * Clean up
	 */
	public void clean();
}
