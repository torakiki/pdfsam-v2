/*
 * Created on 26-Feb-2008
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
package org.pdfsam.guiclient.updates.checkers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.CheckForUpdateException;
import org.pdfsam.i18n.GettextResource;
/**
 * Checks for update over a http connection
 * @author Andrea Vacondio
 *
 */
public class HttpUpdateChecker implements UpdateChecker {

	private URL httpUrl = null;
	
	/**
	 * @param httpUrl
	 */
	public HttpUpdateChecker(URL httpUrl) {
		this.httpUrl = httpUrl;
	}

	public String getLatestVersion() throws CheckForUpdateException {
		String retVal = null;
		String xmlContent = getXmlContent();
	    
	    if(xmlContent != null){
	    	try{
		    	SAXReader reader = new SAXReader();
				Document document = reader.read(new StringReader(new String(xmlContent)));
				Node node = document.selectSingleNode("/pdfsam/latestVersion/@value");
				if(node != null){
					retVal = node.getText().trim();
					retVal = (retVal.length()>0)? retVal: null;
				}
			}catch(Exception e){
				throw new CheckForUpdateException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to get latest available version"), e);
			}			
		}
	    return retVal;
	}

	/**
	 * @return the String xml content retrieved 
	 * @throws CheckForUpdateException
	 */
	private String getXmlContent() throws CheckForUpdateException {
		String retVal = "";
		HttpURLConnection urlConn = null;
		BufferedReader br = null;
		try{
			urlConn = (HttpURLConnection)httpUrl.openConnection();
			urlConn.setRequestProperty("user agent", "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1");
			
			br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			StringBuffer sb=new StringBuffer();
			String tmp=null;
			while((tmp=br.readLine())!=null){
			     sb.append(tmp);
			}
			br.close();
			retVal = sb.toString();
		}catch(Exception e){
			throw new CheckForUpdateException(e);
		}finally{
			if(urlConn != null){
				urlConn.disconnect();
			}
		}
		return retVal;
	}
}
