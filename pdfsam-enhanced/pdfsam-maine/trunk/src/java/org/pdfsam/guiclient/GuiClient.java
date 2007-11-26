/*
 * Created on 08-Nov-2007
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
package org.pdfsam.guiclient;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.gui.frames.JMainFrame;
/**
 * GUI Client for the console
 * @author a.vacondio
 *
 */
public class GuiClient extends JFrame {

	private static final long serialVersionUID = -3608998690519362986L;

	private static final Logger log = Logger.getLogger(GuiClient.class.getPackage().getName());	
    
    public static final String AUTHOR = "Andrea Vacondio";
	public static final String NAME = "PDF Split and Merge enhanced";
	public static final String UNIXNAME = "pdfsam";
	public static final String VERSION = "1.4.0e alpha"; 
	
	private static JMainFrame clientGUI;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {			
			clientGUI = new JMainFrame();
			clientGUI.setVisible(true);
		} catch (Exception e) {
			log.fatal(e);
		}
	}

}
