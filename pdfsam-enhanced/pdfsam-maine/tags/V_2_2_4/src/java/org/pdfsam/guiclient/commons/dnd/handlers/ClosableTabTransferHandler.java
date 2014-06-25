/*
 * Created on 17-Apr-2009
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
import java.io.File;
import java.util.List;

import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.ClosableTabbedPanelAdder;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.panels.JVisualMultiSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Used as TransferHandler for panels included into a {@link JVisualMultiSelectionPanel}
 * @author Andrea Vacondio
 *
 */
public class ClosableTabTransferHandler extends VisualListExportTransferHandler {

	private static final long serialVersionUID = -8007011325038959040L;

	private static final Logger log = Logger.getLogger(ClosableTabTransferHandler.class.getPackage().getName());

	private ClosableTabbedPanelAdder adder = null;
	

	/**
	 * @param adder
	 */
	public ClosableTabTransferHandler(ClosableTabbedPanelAdder adder) {
		super();
		this.adder = adder;
	}
	
	public boolean canImport(TransferHandler.TransferSupport info) {
    	boolean retVal = false;
		if(adder!=null && info.getComponent() instanceof JVisualSelectionList){
			if(info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){		
					retVal = true;
			}else{
				retVal = false;
			}
		}
		return retVal;
   }
	
	@SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport info) {
		boolean retVal = false;
		if(adder != null && info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
            try {
            	List<File> fileList = (List<File>)info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            	adder.addTabs(fileList);
            }catch(Exception e){
            	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
            }
		}
        return retVal;
	}
}
