/*
 * Created on 11-Oct-2009
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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.pdfsam.guiclient.business.ApplicationCloser;
import org.pdfsam.guiclient.gui.frames.JMainFrame;

/**
 * Listener to execute necessary operations on window closing event
 * @author Andrea Vacondio
 *
 */
public class MainWindowListener implements WindowListener {
	
	private ApplicationCloser closer;
	
	/**
	 * @param mainFrame
	 */
	public MainWindowListener(JMainFrame mainFrame) {
		super();
		this.closer = new ApplicationCloser(mainFrame);
	}

	public void windowActivated(WindowEvent e) {
			//empty on purpose
	}

	public void windowClosed(WindowEvent e) {
		//empty on purpose
	}

	public void windowClosing(WindowEvent e) {
		closer.saveGuiConfiguration();
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e) {
		//empty on purpose
	}

	public void windowDeiconified(WindowEvent e) {
		//empty on purpose
	}

	public void windowIconified(WindowEvent e) {
		//empty on purpose
	}

	public void windowOpened(WindowEvent e) {
		//empty on purpose
	}

}
