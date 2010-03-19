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
package org.pdfsam.guiclient.gui.components;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Help icon with long html ToolTip
 * 
 * @author a.vacondio
 * 
 */
public class JHelpLabel extends JLabel {

	private static final long serialVersionUID = 1488661423870319925L;

	private static ImageIcon icon = new ImageIcon(JHelpLabel.class.getResource("/images/help.png"));

	/**
	 * @param text
	 *            Text of the help ToolTip
	 * @param isHtml
	 *            <code>true</code> if it's already an html text
	 */
	public JHelpLabel(String text, boolean isHtml) {
		super();
		if (isHtml) {
			setToolTipText(text);
		} else {
			setToolTipText(HTMLEncode(text));
		}
		setPreferredSize(new Dimension(18, 18));
		setMaximumSize(new Dimension(18, 18));
		setMinimumSize(new Dimension(18, 18));
		setIcon(icon);
	}

	/**
	 * Thanks to benoit.heinrich on forum.java.sun.com
	 * 
	 * @param str
	 * @return html encoded string
	 */
	private String HTMLEncode(String str) {
		StringBuffer sbhtml = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == ' ')
					|| (ch == '\n')) {
				sbhtml.append(ch);
			} else {
				sbhtml.append("&#");
				sbhtml.append((int) ch);
			}
		}
		return "<html><body>" + sbhtml.toString().replaceAll("\n", "<br>") + "</body></html>";
	}

}
