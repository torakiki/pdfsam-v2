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

import org.pdfsam.guiclient.commons.business.listeners.CleanClosedTabbedPanelListener;
import org.pdfsam.guiclient.commons.panels.CloseableTabbedPane;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
/**
 * Adds a tab to the CloseableTabbedPane
 * @author Andrea Vacondio
 */
public class ClosableTabbedPanelAdder {
	
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
    		JVisualPdfPageSelectionPanel inputPanel = new JVisualPdfPageSelectionPanel(JVisualPdfPageSelectionPanel.HORIZONTAL_ORIENTATION, true, false, false, false, JVisualPdfPageSelectionPanel.STYLE_TOP_PANEL_HIDE, true, false, JVisualPdfPageSelectionPanel.MULTIPLE_INTERVAL_SELECTION);
    		if(outputPathPropertyChangeListener!=null){
    			inputPanel.enableSetOutputPathMenuItem();
    			inputPanel.addPropertyChangeListener(outputPathPropertyChangeListener);
    		}
    		File currFile = files[i];
    		inputPanel.setSelectedPdfDocument(currFile);
    		inputPanel.getPdfLoader().addFile(currFile);
    		String panelName = currFile.getName();
    		if(panelName.length()>12){
    			panelName = currFile.getName().substring(0, 9)+"...";
    		}
    		inputTabbedPanel.addTab(panelName, inputPanel, null, currFile.getName());
    		inputTabbedPanel.addCloseableTabbedPaneListener(new CleanClosedTabbedPanelListener(inputTabbedPanel));
    	}
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
