/*
 * Created on 04-Jul-2006
 *
 */
package org.pdfsam.guiclient.commons.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.PdfLoader;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.models.PdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.panels.JStatusPanel;
import org.pdfsam.i18n.GettextResource;
   
/**
* Listener class used by PdfSelectionTable buttons
* @author Andrea Vacondio
*/
public class PdfSelectionTableActionListener implements ActionListener{
    	
	private static final Logger log = Logger.getLogger(JStatusPanel.class.getPackage().getName());
    
	public static final String ADD = "add";
	public static final String ADDSINGLE = "addSingle";
    public static final String MOVE_UP = "moveUp";
    public static final String MOVE_DOWN = "moveDown";
    public static final String CLEAR = "clear";        
    public static final String REMOVE = "remove";
    /**
     * reference to the merge table
     */
    private JPdfSelectionPanel panel;
    private PdfLoader loader;

    /**
     * Constructor
     * @param mainTable
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
    				 ((PdfSelectionTableModel) mainTable.getModel()).clearData();
    			}else if (ADD.equals(e.getActionCommand())){
    				loader.showFileChooserAndAddFiles();
    			}else if (ADDSINGLE.equals(e.getActionCommand())){
    				loader.showFileChooserAndAddFiles(true);
    			}else{
    				int[] selectedRows = mainTable.getSelectedRows();
    				if (selectedRows.length > 0){
    					if(MOVE_UP.equals(e.getActionCommand())){
    						((PdfSelectionTableModel) mainTable.getModel()).moveUpRows(selectedRows);
    						if (selectedRows[0] > 0){
    							mainTable.setRowSelectionInterval(selectedRows[0]-1, selectedRows[selectedRows.length-1]-1);
	                        }
    					}else if(MOVE_DOWN.equals(e.getActionCommand())){
    						((PdfSelectionTableModel) mainTable.getModel()).moveDownRows(selectedRows);
    						if (selectedRows[selectedRows.length-1] < (((PdfSelectionTableModel) mainTable.getModel()).getRowCount()-1)){
    							 mainTable.setRowSelectionInterval(selectedRows[0]+1, selectedRows[selectedRows.length-1]+1);   
	                        }
    					}else if(REMOVE.equals(e.getActionCommand())){
    						((PdfSelectionTableModel) mainTable.getModel()).deleteRows(selectedRows);
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
