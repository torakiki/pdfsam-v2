/*
 * Created on 02JUL-08
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
package org.pdfsam.guiclient.commons.business.listeners.mediators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pdfsam.guiclient.business.PagesWorker;

/**
 * @author Andrea Vacondio
 *
 */
public class PagesActionsMediator implements ActionListener {

	private PagesWorker pageWorker;
	
	
	/**
	 * @param pageWorker
	 */
	public PagesActionsMediator(PagesWorker pageWorker) {
		super();
		this.pageWorker = pageWorker;
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if(e!=null){
			pageWorker.execute(e.getActionCommand());
		}
	}

}
