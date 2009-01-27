/*
 * Created on 12-Oct-2008
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
package org.pdfsam.guiclient.utils;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Utility to show dialogs
 * @author Andrea Vacondio
 *
 */
public class DialogUtility {

	/**
	 * Shows a yes/no/cancel dialog to ask for change the output directory
	 * @param comp parent component
	 * @param suggestedDir suggested directory
	 * @return
	 */
	public static int showConfirmOuputLocationDialog(Component comp, String suggestedDir){

		return JOptionPane.showOptionDialog(comp,
			    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Output file location is not correct")+".\n"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Would you like to change it to")+" "+suggestedDir+" ?",
			    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Output location error"),
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.QUESTION_MESSAGE,null,
			    null,
			    null);
	}
	
	/**
	 * Shows a yes/no/cancel dialog to ask the user about overwriting output file
	 * @param comp parent component
	 * @param filename suggested directory
	 * @return
	 */
	public static int askForOverwriteOutputFileDialog(Component comp, String filename){

		return JOptionPane.showOptionDialog(comp,
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selected output file already exists")+filename+".\n"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Would you like to overwrite it?"),
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Output location error"),
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    null,
		    null);

	}
	/**
	 * Show a dialog to ask the user for the document password
	 * @param comp
	 * @return
	 */
	public static String askForDocumentPasswordDialog(Component comp, String filename){
		String retVal = null;
		JPasswordField passwordField= new JPasswordField();
		Object[] message = new Object[] {GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please provide the password to open the encrypted document"), filename, passwordField};

		if(JOptionPane.showOptionDialog(comp, message, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Password request"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0){
			retVal= String.valueOf(passwordField.getPassword());
		}
		return retVal; 
	}
}
