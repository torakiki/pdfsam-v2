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
package org.pdfsam.plugin.merge.listeners;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.utils.ValidationUtility;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.merge.GUI.MergeMainGUI;

/**
 * Listener for the run button of the merge plugin
 * 
 * @author Andrea Vacondio
 * 
 */
public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private MergeMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(MergeMainGUI panel) {
        super();
        this.panel = panel;
    }

    public void actionPerformed(ActionEvent arg0) {
        if (WorkExecutor.getInstance().getRunningThreads() > 0 || panel.getSelectionPanel().isAdding()) {
            DialogUtility.showWarningAddingDocument(panel);
            return;
        }
        PdfSelectionTableItem[] items = panel.getSelectionPanel().getTableRows();
        if (ArrayUtils.isEmpty(items)) {
            DialogUtility.showWarningNoDocsSelected(panel, DialogUtility.AT_LEAST_ONE_DOC);
            return;
        }
        if(StringUtils.isEmpty(panel.getDestinationTextField().getText())){
            DialogUtility.showWarningNoDestinationSelected(panel, DialogUtility.FILE_DESTINATION);
            return;
        }
        LinkedList<String> args = new LinkedList<String>();
        try {
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

                // if no extension given
                ensurePdfExtensionOnTextField(panel.getDestinationTextField());
                
                File destinationDir = new File(panel.getDestinationTextField().getText());
                File parent = destinationDir.getParentFile();
                // only filename no dir
                if (!(parent != null && parent.exists())) {
                    String suggestedDir = getSuggestedOutputFile(items[items.length - 1], destinationDir.getName());
                    int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                    if (JOptionPane.YES_OPTION == chosenOpt) {
                        panel.getDestinationTextField().setText(suggestedDir);
                    } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                        return;
                    }
                }

                String destination = panel.getDestinationTextField().getText();

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

                args.add("-" + ConcatParsedCommand.O_ARG);
                args.add(destination);

                StringBuilder psStringBuilder = new StringBuilder();
                for (PdfSelectionTableItem item : items) {
                    String pageSelection = (!StringUtils.isEmpty(item.getPageSelection())) ? item.getPageSelection()
                            : MergeMainGUI.ALL_STRING;
                    String[] selections = StringUtils.split(pageSelection, ",");
                    if (!ValidationUtility.isValidPageSelectionsArray(selections)) {
                        DialogUtility.errorValidatingBounds(panel, pageSelection);
                        return;
                    } else {
                        args.add("-" + ConcatParsedCommand.F_ARG);
                        String f = item.getInputFile().getAbsolutePath();
                        if (!StringUtils.isEmpty(item.getPassword())) {
                            log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                                    "Found a password for input file."));
                            f += ":" + item.getPassword();
                        }
                        args.add(f);
                        psStringBuilder.append(pageSelection).append(":");
                    }
                }

                args.add("-" + ConcatParsedCommand.U_ARG);
                args.add(psStringBuilder.toString());

                if (panel.getOverwriteCheckbox().isSelected())
                    args.add("-" + ConcatParsedCommand.OVERWRITE_ARG);
                if (panel.getOutputCompressedCheck().isSelected())
                    args.add("-" + ConcatParsedCommand.COMPRESSED_ARG);
                if (panel.getMergeTypeCheck().isSelected())
                    args.add("-" + ConcatParsedCommand.COPYFIELDS_ARG);

                args.add("-" + ConcatParsedCommand.PDFVERSION_ARG);
                args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());

                args.add(AbstractParsedCommand.COMMAND_CONCAT);

                String[] myStringArray = args.toArray(new String[args.size()]);
                WorkExecutor.getInstance().execute(new WorkThread(myStringArray));
           
        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
            SoundPlayer.getInstance().playErrorSound();
        }
    }

}
