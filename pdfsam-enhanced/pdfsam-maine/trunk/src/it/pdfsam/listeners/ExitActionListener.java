/*
 * Created on 26-Dec-2006
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
/**
 * Listener for the exit action
 * @author a.vacondio
 */
public class ExitActionListener implements ActionListener, Serializable {

	private static final long serialVersionUID = 5383151213336454085L;
	public static final String EXIT_COMMAND = "exit";
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0 != null){
			if(arg0.getActionCommand().equals(ExitActionListener.EXIT_COMMAND)){
				System.exit(1);
			}
		}
	}

}
