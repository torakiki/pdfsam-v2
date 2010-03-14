/*
 * Created on 29-Nov-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.plugin.docinfo.listeners;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.DocumentInfoParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.docinfo.GUI.DocInfoMainGUI;

public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private DocInfoMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(DocInfoMainGUI panel) {
        super();
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (WorkExecutor.getInstance().getRunningThreads() > 0 || panel.getSelectionPanel().isAdding()) {
            DialogUtility.showWarningAddingDocument(panel);
            return;
        }
        PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
        if (items == null || items.length != 1){
            DialogUtility.showWarningNoDocsSelected(panel, DialogUtility.ONE_DOC);
            return;
        }
        if(StringUtils.isEmpty(panel.getDestinationTextField().getText()) && !panel.getSameAsSourceRadio().isSelected()){
            DialogUtility.showWarningNoDestinationSelected(panel, DialogUtility.FILE_DESTINATION);
            return;
        }
        LinkedList<String> args = new LinkedList<String>();
        try {
            PdfSelectionTableItem item = null;
                item = items[0];
                String destination = "";

                if (panel.getSameAsSourceRadio().isSelected()) {
                    destination = item.getInputFile().getAbsolutePath();
                } else {
                    // overwrite confirmation
                    if (panel.getOverwriteCheckbox().isSelected()
                            && Configuration.getInstance().isAskOverwriteConfirmation()) {
                        int dialogRet = DialogUtility.askForOverwriteConfirmation(panel);
                        if (JOptionPane.NO_OPTION == dialogRet) {
                            panel.getOverwriteCheckbox().setSelected(false);
                        } else if (JOptionPane.CANCEL_OPTION == dialogRet) {
                            return;
                        }
                    }

                    ensurePdfExtensionOnTextField(panel.getDestinationTextField());
                    destination = panel.getDestinationTextField().getText();
                    File destinationDir = new File(destination);
                    File parent = destinationDir.getParentFile();
                    if (!(parent != null && parent.exists())) {
                        String suggestedDir = getSuggestedOutputFile(items[items.length - 1], destinationDir.getName());
                        int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                        if (JOptionPane.YES_OPTION == chosenOpt) {
                            destination = suggestedDir;
                        } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                            return;
                        }
                    }
                }
                panel.getDestinationTextField().setText(destination);

                // check if the file already exists and the user didn't select to overwrite
                File destFile = (destination != null) ? new File(destination) : null;
                if (destFile != null && destFile.exists() && !panel.getOverwriteCheckbox().isSelected()) {
                    int chosenOpt = DialogUtility.askForOverwriteOutputFileDialog(panel, destFile.getName());
                    if (JOptionPane.YES_OPTION == chosenOpt) {
                        panel.getOverwriteCheckbox().setSelected(true);
                    } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                        return;
                    }
                }

                args.add("-" + DocumentInfoParsedCommand.O_ARG);
                args.add(destination);

                args.add("-" + DocumentInfoParsedCommand.TITLE_ARG);
                args.add(panel.getTitleTextField().getText());

                args.add("-" + DocumentInfoParsedCommand.AUTHOR_ARG);
                args.add(panel.getAuthorTextField().getText());

                args.add("-" + DocumentInfoParsedCommand.SUBJECT_ARG);
                args.add(panel.getSubjectTextField().getText());

                args.add("-" + DocumentInfoParsedCommand.KEYWORDS_ARG);
                args.add(panel.getKeywordsTextField().getText());

                if (panel.getOverwriteCheckbox().isSelected())
                    args.add("-" + DocumentInfoParsedCommand.OVERWRITE_ARG);
                if (panel.getOutputCompressedCheck().isSelected())
                    args.add("-" + DocumentInfoParsedCommand.COMPRESSED_ARG);

                args.add("-" + SplitParsedCommand.PDFVERSION_ARG);
                if (JPdfVersionCombo.SAME_AS_SOURCE.equals(((StringItem) panel.getVersionCombo().getSelectedItem())
                        .getId())) {
                    StringItem minItem = panel.getVersionCombo().getMinItem();
                    String currentPdfVersion = Character.toString(item.getPdfVersion());
                    if (minItem != null) {
                        if (Integer.parseInt(currentPdfVersion) < Integer.parseInt(minItem.getId())) {
                            if (JOptionPane.YES_OPTION != DialogUtility.askForPdfVersionConfilct(panel, minItem
                                    .getDescription())) {
                                return;
                            }
                        }
                    }
                    args.add(currentPdfVersion);
                } else {
                    args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());
                }

                args.add("-" + DocumentInfoParsedCommand.F_ARG);
                String f = item.getInputFile().getAbsolutePath();
                if ((item.getPassword()) != null && (item.getPassword()).length() > 0) {
                    log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                            "Found a password for input file."));
                    f += ":" + item.getPassword();
                }
                args.add(f);

                args.add(AbstractParsedCommand.COMMAND_SETDOCINFO);

                String[] myStringArray = args.toArray(new String[args.size()]);
                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));
        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
            SoundPlayer.getInstance().playErrorSound();
        }
    }

}
