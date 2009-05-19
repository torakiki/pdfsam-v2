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

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.IdManager;
import org.pdfsam.guiclient.business.thumbnails.ThumbnailCreatorsRegisty;
import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.DialogUtility;
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
	private ThumbnailsCreator creator;
	private long id = 0;
	
	public PdfThumbnailsLoader(JVisualPdfPageSelectionPanel panel){
		this.panel = panel;
	}
	
	 
    /**
     * adds a file to the JList
     */
    public void showFileChooserAndAddFile() throws ThumbnailCreationException{ 
    	lazyInitFileChooser();
		if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION){
			if(canLoad()){	    	
				addFile(fileChooser.getSelectedFile());
        	}
		}
    }
    
    /**
     * Lazy JFileChooser initialization
     */
    private void lazyInitFileChooser(){
    	if(fileChooser == null){
    		fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
            fileChooser.setFileFilter(new PdfFilter());
            fileChooser.setMultiSelectionEnabled(false); 
    	}
    }
    
    /**
     * check if the panel is empty and, if necessary shows a dialog to ask the user
     * @return true if can load
     */
    public boolean canLoad(){
    	boolean retVal = true;
    	if(panel.getThumbnailList().getModel().getSize() >= 1){
			//JList has elements and i want to clean
    		if(JOptionPane.YES_OPTION == DialogUtility.askForEmptySelectionPanel(panel)){
    			//empty the model
    			panel.resetPanel(); 
			}else{
				retVal = false;
			}
    	}
    	return retVal;
    }
    
    /**
     * add a file to the JList
     * @param file input file
     * @param password password
     * @param template pages template
     */
    public synchronized void addFile(final File file, final String password, List<DocumentPage> template) throws ThumbnailCreationException{
   		creator = ThumbnailCreatorsRegisty.getCreator(Configuration.getInstance().getThumbnailsCreatorIdentifier());
   		if(creator != null){
	   		generateNewId();
			creator.initThumbnailsPanel(file, password, panel, id, template);
   		}else{
   			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to initialize the thumbnails creation library."));
   		}
    }

    /**
     * Add file without a template
     * @param file
     * @param password
     * @throws ThumbnailCreationException
     */
    public void addFile(final File file, final String password) throws ThumbnailCreationException{
    	this.addFile(file, password, null);
    }
    
    /**
     * add a file to the JList
     * @param file input file
     */
    public void addFile(File file) throws ThumbnailCreationException{
    	this.addFile(file, null);
    }
    
    /**
     * add a file to the JList
     * @param file
     * @param checkIfAlreadyAdded if true it checks if the list is already filled, if so it asks the user
     */
    public void addFile(File file, boolean checkIfAlreadyAdded) throws ThumbnailCreationException{
    	if(!checkIfAlreadyAdded || (checkIfAlreadyAdded && canLoad())){	    	
			addFile(file);
    	}
    }  
    
    /**
     * Clean creator
     */
    public void cleanCreator(){
    	if(creator != null){
        	creator.clean(id);    		
    	}
    }
	/**
	 * generates a new thumbnails generation ticket
	 */
	private void generateNewId(){
		id = IdManager.getInstance().getNewId();
	}
    
	/**
	 * 
	 * @return current id
	 */
	public long getId(){
		return id;
	}
	
}