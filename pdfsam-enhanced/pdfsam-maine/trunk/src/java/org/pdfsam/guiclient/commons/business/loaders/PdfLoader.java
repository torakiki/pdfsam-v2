/*
 * Created on 21-Nov-2007
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
package org.pdfsam.guiclient.commons.business.loaders;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.loaders.callable.AddPdfDocument;
import org.pdfsam.guiclient.commons.business.loaders.callable.ReloadPdfDocument;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Business class whose job is to load pdf file to PdfSelectionTableItem
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfLoader {

	private static final Logger log = Logger.getLogger(PdfLoader.class.getPackage().getName());

	private JPdfSelectionPanel panel;

	private PdfLoaderExecutor executor = null;

	private PdfDocumentLoadedHook hook = null;

	public PdfLoader(JPdfSelectionPanel panel) {
		this.panel = panel;
		executor = new PdfLoaderExecutor();
	}

	/**
	 * adds a file or many files depending on the value of singleSelection
	 */
	public void showFileChooserAndAddFiles(boolean singleSelection) {
		if (panel.getMainTable().getModel().getRowCount() >= panel.getMaxSelectableFiles()) {
			JOptionPane.showMessageDialog(panel, GettextResource.gettext(Configuration.getInstance()
					.getI18nResourceBundle(), "Selection table is full, please remove some pdf document."),
					GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Table full"),
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			if (!executor.isExecuting()) {
				JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.PDF_FILE,
						JFileChooser.FILES_ONLY);
				fileChooser.setMultiSelectionEnabled(!singleSelection);
				if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
					if (fileChooser.isMultiSelectionEnabled()) {
						addFiles(fileChooser.getSelectedFiles());
					} else {
						addFile(fileChooser.getSelectedFile());
					}
				}
			} else {
				log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
						"Please wait while all files are processed.."));
			}
		}
	}

	/**
	 * adds multiple selected files
	 */
	public void showFileChooserAndAddFiles() {
		showFileChooserAndAddFiles(false);
	}

	/**
	 * add a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 * @param password
	 *            password
	 * @param pageSelection
	 *            page selection
	 */
	public void addFile(File file, String password, String pageSelection) {
		if (file != null) {
			executor.execute(new AddPdfDocument(file, panel, password, pageSelection), hook);
		}
	}

	/**
	 * add a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 * @param password
	 *            password
	 */
	public void addFile(File file, String password) {
		this.addFile(file, password, null);
	}

	/**
	 * add a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 */
	public void addFile(File file) {
		this.addFile(file, null, null);
	}

	/**
	 * reload a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 * @param password
	 *            password
	 * @param pageSelection
	 *            page selection
	 */
	public void reloadFile(File file, String password, String pageSelection, int index) {
		if (file != null) {
			executor.execute(new ReloadPdfDocument(file, panel, password, pageSelection, index), hook);
		}
	}

	/**
	 * reload a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 * @param password
	 *            password
	 */
	public void reloadFile(File file, String password, int index) {
		this.reloadFile(file, password, null, index);
	}

	/**
	 * reload a file to the selectionTable
	 * 
	 * @param file
	 *            input file
	 */
	public void reloadFile(File file, int index) {
		this.reloadFile(file, null, null, index);
	}

	/**
	 * adds files to the selectionTable
	 * 
	 * @param files
	 */

	public synchronized void addFiles(File[] files) {
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				executor.execute(new AddPdfDocument(files[i], panel), hook);
			}
		}
	}

	/**
	 * adds files to the selectionTable
	 * 
	 * @param files
	 *            File objects list
	 * @param ordered
	 *            files are added keeping order
	 */
	public void addFiles(List<File> files, boolean ordered) {
		if (files != null && !files.isEmpty()) {
			addFiles((File[]) files.toArray(new File[files.size()]));
		}
	}

	/**
	 * Add files without keeping order
	 * 
	 * @param files
	 */
	public void addFiles(List<File> files) {
		addFiles(files, false);
	}

	/**
	 * @return true if the loader is adding or reloading documents
	 */
	public boolean isExecuting() {
		return executor.isExecuting();
	}

	/**
	 * set the hook to be called after the document is loaded
	 * 
	 * @param hook
	 *            the hook to set
	 */
	public void setHook(PdfDocumentLoadedHook hook) {
		this.hook = hook;
	}

}
