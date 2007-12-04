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
package org.pdfsam.guiclient.plugins.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.pdfsam.guiclient.configuration.Configuration;
/**
 * Abstract class for plugin
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractPlugablePanel extends JPanel implements Plugable{
	
    private String panelIcon = "";
    private Configuration config;
    
    public AbstractPlugablePanel(){
    	config = Configuration.getInstance();          
		init();
	}
    
    public Icon getIcon() {
		ImageIcon icon = null;
        try{
            if(panelIcon != null && (panelIcon.trim().length() >0)){
            	icon =  new ImageIcon(this.getClass().getResource(panelIcon));
        	}
        }catch (Exception e){
            icon =  null;            
        }
        return icon;
    }
	
    /**
     * Sets the icon resource name for this plugin
     * @param panelIcon
     */
    public void setPanelIcon(String panelIcon) {
		this.panelIcon = panelIcon;
	}        
    
    /**
	 * @return the config
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
     * Common initialization
     */
    private void init(){
    	setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		setFocusable(false);    	
    }
}