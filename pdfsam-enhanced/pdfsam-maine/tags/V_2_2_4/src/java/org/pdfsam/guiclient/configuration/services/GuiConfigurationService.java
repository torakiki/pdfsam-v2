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
package org.pdfsam.guiclient.configuration.services;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Collection;

/**
 * Services related to the GUI configuration
 * 
 * @author Andrea Vacondio
 * 
 */
public interface GuiConfigurationService {

    /**
     * @see javax.swing.JFrame#getExtendedState()
     * 
     * @return the extended state
     */
    int getExtendedState();

    /**
     * set the extended state
     * 
     * @param state
     */
    void setExtendedState(int state);

    /**
     * @see javax.swing.JFrame#getSize())
     * 
     * @return
     */
    Dimension getSize();

    /**
     * set the size
     * 
     * @param dimension
     */
    void setSize(Dimension dimension);

    /**
     * @see javax.swing.JFrame#getLocationOnScreen())
     * 
     * @return
     */
    Point getLocationOnScreen();

    /**
     * set the location on screen
     * 
     * @param point
     */
    void setLocationOnScreen(Point point);

    /**
     * @return the horizontal divider location
     */
    int getHorizontalDividerLocation();

    /**
     * set the horizontal divider location
     * 
     * @param location
     */
    void setHorizontalDividerLocation(int location);

    /**
     * @return the vertical divider location
     */
    int getVerticalDividerLocation();

    /**
     * set the vertical divider location
     * 
     * @param location
     */
    void setVerticalDividerLocation(int location);

    /**
     * @return the horizontal divider dimension
     */
    Dimension getHorizontalDividerDimension();

    /**
     * set the horizontal divider dimension
     * 
     * @param horizontalDividerDimension
     */
    void setHorizontalDividerDimension(Dimension horizontalDividerDimension);

    /**
     * @return the vertical divider dimension
     */
    Dimension getVerticalDividerDimension();

    /**
     * set the vertical divider dimension
     * 
     * @param verticalDividerDimension
     */
    void setVerticalDividerDimension(Dimension verticalDividerDimension);

    /**
     * set the selected plugin
     * 
     * @param selectedPlugin
     */
    void setSelectedPlugin(String selectedPlugin);

    /**
     * @return the selected plugin
     */
    String getSelectedPlugin();

    /**
     * saves the current gui configuration
     */
    void save() throws IOException;

    /**
     * adds the given env ensuring that there are no duplicates, that the new env is at the top of the list and that the list size is at most 8.
     * 
     * @param envPath
     */
    void addRecentEnvironment(String envPath);

    /**
     * @return the queue with the recent environments
     */
    Collection<String> getRecentEnvironments();

}
