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
package org.pdfsam.guiclient.business.listeners.mediators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pdfsam.guiclient.business.TransformationHandler;
import org.pdfsam.guiclient.gui.components.JPreviewImage;
/**
 * Mediator between JPreviewImage and TransformationHandler
 * @author Andrea Vacondio
 *
 */
public class TransformationHandlerMediator implements ActionListener {

	public static final String FLIP_VER_ACTION = "fvertical";
	public static final String FLIP_HOR_ACTION = "fhorizontal";
	public static final String ROTATE_LEFT_ACTION = "rleft";
	public static final String ROTATE_RIGHT_ACTION = "rright";
	public static final String ZOOM_IN_ACTION = "zoomin";
	public static final String ZOOM_OUT_ACTION = "zoomout";
	public static final String ZOOM_NORMAL_ACTION = "zoomnormal";
	
	public static final double ZOOM_RATE = 0.1d;
	
	private JPreviewImage image = null;
	private TransformationHandler handler = null;
	
	/**
	 * @param handler
	 * @param image
	 */
	public TransformationHandlerMediator(TransformationHandler handler, JPreviewImage image) {
		super();
		this.handler = handler;
		this.image = image;
	}

	public void actionPerformed(ActionEvent e) {
		if(handler != null && e != null && image != null){
			if(FLIP_VER_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getFlipVerticalTransform());
			}else if(FLIP_HOR_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getFlipHorizontalTransform());
			}else if(ROTATE_LEFT_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getRotateTransform(-(Math.PI/2)));
			}else if(ROTATE_RIGHT_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getRotateTransform(Math.PI/2));
			}else if(ZOOM_IN_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getZoomTransformation(ZOOM_RATE));
			}else if(ZOOM_OUT_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getZoomTransformation(-ZOOM_RATE));
			}else if(ZOOM_NORMAL_ACTION.equals(e.getActionCommand())){
				image.setTransformation(handler.getZoomTransformation(1));
			}
		}
	}

}
