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
package org.pdfsam.guiclient.business.listeners;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JPreviewImage;
import org.pdfsam.guiclient.utils.filters.AbstractFileFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Action listener for the save commands
 * @author Andrea Vacondio
 *
 */
public class SaveImageActionListener implements ActionListener {

	private static final Logger log = Logger.getLogger(SaveImageActionListener.class.getPackage().getName());

	public static final String SAVE_AS_ACTION = "saveas";
	
	private JFileChooser fileChooser;
	private JPreviewImage previewImage;
	private JFrame parent;
	
	/**
	 * @param previewImage
	 */
	public SaveImageActionListener(JPreviewImage previewImage, JFrame parent) {
		super();
		this.previewImage = previewImage;
		this.parent = parent;
	}


	public void actionPerformed(ActionEvent e) {
		if(previewImage!=null){
			if(fileChooser==null){
				fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
				String[] types = ImageIO.getWriterFormatNames();
				HashSet<String> extensionsSet = new HashSet<String>();
				for(final String type : types){
					extensionsSet.add(type.toLowerCase());
				}
				if(extensionsSet.size()>0){
					for(final String extension : extensionsSet){
						fileChooser.addChoosableFileFilter(new AbstractFileFilter(){
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
		                Image image = previewImage.getImage();
		                if (image instanceof RenderedImage) {
		                    ImageIO.write((RenderedImage)image,fileChooser.getFileFilter().getDescription(),fileChooser.getSelectedFile());
		                }
		            } catch (Exception ex) {
		                log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Unable to save image"), ex);
		            }
			 }
		}
	}


	/**
	 * @return the previewImage
	 */
	public JPreviewImage getPreviewImage() {
		return previewImage;
	}


	/**
	 * @param previewImage the previewImage to set
	 */
	public void setPreviewImage(JPreviewImage previewImage) {
		this.previewImage = previewImage;
	}

	
}
