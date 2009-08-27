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
package org.pdfsam.guiclient.utils.xml;

import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.i18n.GettextResource;
/**
 * Utility class to deal with xml
 * @author Andrea Vacondio
 *
 */
public class XmlUtility {

	private static final Logger log = Logger.getLogger(XmlUtility.class.getPackage().getName());

	/**
	 * @param pageNode
	 * @return given a page dom4j node it returns a DocumentPage object
	 */
	public static DocumentPage getDocumentPage(Node pageNode){
		DocumentPage retVal = null;
		try{
			if(pageNode!=null){
				retVal = new DocumentPage();
				
				Node deletedNode = (Node) pageNode.selectSingleNode("@deleted");
				if (deletedNode != null && deletedNode.getText().length()>0){
					retVal.setDeleted(Boolean.valueOf(deletedNode.getText()));
				}
				
				Node numberNode = (Node) pageNode.selectSingleNode("@number");
				if (numberNode != null && numberNode.getText().length()>0){
					retVal.setPageNumber(Integer.valueOf(numberNode.getText()));
				}
				
				Node rotationNode = (Node) pageNode.selectSingleNode("@rotation");
				if (rotationNode != null && rotationNode.getText().length()>0){
					retVal.setRotation(Rotation.getRotation(Integer.valueOf(rotationNode.getText())));
				}
				
			}
		}catch(Exception e){
			log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error retrieving page saved informations"), e);
		}
		return retVal;
	}
}
