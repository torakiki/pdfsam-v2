/*
 * Created on 24-DEC-2008
 * Copyright (C) 2008 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.utils;

import java.io.InputStream;
import java.util.List;

import org.dom4j.io.SAXReader;


/**
 * Utility class for pdf documents
 * @author Andrea Vacondio
 *
 */
public class PdfUtility {

	/**
	 * @param bookmarks the stream to read the xml. Stream is not closed. 
	 * @return the max depth of the bookmarks tree. 0 if no bookmark.
	 */
	public static int getMaxBookmarksDepth(InputStream bookmarks) throws Exception{
		int retVal = 0;
		if(bookmarks!=null){
			SAXReader reader = new SAXReader();
			org.dom4j.Document document = reader.read(bookmarks);
			String xQuery = "/Bookmark/Title[@Action=\"GoTo\"]";
			List nodes = document.selectNodes(xQuery);
			while((nodes!=null && nodes.size()>0)){
				retVal++;
				xQuery += "/Title[@Action=\"GoTo\"]";
				nodes = document.selectNodes(xQuery);
			}
			
		}
		return retVal;
	}
}
