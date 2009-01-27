/*
 * Created on 26-Feb-2008
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
package org.pdfsam.guiclient.updates;

import java.net.URL;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.updates.checkers.HttpUpdateChecker;
import org.pdfsam.guiclient.updates.checkers.UpdateChecker;
import org.pdfsam.i18n.GettextResource;
/**
 * Manager to check for an available new version
 * @author Andrea Vacondio
 *
 */
public class UpdateManager {

	private static Logger log = Logger.getLogger(UpdateManager.class.getPackage().getName());	

	private UpdateChecker checker = null;
	private boolean checked = false;
	private String availableVersion = null;
	private URL httpUrl = null;
	
	
	
	/**
	 * @param httpUrl
	 */
	public UpdateManager(URL httpUrl) {
		this.httpUrl = httpUrl;
	}

	/**
	 * @return true if there is an available version and this version is different from the current version
	 */
	public boolean isNewVersionAvailable(){
		checkForNewVersion(false);
		return ((availableVersion!=null)&&(!GuiClient.getVersion().equals(availableVersion)));
	}

	/**
	 * 
	 * @return the availableVersion
	 */
	public String getAvailableVersion() {
		checkForNewVersion(false);
		return availableVersion;
	}
	
	/**
	 * Check for a new version available if not already checked.
	 * @param forceRecheck force to recheck for a new version available
	 */
	public void checkForNewVersion(boolean forceRecheck){
		if(forceRecheck || !checked){
			log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Checking for a new version available."));
			try{
				if (checker == null){
					checker = new HttpUpdateChecker(httpUrl);
				}
				availableVersion = checker.getLatestVersion();
			}catch(Exception e){
				log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error checking for a new version available."), e);
			}
		}
		checked = true;
	}
	
	/**
	 * Check for a new version available
	 */
	public void checkForNewVersion(){
		checkForNewVersion(true);
	}
		
}
