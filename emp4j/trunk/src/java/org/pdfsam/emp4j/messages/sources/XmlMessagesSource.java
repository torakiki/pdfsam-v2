/*
 * Created on 21-June-2006
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
package org.pdfsam.emp4j.messages.sources;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.emp4j.messages.interfaces.AbstractMessagesSource;
import org.w3c.dom.DOMException;

/**
 * Implementation of the MessageSourceInterface.
 * It gets exception messages from an xml source
 * @author Andrea Vacondio
 *
 */
public class XmlMessagesSource extends AbstractMessagesSource{

	private static final long serialVersionUID = 1L;
	private Document document;

	public XmlMessagesSource(Node node) throws Exception{
		super(node);
		Node arg1 = node.selectSingleNode("xmlsource/@filename");
		if(arg1 != null){
			document = getDocument(arg1.getText());
		}
		else{
			throw new DOMException(DOMException.NOT_FOUND_ERR, "Error reading configuration node, node is null.");
		}
	}
	
	/**
	 * This method tries to get the Document containing the exception messages. 
	 * First it tries using <code>filename</code> as a path to file.
	 * Second it tries to use it as Resource name or a SystemResource name.
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private Document getDocument(String filename) throws Exception{
		Document retVal;
		SAXReader reader = new SAXReader();
		File resourceFile = new File(filename);
		if(resourceFile.exists()){
			retVal = reader.read(resourceFile);
		}else{
			ClassLoader cl = XmlMessagesSource.class.getClassLoader();
			URL resourceUrl = null;
			if(cl != null){
				resourceUrl = cl.getResource(filename);
			}else{
				resourceUrl = ClassLoader.getSystemResource(filename);
			}
			if(resourceUrl != null){
				retVal = reader.read(resourceUrl);
			}else{
				throw new NullPointerException("Cannot locate XmlMessageSource data file.");
			}
		}
		return retVal;
	}
	
	public String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception{
		return getExceptionMessage(exceptionTypeKey, exceptionErrorCode);
	}
	
	public String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception{
		String retVal = "";
		retVal = getTypeDescription(exceptionTypeKey.toString())+padString(Integer.toString(exceptionErrorCode), 3, '0', true)+" - ";
		Node node = document.selectSingleNode("/exception-types/type[@key=\""+exceptionTypeKey.toString()+"\"]/exception[@errorcode=\""+exceptionErrorCode+"\"]/@message");
		if(node != null){
			retVal += node.getText();
		}
		return retVal;
	}

	/**
	 * 
	 * @param exceptionTypeKey the given exception type key
	 * @return the associated description
	 * @throws Exception
	 */
	private String getTypeDescription(String exceptionTypeKey) throws Exception{
		String retVal = "UNK";
		Node node = document.selectSingleNode("/exception-types/type[@key=\""+exceptionTypeKey+"\"]/@description");
		if(node != null){
			retVal = node.getText();
		}
		return retVal;
	}
	
	/**
	 * Given a string s, length n it fills with char c till length n
	 * @param s input String
	 * @param n output string length
	 * @param c char
	 * @param paddingLeft if <code>true</code> chars are added to the left.
	 */
	private String padString(String s, int n, char c, boolean paddingLeft) {		
		StringBuffer str = new StringBuffer(s);
		int strLength = str.length();
		if (n > 0 && n > strLength) {
			for (int i = 0; i <= n; i++) {
				if (paddingLeft) {
					if (i < n - strLength) {str.insert(0, c);}
				}
				else {
					if (i > strLength) {str.append(c);}
				}
			}
		}
		return str.toString();
	}
}
