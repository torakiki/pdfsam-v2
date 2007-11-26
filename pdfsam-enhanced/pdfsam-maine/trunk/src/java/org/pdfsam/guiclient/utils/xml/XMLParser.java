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
package org.pdfsam.guiclient.utils.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


/**
 * Parser XML. Given and XML file, it's parsed and the DOM object created. If and error occur
 * an exception is thrown.
 * @author Andrea Vacondio 
 * @see org.pdfsam.guiclient.utils.xml.XMLConfig
 */
public class XMLParser {	

	/**
	 * Parse the xml file converting the given path
	 * @param fullPath
	 * @return
	 * @throws DocumentException
	 */
    public static Document parseXmlFile(String fullPath) throws DocumentException{
    	return parseXmlFile(new File(fullPath));
    }
    
    /**
     * parse the xml input file
     * @param inputFile
     * @return
     * @throws DocumentException
     */
    public static Document parseXmlFile(File inputFile) throws DocumentException{
    	Document document = null;
    	if (inputFile.isFile()){
			SAXReader reader = new SAXReader();
			document = reader.read(inputFile);        		
    	}else{
    		throw new DocumentException("Unable to read "+inputFile+".");
    	}		
		return document;
    }
    
    /**
     * Parse the url
     * @return The DOM object
     */
        public static Document parseXmlFile(URL url) throws DocumentException{
        	Document document = null;
   			SAXReader reader = new SAXReader();
   			document = reader.read(url);
    		return document;
        }

	/**
	 * Write the DOM to the xml file
	 * 
	 * @param domDoc Document to write
	 * @param fullPath Full path to the xml file to write
	 * @throws Exception
	 */    
	 public static void writeXmlFile(Document domDoc, File outFile) throws Exception{           
	   	BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");    
		XMLWriter xmlFileWriter = new XMLWriter(bos, format);
		xmlFileWriter.write(domDoc);
		xmlFileWriter.flush();
		xmlFileWriter.close();
	}
}    
