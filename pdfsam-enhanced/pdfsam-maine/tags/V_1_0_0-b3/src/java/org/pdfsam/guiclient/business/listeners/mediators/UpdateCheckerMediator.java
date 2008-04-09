/*
 * Created on 27-Feb-2008
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
package org.pdfsam.guiclient.business.listeners.mediators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.panels.JStatusPanel;
import org.pdfsam.guiclient.updates.UpdateManager;
import org.pdfsam.i18n.GettextResource;
/**
 * Update checker mediator
 * @author Andrea Vacondio
 */
public class UpdateCheckerMediator implements ActionListener {

	private JStatusPanel statusPanel = null;
	private final UpdateChecker updateChecker = new UpdateChecker();
	private Thread t = null;
	
	public UpdateCheckerMediator(JStatusPanel statusPanel){
		this.statusPanel = statusPanel;
		t = new Thread(updateChecker);
	}
	
	public void actionPerformed(ActionEvent e) {
		checkForUpdates();
	}
	
	/**
	 * Run the runnable to check if an update is available after a delay
	 * @param delay 
	 * @param forceRecheck tells if a complete check have to be done.
	 */
	public void checkForUpdates(long delay, boolean forceRecheck){
		updateChecker.setDelay(delay);
		updateChecker.setForceRecheck(forceRecheck);
		if(!t.isAlive()){
			t = new Thread(updateChecker);
			t.start();
		}
	}
	
	/**
	 * Run the runnable to check if an update is available
	 */
	public void checkForUpdates(){
		checkForUpdates(0, true);
	}
	
	/**
	 * Runnable that checks for updates
	 * @author Andrea Vacondio
	 */
	private class UpdateChecker implements Runnable{
		
		private Logger log = null;
		private long delay = 0;
		private boolean forceRecheck = false;
		private UpdateManager updateManager = null;
		private final String destinationUrl = "http://www.pdfsam.org/check-version.php"; 
		
		
		/**
		 * @param delay the delay to set
		 */
		public void setDelay(long delay) {
			this.delay = delay;
		}		

		/**
		 * @param forceRecheck the forceRecheck to set
		 */
		public void setForceRecheck(boolean forceRecheck) {
			this.forceRecheck = forceRecheck;
		}

		/**
		 * check for a new version available and, if there is one, updates the status bar.
		 */
		public void run() {
			try{ 
		 		Thread.sleep(delay);
		 		URL url = new URL(destinationUrl+"?version="+URLEncoder.encode(GuiClient.getVersionType(),"UTF-8")+"&remoteversion="+URLEncoder.encode(GuiClient.getVersion(),"UTF-8"));
		 		if(updateManager == null){
		 			updateManager = new UpdateManager(url);
		 		}
		 		updateManager.checkForNewVersion(forceRecheck);
		 		if(updateManager.isNewVersionAvailable()){
		 			getLogger().info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"New version available."));
		 			statusPanel.setNewAvailableVersion(updateManager.getAvailableVersion());
		 		}else{
		 			getLogger().info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"No new version available."));
		 		}
			}catch(Exception ex){
				getLogger().error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), ex);
		    }		      
		}
		
		private Logger getLogger(){
			if(log == null){
				log = Logger.getLogger(UpdateChecker.class.getPackage().getName());
			}
			return log;
			
		}
	}
}
