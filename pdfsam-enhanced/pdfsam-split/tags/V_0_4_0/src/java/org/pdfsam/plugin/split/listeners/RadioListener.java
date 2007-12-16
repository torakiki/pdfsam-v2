/*
 * Created on 20-Feb-2006
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
package org.pdfsam.plugin.split.listeners;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pdfsam.plugin.split.GUI.SplitMainGUI;
import org.pdfsam.plugin.split.components.JSplitRadioButton;
/**
 * Listener class used by JSplitRadioButton.
 * It's used to enable/disable JTextField binded to SPLIT and NSPLIT JSplitRadioButton.
 * 
 * @author Andrea Vacondio
 * @see org.pdfsam.plugin.split.components.JSplitRadioButton
 */
public class RadioListener implements ActionListener {

    public static final String DISABLE_ALL = "disableAll";
    public static final String ENABLE_FIRST = "enableFirst";
    public static final String ENABLE_SECOND = "enableSecond";
    public static final String ENABLE_THIRD = "enableThird";
    
    private Component first;
    private Component second;
    private Component third;
    private SplitMainGUI container;
  
/**
 * Constructor
 * @param first First JTextField of the radio group
 * @param second Second JTextField of the radio group
 */
    public RadioListener(SplitMainGUI container, Component first, Component second, Component third){
        this.first = first;
        this.second = second;
        this.third = third;
        this.container = container;
    }
    
	/**
	 * When radio button is selected, JTextFields are enabled or disabled based on event action command
	 */    
    public void actionPerformed(ActionEvent e) {
    	if(e != null && e.getActionCommand()!=null && e.getActionCommand().length()>0){
    		if(DISABLE_ALL.equals(e.getActionCommand())){
    			first.setEnabled(false);
    			second.setEnabled(false);
    			third.setEnabled(false);
    		}else if(ENABLE_FIRST.equals(e.getActionCommand())){
    			first.setEnabled(true);
    			second.setEnabled(false);
    			third.setEnabled(false);
                first.requestFocus();
    		}else if(ENABLE_SECOND.equals(e.getActionCommand())){
    			first.setEnabled(false);
    			second.setEnabled(true);
    			third.setEnabled(false);
    			second.requestFocus();
    		}else if(ENABLE_THIRD.equals(e.getActionCommand())){
    			first.setEnabled(false);
    			second.setEnabled(false);
    			third.setEnabled(true);
    			third.requestFocus();
    		} 
    	}  
    	container.setSplitType(((JSplitRadioButton)e.getSource()).getSplitCommand());
    }

}
