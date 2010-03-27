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
package org.pdfsam.plugin.coverfooter.listeners;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.coverfooter.GUI.CoverFooterMainGUI;

/**
 * Action listener for the run button of the cover footer plugin
 * 
 * @author Andrea Vacondio
 * 
 */
public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private CoverFooterMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(CoverFooterMainGUI panel) {
        super();
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent e) {

        if (WorkExecutor.getInstance().getRunningThreads() > 0 || panel.getSelectionPanel().isAdding()) {
            DialogUtility.showWarningAddingDocument(panel);
            return;
        }
        PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
        if (ArrayUtils.isEmpty(items)) {
            DialogUtility.showWarningNoDocsSelected(panel, DialogUtility.AT_LEAST_ONE_DOC);
            return;
        }

        LinkedList<String> args = new LinkedList<String>();
        LinkedList<String> args1 = new LinkedList<String>();
        LinkedList<String> argsFooter = new LinkedList<String>();
        // validation and permission check are demanded
        try {
            if (panel.getOutputCompressedCheck().isSelected()) {
                args.add("-" + ConcatParsedCommand.COMPRESSED_ARG);
            }
            if (panel.getMergeTypeCheck().isSelected()) {
                args.add("-" + ConcatParsedCommand.COPYFIELDS_ARG);
            }

            args.add("-" + ConcatParsedCommand.PDFVERSION_ARG);
            args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());

            PdfSelectionTableItem[] coveritems = panel.getCoverSelectionPanel().getTableRows();
            PdfSelectionTableItem[] footeritems = panel.getFooterSelectionPanel().getTableRows();
            String coverSelectionString = "";
            // manage cover
            if ((coveritems == null || coveritems.length != 1)
                    && (footeritems == null || footeritems.length != 1)) {
                JOptionPane.showMessageDialog(panel, GettextResource.gettext(Configuration.getInstance()
                        .getI18nResourceBundle(), "Select at least one cover or one footer"), GettextResource.gettext(
                        Configuration.getInstance().getI18nResourceBundle(), "Warning"), JOptionPane.WARNING_MESSAGE);
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
                if (panel.getOverwriteCheckbox().isSelected()) {
                    args.add("-" + ConcatParsedCommand.OVERWRITE_ARG);
                }

                if ((coveritems != null && coveritems.length == 1)) {
                    PdfSelectionTableItem coveritem = coveritems[0];
                    String coverSelection = (coveritem.getPageSelection() != null && coveritem.getPageSelection()
                            .length() > 0) ? coveritem.getPageSelection() : CoverFooterMainGUI.ALL_STRING;
                    if (coverSelection.trim().length() > 0 && coverSelection.indexOf(",") != 0) {
                        String[] selectionsArray = coverSelection.split(",");
                        for (int j = 0; j < selectionsArray.length; j++) {
                            String tmpString = selectionsArray[j].trim();
                            if ((tmpString != null) && (!tmpString.equals(""))) {
                                args.add("-" + ConcatParsedCommand.F_ARG);
                                String f = coveritem.getInputFile().getAbsolutePath();
                                if ((coveritem.getPassword()) != null && (coveritem.getPassword()).length() > 0) {
                                    log.debug(GettextResource.gettext(Configuration.getInstance()
                                            .getI18nResourceBundle(), "Found a password for input file."));
                                    f += ":" + coveritem.getPassword();
                                }
                                args.add(f);
                                coverSelectionString += (tmpString.matches("[\\d]+")) ? tmpString + "-" + tmpString
                                        + ":" : tmpString + ":";
                            }
                        }

                    } else {
                        args.add("-" + ConcatParsedCommand.F_ARG);
                        String f = coveritem.getInputFile().getAbsolutePath();
                        if ((coveritem.getPassword()) != null && (coveritem.getPassword()).length() > 0) {
                            log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                                    "Found a password for input file."));
                            f += ":" + coveritem.getPassword();
                        }
                        args.add(f);
                        coverSelectionString += (coverSelection.matches("[\\d]+")) ? coverSelection + "-"
                                + coverSelection + ":" : coverSelection + ":";
                    }
                }
                String footerSelectionString = "";
                // manage footer
                if ((footeritems != null && footeritems.length == 1)) {
                    PdfSelectionTableItem footeritem = footeritems[0];
                    String footerSelection = (footeritem.getPageSelection() != null && footeritem.getPageSelection()
                            .length() > 0) ? footeritem.getPageSelection() : CoverFooterMainGUI.ALL_STRING;
                    if (footerSelection.trim().length() > 0 && footerSelection.indexOf(",") != 0) {
                        String[] selectionsArray = footerSelection.split(",");
                        for (int j = 0; j < selectionsArray.length; j++) {
                            String tmpString = selectionsArray[j].trim();
                            if ((tmpString != null) && (!tmpString.equals(""))) {
                                argsFooter.add("-" + ConcatParsedCommand.F_ARG);
                                String footerItem = footeritem.getInputFile().getAbsolutePath();
                                if ((footeritem.getPassword()) != null && (footeritem.getPassword()).length() > 0) {
                                    log.debug(GettextResource.gettext(Configuration.getInstance()
                                            .getI18nResourceBundle(), "Found a password for input file."));
                                    footerItem += ":" + footeritem.getPassword();
                                }
                                argsFooter.add(footerItem);
                                footerSelectionString += (tmpString.matches("[\\d]+")) ? tmpString + "-" + tmpString
                                        + ":" : tmpString + ":";
                            }
                        }

                    } else {
                        argsFooter.add("-" + ConcatParsedCommand.F_ARG);
                        String footerItem = footeritem.getInputFile().getAbsolutePath();
                        if ((footeritem.getPassword()) != null && (footeritem.getPassword()).length() > 0) {
                            log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                                    "Found a password for input file."));
                            footerItem += ":" + footeritem.getPassword();
                        }
                        argsFooter.add(footerItem);
                        footerSelectionString += (footerSelection.matches("[\\d]+")) ? footerSelection + "-"
                                + footerSelection + ":" : footerSelection + ":";
                    }
                }
                // selection page
                PdfSelectionTableItem item = null;
                for (int i = 0; i < items.length; i++) {
                    String pageSelectionString = coverSelectionString;
                    try {
                        args1.clear();
                        args1.addAll(args);

                        item = items[i];
                        String pageSelection = (item.getPageSelection() != null && item.getPageSelection().length() > 0) ? item
                                .getPageSelection()
                                : CoverFooterMainGUI.ALL_STRING;
                        if (pageSelection.trim().length() > 0 && pageSelection.indexOf(",") != 0) {
                            String[] selectionsArray = pageSelection.split(",");
                            for (int j = 0; j < selectionsArray.length; j++) {
                                String tmpString = selectionsArray[j].trim();
                                if ((tmpString != null) && (!tmpString.equals(""))) {
                                    args1.add("-" + ConcatParsedCommand.F_ARG);
                                    String f = item.getInputFile().getAbsolutePath();
                                    if ((item.getPassword()) != null && (item.getPassword()).length() > 0) {
                                        log.debug(GettextResource.gettext(Configuration.getInstance()
                                                .getI18nResourceBundle(), "Found a password for input file."));
                                        f += ":" + item.getPassword();
                                    }
                                    args1.add(f);
                                    pageSelectionString += (tmpString.matches("[\\d]+")) ? tmpString + "-" + tmpString
                                            + ":" : tmpString + ":";
                                }
                            }

                        } else {
                            args1.add("-" + ConcatParsedCommand.F_ARG);
                            String f = item.getInputFile().getAbsolutePath();
                            if ((item.getPassword()) != null && (item.getPassword()).length() > 0) {
                                log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                                        "Found a password for input file."));
                                f += ":" + item.getPassword();
                            }
                            args1.add(f);
                            pageSelectionString += (pageSelection.matches("[\\d]+")) ? pageSelection + "-"
                                    + pageSelection + ":" : pageSelection + ":";
                        }

                        args1.addAll(argsFooter);
                        args1.add("-" + ConcatParsedCommand.U_ARG);
                        args1.add(pageSelectionString + footerSelectionString);

                        // manage output destination option
                        args1.add("-" + ConcatParsedCommand.O_ARG);
                        if (StringUtils.isEmpty(panel.getDestinationTextField().getText())) {
                            String suggestedDir = getSuggestedDestinationDirectory(items[items.length - 1]);
                                int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                                if (JOptionPane.YES_OPTION == chosenOpt) {
                                    panel.getDestinationTextField().setText(suggestedDir);
                                } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                                    return;
                                }
                        }
                        if (panel.getDestinationTextField().getText().length() > 0) {
                            args1.add(panel.getDestinationTextField().getText() + File.separator
                                    + item.getInputFile().getName());
                        }

                        args1.add(AbstractParsedCommand.COMMAND_CONCAT);

                        WorkExecutor.getInstance().execute(new WorkThread(args1.toArray(new String[args1.size()])));
                    } catch (Exception ex) {
                        log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                                "Error: "), ex);
                    }
                }
            }
        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
            SoundPlayer.getInstance().playErrorSound();
        }

    }

}
