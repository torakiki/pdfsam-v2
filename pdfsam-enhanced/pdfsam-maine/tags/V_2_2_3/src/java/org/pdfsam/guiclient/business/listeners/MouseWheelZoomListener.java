/*
 * Created on 14/mar/2010
 * Copyright (C) 2010 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.pdfsam.guiclient.gui.components.JPreviewImage;

/**
 * Listen for the mouse wheel to perform zoom operations on the
 * {@link JPreviewImage}
 * 
 * @author Andrea Vacondio
 * 
 */
public class MouseWheelZoomListener implements MouseWheelListener {

	private JPreviewImage image;

	/**
	 * @param image
	 */
	public MouseWheelZoomListener(JPreviewImage image) {
		super();
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
	 * MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
			if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
				image.zoom(e.getWheelRotation());
			}
		}
	}
}
