/*
 * Created on 27-Jun-2008
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
package org.pdfsam.guiclient.commons.dnd.handlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.transferables.VisualPageListTransferable;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Transfer handler for the JVisualSelectionList
 * @author Andrea Vacondio
 * @see JVisualSelectionList
 * @see VisualPageListTransferable
 */
public class VisualSelectionListTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 5096134904057557588L;

	private static final Logger log = Logger.getLogger(VisualSelectionListTransferHandler.class.getPackage().getName());

	private PdfThumbnailsLoader loader;
	private int addIndex = 0;
	private boolean acceptFromDifferentSource = false;
	private boolean canImportFile = true;
	private boolean canImportListObject = true;
	
	public VisualSelectionListTransferHandler(PdfThumbnailsLoader loader) {
		this(loader, false);
	}	
	
	/**
	 * @param acceptFromDifferentSource
	 * @param loader
	 */
	public VisualSelectionListTransferHandler(PdfThumbnailsLoader loader, boolean acceptFromDifferentSource) {
		this(loader, acceptFromDifferentSource, true, true);
	}

	

	/**
	 * @param loader
	 * @param acceptFromDifferentSource
	 * @param canImportFile
	 * @param canImportListObject
	 */
	public VisualSelectionListTransferHandler(PdfThumbnailsLoader loader, boolean acceptFromDifferentSource, boolean canImportFile,
			boolean canImportListObject) {
		super();
		this.acceptFromDifferentSource = acceptFromDifferentSource;
		this.canImportFile = canImportFile;
		this.canImportListObject = canImportListObject;
		this.loader = loader;
	}

	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		boolean retVal = false;
		if((comp instanceof JVisualSelectionList) && (transferFlavors != null)){
			for (int i = 0; i<transferFlavors.length; i++){
				if((transferFlavors[i].equals(DataFlavor.javaFileListFlavor) && canImportFile) || (transferFlavors[i].equals(VisualPageListTransferable.getVisualListFlavor()) && canImportListObject)){
					retVal = true;
				}else{
					retVal = false;
					break;
				}
			}
		}
		return retVal;
	}
	
	protected Transferable createTransferable(JComponent c) {
		VisualPageListTransferable retVal = null;
		int[] selectedList = ((JVisualSelectionList)c).getSelectedIndices();
		if(selectedList != null && selectedList.length>0){
			retVal = new VisualPageListTransferable(c,selectedList);			
		}
		return retVal;
	}
	
	protected void exportDone(JComponent source, Transferable data, int action) {
		//clean the source only if i'm not exporting to another component
		if(action==MOVE && !((VisualPageListTransferable)data).isDifferentDestination()){
	    	JVisualSelectionList listComponent = (JVisualSelectionList) source;
	    	int[] dataList = ((VisualPageListTransferable) data).getDataList();
	    	if (dataList[0] <= addIndex) {
	    		((VisualListModel)listComponent.getModel()).removeElements(dataList[0], dataList[dataList.length-1], true);
	        }else{
	        	((VisualListModel)listComponent.getModel()).removeElements(dataList[0]+dataList.length, dataList[dataList.length-1]+dataList.length, true);
	        }
	    	addIndex = 0;
    	}
	}

	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	public boolean importData(JComponent comp, Transferable t) {
		boolean retVal = false;
		if(canImport(comp, t.getTransferDataFlavors())){
            try {                
                if(hasFileFlavor(t)) {
                	List fileList = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                	if(fileList.size()!=1){
                		log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please select a single pdf document."));
                	}else{
                		File selectedFile = (File)fileList.get(0);
                		if (selectedFile!=null && new PdfFilter(false).accept(selectedFile)){                			
                			if(loader.canLoad()){ 
                				loader.addFile(selectedFile);
                    			retVal = true;
                			}else{
                    			retVal = false;                				
                			}
                		}else{
                			log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"File type not supported."));
                		}
                	}
                }else if (hasVisualListItemFlavor(t)) {
                	Object obj = t.getTransferData(VisualPageListTransferable.getVisualListFlavor());
                    if ((obj instanceof VisualPageListTransferable)){
                    	VisualPageListTransferable transferable = (VisualPageListTransferable)obj;
                    	transferable.setDestination(comp);
                    	//source and destination are equals or the handler accepts different components
                    	if(!transferable.isDifferentDestination()|| (transferable.isDifferentDestination() && acceptFromDifferentSource)){	
                    		retVal = importVisualListItems(transferable);
                    	}
                    }
                } 
            }catch(Exception e){
            	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error during drag and drop."), e);
            }
		}
        return retVal;
	}
	
	/**
	 * @param t
	 * @return true if the flavor is javaFileListFlavor
	 */
	private boolean hasFileFlavor(Transferable t) {
        boolean retVal = false;
		 DataFlavor[] flavors = t.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
            	retVal =  true;
            }else{
            	retVal = false;
            	break;
            }
        }
        return retVal;
    }
    
	/**
	 * @param t
	 * @return true if the flavor is visualListFlavor
	 */
    private boolean hasVisualListItemFlavor(Transferable t) {
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
    
    /**
     * import the data from the transferable
     * @param comp
     * @param t
     * @return
     */
    private boolean importVisualListItems(VisualPageListTransferable t){
    	boolean retVal = false;
    	JVisualSelectionList destComponent = (JVisualSelectionList) t.getDestination();
    	JVisualSelectionList sourceComponent = (JVisualSelectionList) t.getSource();
    	int[] dataList = t.getDataList();
    	int index = destComponent.getSelectedIndex();
    	if(t.isDifferentDestination()){
    		//drop at the end if nothing is selected
    		if(index == -1){
    			addIndex = destComponent.getModel().getSize();
    		}else{
    			addIndex = index+1;
    		}
    		retVal = true;
    	}
        //same component prevents dropping over itself
    	else if(dataList!=null && dataList.length>0 &&(index<(dataList[0]) || (index>dataList[dataList.length-1]))){
    		//check limits
    		int listSize = destComponent.getModel().getSize();
    		if(index>listSize){
    			addIndex=listSize;
    		}else if (index<0){
    			addIndex=0;
    		}else{
    			addIndex = index;
    			//if moving forward
    			if(addIndex>dataList[0]){
    				addIndex++;
    			}
    		}
    		retVal = true;
    	}
    	if(retVal){
    		Collection c = ((VisualListModel)sourceComponent.getModel()).subList(dataList[0], dataList[dataList.length-1]+1, true);
    		((VisualListModel)destComponent.getModel()).addAllElements(addIndex, c);
    		destComponent.setSelectionInterval(addIndex, addIndex+dataList.length-1);    		
    	}    
    	return retVal;
    }
 
}
