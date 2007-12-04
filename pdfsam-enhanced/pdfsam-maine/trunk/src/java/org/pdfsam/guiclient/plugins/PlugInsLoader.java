/*
 * Created on 12-Nov-2007
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
package org.pdfsam.guiclient.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.PluginException;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.plugins.models.PluginDataModel;
import org.pdfsam.guiclient.utils.filters.JarFilter;
import org.pdfsam.guiclient.utils.xml.XMLConfig;
import org.pdfsam.i18n.GettextResource;


/**
 * Loader for plugins. This tries to get the plugins directory if no pluginsDirectory is given.
 * @author Andrea Vacondio
 */
public class  PlugInsLoader{
    
	private static final Logger log = Logger.getLogger(PlugInsLoader.class.getPackage().getName());
	
    private File pluginsDirectory;
    private File[] pluginsList;
    private Configuration config;
    
    private static final Class PLUGIN_SUPER_CLASS = org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel.class;
    /**
     * Constructor
     * @param pluginsDirectory Plug ins absolute path. If it's null or empty it tries to find the plugins dir.
     * @throws PluginLoadException 
     */
    public PlugInsLoader(String pluginsDirectory) throws PluginException {
    	config = Configuration.getInstance();
        if (pluginsDirectory != null && pluginsDirectory.length() >0){
        	this.pluginsDirectory = new File(pluginsDirectory);
                
        }
        else {
        	try{
                File plugsPath = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8"));
                String dir = (plugsPath.getParent().endsWith("/") || plugsPath.getParent().endsWith("\\"))? plugsPath.getParent()+"plugins" :plugsPath.getParent()+"/plugins";
                this.pluginsDirectory = new File(dir);
            }catch (Exception e){
                throw new PluginException(GettextResource.gettext(config.getI18nResourceBundle(),"Error getting plugins directory."), e);
            }
        }
        pluginsList = getPlugInsList();
    }

    /**
     * Get the list of directories under the plugins directory
     * @return List of plugin directories
     * @throws PluginLoadException
     */
    private File[] getPlugInsList()throws PluginException {
    	ArrayList retVal = new ArrayList();
    	if(pluginsDirectory != null){
    		if(pluginsDirectory.isDirectory() && pluginsDirectory.canRead()){
    			try{
    	        	File[] pluginsSubDirs = pluginsDirectory.listFiles();
    		        for (int i = 0 ; i < pluginsSubDirs.length ; i++ ) {
    		            if (pluginsSubDirs[i].isDirectory()){
    		            	retVal.add(pluginsSubDirs[i]);
    		            }
    		        }
    	        }catch (Exception e){
    	            throw new PluginException("Error getting plugins list",e);
    	        }
    		}else{
        		throw new PluginException(GettextResource.gettext(config.getI18nResourceBundle(),"Cannot read plugins directory ")+pluginsDirectory.getAbsolutePath());
        	}
    	}else{
    		throw new PluginException(GettextResource.gettext(config.getI18nResourceBundle(),"Plugins directory is null."));
    	}       
        return (File[])retVal.toArray(new File[retVal.size()]);
    }
    
    /**
     * load the plugins and return a map(k,value) where k is the pluginDataModel and value is the instance
     * @return
     * @throws PluginException
     */
    public Hashtable loadPlugins() throws PluginException {
    	Hashtable retMap = new Hashtable();
    	URLClassLoader urlClassLoader = null;
    	ArrayList urlList = new ArrayList();
    	ArrayList classList = new ArrayList();
    	
    	//crates a list of URL and classes
    	for(int i=0; i<pluginsList.length; i++){
    		File currentDir = pluginsList[i];
    		if(currentDir != null && currentDir.isDirectory()){
    			try{
    				XMLConfig xmlConfigObject = new XMLConfig(currentDir.getAbsolutePath()); 
    				File[] fileList = currentDir.listFiles(new JarFilter(false));
    				if(fileList.length == 1){
    					urlList.add(fileList[0].toURL());
    					classList.add(xmlConfigObject.getXMLConfigValue("/plugin/data/classname"));
    				}else{
    					log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Found zero or many jars in plugin directory ")+currentDir.getAbsolutePath());
    				}
    			}catch (Exception e){
    				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Exception loading plugins."), e);
    	        }
    		}else{
    			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Cannot read plugin directory ")+currentDir);
    		}
    	}
    	
    	urlClassLoader = new URLClassLoader((URL[])urlList.toArray(new URL[urlList.size()]));
    	
    	for (Iterator classesIterator=classList.iterator(); classesIterator.hasNext();) {
    		String className = "";
    		try{
    			className = (String) classesIterator.next();
    			Class currentClass = urlClassLoader.loadClass(className);
    			if((currentClass.getSuperclass().isAssignableFrom(PLUGIN_SUPER_CLASS))){
    				AbstractPlugablePanel instance = (AbstractPlugablePanel) currentClass.newInstance();
    				PluginDataModel pluginDataModel = new PluginDataModel(instance.getPluginName(), instance.getVersion(), instance.getPluginAuthor());
    				retMap.put(pluginDataModel, instance);
    				log.info(pluginDataModel.getName()+GettextResource.gettext(config.getI18nResourceBundle()," plugin loaded."));
    			}else{
    				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Unable to load a plugin that is not JPanel subclass."));
    			}
    		}catch(Exception e){
    			log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error loading class ")+className, e);
    		}
    	}
    	return retMap;
    }
    
}

