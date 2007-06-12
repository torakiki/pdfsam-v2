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
package it.pdfsam.utils;

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
    private String bundle_name;
    private Locale current_locale; 
    
    /**
     * Creates a loader for the ResourceBundle
     * 
     * @param language_code language code "en_GB", "it_IT", "pt_BZ"...
     * @param bundle string rapresenting the bundle name
     */
    public LanguageLoader(String language_code, String bundle){
        bundle_name = bundle;
        String[] i18n_infos = language_code.split("_");
        try{
            current_locale = new Locale (i18n_infos[0].toLowerCase(), i18n_infos[1].toUpperCase());
        }catch(NullPointerException nupe){
            current_locale = DEFAULT_LOCALE;
        }
        catch(Exception ex){
            current_locale = DEFAULT_LOCALE;
        }
    }
    
    /**
     * @return the ResourceBundle associated with the bundle_name and current_local. If
     * an exception is rised it tries to return bundle_name but default_locale 
     */
    public ResourceBundle getBundle(){
        try{            
            return ResourceBundle.getBundle(bundle_name, current_locale);
        }catch(Exception exc){
            return ResourceBundle.getBundle(bundle_name, DEFAULT_LOCALE);
        }
    }

    /**
     * @return the ResourceBundle associated with the bundle_name and current_local. If
     * an exception is rised it tries to return bundle_name but default_locale 
     */
    public ResourceBundle getBundle(ClassLoader cl){
        try{
            return ResourceBundle.getBundle(bundle_name, current_locale, cl);
        }catch(Exception exc){
            return ResourceBundle.getBundle(bundle_name, DEFAULT_LOCALE, cl);
        }
    }

    /**
     * @param bundle_name The bundle_name to set.
     */
    public void setBundleName(String bundle_name) {
        this.bundle_name = bundle_name;
    }

    /**
     * @param current_locale The locale to set.
     */
    public void setLocale(Locale current_locale) {
        this.current_locale = current_locale;
    }
}
