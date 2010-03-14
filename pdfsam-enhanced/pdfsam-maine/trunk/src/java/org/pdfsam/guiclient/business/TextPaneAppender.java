/*
 * Created on 13-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
/**
 * JTextPane appender
 * @author Andrea Vacondio
 *
 */
public class TextPaneAppender extends AppenderSkeleton {

	private static JTextPane logTextArea = null;
	private static StyledDocument styledDocument = null;
	private static Hashtable<Level, SimpleAttributeSet> attributes = null;
	
	public TextPaneAppender() {
		getTextPaneInstance();
	}

	public synchronized static JTextPane getTextPaneInstance(){
		if(logTextArea == null){
			logTextArea = new JTextPane();
			logTextArea.setEditable(false);
			logTextArea.setDragEnabled(true);
			DefaultCaret caret = (DefaultCaret)logTextArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			styledDocument = logTextArea.getStyledDocument();
			createTextAttributes();
		}
		return logTextArea;
	}
	
	/**
	 * creates attributes map for message style
	 */
	private static void createTextAttributes() {	
		attributes = new Hashtable<Level, SimpleAttributeSet>();
		attributes.put(Level.ERROR, new SimpleAttributeSet());
		attributes.put(Level.FATAL, new SimpleAttributeSet());
		attributes.put(Level.WARN, new SimpleAttributeSet());
		StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.ERROR), Color.red);
		StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.FATAL), Color.red);
		StyleConstants.setForeground((MutableAttributeSet)attributes.get(Level.WARN), Color.blue);
	  }
	
	protected void append(LoggingEvent arg0) {
		if(this.layout != null){
			String logText = this.layout.format(arg0);
			StringBuilder trace = new StringBuilder("");
			try{
				  if (arg0.getThrowableInformation() != null) {
			            String[] throwableStrings = arg0.getThrowableInformation().getThrowableStrRep();			           
			            for (int i = 0; i < throwableStrings.length; i++){
			               trace.append(throwableStrings[i]).append("\n");
			            }
			        }
				    if(attributes.get(arg0.getLevel()) != null){
				    	styledDocument.insertString(styledDocument.getLength(), logText+trace.toString(), (MutableAttributeSet)attributes.get(arg0.getLevel()));
				    }else{
				    	styledDocument.insertString(styledDocument.getLength(), logText, null);
				    }			
			}catch(BadLocationException e){
				logTextArea.setText(logTextArea.getText()+this.layout.format(arg0));
			}
		}
	}

	public void close() {
		logTextArea = null;
	}

	public boolean requiresLayout() {
		return true;
	}


}
