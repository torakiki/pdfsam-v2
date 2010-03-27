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
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.exceptions.PluginException;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.guiclient.plugins.models.PluginDataModel;
import org.pdfsam.guiclient.utils.XmlUtility;
import org.pdfsam.guiclient.utils.filters.JarFilter;
import org.pdfsam.i18n.GettextResource;


/**
 * Loader for plugins. This tries to get the plugins directory if no pluginsDirectory is given.
 * @author Andrea Vacondio
 */
public class  PlugInsLoader{
    
	private static final Logger log = Logger.getLogger(PlugInsLoader.class.getPackage().getName());
	
    private File pluginsDirectory;
    private File[] pluginsList;
    
    private static final Class<?> PLUGIN_SUPER_CLASS = org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel.class;
    /**
     * Constructor
     * @param pluginsDirectory Plug ins absolute path. If it's null or empty it tries to find the plugins dir.
     * @throws PluginLoadException 
     */
    public PlugInsLoader(String pluginsDirectory) throws PluginException {
        if (pluginsDirectory != null && pluginsDirectory.length() >0){
        	this.pluginsDirectory = new File(pluginsDirectory);                
        }
        else {
        	try{
        		String configSearchPath = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8")).getParent();
                this.pluginsDirectory = new File(configSearchPath, "plugins");
            }catch (Exception e){
                throw new PluginException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error getting plugins directory."), e);
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
    	ArrayList<File> retVal = new ArrayList<File>();
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
        		throw new PluginException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Cannot read plugins directory ")+pluginsDirectory.getAbsolutePath());
        	}
    	}else{
    		throw new PluginException(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Plugins directory is null."));
    	}       
        return (File[])retVal.toArray(new File[retVal.size()]);
    }
    
	/**
	 * Print an error message if the input array is empty. Returns null if the
	 * input array is empty, files[0] if the input array contains one element,
	 * the file with the highest Last Modified Date if there are 2 or more
	 * files.
	 * 
	 * @param files
	 * @param parentDirectory
	 * @return
	 */
	private File getMostRecent(File[] files, String parentDirectory) {
		File retVal = null;
		if (files.length > 0) {
			if (files.length == 1) {
				retVal = files[0];
			} else {
				log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
						"Found many plugins in ")
						+ parentDirectory + ".");
				for (File currentFile : files) {
					if (retVal == null || currentFile.lastModified() > retVal.lastModified()) {
						retVal = currentFile;
					}
				}
				log.warn(retVal.getAbsolutePath()
						+ " "
						+ GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
								"selected as the most recent plugin") + ".");
			}

		} else {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"No plugin found in ")
					+ parentDirectory);
		}
		return retVal;
	}
    
    /**
     * load the plugins and return 
     * @return a map(k,value) where k is the pluginDataModel and value is the instance
     * @throws PluginException
     */
    public Map<PluginDataModel, AbstractPlugablePanel> loadPlugins() throws PluginException {
    	Map<PluginDataModel, AbstractPlugablePanel> retMap = new TreeMap<PluginDataModel, AbstractPlugablePanel>();
    	URLClassLoader urlClassLoader = null;
    	ArrayList<URL> urlList = new ArrayList<URL>();
    	ArrayList<String> classList = new ArrayList<String>();
    	
    	//crates a list of URL and classes
   		for(File currentDir : pluginsList){
    		if(currentDir != null && currentDir.isDirectory()){
    			try{
    				Document document = XmlUtility.parseXmlFile(new File(currentDir.getAbsolutePath(), "config.xml"));
    				File[] fileList = currentDir.listFiles(new JarFilter(false));
    				File selectedFile = getMostRecent(fileList, currentDir.getAbsolutePath());
    				if(selectedFile != null){    					
    					urlList.add(selectedFile.toURI().toURL());
    					classList.add(XmlUtility.getXmlValue(document, "/plugin/data/classname"));
    				}
    			}catch (Exception e){
    				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Exception loading plugins."), e);
    	        }
    		}else{
    			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Cannot read plugin directory ")+currentDir);
    		}
    	}
    	
    	urlClassLoader = new URLClassLoader((URL[])urlList.toArray(new URL[urlList.size()]));
    	
    	for(String className : classList){	
    		try{
    			Class<?> currentClass = urlClassLoader.loadClass(className);
    			if((currentClass.getSuperclass().isAssignableFrom(PLUGIN_SUPER_CLASS))){
    				AbstractPlugablePanel instance = (AbstractPlugablePanel) currentClass.newInstance();
    				PluginDataModel pluginDataModel = new PluginDataModel(instance.getPluginName(), instance.getVersion(), instance.getPluginAuthor(), className);
    				retMap.put(pluginDataModel, instance);
    				log.info(pluginDataModel.getName()+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle()," plugin loaded."));
    			}else{
    				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to load a plugin that is not JPanel subclass."));
    			}
    		}catch(Exception e){
    			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error loading class ")+className, e);
    		}
    	}
    	return retMap;
    }
    
}

