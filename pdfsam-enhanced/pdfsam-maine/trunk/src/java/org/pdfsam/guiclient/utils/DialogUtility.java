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

	public static final int AT_LEAST_ONE_DOC = 0;
	public static final int TWO_DOC = 1;

	/**
	 * Shows a yes/no/cancel dialog to ask for change the output directory
	 * @param comp parent component
	 * @param suggestedDir suggested directory
	 * @return an integer indicating the option chosen by the user
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
	 * Shows a warning dialog telling the user that at least one document should be selected
	 * @param comp
	 * @param msgType the type of the message
	 */
	public static void showWarningNoDocsSelected(Component comp, int msgType){
		String msg = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Please select at least one pdf document.");
		if(TWO_DOC == msgType){
			msg = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please select two pdf documents.");
		}
		JOptionPane.showMessageDialog(comp, 
									  msg, 
									  GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Warning"), 
									  JOptionPane.WARNING_MESSAGE);
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
	 * Shows a yes/no/cancel asking the user for a confirmation about the overwrite check
	 * @param comp
	 * @return
	 */
	public static int askForOverwriteConfirmation(Component comp){

		return JOptionPane.showOptionDialog(comp,
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please note that output files will overwrite existing files of the same name without warning.")+"\n"+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Do you confirm to overwrite?"),
		    GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Confirm overwrite"),
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
	
	/**
	 * Generic error dialog
	 * @param comp
	 * @param title
	 * @param message
	 */
	public static void errorGenericDialog(Component comp, String title, String message){
		JOptionPane.showMessageDialog(comp, 
			message,
			title, 
		    JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Show a dialog to ask the user for the document password
	 * @param comp
	 * @return an integer indicating the option chosen by the user
	 */
	public static String askForDocumentPasswordDialog(Component comp, String filename){
		String retVal = null;
		JPasswordField passwordField= new JPasswordField();
		Object[] message = new Object[] {GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please provide the password to open the encrypted document"), filename, passwordField};
		passwordField.requestFocus();
		
		if(JOptionPane.showOptionDialog(comp, message, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Password request"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0){
			retVal= String.valueOf(passwordField.getPassword());
		}
		return retVal; 
	}
	
	/**
	 * Shows a yes/no confirmation dialog to ask the user if he wants to empty the selection list
	 * @param comp
	 * @return an integer indicating the option chosen by the user
	 */
	public static int askForEmptySelectionPanel(Component comp){
		return  JOptionPane.showConfirmDialog(comp,
   				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selection list is full, would you like to empty it and load the new document?"),
				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"List full"),
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE);
	}
}
