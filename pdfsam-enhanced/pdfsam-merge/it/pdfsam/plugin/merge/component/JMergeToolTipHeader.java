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

package it.pdfsam.plugin.merge.component;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * Used to show tooltip on Merge Table Header
 * @author  Andrea Vacondio
 * @see it.pdfsam.plugin.merge.component.JMergeTable
 */

public class JMergeToolTipHeader extends JTableHeader {


		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/**
         * String array of the tooltips
         */
        String[] tool_tips = {"",
                "",
                "",
                ""};
        

        public JMergeToolTipHeader(TableColumnModel model) {
          super(model);
        }

        /**
         * @return tool tip string to show
         */
        public String getToolTipText(MouseEvent e) {
          int col = columnAtPoint(e.getPoint());
          int modelCol = getTable().convertColumnIndexToModel(col);
          String ret_val;
          try {
              ret_val = tool_tips[modelCol];
          } 
          catch (Exception ex) {
              ret_val = "";
          } 
          if (ret_val.length() < 1) {
              ret_val = super.getToolTipText(e);
          }
          return ret_val;
        }
        /**
         * Set the tool tip string
         * @param tool_tips_strings
         */
        public void setToolTipStrings(String[] tool_tips_strings) {
          this.tool_tips = tool_tips_strings;
        }
      }