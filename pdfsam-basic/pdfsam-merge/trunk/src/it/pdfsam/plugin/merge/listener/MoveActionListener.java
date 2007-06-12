/*
 * Created on 04-Jul-2006
 *
 */
package it.pdfsam.plugin.merge.listener;

import it.pdfsam.plugin.merge.GUI.MergeMainGUI;
import it.pdfsam.plugin.merge.component.JMergeTable;
import it.pdfsam.plugin.merge.model.MergeTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
    /**
     * Listener class used by remove_button
     * 
     * @author Andrea Vacondio
     * @see it.pdfsam.plugin.merge.GUI.MergeMainGUI
     */
    public class MoveActionListener implements ActionListener{
        public static final byte MOVE_UP = 0x01;
        public static final byte MOVE_DOWN = 0x02;
        
    /**
     * reference to the merge table
     */
    private JMergeTable merge_table;
    /**
      * reference to the main GUI
      */
    private MergeMainGUI main_gui;
    /**
     * Action type
     */
    private byte type;

    /**
     * Constructor
     * @param merge_table
     * @param main_gui
     */
    public MoveActionListener(JMergeTable merge_table, MergeMainGUI main_gui, byte action_type) {
         super();
         this.main_gui = main_gui;
         this.merge_table = merge_table;
         this.type = action_type;
    }
        
    /**
     * if the button is pressed it moves up or down items in the table
     */
    public void actionPerformed(ActionEvent e) {
        if ((main_gui == null) || (merge_table == null)) return;
        int[] selected_rows = merge_table.getSelectedRows();
        if ( selected_rows != null){
            try{
                if ( selected_rows.length > 0){
                    //Check the type of movement
                    switch (type){

                    case MOVE_UP:
                        // ((MergeTableModel) merge_table.getModel()).moveUpRow(selected_row);
                        ((MergeTableModel) merge_table.getModel()).moveUpRows(selected_rows);
                        merge_table.setRowSelectionInterval(selected_rows[0]-1, selected_rows[selected_rows.length-1]-1);
                        break;
                    
                    case MOVE_DOWN:
                        ((MergeTableModel) merge_table.getModel()).moveDownRows(selected_rows);
                        merge_table.setRowSelectionInterval(selected_rows[0]+1, selected_rows[selected_rows.length-1]+1);                        
                        break;
                    }
                }
            }
            catch (IndexOutOfBoundsException ioobe){
                main_gui.fireLogActionPerformed("Error: "+ioobe.getMessage(), "FF0000"); 
            }
            catch (IllegalArgumentException iae){
            }
        }
     }
}
