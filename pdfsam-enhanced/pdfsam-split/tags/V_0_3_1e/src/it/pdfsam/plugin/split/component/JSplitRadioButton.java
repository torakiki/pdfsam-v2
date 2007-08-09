/*
 * Created on 25-feb-2006
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
package it.pdfsam.plugin.split.component;

import javax.swing.JRadioButton;

/**
 * This component is used to get the split type selected by the user
 * @author Andrea Vacondio
 * @see it.pdfsam.console.MainConsole
 *
 */
public class JSplitRadioButton extends JRadioButton {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String split_command;
    
    public JSplitRadioButton(String split_mode) {
        super();
        this.split_command = split_mode;
        this.setActionCommand(split_mode);
    }

    /**
     * @return Returns the split_command.
     */
    public String getSplitCommand() {
        return split_command;
    }

    /**
     * @param split_command The split_command to set.
     */
    protected void setSplitCommand(String split_command) {
        this.split_command = split_command;
    }


}
