/*
 * Created on 27-JUN-08
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
package org.pdfsam.guiclient.commons.dnd.droppers;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

/**
 * Dropper for the JVisualSelectionList
 * @author Andrea Vacondio
 *
 */
public class JVisualSelectionListDropper extends AbstractDropper {

	private static final Logger log = Logger.getLogger(JVisualSelectionListDropper.class.getPackage().getName());
	
	private PdfThumbnailsLoader loader;	
	
	/**
	 * @param loader
	 */
	public JVisualSelectionListDropper(PdfThumbnailsLoader loader) {
		this.loader = loader;
	}

	protected void executeDrop(Object arg0) {
		List fileList = (List)arg0;
    	if(fileList.size()!=1){
    		log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please select a single pdf document."));
    	}else{
    		File selectedFile = (File)fileList.get(0);
    		if (selectedFile!=null && new PdfFilter(false).accept(selectedFile)){
    			loader.addFile(selectedFile, true);
    		}else{
    			log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"File type not supported."));
    		}
    	}
	}

}
