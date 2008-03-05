/*
 * Created on 29-Feb-2008
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

import java.util.Vector;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.i18n.GettextResource;
/**
 * Utility for the Updates section
 * @author Andrea Vacodnio
 *
 */
public class UpdatesUtility {
	
	public static String NEVER_CHECK = "0";
	public static String CHECK_AT_STARTUP = "1";
	 /**
     * @return the items for the checkNewVersion combo
     */
    public static  Vector getCheckNewVersionItems(){
    	Vector items = new Vector(2,2);
    	items.add(new StringItem(NEVER_CHECK, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Never")));
    	items.add(new StringItem(CHECK_AT_STARTUP, GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"When pdfsam starts")));    	
		return items;
    }
}
