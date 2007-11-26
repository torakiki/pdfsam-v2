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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Listener for the exit action
 * @author a.vacondio
 */
public class ExitActionListener implements ActionListener{

	public static final String EXIT_COMMAND = "exit";
	
	public void actionPerformed(ActionEvent arg0) {
		if(arg0 != null){
			if(ExitActionListener.EXIT_COMMAND.equals(arg0.getActionCommand())){
				System.exit(1);
			}
		}
	}

}
