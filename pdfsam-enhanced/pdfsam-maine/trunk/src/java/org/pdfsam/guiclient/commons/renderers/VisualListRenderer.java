/*
 * Created on 18-Jun-2008
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
package org.pdfsam.guiclient.commons.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.dto.VisualPageListItem;
/**
 * JList renderer
 * @author Andrea Vacondio
 *
 */
public class VisualListRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = -6125533840590452401L;
	
	private static final int HORIZONTAL_GAP = 6;
	private static final int VERTICAL_GAP = 25;
	
	private boolean drawRedCross = false;

	public VisualListRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		VisualPageListItem item = (VisualPageListItem)value;
		if(!item.isDeleted() || (item.isDeleted() && ((JVisualSelectionList)list).isDrawDeletedItems())){		
			if(item.getThumbnail()!= null){
				ImageIcon image = new ImageIcon(item.getThumbnail());
				setPreferredSize(getZoomedSize(image, (JVisualSelectionList)list));
				drawRedCross = item.isDeleted();
				setIcon(image);
			}
			setText(item.getPageNumber()+"");
			setHorizontalTextPosition(JLabel.CENTER);
			setVerticalTextPosition(JLabel.BOTTOM);
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.BOTTOM);
			setIconTextGap(3);
	        setForeground(list.getForeground());
	        setBackground(UIManager.getColor("Panel.background"));
			if(isSelected){
		        setBorder(BorderFactory.createLineBorder(Color.red, 1));
		    }else{
		        setBorder(BorderFactory.createLineBorder(list.getBackground()));
		    }	
		}	
		return this;		
	}
	
	/**
	 * @param image
	 * @param list
	 * @return the dimension of the image to be displayed according with the zoom level
	 */
	private Dimension getZoomedSize(ImageIcon image, JVisualSelectionList list){
		Dimension retVal = null;
		if(image != null && list != null){
			int height = image.getIconHeight();
			int width = image.getIconWidth();
			if (height>width){
				retVal = new Dimension(width+HORIZONTAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.HORIZONTAL_ZOOM_STEP), height+VERTICAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.VERTICAL_ZOOM_STEP));
			}else if (width>height){
				retVal = new Dimension(width+HORIZONTAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.VERTICAL_ZOOM_STEP), height+VERTICAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.HORIZONTAL_ZOOM_STEP));
			}else{
				retVal = new Dimension(width+HORIZONTAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.HORIZONTAL_ZOOM_STEP), height+VERTICAL_GAP+(list.getCurrentZoomLevel()*JVisualSelectionList.HORIZONTAL_ZOOM_STEP));
			}
		}
		return retVal;
	}	

	public void paintComponent(Graphics g){
		super.paintComponent(g);		
		if(drawRedCross){
			g.setColor(Color.red);	
			g.drawLine(0,getHeight(),getWidth(),0); 
			g.drawLine(getWidth(),getHeight(),0,0);
		}
	}
}
