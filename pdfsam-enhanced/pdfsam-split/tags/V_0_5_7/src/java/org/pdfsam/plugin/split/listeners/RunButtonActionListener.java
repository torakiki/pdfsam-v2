/*
 * Created on 15-Nov-2009
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
package org.pdfsam.plugin.split.listeners;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
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
import org.pdfsam.plugin.split.GUI.SplitMainGUI;

/**
 * Action listener for the run button of the split plugin
 * 
 * @author Andrea Vacondio
 * 
 */
public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private SplitMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(SplitMainGUI panel) {
        super();
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent e) {

        if (WorkExecutor.getInstance().getRunningThreads() > 0 || panel.getSelectionPanel().isAdding()) {
            DialogUtility.showWarningAddingDocument(panel);
            return;
        }
        if (!panel.getSameAsSourceRadio().isSelected() && StringUtils.isEmpty(panel.getDestinationFolderText().getText())) {
            DialogUtility.showWarningNoDestinationSelected(panel, DialogUtility.DIRECTORY_DESTINATION);
            return;
        }
        PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
        if (items == null || items.length != 1) {
            DialogUtility.showWarningNoDocsSelected(panel, DialogUtility.ONE_DOC);
            return;
        }
        LinkedList<String> args = new LinkedList<String>();
        try {
            PdfSelectionTableItem item = null;
            // overwrite confirmation
            if (panel.getOverwriteCheckbox().isSelected() && Configuration.getInstance().isAskOverwriteConfirmation()) {
                int dialogRet = DialogUtility.askForOverwriteConfirmation(panel);
                if (JOptionPane.NO_OPTION == dialogRet) {
                    panel.getOverwriteCheckbox().setSelected(false);
                } else if (JOptionPane.CANCEL_OPTION == dialogRet) {
                    return;
                }
            }

            item = items[0];
            args.add("-" + SplitParsedCommand.F_ARG);
            String f = item.getInputFile().getAbsolutePath();
            if ((item.getPassword()) != null && (item.getPassword()).length() > 0) {
                log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Found a password for input file."));
                f += ":" + item.getPassword();
            }
            args.add(f);

            args.add("-" + SplitParsedCommand.P_ARG);
            args.add(panel.getOutPrefixText().getText());
            args.add("-" + SplitParsedCommand.S_ARG);
            String splitType = panel.getSplitType();
            args.add(splitType);
            // check if is needed page option
            if (splitType.equals(SplitParsedCommand.S_SPLIT)) {
                args.add("-" + SplitParsedCommand.N_ARG);
                args.add(panel.getThisPageTextField().getText());
            } else if (splitType.equals(SplitParsedCommand.S_NSPLIT)) {
                args.add("-" + SplitParsedCommand.N_ARG);
                args.add(panel.getnPagesTextField().getText());
            } else if (splitType.equals(SplitParsedCommand.S_SIZE)) {
                args.add("-" + SplitParsedCommand.B_ARG);
                if (panel.getSplitSizeCombo().isSelectedItem() && panel.getSplitSizeCombo().isValidSelectedItem()) {
                    args.add(Long.toString(panel.getSplitSizeCombo().getSelectedBytes()));
                } else {
                    throw new Exception(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                            "Invalid split size"));
                }
            } else if (splitType.equals(SplitParsedCommand.S_BLEVEL)) {
                args.add("-" + SplitParsedCommand.BL_ARG);
                args.add((String) panel.getbLevelCombo().getSelectedItem());
            }

            args.add("-" + SplitParsedCommand.O_ARG);
            // check radio for output options
            if (panel.getSameAsSourceRadio().isSelected()) {
                if (item != null) {
                    args.add(item.getInputFile().getParent());
                }
            } else {
                if (StringUtils.isEmpty(panel.getDestinationFolderText().getText())) {
                    String suggestedDir = getSuggestedDestinationDirectory(item);
                    int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                    if (JOptionPane.YES_OPTION == chosenOpt) {
                        panel.getDestinationFolderText().setText(suggestedDir);
                    } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                        return;
                    }

                }
                args.add(panel.getDestinationFolderText().getText());
            }
            if (panel.getOverwriteCheckbox().isSelected())
                args.add("-" + SplitParsedCommand.OVERWRITE_ARG);
            if (panel.getOutputCompressedCheck().isSelected())
                args.add("-" + SplitParsedCommand.COMPRESSED_ARG);
            args.add("-" + SplitParsedCommand.PDFVERSION_ARG);
            if (JPdfVersionCombo.SAME_AS_SOURCE
                    .equals(((StringItem) panel.getVersionCombo().getSelectedItem()).getId())) {
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
            args.add(AbstractParsedCommand.COMMAND_SPLIT);

            String[] myStringArray = args.toArray(new String[args.size()]);
            WorkExecutor.getInstance().execute(new WorkThread(myStringArray));

        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
            SoundPlayer.getInstance().playErrorSound();
        }

    }

}
