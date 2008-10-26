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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.thumbnails.creators.JPedalThumbnailCreator;
import org.pdfsam.guiclient.business.thumbnails.creators.JPodThumbnailCreator;
import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
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
	
	public PdfThumbnailsLoader(JVisualPdfPageSelectionPanel panel){
		this.panel = panel;
	}
	
	 
    /**
     * adds a file to the JList
     */
    public void showFileChooserAndAddFile(){ 
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
    		if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(panel,
       				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selection list is full, would you like to empty before the new documents is loaded?"),
    				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"List full"),
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE)){
    			//empty the model
    			panel.resetPanel(); 
    			creator.clean();
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
     */
    public synchronized void addFile(final File file, final String password){
    	try{
    		initThumbnailsCreator();
			creator.initThumbnailsPanel(file, password, panel);					
    	}catch(Exception e){
        	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), e);
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
     * add a file to the JList
     * @param file
     * @param checkIfAlreadyAdded if true it checks if the list is already filled, if so it askes the user
     */
    public void addFile(File file, boolean checkIfAlreadyAdded){
    	if(!checkIfAlreadyAdded || (checkIfAlreadyAdded && canLoad())){	    	
			addFile(file);
    	}
    }
    
    /**
     * Thumbnails creator initialization
     */
    private void initThumbnailsCreator(){
    	if(Configuration.JPEDAL.equals(Configuration.getInstance().getThumbnailsGeneratorLibrary())){
    		creator = new JPedalThumbnailCreator();
    	}else{
    		creator = new JPodThumbnailCreator();
    	}
    }
    
    /**
     * Clean creator
     */
    public void cleanCreator(){
    	creator.clean();
    }
   
}