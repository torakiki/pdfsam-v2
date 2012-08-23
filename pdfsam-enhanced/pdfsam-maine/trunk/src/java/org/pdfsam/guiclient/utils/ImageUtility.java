/*
 * Created on 17-Jan-2009
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
package org.pdfsam.guiclient.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Image utility
 * 
 * @author Andrea Vacondio
 * 
 */
public class ImageUtility {

    private static final Logger LOG = Logger.getLogger(ImageUtility.class.getPackage().getName());

    private static BufferedImage ERROR_IMAGE = null;

    private static BufferedImage HOURGLASS = null;

    static {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }

    /**
     * @param inputImage
     * @param degrees
     * @return rotated image
     */
    public static BufferedImage rotateImage(Image inputImage, int degrees) {
        BufferedImage retVal = null;
        RenderedImage ri = JAI.create("awtImage", inputImage);
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(ri);
        TransposeType rotOp = null;
        RenderedOp op = null;
        if (degrees == 90) {
            rotOp = TransposeDescriptor.ROTATE_90;
        } else if (degrees == 180) {
            rotOp = TransposeDescriptor.ROTATE_180;
        } else if (degrees == 270) {
            rotOp = TransposeDescriptor.ROTATE_270;
        }
        if (rotOp != null) {
            // use Transpose operation
            pb.add(rotOp);
            op = JAI.create("transpose", pb);
        } else {
            // setup "normal" rotation
            pb.add(ri.getWidth() / 2.0f);
            pb.add(ri.getHeight() / 2.0f);
            pb.add((float) Math.toRadians(degrees));
            pb.add(new InterpolationNearest());
            op = JAI.create("Rotate", pb, null);
        }
        PlanarImage myPlanar = op.createInstance();
        retVal = myPlanar.getAsBufferedImage();
        return retVal;
    }

    /**
     * @return an image displaying an error message
     */
    public static BufferedImage getErrorImage() {
        try {
            if (ERROR_IMAGE == null) {
                InputStream is = ImageUtility.class.getResourceAsStream("/images/thumbnailerror.png");
                BufferedImage img = ImageIO.read(is);
                is.close();
                // convert to TYPE_INT_RGB
                ERROR_IMAGE = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = (Graphics2D) ERROR_IMAGE.getGraphics();
                g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
                g2d.dispose();
            }
        } catch (IOException e) {
            LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Unable to create error image."));
        }
        return ERROR_IMAGE;
    }

    /**
     * @return am image displaying an hourglass
     */
    public static BufferedImage getHourglassImage() {
        try {
            if (HOURGLASS == null) {
                InputStream is = ImageUtility.class.getResourceAsStream("/images/hourglass.png");
                BufferedImage img = ImageIO.read(is);
                is.close();
                // convert to TYPE_INT_RGB
                HOURGLASS = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = (Graphics2D) HOURGLASS.getGraphics();
                g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
                g2d.dispose();
            }
        } catch (IOException e) {
            LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Unable to create error image."));
        }
        return HOURGLASS;
    }

    /**
     * @param o
     * @return a byte[] representing the input image
     * @throws IOException
     */
    public static byte[] toByteArray(BufferedImage o) throws IOException {
        if (o != null) {
            BufferedImage image = o;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ImageIO.write(image, "jpeg", baos);
            byte[] b = baos.toByteArray();
            return b;
        }
        return new byte[0];
    }

    /**
     * @param imagebytes
     * @return the Buffered image represented by the byte[]
     * @throws IOException
     */
    public static BufferedImage fromByteArray(byte[] imagebytes) throws IOException {
        BufferedImage retVal = null;
        if (imagebytes != null && (imagebytes.length > 0)) {
            retVal = ImageIO.read(new ByteArrayInputStream(imagebytes));
        }
        return retVal;
    }

    /**
     * 
     * @param img
     *            input image
     * @param targetWidth
     * @param targetHeight
     * @return a scaled image
     */
    public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight) {
        int type = (img.getTransparency() == java.awt.Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        int w = img.getWidth();
        int h = img.getHeight();

        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }
}
