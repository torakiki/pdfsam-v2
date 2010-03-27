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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

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
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Save the image"));
		this.parent = parent;
	}

	public void actionPerformed(ActionEvent e) {
		if (getPreviewImage() != null) {
			if (fileChooser == null) {
				fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDirectory());
				fileChooser.setAcceptAllFileFilterUsed(false);
				Set<String> types = unique(ImageIO.getWriterFileSuffixes());
				for (final String extension : types) {
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
			if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
				try {
					File selectedFile = fileChooser.getSelectedFile();
					String extension = getExtension(selectedFile);
					// no extension to the file or the user typed some custom
					// extension non recognized. Apply the one on the filter.
					if (extension == null || !accept(fileChooser.getChoosableFileFilters(), selectedFile)) {
						extension = fileChooser.getFileFilter().getDescription();
						selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
					}

					Image image = getPreviewImage().getImage();
					if (image instanceof RenderedImage) {
						Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(extension);
						if (writers.hasNext()) {
							ImageWriter writer = writers.next();
							writer.setOutput(ImageIO.createImageOutputStream(selectedFile));
							writer.write((RenderedImage) image);
						} else {
							// should never happen
							LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
									"Unable to save image"));
						}
					}
				} catch (Exception ex) {
					LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
							"Unable to save image"), ex);
				}
			}
		}
	}

	/**
	 * 
	 * @param filters
	 * @param selectedFile
	 * @return true if one of the filters accept the input file
	 */
	private boolean accept(FileFilter[] filters, File selectedFile) {
		for (FileFilter filter : filters) {
			if (filter.accept(selectedFile)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param f
	 * @return the file extension
	 */
	private String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private Set<String> unique(String[] strings) {
		Set<String> set = new HashSet<String>();
		for (String current : strings) {
			set.add(current.toLowerCase());
		}
		return set;
	}
}
