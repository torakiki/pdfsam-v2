/*
 * Created on 20/mar/2010
 * Copyright (C) 2010 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Remove action for the selection table
 * 
 * @author Andrea Vacondio
 * 
 */
public class RemoveSelectionTableAction extends AbstractAction {

	private static final long serialVersionUID = 8782788160363934282L;

	private JPdfSelectionTable mainTable;

	/**
	 * @param mainTable
	 */
	public RemoveSelectionTableAction(JPdfSelectionTable mainTable) {
		super(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Remove"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Remove a pdf from the list"));
		this.putValue(Action.SMALL_ICON, new ImageIcon(this.getClass().getResource("/images/remove.png")));
		this.mainTable = mainTable;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int[] selectedRows = mainTable.getSelectedRows();
		if (selectedRows.length > 0) {
			((AbstractPdfSelectionTableModel) mainTable.getModel()).deleteRows(selectedRows);
		}
	}

}
