/*
 * Created on 02-Dec-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import javax.swing.JToggleButton.ToggleButtonModel;
/**
 * Model for the split radio buttons
 * @author Andrea Vacondio
 *
 */
public class JSplitRadioButtonModel extends ToggleButtonModel {

	private static final long serialVersionUID = -541344769909895211L;

	private String splitCommand;

	
	/**
	 * 
	 */
	public JSplitRadioButtonModel(String splitCommand) {
		super();
		this.splitCommand = splitCommand;
	}

	/**
	 * @return the splitCommand
	 */
	public String getSplitCommand() {
		return splitCommand;
	}

	/**
	 * @param splitCommand the splitCommand to set
	 */
	public void setSplitCommand(String splitCommand) {
		this.splitCommand = splitCommand;
	}
	
	
}
