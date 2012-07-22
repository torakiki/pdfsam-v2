/*
 * Created on 06-Sep-2009
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

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.pdfsam.guiclient.business.IdManager;
import org.pdfsam.guiclient.business.thumbnails.creators.IcePdfThumbnailsCreator;
import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.utils.ImageUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * Callable used to generate thumbnails with ICEpdf
 * 
 * @author Andrea Vacondio
 * 
 */
public class IcePdfThumbnailCallable implements Callable<Boolean> {

    private static final Logger log = Logger.getLogger(IcePdfThumbnailCallable.class.getPackage().getName());

    private Document pdfDocument;
    private JVisualPdfPageSelectionPanel panel;
    private VisualPageListItem pageItem;
    private long id;

    /**
     * @param pdfDocument
     *            document
     * @param panel
     * @param pageItem
     * @param id
     */
    public IcePdfThumbnailCallable(Document pdfDocument, VisualPageListItem pageItem,
            JVisualPdfPageSelectionPanel panel, long id) {
        super();
        this.pdfDocument = pdfDocument;
        this.panel = panel;
        this.pageItem = pageItem;
        this.id = id;
    }

    public Boolean call() {
        Boolean retVal = Boolean.FALSE;
        if (!IdManager.getInstance().isCancelledExecution(id)) {
            try {
                int pageNumber = pageItem.getPageNumber() - 1;
                PDimension dimensions = pdfDocument.getPageDimension(pageNumber, 0);
                double rectHeight = dimensions.getHeight();
                double recWidth = dimensions.getWidth();
                float resizePercentage = (float) getResizePercentage(rectHeight, recWidth);

                BufferedImage scaledInstance = (BufferedImage) pdfDocument.getPageImage(pageNumber, (Configuration
                        .getInstance().isHighQualityThumbnails() ? GraphicsRenderingHints.PRINT
                        : GraphicsRenderingHints.SCREEN), Page.BOUNDARY_CROPBOX, 0, resizePercentage);
                pageItem.setPaperFormat(recWidth, rectHeight, IcePdfThumbnailsCreator.ICEPDF_RESOLUTION);
                int rotation = (int) pdfDocument.getPageTree().getPage(pageNumber, this).getTotalRotation(0);
                if (rotation != 0) {
                    pageItem.setOriginalRotation(Rotation.getRotation(rotation));
                }
                pageItem.setThumbnail(scaledInstance);
                retVal = Boolean.TRUE;
            } catch (Throwable t) {
                pageItem.setThumbnail(ImageUtility.getErrorImage());
                log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Unable to generate thumbnail"), t);
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
