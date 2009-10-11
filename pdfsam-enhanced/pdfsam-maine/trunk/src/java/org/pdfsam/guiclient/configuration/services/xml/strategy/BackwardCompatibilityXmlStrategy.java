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

import org.dom4j.Document;
import org.pdfsam.guiclient.utils.XmlUtility;

/**
 * Old xml strategy
 * @author Andrea Vacondio
 *
 */
public class BackwardCompatibilityXmlStrategy extends AbstractXmlConfigStrategy {
	
	private static final String LAF_XPATH = "/pdfsam/settings/lookAndfeel/LAF";
	private static final String THEME_XPATH = "/pdfsam/settings/lookAndfeel/theme";
	private static final String LANGUAGE_XPATH = "/pdfsam/settings/i18n";
	private static final String LOGGING_LEVEL_XPATH = "/pdfsam/settings/loglevel";
	private static final String DEF_WORKING_DIR_XPATH = "/pdfsam/settings/default_working_dir";
	private static final String THUMBNAILS_CREATOR_XPATH = "/pdfsam/settings/thumbnails_creator";
	private static final String CHECK_UPDATES_XPATH = "/pdfsam/settings/checkupdates";
	private static final String PLAYSOUNDS_XPATH = "/pdfsam/settings/playsounds";
	private static final String POOL_SIZE_XPATH = "/pdfsam/settings/thumbpoolsize";
	private static final String DEFAULT_JOB_XPATH = "/pdfsam/settings/defaultjob";
	private static final String PLUGIN_ABSOLUTE_XPATH = "/pdfsam/settings/plugs_absolute_dir";

		
	/**
	 * @param document
	 */
	public BackwardCompatibilityXmlStrategy(Document document) {
		super(document);
	}

	@Override
	public String getCheckForUpdatesValue() {
		return XmlUtility.getXmlValue(getDocument(), CHECK_UPDATES_XPATH);
	}

	@Override
	public String getDefaultEnvironmentValue() {
		return XmlUtility.getXmlValue(getDocument(), DEFAULT_JOB_XPATH);
	}

	@Override
	public String getDefaultWorkingDirectoryValue() {
		return XmlUtility.getXmlValue(getDocument(), DEF_WORKING_DIR_XPATH);
	}

	@Override
	public String getLoggingLevelValue() {
		return XmlUtility.getXmlValue(getDocument(), LOGGING_LEVEL_XPATH);
	}

	@Override
	public String getPlaySoundsValue() {
		return XmlUtility.getXmlValue(getDocument(), PLAYSOUNDS_XPATH);
	}

	@Override
	public String getThreadPoolSizeValue() {
		return XmlUtility.getXmlValue(getDocument(), POOL_SIZE_XPATH);
	}

	@Override
	public String getThumbnailsCreatorIdentifierValue() {
		return XmlUtility.getXmlValue(getDocument(), THUMBNAILS_CREATOR_XPATH);
	}

	@Override
	public String getLookAndFeelValue() {
		return XmlUtility.getXmlValue(getDocument(), LAF_XPATH);
	}

	@Override
	public String getThemeValue() {
		return XmlUtility.getXmlValue(getDocument(), THEME_XPATH);
	}

	@Override
	public String getLocaleValue() {
		return XmlUtility.getXmlValue(getDocument(), LANGUAGE_XPATH);
	}

	@Override
	public String getPluginAbsolutePath() {
		return XmlUtility.getXmlValue(getDocument(), PLUGIN_ABSOLUTE_XPATH);
	}
}
