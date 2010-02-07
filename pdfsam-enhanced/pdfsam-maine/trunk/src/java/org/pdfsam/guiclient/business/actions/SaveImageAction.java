/*
 * Created on 30-Oct-2008
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
package org.pdfsam.guiclient.business.actions;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JPreviewImage;
import org.pdfsam.guiclient.utils.filters.AbstractFileFilter;
import org.pdfsam.i18n.GettextResource;

/**
 * Action listener for the save commands
 * 
 * @author Andrea Vacondio
 * 
 */
public class SaveImageAction extends AbstractImageAction {

	private static final long serialVersionUID = 136283111853869669L;

	private static final Logger LOG = Logger.getLogger(SaveImageAction.class.getPackage().getName());

	private JFileChooser fileChooser;

	private JFrame parent;

	/**
	 * 
	 * @param previewImage
	 * @param parent
	 */
	public SaveImageAction(JPreviewImage previewImage, JFrame parent) {
		super(previewImage, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Save as"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Save the image"));
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		if (getPreviewImage() != null) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
				String[] types = ImageIO.getWriterFormatNames();
				HashSet<String> extensionsSet = new HashSet<String>();
				for (final String type : types) {
					extensionsSet.add(type.toLowerCase());
				}
				if (extensionsSet.size() > 0) {
					for (final String extension : extensionsSet) {
						fileChooser.addChoosableFileFilter(new AbstractFileFilter() {
							public String getAcceptedExtension() {
								return extension;
							}

							public String getDescription() {
								return extension;
							}
						});
					}
				}
			}
			if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				try {
					Image image = getPreviewImage().getImage();
					if (image instanceof RenderedImage) {
						ImageIO.write((RenderedImage) image, fileChooser.getFileFilter().getDescription(), fileChooser
								.getSelectedFile());
					}
				} catch (Exception ex) {
					LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
							"Unable to save image"), ex);
				}
			}
		}
	}

}
