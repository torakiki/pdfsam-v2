/*
 * Created on 27-Dec-2007
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
package org.pdfsam.guiclient.commons.renderers;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Renderer to show red background in rows loaded with errors
 * @author Andrea Vacondio
 *
 */
public class JPdfSelectionTableRenderer extends JLabel implements TableCellRenderer{

	private static final long serialVersionUID = -4780112050203181493L;
	
	private boolean fullAccessRequired = true;
	
	public JPdfSelectionTableRenderer() {
	}

	/**
	 * @param fullAccessRequired
	 */
	public JPdfSelectionTableRenderer(boolean fullAccessRequired) {
		super();
		this.fullAccessRequired = fullAccessRequired;
	}

	/**
	 * @return the fullAccessRequired
	 */
	public boolean isFullAccessRequired() {
		return fullAccessRequired;
	}

	/**
	 * @param fullAccessRequired the fullAccessRequired to set
	 */
	public void setFullAccessRequired(boolean fullAccessRequired) {
		this.fullAccessRequired = fullAccessRequired;
	}


	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		setOpaque(true);
		setIcon(null);
		setFont(table.getFont());
		setToolTipText(null);
		boolean loadedWithErrors = ((AbstractPdfSelectionTableModel)table.getModel()).getRow(row).isLoadedWithErrors();
		boolean syntaxErrors = ((AbstractPdfSelectionTableModel)table.getModel()).getRow(row).isSyntaxErrors();
		boolean fullPermission = ((AbstractPdfSelectionTableModel)table.getModel()).getRow(row).isFullPermission();
		boolean encrypted = ((AbstractPdfSelectionTableModel)table.getModel()).getRow(row).isEncrypted();
		//rowheader
		if (column == AbstractPdfSelectionTableModel.ROW_NUM){
			setFont(table.getTableHeader().getFont());
			setBackground(table.getTableHeader().getBackground());
			setForeground(table.getTableHeader().getForeground());			
		}else{
			if(isSelected){
	          setForeground(table.getSelectionForeground()); 
	          setBackground(table.getSelectionBackground());
	        }
	        else{
	          setForeground(table.getForeground());
	          setBackground(table.getBackground()); 
	        }
			if(loadedWithErrors){
				setBackground(new Color(222,189,189));
				if (column == AbstractPdfSelectionTableModel.FILENAME){
					setIcon(new ImageIcon(this.getClass().getResource("/images/erroronload.png")));
				}
			}else if(syntaxErrors || (fullAccessRequired && !fullPermission)){
				setBackground(Color.YELLOW);
			}
		}
		//value
		if (column == AbstractPdfSelectionTableModel.PASSWORD){
			if(value != null && value.toString().length()>0){
				setText("**********"); 
			}else{
				setText("");
			}	
		}else{
			if(value != null){
				setText(value.toString()); 
			}else{
				setText("");
			}		
		}
		//encrypt icon
		if (column == AbstractPdfSelectionTableModel.FILENAME){
			if(encrypted){
			   setIcon(new ImageIcon(this.getClass().getResource("/images/encrypted.png")));
		   	}
		}
		//focus
		if(column == AbstractPdfSelectionTableModel.ROW_NUM){
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		}else{
			if (hasFocus) {
			    setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
			} else {
			    setBorder(new EmptyBorder(1, 1, 1, 1));
			}     
		}   
		
		//tooltip messages
		if(syntaxErrors || (fullAccessRequired && !fullPermission) || loadedWithErrors){
			String toolTip = "<html><body>";
			if(syntaxErrors){
				toolTip += "<b>"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"The cross reference table cantained some error and has been rebuilt")+".";				
			}
			if(loadedWithErrors){
				toolTip += "<b>"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"An error occured while loading the document")+".";
			}
    		toolTip +="</body></html>";
			setToolTipText(toolTip);
		}
		
        return this;		
	}

}
