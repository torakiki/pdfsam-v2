/*
 * Created on 08-Nov-2007
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
package org.pdfsam.guiclient;

import java.awt.Point;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.thumbnails.ThumbnailCreatorsRegisty;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.configuration.GuiConfiguration;
import org.pdfsam.guiclient.gui.frames.JMainFrame;
import org.pdfsam.guiclient.utils.filters.JarFilter;

/**
 * GUI Client for the console
 * 
 * @author a.vacondio
 * 
 */
public class GuiClient {

    private static final long serialVersionUID = -3608998690519362986L;

    private static final Logger log = Logger.getLogger(GuiClient.class.getPackage().getName());
    private static final String PROPERTY_FILE = "pdfsam.properties";

    public static final String AUTHOR = "Andrea Vacondio";
    public static final String UNIXNAME = "pdfsam";

    private static final String NAME = "PDF Split and Merge";
    private static final String VERSION_TYPE_PROPERTY = "pdfsam.version";
    private static final String VERSION_TYPE_DEFAULT = "basic";

    private static final String VERSION_PROPERTY = "pdfsam.jar.version";
    private static final String VERSION_DEFAULT = "1.0.0";

    private static final String BUILDDATE_PROPERTY = "pdfsam.builddate";
    private static final String BUILDDATE_DEFAULT = "";

    private static final String BRANCH_PROPERTY = "pdfsam.branch";
    private static final String BRANCH_DEFAULT = "1";

    private static JMainFrame clientGUI;
    private static Properties defaultProps = new Properties();

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            loadApplicationProperties();
            loadExtendedLibraries();
            clientGUI = new JMainFrame();
            initializeUserInterface();
            clientGUI.setVisible(true);
            initializeExtendedState();
        } catch (Throwable t) {
            log.fatal("Error:", t);
        }
    }

    /**
     * load application properties
     */
    private static void loadApplicationProperties() {
        try {
            InputStream is = GuiClient.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
            defaultProps.load(is);
        } catch (Exception e) {
            log.error("Unable to load pdfsam properties.", e);
        }
    }

    /**
     * Loads the libraries in the "ext" subdirectory
     */
    private static void loadExtendedLibraries() {
        try {
            String configSearchPath = new File(URLDecoder.decode(GuiClient.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath(), "UTF-8")).getParent();
            File currentDir = new File(configSearchPath, "ext");
            File[] fileList = currentDir.listFiles(new JarFilter(false));
            if (!ArrayUtils.isEmpty(fileList)) {
                URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                for (File currentFile : fileList) {
                    addJar(currentFile.toURI().toURL(), urlClassLoader);
                }
                ThumbnailCreatorsRegisty.reload(urlClassLoader);
            }
        } catch (Exception e) {
            log.error("Unable to load extended libraries.", e);
        }
    }

    private static void addJar(URL jarUrl, URLClassLoader loader) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(loader, new Object[] { jarUrl });
    }

    /**
     * @return application version
     */
    public static String getVersion() {
        return defaultProps.getProperty(VERSION_PROPERTY, VERSION_DEFAULT);
    }

    /**
     * @return application name
     */
    public static String getApplicationName() {
        return NAME + " " + defaultProps.getProperty(VERSION_TYPE_PROPERTY, VERSION_TYPE_DEFAULT);
    }

    /**
     * @return application version type (basic or enhanced)
     */
    public static String getVersionType() {
        return defaultProps.getProperty(VERSION_TYPE_PROPERTY, VERSION_TYPE_DEFAULT);
    }

    /**
     * @return application build date
     */
    public static String getBuildDate() {
        return defaultProps.getProperty(BUILDDATE_PROPERTY, BUILDDATE_DEFAULT);
    }

    /**
     * @return application branch
     */
    public static String getBranch() {
        return defaultProps.getProperty(BRANCH_PROPERTY, BRANCH_DEFAULT);
    }

    private static void initializeExtendedState() {
        clientGUI.setExtendedState(GuiConfiguration.getInstance().getExtendedState());
    }

    /**
     * User interface initialization
     */
    private static void initializeUserInterface() {

        if (GuiConfiguration.getInstance().getSize() != null) {
            clientGUI.setSize(GuiConfiguration.getInstance().getSize());
        }

        Point locationOnScreen = GuiConfiguration.getInstance().getLocationOnScreen();
        if (locationOnScreen != null) {
            clientGUI.setLocation(locationOnScreen);
        }

        if (GuiConfiguration.getInstance().getHorizontalDividerDimension() != null) {
            clientGUI.setHorizontalDividerDimension(GuiConfiguration.getInstance().getHorizontalDividerDimension());
        }
        if (GuiConfiguration.getInstance().getHorizontalDividerLocation() > 0) {
            clientGUI.setHorizontalDividerLocation(GuiConfiguration.getInstance().getHorizontalDividerLocation());
        } else {
            clientGUI.setHorizontalDividerLocation(155);
        }

        if (GuiConfiguration.getInstance().getVerticalDividerDimension() != null) {
            clientGUI.setVerticalDividerDimension(GuiConfiguration.getInstance().getVerticalDividerDimension());
        }
        if (GuiConfiguration.getInstance().getVerticalDividerLocation() > 0) {
            clientGUI.setVerticalDividerLocation(GuiConfiguration.getInstance().getVerticalDividerLocation());
        }

        String selectedPlugin = GuiConfiguration.getInstance().getSelectedPlugin();
        if (selectedPlugin != null && selectedPlugin.length() > 0) {
            // If a default environment is set, the plugin set on it has precedence
            String defaultEnv = Configuration.getInstance().getDefaultEnvironment();
            if (defaultEnv == null || defaultEnv.length() <= 0) {
                clientGUI.getTreePanel().setSelectedPlugin(selectedPlugin);
            }
        }
        
        clientGUI.rebuildRecentEnvironmentsMenu();

    }
}
