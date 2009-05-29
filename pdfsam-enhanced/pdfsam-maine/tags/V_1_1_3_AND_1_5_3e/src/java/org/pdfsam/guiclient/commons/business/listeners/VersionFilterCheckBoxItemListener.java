/*
 * Created on 20-Oct-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
/**
 * ItemListener used to disable or enable items in JPdfVersionCombo based on the version filter
 * @author Andrea Vacondio
 *
 */
public class VersionFilterCheckBoxItemListener implements ItemListener {

	private JPdfVersionCombo versionCombo = null;
	private Integer versionFilter = null;
	
	
	
	/**
	 * @param versionCombo
	 * @param versionFilter
	 */
	public VersionFilterCheckBoxItemListener(JPdfVersionCombo versionCombo, Integer versionFilter) {
		super();
		this.versionCombo = versionCombo;
		this.versionFilter = versionFilter;
	}



	public void itemStateChanged(ItemEvent e) {
		if(versionCombo != null){
			if(ItemEvent.SELECTED == e.getStateChange()){
	        	versionCombo.addVersionFilter(versionFilter);
	        }else if(e.getStateChange() == ItemEvent.DESELECTED){
	      	  	versionCombo.removeVersionFilter(versionFilter);
	        }
        }
	}

}
