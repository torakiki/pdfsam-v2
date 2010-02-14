/*
 * Created on 13-Feb-2010 
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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JPreviewImage;
import org.pdfsam.i18n.GettextResource;

/**
 * Action for the rotate anti clockwise function
 * 
 * @author Andrea Vacondio
 * 
 */
public class RotateLeftImageAction extends AbstractImageAction {

	private static final long serialVersionUID = -5738720899007881368L;

	/**
	 * 
	 * @param previewImage
	 * @param parent
	 */
	public RotateLeftImageAction(JPreviewImage previewImage) {
		super(previewImage, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
				"Rotate anticlockwise"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Rotate anticlockwise"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (getPreviewImage() != null) {
			getPreviewImage().rotateAntiClockwise();
		}
	}

}
