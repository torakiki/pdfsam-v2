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
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.IdManager;
import org.pdfsam.guiclient.business.thumbnails.executors.ThumbnailsExecutor;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.ImageUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * Abstract thumbnail creator
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractThumbnailCreator implements ThumbnailsCreator {
	
	private static final Logger log = Logger.getLogger(AbstractThumbnailCreator.class.getPackage().getName());

	private String providedPassword = "";
	private File inputFile = null;
	private JVisualPdfPageSelectionPanel panel;
	private long currentId = 0;
	
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


	public BufferedImage getThumbnail(String fileName, String password, int page, float resizePercentage) throws ThumbnailCreationException {
		BufferedImage retVal = null;
		if(fileName != null && fileName.length()>0){
			File inputFile = new File(fileName);
    		retVal = getThumbnail(inputFile, password, page, resizePercentage);
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
	 * @deprecated use the {@link ImageUtility#getScaledInstance(BufferedImage, int, int)}
	 */
    protected BufferedImage getScaledImage(BufferedImage page , int height) throws Exception{
    	BufferedImage retVal = null;            	
    	Image scaledInstance = null;
    	scaledInstance = page.getScaledInstance(-1,height,BufferedImage.SCALE_SMOOTH);
    	retVal  = new BufferedImage(scaledInstance.getWidth(null),scaledInstance.getHeight(null) , BufferedImage.TYPE_INT_ARGB);                
        Graphics2D g2 = retVal.createGraphics();                
        g2.drawImage(scaledInstance, 0, 0,null);
    	return retVal;
    }
    
	@Override
	public void clean(long id) {
		IdManager.getInstance().cancelExecution(id);
	}
	
	public void initThumbnailsPanel(String fileName, String password, JVisualPdfPageSelectionPanel panel, long id, List<DocumentPage> template) throws ThumbnailCreationException {
		if(fileName != null && fileName.length()>0){
    		File inputFile = new File(fileName);
    		initThumbnailsPanel(inputFile, password, panel, id, template);			
		}else{
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create thumbnails for a null input document"));
		}       
	}
	
	public void initThumbnailsPanel(File inputFile, String password, JVisualPdfPageSelectionPanel panel, long id, List<DocumentPage> template) throws ThumbnailCreationException{
		setProvidedPassword(password);
		setInputFile(inputFile);
		setPanel(panel);
		setCurrentId(id);
		initThumbnailsCreation();
		if(openInputDocument()){
			panel.setSelectedPdfDocument(inputFile);              	
			panel.setSelectedPdfDocumentPassword(getProvidedPassword());
			DocumentInfo documentInfo = getDocumentInfo();
			if(documentInfo!=null){
				panel.setDocumentProperties(documentInfo);		            		
	        	panel.setDocumentPropertiesVisible(true);
			}
			Vector<VisualPageListItem> modelList = getDocumentModel(template);
			if(modelList!=null && modelList.size()>0){
				((VisualListModel)panel.getThumbnailList().getModel()).setData(modelList);				
				ThumbnailsExecutor.getInstance().invokeAll(getGenerationTasks(modelList), getCloserTask(), id);
			}
		}
		finalizeThumbnailsCreation();
	}	
	
	
	public BufferedImage getPageImage(File inputFile, String password, int page) throws ThumbnailCreationException {
		return getPageImage(inputFile, password, page, 0);
	}
	
	/**
	 * initialization of the thumbnail creation process
	 * @throws ThumbnailCreationException
	 */
	protected abstract void initThumbnailsCreation() throws ThumbnailCreationException;
	
	/**
	 * Opens the input document
	 * @return true if opened correctly 
	 * @throws ThumbnailCreationException
	 */
	protected abstract boolean openInputDocument() throws ThumbnailCreationException;
	
	/**
	 * @return the DocumentiInfo to set on the thumbnails panel
	 * @throws ThumbnailCreationException
	 */
	protected abstract DocumentInfo getDocumentInfo() throws ThumbnailCreationException;
	
	/**
	 * The model to set on thumbnails list before the thumbnails generation starts. This is used to show hourglasses images as thumbnails.
	 * @param template Pages template. Used when loading an environment to set rotation and other informations
	 * @return a Vector of VisualPageListItem
	 * @throws ThumbnailCreationException
	 */
	protected abstract Vector<VisualPageListItem> getDocumentModel(List<DocumentPage> template) throws ThumbnailCreationException;
	
	/**
	 * @return The Collable that is submitted when all the thumbnails generation tasks are terminated. Used to close the document or other clean operations.
	 * @throws ThumbnailCreationException
	 */
	protected abstract Callable<Boolean> getCloserTask() throws ThumbnailCreationException;
	
	/**
	 * @param modelList the model list set on the Visual List Component
	 * @return a Collection of tasks that will run the thumbnails generation
	 * @throws ThumbnailCreationException
	 */
	protected abstract Collection<? extends Callable<Boolean>> getGenerationTasks(Vector<VisualPageListItem> modelList) throws ThumbnailCreationException;
	
	/**
	 * generic finalization. It shouldn't clean resources used by the Thumbnails Generation Tasks (Callable). 
	 * @throws ThumbnailCreationException
	 */
	protected abstract void finalizeThumbnailsCreation() throws ThumbnailCreationException;

	/**
	 * @return the providedPassword
	 */
	protected String getProvidedPassword() {
		return providedPassword;
	}

	/**
	 * @param providedPassword the providedPassword to set
	 */
	protected void setProvidedPassword(String providedPassword) {
		this.providedPassword = providedPassword;
	}

	/**
	 * @return the inputFile
	 */
	protected File getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile the inputFile to set
	 */
	private void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the panel
	 */
	protected JVisualPdfPageSelectionPanel getPanel() {
		return panel;
	}

	/**
	 * @param panel the panel to set
	 */
	private void setPanel(JVisualPdfPageSelectionPanel panel) {
		this.panel = panel;
	}

	/**
	 * @return the currentId
	 */
	protected long getCurrentId() {
		return currentId;
	}

	/**
	 * @param currentId the currentId to set
	 */
	private void setCurrentId(long currentId) {
		this.currentId = currentId;
	}
	
	
	
}
