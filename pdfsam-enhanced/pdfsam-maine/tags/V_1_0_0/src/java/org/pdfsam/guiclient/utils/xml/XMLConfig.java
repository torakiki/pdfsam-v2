/*
 * Created on 15-feb-2005
 * Usage: XMLConfig xml_cfg_document = new XMLConfig();
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

/**
 * @author Andrea Vacondio
 *
 */
package org.pdfsam.guiclient.utils.xml;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.guiclient.exceptions.ConfigurationException;


/**
 * Object for the xml config file. It provides functions to get and set xml tags in the
 * config.xml file.
 * @author Andrea Vacondio
 * @see org.pdfsam.guiclient.utils.xml.XMLParser
 * @see org.dom4j.Document
 *
 */
public class XMLConfig{
	
	private static final Logger log = Logger.getLogger(XMLConfig.class.getPackage().getName());
	
    private Document document;
	private File xmlConfigFile;
	private final String defaultDirectory = System.getProperty("user.home")+"/.pdfsam";
    
    public XMLConfig() throws Exception{
		xmlConfigFile = new File("config.xml");
    	document = XMLParser.parseXmlFile(xmlConfigFile);
    }

    public XMLConfig(String configPath) throws Exception{   
    	this(configPath, false);
    }
    
    /**
	 * @param configPath
	 * @param checkUserHome
	 *            if true tries to check if the file
	 *            ${user.home}/.pdfsam/config.xml exists and can be written, if
	 *            not tries to check if the file APPLICATIOH_PATH/config.xml can
	 *            be written, if not copies the APPLICATIOH_PATH/config.xml to
	 *            ${user.home}/.pdfsam/config.xml
	 * @throws Exception
	 */
    public XMLConfig(String configPath, boolean checkUserHome) throws Exception{
    	File configFile= new File(configPath, "config.xml");
    	if(checkUserHome){
    		File defaultConfigFile = new File(defaultDirectory, "config.xml");
    		if (!(defaultConfigFile.exists() && defaultConfigFile.canWrite())){
    			if(!configFile.exists()){
    				throw new ConfigurationException("Unable to find configuration file.");
    			}
    			if (!configFile.canWrite()){
    				File defaultDir = new File(defaultDirectory);
    				defaultDir.mkdirs();
    				log.info("Copying config.xml from "+configFile.getPath()+" to "+defaultConfigFile.getPath());
    				FileUtility.copyFile(configFile, defaultConfigFile);
    				configFile = defaultConfigFile;
    			}
    		}else{
    			configFile = defaultConfigFile;
    		}
    	}
    	xmlConfigFile = configFile;
        document = XMLParser.parseXmlFile(xmlConfigFile); 
    }
    
    /**
     * 
     * @return File name of the XML config file.
     */
    public File getXMLConfigFile(){
        	return xmlConfigFile;
    }
 
    /**
     * It gives back a tag value
     * 
     * @param xpath 
     * @return tag value
     */
    public String getXMLConfigValue(String xpath) throws Exception{
    	String retVal = "";
    	Node node = document.selectSingleNode(xpath);
    	if(node != null){
    		retVal = node.getText().trim();
    	}
    	return retVal;
    }
 
     /**
     * It gives back the language tag value
     * 
     * @return tag value
     */
    public List getXMLLanguagesList() throws Exception{        
			Vector langs = new Vector(10,5);
			List nodeList = document.selectNodes("/pdfsam/available_languages/language");
			for (int i = 0; nodeList != null && i < nodeList.size(); i++){ 
				langs.add(((Node) nodeList.get(i)).selectSingleNode("@value").getText());
			}
            return langs;
    }
	
    /**
     * Given a DOM object it sets the new tag value
     * 
     * @param xpath
     * @param value new value of the xml tag
     * @return true or false
     */
        public boolean setXMLConfigValue(String xpath, String value) throws Exception{
            boolean retvalue = false;
			Node node = document.selectSingleNode(xpath);
			if (node == null){
				node = document.selectSingleNode(xpath.substring(0, xpath.lastIndexOf("/")));
                if (node != null){
                     node = (Node) ((Element)node).addElement(xpath.substring(xpath.lastIndexOf("/")+1));
                }else{
                	log.warn("Unable to set "+value+" to "+xpath);
                }
			}
			node.setText(value);
			retvalue = true;			
			return retvalue;
        }

    /**
     * It saves any changes on the xml file
     * 
     * @throws Exception
     * @see org.pdfsam.guiclient.utils.xml.XMLParser#writeXmlFile(Document, File)
     */
        public void saveXMLfile() throws Exception{
            XMLParser.writeXmlFile(document, xmlConfigFile);
        }
       

}
