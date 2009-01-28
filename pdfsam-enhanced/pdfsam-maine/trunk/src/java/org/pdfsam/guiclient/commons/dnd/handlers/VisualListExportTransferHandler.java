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

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.transferables.VisualPageListTransferable;
import org.pdfsam.guiclient.dto.VisualPageListItem;
/**
 * Transfer Handler with the only export support
 * @author Andrea Vacondio
 *
 */
public class VisualListExportTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -8912890262680226922L;

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
	
	/**
	 * @param t
	 * @return true if the flavor is visualListFlavor
	 */
    protected boolean hasVisualListItemFlavor(Transferable t) {
    	 boolean retVal = false;
 		 DataFlavor[] flavors = t.getTransferDataFlavors();
         for (int i = 0; i < flavors.length; i++) {
             if (flavors[i].equals(VisualPageListTransferable.getVisualListFlavor())) {
             	retVal =  true;
             }else{
             	retVal = false;
             	break;
             }
         }
         return retVal;
    }
    

    public boolean canImport(TransferHandler.TransferSupport info) {
    	boolean retVal = false;
		if(info.getComponent() instanceof JVisualSelectionList){
			if(info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){		
					retVal = true;
			}else{
				retVal = false;
			}
		}
		return retVal;
   }
}
