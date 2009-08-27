/*
 * Created on 22-Mar-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
/**
 * Escape button hides the frame
 * @author Andrea Vacondio
 *
 */
public class EscapeKeyListener implements KeyListener {


	private JFrame frame;
	
	/**
	 * @param frame
	 */
	public EscapeKeyListener(JFrame frame) {
		super();
		this.frame = frame;
	}

	public void keyPressed(KeyEvent e) {
		 if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			 frame.setVisible(false);
        }		 
	}
	
	public void keyReleased(KeyEvent e) {					
	}

	public void keyTyped(KeyEvent e) {
	}

}
