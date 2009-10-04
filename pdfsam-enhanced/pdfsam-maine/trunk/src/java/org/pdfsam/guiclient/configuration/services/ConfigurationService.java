/*
 * Created on 24-Set-2009
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
package org.pdfsam.guiclient.configuration.services;

import java.io.IOException;
import java.util.ResourceBundle;

import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.guiclient.configuration.Configuration;

/**
 * configuration service
 * 
 * @author Andrea Vacondio
 * 
 */
public interface ConfigurationService {
	
	/**
	 * @return facade for the console services
	 */
	ConsoleServicesFacade getConsoleServicesFacade();

	/**
	 * @return path to the default environment
	 */
	String getDefaultEnvironment();

	/**
	 * set the path for the default environment
	 * @param path
	 */
	void setDefaultEnvironment(String path);

	/**
	 * @return path to te default working directory
	 */
	String getDefaultWorkingDirectory();
	
	/**
	 * set the path to the default working directory
	 * @param path
	 */
	void setDefaultWorkingDirectory(String path);

	/**
	 * @return internationalization resource bundle
	 */
	ResourceBundle getI18nResourceBundle();

	/**
	 * @return logging level
	 */
	int getLoggingLevel();

	/**
	 * Set the logging level
	 * @param level
	 */
	void setLoggingLevel(int level);
	
	/**
	 * @return size  of the threadpool used by the thumbnails creator
	 */
	int getThumbCreatorPoolSize();

	/**
	 * set the thumbnails crreator pool size
	 * @param poolSize
	 */
	void setThumbCreatorPoolSize(int poolSize);

	/**
	 * @return identifier of the thumbnails creator to use
	 */
	String getThumbnailsCreatorIdentifier();
	
	/**
	 * Set the thumbnail creator identifier
	 * @param identifier
	 */
	void setThumbnailsCreatorIdentifier(String identifier);
	
	/**
	 * @return the checkForUpdates
	 */
	boolean isCheckForUpdates();
	
	/**
	 * set the checl for updates flag
	 * @param checkForUpdateds
	 */
	void setCheckForUpdates(boolean checkForUpdateds);
	
	/**
	 * @return the playSounds
	 */
	boolean isPlaySounds();
	
	/**
	 * Set the play sounds flag
	 * @param playSounds
	 */
	void setPlaySounds(boolean playSounds);
	
	/**
	 * @return information about the configuration service to be displayed 
	 */
	String getConfigurationInformations();
	
	/**
	 * @return the look and feel
	 */
	int getLookAndFeel();
	
	/**
	 * Set the look and feel
	 * @param lookAndFeel
	 */
	void setLookAndFeel(int lookAndFeel);
	
	/**
	 * @return the theme
	 */
	int getTheme();
	
	/**
	 * Set the theme
	 * @param theme
	 */
	void setTheme(int theme);
	
	/**
	 * @return String representation of the language
	 */
	String getLanguage();
	
	/**
	 * Set the language
	 * @param language
	 */
	void setLanguage(String language);
	
	/**
	 * @return the plugin absolute path
	 */
	String getPluginAbsolutePath();
	
	/**
	 * save the configuration
	 * @throws IOException in case of error saving the configuration
	 */
	public void save(Configuration configuration)throws IOException ;
}
