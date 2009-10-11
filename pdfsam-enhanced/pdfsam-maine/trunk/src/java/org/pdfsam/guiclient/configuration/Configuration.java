/*
 * Created on 21-Dec-2006
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
package org.pdfsam.guiclient.configuration;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.guiclient.business.TextPaneAppender;
import org.pdfsam.guiclient.configuration.services.ConfigurationService;
import org.pdfsam.guiclient.configuration.services.ConfigurationServiceLocator;
import org.pdfsam.guiclient.utils.ThemeUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * Configuration Singleton
 * 
 * @author Andrea Vacondio
 * 
 */
public class Configuration {

	private static final Logger log = Logger.getLogger(Configuration.class.getPackage().getName());

	public static final int DEFAULT_POOL_SIZE = 3;

	private static Configuration configObject;

	private ConfigurationService configurationService;

	private int screenResolution = 0;

	private Configuration() {
		initialize();
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone configuration object.");
	}

	public static synchronized Configuration getInstance() {
		if (configObject == null) {
			configObject = new Configuration();
		}
		return configObject;
	}

	/**
	 * Initialization
	 */
	private void initialize() {
		try {
			configurationService = ConfigurationServiceLocator.getInstance().getConfigurationService();

			initializeLookAndFeel();
			initializeLoggingLevel();
			screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
		} catch (Exception e) {
			log.fatal("Error loading configuration.",e);
		}
	}

	/**
	 * sets the look and feel
	 * 
	 * @throws Exception
	 */
	private void initializeLookAndFeel() throws Exception {
		if (ThemeUtility.isPlastic(configurationService.getLookAndFeel())) {
			ThemeUtility.setTheme(configurationService.getTheme());
		}
		UIManager.setLookAndFeel(ThemeUtility.getLAF(configurationService.getLookAndFeel()));
	}

	/**
	 * sets the logging threshold for the appender
	 */
	private void initializeLoggingLevel() {
		int logLevel = configurationService.getLoggingLevel();
		try {
			TextPaneAppender appender = (TextPaneAppender) Logger.getLogger("org.pdfsam").getAppender("JLogPanel");
			Level loggingLevel = Level.toLevel(logLevel, Level.DEBUG);
			log.info(GettextResource.gettext(getI18nResourceBundle(), "Logging level set to ") + loggingLevel);
			appender.setThreshold(loggingLevel);
		} catch (Exception e) {
			log.warn(GettextResource.gettext(getI18nResourceBundle(), "Unable to set logging level."), e);
		}
	}

	/**
	 * @return the language ResourceBundle
	 */
	public ResourceBundle getI18nResourceBundle() {
		return configurationService.getI18nResourceBundle();
	}

	/**
	 * @return the default environment
	 */
	public String getDefaultEnvironment() {
		return configurationService.getDefaultEnvironment();
	}

	/**
	 * Set the default environment path
	 * 
	 * @param environmentPath
	 */
	public void setDefaultEnvironment(String environmentPath) {
		configurationService.setDefaultEnvironment(environmentPath);
	}

	/**
	 * 
	 * @return the ConsoleServicesFacade
	 */
	public ConsoleServicesFacade getConsoleServicesFacade() {
		return configurationService.getConsoleServicesFacade();
	}

	/**
	 * @return the loggingLevel
	 */
	public int getLoggingLevel() {
		return configurationService.getLoggingLevel();
	}

	/**
	 * @return the checkForUpdates
	 */
	public boolean isCheckForUpdates() {
		return configurationService.isCheckForUpdates();
	}

	/**
	 * Set the check for updates
	 * 
	 * @param checkForUpdateds
	 */
	public void setCheckForUpdates(boolean checkForUpdateds) {
		configurationService.setCheckForUpdates(checkForUpdateds);
	}

	/**
	 * @return the defaultWorkingDir
	 */
	public String getDefaultWorkingDirectory() {
		return configurationService.getDefaultWorkingDirectory();
	}

	/**
	 * Set the default working directory
	 * 
	 * @param defaultDirectory
	 */
	public void setDefaultWorkingDirectory(String defaultDirectory) {
		configurationService.setDefaultWorkingDirectory(defaultDirectory);
	}

	/**
	 * @return the playSounds
	 */
	public boolean isPlaySounds() {
		return configurationService.isPlaySounds();
	}

	/**
	 * @param playSounds
	 *            the playSounds to set
	 */
	public void setPlaySounds(boolean playSounds) {
		configurationService.setPlaySounds(playSounds);
	}

	/**
	 * @return the screenResolution
	 */
	public int getScreenResolution() {
		return screenResolution;
	}

	/**
	 * @return the thumbCreatorPoolSize
	 */
	public int getThumbCreatorPoolSize() {
		return configurationService.getThumbCreatorPoolSize();
	}

	/**
	 * @return the thumbnailsCreatorIdentifier
	 */
	public String getThumbnailsCreatorIdentifier() {
		return configurationService.getThumbnailsCreatorIdentifier();
	}

	/**
	 * @param thumbnailsCreatorIdentifier
	 *            the thumbnailsCreatorIdentifier to set
	 */
	public void setThumbnailsCreatorIdentifier(String thumbnailsCreatorIdentifier) {
		configurationService.setThumbnailsCreatorIdentifier(thumbnailsCreatorIdentifier);
	}

	/**
	 * @return the selected language String representation
	 */
	public String getSelectedLanguage() {
		return configurationService.getLanguage();
	}

	/**
	 * Set the selected language
	 * 
	 * @param language
	 */
	public void setSelectedLanguage(String language) {
		configurationService.setLanguage(language);
	}

	/**
	 * @return informations to be displayed
	 */
	public String getConfigurationInformations() {
		return configurationService.getConfigurationInformations();
	}

	/**
	 * @return the plugin absolute path
	 */
	public String getPluginAbsolutePath() {
		return configurationService.getPluginAbsolutePath();
	}

	/**
	 * @return the look and feel
	 */
	public int getLookAndFeel() {
		return configurationService.getLookAndFeel();
	}

	/**
	 * Set the look and feel
	 * 
	 * @param lookAndFeel
	 */
	public void setLookAndFeel(int lookAndFeel) {
		configurationService.setLookAndFeel(lookAndFeel);
	}

	/**
	 * @return the theme
	 */
	public int getTheme() {
		return configurationService.getTheme();
	}

	/**
	 * Set the theme
	 * 
	 * @param theme
	 */
	public void setTheme(int theme) {
		configurationService.setTheme(theme);
	}

	/**
	 * Set the logging level
	 * 
	 * @param level
	 */
	public void setLoggingLevel(int level) {
		configurationService.setLoggingLevel(level);
	}

	/**
	 * save the current configuration
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException {
		configurationService.save();
	}

}
