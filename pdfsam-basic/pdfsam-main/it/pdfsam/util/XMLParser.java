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
package it.pdfsam.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Parser XML. Given and XML file, it's parsed and the DOM object created. If and error occur
 * an exception is thrown.
 * @author Andrea Vacondio 
 * @see it.pdfsam.util.XMLConfig
 */
public class XMLParser {
	private String XML_file;
	private Document ParsedXML_Document;
	
	public XMLParser(String XMLfile, boolean do_validating) throws Exception{
		XML_file = XMLfile;
		ParsedXML_Document = parseXmlFile(do_validating);
    }

/**
 * 
 * @return the XML file name
 */	
	public String getXMLFileName(){
    	return this.XML_file;
	}

/**
 * 
 * @return the DOM object
 */	
	public Document getParsedXML_Document(){
    	return this.ParsedXML_Document;
	}

/**
 * Parse the xml file
 * 
 * @param validating If true it makes a validation against DTD
 * @return The DOM object
 */
    private Document parseXmlFile(boolean validating) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
           // Create the builder and parse the file
        Document parsed_document = factory.newDocumentBuilder().parse(new File(this.XML_file));  
        return parsed_document;
    }
/**
 * Write the DOM to the xml file
 * 
 * @param DOM_doc Document to write
 * @throws Exception
 */    
    public void writeXmlFile(Document DOM_doc) throws Exception{
            this.ParsedXML_Document = DOM_doc;
            // Prepare the DOM document for writing
            DOMSource source = new DOMSource(this.ParsedXML_Document);
    
            // Prepare the output file
            File file = new File(XML_file);
            StreamResult result = new StreamResult(file);
    
            // Write the DOM document to the file
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
        }
}    
