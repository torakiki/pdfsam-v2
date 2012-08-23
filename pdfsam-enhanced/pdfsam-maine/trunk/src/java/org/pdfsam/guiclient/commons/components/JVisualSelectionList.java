/*
 * Created on 26-Jun-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.components;

import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * JList with zoom support
 * 
 * @author Andrea Vacondio
 * 
 */
public class JVisualSelectionList extends JList {

    private static final long serialVersionUID = -491255443741363383L;

    public static final int DEFAULT_ZOOM_LEVEL = -4;
    public static final int MIN_ZOOM_LEVEL = -7;
    public static final int MAX_ZOOM_LEVEL = 0;

    private int currentZoomLevel = DEFAULT_ZOOM_LEVEL;
    private boolean drawDeletedItems = true;

    public JVisualSelectionList() {
    }

    /**
     * @param dataModel
     */
    public JVisualSelectionList(ListModel dataModel) {
        super(dataModel);
    }

    /**
     * @param listData
     */
    public JVisualSelectionList(Object[] listData) {
        super(listData);
    }

    /**
     * @param listData
     */
    public JVisualSelectionList(Vector<?> listData) {
        super(listData);
    }

    public JVisualSelectionList(boolean drawDeletedItems) {
        this.drawDeletedItems = drawDeletedItems;
    }

    /**
     * @return the currentZoomLevel
     */
    public int getCurrentZoomLevel() {
        return currentZoomLevel;
    }

    /**
     * @param currentZoomLevel
     *            the currentZoomLevel to set
     */
    public void setCurrentZoomLevel(int currentZoomLevel) {
        this.currentZoomLevel = currentZoomLevel;
    }

    /**
     * increment the zoom level
     */
    public void incZoomLevel() {
        if (currentZoomLevel < MAX_ZOOM_LEVEL) {
            currentZoomLevel++;
        }
    }

    /**
     * deincrement the zoom level
     */
    public void deincZoomLevel() {
        if (currentZoomLevel > MIN_ZOOM_LEVEL) {
            currentZoomLevel--;
        }
    }

    /**
     * @return the drawDeletedItems
     */
    public boolean isDrawDeletedItems() {
        return drawDeletedItems;
    }

    /**
     * @param drawDeletedItems
     *            the drawDeletedItems to set
     */
    public void setDrawDeletedItems(boolean drawDeletedItems) {
        this.drawDeletedItems = drawDeletedItems;
    }

}
