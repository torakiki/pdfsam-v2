/*
 * Created on 10-Oct-2009
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

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import javax.swing.JFrame;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.configuration.services.GuiConfigurationService;
import org.pdfsam.guiclient.exceptions.ConfigurationException;
import org.pdfsam.guiclient.utils.XmlUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * GUI configuration service where values are read and written on an xml file
 * 
 * @author Andrea Vacondio
 * 
 */
public class XmlGuiConfigurationService implements GuiConfigurationService {

    private static final Logger LOG = Logger.getLogger(XmlGuiConfigurationService.class.getPackage().getName());

    public static final String CONFIGURATION_FILE_NAME = "gui-config.xml";
    private static final String ROOT_NODE = "/pdfsam-gui";
    private static final String EXTENDED_STATE_XPATH = "/gui-settings/extended-state/@value";
    private static final String SELECTED_PLUGIN_XPATH = "/gui-settings/selected-plugin/@class";
    private static final String LOCATION_X_XPATH = "/gui-settings/location/@xvalue";
    private static final String LOCATION_Y_XPATH = "/gui-settings/location/@yvalue";
    private static final String SIZE_WIDTH_XPATH = "/gui-settings/size/@width";
    private static final String SIZE_HEIGHT_XPATH = "/gui-settings/size/@height";
    private static final String VERTICAL_DIVIDER_LOCATION_XPATH = "/gui-settings/vertical-divider/@location";
    private static final String VERTICAL_DIVIDER_HEIGHT_XPATH = "/gui-settings/vertical-divider/@height";
    private static final String VERTICAL_DIVIDER_WIDTH_XPATH = "/gui-settings/vertical-divider/@width";
    private static final String HORIZONTAL_DIVIDER_LOCATION_XPATH = "/gui-settings/horizontal-divider/@location";
    private static final String HORIZONTAL_DIVIDER_HEIGHT_XPATH = "/gui-settings/horizontal-divider/@height";
    private static final String HORIZONTAL_DIVIDER_WIDTH_XPATH = "/gui-settings/horizontal-divider/@width";
    private static final String RECENT_ENVS_NODE_XPATH = "env/@value";
    private static final String RECENT_ENVS_PARENT_XPATH = "/recent_envs/";

    private Document document;
    private int extendedState = JFrame.MAXIMIZED_BOTH;
    private Dimension size;
    private Point locationOnScreen;
    private File configurationFile;
    private int horizontalDividerLocation;
    private Dimension horizontalDividerDimension;
    private int verticalDividerLocation;
    private Dimension verticalDividerDimension;
    private String selectedPlugin;
    private Deque<String> recentEnvironments = new ArrayDeque<String>();

    public XmlGuiConfigurationService() {
        initializeService();
    }

    /**
     * Initialization
     * 
     * @throws ConfigurationException
     */
    private void initializeService() {
        LOG.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                "Loading user interface configuration") + "..");
        configurationFile = new File(XmlConfigurationService.DEFAULT_CONFIG_DIRECTORY, CONFIGURATION_FILE_NAME);
        if (!configurationFile.exists()) {
            LOG.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Unable to find configuration file for the user interface"));
        } else {
            try {
                document = XmlUtility.parseXmlFile(configurationFile);
                if (document != null) {
                    iniExtendedState();
                    selectedPlugin = XmlUtility.getXmlValue(document, ROOT_NODE + SELECTED_PLUGIN_XPATH);
                    initDimension();
                    initLocationOnScreen();
                    initDividersLocation();
                    initHorizontalDividerDimension();
                    initVerticalDividerDimension();
                    initRecentEnvs();
                }
            } catch (DocumentException e) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error reading the configuration file for the user interface"), e);
            } finally {
                document = null;
            }
        }
    }

    /**
     * Initialization of recent environments
     */
    private void initRecentEnvs() {
        recentEnvironments = new ArrayDeque<String>();
        for (String env : XmlUtility.getXmlValues(document, ROOT_NODE + RECENT_ENVS_PARENT_XPATH + RECENT_ENVS_NODE_XPATH)) {
            recentEnvironments.addLast(env);
        }
    }

    /**
     * Initialization of the dividers location
     */
    private void initDividersLocation() {
        String verticalLocation = XmlUtility.getXmlValue(document, ROOT_NODE + VERTICAL_DIVIDER_LOCATION_XPATH);
        String horizontalLocation = XmlUtility.getXmlValue(document, ROOT_NODE + HORIZONTAL_DIVIDER_LOCATION_XPATH);
        if (verticalLocation.length() > 0) {
            try {
                verticalDividerLocation = Integer.parseInt(verticalLocation);
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface vertical divider location"), nfe);
            }
        }
        if (horizontalLocation.length() > 0) {
            try {
                horizontalDividerLocation = Integer.parseInt(horizontalLocation);
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface horizontal divider location"), nfe);
            }
        }
    }

    /**
     * Initialization of the extended state
     */
    private void iniExtendedState() {
        String state = XmlUtility.getXmlValue(document, ROOT_NODE + EXTENDED_STATE_XPATH);
        if (state.length() > 0) {
            try {
                extendedState = Integer.parseInt(state);
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface extended state"), nfe);
            }
        }
    }

    /**
     * Initialization of the dimension
     */
    private void initDimension() {
        String width = XmlUtility.getXmlValue(document, ROOT_NODE + SIZE_WIDTH_XPATH);
        String height = XmlUtility.getXmlValue(document, ROOT_NODE + SIZE_HEIGHT_XPATH);
        if (width.length() > 0 && height.length() > 0) {
            try {
                size = new Dimension(Integer.parseInt(width), Integer.parseInt(height));
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface size"), nfe);
            }
        }
    }

    /**
     * Initialization of the horizontal divider dimension
     */
    private void initHorizontalDividerDimension() {
        String width = XmlUtility.getXmlValue(document, ROOT_NODE + HORIZONTAL_DIVIDER_WIDTH_XPATH);
        String height = XmlUtility.getXmlValue(document, ROOT_NODE + HORIZONTAL_DIVIDER_HEIGHT_XPATH);
        if (width.length() > 0 && height.length() > 0) {
            try {
                horizontalDividerDimension = new Dimension(Integer.parseInt(width), Integer.parseInt(height));
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface horizontal divider size"), nfe);
            }
        }
    }

    /**
     * Initialization of the horizontal divider dimension
     */
    private void initVerticalDividerDimension() {
        String width = XmlUtility.getXmlValue(document, ROOT_NODE + VERTICAL_DIVIDER_WIDTH_XPATH);
        String height = XmlUtility.getXmlValue(document, ROOT_NODE + VERTICAL_DIVIDER_HEIGHT_XPATH);
        if (width.length() > 0 && height.length() > 0) {
            try {
                verticalDividerDimension = new Dimension(Integer.parseInt(width), Integer.parseInt(height));
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface vertical divider size"), nfe);
            }
        }
    }

    /**
     * Initialization of the location
     */
    private void initLocationOnScreen() {
        String x = XmlUtility.getXmlValue(document, ROOT_NODE + LOCATION_X_XPATH);
        String y = XmlUtility.getXmlValue(document, ROOT_NODE + LOCATION_Y_XPATH);
        if (x.length() > 0 && y.length() > 0) {
            try {
                locationOnScreen = new Point(Integer.parseInt(x), Integer.parseInt(y));
            } catch (NumberFormatException nfe) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Error initializing the user interface location"), nfe);
            }
        }
    }

    public int getExtendedState() {
        return extendedState;
    }

    public void setExtendedState(int extendedState) {
        this.extendedState = extendedState;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Point getLocationOnScreen() {
        return locationOnScreen;
    }

    public void setLocationOnScreen(Point locationOnScreen) {
        this.locationOnScreen = locationOnScreen;
    }

    public int getHorizontalDividerLocation() {
        return horizontalDividerLocation;
    }

    public void setHorizontalDividerLocation(int horizontalDividerLocation) {
        this.horizontalDividerLocation = horizontalDividerLocation;
    }

    public int getVerticalDividerLocation() {
        return verticalDividerLocation;
    }

    public void setVerticalDividerLocation(int verticalDividerLocation) {
        this.verticalDividerLocation = verticalDividerLocation;
    }

    public Dimension getHorizontalDividerDimension() {
        return horizontalDividerDimension;
    }

    public void setHorizontalDividerDimension(Dimension horizontalDividerDimension) {
        this.horizontalDividerDimension = horizontalDividerDimension;
    }

    public Dimension getVerticalDividerDimension() {
        return verticalDividerDimension;
    }

    public void setVerticalDividerDimension(Dimension verticalDividerDimension) {
        this.verticalDividerDimension = verticalDividerDimension;
    }

    public String getSelectedPlugin() {
        return selectedPlugin;
    }

    public void setSelectedPlugin(String selectedPlugin) {
        this.selectedPlugin = selectedPlugin;
    }

    public Collection<String> getRecentEnvironments() {
        return recentEnvironments;
    }

    public void addRecentEnvironment(String envPath) {
        if (envPath != null) {
            if (recentEnvironments.contains(envPath)) {
                recentEnvironments.remove(envPath);
            } else {
                while (recentEnvironments.size() > 8) {
                    recentEnvironments.removeLast();
                }
            }
            recentEnvironments.addFirst(envPath);
        }
    }

    public void save() throws IOException {
        if (configurationFile != null) {
            File parentDirectory = new File(XmlConfigurationService.DEFAULT_CONFIG_DIRECTORY);
            if (!parentDirectory.isDirectory() || !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            Document document = DocumentHelper.createDocument();
            Element root = document.addElement(ROOT_NODE.replaceAll("/", ""));
            XmlUtility.processXPath(root, EXTENDED_STATE_XPATH, Integer.toString(extendedState));
            XmlUtility.processXPath(root, SELECTED_PLUGIN_XPATH, selectedPlugin);

            if (size != null) {
                XmlUtility.processXPath(root, SIZE_WIDTH_XPATH, Integer.toString((int) size.getWidth()));
                XmlUtility.processXPath(root, SIZE_HEIGHT_XPATH, Integer.toString((int) size.getHeight()));
            }

            if (locationOnScreen != null) {
                XmlUtility.processXPath(root, LOCATION_X_XPATH, Integer.toString((int) locationOnScreen.getX()));
                XmlUtility.processXPath(root, LOCATION_Y_XPATH, Integer.toString((int) locationOnScreen.getY()));
            }

            if (horizontalDividerLocation > 0) {
                XmlUtility.processXPath(root, HORIZONTAL_DIVIDER_LOCATION_XPATH,
                        Integer.toString(horizontalDividerLocation));
            }

            if (horizontalDividerDimension != null) {
                XmlUtility.processXPath(root, HORIZONTAL_DIVIDER_WIDTH_XPATH,
                        Integer.toString((int) horizontalDividerDimension.getWidth()));
                XmlUtility.processXPath(root, HORIZONTAL_DIVIDER_HEIGHT_XPATH,
                        Integer.toString((int) horizontalDividerDimension.getHeight()));
            }

            if (verticalDividerLocation > 0) {
                XmlUtility.processXPath(root, VERTICAL_DIVIDER_LOCATION_XPATH,
                        Integer.toString(verticalDividerLocation));
            }

            if (verticalDividerDimension != null) {
                XmlUtility.processXPath(root, VERTICAL_DIVIDER_WIDTH_XPATH,
                        Integer.toString((int) verticalDividerDimension.getWidth()));
                XmlUtility.processXPath(root, VERTICAL_DIVIDER_HEIGHT_XPATH,
                        Integer.toString((int) verticalDividerDimension.getHeight()));
            }

            Element recentEnvNode = XmlUtility.processXPath(root, RECENT_ENVS_PARENT_XPATH, null);
            String[] recentEnvTokens = StringUtils.splitByWholeSeparator(RECENT_ENVS_NODE_XPATH, "/@");
            XmlUtility.addXmlNodesAndAttribute(recentEnvNode, recentEnvTokens[0], recentEnvTokens[1], recentEnvironments);
            
            LOG.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Saving GUI configuration") + "..");
            XmlUtility.writeXmlFile(document, configurationFile);
        }
    }

}
