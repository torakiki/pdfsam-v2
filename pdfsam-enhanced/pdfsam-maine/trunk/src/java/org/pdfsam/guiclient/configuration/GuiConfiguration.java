/*
 * Created on 11-Oct-2009
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
package org.pdfsam.guiclient.configuration;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Collection;

import org.pdfsam.guiclient.configuration.services.ConfigurationServiceLocator;
import org.pdfsam.guiclient.configuration.services.GuiConfigurationService;

/**
 * User interface configuration singleton
 * 
 * @author Andrea Vacondio
 * 
 */
public class GuiConfiguration {

    private static GuiConfiguration instance = null;

    private GuiConfigurationService guiConfigurationService;

    private GuiConfiguration() {
        initialize();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone configuration object.");
    }

    public static synchronized GuiConfiguration getInstance() {
        if (instance == null) {
            instance = new GuiConfiguration();
        }
        return instance;
    }

    /**
     * initialize the GUI configuration service
     */
    private void initialize() {
        guiConfigurationService = ConfigurationServiceLocator.LOCATOR.getGuiConfigurationService();
    }

    /**
     * save the current gui configuration
     * 
     * @throws IOException
     */
    public void save() throws IOException {
        guiConfigurationService.save();
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getExtendedState()
     */
    public int getExtendedState() {
        return guiConfigurationService.getExtendedState();
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getLocationOnScreen()
     */
    public Point getLocationOnScreen() {
        return guiConfigurationService.getLocationOnScreen();
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getSize()
     */
    public Dimension getSize() {
        return guiConfigurationService.getSize();
    }

    /**
     * @param state
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setExtendedState(int)
     */
    public void setExtendedState(int state) {
        guiConfigurationService.setExtendedState(state);
    }

    /**
     * @param point
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setLocationOnScreen(java.awt.Point)
     */
    public void setLocationOnScreen(Point point) {
        guiConfigurationService.setLocationOnScreen(point);
    }

    /**
     * @param dimension
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setSize(java.awt.Dimension)
     */
    public void setSize(Dimension dimension) {
        guiConfigurationService.setSize(dimension);
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getHorizontalDividerLocation()
     */
    public int getHorizontalDividerLocation() {
        return guiConfigurationService.getHorizontalDividerLocation();
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getVerticalDividerLocation()
     */
    public int getVerticalDividerLocation() {
        return guiConfigurationService.getVerticalDividerLocation();
    }

    /**
     * @param location
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setHorizontalDividerLocation(int)
     */
    public void setHorizontalDividerLocation(int location) {
        guiConfigurationService.setHorizontalDividerLocation(location);
    }

    /**
     * @param location
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setVerticalDividerLocation(int)
     */
    public void setVerticalDividerLocation(int location) {
        guiConfigurationService.setVerticalDividerLocation(location);
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getHorizontalDividerDimension()
     */
    public Dimension getHorizontalDividerDimension() {
        return guiConfigurationService.getHorizontalDividerDimension();
    }

    /**
     * @param horizontalDividerDimension
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setHorizontalDividerDimension(java.awt.Dimension)
     */
    public void setHorizontalDividerDimension(Dimension horizontalDividerDimension) {
        guiConfigurationService.setHorizontalDividerDimension(horizontalDividerDimension);
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getVerticalDividerDimension()
     */
    public Dimension getVerticalDividerDimension() {
        return guiConfigurationService.getVerticalDividerDimension();
    }

    /**
     * @param verticalDividerDimension
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setVerticalDividerDimension(java.awt.Dimension)
     */
    public void setVerticalDividerDimension(Dimension verticalDividerDimension) {
        guiConfigurationService.setVerticalDividerDimension(verticalDividerDimension);
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#getSelectedPlugin()
     */
    public String getSelectedPlugin() {
        return guiConfigurationService.getSelectedPlugin();
    }

    /**
     * @param selectedPlugin
     * @see org.pdfsam.guiclient.configuration.services.GuiConfigurationService#setSelectedPlugin(java.lang.String)
     */
    public void setSelectedPlugin(String selectedPlugin) {
        guiConfigurationService.setSelectedPlugin(selectedPlugin);
    }
    
    /**
     * @param envPath
     * @see org.pdfsam.guiclient.configuration.services.ConfigurationService#addRecentEnvironment(java.lang.String)
     */
    public void addRecentEnvironment(String envPath) {
        guiConfigurationService.addRecentEnvironment(envPath);
    }

    /**
     * @return
     * @see org.pdfsam.guiclient.configuration.services.ConfigurationService#getRecentEnvironments()
     */
    public Collection<String> getRecentEnvironments() {
        return guiConfigurationService.getRecentEnvironments();
    }

}
