/*
 * Created on 23-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Drag&Drop abstract class
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractDropper extends DropTargetAdapter {
	
	private static final Logger log = Logger.getLogger(AbstractDropper.class.getPackage().getName());

	/**
	 * execute the drop
	 */
	@SuppressWarnings("unchecked")
	public void drop(DropTargetDropEvent e)  {
		try {
            DropTargetContext context = e.getDropTargetContext();
            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable t = e.getTransferable();
            if(hasFileFlavor(t)){
	            List<File> data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
	            if(data!=null){
	            	executeDrop(data);
	            }
            }
            context.dropComplete(true);
        }       
        catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), ex);
        }	
    }
	
	/**
	 * @param t
	 * @return true if it's a file flavor
	 */
	private boolean hasFileFlavor(Transferable t) {
		boolean retVal = false;
		DataFlavor[] flavors;
		flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
				retVal = true;
				break;
			}
		}
		return retVal;
	}
	   
	/**
	 * Executes the drop logic given the TransferData
	 * @param arg0
	 */
	protected abstract void executeDrop(List<File> arg0);
}
