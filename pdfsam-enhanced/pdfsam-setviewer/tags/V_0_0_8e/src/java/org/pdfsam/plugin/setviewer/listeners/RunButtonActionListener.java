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
package org.pdfsam.plugin.setviewer.listeners;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.SetViewerParsedCommand;
import org.pdfsam.guiclient.business.listeners.AbstractRunButtonActionListener;
import org.pdfsam.guiclient.commons.business.SoundPlayer;
import org.pdfsam.guiclient.commons.business.WorkExecutor;
import org.pdfsam.guiclient.commons.business.WorkThread;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.setviewer.GUI.SetViewerMainGUI;

public class RunButtonActionListener extends AbstractRunButtonActionListener {

    private static final Logger log = Logger.getLogger(RunButtonActionListener.class.getPackage().getName());

    private SetViewerMainGUI panel;

    /**
     * @param panel
     */
    public RunButtonActionListener(SetViewerMainGUI panel) {
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

            args.addAll(getInputFilesArguments(items));

            args.add("-" + SetViewerParsedCommand.P_ARG);
            args.add(panel.getOutPrefixTextField().getText());

            args.add("-" + SetViewerParsedCommand.O_ARG);

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

            args.add("-" + SetViewerParsedCommand.L_ARG);
            args.add(((StringItem) panel.getViewerLayout().getSelectedItem()).getId());

            args.add("-" + SetViewerParsedCommand.M_ARG);
            args.add(((StringItem) panel.getViewerOpenMode().getSelectedItem()).getId());

            if (panel.getNonFullScreenMode().isEnabled()) {
                args.add("-" + SetViewerParsedCommand.NFSM_ARG);
                args.add(((StringItem) panel.getNonFullScreenMode().getSelectedItem()).getId());
            }

            if (((StringItem) panel.getDirectionCombo().getSelectedItem()).getId().length() > 0) {
                args.add("-" + SetViewerParsedCommand.DIRECTION_ARG);
                args.add(((StringItem) panel.getDirectionCombo().getSelectedItem()).getId());
            }

            if (panel.getHideMenuBar().isSelected()) {
                args.add("-" + SetViewerParsedCommand.HIDEMENU_ARG);
            }
            if (panel.getHideToolBar().isSelected()) {
                args.add("-" + SetViewerParsedCommand.HIDETOOLBAR_ARG);
            }
            if (panel.getHideUIElements().isSelected()) {
                args.add("-" + SetViewerParsedCommand.HIDEWINDOWUI_ARG);
            }
            if (panel.getResizeToFit().isSelected()) {
                args.add("-" + SetViewerParsedCommand.FITWINDOW_ARG);
            }
            if (panel.getCenterScreen().isSelected()) {
                args.add("-" + SetViewerParsedCommand.CENTERWINDOW_ARG);
            }
            if (panel.getDisplayTitle().isSelected()) {
                args.add("-" + SetViewerParsedCommand.DOCTITLE_ARG);
            }
            if (panel.getNoPageScaling().isSelected()) {
                args.add("-" + SetViewerParsedCommand.NOPRINTSCALING_ARG);
            }
            if (panel.getOverwriteCheckbox().isSelected()) {
                args.add("-" + SetViewerParsedCommand.OVERWRITE_ARG);
            }
            if (panel.getOutputCompressedCheck().isSelected()) {
                args.add("-" + SetViewerParsedCommand.COMPRESSED_ARG);
            }

            args.add("-" + SetViewerParsedCommand.PDFVERSION_ARG);
            args.add(((StringItem) panel.getVersionCombo().getSelectedItem()).getId());

            args.add(SetViewerParsedCommand.COMMAND_SETVIEWER);

            String[] myStringArray = (String[]) args.toArray(new String[args.size()]);
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
            retList.add("-" + SetViewerParsedCommand.F_ARG);
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

}
