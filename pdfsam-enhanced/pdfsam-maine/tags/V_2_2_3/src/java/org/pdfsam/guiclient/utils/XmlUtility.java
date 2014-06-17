/*
 * Created on 22-Jul-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.i18n.GettextResource;

/**
 * Utility class to deal with xml
 * 
 * @author Andrea Vacondio
 * 
 */
public final class XmlUtility {

    private static final Logger LOG = Logger.getLogger(XmlUtility.class.getPackage().getName());

    private XmlUtility() {
        // no constructor
    }

    /**
     * @param pageNode
     * @return given a page dom4j node it returns a DocumentPage object
     */
    public static DocumentPage getDocumentPage(Node pageNode) {
        DocumentPage retVal = null;
        try {
            if (pageNode != null) {
                retVal = new DocumentPage();

                Node deletedNode = (Node) pageNode.selectSingleNode("@deleted");
                if (deletedNode != null && deletedNode.getText().length() > 0) {
                    retVal.setDeleted(Boolean.valueOf(deletedNode.getText()));
                }

                Node numberNode = (Node) pageNode.selectSingleNode("@number");
                if (numberNode != null && numberNode.getText().length() > 0) {
                    retVal.setPageNumber(Integer.valueOf(numberNode.getText()));
                }

                Node rotationNode = (Node) pageNode.selectSingleNode("@rotation");
                if (rotationNode != null && rotationNode.getText().length() > 0) {
                    retVal.setRotation(Rotation.getRotation(Integer.valueOf(rotationNode.getText())));
                }

            }
        } catch (Exception e) {
            LOG.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Error retrieving page saved informations"), e);
        }
        return retVal;
    }

    /**
     * Write the DOM to the xml file
     * 
     * @param domDoc
     *            Document to write
     * @param outFile
     *            xml File to write
     * @throws IOException
     */
    public static void writeXmlFile(Document domDoc, File outFile) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlFileWriter = new XMLWriter(bos, format);
        xmlFileWriter.write(domDoc);
        xmlFileWriter.flush();
        xmlFileWriter.close();
    }

    /**
     * @param document
     * @param xpath
     * @return The xml tag values for the given xpath
     * @throws Exception
     */
    public static String getXmlValue(Document document, String xpath) {
        String retVal = "";
        Node node = document.selectSingleNode(xpath);
        if (node != null) {
            retVal = node.getText().trim();
        }
        return retVal;
    }

    /**
     * @param document
     * @param xpath
     * @return a list of values for the given xpath
     * @throws Exception
     */
    public static Set<String> getXmlValues(Document document, String xpath) {
        Set<String> retVal = new LinkedHashSet<String>();
        for (Node node : (List<Node>) document.selectNodes(xpath)) {
            if (StringUtils.isNotBlank(node.getText())) {
                retVal.add(node.getText().trim());
            }
        }
        return retVal;
    }

    /**
     * Parse the xml file converting the given path
     * 
     * @param fullPath
     * @return parsed Document
     * @throws DocumentException
     */
    public static Document parseXmlFile(String fullPath) throws DocumentException {
        return parseXmlFile(new File(fullPath));
    }

    /**
     * parse the xml input file
     * 
     * @param inputFile
     * @return parsed Document
     * @throws DocumentException
     */
    public static Document parseXmlFile(File inputFile) throws DocumentException {
        Document document = null;
        if (inputFile.isFile()) {
            SAXReader reader = new SAXReader();
            document = reader.read(inputFile);
        } else {
            throw new DocumentException("Unable to read " + inputFile + ".");
        }
        return document;
    }

    /**
     * Parse the url
     * 
     * @return The DOM object
     */
    public static Document parseXmlFile(URL url) throws DocumentException {
        Document document = null;
        SAXReader reader = new SAXReader();
        document = reader.read(url);
        return document;
    }

    /**
     * Adds the to rootElement the given xpath and, if the xpath contains an attribute, sets the attribute value.
     * 
     * @param rootElement
     * @param xpath
     * @param attributeValue
     * @return the added element
     */
    public static Element processXPath(Element rootElement, String xpath, String attributeValue) {
        String[] values = xpath.split("@");
        if (values.length == 2) {
            return addXmlNodeAndAttribute(rootElement, values[0], values[1], attributeValue);
        }
        return addXmlNodeAndAttribute(rootElement, values[0], null, null);
    }

    /**
     * Adds to the rootElement the nodes specified by nodeXPath. If not null it adds the attibuteName with the give Attribute Value
     * 
     * @param rootElement
     * @param nodeXPath
     * @param attributeName
     * @param AttributeValue
     * @return the added element
     */
    public static Element addXmlNodeAndAttribute(Element rootElement, String nodeXPath, String attributeName,
            String attributeValue) {
        String[] nodes = nodeXPath.split("/");
        Element currentElement = rootElement;

        for (String node : nodes) {
            if (StringUtils.isNotBlank(node)) {
                Element tmpElement = (Element) currentElement.selectSingleNode(node);
                if (tmpElement != null) {
                    currentElement = tmpElement;
                } else {
                    currentElement = currentElement.addElement(node);
                }
            }
        }

        if (attributeName != null && attributeValue != null) {
            currentElement.addAttribute(attributeName, attributeValue);
        }
        return currentElement;
    }
    
    /**
     * Adds multiple nodes to the parent element, one for each attributeValues and each of them with an attribute with the given name and value.
     * @param parentElement
     * @param nodeName
     * @param attributeName
     * @param attributeValues
     */
    public static void addXmlNodesAndAttribute(Element parentElement, String nodeName, String attributeName,
            Collection<String> attributeValues) {

        for (String attributeValue : attributeValues) {
            if (StringUtils.isNotBlank(attributeValue)) {
                Element currentElement = parentElement.addElement(nodeName);
                currentElement.addAttribute(attributeName, attributeValue);
            }
        }
    }
}
