/*
 * Created on 14-Nov-2007
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
package org.pdfsam.guiclient.commons.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.PdfLoader;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;
   
/**
* Listener class used by PdfSelectionTable buttons
* @author Andrea Vacondio
*/
public class PdfSelectionTableActionListener implements ActionListener{
    	
	private static final Logger log = Logger.getLogger(PdfSelectionTableActionListener.class.getPackage().getName());
    
	public static final String ADD = "add";
	public static final String ADDSINGLE = "addSingle";
    public static final String MOVE_UP = "moveUp";
    public static final String MOVE_DOWN = "moveDown";
    public static final String CLEAR = "clear";        
    public static final String REMOVE = "remove";
    public static final String RELOAD = "reload";
    /**
     * reference to the merge table
     */
    private JPdfSelectionPanel panel;
    private PdfLoader loader;

  
    /**
     * @param panel Selection panel
     * @param loader PdfLoader
     */
    public PdfSelectionTableActionListener(JPdfSelectionPanel panel, PdfLoader loader) {    	
         this.panel = panel;
         this.loader = loader;
    }
        
    /**
     * if the button is pressed it moves up or down items in the table
     */
    public void actionPerformed(ActionEvent e) {
    	JPdfSelectionTable mainTable = panel.getMainTable();
        if (e != null && mainTable != null){        	
    		try{
    			if(CLEAR.equals(e.getActionCommand())){
    				 ((AbstractPdfSelectionTableModel) mainTable.getModel()).clearData();
    			}else if (ADD.equals(e.getActionCommand())){
    				loader.showFileChooserAndAddFiles();
    			}else if (ADDSINGLE.equals(e.getActionCommand())){
    				loader.showFileChooserAndAddFiles(true);
    			}else{
    				int[] selectedRows = mainTable.getSelectedRows();
    				if (selectedRows.length > 0){
    					if(MOVE_UP.equals(e.getActionCommand())){
    						((AbstractPdfSelectionTableModel) mainTable.getModel()).moveUpRows(selectedRows);
    						if (selectedRows[0] > 0){
    							mainTable.setRowSelectionInterval(selectedRows[0]-1, selectedRows[selectedRows.length-1]-1);
	                        }
    					}else if(MOVE_DOWN.equals(e.getActionCommand())){
    						((AbstractPdfSelectionTableModel) mainTable.getModel()).moveDownRows(selectedRows);
    						if (selectedRows[selectedRows.length-1] < (((AbstractPdfSelectionTableModel) mainTable.getModel()).getRowCount()-1)){
    							 mainTable.setRowSelectionInterval(selectedRows[0]+1, selectedRows[selectedRows.length-1]+1);   
	                        }
    					}else if(REMOVE.equals(e.getActionCommand())){
    						((AbstractPdfSelectionTableModel) mainTable.getModel()).deleteRows(selectedRows);
    					}else if (RELOAD.equals(e.getActionCommand())){
    						for(int i=0; i<selectedRows.length; i++){
    							PdfSelectionTableItem row = ((AbstractPdfSelectionTableModel) mainTable.getModel()).getRow(selectedRows[i]);
    							loader.reloadFile(row.getInputFile(), row.getPassword(), row.getPageSelection(), selectedRows[i]);
    						}
    					}
    				}
    			}
            }
            catch (Exception ex){
                log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "),ex); 
            }
        }
	}

}
