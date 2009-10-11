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

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.pdfsam.guiclient.configuration.services.ConfigurationService;
import org.pdfsam.guiclient.utils.XmlUtility;

/**
 * Default strategy
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultXmlStrategy extends AbstractXmlConfigStrategy {
	
	private static final String ROOT_NODE = "/pdfsam";
	private static final String LAF_XPATH = "/settings/laf/@current-laf";
	private static final String THEME_XPATH = "/settings/laf/@current-theme";
	private static final String LANGUAGE_XPATH = "/settings/i18n/@value";
	private static final String LOGGING_LEVEL_XPATH = "/settings/loglevel/@value";
	private static final String DEF_WORKING_DIR_XPATH = "/settings/default_working_path/@value";
	private static final String THUMBNAILS_CREATOR_XPATH = "/settings/thumbnails_creator/@class";
	private static final String CHECK_UPDATES_XPATH = "/settings/checkupdates/@value";
	private static final String PLAYSOUNDS_XPATH = "/settings/playsounds/@value";
	private static final String POOL_SIZE_XPATH = "/settings/thumbpoolsize/@value";
	private static final String DEFAULT_ENVIRONMENT_XPATH = "/settings/default_environment/@value";
	private static final String PLUGIN_ABSOLUTE_XPATH = "/settings/plugs_absolute_path/@value";

	/**
	 * @param document
	 */
	public DefaultXmlStrategy(Document document) {
		super(document);
	}

	@Override
	public String getCheckForUpdatesValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+CHECK_UPDATES_XPATH);
	}

	@Override
	public String getDefaultEnvironmentValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+DEFAULT_ENVIRONMENT_XPATH);
	}

	@Override
	public String getDefaultWorkingDirectoryValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+DEF_WORKING_DIR_XPATH);
	}

	@Override
	public String getLoggingLevelValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+LOGGING_LEVEL_XPATH);
	}

	@Override
	public String getPlaySoundsValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+PLAYSOUNDS_XPATH);
	}

	@Override
	public String getThreadPoolSizeValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+POOL_SIZE_XPATH);
	}

	@Override
	public String getThumbnailsCreatorIdentifierValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+THUMBNAILS_CREATOR_XPATH);
	}

	@Override
	public String getLookAndFeelValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+LAF_XPATH);
	}

	@Override
	public String getThemeValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+THEME_XPATH);
	}

	@Override
	public String getLocaleValue() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+LANGUAGE_XPATH);
	}

	@Override
	public String getPluginAbsolutePath() {
		return XmlUtility.getXmlValue(getDocument(),ROOT_NODE+PLUGIN_ABSOLUTE_XPATH);
	}

	/**
	 * Saves the configuration on the configuration xml file. The configuration
	 * file content is replaced.
	 * 
	 * @param configurationFile
	 * @param configuration
	 * @throws IOException 
	 */
	public static void saveXmlConfigurationFile(File configurationFile, ConfigurationService configuration) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(ROOT_NODE.replaceAll("/", ""));
		root.addAttribute("config-version", "2");
		XmlUtility.processXPath(root, LAF_XPATH, Integer.toString(configuration.getLookAndFeel()));
		XmlUtility.processXPath(root, THEME_XPATH, Integer.toString(configuration.getTheme()));
		XmlUtility.processXPath(root, POOL_SIZE_XPATH, Integer.toString(configuration.getThumbCreatorPoolSize()));
		XmlUtility.processXPath(root, LOGGING_LEVEL_XPATH, Integer.toString(configuration.getLoggingLevel()));
		XmlUtility.processXPath(root, LANGUAGE_XPATH, configuration.getLanguage());
		XmlUtility.processXPath(root, DEF_WORKING_DIR_XPATH, configuration.getDefaultWorkingDirectory());
		XmlUtility.processXPath(root, THUMBNAILS_CREATOR_XPATH, configuration.getThumbnailsCreatorIdentifier());
		XmlUtility.processXPath(root, DEFAULT_ENVIRONMENT_XPATH, configuration.getDefaultEnvironment());
		XmlUtility.processXPath(root, PLUGIN_ABSOLUTE_XPATH, configuration.getPluginAbsolutePath());
		XmlUtility.processXPath(root, CHECK_UPDATES_XPATH, Boolean.toString(configuration.isCheckForUpdates()));
		XmlUtility.processXPath(root, PLAYSOUNDS_XPATH, Boolean.toString(configuration.isPlaySounds()));
		XmlUtility.writeXmlFile(document, configurationFile);
	}

}
