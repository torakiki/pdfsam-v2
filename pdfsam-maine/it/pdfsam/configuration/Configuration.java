/*
 * Created on 21-Dec-2006
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
package it.pdfsam.configuration;

import it.pdfsam.console.MainConsole;
import it.pdfsam.util.XMLConfig;

import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Configuration Singleton
 * @author a.vacondio
 *
 */
public class Configuration  implements Serializable{
	
	private static final long serialVersionUID = -3590221973347278456L;

	private static Configuration configObject;
	private ResourceBundle i18n_messages;
	private int logLevel;
	private XMLConfig xmlConfigObject;
	private MainConsole mc;

	private Configuration() {	
		mc = new MainConsole();
	}

	public static synchronized Configuration getInstance() { 
		if (configObject == null){
			configObject = new Configuration();
		}
		return configObject;
	}

	public synchronized void setI18nResourceBundle(ResourceBundle i18n_messages){
		this.i18n_messages = i18n_messages;
	}

	public synchronized ResourceBundle getI18nResourceBundle(){
		return i18n_messages;
	}

	public synchronized int getLogLevel() {
		return logLevel;
	}

	public synchronized void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public synchronized XMLConfig getXmlConfigObject() {
		return xmlConfigObject;
	}

	public synchronized void setXmlConfigObject(XMLConfig xmlConfigObject) {
		this.xmlConfigObject = xmlConfigObject;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone configuration object.");
	}

	public MainConsole getMainConsole() {
		return mc;
	}


}
