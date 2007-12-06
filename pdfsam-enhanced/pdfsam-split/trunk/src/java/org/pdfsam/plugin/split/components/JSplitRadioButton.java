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
package org.pdfsam.plugin.split.components;

import javax.swing.JRadioButton;

/**
 * This component is used to get the split type selected by the user
 * @author Andrea Vacondio
 * @see it.pdfsam.console.MainConsole
 *
 */
public class JSplitRadioButton extends JRadioButton {

	private static final long serialVersionUID = 5210692149732974444L;

	public JSplitRadioButton(String splitCommand) {
        super();
        this.setModel(new JSplitRadioButtonModel(splitCommand));
    }

	public String getSplitCommand(){
		return ((JSplitRadioButtonModel)this.getModel()).getSplitCommand();
	}
}
