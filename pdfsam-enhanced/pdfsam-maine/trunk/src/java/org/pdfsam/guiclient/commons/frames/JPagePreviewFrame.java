/*
 * Created on 09-Nov-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.frames;

import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JPreviewImage;
import org.pdfsam.i18n.GettextResource;
/**
 * Frame to open the single page preview
 * @author Andrea Vacondio
 *
 */
public class JPagePreviewFrame extends JFrame {

	private static final long serialVersionUID = -7352665495415591680L;

	private static final Logger log = Logger.getLogger(JPagePreviewFrame.class.getPackage().getName());
	
	private final JPanel mainPanel = new JPanel();
	private JScrollPane mainScrollPanel;
	private final JPreviewImage pagePreview = new JPreviewImage();
	
	public JPagePreviewFrame(){
		initialize();
	}
	
	private void initialize() {
		try{	
			mainPanel.add(pagePreview);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			
			mainScrollPanel = new JScrollPane(mainPanel);
			add(mainScrollPanel);
		}catch(Exception e){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error creating preview panel."),e);
		}
	}
	
	/**
	 * sets the image to be displayed
	 * @param image
	 */
	public void setPagePreview(Image image){
		pagePreview.setImage(image);
	}
}
