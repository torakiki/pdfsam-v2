/*
 * Created on 07-Nov-2007
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pdfsam.guiclient.GuiClient;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
/**
 * Environment logic
 * @author Andrea Vacondio
 *
 */
public class Environment {
	
	private static final Logger log = Logger.getLogger(Environment.class.getPackage().getName());
	
	private ResourceBundle i18nMessages;

	private Hashtable plugins;
	
	public Environment(Hashtable plugins){
		this.plugins = plugins;
		this.i18nMessages = Configuration.getInstance().getI18nResourceBundle();
	}
	
	/**
	 * saves and environment to the output file
	 * @param outFile
	 * @param savePasswords true save passwords informations
	 */
	public void saveEnvironment(File outFile, boolean savePasswords){
		try {
			if (outFile != null){
				synchronized(Environment.class){
					Document document = DocumentHelper.createDocument();
					Element root = document.addElement("pdfsam_saved_jobs");
					root.addAttribute("version", GuiClient.VERSION);
					root.addAttribute("savedate", new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
					for (Iterator plugsIterator = plugins.values().iterator();plugsIterator.hasNext();) {
						AbstractPlugablePanel plugablePanel = (AbstractPlugablePanel) plugsIterator.next();
						Element node = (Element) root.addElement("plugin");
						node.addAttribute("class", plugablePanel.getClass().getName());
						node.addAttribute("name", plugablePanel.getPluginName());
						plugablePanel.getJobNode(node, savePasswords);
						log.info(GettextResource.gettext(i18nMessages, plugablePanel.getPluginName()+ " node environment loaded."));
					}
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
					OutputFormat format = OutputFormat.createPrettyPrint();
					format.setEncoding("UTF-8");
					XMLWriter xmlWriter = new XMLWriter(bos, format);
					xmlWriter.write(document);
					xmlWriter.flush();
					xmlWriter.close();
				}
				log.info(GettextResource.gettext(i18nMessages, "Environment saved."));
			}else{
				log.error(GettextResource.gettext(i18nMessages, "Error saving environment, output file is null."));
			}
		} catch (Exception ex) {
			log.error(GettextResource.gettext(i18nMessages, "Error saving environment."),ex);
		}
	}
	
	/**
	 * saves and environment to the output file without saving passwords
	 * @param outFile
	 */
	public void saveEnvironment(File outFile){
		saveEnvironment(outFile,false);
	}	
	/**
	 * loads an environment from an input file
	 * @param inputFile
	 */
	public void loadJobs(File inputFile) {
		if (inputFile != null && inputFile.exists() && inputFile.canRead()) {
			try {
				synchronized(Environment.class){
					SAXReader reader = new SAXReader();
					Document document = reader.read(inputFile);
					for (Iterator plugsIterator = plugins.values().iterator();plugsIterator.hasNext();) {
						AbstractPlugablePanel plugablePanel = (AbstractPlugablePanel) plugsIterator.next();
						Node node = document.selectSingleNode("/pdfsam_saved_jobs/plugin[@class=\""
								+ plugablePanel.getClass().getName() + "\"]");
						if(node != null){
							plugablePanel.loadJobNode(node);
						}
					}
					log.info(GettextResource.gettext(i18nMessages, "Environment loaded."));
				}
			} catch (Exception ex) {
				log.error(GettextResource.gettext(i18nMessages, "Error loading environment."),ex);
			}
		} else {
			log.error(GettextResource.gettext(i18nMessages, "Error loading environment from input file. "+inputFile));
		}
	}
}
