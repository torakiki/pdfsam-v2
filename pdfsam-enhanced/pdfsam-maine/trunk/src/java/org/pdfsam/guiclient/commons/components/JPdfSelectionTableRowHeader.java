package org.pdfsam.guiclient.commons.components;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
/**
 * Header for the selection table
 * @author Andrea Vacondio
 *
 */
public class JPdfSelectionTableRowHeader extends JTable {

	private static final long serialVersionUID = 9074507882735673054L;
	private JTable mainTable;
	 
	public JPdfSelectionTableRowHeader(JTable table){
		super();
		mainTable = table;
		setAutoCreateColumnsFromModel(false);
		setModel(mainTable.getModel());
        //setSelectionModel(mainTable.getSelectionModel());
		setAutoscrolls(false);
		setFocusable(false);
		setRowSelectionAllowed(false);
 
		addColumn(new TableColumn());
		getColumnModel().getColumn(0).setCellRenderer(getDefaultRenderer(Integer.class));
		getColumnModel().getColumn(0).setPreferredWidth(20);
		setPreferredScrollableViewportSize(getPreferredSize());
	}
 
	public boolean isCellEditable(int row, int column){
		return false;
	}
 
	public Object getValueAt(int row, int column){
		return new Integer(row + 1);
	}
 
	public int getRowHeight(int row){
		return mainTable.getRowHeight();
	}
	
	public boolean isFocusTraversable(){
		return false;
	}
}
