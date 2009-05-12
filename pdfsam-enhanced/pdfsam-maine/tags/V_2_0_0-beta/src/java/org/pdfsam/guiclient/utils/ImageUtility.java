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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Image utility
 * @author Andrea Vacondio
 *
 */
public class ImageUtility {

	private static final Logger log = Logger.getLogger(ImageUtility.class.getPackage().getName());

	private static BufferedImage ERROR_IMAGE = null;
	public static  BufferedImage HOURGLASS = null;
	
	static{
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}
	
	/**
	 * @param inputImage
	 * @param degrees
	 * @return rotated image
	 */
	public static synchronized BufferedImage rotateImage(Image inputImage, int degrees){
		BufferedImage retVal = null;
		RenderedImage ri = JAI.create("awtImage", inputImage);
		ParameterBlock pb = new ParameterBlock();
		pb.addSource(ri);
		pb.add(ri.getWidth() / 2.0f);
		pb.add(ri.getHeight() / 2.0f);
		pb.add((float) Math.toRadians(degrees));
		pb.add(new InterpolationNearest());
		RenderedOp op = JAI.create("Rotate", pb, null);
		PlanarImage myPlanar = op.createInstance();
		retVal = myPlanar.getAsBufferedImage();
		return retVal;
	}
	
	/**
	 * @return an image displaying an error message
	 */
	public static BufferedImage getErrorImage(){
		try {
			if(ERROR_IMAGE == null){
				ERROR_IMAGE = ImageIO.read(ImageUtility.class.getResourceAsStream("/images/thumbnailerror.png"));
			}
		} catch (IOException e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create error image."));
		};
		return ERROR_IMAGE;
	}
	
	/**
	 * @return am image displaying an hourglass
	 */
	public static BufferedImage getHourglassImage(){
		try {
			if(HOURGLASS == null){
				HOURGLASS =  ImageIO.read(ImageUtility.class.getResourceAsStream("/images/hourglass.png"));
			}
		} catch (IOException e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to create error image."));
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
			BufferedImage image = (BufferedImage) o;
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
}
