/*
 * Created on 24-Jul-2009
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
package org.pdfsam.guiclient.commons.components;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.pdfsam.guiclient.business.listeners.PrefixMenuActionListener;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Popup menu to show default menu plus prefix menu items
 * @author Andrea Vacondio
 *
 */
public class PrefixPopupMenu extends DefaultPopupMenu {

	private static final long serialVersionUID = -9202595588797924181L;

	public static final int BASIC_MENU = 0;
	public static final int FULL_MENU = 1;
	
	public PrefixPopupMenu(int menuType, JTextField textField) {
		
		PrefixMenuActionListener listener = new PrefixMenuActionListener(textField);
		
		addSeparator();
		JMenu prefixMenu = new JMenu(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Add prefix"));

		JMenuItem menuTimestamp = new JMenuItem();
		menuTimestamp.setActionCommand(PrefixMenuActionListener.INSERT_TIMESTAMP_ACTION);
		menuTimestamp.setText("[TIMESTAMP]");
		menuTimestamp.addActionListener(listener);
		prefixMenu.add(menuTimestamp);
		
		JMenuItem menuBasename = new JMenuItem();
		menuBasename.setActionCommand(PrefixMenuActionListener.INSERT_BASENAME_ACTION);
		menuBasename.setText("[BASENAME] ");
		menuBasename.addActionListener(listener);
		prefixMenu.add(menuBasename);

		if(menuType == FULL_MENU){
			JMenuItem menuFilenumber = new JMenuItem();
			menuFilenumber.setActionCommand(PrefixMenuActionListener.INSERT_FILENUMBER_ACTION);
			menuFilenumber.setText("[FILENUMBER] ");
			menuFilenumber.addActionListener(listener);
			prefixMenu.add(menuFilenumber);
	
			JMenuItem menuCurrentpage = new JMenuItem();
			menuCurrentpage.setActionCommand(PrefixMenuActionListener.INSERT_CURRENTPAGE_ACTION);
			menuCurrentpage.setText("[CURRENTPAGE]");
			menuCurrentpage.addActionListener(listener);
			prefixMenu.add(menuCurrentpage);
	
			JMenuItem menuBookmark = new JMenuItem();
			menuBookmark.setActionCommand(PrefixMenuActionListener.INSERT_BOOKMARK_NAME_ACTION);
			menuBookmark.setText("[BOOKMARK_NAME] ");
			menuBookmark.addActionListener(listener);
			prefixMenu.add(menuBookmark);
		}

		add(prefixMenu);
	}
}
