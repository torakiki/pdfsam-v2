/*
 * Created on 09-Nov-2007
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
package org.pdfsam.guiclient.business.listeners.mediators;

import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.pdfsam.guiclient.gui.frames.JMainFrame;
import org.pdfsam.guiclient.gui.panels.JStatusPanel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.plugins.models.PluginDataModel;

/**
 * Mediator among JTree, JStatusPanel, MainPanel and PluginsMap
 * 
 * @author Andrea Vacondio
 * 
 */
public class TreeMediator implements TreeSelectionListener {

    private JMainFrame container;

    public TreeMediator(JMainFrame container) {
        this.container = container;

    }

    public void valueChanged(TreeSelectionEvent e) {
        JStatusPanel statusPanel = container.getStatusPanel();
        JPanel plugsPanel = container.getMainPanel();
        DefaultMutableTreeNode node = container.getTreePanel().getSelectedNode();
        if (node != null && node.isLeaf()) {
            Object selectedObject = node.getUserObject();
            if (selectedObject instanceof PluginDataModel) {
                PluginDataModel selectedPlug = (PluginDataModel) selectedObject;
                AbstractPlugablePanel panel = container.getPluginsMap().get(selectedPlug);
                statusPanel.setText(selectedPlug.getName());
                statusPanel.setIcon(panel.getIcon());
                CardLayout cl = (CardLayout) (plugsPanel.getLayout());
                cl.show(plugsPanel, selectedPlug.getName());
                container.setFocusTraversalPolicy(panel.getFocusPolicy());
                container.setMainPanelPreferredSize(panel.getPreferredSize());
            }
        }
    }

}
