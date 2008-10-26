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
}
