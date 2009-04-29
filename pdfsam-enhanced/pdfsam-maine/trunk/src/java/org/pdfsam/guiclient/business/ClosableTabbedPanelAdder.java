/*
 * Created on 09-SEP-08
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
package org.pdfsam.guiclient.business;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.listeners.CleanClosedTabbedPanelListener;
import org.pdfsam.guiclient.commons.dnd.handlers.ClosableTabTransferHandler;
import org.pdfsam.guiclient.commons.panels.CloseableTabbedPane;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Adds a tab to the CloseableTabbedPane
 * @author Andrea Vacondio
 */
public class ClosableTabbedPanelAdder {
	private static final Logger log = Logger.getLogger(ClosableTabbedPanelAdder.class.getPackage().getName());

	private CloseableTabbedPane inputTabbedPanel;
	private PropertyChangeListener outputPathPropertyChangeListener = null;
	/**
	 * @param inputTabbedPanel
	 */
	public ClosableTabbedPanelAdder(CloseableTabbedPane inputTabbedPanel) {
		super();
		this.inputTabbedPanel = inputTabbedPanel;
		this.outputPathPropertyChangeListener = null;
	}

	
	/**
	 * @param inputTabbedPanel
	 * @param outputPathPropertyChangeListener listen the JVisualPdfPageSelectionPanel.OUTPUT_PATH_PROPERTY changes
	 */
	public ClosableTabbedPanelAdder(CloseableTabbedPane inputTabbedPanel, PropertyChangeListener outputPathPropertyChangeListener) {
		super();
		this.inputTabbedPanel = inputTabbedPanel;
		this.outputPathPropertyChangeListener = outputPathPropertyChangeListener;
	}


	/**
	 * Adds a tab for every File in fileList
	 * @param fileList File list
	 */
	public void addTabs(List<File> fileList) {		
		if(fileList != null && fileList.size()>0){
			addTabs((File[]) fileList.toArray(new File[fileList.size()]));        	
        }		
	}
	
	/**
	 * Adds a tab for every File in files
	 * @param files
	 */
	public void addTabs(File[] files){
		for(int i =0; i<files.length; i++){
			addTab(files[i]);			
    	}
	}

	/**
	 * add a tab for the input file and password
	 * @param file
	 * @param password
	 */
	public void addTab(File file, String password){
		try{
			if (file!=null && file.exists() && new PdfFilter(false).accept(file)){
	    		JVisualPdfPageSelectionPanel inputPanel = new JVisualPdfPageSelectionPanel(JVisualPdfPageSelectionPanel.HORIZONTAL_ORIENTATION, true, false, false, JVisualPdfPageSelectionPanel.STYLE_TOP_PANEL_HIDE, JVisualPdfPageSelectionPanel.DND_SUPPORT_NONE, JVisualPdfPageSelectionPanel.MULTIPLE_INTERVAL_SELECTION);
	    		inputPanel.getThumbnailList().setTransferHandler(new ClosableTabTransferHandler(this));
	    		if(outputPathPropertyChangeListener!=null){
	    			inputPanel.enableSetOutputPathMenuItem();
	    			inputPanel.addPropertyChangeListener(outputPathPropertyChangeListener);
	    		}
	    		inputPanel.setSelectedPdfDocument(file);
	    		inputPanel.getPdfLoader().addFile(file, password);
	    		String panelName = file.getName();
	    		if(panelName.length()>16){
	    			panelName = file.getName().substring(0, 14)+"..";
	    		}
	    		inputTabbedPanel.addTab(panelName, inputPanel, null, file.getName());
	    		inputTabbedPanel.addCloseableTabbedPaneListener(new CleanClosedTabbedPanelListener(inputTabbedPanel));
			}
		}catch(ThumbnailCreationException tce){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), tce);
		}
	}
	
	/**
	 * add a tab for the input file
	 * @param file
	 */
	public void addTab(File file){
		this.addTab(file, null);
	}
	/**
	 * @return the propertyChangeListener
	 */
	public PropertyChangeListener getOutputPathPropertyChangeListener() {
		return outputPathPropertyChangeListener;
	}

	/**
	 * @param outputPathPropertyChangeListener the propertyChangeListener to set, listen the JVisualPdfPageSelectionPanel.OUTPUT_PATH_PROPERTY changes
	 */
	public void setOutputPathPropertyChangeListener(PropertyChangeListener outputPathPropertyChangeListener) {
		this.outputPathPropertyChangeListener = outputPathPropertyChangeListener;
	}
	
	
}
