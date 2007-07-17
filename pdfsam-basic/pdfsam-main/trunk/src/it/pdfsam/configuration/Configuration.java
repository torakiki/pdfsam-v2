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
import it.pdfsam.types.ListItem;
import it.pdfsam.utils.XMLConfig;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Configuration Singleton
 * @author a.vacondio
 *
 */
public class Configuration  implements Serializable{
		
	private static final long serialVersionUID = -1987140465838537628L;
	
	private static Configuration configObject;
	private ResourceBundle i18n_messages;
	private XMLConfig xmlConfigObject;
	private MainConsole mc;
	private Vector langs;

	private Configuration() {	
		mc = new MainConsole();
	}

	public static synchronized Configuration getInstance() { 
		if (configObject == null){
			configObject = new Configuration();
		}
		return configObject;
	}

	/**
	 * Sets the language ResourceBundle
	 * @param i18n_messages ResourceBundle
	 */
	public synchronized void setI18nResourceBundle(ResourceBundle i18n_messages){
		this.i18n_messages = i18n_messages;
	}

	/**
	 * @return the language ResourceBundle
	 */
	public synchronized ResourceBundle getI18nResourceBundle(){
		return i18n_messages;
	}

	/**
	 * @return the XMLConfig
	 */
	public synchronized XMLConfig getXmlConfigObject() {
		return xmlConfigObject;
	}

	/**
	 * sets the XMLConfig
	 * @param xmlConfigObject
	 */
	public synchronized void setXmlConfigObject(XMLConfig xmlConfigObject) {
		this.xmlConfigObject = xmlConfigObject;
	}

	/**
	 * cannot clone a singleton
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone configuration object.");
	}

	/**
	 * @return the MainConsole
	 */
	public MainConsole getMainConsole() {
		return mc;
	}

	 /**
     * @return language list
     */
    public List getLanguagesList(){ 
    	Collections.sort(langs);
        return langs;
    }
    
    /**
	 * @param languageList languages list 
	 */
	public synchronized void setLanguageList(Vector languageList) {
		try{
			langs = new Vector(languageList.size(), 1);
			langs.addAll(languageList);
		}catch(Exception e){
			langs = new Vector(1, 1);
			langs.add(new ListItem("en_GB", "English"));
		}
	}
}
