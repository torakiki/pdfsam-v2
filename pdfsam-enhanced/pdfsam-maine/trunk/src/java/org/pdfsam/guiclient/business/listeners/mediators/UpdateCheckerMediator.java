/*
 * Created on 27-Feb-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.listeners.mediators;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.gui.panels.JStatusPanel;
import org.pdfsam.guiclient.updates.UpdateManager;
import org.pdfsam.i18n.GettextResource;

/**
 * Update checker mediator
 * 
 * @author Andrea Vacondio
 */
public class UpdateCheckerMediator implements ActionListener {

    private static final String DESTINATION_URL = "http://www.pdfsam.org/check-version.php";
    private static final Logger LOG = Logger.getLogger(UpdateChecker.class.getPackage().getName());

    private JStatusPanel statusPanel = null;
    private ScheduledExecutorService executor;

    public UpdateCheckerMediator(JStatusPanel statusPanel) {
        this.statusPanel = statusPanel;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void actionPerformed(ActionEvent e) {
        checkForUpdates();
    }

    /**
     * Run the runnable to check if an update is available after a delay
     * 
     * @param delay
     * @param forceRecheck
     *            tells if a complete check have to be done.
     */
    public void checkForUpdates(long delay, boolean forceRecheck) {
        UpdateChecker updateChecker = new UpdateChecker(forceRecheck);
        executor.schedule(updateChecker, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Run the runnable to check if an update is available
     */
    public void checkForUpdates() {
        checkForUpdates(0, true);
    }

    /**
     * Callable that checks if a new version is available
     * 
     * @author Andrea Vacondio
     */
    private class UpdateChecker extends SwingWorker<String, Void> {

        private boolean forceRecheck = false;
        private UpdateManager updateManager = null;

        /**
         * @param forceRecheck
         */
        public UpdateChecker(boolean forceRecheck) {
            this.forceRecheck = forceRecheck;
            this.updateManager = new UpdateManager(String.format("%s?version=%s&remoteversion=%s&branch=%s",
                    DESTINATION_URL, GuiClient.getVersionType(), GuiClient.getVersion(), GuiClient.getBranch()));
        }

        @Override
        protected String doInBackground() throws Exception {
            updateManager.checkForNewVersion(forceRecheck);
            if (updateManager.isNewVersionAvailable()) {
                LOG.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "New version available."));
                return updateManager.getAvailableVersion();
            } else {
                LOG.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "No new version available."));
            }
            return "";
        }

        @Override
        protected void done() {
            String newVersion;
            try {
                newVersion = get();
                if (StringUtils.isNotBlank(newVersion)) {
                    statusPanel.setNewAvailableVersion(newVersion);
                }
            } catch (Exception e) {
                LOG.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), e);
            }

        }

    }
}
