/*
 * Created on 26-Oct-2008
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
package org.pdfsam.guiclient.business;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;

/**
 * Provides transformation to the JPreviewImage Component
 * @author Andrea Vacondio
 *
 */
public class TransformationHandler {

	/**
	 * @param theta angle in radians
	 * @return rotation transform of theta
	 */
	public AffineTransform getRotateTransform(double theta) {
		AffineTransform retVal = new AffineTransform();
		retVal.rotate(theta);
		return retVal;
	}
	/**
	 * @param sx
	 * @param sy
	 * @return scaling transformation
	 */
	public AffineTransform getScaleTransform(double sx, double sy) {
		AffineTransform retVal = new AffineTransform();
		retVal.scale(sx,sy);
		return retVal;
	}
	/**
	 * @return a flip vertically transformation
	 */
	public AffineTransform getFlipVerticalTransform(){
		return getScaleTransform(1.0, -1.0);
	}
	
	/**
	 * @return a flip horizontally transformation
	 */
	public AffineTransform getFlipHorizontalTransform(){
		return getScaleTransform(-1.0, 1.0);
	}


    /**
     * Scales the currently displayed image
     * @param z the zoom factor
     */
    public AffineTransform getZoomTransformation(double z) {
    	AffineTransform retVal = new AffineTransform();
        double x = retVal.getScaleX(),
               y = retVal.getScaleY();
        if (z != 0 && x != 0 && y != 0) {
            x = (x < 0)? -z: z;
            y = (y < 0)? -z: z;
            retVal.setToScale(x, y);
        }
        return retVal;
    }

    /**
     * 
     * @param size dimension to fit
     * @param sourceDimension original dimension
     * @return transformation
     */
    public AffineTransform getFitTransformation(Dimension size, Dimension sourceDimension) {
        double sourceScale = sourceDimension.width / (double)sourceDimension.height,
               targetScale = size.getWidth() / (double)size.getHeight(),
               zoom = 1.0;
        if (sourceScale > targetScale) {
            zoom = size.width / (double)sourceDimension.width;
        } else if (sourceScale < targetScale) {
            zoom = size.height / (double)sourceDimension.height;
        }
        return getZoomTransformation(zoom);
    }
}
