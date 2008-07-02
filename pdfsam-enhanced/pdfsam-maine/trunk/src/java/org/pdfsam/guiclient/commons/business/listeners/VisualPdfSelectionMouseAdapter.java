/*
 * Created on 30-Jun-08
 * Copyright (C) 2008 by Andrea Vacondio.
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.pdfsam.guiclient.commons.business.PagesWorker;

/**
 * MouseAdapter for the popup menu
 * @author Andrea Vacondio
 *
 */
public class VisualPdfSelectionMouseAdapter extends MouseAdapter {

    private PagesWorker worker;
    private String action;    
    
    /**
	 * @param action
	 * @param worker
	 */
	public VisualPdfSelectionMouseAdapter(String action, PagesWorker worker) {
		super();
		this.worker = worker;
		this.action = action;
	}


	public void mouseReleased(MouseEvent e) {
		worker.execute(action);
    }

}
