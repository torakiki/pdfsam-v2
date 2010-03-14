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
import javax.swing.Icon;
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
	
	private static final double ZOOM_STEP = 0.1;
	private static final int COMPONENT_SIZE = 170;
	
	private int currentZoomLevel = 0;
	private boolean drawRedCross = false;
	private ImageIcon image = null;
	
	public VisualListRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		VisualPageListItem item = (VisualPageListItem)value;
		currentZoomLevel = ((JVisualSelectionList)list).getCurrentZoomLevel();
		if(!item.isDeleted() || (item.isDeleted() && ((JVisualSelectionList)list).isDrawDeletedItems())){		
			if(item.getThumbnail()!= null){
				image = new ImageIcon(item.getThumbnail());
				setPreferredSize(getZoomedSize());
				drawRedCross = item.isDeleted();
			}
			String text = item.getPageNumber()+"";
			if(item.getPaperFormat()!=null && item.getPaperFormat().length()>0){
				text += " - ["+item.getPaperFormat()+"]";
			}
			setText(text);
			setHorizontalTextPosition(JLabel.CENTER);
			setVerticalTextPosition(JLabel.BOTTOM);
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.BOTTOM);
			setIconTextGap(2);
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
	 * @return the dimension of the image to be displayed according with the zoom level
	 */
	private Dimension getZoomedSize(){
		Dimension retVal = null;
		int comSize = COMPONENT_SIZE+(int)(COMPONENT_SIZE*ZOOM_STEP*currentZoomLevel);
		retVal = new Dimension(comSize+2, comSize+15);
		return retVal;
	}	

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(image!=null){
			int imageHeight = 0;
			int imageWidth = 0;
			int x = 1;
			int y = 0;
			//get image dimensions
			if(isHorizontal(image)){
				imageWidth = getWidth()-2;
				imageHeight = Math.round(image.getIconHeight()*((float)imageWidth/(float)image.getIconWidth()));
				if(imageHeight>getHeight()-15){
					imageHeight = getHeight()-15;
				}
				y=(getHeight()-15-imageHeight)/2;
			}else{
				imageHeight = getHeight()-15;
				imageWidth = Math.round(image.getIconWidth()*((float)imageHeight/(float)image.getIconHeight()));
				if(imageWidth>getWidth()-2){
					imageWidth = getWidth()-2;
				}
				x=(getWidth()-2-imageWidth)/2;
			}
			g.drawImage((image).getImage(), x, y, imageWidth, imageHeight, null);
		}
		if(drawRedCross){
			g.setColor(Color.red);	
			g.drawLine(0,getHeight(),getWidth(),0); 
			g.drawLine(getWidth(),getHeight(),0,0);
		}
	}
	
	private boolean isHorizontal(Icon image){
		boolean retVal = false;
		if(image!=null){
			retVal = image.getIconWidth()>image.getIconHeight();
		}
		return retVal;
	}
}
