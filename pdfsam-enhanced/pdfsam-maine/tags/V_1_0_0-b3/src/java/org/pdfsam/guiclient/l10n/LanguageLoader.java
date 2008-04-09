/*
 * Created on 29-Jun-2006
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
package org.pdfsam.guiclient.l10n;

import java.util.Locale;
import java.util.ResourceBundle;
/**
 * Loader for the i18n resource bundle
 * @author Andrea Vacondio
 */
public class LanguageLoader {

    //const
    public static final String DEFAULT_LANGUAGE = "en_EN";
    public final Locale DEFAULT_LOCALE = Locale.UK;
    //vars
    private String bundleName;
    private Locale currentLocale; 
    
    /**
     * Creates a loader for the ResourceBundle
     * 
     * @param languageCode language code "en_GB", "it_IT", "pt_BR"...
     * @param bundle string rapresenting the bundle name
     */
    public LanguageLoader(String languageCode, String bundle){
        bundleName = bundle;
        String[] i18nInfos = languageCode.split("_");
        try{
        	if(i18nInfos.length>1){
        		currentLocale = new Locale (i18nInfos[0].toLowerCase(), i18nInfos[1].toUpperCase());
        	}else{
        		currentLocale = new Locale (i18nInfos[0].toLowerCase());
        	}
        }catch(Exception ex){
            currentLocale = DEFAULT_LOCALE;
        }
    }
    
    /**
     * @return the ResourceBundle associated with the bundleName and current_local. If
     * an exception is rised it tries to return bundleName but default_locale 
     */
    public ResourceBundle getBundle(){
        try{            
            return ResourceBundle.getBundle(bundleName, currentLocale);
        }catch(Exception exc){
            return ResourceBundle.getBundle(bundleName, DEFAULT_LOCALE);
        }
    }

    /**
     * @return the ResourceBundle associated with the bundleName and current_local. If
     * an exception is rised it tries to return bundleName but default_locale 
     */
    public ResourceBundle getBundle(ClassLoader cl){
        try{
            return ResourceBundle.getBundle(bundleName, currentLocale, cl);
        }catch(Exception exc){
            return ResourceBundle.getBundle(bundleName, DEFAULT_LOCALE, cl);
        }
    }

    /**
     * @param bundleName The bundleName to set.
     */
    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * @param currentLocale The locale to set.
     */
    public void setLocale(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }
}
