/*
 * Created on 24-Jul-2009
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

import javax.swing.JTextField;
/**
 * Listener for the prefix menu actions
 * @author Andrea Vacondio
 *
 */
public class PrefixMenuActionListener implements ActionListener {

	//actions
	public static final String INSERT_BASENAME_ACTION = "basename";
	public static final String INSERT_TIMESTAMP_ACTION = "timestamp";
	public static final String INSERT_CURRENTPAGE_ACTION = "currentpage";
	public static final String INSERT_BOOKMARK_NAME_ACTION = "bookmarkname";
	public static final String INSERT_FILENUMBER_ACTION = "filenumber";
	
	//prefixes
	private static final String CURRENTPAGE_STRING = "[CURRENTPAGE]";
	private static final String FILENUMBER_STRING = "[FILENUMBER]";
	private static final String TIMESTAMP_STRING = "[TIMESTAMP]";
	private static final String BASENAME_STRING  = "[BASENAME]";
	private static final String BOOKMARK_NAME_STRING  = "[BOOKMARK_NAME]";
	
	private JTextField textField;
		
	/**
	 * @param textField
	 */
	public PrefixMenuActionListener(JTextField textField) {
		super();
		this.textField = textField;
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(PrefixMenuActionListener.INSERT_BASENAME_ACTION)){
			insertTextAtCaret(BASENAME_STRING);
		}else if(e.getActionCommand().equals(PrefixMenuActionListener.INSERT_TIMESTAMP_ACTION)){
			insertTextAtCaret(TIMESTAMP_STRING);
		}else if(e.getActionCommand().equals(PrefixMenuActionListener.INSERT_CURRENTPAGE_ACTION)){
			insertTextAtCaret(CURRENTPAGE_STRING);
		}else if(e.getActionCommand().equals(PrefixMenuActionListener.INSERT_BOOKMARK_NAME_ACTION)){
			insertTextAtCaret(BOOKMARK_NAME_STRING);
		}else if(e.getActionCommand().equals(PrefixMenuActionListener.INSERT_FILENUMBER_ACTION)){
			insertTextAtCaret(FILENUMBER_STRING);
		}
	}

	/**
	 * insertion of the prefix string at the caret position
	 * @param prefix
	 */
	private void insertTextAtCaret(String prefix){
		if(textField!=null && textField.getText()!=null){
			StringBuilder sb = new StringBuilder(textField.getText());
			if(textField.getSelectedText() != null){
				sb.replace(textField.getSelectionStart(), textField.getSelectionEnd(), prefix);
			}else{
			int position = textField.getCaretPosition();
				sb.insert(position, prefix);
			}
			textField.setText(sb.toString());
		}
	}
}
