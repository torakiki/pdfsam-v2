/*
 * Created on 24-Jun-2008
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
package org.pdfsam.guiclient.commons.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Listen for event coming from the selection panel
 * @author Andrea Vacondio
 *
 */
public class VisualPdfSelectionActionListener implements ActionListener {
  	
	private static final Logger log = Logger.getLogger(VisualPdfSelectionActionListener.class.getPackage().getName());
    
	public static final String ADD = "add";
	public static final String ADDSINGLE = "addSingle";
    public static final String CLEAR = "clear";       
    
    /**
     * reference to the selection panel
     */
    private JVisualPdfPageSelectionPanel panel;
    private PdfThumbnailsLoader loader;

    
	/**
	 * @param panel
	 * @param loader
	 */
	public VisualPdfSelectionActionListener(JVisualPdfPageSelectionPanel panel, PdfThumbnailsLoader loader) {
		super();
		this.panel = panel;
		this.loader = loader;
	}


	public void actionPerformed(ActionEvent e) {
		JList pagesList = panel.getThumbnailList();
        if (e != null && pagesList != null){        	
    		try{
    			if(CLEAR.equals(e.getActionCommand())){
    				 panel.resetPanel();
    			}else if (ADD.equals(e.getActionCommand())){
    				loader.showFileChooserAndAddFile();
    			}
    		 }
            catch (Exception ex){
                log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "),ex); 
            }
        }
	}
}
