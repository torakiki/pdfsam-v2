/*
 * Created on 18-Jun-2008
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
package org.pdfsam.guiclient.commons.renderers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.VisualPageListItem;

/**
 * JList renderer
 * 
 * @author Andrea Vacondio
 * 
 */
public class VisualListRenderer extends JLabel implements ListCellRenderer {

    private static final long serialVersionUID = -6125533840590452401L;

    private static final double ZOOM_STEP = 0.1;

    private int currentZoomLevel = 0;
    private boolean drawRedCross = false;
    private BufferedImage image = null;

    public VisualListRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        VisualPageListItem item = (VisualPageListItem) value;
        currentZoomLevel = ((JVisualSelectionList) list).getCurrentZoomLevel();
        if (!item.isDeleted() || (item.isDeleted() && ((JVisualSelectionList) list).isDrawDeletedItems())) {
            if (item.getThumbnail() != null) {
                image = item.getThumbnail();
                setPreferredSize(getZoomedSize());
                drawRedCross = item.isDeleted();
            }
            String text = item.getPageNumber() + "";
            if (item.getPaperFormat() != null && item.getPaperFormat().length() > 0) {
                text += " - [" + item.getPaperFormat() + "]";
            }
            setText(text);
            setHorizontalTextPosition(JLabel.CENTER);
            setVerticalTextPosition(JLabel.BOTTOM);
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.BOTTOM);
            setIconTextGap(2);
            setForeground(list.getForeground());
            setBackground(UIManager.getColor("Panel.background"));
            if (isSelected) {
                setBorder(BorderFactory.createLineBorder(Color.red, 1));
            } else {
                setBorder(BorderFactory.createLineBorder(list.getBackground()));
            }
        }
        return this;
    }

    /**
     * @return the dimension of the image to be displayed according with the zoom level
     */
    private Dimension getZoomedSize() {
        Dimension retVal = null;
        int comSize = Configuration.getInstance().getThumbnailSize()
                + (int) (Configuration.getInstance().getThumbnailSize() * ZOOM_STEP * currentZoomLevel);
        retVal = new Dimension(comSize + 2, comSize + 15);
        return retVal;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (image != null) {
            int imageHeight = 0;
            int imageWidth = 0;
            int x = 1;
            int y = 0;
            // get image dimensions
            if (isHorizontal(image)) {
                imageWidth = getWidth() - 2;
                imageHeight = Math.round(image.getHeight(null) * ((float) imageWidth / (float) image.getWidth(null)));
                if (imageHeight > getHeight() - 15) {
                    imageHeight = getHeight() - 15;
                }
                y = (getHeight() - 15 - imageHeight) / 2;
            } else {
                imageHeight = getHeight() - 15;
                imageWidth = Math.round(image.getWidth(null) * ((float) imageHeight / (float) image.getHeight(null)));
                if (imageWidth > getWidth() - 2) {
                    imageWidth = getWidth() - 2;
                }
                x = (getWidth() - 2 - imageWidth) / 2;
            }
            if (Configuration.getInstance().isHighQualityThumbnails()) {
                g2d.setComposite(AlphaComposite.Src);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            }
            g2d.drawImage(image, x, y, imageWidth, imageHeight, null);
        }
        if (drawRedCross) {
            g2d.setColor(Color.red);
            g2d.drawLine(0, getHeight(), getWidth(), 0);
            g2d.drawLine(getWidth(), getHeight(), 0, 0);
        }
    }

    private boolean isHorizontal(Image image) {
        boolean retVal = false;
        if (image != null) {
            retVal = image.getWidth(null) > image.getHeight(null);
        }
        return retVal;
    }
}
