/*
 * Created on 25-Dic-2007
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
package org.pdfsam.guiclient.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.i18n.GettextResource;

/**
 * Pdf document version utilities
 * @author Andrea Vacondio
 *
 */
public class PdfVersionUtility {
	
	private static final Logger log = Logger.getLogger(PdfVersionUtility.class.getPackage().getName());
	private static HashMap cache = new HashMap(); 
	private static ArrayList listCache = new ArrayList(); 
	
	/**
	 * @param c pdfVersion
	 * @return String Version description
	 */
	public static String getVersionDescription(char c){
		String retVal = "";
		try{
			Object description = getVersions().get(Character.toString(c));
			retVal = (description != null)? (String) description : "";
		}catch(Exception e){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error getting pdf version description."), e);
		}		
		return retVal;
	}
	
	/**
	 * @return a map containing every possible pdf version
	 */
	public static HashMap getVersions(){
		if(cache.isEmpty()){
			Configuration config = Configuration.getInstance();
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_2), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.2 (Acrobat 3)"));
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_3), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.3 (Acrobat 4)"));
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_4), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.4 (Acrobat 5)"));
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_5), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.5 (Acrobat 6)"));
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_6), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.6 (Acrobat 7)"));
			cache.put(Character.toString(AbstractParsedCommand.VERSION_1_7), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.7 (Acrobat 8)"));
		}
		return cache;
	}
	
	/**
	 * @return a list containing every possible pdf version as a StrinItem
	 */
	public static ArrayList getVersionsList(){
		if(listCache.isEmpty()){
			Configuration config = Configuration.getInstance();
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_2), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.2 (Acrobat 3)")));
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_3), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.3 (Acrobat 4)")));
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_4), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.4 (Acrobat 5)")));
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_5), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.5 (Acrobat 6)")));
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_6), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.6 (Acrobat 7)")));
			listCache.add(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_7), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.7 (Acrobat 8)")));
		}
		return listCache;
		
	}
}
