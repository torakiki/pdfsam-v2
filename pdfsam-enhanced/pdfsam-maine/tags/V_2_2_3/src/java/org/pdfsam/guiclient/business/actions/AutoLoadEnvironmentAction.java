/*
 * Created on 24/feb/2011
 * Copyright (C) 2010 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.business.environment.Environment;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Action that automatically loads an environment
 * 
 * @author Andrea Vacondio
 * 
 */
public class AutoLoadEnvironmentAction extends AbstractAction {

    private static final long serialVersionUID = -3407253335399985764L;

    private Environment environment;
    private File recentEnvironment;

    public AutoLoadEnvironmentAction(Environment environment, File recentEnvironment) {
        super(recentEnvironment.getName());
        this.putValue(Action.SHORT_DESCRIPTION, GettextResource.gettext(Configuration.getInstance()
                .getI18nResourceBundle(), recentEnvironment.getAbsolutePath()));
        this.setEnabled(true);
        this.environment = environment;
        this.recentEnvironment = recentEnvironment;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (recentEnvironment != null && StringUtils.endsWithIgnoreCase(recentEnvironment.getName(), ".xml")) {
            if (recentEnvironment.exists()) {
                environment.loadJobs(recentEnvironment);
            }
        }
    }
}
