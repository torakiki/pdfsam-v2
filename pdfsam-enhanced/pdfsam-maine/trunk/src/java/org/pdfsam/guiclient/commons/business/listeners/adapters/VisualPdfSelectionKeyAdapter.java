/*
 * Created on 03-JUL-08
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
package org.pdfsam.guiclient.commons.business.listeners.adapters;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.pdfsam.guiclient.business.PagesWorker;

/**
 * KeyAdapter for the thumbnails JList
 * @author Andrea Vacondio
 *
 */
public class VisualPdfSelectionKeyAdapter extends KeyAdapter {
	
	private PagesWorker worker;

	/**
	 * @param worker
	 */
	public VisualPdfSelectionKeyAdapter(PagesWorker worker) {
		super();
		this.worker = worker;
	}
	
	 public void keyPressed(KeyEvent e) {
         if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_UP)){
         	worker.execute(PagesWorker.MOVE_UP);
         }
         else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_DOWN)){
        	 worker.execute(PagesWorker.MOVE_DOWN);
         }
         else if((e.getKeyCode() == KeyEvent.VK_DELETE)){
        	 worker.execute(PagesWorker.REMOVE);
         }
         else if((e.isControlDown())&& (e.getKeyCode() == KeyEvent.VK_Z)){
        	 worker.execute(PagesWorker.UNDELETE);
         }
     }
}
