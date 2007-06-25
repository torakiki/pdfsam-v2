/*
 * Created on 17-Oct-2006
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
package it.pdfsam.renders;

import it.pdfsam.types.ListItem;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
/**
 * Render for ListItem combo
 * @author Andrea Vacondio
 * @see it.pdfsam.types.ListItem
 */
public class JComboListItemRender extends JLabel implements ListCellRenderer {
		/**
	 * 
	 */
	private static final long serialVersionUID = 5557011327195948679L;
		int selectedIndex;

		/*Costruttore*/
	    public JComboListItemRender() {
	        setOpaque(true);
	        selectedIndex = 0;
	    }

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			if (isSelected) {
		            setBackground(list.getSelectionBackground());
		            setForeground(list.getSelectionForeground());
		            selectedIndex = index;
		        }			
				else {
		            setBackground(list.getBackground());
		            setForeground(list.getForeground());
		        }
				if (value != null) {
					String text = ((ListItem)value).getValue();
					setText(text);
				}
				else {
					setText("");
				}
		     return this;
		 }	

	}

