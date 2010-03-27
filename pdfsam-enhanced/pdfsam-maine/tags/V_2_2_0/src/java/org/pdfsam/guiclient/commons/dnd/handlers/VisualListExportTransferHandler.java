/*
 * Created on 23-Jan-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.dnd.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.DnDSupportUtility;
import org.pdfsam.guiclient.commons.dnd.transferables.VisualPageListTransferable;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Transfer Handler with the only export support
 * @author Andrea Vacondio
 *
 */
public class VisualListExportTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -8912890262680226922L;
	
	private static final Logger log = Logger.getLogger(VisualListExportTransferHandler.class.getPackage().getName());

	private PdfThumbnailsLoader loader = null;

	/**
	 * Default constructor. Cannot import files.
	 */
	public VisualListExportTransferHandler() {
		this(null);
	}

	/**
	 * @param loader if the loader is != null it can import files.
	 */
	public VisualListExportTransferHandler(PdfThumbnailsLoader loader) {
		super();
		this.loader = loader;
	}

	public int getSourceActions(JComponent c) {
	    return COPY;
	}
	
	protected Transferable createTransferable(JComponent c) {
		VisualPageListTransferable retVal = null;
		Object[] selectedList = ((JVisualSelectionList)c).getSelectedValues();
		if(selectedList != null && selectedList.length>0){
			VisualPageListItem[] items = new VisualPageListItem[selectedList.length];
			for(int i = 0; i< selectedList.length; i++){
				items[i] = (VisualPageListItem)selectedList[i];
			}
			int[] indexes = ((JVisualSelectionList)c).getSelectedIndices();
			retVal = new VisualPageListTransferable(items, indexes);			
		}
		return retVal;
	}
	
    public boolean canImport(TransferHandler.TransferSupport info) {
    	boolean retVal = false;
		if(loader!=null && info.getComponent() instanceof JVisualSelectionList){
			if(info.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || info.isDataFlavorSupported(DnDSupportUtility.URI_LIST_FLAVOR)){		
					retVal = true;
			}else{
				retVal = false;
			}
		}
		return retVal;
   }

	/**
	 * @return the loader
	 */
	protected PdfThumbnailsLoader getLoader() {
		return loader;
	}

	/**
	 * @param loader the loader to set
	 */
	protected void setLoader(PdfThumbnailsLoader loader) {
		this.loader = loader;
	}
    
	@SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport info) {
		boolean retVal = false;
		if(loader != null){
			List<File> fileList = null;
            try {
            	if(info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            		fileList = (List<File>)info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            	}else if(info.isDataFlavorSupported(DnDSupportUtility.URI_LIST_FLAVOR)){
            		fileList = DnDSupportUtility.textURIListToFileList((String)info.getTransferable().getTransferData(DnDSupportUtility.URI_LIST_FLAVOR));
            	}

				if (fileList != null) {
					if (fileList.size() != 1) {
						log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please select a single pdf document."));
					} else {
						File selectedFile = fileList.get(0);
						if (selectedFile != null && new PdfFilter(false).accept(selectedFile)) {
							loader.addFile(selectedFile, true);
							retVal = true;
						} else {
							log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"File type not supported."));
						}
					}
				}
            }catch(Exception e){
            	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
            }
		}
        return retVal;
	}
	
}
