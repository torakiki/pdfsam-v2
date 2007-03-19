/*
 * Created on 26-Dec-2006
 * Copyright (C) 2006 by Andrea Vacondio.
 * Thanks to john_greenhow on forum.java.sun.com  
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
package it.pdfsam.panels;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Panel used in JSplash screen to display background image 
 * @author a.vacondio
 */
public class JBackgroundedPanel extends JPanel {

	private static final long serialVersionUID = -1882976522867446997L;
	private Image background;
	
	/**
	 * A constructor to build and initialise your panel. 
	 * @param resourceName
	 */
	public JBackgroundedPanel(String resourceName) {
		try{
			if(resourceName != null && resourceName.length()>0){
				background = new ImageIcon(this.getClass().getResource(resourceName)).getImage();
				setOpaque(false);
			}else{
				background = null;
			}
		}catch(Exception e){
			background = null;
		}
	}
	
	/**
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		if(background != null){
			g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}		
		super.paint(g);			
	}
}
