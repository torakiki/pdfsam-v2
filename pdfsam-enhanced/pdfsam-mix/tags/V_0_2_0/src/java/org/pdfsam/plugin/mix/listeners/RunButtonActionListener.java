/*
 * Created on 14-Nov-2009
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
package org.pdfsam.plugin.mix.listeners;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.mix.GUI.MixMainGUI;

/**
 * Action listener for the run button of the mix plugin
 * 
 * @author Andrea Vacondio
 * 
 */
public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private MixMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(MixMainGUI panel) {
        super();
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent arg0) {
        if (WorkExecutor.getInstance().getRunningThreads() > 0 || panel.getSelectionPanel().isAdding()) {
            DialogUtility.showWarningAddingDocument(panel);
            return;
        }
        PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
        if (items == null || items.length != 2) {
            DialogUtility.showWarningNoDocsSelected(panel, DialogUtility.TWO_DOC);
            return;
        }
        if (StringUtils.isEmpty(panel.getDestinationTextField().getText())) {
            DialogUtility.showWarningNoDestinationSelected(panel, DialogUtility.FILE_DESTINATION);
            return;
        }
        LinkedList<String> args = new LinkedList<String>();
        try {
            // overwrite confirmation
            if (panel.getOverwriteCheckbox().isSelected() && Configuration.getInstance().isAskOverwriteConfirmation()) {
                int dialogRet = DialogUtility.askForOverwriteConfirmation(panel);
                if (JOptionPane.NO_OPTION == dialogRet) {
                    panel.getOverwriteCheckbox().setSelected(false);
                } else if (JOptionPane.CANCEL_OPTION == dialogRet) {
                    return;
                }
            }

            args.addAll(getInputFilesArguments(items));

            String destination = "";
            // if no extension given
            ensurePdfExtensionOnTextField(panel.getDestinationTextField());
            File destinationDir = new File(panel.getDestinationTextField().getText());
            File parent = destinationDir.getParentFile();
            if (!(parent != null && parent.exists())) {
                String suggestedDir = getSuggestedOutputFile(items[items.length - 1], destinationDir.getName());
                int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                if (JOptionPane.YES_OPTION == chosenOpt) {
                    panel.getDestinationTextField().setText(suggestedDir);
                } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                    return;
                }
            }

            destination = panel.getDestinationTextField().getText();

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
            args.add("-" + MixParsedCommand.O_ARG);
            args.add(destination);

            String step = panel.getStepTextField().getText();
            if (StringUtils.isNotEmpty(step)) {
                args.add("-" + MixParsedCommand.STEP_ARG);
                args.add(step);
            }

            String secondStep = panel.getSecondStepTextField().getText();
            if (StringUtils.isNotEmpty(secondStep)) {
                args.add("-" + MixParsedCommand.SECOND_STEP_ARG);
                args.add(secondStep);
            }

            if (panel.getOverwriteCheckbox().isSelected())
                args.add("-" + MixParsedCommand.OVERWRITE_ARG);
            if (panel.getOutputCompressedCheck().isSelected())
                args.add("-" + MixParsedCommand.COMPRESSED_ARG);
            if (panel.getReverseFirstCheckbox().isSelected())
                args.add("-" + MixParsedCommand.REVERSE_FIRST_ARG);
            if (panel.getReverseSecondCheckbox().isSelected())
                args.add("-" + MixParsedCommand.REVERSE_SECOND_ARG);

            args.add("-" + MixParsedCommand.PDFVERSION_ARG);
            args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());

            args.add(MixParsedCommand.COMMAND_MIX);
            String[] myStringArray = args.toArray(new String[args.size()]);
            WorkExecutor.getInstance().execute(new WorkThread(myStringArray));

        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
            SoundPlayer.getInstance().playErrorSound();
        }

    }

    /**
     * @param items
     * @return the list of the -f arguments
     */
    private List<String> getInputFilesArguments(PdfSelectionTableItem[] items) {
        List<String> retList = new LinkedList<String>();
        retList.add("-" + MixParsedCommand.F1_ARG);
        String f1 = items[0].getInputFile().getAbsolutePath();
        if ((items[0].getPassword()) != null && (items[0].getPassword()).length() > 0) {
            log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Found a password for first file."));
            f1 += ":" + items[0].getPassword();
        }
        retList.add(f1);

        retList.add("-" + MixParsedCommand.F2_ARG);
        String f2 = items[1].getInputFile().getAbsolutePath();
        if ((items[1].getPassword()) != null && (items[1].getPassword()).length() > 0) {
            log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Found a password for second file."));
            f2 += ":" + items[1].getPassword();
        }
        retList.add(f2);
        return retList;
    }

}
