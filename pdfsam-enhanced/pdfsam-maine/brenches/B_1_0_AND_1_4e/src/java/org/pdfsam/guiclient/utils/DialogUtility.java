/*
 * Created on 12-Oct-2007
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
package org.pdfsam.guiclient.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Utility to show dialogs
 * @author Andrea Vacondio
 *
 */
public class DialogUtility {

	/**
	 * Shows a yes/no/cancel dialog to ask for change the ouput directory
	 * @param comp parent component
	 * @param suggestedDir suggested directory
	 * @return the dialog return value
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
	 * @return an integer indicating the option chosen by the user
	 */
	public static int askForOverwriteOutputFileDialog(Component comp, String filename){

		return JOptionPane.showOptionDialog(comp,
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selected output file already exists ")+filename+"\n"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Would you like to overwrite it?"),
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Output location error"),
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    null,
		    null);
	}
	
	/**
	 * Shows an error dialog
	 * @param comp
	 * @param bounds
	 */
	public static void errorValidatingBounds(Component comp, String bounds){
		JOptionPane.showMessageDialog(comp, 
			GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Provided pages selection is not valid")+" ("+bounds+")\n"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Limits must be a comma separated list of \"page_number\" or \"page_number-page_number\""),
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Limits are not valid"), 
		    JOptionPane.ERROR_MESSAGE);
	}
}
