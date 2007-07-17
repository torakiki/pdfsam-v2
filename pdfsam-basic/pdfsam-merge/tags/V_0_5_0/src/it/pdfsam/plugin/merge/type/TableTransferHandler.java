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
package it.pdfsam.plugin.merge.type;

import it.pdfsam.plugin.merge.component.JMergeTable;
import it.pdfsam.plugin.merge.model.MergeTableModel;

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
 * Class used to manage drag and drop in merge table.
 * @author Andrea Vacondio
 * @see javax.swing.TransferHandler
 * @see it.pdfsam.plugin.merge.type.MergeItemTransfer
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
     * @return MergeItem we are trying to move inside the MergeTable
     */
    protected ArrayList exportMergeItem(JComponent c) {
        ArrayList data = new ArrayList();
        if (!(c instanceof JMergeTable)){
            return null;
        }
        JMergeTable merge_table = (JMergeTable)c;
        rows = merge_table.getSelectedRows();
        if (rows != null){
            for (int i = 0; i < rows.length; i++) {
                data.add(((MergeTableModel)merge_table.getModel()).getRow(rows[i]));
            }
        }
        return data;
        //return (rows == -1)? null: ((MergeTableModel)merge_table.getModel()).getRow(rows);       
    }

    /**
     * 
     * @param c component to drop the item to
     * @param list_item items list to drop
     */
    protected void importMergeItem(JComponent c, ArrayList list_item) {
        if (!(c instanceof JMergeTable)){
            return;
        }
        JMergeTable target_merge_table = (JMergeTable)c;
        MergeTableModel model = (MergeTableModel)(target_merge_table.getModel());
        int index = target_merge_table.getSelectedRow();
        
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
                if (list_item.get(i) instanceof MergeItemType){
                    MergeItemType item = (MergeItemType) list_item.get(i);
                    model.addRowAt(index++, item);
                    addCount++;
                }
            }
            target_merge_table.setRowSelectionInterval(addIndex, index-1);
        }
        catch(IndexOutOfBoundsException ioobe){
            return;
        }
    }
    
    
    protected void cleanup(JComponent c, boolean remove) {
        JMergeTable source = (JMergeTable)c;
        if (remove && rows != null) {
            MergeTableModel model = (MergeTableModel)source.getModel();

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
        ArrayList arr = exportMergeItem(c);
        return new MergeItemTransfer(c, arr);
        //return new StringSelection(exportMergeItem(c));
    }
    
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
 
    /**
     * Drop the MergeItem
     */
    public boolean importData(JComponent c, Transferable t) {
        if (!(c instanceof JMergeTable)){return false;}        
        if (canImport(c, t.getTransferDataFlavors())) {
            try {                
                if (hasMergeItemFlavor(t)) {
                    Object obj = t.getTransferData(MergeItemTransfer.MERGEITEMFLAVOUR);
                    if (!(obj instanceof MergeItemTransfer)) return false;
                    MergeItemTransfer mit = (MergeItemTransfer)obj;
                    ArrayList merge_item_obj = mit.getData();
                    importMergeItem(c, merge_item_obj);
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
                        MergeItemType merge_item_obj = new MergeItemType(file_item.getName(),file_item.getAbsolutePath(), num_pages,"All",encrypt);
                        row_items.add(merge_item_obj);
                    }
                    importMergeItem(c, row_items);
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
    
    private boolean hasMergeItemFlavor(Transferable t) {
        DataFlavor[] flavors;
        flavors = t.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(MergeItemTransfer.MERGEITEMFLAVOUR)) {
                return true;
            }
        }
        return false;
    }
    
    
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (!(c instanceof JMergeTable)){return;}    
        cleanup(c, action == MOVE);
    }
    
    /**
     * Check if the item can be imported
     */    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(MergeItemTransfer.MERGEITEMFLAVOUR) || flavors[i].equals(DataFlavor.javaFileListFlavor)){
                return true;
            }
        }
        return false;
    }
    

}
