/*
 * Created on 06-Nov-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.TextPaneAppender;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Listener to the log actions
 * 
 * @author Andrea Vacondio
 * 
 */
public class LogActionListener implements ActionListener {

    private static final Logger log = Logger.getLogger(LogActionListener.class.getPackage().getName());

    public static final String CLEAR_LOG_ACTION = "clearlog";
    public static final String SAVE_LOG_ACTION = "savelog";
    public static final String SELECTALL_LOG_ACTION = "selectalllog";

    private JTextPane logTextArea;

    public LogActionListener(JTextPane logTextArea) {
        this.logTextArea = logTextArea;

    }

    public LogActionListener() {
        this(TextPaneAppender.getTextPaneInstance());
    }

    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getActionCommand().equals(LogActionListener.CLEAR_LOG_ACTION)) {
            clearTextPane();
        } else if (arg0.getActionCommand().equals(LogActionListener.SELECTALL_LOG_ACTION)) {
            selectAllTextPane();
        } else if (arg0.getActionCommand().equals(LogActionListener.SAVE_LOG_ACTION)) {
            saveLog();
        } else {
            log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Unknown action."));
        }
    }

    /**
     * clear the text of the log text pane
     */
    private void clearTextPane() {
        logTextArea.setText("");
    }

    /**
     * select the text of the log text pane
     */
    private void selectAllTextPane() {
        logTextArea.selectAll();
        logTextArea.requestFocus();
    }

    /**
     * Save log text to file
     */
    private void saveLog() {
        JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.TXT_FILE,
                JFileChooser.FILES_ONLY);
        if (fileChooser.showSaveDialog(logTextArea) == JFileChooser.APPROVE_OPTION) {
            File chosenFile = fileChooser.getSelectedFile();
            if (chosenFile != null) {
                try {
                    FileWriter fileWriter = new FileWriter(chosenFile);
                    fileWriter.write(logTextArea.getText());
                    fileWriter.flush();
                    fileWriter.close();
                    log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Log saved."));
                } catch (Exception e) {
                    log.error("Error saving log file. ", e);
                }
            }
        }
    }

}
