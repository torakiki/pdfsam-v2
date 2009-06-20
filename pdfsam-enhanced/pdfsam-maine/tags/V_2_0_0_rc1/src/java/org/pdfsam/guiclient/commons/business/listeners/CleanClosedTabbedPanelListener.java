/*
 * Created on 07-Oct-2008
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

import java.awt.Component;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.CloseableTabbedPane;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Clean the creator to stop thumbnails threads
 * @author Andrea Vacondio
 *
 */
public class CleanClosedTabbedPanelListener implements CloseableTabbedPaneListener {

	private static final Logger log = Logger.getLogger(CleanClosedTabbedPanelListener.class.getPackage().getName());
	
	private CloseableTabbedPane panel;
		
	/**
	 * @param panel
	 */
	public CleanClosedTabbedPanelListener(CloseableTabbedPane panel) {
		super();
		this.panel = panel;
	}


	public boolean closeTab(int tabIndexToClose) {
		try{
			Component currentPanel = panel.getComponentAt(tabIndexToClose);
			if(currentPanel!=null){
				((JVisualPdfPageSelectionPanel)currentPanel).getPdfLoader().cleanCreator();
			}
		}catch(Exception e){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to clean pdf loader"), e);
		}
		return true;
	}

}
