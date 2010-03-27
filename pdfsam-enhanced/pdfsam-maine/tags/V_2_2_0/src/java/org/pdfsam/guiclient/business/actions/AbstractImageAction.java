/*
 * Created on 7-Feb-2010 
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
package org.pdfsam.guiclient.business.actions;

import javax.swing.AbstractAction;

import org.pdfsam.guiclient.gui.components.JPreviewImage;

/**
 * Abstract action for the Preview Image panel
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractImageAction extends AbstractAction {

	private static final long serialVersionUID = -4440999375768544266L;

	private JPreviewImage previewImage;

	/**
	 * @param previewImage
	 * @param name
	 *            name of the action
	 */
	public AbstractImageAction(JPreviewImage previewImage, String name) {
		super(name);
		this.previewImage = previewImage;
	}

	/**
	 * @return the previewImage
	 */
	JPreviewImage getPreviewImage() {
		return previewImage;
	}

	/**
	 * @param previewImage
	 *            the previewImage to set
	 */
	void setPreviewImage(JPreviewImage previewImage) {
		this.previewImage = previewImage;
	}

}
