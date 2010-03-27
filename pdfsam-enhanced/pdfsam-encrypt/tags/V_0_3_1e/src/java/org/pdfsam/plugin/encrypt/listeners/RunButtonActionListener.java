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
package org.pdfsam.plugin.encrypt.listeners;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.guiclient.utils.EncryptionUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.encrypt.GUI.EncryptMainGUI;

/**
 * Action listener for the run button of the encrypt plugin
 * 
 * @author Andrea Vacondio
 * 
 */
public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private EncryptMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(EncryptMainGUI panel) {
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
        // validation and permission check are demanded to the CmdParser object
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

            args.addAll(getEncPermissions(panel.getPermissionsCheck(), panel.getAllowAllCheck()));
            args.addAll(getInputFilesArguments(items));

            args.add("-" + EncryptParsedCommand.P_ARG);
            args.add(panel.getOutPrefixTextField().getText());
            args.add("-" + EncryptParsedCommand.APWD_ARG);
            args.add(panel.getOwnerPwdField().getText());
            args.add("-" + EncryptParsedCommand.UPWD_ARG);
            args.add(panel.getUserPwdField().getText());
            // check if is needed page option
            args.add("-" + EncryptParsedCommand.ETYPE_ARG);
            args.add(EncryptionUtility.getEncAlgorithm((String) panel.getEncryptType().getSelectedItem()));
            args.add("-" + EncryptParsedCommand.O_ARG);

            if (StringUtils.isEmpty(panel.getDestFolderText().getText())) {
                String suggestedDir = getSuggestedDestinationDirectory(items[items.length - 1]);
                int chosenOpt = DialogUtility.showConfirmOuputLocationDialog(panel, suggestedDir);
                if (JOptionPane.YES_OPTION == chosenOpt) {
                    panel.getDestFolderText().setText(suggestedDir);
                } else if (JOptionPane.CANCEL_OPTION == chosenOpt) {
                    return;
                }
            }
            args.add(panel.getDestFolderText().getText());

            if (panel.getOverwriteCheckbox().isSelected()) {
                args.add("-" + EncryptParsedCommand.OVERWRITE_ARG);
            }
            if (panel.getOutputCompressedCheck().isSelected()) {
                args.add("-" + EncryptParsedCommand.COMPRESSED_ARG);
            }

            args.add("-" + EncryptParsedCommand.PDFVERSION_ARG);
            args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());

            args.add(AbstractParsedCommand.COMMAND_ENCRYPT);

            final String[] myStringArray = (String[]) args.toArray(new String[args.size()]);
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
        for (PdfSelectionTableItem item : items) {
            retList.add("-" + EncryptParsedCommand.F_ARG);
            String f = item.getInputFile().getAbsolutePath();
            if ((item.getPassword()) != null && (item.getPassword()).length() > 0) {
                log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Found a password for input file."));
                f += ":" + item.getPassword();
            }
            retList.add(f);
        }
        return retList;
    }

    /**
     *@return <code>LinkedList</code> containing permissions parameters
     */
    private LinkedList<String> getEncPermissions(JCheckBox[] pChecks, JCheckBox allowAll) {
        LinkedList<String> ret = new LinkedList<String>();
        if (allowAll.isSelected()) {
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_PRINT);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_MODIFY);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_COPY);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_ANNOTATION);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_SCREEN);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_FILL);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_ASSEMBLY);
            ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
            ret.add(EncryptParsedCommand.E_DPRINT);
        } else {
            if (pChecks[EncryptMainGUI.PRINT].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_PRINT);
            }
            if (pChecks[EncryptMainGUI.DPRINT].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_DPRINT);
            }
            if (pChecks[EncryptMainGUI.COPY].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_COPY);
            }
            if (pChecks[EncryptMainGUI.MODIFY].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_MODIFY);
            }
            if (pChecks[EncryptMainGUI.FILL].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_FILL);
            }
            if (pChecks[EncryptMainGUI.SCREEN].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_SCREEN);
            }
            if (pChecks[EncryptMainGUI.ASSEMBLY].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_ASSEMBLY);
            }
            if (pChecks[EncryptMainGUI.ANNOTATION].isSelected()) {
                ret.add("-" + EncryptParsedCommand.ALLOW_ARG);
                ret.add(EncryptParsedCommand.E_ANNOTATION);
            }
        }
        return ret;
    }

}
