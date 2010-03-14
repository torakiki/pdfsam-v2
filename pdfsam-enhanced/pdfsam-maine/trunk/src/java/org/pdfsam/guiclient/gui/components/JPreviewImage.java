/*
 * Created on 24-Oct-2008
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
package org.pdfsam.guiclient.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import org.pdfsam.guiclient.utils.ImageUtility;

/**
 * Component used to display an image
 * 
 * @author Andrea Vacondio
 * 
 */
public class JPreviewImage extends JComponent {

	private static final long serialVersionUID = -8027227547898755631L;

	private static final Double ZOOM_STEP = Double.valueOf("0.05");

	private Image image;

	private Rectangle bounds;

	private AffineTransform transformation;

	private Double zoomLevel = Double.valueOf("1");

	private Dimension originalDimension;

	/**
	 * @param image
	 */
	public JPreviewImage(Image image) {
		this();
		setImage(image);
	}

	public JPreviewImage() {
		this.bounds = new Rectangle();
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(Image image) {
		resetComponent();
		if (image != null) {
			this.image = image;
			originalDimension = new Dimension(image.getWidth(this), image.getHeight(this));
			this.bounds = new Rectangle(originalDimension);
		}
		initTransformation();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		boolean resized = width != getWidth() || height != getHeight();
		super.setBounds(x, y, width, height);
		if (resized) {
			initTransformation();
		}
	}

	/**
	 * init
	 */
	public void initTransformation() {
		this.transformation = getCenteredTransformation();
		revalidate();
		repaint();
	}

	public Dimension getPreferredSize() {
		Dimension retVal = super.getPreferredSize();
		if (image != null && !super.isPreferredSizeSet()) {
			retVal = bounds.getSize();
		}
		return retVal;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		graphics.setBackground(getBackground());
		graphics.clearRect(0, 0, getWidth(), getHeight());
		if (image != null) {
			graphics.drawImage(image, transformation, this);
		}
		g.dispose();
	}

	private AffineTransform getCenteredTransformation() {
		AffineTransform retVal = null;
		if (image != null) {
			retVal = new AffineTransform();
			Dimension size = getSize();
			// Tracks the lengths needed to center canvas
			int x = -bounds.x, y = -bounds.y;
			if (bounds.height < size.height) {
				y += (size.height - bounds.height) / 2;
			}
			if (bounds.width < size.width) {
				x += (size.width - bounds.width) / 2;
			}
			retVal.scale(bounds.width / originalDimension.getWidth(), bounds.height / originalDimension.getHeight());
			retVal.translate(x, y);
		}
		return retVal;
	}

	/**
	 * reset the component
	 */
	public void resetComponent() {
		this.image = null;
		this.bounds = null;
		this.transformation = null;
		this.zoomLevel = Double.valueOf("1");
		this.originalDimension = null;
	}

	/**
	 * increment zoom level
	 */
	public void zoomIn() {
		zoomLevel += ZOOM_STEP;
		initRectangle();
	}

	/**
	 * decrement zoom level
	 */
	public void zoomOut() {
		zoomLevel -= ZOOM_STEP;
		initRectangle();
	}

	/**
	 * remove any zoom
	 */
	public void zoomNone() {
		zoomLevel = Double.valueOf("1");
		initRectangle();
	}

	/**
	 * Perform a zoom multiplying the ZOOM_STEP by the input parameter.
	 * 
	 * @param numberOfSteps
	 *            a positive number zooms in, a negative zooms out.
	 */
	public void zoom(int numberOfSteps) {
		zoomLevel += (ZOOM_STEP * numberOfSteps);
		initRectangle();
	}

	/**
	 * rotate clockwise
	 */
	public void rotateClockwise() {
		image = ImageUtility.rotateImage(image, 90);
		originalDimension = new Dimension(image.getWidth(this), image.getHeight(this));
		initRectangle();
	}

	/**
	 * rotate anti clockwise
	 */
	public void rotateAntiClockwise() {
		image = ImageUtility.rotateImage(image, 270);
		originalDimension = new Dimension(image.getWidth(this), image.getHeight(this));
		initRectangle();
	}

	private void initRectangle() {
		if (bounds != null) {
			int width = (int) (originalDimension.getWidth() * zoomLevel);
			int height = (int) (originalDimension.getHeight() * zoomLevel);
			bounds = new Rectangle(width, height);
			initTransformation();
		}
	}
}
