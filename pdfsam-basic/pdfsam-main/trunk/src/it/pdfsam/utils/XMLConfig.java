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
package it.pdfsam.utils;

import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;


/**
 * Object for the xml config file. It provides functions to get and set xml tags in the
 * config.xml file.
 * @author Andrea Vacondio
 * @see it.pdfsam.utils.XMLParser
 * @see org.dom4j.Document
 *
 */
public class XMLConfig{
    private Document DOM_document;
	private String XML_config_file = "";
    
    public XMLConfig() throws Exception{
		XML_config_file = "config.xml";
    	DOM_document = XMLParser.parseXmlFile(XML_config_file);
    }

    public XMLConfig(String config_path) throws Exception{   
		if(config_path.endsWith("/") || config_path.endsWith("\\")){
			XML_config_file = config_path+"config.xml";
		}else{
			XML_config_file = config_path+"/config.xml";
    	}	
        DOM_document = XMLParser.parseXmlFile(XML_config_file); 
    }
    
    /**
     * 
     * @return File name of the XML config file.
     */
    public String getXMLConfigFileName(){
        	return XML_config_file;
    }
 
    /**
     * It gives back a tag value
     * 
     * @param xpath 
     * @return tag value
     */
    public String getXMLConfigValue(String xpath) throws Exception{        
            return DOM_document.selectSingleNode(xpath).getText().trim();
    }
 
     /**
     * It gives back the language tag value
     * 
     * @return tag value
     */
    public List getXMLLanguagesList() throws Exception{        
			Vector langs = new Vector(10,5);
			List nodeList = DOM_document.selectNodes("/pdfsam/available_languages/language");
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
			Node node = DOM_document.selectSingleNode(xpath);
			if (node == null){
				node = DOM_document.selectSingleNode(xpath.substring(0, xpath.lastIndexOf("//")-1));
                if (node != null){
                     ((Element)node).addElement(xpath.substring(xpath.lastIndexOf("//")));
                     retvalue = true;
                }
			}else{
				node.setText(value);
				retvalue = true;
			}
			return retvalue;
        }

    /**
     * It saves any changes on the xml file
     * 
     * @throws Exception
     * @see it.pdfsam.utils.XMLParser#writeXmlFile(Document, String)
     */
        public void saveXMLfile() throws Exception{
            XMLParser.writeXmlFile(DOM_document, XML_config_file);
        }

}
