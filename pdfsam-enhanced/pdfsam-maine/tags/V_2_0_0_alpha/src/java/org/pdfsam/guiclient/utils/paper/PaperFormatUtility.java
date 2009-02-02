/*
 * Created on 16-Jan-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.utils.paper;

import java.util.ArrayList;

/**
 * Utility to get the paper format
 * @author Andrea Vacondio
 *
 */
public class PaperFormatUtility {

	private static ArrayList<PaperFormat> formats = null;
	
	/**
	 * return the description of the given format
	 * @param width
	 * @param height
	 * @return
	 */
	public static String getFormat(double width, double height){
		String retVal = Math.round(width)+"x"+Math.round(height);
		initFormats();
		PaperFormat inputValue = new PaperFormat(width, height, "", 0.0, 0.0);
		for(PaperFormat value : formats){
			if(value.isTolerable(inputValue)){
				retVal = value.getDescription();
				break;
			}
		}
		return retVal;
	}
	
	/**
	 * init the formats array
	 */
	private static synchronized void initFormats(){
		if(formats==null){
			formats = new ArrayList<PaperFormat>(30);
			formats.add(new PaperFormat(297, 420, "A3", 2.0, 2.0));
			formats.add(new PaperFormat(210, 297, "A4", 2.0, 2.0));
			formats.add(new PaperFormat(841, 1189, "A0", 3.0, 3.0));
			formats.add(new PaperFormat(594, 841, "A1", 2.0, 3.0));
			formats.add(new PaperFormat(420, 594, "A2", 2.0, 2.0));
			formats.add(new PaperFormat(148, 210, "A5", 1.5, 2.0));
			formats.add(new PaperFormat(105, 148, "A6", 1.5, 1.5));
			formats.add(new PaperFormat(74, 105, "A7", 1.5, 1.5));
			formats.add(new PaperFormat(52, 74, "A8", 1.5, 1.5));
			formats.add(new PaperFormat(37, 52, "A9", 1.5, 1.5));
			formats.add(new PaperFormat(26, 37, "A10", 1.5, 1.5));
			formats.add(new PaperFormat(1000, 1414, "B0", 3.0, 3.0));
			formats.add(new PaperFormat(707, 1000, "B1", 3.0, 3.0));
			formats.add(new PaperFormat(500, 707, "B2", 2.0, 2.0));
			formats.add(new PaperFormat(353, 500, "B3", 2.0, 2.0));
			formats.add(new PaperFormat(250, 353, "B4", 2.0, 2.0));
			formats.add(new PaperFormat(176, 250, "B5", 2.0, 2.0));
			formats.add(new PaperFormat(125, 176, "B6", 1.5, 2.0));
			formats.add(new PaperFormat(88, 125, "B7", 1.5, 1.5));
			formats.add(new PaperFormat(62, 88, "B8", 1.5, 1.5));
			formats.add(new PaperFormat(44, 62, "B9", 1.5, 1.5));
			formats.add(new PaperFormat(31, 44, "B10", 1.5, 1.5));
			formats.add(new PaperFormat(917 , 1297, "C0", 3.0, 3.0));
			formats.add(new PaperFormat(648 , 917, "C1", 3.0, 3.0));
			formats.add(new PaperFormat(458 , 648, "C2", 2.0, 3.0));
			formats.add(new PaperFormat(324 , 458, "C3", 2.0, 2.0));
			formats.add(new PaperFormat(229 , 324, "C4", 2.0, 2.0));
			formats.add(new PaperFormat(162 , 229, "C5", 2.0, 2.0));
			formats.add(new PaperFormat(114 , 162, "C6", 1.5, 2.0));
			formats.add(new PaperFormat(81 , 114, "C7", 1.5, 1.5));
			formats.add(new PaperFormat(57 , 81, "C8", 1.5, 1.5));
			formats.add(new PaperFormat(40 , 57, "C9", 1.5, 1.5));
			formats.add(new PaperFormat(28 , 40, "C10", 1.5, 1.5));
		}
	}
}
