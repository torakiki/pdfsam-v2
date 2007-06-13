/*
 * Created on 05-Apr-2007
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
package it.pdfsam.components;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;
/**
 * Help icon with long html ToolTip
 * @author a.vacondio
 *
 */
public class JHelpLabel extends JLabel {

	private static final long serialVersionUID = 1488661423870319925L;
	private static byte[] imageBytes = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 16, 0, 0, 0, 16, 8, 6, 0, 0, 0, 31, -13, -1, 97, 0, 0, 0, 6, 98, 75, 71, 68, 0, -12, 0, 19, 0, 19, -34, 41, 17, 21, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 15, 18, 0, 0, 15, 18, 1, 33, -101, -14, 51, 0, 0, 0, 7, 116, 73, 77, 69, 7, -41, 5, 21, 16, 53, 40, -25, 106, 103, -42, 0, 0, 0, 29, 116, 69, 88, 116, 67, 111, 109, 109, 101, 110, 116, 0, 67, 114, 101, 97, 116, 101, 100, 32, 119, 105, 116, 104, 32, 84, 104, 101, 32, 71, 73, 77, 80, -17, 100, 37, 110, 0, 0, 1, -36, 73, 68, 65, 84, 56, -53, 117, -109, -49, 106, 83, 81, 16, -58, 127, 51, -25, -74, -40, -83, 110, -62, 125, -116, -42, 80, -117, -48, 117, -80, 20, -21, -54, -107, 47, 33, 34, 52, 77, -82, 109, -13, -89, 27, -33, -62, 87, 40, 18, -36, -71, -79, -120, 54, -35, -11, 17, 46, -79, 20, -92, -108, 70, -112, -34, 25, 23, 39, 55, -71, 73, -51, -64, 97, 14, -25, -98, -7, -50, 55, -33, 119, 71, 46, -73, -74, -9, 0, 16, -123, -96, 49, -85, 32, 26, 64, 4, 84, 39, 75, 64, 67, -52, 18, -49, -4, -41, -120, 4, 81, 62, 53, 63, 126, -89, 18, -95, 54, -56, -35, 12, 55, -57, -54, 92, 56, -55, -19, -53, -76, 122, -17, 77, -1, -35, 102, -126, -54, 124, -95, 59, 118, 111, -12, 54, 51, -36, 29, -111, -8, -67, 125, -34, -61, 30, 125, -50, 53, 40, -59, -88, 17, -127, 84, 72, -48, 16, -85, -97, -100, -26, -59, 95, -24, 61, -53, 28, 16, 51, -29, -61, -59, 9, 110, 70, -73, -34, -90, -77, 113, -128, -69, 123, 54, -20, 75, -88, 13, -14, 98, -44, 72, -47, -64, -108, -127, 6, -91, -13, -76, -27, -128, 100, -61, 62, 101, 11, 0, -5, -33, 14, 113, 115, 78, -98, 31, -54, -15, 122, -45, -77, 97, 95, 74, 6, 42, -94, -124, -38, 32, 23, 85, 68, 68, -102, 103, 71, 0, -40, -43, -117, -44, -81, 119, 82, -65, -34, 73, -27, -9, 110, -70, -78, -74, 74, 54, -20, 3, -120, 21, 54, -87, 9, 40, -86, 0, 28, -81, 55, 61, -102, 81, -23, -79, 18, -27, -103, -69, -45, -83, -73, 28, -64, -1, -116, 81, 31, -33, 1, 80, -46, -110, -118, -88, 85, 113, 67, 109, -112, 91, 97, -88, 42, 111, -65, -20, -53, -19, -43, 13, -120, 70, 17, -117, 81, 35, 13, -75, 65, -34, 62, -17, 33, 2, 14, -8, -29, -45, 92, 85, 38, -128, 90, -118, 24, -99, 81, 97, -19, -2, 117, -118, -2, -100, -39, -72, 72, 59, 89, 77, -24, 108, 28, 80, -46, 118, 119, -34, 127, 109, -29, 54, -77, 54, 50, 16, -27, 127, 97, -123, 77, -9, -51, -77, 35, -52, -30, -53, 43, 119, -81, 102, 15, 5, 37, 41, 69, 92, 12, 13, 74, -21, 71, 23, 55, 3, 17, -62, -51, -18, 3, 97, 69, 20, -107, 37, 0, 0, -35, 122, -117, -34, 102, -122, 44, -69, -96, 58, -77, -15, 65, 11, -9, -59, -52, -62, 74, 59, -117, 0, 115, -77, 48, 71, 79, 53, -2, -1, -123, -59, 54, -106, 0, -56, -27, -42, -10, 94, 28, 83, -99, 46, -87, -116, -20, -36, 40, -117, 76, 71, 94, 84, -16, -15, -104, 127, 112, -4, -46, 38, -88, 45, -32, -59, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
	private static ImageIcon icon = new ImageIcon(imageBytes);
	
	/**
	 * @param text Text of the help ToolTip
	 * @param isHtml <code>true</code> if it's alread an html text
	 */
	public JHelpLabel(String text, boolean isHtml) {
		super();
		if(isHtml){
			setToolTipText(text);
		}
		else{
			setToolTipText(HTMLEncode(text));
		}
		setPreferredSize(new Dimension(18, 18));
		setMaximumSize(new Dimension(18, 18));
		setMinimumSize(new Dimension(18, 18));
		setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
		setIcon(icon);
	}
	
	/**
	 * Thanks to benoit.heinrich on forum.java.sun.com
	 * @param str
	 * @return
	 */
	private String HTMLEncode(String str){
		StringBuffer sbhtml = new StringBuffer();
			for (int i = 0 ; i < str.length() ; i++ ){
				char ch = str.charAt( i );
				if ( ( ch >= '0' && ch <= '9' ) ||	( ch >= 'A' && ch <= 'Z' ) || (ch >= 'a' && ch <= 'z') || (ch == ' ') || (ch == '\n')){
					sbhtml.append( ch );
				}
				else{
					sbhtml.append( "&#" );
					sbhtml.append( (int) ch );
				}
			}
			return "<html><body>"+sbhtml.toString().replaceAll("\n","<br>")+"</body></html>";
	} 

}
