/*
 * Created on 20-Dec-2007
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
package org.pdfsam.guiclient.commons.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
/**
 * ActionListener used to disable or enable items in JPdfVersionCombo
 * @author Andrea Vacondio
 *
 */
public class CompressCheckBoxActionListener implements ActionListener {

	private JPdfVersionCombo versionCombo = null;
	
	/**
	 * @param versionCombo version Combo 
	 */
	public CompressCheckBoxActionListener(JPdfVersionCombo versionCombo) {
		super();
		this.versionCombo = versionCombo;
	}

	public void actionPerformed(ActionEvent e) {
		if(versionCombo != null){
			AbstractButton abstractButton = (AbstractButton) e.getSource();
	        if(abstractButton.getModel().isSelected()){ 
	        	versionCombo.removeLowerVersionItems(Integer.parseInt(""+AbstractParsedCommand.VERSION_1_5));
	        }else{
	      	  	versionCombo.enableAll();
	        }
        }
	}

}
