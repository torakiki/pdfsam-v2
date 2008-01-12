/*
 * Created on 23-Mar-2006
 *
 * Copyright notice: this code is based on concat_pdf class by Mark Thompson. Copyright (c) 2002 Mark Thompson.
 * Copyright (C) 2006 by Andrea Vacondio.
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
package it.pdfsam.plugin.coverfooter.type;

import it.pdfsam.plugin.coverfooter.component.JCoverFooterTable;
import it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.lowagie.text.pdf.PdfReader;

/**
 * 
 * Class used to manage drag and drop in cover footer table.
 * @author Andrea Vacondio
 * @see javax.swing.TransferHandler
 * @see it.pdfsam.plugin.coverfooter.type.CoverFooterItemTransfer
 * 
 */

public class TableTransferHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1025963121787698726L;
	private int[] rows = null; //multiple selection
    private int addIndex = -1; //Location where items were added
    private int addCount = 0;  //Number of items added.

    /**
     * Export selected row
     * @param c component holding data
     * @return CoverFooterItem we are trying to move inside the CoverFooterTable
     */
    protected ArrayList exportCoverFooterItem(JComponent c) {
        ArrayList data = new ArrayList();
        if (!(c instanceof JCoverFooterTable)){
            return null;
        }
        JCoverFooterTable cover_table = (JCoverFooterTable)c;
        rows = cover_table.getSelectedRows();
        if (rows != null){
            for (int i = 0; i < rows.length; i++) {
                data.add(((CoverFooterTableModel)cover_table.getModel()).getRow(rows[i]));
            }
        }
        return data;
    }

    /**
     * 
     * @param c component to drop the item to
     * @param list_item items list to drop
     */
    protected void importCoverFooterItem(JComponent c, ArrayList list_item) {
        if (!(c instanceof JCoverFooterTable)){
            return;
        }
        JCoverFooterTable target_cover_table = (JCoverFooterTable)c;
        CoverFooterTableModel model = (CoverFooterTableModel)(target_cover_table.getModel());
        int index = target_cover_table.getSelectedRow();
        
        //Prevent the user from dropping data back on itself.
        //For example, if the user is moving rows #4,#5,#6 and #7 and
        //attempts to insert the rows after row #5, this would
        //be problematic when removing the original rows.
        if ((rows != null) && (index > (rows[0] - 1)) &&  (index <= rows[rows.length - 1])){
            //needed to avoid cleanup
            rows = null;
            return;
        }
//GET_DROP_INDEX        
        int max = model.getRowCount();
        if (index < 0) {
            index = max;
        } else {
            if (rows == null){
                index++;
            }
            else{
                if (rows[0] < index){
                    index++;
                }                
            }
            if (index > max) {
                index = max;
            }
        }
        addIndex = index;
//GET_DROP_INDEX_END
        try{
            for(int i=0; i< list_item.size(); i++){
                if (list_item.get(i) instanceof CoverFooterItemType){
                    CoverFooterItemType item = (CoverFooterItemType) list_item.get(i);
                    model.addRowAt(index++, item);
                    addCount++;
                }
            }
            target_cover_table.setRowSelectionInterval(addIndex, index-1);
        }
        catch(IndexOutOfBoundsException ioobe){
            return;
        }
    }
    
    
    protected void cleanup(JComponent c, boolean remove) {
        JCoverFooterTable source = (JCoverFooterTable)c;
        if (remove && rows != null) {
            CoverFooterTableModel model = (CoverFooterTableModel)source.getModel();

            //If we are moving items around in the same table, we
            //need to adjust the rows accordingly, since those
            //after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < rows.length; i++) {
                    if (rows[i] > addIndex) {
                        rows[i] += addCount;
                    }
                }
            }
            for (int i = rows.length - 1; i >= 0; i--) {
                model.deleteRow(rows[i]);
            }
        }
        rows = null;
        addCount = 0;
        addIndex = -1;
    }

    /**
     * Creates the Transferable object to manage drag and drop
     * @return Transferable object
     */
    protected Transferable createTransferable(JComponent c) {
        ArrayList arr = exportCoverFooterItem(c);
        return new CoverFooterItemTransfer(c, arr);
    }
    
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
 
    /**
     * Drop the CoverFooterItem
     */
    public boolean importData(JComponent c, Transferable t) {
        if (!(c instanceof JCoverFooterTable)){return false;}        
        if (canImport(c, t.getTransferDataFlavors())) {
            try {                
                if (hasCoverFooterItemFlavor(t)) {
                    Object obj = t.getTransferData(CoverFooterItemTransfer.COVERFOOTERITEMFLAVOUR);
                    if (!(obj instanceof CoverFooterItemTransfer)) return false;
                    CoverFooterItemTransfer mit = (CoverFooterItemTransfer)obj;
                    ArrayList cover_item_obj = mit.getData();
                    importCoverFooterItem(c, cover_item_obj);
                    return true;
                }else if (hasFileFlavor(t)){
                    List file_list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
                    ArrayList row_items = new ArrayList();
                    for (int i = 0; i < file_list.size(); i++) {
                        File file_item = (File)file_list.get(i);
                        boolean encrypt = false;
                        String num_pages = "";
                        try{
                            PdfReader pdf_reader = new PdfReader(file_item.getAbsolutePath());
                            encrypt = pdf_reader.isEncrypted();
                            // we retrieve the total number of pages
                            num_pages = Integer.toString(pdf_reader.getNumberOfPages());
                        }
                        catch (Exception ex){
                            num_pages = ex.getMessage();
                            
                        }
                        CoverFooterItemType cover_item_obj = new CoverFooterItemType(file_item.getName(),file_item.getAbsolutePath(), num_pages,"All",encrypt);
                        row_items.add(cover_item_obj);
                    }
                    importCoverFooterItem(c, row_items);
                    return true;                    
                }                
                else{
                    return false;
                }
            } catch (UnsupportedFlavorException ufe) {                
            } catch (IOException ioe) {
            }
        }
        return false;
    }    

    private boolean hasFileFlavor(Transferable t) {
        DataFlavor[] flavors;
        flavors = t.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(DataFlavor.javaFileListFlavor)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasCoverFooterItemFlavor(Transferable t) {
        DataFlavor[] flavors;
        flavors = t.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(CoverFooterItemTransfer.COVERFOOTERITEMFLAVOUR)) {
                return true;
            }
        }
        return false;
    }
    
    
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (!(c instanceof JCoverFooterTable)){return;}    
        cleanup(c, action == MOVE);
    }
    
    /**
     * Check if the item can be imported
     */    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(CoverFooterItemTransfer.COVERFOOTERITEMFLAVOUR) || flavors[i].equals(DataFlavor.javaFileListFlavor)){
                return true;
            }
        }
        return false;
    }
    

}
