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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.pdfsam.guiclient.commons.business.loaders.PdfLoader;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;

/**
 * Reload the selected document
 * 
 * @author Andrea Vacondio
 * 
 */
public class ReloadDocumentSelectionTableAction extends AbstractAction {

	private static final long serialVersionUID = 8142152957534813440L;

	private JPdfSelectionTable mainTable;

	private PdfLoader loader;

	/**
	 * @param mainTable
	 */
	public ReloadDocumentSelectionTableAction(JPdfSelectionTable mainTable, PdfLoader loader) {
		super(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Reload"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Reload the selected document"));
		this.putValue(Action.SMALL_ICON, new ImageIcon(this.getClass().getResource("/images/reload.png")));
		this.mainTable = mainTable;
		this.loader = loader;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int selectedRow = mainTable.getSelectedRow();
		if (mainTable.getSelectedRowCount() == 1) {
			PdfSelectionTableItem row = ((AbstractPdfSelectionTableModel) mainTable.getModel()).getRow(selectedRow);
			loader.reloadFile(row.getInputFile(), row.getPassword(), row.getPageSelection(), selectedRow);
		}
	}

}
