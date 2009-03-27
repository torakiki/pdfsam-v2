/*
 * Created on 14-SEP-2008
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
package org.pdfsam.guiclient.commons.components.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Default listener that shows a popup menu with copy/cut/paste items
 * 
 * @author Andrea Vacondio
 * 
 */
public class DefaultMouseListener implements MouseListener {

	JPopupMenu popup;

	public DefaultMouseListener() {
		popup = new JPopupMenu();
		
		JMenuItem menuCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuCopy.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Copy"));
		popup.add(menuCopy);
		
		JMenuItem menuCut = new JMenuItem(new DefaultEditorKit.CutAction());
		menuCut.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Cut"));
		popup.add(menuCut);

		JMenuItem menuPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
		menuPaste.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Paste"));
		popup.add(menuPaste);
	}

	private void checkForPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		checkForPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		checkForPopup(e);
	}

}
