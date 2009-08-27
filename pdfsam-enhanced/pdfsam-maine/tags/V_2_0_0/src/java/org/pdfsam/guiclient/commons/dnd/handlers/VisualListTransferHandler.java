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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.transferables.VisualPageListTransferable;
import org.pdfsam.guiclient.commons.dnd.transferables.VisualPageListTransferable.TransferableData;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.i18n.GettextResource;
/**
 * Transfer handler for the JVisualSelectionList
 * @author Andrea Vacondio
 * @see JVisualSelectionList
 */
public class VisualListTransferHandler extends VisualListExportTransferHandler {

	private static final long serialVersionUID = -6893213515673375373L;

	private static final Logger log = Logger.getLogger(VisualListTransferHandler.class.getPackage().getName());

	private int addIndex = 0;

	public VisualListTransferHandler() {
	}

	/**
	 * @param loader
	 */
	public VisualListTransferHandler(PdfThumbnailsLoader loader) {
		super(loader);
	}

	public int getSourceActions(JComponent c) {
	    return MOVE;
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		//clean only if MOVE
		//it works only if the selection type of the JList is ListSelectionModel.SINGLE_INTERVAL_SELECTION
		if(action==MOVE){
	    	try{
	    		JVisualSelectionList listComponent = (JVisualSelectionList) source;
	            TransferableData transferredData = (TransferableData)data.getTransferData(VisualPageListTransferable.getVisualListFlavor());
	            if(transferredData != null && transferredData.getIndexesList()!=null){
	            	int[] dataList = transferredData.getIndexesList();
	            	int delta = (dataList[0] > addIndex)? dataList.length: 0;
	            	((VisualListModel)listComponent.getModel()).removeElements(dataList[0]+delta, dataList[dataList.length-1]+delta, true);
	            }
		    	addIndex = 0;
	    	}catch(Exception e){
    			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
    		}
    	}
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		boolean retVal = false;
		if(info.isDrop()){
            try {        

            	Transferable t = info.getTransferable();
            	if(hasFileFlavor(t)){
            		retVal = super.importData(info);
            	}else if (hasVisualListItemFlavor(t)) {
                   	retVal = importVisualListItems(info);                    	
                } 
            }catch(Exception e){
            	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
            }
		}
        return retVal;
	}
    

    
    /**
     * import the data from the transferable
     * @param comp
     * @param t
     * @return
     */
    private boolean importVisualListItems(TransferHandler.TransferSupport info){
    	boolean retVal = false;
    	Transferable transferable = info.getTransferable();
    	//source and destination are equals or the handler accepts different components
    		try{
	    		JList.DropLocation dropLocation = (JList.DropLocation)info.getDropLocation();
	            int index = dropLocation.getIndex();
	            VisualListModel destModel =  ((VisualListModel)((JVisualSelectionList)info.getComponent()).getModel());
	            TransferableData transferredData = (TransferableData)transferable.getTransferData(VisualPageListTransferable.getVisualListFlavor());
	            if(transferredData!=null){
	            	VisualPageListItem[] dataList = transferredData.getDataList();
			    	if(dataList!=null && dataList.length>0){
			    		//drop at the end
			    		if(index==-1){
			    			addIndex = destModel.getSize();
			    		}else{
				    		//check limits
				    		int listSize = destModel.getSize();
				    		if(index>listSize || index<0){
				    			addIndex=listSize;
				    		}else{
				    			addIndex = index;
				    		}
			    		}
			    		retVal = true;
			    	}
			    	if(retVal){
			    		Collection<VisualPageListItem> items = Arrays.asList(dataList);
			    		//if moving to another component
			    		if(info.getSourceDropActions()==COPY){
			    			Vector<VisualPageListItem> newList = new Vector<VisualPageListItem>(items.size());
		        			for(VisualPageListItem currItem : items){
		        				newList.add((VisualPageListItem) currItem.clone());
		        			}
		        			items = newList;
			    		}
			    		destModel.addAllElements(addIndex, items);
			    	} 
		    	}
    		}catch(Exception e){
    			retVal = false;
    			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
    		}
    	return retVal;
    }
    
    public boolean canImport(TransferHandler.TransferSupport info) {
    	boolean retVal = false;
    	if(super.canImport(info)){
    		retVal = true;
    	}
    	else if(info.getComponent() instanceof JVisualSelectionList){
			if(info.isDataFlavorSupported(VisualPageListTransferable.getVisualListFlavor())){
				if(info.getSourceDropActions()==MOVE){
					try{
						TransferableData transferredData = (TransferableData)info.getTransferable().getTransferData(VisualPageListTransferable.getVisualListFlavor());
						JList.DropLocation dropLocation = (JList.DropLocation)info.getDropLocation();
			            int index = dropLocation.getIndex();
			            if(transferredData!=null){
			            	int[] indices = transferredData.getIndexesList();
			            	//prevent dropping over itself
							retVal =  !(indices != null && index >= indices[0] && index <= indices[indices.length -1]+1);
			            }
		            }catch (UnsupportedFlavorException e) {
						retVal = false;
					} catch (IOException e) {
						retVal = false;
					}
				}else{
					retVal = true;
					
				}				
			}else{
				retVal = false;
			}
		}
		return retVal;
   }

}
