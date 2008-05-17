/*
 * Created on 19-June-2006
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

package org.pdfsam.emp4j.providers;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.emp4j.messages.interfaces.InquirableMessagesSource;
/**
 * Exception messages provider. Singleton.
 * @author a.vacondio
 *
 */
public class ExceptionMessageProvider implements Serializable{

	private static final long serialVersionUID = -3579371571520612008L;
	public static final String CONFIGURATION_PATH_PARAM = "emp4j.configuration";
	public static final String CONFIGURATION_PATH_DEFAULT = "emp4j.xml";
	
	private static ExceptionMessageProvider providerInstance;		
	private InquirableMessagesSource source;
	private static boolean errorOnCreate = false;
	/**
	 * logger 
	 */
	private static transient Logger log;
	
	private ExceptionMessageProvider() throws Exception {
		Document document = getConfiguration();		
		Node classNode = document.selectSingleNode("/exception-message-provider/source/@class");
		if(classNode != null){
			Class fileSourceClass = Class.forName(classNode.getText());
			Node sourceNode = document.selectSingleNode("/exception-message-provider/source");
			Constructor constructor = fileSourceClass.getConstructor(new Class[]{Node.class});
			source = (InquirableMessagesSource) constructor.newInstance(new Object[]{sourceNode});
		}
		else{
			throw new Exception("Unable to find MessagesSource class name");
		}	
	}

	/**
	 * @return the ExceptionMessageProvider instance
	 */
	public static synchronized ExceptionMessageProvider getInstance() throws Exception{ 
		try{
			if (providerInstance == null){
				if(!ExceptionMessageProvider.errorOnCreate){
					providerInstance = new ExceptionMessageProvider();
				}
			}
		}catch(Throwable t){
			ExceptionMessageProvider.errorOnCreate = true;
			getLog().fatal("Error creating instance of ExceptionMessageProvider." , t);
		}
		return providerInstance;
	}

	/**
	 * This method tries to get the Document containing the ExceptionMessageProvider configuration.
	 * It gets the value of the property emp4j.configuration assigning it the default value 'emp4j.xml' if the property is empty.
	 * First it tries using the value of emp4j.configuration as an absolute path.
	 * Second it tries using the value of emp4j.configuration Resource name or a SystemResource name.
	 * @return the Document object
	 * @throws Exception
	 */
	private Document getConfiguration() throws Exception{
		Document retVal;
		SAXReader reader = new SAXReader();
		String configPath = System.getProperty(CONFIGURATION_PATH_PARAM, CONFIGURATION_PATH_DEFAULT);
		File configFile = new File(configPath);
		if(configFile.exists()){
			retVal = reader.read(configFile);
		}else{
			ClassLoader cl = ExceptionMessageProvider.class.getClassLoader();
			URL resourceUrl = null;
			if(cl != null){
				resourceUrl = cl.getResource(configPath);
				if(resourceUrl == null){
					resourceUrl = cl.getResource(configPath);
				}
			}else{
				resourceUrl = ClassLoader.getSystemResource(configPath);
			}
			if(resourceUrl != null){
				retVal = reader.read(resourceUrl);
			}else{
				throw new NullPointerException("Cannot locate ExceptionMessageProvider configuration file.");
			}
		}
		return retVal;
	}
	
	/**
	 * cannot clone a singleton
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone ExceptionMessageProvider object.");
	}
	
	/**
	 * @param exceptionTypeKey Exception type code
	 * @param exceptionErrorCode Exception error code
	 * @return the exception message 
	 * @throws Exception
	 */
	public synchronized String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception{
		return source.getExceptionMessage(exceptionTypeKey, exceptionErrorCode);
	}
	
	/**
	 * @param exceptionTypeKey Exception type code
	 * @param exceptionErrorCode Exception error code
	 * @param args Strings to be substituted to the message
	 * @return the exception message 
	 * @throws Exception
	 */
	public synchronized String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception{
			return source.getExceptionMessage(exceptionTypeKey, exceptionErrorCode, args);		
	}	

	/**
	 * @param exceptionTypeKey Exception type code
	 * @param exceptionErrorCode Exception error code
	 * @return the localized exception message 
	 * @throws Exception
	 */
	public synchronized String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception{
		return source.getLocalizedExceptionMessage(exceptionTypeKey, exceptionErrorCode);
	}
	
	/**
	 * @param exceptionTypeKey Exception type code
	 * @param exceptionErrorCode Exception error code
	 * @param args Strings to be substituted to the message
	 * @return the localized exception message 
	 * @throws Exception
	 */
	public synchronized String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception{
			return source.getLocalizedExceptionMessage(exceptionTypeKey, exceptionErrorCode, args);		
	}
	
	/**
	 * @return Logger
	 */
	private static Logger getLog(){
		if ( log == null){
			log =  Logger.getLogger(ExceptionMessageProvider.class.getPackage().getName());
		}
		return log;
	}
}
