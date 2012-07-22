/*
 * Created on 26-Set-2009
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
package org.pdfsam.guiclient.configuration.services.xml.strategy;

import java.util.Deque;

/**
 * Strategy to retrieve values from the config xml dom
 * 
 * @author Andrea Vacondio
 * 
 */
public interface XmlConfigStrategy {

	/**
	 * @return the environment value
	 */
	String getDefaultEnvironmentValue();

	/**
	 * @return the default working directory value
	 */
	String getDefaultWorkingDirectoryValue();

	/**
	 * @return the play sounds value
	 */
	String getPlaySoundsValue();

	/**
	 * @return the check for updates value
	 */
	String getCheckForUpdatesValue();

	/**
	 * @return the value of the thread pool size for the thumbnails creator
	 */
	String getThreadPoolSizeValue();

	/**
	 * @return the thumbnails creator identifier value
	 */
	String getThumbnailsCreatorIdentifierValue();

	/**
	 * @return the logging level value
	 */
	String getLoggingLevelValue();
	
	/**
	 * @return the theme value
	 */
	String getThemeValue();
	
	/**
	 * @return the look and feel value
	 */
	String getLookAndFeelValue();
	
	/**
	 * @return locale value
	 */
	String getLocaleValue();
	
	/**
	 * @return the plugin absolute path
	 */
	String getPluginAbsolutePath();
	
	/**
	 * @return the ask overwrite confirmation value
	 */
	String getAskOverwriteConfirmation();

	/**
	 * @return the size of the thumbnails
	 */
	String getThumbnailsSize();
	
	/**
	 * @return the quality of the thumbnails
	 */
	String getHighQualityThumbnails();
	/**
	 * close resources if necessary
	 */
	void close();

}
