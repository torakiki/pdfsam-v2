/*
 * Created on 13-Nov-2007
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
package org.pdfsam.guiclient.gui.panels;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.log4j.Level;
import org.pdfsam.guiclient.business.TextPaneAppender;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.components.JLogPopupMenu;
import org.pdfsam.i18n.GettextResource;
/**
 * Log panel
 * @author Andrea Vacondio
 *
 */
public class JLogPanel extends JPanel implements MouseListener{

	private static final long serialVersionUID = 2531783640694977646L;

	private final JLabel logLevel = new JLabel();
    private JTextPane logTextPanel = null;
    private JLogPopupMenu popupMenu = null;
    private JScrollPane logPanel = new JScrollPane();
    

	public JLogPanel() {
		init();
		
	}

	/**
	 * Initialization
	 */
	private void init(){
		logTextPanel = TextPaneAppender.getTextPaneInstance();
		popupMenu = new JLogPopupMenu();
		
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(500, 130));
		setMinimumSize(new Dimension(0, 0));
		
		logLevel.setIcon(new ImageIcon(this.getClass().getResource("/images/log.png")));
		logLevel.setPreferredSize(new Dimension(0,25));
		logLevel.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Log level:")+" "+Level.toLevel(Configuration.getInstance().getLoggingLevel(),Level.DEBUG));
		
		logPanel.setMinimumSize(new Dimension(0, 0));
		logPanel.setViewportView(logTextPanel);
        
	    add(Box.createRigidArea(new Dimension(3, 0)));
	    add(logLevel);
		add(logPanel);
		
		logTextPanel.addMouseListener(this);
    }
	
    
    public void mouseReleased(MouseEvent e){
		if(e.isPopupTrigger()){
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}		
	}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent e) {
		if(e.isPopupTrigger()){
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
