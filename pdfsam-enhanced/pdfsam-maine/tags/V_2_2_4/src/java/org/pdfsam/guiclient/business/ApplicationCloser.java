/*
 * Created on 11-Oct-2009
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
package org.pdfsam.guiclient.business;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.configuration.GuiConfiguration;
import org.pdfsam.guiclient.gui.frames.JMainFrame;
import org.pdfsam.i18n.GettextResource;

/**
 * Contains methods that get called during the application closure
 * 
 * @author Andrea Vacondio
 * 
 */
public class ApplicationCloser {

    private static final Logger log = Logger.getLogger(ApplicationCloser.class.getPackage().getName());

    private JMainFrame mainFrame;

    /**
     * @param mainFrame
     */
    public ApplicationCloser(JMainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
    }

    /**
     * Saves the user interface configuration
     */
    public void saveGuiConfiguration() {
        GuiConfiguration.getInstance().setExtendedState(mainFrame.getExtendedState());
        if (mainFrame.isShowing()) {
            GuiConfiguration.getInstance().setLocationOnScreen(mainFrame.getLocationOnScreen());
        }
        GuiConfiguration.getInstance().setSize(mainFrame.getSize());
        GuiConfiguration.getInstance().setVerticalDividerLocation(mainFrame.getVerticalDividerLocation());
        GuiConfiguration.getInstance().setVerticalDividerDimension(mainFrame.getVerticalDividerDimension());
        GuiConfiguration.getInstance().setHorizontalDividerLocation(mainFrame.getHorizontalDividerLocation());
        GuiConfiguration.getInstance().setHorizontalDividerDimension(mainFrame.getHorizontalDividerDimension());
        GuiConfiguration.getInstance().setSelectedPlugin(mainFrame.getTreePanel().getSelectedPlugin());

        try {
            GuiConfiguration.getInstance().save();
        } catch (IOException ioe) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Unable to save user interface status."), ioe);
        }
    }

}
