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
package org.pdfsam.guiclient.gui.panels;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.plugins.models.PluginDataModel;
import org.pdfsam.i18n.GettextResource;
/**
 * panel containing the selection tree
 * @author Andrea Vacondio
 *
 */
public class JTreePanel extends JScrollPane {

	private static final long serialVersionUID = -53156470256505793L;
	
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode plugsNode;
	private DefaultMutableTreeNode rootNode;
	
	public JTreePanel(DefaultMutableTreeNode rootNode){
		this.rootNode = rootNode;
		this.plugsNode = new DefaultMutableTreeNode(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Plugins"));
		this.rootNode.add(plugsNode); 
		this.treeModel = new DefaultTreeModel(rootNode);
		this.tree = new JTree(treeModel);
		init();
		setViewportView(this.tree);
	}
	
	/**
	 * initialization
	 */
	private void init(){
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.setShowsRootHandles(false);
	}
	
	/**
	 * adds an element to the root node
	 * @param pluginDataModel
	 */
	public void addToRootNode(PluginDataModel pluginDataModel){
		rootNode.add(new DefaultMutableTreeNode(pluginDataModel));
		treeModel.reload();
	}
	
	/**
	 * adds an element to the plugs node
	 * @param pluginDataModel
	 */
	public void addToPlugsNode(PluginDataModel pluginDataModel){
		plugsNode.add(new DefaultMutableTreeNode(pluginDataModel));
	}

	/**
	 * expands the tree
	 */
	public void expand(){
		this.tree.expandPath(new TreePath(plugsNode.getPath()));
	}
	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * @return the treeModel
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * @return the plugsNode
	 */
	public DefaultMutableTreeNode getPlugsNode() {
		return plugsNode;
	}

	/**
	 * @return the rootNode
	 */
	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}
	/**
	 * @return the selected node
	 */
	public DefaultMutableTreeNode getSelectedNode(){
		return (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
	}
	
	/**
	 * @return the selection path
	 */
	public TreePath getSelectionPath(){
		return tree.getSelectionPath();
	}
	
}
