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
package org.pdfsam.guiclient.configuration;

import java.io.File;
import java.net.URLDecoder;
import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.guiclient.business.TextPaneAppender;
import org.pdfsam.guiclient.l10n.LanguageLoader;
import org.pdfsam.guiclient.utils.ThemeUtility;
import org.pdfsam.guiclient.utils.xml.XMLConfig;
import org.pdfsam.i18n.GettextResource;

/**
 * Configuration Singleton
 * @author a.vacondio
 *
 */
public class Configuration{

	private static final Logger log = Logger.getLogger(Configuration.class.getPackage().getName());
	
    
	private static Configuration configObject;
	private ResourceBundle i18nMessages;
	private XMLConfig xmlConfigObject;
	private ConsoleServicesFacade servicesFacade;
	private Level loggingLevel = Level.DEBUG;
	private boolean checkForUpdates = true;

	private Configuration() {
		init();
	}

	public static synchronized Configuration getInstance() { 
		if (configObject == null){
			configObject = new Configuration();
		}
		return configObject;
	}

	/**
	 * Sets the language ResourceBundle
	 * @param i18nMessages ResourceBundle
	 */
	public synchronized void setI18nResourceBundle(ResourceBundle i18nMessages){
		this.i18nMessages = i18nMessages;
	}

	/**
	 * @return the language ResourceBundle
	 */
	public synchronized ResourceBundle getI18nResourceBundle(){
		return i18nMessages;
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

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone configuration object.");
	}

	/**
	 * @return the ConsoleServicesFacade
	 */
	public ConsoleServicesFacade getConsoleServicesFacade() {
		return servicesFacade;
	}			
	
	/**
	 * @return the loggingLevel
	 */
	public Level getLoggingLevel() {
		return loggingLevel;
	}
	
	/**
	 * @return the checkForUpdates
	 */
	public boolean isCheckForUpdates() {
		return checkForUpdates;
	}

	private void init(){
		try{
			String applicationPath = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8")).getParent();
			log.info("Loading configuration..");
			xmlConfigObject = new XMLConfig(applicationPath, true);
			servicesFacade = new ConsoleServicesFacade();
			//language
			setLanguage();
			//look and feel
			setLookAndFeel();
			//log init
			setLoggingLevel();
			//check for updates init
			setCheckForUpdates();
		}catch(Exception e){
			log.fatal(e);
		}
	}
	
	/**
	 * sets the look and feel
	 * @throws Exception
	 */
	private void setLookAndFeel() throws Exception{
		log.info(GettextResource.gettext(i18nMessages,"Setting look and feel..."));
		int lookAndFeelIntValue = Integer.parseInt(xmlConfigObject.getXMLConfigValue("/pdfsam/settings/lookAndfeel/LAF"));
		String loogAndFeel = ThemeUtility.getLAF(lookAndFeelIntValue);            
		if (ThemeUtility.isPlastic(lookAndFeelIntValue)){            
			ThemeUtility.setTheme(Integer.parseInt(xmlConfigObject.getXMLConfigValue("/pdfsam/settings/lookAndfeel/theme")));
		}
		UIManager.setLookAndFeel(loogAndFeel);
	}
	
	/**
	 * sets the ResourceBoudle for the selected language
	 */
	private void setLanguage(){
		log.info("Getting language...");
		String language;
		try{
			language = xmlConfigObject.getXMLConfigValue("/pdfsam/settings/i18n");
		}catch(Exception e){
			log.warn("Unable to get language ResourceBudle, setting the default language.", e);
			language = LanguageLoader.DEFAULT_LANGUAGE;
		}
	
		//get bundle
		i18nMessages = (new LanguageLoader(language, "org.pdfsam.i18n.resources.Messages").getBundle());
	}
	
	/**
	 * sets the logging threshold for the appender
	 */
	private void setLoggingLevel(){
		log.info(GettextResource.gettext(i18nMessages,"Setting logging level..."));
		int logLevel;
		try {
			String logLev = xmlConfigObject.getXMLConfigValue("/pdfsam/settings/loglevel");
			if(logLev != null && logLev.length()>0){
				logLevel = Integer.parseInt(logLev);
			}else{
				log.warn(GettextResource.gettext(i18nMessages,"Unable to find log level, setting to default level (DEBUG)."));
				logLevel = Level.DEBUG_INT;
			}
			TextPaneAppender appender = (TextPaneAppender)Logger.getLogger("org.pdfsam").getAppender("JLogPanel");
			loggingLevel = Level.toLevel(logLevel,Level.DEBUG);
			log.info(GettextResource.gettext(i18nMessages,"Logging level set to ")+loggingLevel);
			appender.setThreshold(loggingLevel);
		} catch (Exception e) {
			log.warn(GettextResource.gettext(i18nMessages,"Unable to set logging level."), e);
		}
	}
	
	/**
	 * sets the configuration about the updates check
	 */
	private void setCheckForUpdates(){
		try {
			String checkUpdates = xmlConfigObject.getXMLConfigValue("/pdfsam/settings/checkupdates");
			if(checkUpdates != null && checkUpdates.length()>0){
				checkForUpdates = Integer.parseInt(checkUpdates)== 1;
			}
			
		} catch (Exception e) {
			//default
			checkForUpdates = true;
		}
	}
}
