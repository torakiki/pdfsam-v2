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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;

/**
 * @author Andrea Vacondio
 * 
 */
public class SetOutputPathSelectionTableAction extends AbstractAction {

	private static final long serialVersionUID = -7102238226761449078L;

	private final JPdfSelectionPanel selectionPanel;

	private final JTextField destinationField;

	private final String defaultOutputFileName;

	/**
	 * @param selectionPanel
	 * @param destinationField
	 *            the JTextField to update
	 * @param defaultOutputFileName
	 *            the output file name. If null it's ignored.
	 */
	public SetOutputPathSelectionTableAction(JPdfSelectionPanel selectionPanel, JTextField destinationField,
			String defaultOutputFileName) {
		super(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Set destination"));
		this.setEnabled(true);
		this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK));
		this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
				.getI18nResourceBundle(), "Set the destination path"));
		this.putValue(Action.SMALL_ICON, new ImageIcon(this.getClass().getResource("/images/set_outfile.png")));
		this.selectionPanel = selectionPanel;
		this.destinationField = destinationField;
		this.defaultOutputFileName = defaultOutputFileName;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JPdfSelectionTable mainTable = selectionPanel.getMainTable();
		if (mainTable.getSelectedRowCount() == 1) {
			PdfSelectionTableItem item = ((AbstractPdfSelectionTableModel) mainTable.getModel()).getRow(mainTable.getSelectedRow());
			if (item != null) {
				String defaultOutputPath = item.getInputFile().getParent();
				if (!defaultOutputPath.endsWith(File.separator)) {
					defaultOutputPath += File.separator;
				}
				if (!StringUtils.isEmpty(defaultOutputFileName)) {
					defaultOutputPath += defaultOutputFileName;
				}
				destinationField.setText(defaultOutputPath);
			}
		}
	}

}
