/*
 * Created on 26-May-2006
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * Class used to transfer MergeItem in drag and drop process.
 * @author Andrea Vacondio
 * @see it.pdfsam.plugin.merge.type.TableTransferHandler
 * 
 */
public class CoverFooterItemTransfer implements Transferable {
    
    public static DataFlavor MERGEITEMFLAVOUR;
    //public static DataFlavor FILELISTFLAVOUR = DataFlavor.javaFileListFlavor;
    
    static {
            try {
                MERGEITEMFLAVOUR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
           }
    protected JComponent mit_source;
    protected ArrayList mit_data;

    public CoverFooterItemTransfer(JComponent source, ArrayList data) {
        mit_source = source;
        mit_data = data;
    }
    
    /**
     * @param flavor the DataFlavour
     * @return this transferable
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)){
            throw new UnsupportedFlavorException(flavor);
        }
        return this;
    }
    
    /**
     * @return true if flavour is supported 
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (MERGEITEMFLAVOUR.equals(flavor));
    }
    
    /**
     * @return DataFlavours of this transferable
     */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { MERGEITEMFLAVOUR };
    }
    
    /**
     * 
     * @return source component
     */
    public JComponent getSource() {
        return mit_source;
    }
    
    /**
     * 
     * @return data to transfer
     */
    public ArrayList getData() {
        return mit_data;
    }
}

