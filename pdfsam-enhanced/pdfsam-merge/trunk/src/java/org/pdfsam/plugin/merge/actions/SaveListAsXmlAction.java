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
package org.pdfsam.plugin.merge.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.utils.FileExtensionUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * Saves the selection list an xml file that can be used as input for the pdfsam-console
 * 
 * @author Andrea Vacondio
 * 
 */
public class SaveListAsXmlAction extends AbstractAction {

    private static final long serialVersionUID = -1905012939581899825L;

    private static final Logger LOG = Logger.getLogger(SaveListAsXmlAction.class.getPackage().getName());

    private JPdfSelectionPanel selectionPanel;

    /**
     * @param selectionPanel
     */
    public SaveListAsXmlAction(JPdfSelectionPanel selectionPanel) {
        super(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Export as xml"));
        this.setEnabled(true);
        this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_DOWN_MASK));
        this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
                .getI18nResourceBundle(), "Export the selection list in xml format"));
        this.putValue(Action.SMALL_ICON, new ImageIcon(this.getClass().getResource("/images/saveXml.png")));
        this.selectionPanel = selectionPanel;
    }

    public void actionPerformed(ActionEvent e) {
        PdfSelectionTableItem[] rows = selectionPanel.getTableRows();
        if (!ArrayUtils.isEmpty(rows)) {
            try {
                JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.XML_FILE,
                        JFileChooser.FILES_ONLY);
                if (fileChooser.showSaveDialog(selectionPanel) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = FileExtensionUtility.ensureExtension(fileChooser.getSelectedFile(),
                            FileExtensionUtility.XML_EXTENSION);
                    writeXmlFile(rows, selectedFile);
                }
            } catch (Exception ex) {
                LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Unable to save xml file."), ex);
            }
        }
    }

    /**
     * Save the xml file
     * 
     * @param rows
     * @param selectedFile
     * @throws Exception
     */
    public void writeXmlFile(PdfSelectionTableItem[] rows, File selectedFile) throws Exception {
        if (selectedFile != null && rows != null) {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("filelist");
            for (int i = 0; i < rows.length; i++) {
                PdfSelectionTableItem row = rows[i];
                Element node = (Element) root.addElement("file");
                node.addAttribute("value", row.getInputFile().getAbsolutePath());
                String pwd = row.getPassword();
                if (pwd != null && pwd.length() > 0) {
                    node.addAttribute("password", pwd);
                }
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(selectedFile));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter xmlWriter = new XMLWriter(bos, format);
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
            LOG.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File xml saved."));
        } else {
            LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Error saving xml file, output file is null."));
        }
    }
}
