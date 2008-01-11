/*
 * Created on 07-Mar-2006
 *
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

import java.awt.Container;

import javax.swing.JTable;
import javax.swing.JViewport;
/**
 * Code snippet found on forum.java.sun by pam11 .
 * @author Andrea Vacondio
 *
 */
public class JPdfSelectionTable extends JTable {

	private static final long serialVersionUID = -6501303401258684529L;

    public boolean getScrollableTracksViewportWidth() {
    	boolean retVal = super.getScrollableTracksViewportWidth();
        if (autoResizeMode == AUTO_RESIZE_OFF) {
            Container parent = getParent();
            if (parent instanceof JViewport) {
                    retVal =  (parent.getSize().getWidth() > getPreferredSize().getWidth());
            }
        }
        return retVal;
    
    }
}
