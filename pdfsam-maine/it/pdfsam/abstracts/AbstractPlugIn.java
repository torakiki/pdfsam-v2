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
package it.pdfsam.abstracts;

import it.pdfsam.interfaces.PlugablePanel;

import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * Abstract class for plugin
 * @author a.vacondio
 *
 */
public abstract class AbstractPlugIn extends AbstractLogWriter implements PlugablePanel{

    
    private String panel_icon = "";


    public Icon getIcon() {
		ImageIcon icon = null;
        try{
            if(panel_icon != null && !panel_icon.equals("")){
            	icon =  new ImageIcon(this.getClass().getResource(panel_icon));
        	}
			return icon;
        }catch (Exception e){
            return null;            
        }
    }
	
    public void setPanelIcon(String panel_icon) {
		this.panel_icon = panel_icon;
	} 
}