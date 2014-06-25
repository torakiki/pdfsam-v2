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
package org.pdfsam.guiclient.configuration.services.xml;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.guiclient.configuration.services.ConfigurationService;
import org.pdfsam.guiclient.configuration.services.xml.strategy.BackwardCompatibilityXmlStrategy;
import org.pdfsam.guiclient.configuration.services.xml.strategy.DefaultXmlStrategy;
import org.pdfsam.guiclient.configuration.services.xml.strategy.XmlConfigStrategy;
import org.pdfsam.guiclient.exceptions.ConfigurationException;
import org.pdfsam.guiclient.l10n.LanguageLoader;
import org.pdfsam.guiclient.utils.ThemeUtility;
import org.pdfsam.guiclient.utils.XmlUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * Configuration service that reads the configuration from an xml file
 * 
 * @author Andrea Vacondio
 * 
 */
public class XmlConfigurationService implements ConfigurationService {

    private static final Logger log = Logger.getLogger(XmlConfigurationService.class.getPackage().getName());

    private static final String VERSION_XPATH = "/pdfsam/@config-version";
    public static final String OLD_CONFIGURATION_FILE_NAME = "config.xml";
    public static final String CONFIGURATION_FILE_NAME = "pdfsam-config.xml";
    public static final int DEFAULT_POOL_SIZE = 3;
    public static final String DEFAULT_CONFIG_DIRECTORY = System.getProperty("user.home") + "/.pdfsam";

    private ResourceBundle i18nResourceBundle;
    private ConsoleServicesFacade consoleServicesFacade;
    private boolean checkForUpdates = true;
    private boolean playSounds = true;
    private boolean askOverwriteConfirmation = true;
    private boolean thumbnailsHighQuality = false;
    private String defaultWorkingDirectory = null;
    private String defaultEnvironment = null;
    private int thumbCreatorPoolSize = DEFAULT_POOL_SIZE;
    private String thumbnailsCreatorIdentifier = "";
    private int thumbnailsSize = 190;
    private int loggingLevel;
    private int lookAndFeel = 0;
    private int theme = 0;
    private String language;
    private String pluginAbsolutePath;
    private String configurationFilePath;

    public XmlConfigurationService() throws ConfigurationException {
        initializeService();
    }

    /**
     * Initialization
     * 
     * @throws ConfigurationException
     */
    private void initializeService() throws ConfigurationException {
        log.info("Loading configuration..");
        File configurationFile = null;
        try {
            configurationFile = getConfigurationXmlFile(CONFIGURATION_FILE_NAME);
            if (configurationFile == null) {
                configurationFile = getConfigurationXmlFile(OLD_CONFIGURATION_FILE_NAME);
                if (configurationFile == null) {
                    throw new ConfigurationException("Unable to find configuration file");
                }
            }
            configurationFilePath = configurationFile.getAbsolutePath();
            Document document = XmlUtility.parseXmlFile(configurationFile);
            if (document != null) {
                // setting up strategy
                XmlConfigStrategy strategy = null;
                Node node = document.selectSingleNode(VERSION_XPATH);
                if (node != null && "2".equals(node.getText().trim())) {
                    strategy = new DefaultXmlStrategy(document);
                } else {
                    strategy = new BackwardCompatibilityXmlStrategy(document);
                }

                initializeLocale(strategy);
                initializeLookAndFeel(strategy);
                initializeLoggingLevel(strategy);
                initializePoolSize(strategy);
                initializeThumbnailsSize(strategy);

                defaultWorkingDirectory = strategy.getDefaultWorkingDirectoryValue();
                defaultEnvironment = strategy.getDefaultEnvironmentValue();
                thumbnailsCreatorIdentifier = strategy.getThumbnailsCreatorIdentifierValue();
                checkForUpdates = isValidTrueValue(strategy.getCheckForUpdatesValue());
                playSounds = isValidTrueValue(strategy.getPlaySoundsValue());
                pluginAbsolutePath = strategy.getPluginAbsolutePath();
                askOverwriteConfirmation = isValidTrueValue(strategy.getAskOverwriteConfirmation());
                thumbnailsHighQuality = isValidTrueValue(strategy.getHighQualityThumbnails());
                log.info("Thumbnails high quality " + Boolean.toString(thumbnailsHighQuality));

                consoleServicesFacade = new ConsoleServicesFacade();
                strategy.close();
            } else {
                throw new ConfigurationException("Unable to parse xml configuration file.");
            }
        } catch (DocumentException e) {
            throw new ConfigurationException(e);
        } catch (UnsupportedEncodingException ue) {
            throw new ConfigurationException(ue);
        }
    }

    /**
     * thread pool size initialization
     * 
     * @param strategy
     */
    private void initializePoolSize(XmlConfigStrategy strategy) {
        String poolSizeString = strategy.getThreadPoolSizeValue();
        if (poolSizeString.length() > 0) {
            thumbCreatorPoolSize = Integer.parseInt(poolSizeString);
        }
    }

    private void initializeThumbnailsSize(XmlConfigStrategy strategy) {
        String thumbSize = strategy.getThumbnailsSize();
        if (thumbSize.length() > 0) {
            thumbnailsSize = Integer.parseInt(thumbSize);
        }
        log.info("Thumbnails size set to " + thumbnailsSize);
    }

    /**
     * locale initialization
     * 
     * @param strategy
     */
    private void initializeLocale(XmlConfigStrategy strategy) {
        log.info("Getting language...");
        language = strategy.getLocaleValue();
        if (language.length() <= 0) {
            log.warn("Unable to get language ResourceBudle, setting the default language ("
                    + LanguageLoader.DEFAULT_LANGUAGE + ").");
            language = LanguageLoader.DEFAULT_LANGUAGE;
        }
        i18nResourceBundle = new LanguageLoader(language, "org.pdfsam.i18n.resources.Messages").getBundle();
    }

    /**
     * theme, look and feel initialization
     * 
     * @param strategy
     */
    private void initializeLookAndFeel(XmlConfigStrategy strategy) {
        log.info(GettextResource.gettext(i18nResourceBundle, "Setting look and feel..."));
        String lookAndFeelString = strategy.getLookAndFeelValue();
        if (lookAndFeelString.length() > 0) {
            lookAndFeel = Integer.parseInt(lookAndFeelString);
        }
        if (ThemeUtility.isPlastic(lookAndFeel)) {
            String themeString = strategy.getThemeValue();
            if (themeString.length() > 0) {
                theme = Integer.parseInt(themeString);
            }
        }
    }

    /**
     * logging level initlialization
     * 
     * @param strategy
     */
    private void initializeLoggingLevel(XmlConfigStrategy strategy) {
        log.info(GettextResource.gettext(i18nResourceBundle, "Setting logging level..."));
        String logLev = strategy.getLoggingLevelValue();
        if (logLev != null && logLev.length() > 0) {
            loggingLevel = Integer.parseInt(logLev);
        } else {
            log.warn(GettextResource.gettext(i18nResourceBundle,
                    "Unable to find log level, setting to default level (DEBUG)."));
            loggingLevel = Level.DEBUG_INT;
        }
    }

    /**
     * @param value
     * @return true if the input param is "true" or "1"
     */
    private boolean isValidTrueValue(String value) {
        boolean retVal = Boolean.parseBoolean(value);
        if (!retVal) {
            retVal = "1".equals(value);
        }
        return retVal;
    }

    /**
     * It search the config file.
     * <ul>
     * <li>the first try uses secondaryPath=main jar path</li>
     * <li>the second try uses secondaryPath=System.getProperty("user.dir")</li>
     * </ul>
     * 
     * @param configurationFileName
     *            file name to search
     * @return the configuration file
     * @throws UnsupportedEncodingException
     * @throws ConfigurationException
     */
    private File getConfigurationXmlFile(String configurationFileName) throws UnsupportedEncodingException,
            ConfigurationException {
        File retVal = null;
        String configSearchPath = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource()
                .getLocation().getPath(), "UTF-8")).getParent();

        retVal = searchConfigurationFile(configSearchPath, configurationFileName);
        if (retVal == null) {
            log.warn("Unable to find " + configurationFileName + " into " + configSearchPath);
            configSearchPath = System.getProperty("user.dir");
            log.info("Looking for " + configurationFileName + " into " + configSearchPath);
            retVal = searchConfigurationFile(configSearchPath, configurationFileName);
        }
        return retVal;
    }

    /**
     * Search the configuration file
     * <ul>
     * <li>in ${user.home}/.pdfsam</li>
     * <li>if step 1 fails it search in searchPathIfFails</li>
     * <li>if found in step 2 but is write protected it copies the file in ${user.home}/.pdfsam</li>
     * </ul>
     * 
     * @param searchPathIfFails
     *            the path to search the configuration file if not found in the default path
     * @param configurationFileName
     *            file name to search
     * @return the path of the configuration file
     * @throws ConfigurationException
     */
    private File searchConfigurationFile(String searchPathIfFails, String configurationFileName)
            throws ConfigurationException {
        File retVal = new File(DEFAULT_CONFIG_DIRECTORY, configurationFileName);
        if (!(retVal.exists() && retVal.canWrite())) {
            File secondaryPath = new File(searchPathIfFails, configurationFileName);
            if (secondaryPath.exists()) {
                if (!secondaryPath.canWrite()) {
                    File defaultPath = new File(DEFAULT_CONFIG_DIRECTORY);
                    if (defaultPath.mkdirs()) {
                        log.info("Copying " + configurationFileName + " from " + secondaryPath.getPath() + " to "
                                + defaultPath.getPath());
                        FileUtility.copyFile(secondaryPath, retVal);
                    } else {
                        throw new ConfigurationException("Unable to create " + defaultPath);
                    }
                } else {
                    retVal = secondaryPath;
                }
            } else {
                retVal = null;
            }
        }
        return retVal;
    }

    public boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public void setCheckForUpdates(boolean checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }

    public boolean isPlaySounds() {
        return playSounds;
    }

    public void setPlaySounds(boolean playSounds) {
        this.playSounds = playSounds;
    }

    public String getDefaultWorkingDirectory() {
        return defaultWorkingDirectory;
    }

    public void setDefaultWorkingDirectory(String defaultWorkingDirectory) {
        this.defaultWorkingDirectory = defaultWorkingDirectory;
    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public int getThumbCreatorPoolSize() {
        return thumbCreatorPoolSize;
    }

    public String getThumbnailsCreatorIdentifier() {
        return thumbnailsCreatorIdentifier;
    }

    public void setThumbnailsCreatorIdentifier(String thumbnailsCreatorIdentifier) {
        this.thumbnailsCreatorIdentifier = thumbnailsCreatorIdentifier;
    }

    public int getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(int loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public ResourceBundle getI18nResourceBundle() {
        return i18nResourceBundle;
    }

    public ConsoleServicesFacade getConsoleServicesFacade() {
        return consoleServicesFacade;
    }

    public int getLookAndFeel() {
        return lookAndFeel;
    }

    public void setLookAndFeel(int lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getConfigurationInformations() {
        return configurationFilePath;
    }

    public String getPluginAbsolutePath() {
        return pluginAbsolutePath;
    }

    public boolean isAskOverwriteConfirmation() {
        return askOverwriteConfirmation;
    }

    public void setAskOverwriteConfirmation(boolean askOverwriteConfirmation) {
        this.askOverwriteConfirmation = askOverwriteConfirmation;
    }

    public int getThumbnailSize() {
        return thumbnailsSize;
    }

    public boolean isHighQualityThumbnils() {
        return thumbnailsHighQuality;
    }

    public void save() throws IOException {
        File defaultPath = new File(DEFAULT_CONFIG_DIRECTORY);
        if (!defaultPath.exists() || !defaultPath.isDirectory()) {
            defaultPath.mkdirs();
        }
        DefaultXmlStrategy.saveXmlConfigurationFile(new File(defaultPath, CONFIGURATION_FILE_NAME), this);
    }

}
