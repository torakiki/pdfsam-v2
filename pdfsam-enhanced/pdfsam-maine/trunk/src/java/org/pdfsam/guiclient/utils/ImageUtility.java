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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
/**
 * Image utility
 * @author Andrea Vacondio
 *
 */
public class ImageUtility {

	
	/*static{
		System.setProperty("com.sun.media.jai.disableMediaLib", "true");
	}*/
	
	public static synchronized Image rotateImage(Image inputImage, int degrees){
		Image retVal = null;
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
}
