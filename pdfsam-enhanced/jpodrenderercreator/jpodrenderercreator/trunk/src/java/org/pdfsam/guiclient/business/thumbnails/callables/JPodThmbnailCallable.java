/*
 * Created on 12-May-2009
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
package org.pdfsam.guiclient.business.thumbnails.callables;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.IdManager;
import org.pdfsam.guiclient.business.thumbnails.JPodRenderer;
import org.pdfsam.guiclient.business.thumbnails.creators.JPodThumbnailCreator;
import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.utils.ImageUtility;
import org.pdfsam.i18n.GettextResource;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.pd.PDPage;

/**
 * Callable used to generate thumbnails with JPod
 * 
 * @author Andrea Vacondio
 * 
 */
public class JPodThmbnailCallable implements Callable<Boolean> {

    private static final Logger log = Logger.getLogger(JPodThmbnailCallable.class.getPackage().getName());

    private PDPage pdPage;
    private JVisualPdfPageSelectionPanel panel;
    private VisualPageListItem pageItem;
    private long id;

    /**
     * @param pdPage
     * @param pageItem
     * @param panel
     */
    public JPodThmbnailCallable(PDPage pdPage, VisualPageListItem pageItem, JVisualPdfPageSelectionPanel panel, long id) {
        super();
        this.pdPage = pdPage;
        this.pageItem = pageItem;
        this.panel = panel;
        this.id = id;
    }

    public Boolean call() {
        Boolean retVal = Boolean.FALSE;
        if (!IdManager.getInstance().isCancelledExecution(id)) {
            IGraphicsContext graphics = null;
            try {
                Rectangle2D rect = pdPage.getCropBox().toNormalizedRectangle();
                double rectHeight = rect.getHeight();
                double recWidth = rect.getWidth();
                double resizePercentage = getResizePercentage(rectHeight, recWidth);

                int height = Math.round(((int) rect.getHeight()) * (float) resizePercentage);
                int width = Math.round(((int) rect.getWidth()) * (float) resizePercentage);
                BufferedImage imageInstance = new BufferedImage((int) recWidth, (int) rectHeight,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = (Graphics2D) imageInstance.getGraphics();
                graphics = new CwtAwtGraphicsContext(g2);
                // setup user space
                AffineTransform imgTransform = graphics.getTransform();
                imgTransform.scale(1, -1);
                imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
                graphics.setTransform(imgTransform);
                graphics.setBackgroundColor(Color.WHITE);
                graphics.fill(rect);
                CSContent content = pdPage.getContentStream();
                if (content != null) {
                    JPodRenderer renderer = new JPodRenderer(null, graphics);
                    renderer.process(content, pdPage.getResources());
                }
                // pageItem.setThumbnail(scaledInstance);
                BufferedImage scaledInstance = ImageUtility.getScaledInstance(imageInstance, width, height);
                pageItem.setPaperFormat(recWidth, rectHeight, JPodThumbnailCreator.JPOD_RESOLUTION);
                if (pdPage.getRotate() != 0) {
                    pageItem.setOriginalRotation(Rotation.getRotation(pdPage.getRotate()));
                }
                if (pageItem.isRotated()) {
                    pageItem.setThumbnail(ImageUtility.rotateImage(scaledInstance, pageItem.getCompleteRotation()));
                } else {
                    pageItem.setThumbnail(scaledInstance);

                }
                retVal = Boolean.TRUE;
            } catch (Throwable t) {
                pageItem.setThumbnail(ImageUtility.getErrorImage());
                log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Unable to generate thumbnail"), t);
            } finally {
                if (graphics != null) {
                    graphics.dispose();
                }
                pdPage = null;
            }
            ((VisualListModel) panel.getThumbnailList().getModel()).elementChanged(pageItem);
        }
        return retVal;
    }

    /**
     * @param height
     * @param width
     * @return percentage resize
     */
    private double getResizePercentage(double height, double width) {
        double retVal = 0;
        if (height >= width) {
            retVal = Math.round((Configuration.getInstance().getThumbnailSize() / height) * 100.0) / 100.0;
        } else {
            retVal = Math.round((Configuration.getInstance().getThumbnailSize() / width) * 100.0) / 100.0;
        }
        return retVal < 1 ? retVal : 1d;
    }
}
