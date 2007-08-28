/*
 * Created on 19-feb-2005
 *
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
package it.pdfsam.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * Parser XML. Given and XML file, it's parsed and the DOM object created. If and error occur
 * an exception is thrown.
 * @author Andrea Vacondio 
 * @see it.pdfsam.utils.XMLConfig
 */
public class XMLParser {	

 /**
 * Parse the xml file
 * @return The DOM object
 */
    public static Document parseXmlFile(String full_path) throws Exception{
    	Document document = null;
    	try{
        	File inputFile = new File(full_path);
        	if (inputFile.isFile()){
    			SAXReader reader = new SAXReader();
    			document = reader.read(inputFile);        		
        	}else{
        		throw new Exception("Unable to read "+full_path+".");
        	}
		}catch(Exception e){
			throw new Exception("Exception creating Document.", e);
		}  
		return document;
    }
    
    /**
     * Parse the url
     * @return The DOM object
     */
        public static Document parseXmlFile(URL url) throws Exception{
        	Document document = null;
            try{
    			SAXReader reader = new SAXReader();
    			document = reader.read(url);
    		}catch(Exception e){
    			throw new Exception("Exception reading "+url+":"+e.getMessage(), e);
    		}  
    		return document;
        }

    /**
     * Write the DOM to the xml file
     * @param domDoc Document to write
     * @param full_path Full path to the xml file to write
     * @throws Exception
     */    
    public static void writeXmlFile(Document domDoc, String full_path) throws Exception{           
    	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(full_path));
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");    
		XMLWriter xml_file_writer = new XMLWriter(bos, format);
		xml_file_writer.write(domDoc);
		xml_file_writer.flush();
		xml_file_writer.close();
	}
}    
