/*
 * Created on 15-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

import org.pdfsam.guiclient.business.listeners.LogActionListener;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Popup menu for the log window
 * @author Andrea Vacondio
 *
 */
public class JLogPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 5285203284602898113L;

	private Configuration config;
	private ActionListener listener;
	
	/**
	 * Default constructor, it adds a LogActionListener to listen menu items
	 */
	public JLogPopupMenu(){
		this(new LogActionListener());
	}
	
	public JLogPopupMenu(ActionListener listener){
		this.listener = listener;
		init();
	}
	
	private void init(){
		config = Configuration.getInstance();
		
		JMenuItem menuCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
        menuCopy.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Copy"));
        menuCopy.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-copy.png")));
        this.add(menuCopy);
        
        JMenuItem menuClear = new JMenuItem();
        menuClear.setActionCommand(LogActionListener.CLEAR_LOG_ACTION);
        menuClear.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear"));
        menuClear.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-clear.png")));
        menuClear.addActionListener(listener);
        this.add(menuClear);

        JMenuItem menuSelectAll = new JMenuItem();
        menuSelectAll.setActionCommand(LogActionListener.SELECTALL_LOG_ACTION);
        menuSelectAll.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Select all"));
        menuSelectAll.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-select-all.png")));
        menuSelectAll.addActionListener(listener);
        this.add(menuSelectAll);
        
        this.addSeparator();
        
        JMenuItem menuSave = new JMenuItem();
        menuSave.setActionCommand(LogActionListener.SAVE_LOG_ACTION);
        menuSave.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Save log"));
        menuSave.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-save.png")));
        menuSave.addActionListener(listener);
        this.add(menuSave);
	}
}
