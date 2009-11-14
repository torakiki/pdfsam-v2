/*
 * Created on 26-Set-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.configuration.services.xml.strategy;

import org.dom4j.Document;

/**
 * Abstract xml config strategy
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractXmlConfigStrategy implements XmlConfigStrategy {

	private Document document;

	/**
	 * @param document
	 */
	public AbstractXmlConfigStrategy(Document document) {
		super();
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}

	public void close() {
		document = null;		
	}

}
