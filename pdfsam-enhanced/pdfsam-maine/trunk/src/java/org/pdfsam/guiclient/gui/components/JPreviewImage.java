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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
/**
 * Component used to display an image
 * @author Andrea Vacondio
 *
 */
public class JPreviewImage extends JComponent {

	private static final long serialVersionUID = -8027227547898755631L;

	private Image image;
    private Rectangle bounds;
    private AffineTransform transformation;
    private Dimension originalDimension;
	
    /**
	 * @param image
	 */
	public JPreviewImage(Image image) {
		this();
		setImage(image);	
	}
	
	public JPreviewImage(){
		this.bounds = new Rectangle();
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		resetComponent();
		if(image != null){
			this.image = image;
			this.originalDimension = new Dimension(image.getWidth(this), image.getHeight(this));
		}
		this.bounds = new Rectangle();
		setTransformation(null);
	}

    @Override
    public void setBounds(int x, int y, int width, int height) {
        boolean resized = width != getWidth() || height != getHeight();
        super.setBounds(x, y, width, height);
        if (resized) {
        	setTransformation(null);
        }
    }
	/**
	 * @return the transformation
	 */
	public AffineTransform getTransformation() {
		return transformation;
	}

	/**
	 * @param transformation the transformation to set
	 */
	public void setTransformation(AffineTransform transformation) {
        initBounds(transformation);
		AffineTransform centerTrans = getCenteredTransformation();
		if(transformation != null){
			centerTrans.concatenate(transformation);
		}
		this.transformation = centerTrans;
        revalidate();
        repaint();        
	}

	/**
	 * @return the originalDimension
	 */
	public Dimension getOriginalDimension() {
		return originalDimension;
	}
	/**
	 * @return the dimension after transformation
	 */
    public Dimension getTransformedDimension() {
        return bounds.getSize();
    }

    public Dimension getPreferredSize() {
    	Dimension retVal = super.getPreferredSize();
    	if(image!=null && !super.isPreferredSizeSet()){
    		retVal = getTransformedDimension();
    	}
    	return retVal;
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
	    Graphics2D graphics = (Graphics2D)g;
	    graphics.setBackground(getBackground());
	    graphics.clearRect(0, 0, getWidth(), getHeight());
	    if(image != null){
	    	graphics.drawImage(image, transformation, this);
	    }
	    g.dispose();
    }
    
    private AffineTransform getCenteredTransformation () {
    	AffineTransform retVal = null;
        if (image != null) {
        	retVal = new AffineTransform();
            // Apply transformations
            Dimension size = getSize();
            // Tracks the lengths needed to center canvas
            int x = -bounds.x, y = -bounds.y;
            float xScale = 1, yScale = 1;
            if (bounds.height < size.height) {
                y += (size.height - bounds.height) / 2;
            } else if (bounds.height > 0) {
                yScale = (float)size.height / bounds.height;
            }
            if (bounds.width < size.width) {
                x += (size.width - bounds.width) / 2;
            } else if (bounds.width > 0) {
                xScale = (float)size.width / bounds.width;
            }
            retVal.scale(xScale, yScale);
            retVal.translate(x, y);
        }
        return retVal;
    }

 
    private void initBounds(AffineTransform inputTransformation) {
        bounds = new Rectangle(originalDimension);
        if (image != null && inputTransformation != null) {
            // Determine the bounding rectangle of the canvas before
            // userTransformation
            Point[] points = new Point[] {
              new Point(0, 0),
              new Point(originalDimension.width, 0),
              new Point(0, originalDimension.height),
              new Point(originalDimension.width, originalDimension.height)
            };
            Point bottomRight = new Point();
            for (Point current: points) {
                transformation.deltaTransform(current, current);
                if (bounds.x > current.x) {
                    bounds.x = current.x;
                }
                if (bounds.y > current.y) {
                    bounds.y = current.y;
                }
                if (bottomRight.x < current.x) {
                    bottomRight.x = current.x;
                }
                if (bottomRight.y < current.y) {
                    bottomRight.y = current.y;
                }
            }
            bounds.width = Math.abs(bottomRight.x - bounds.x);
            bounds.height = Math.abs(bottomRight.y - bounds.y);
        }
    }
	/**
	 * reset the component
	 */
	private void resetComponent(){
		this.image = null;
		this.bounds = null;
		this.originalDimension=null;
		this.transformation=null;
		
	}
    
}
