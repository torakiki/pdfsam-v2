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
package it.pdfsam.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * Object for the xml config file. It provides functions to get and set xml tags in the
 * config.xml file.
 * @author Andrea Vacondio
 * @see it.pdfsam.util.XMLParser
 * @see org.w3c.dom.Document
 *
 */
public class XMLConfig{
    private XMLParser XML_config_file;
    private Document DOM_document;

    
    public XMLConfig() throws Exception{
    	XML_config_file = new XMLParser("config.xml", false);
	    DOM_document = XML_config_file.getParsedXML_Document();
    }

    public XMLConfig(String config_path) throws Exception{        
        XML_config_file = new XMLParser(config_path+"/config.xml", false);
        DOM_document = XML_config_file.getParsedXML_Document();
    }
    
    /**
     * 
     * @return File name of the XML config file.
     */
    public String getXMLConfigFileName(){
        	return XML_config_file.getXMLFileName();
    }
 
    /**
     * It gives back a tag value
     * 
     * @param taglist , a string of this type: dati_anagrafici->azienda->referente->telefono
     * @return tag value
     */
    public String getXMLConfigValue(String taglist) throws Exception{
        String[] tags = taglist.split("->");
        Element CurrentElement;
        try{
            CurrentElement = DOM_document.getDocumentElement();
            for (int i=0; i<tags.length; i++){
	            CurrentElement = (Element)CurrentElement.getElementsByTagName(tags[i]).item(0);
	        }
	        return  CurrentElement.getFirstChild().getNodeValue().trim();
        }
        catch(Exception e){
            return "";
        }
    }
 
    /**
     * Given a DOM object it sets the new tag value
     * 
     * @param taglist , a string of this type: dati_anagrafici->azienda->referente->telefono
     * @param value new value of the xml tag
     * @return true or false
     */
        public boolean setXMLConfigValue(String taglist, String value) throws Exception{
            String[] tags = taglist.split("->");
            Element CurrentElement;
                CurrentElement = DOM_document.getDocumentElement();
                for (int i=0; i<tags.length; i++){
    	            CurrentElement = (Element)CurrentElement.getElementsByTagName(tags[i]).item(0);
    	        }
                /*ritrovo il valore*/
                Node fc = CurrentElement.getFirstChild();
                if (fc == null){
                    CurrentElement.appendChild(DOM_document.createTextNode(value));
                }
                else{
                    CurrentElement.getFirstChild().setNodeValue(value);
                }            
    	        return  true;
            }

    /**
     * It saves any changes on the xml file
     * 
     * @throws Exception
     * @see it.pdfsam.util.XMLParser#writeXmlFile(Document)
     */
        public void saveXMLfile() throws Exception{
            XML_config_file.writeXmlFile(DOM_document);
        }

}
