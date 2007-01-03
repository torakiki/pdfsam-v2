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
package it.pdfsam.plugin.split.listener;

import it.pdfsam.plugin.split.GUI.SplitMainGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
/**
 * Listener class used by JSplitRadioButton.
 * It's used to enable/disable JTextField binded to SPLIT and NSPLIT JSplitRadioButton.
 * 
 * @author Andrea Vacondio
 * @see it.pdfsam.plugin.split.component.JSplitRadioButton
 */
public class RadioListener implements ActionListener {

    public static final byte DISABLE_ALL = 0x00;
    public static final byte DISABLE_FIRST = 0x01;
    public static final byte DISABLE_SECOND = 0x02;
    
    private SplitMainGUI container;
    private JTextField first_text;
    private JTextField second_text;
    private byte policy;
  
/**
 * Constructor
 * @param main_container Reference to the container main panel to set te split options when action is cathced
 * @param first First JTextField of the radio group
 * @param second Second JTextField of the radio group
 * @param disable_policy Disable policy for the JTextFields
 */
    public RadioListener(SplitMainGUI main_container, JTextField first, JTextField second, byte disable_policy){
        container = main_container;
        first_text = first;
        second_text = second;
        policy = disable_policy;
    }
/**
 * When radio button is selected, JTextFields are enabled or disabled based on the policy
 */    
    public void actionPerformed(ActionEvent e) {
        
       switch(policy){
       
       case RadioListener.DISABLE_ALL:
           if(first_text != null) first_text.setEnabled(false);
           if(second_text != null) second_text.setEnabled(false);
           break;
       
       case RadioListener.DISABLE_FIRST:
           if(first_text != null) first_text.setEnabled(false);
           if(second_text != null){
               second_text.setEnabled(true);
               second_text.requestFocus();
           }
           break;
               
       case RadioListener.DISABLE_SECOND:
           if(first_text != null){
               first_text.setEnabled(true);
               first_text.requestFocus();
           }
           if(second_text != null) second_text.setEnabled(false);
           break;
       
       default:
           if(first_text != null) first_text.setEnabled(false);
           if(second_text != null) second_text.setEnabled(false);
           
       } 
       //sets the split type
       if (container instanceof it.pdfsam.plugin.split.GUI.SplitMainGUI){
           container.setSplitType(e.getActionCommand());
       }

    }

}
