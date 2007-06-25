/*
 * Created on 23-Mar-2006
 *
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
package it.pdfsam.listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/** 
 * Implements KeyListener to add to buttons. When "Enter" is pressed, it calls a click programmatically
 * @author Andrea Vacondio
 * @see java.awt.event.KeyListener
 * @see java.awt.event.KeyAdapter
 *
 */
public class EnterDoClickListener extends KeyAdapter {
    /**
     * Button to click
     */
    private JButton button;
    
    /**
     * Constructor
     * 
     * @param button_to_click Button to click
     */
    public EnterDoClickListener(JButton button_to_click){
        button = button_to_click;   
    }
    
    /**
     * Invoked when a key has been pressed.
     */     
    public void keyPressed(KeyEvent e) {
        if (button != null){
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                button.doClick();
            }
        }
    }


    /**
     * @return Returns the button.
     */
    public JButton getButton() {
        return button;
    }


    /**
     * @param button The button to set.
     */
    public void setButton(JButton button) {
        this.button = button;
    }
}
