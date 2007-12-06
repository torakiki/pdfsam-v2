/*
 * Created on 14-Mar-2006
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
package org.pdfsam.guiclient.commons.components;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
/**
 * Used to show tooltip on JPdfSelectionTable Header
 * @author  Andrea Vacondio
 */
public class JPdfSelectionToolTipHeader extends JTableHeader {
  
	private static final long serialVersionUID = -5265133364886971725L;
	
	private String[] toolTips = {"","","","",""};
    

    public JPdfSelectionToolTipHeader(TableColumnModel model) {
      super(model);
    }

    /**
     * @return tool tip string to show
     */
    public String getToolTipText(MouseEvent e) {
        String retVal;
        int col = columnAtPoint(e.getPoint());
        int modelCol = getTable().convertColumnIndexToModel(col);
	    try {
	    	retVal = toolTips[modelCol];
	    } 
	    catch (Exception ex) {
	    	retVal = "";
	    } 
	    return retVal;
    }
    /**
     * Set the tool tip string
     * @param toolTips
     */
    public void setToolTips(String[] toolTips) {
      this.toolTips = toolTips;
    }
}
