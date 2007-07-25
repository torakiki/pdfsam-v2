/*
 * Created on 4-feb-2006
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

import it.pdfsam.exceptions.PluginLoadException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Loader for plugins. This tryes to get the plugins directory if no plugs_dir is given.
 * The plugins directory list is loaded and than the MainGUI tryes to load plugins one by one.
 * @author Andrea Vacondio
 * @see it.pdfsam.exceptions.PluginLoadException
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see it.pdfsam.GUI.MainGUI
 */
public class  PlugInsLoader{
    
    private String pluginsdir;
    private ArrayList p_list;
    private int p_number;
    private String jar_name;
    
    static final Class PLUGIN_INTERFACE_CLASS = it.pdfsam.interfaces.PlugablePanel.class;
    /**
     * Constructor
     * @param plugs_dir Plug ins absolute path. If it's null or empty it tryies to find the plugins dir.
     * @throws PluginLoadException 
     */
    public PlugInsLoader(String plugs_dir) throws PluginLoadException {
        if (plugs_dir != null){
            if (plugs_dir.trim().equals("")){
                try{
                    File plugs_path = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),"UTF-8"));
                    pluginsdir = plugs_path.getParent()+"/plugins";
                }catch (NullPointerException np_exception){
                    throw new PluginLoadException("Error getting pdfsam.jar file path", np_exception);
                }catch (Exception n_exception){
                    throw new PluginLoadException("Error creating PlugInsLoader object", n_exception);
                }
            }
            else {
                pluginsdir = plugs_dir;
            }
        }
        
        p_list = getPlugInsList();
        p_number = p_list.size();
    }

    /**
     * Get the list of directories under the plugins directory
     * @return List of plugin directories
     * @throws PluginLoadException
     */
    private ArrayList getPlugInsList()throws PluginLoadException {
        File pi_dir;
        //System.out.print(pluginsdir+"\n");
        try{
            pi_dir = new File (pluginsdir);
        }catch (NullPointerException np_exception){
            throw new PluginLoadException("Error getting working directory: "+np_exception);
        }
        ArrayList retval = new ArrayList();
		//fix 22/01/07
        // Get all the files and directory under plugins diretcory
        try{
        	File[] strFilesDirs = pi_dir.listFiles();
	        for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
	            if ( strFilesDirs[i].isDirectory ( ) ){
	           //     System.out.print(strFilesDirs[i]+"\n");
	               retval.add(strFilesDirs[i]);
	            }
	        }
        }catch (Exception exception){
            throw new PluginLoadException("Error getting plugins list: "+exception.getMessage());
        }
        return retval;
    }
    
    /**
     * Load the plugin number <code>num</code>. Where the plugin list is made while creaing PlugInsLoader instance.
     * @param num Number of plugin to load
     * @throws PluginLoadException
     */
    public Object loadPlugin(int num)  throws PluginLoadException {
        int counter = 0;
        URLClassLoader urlcl = null;
        //plugin dir
        File currdir = ((File)p_list.get(num));
        XMLConfig xml_plugin_object;
        boolean found_interface = false;
        boolean found_superclass = false;
        //load config file
        try{
            //TODO find a good way
            xml_plugin_object = new XMLConfig(currdir.toString()+"/");            
        }catch (Exception xml_exception){
            throw new PluginLoadException("Error getting plugin file config.xml", xml_exception);
        }
        //scan dir
        // This is a directory. Attempt to find jar files in that directory.
        File[] str_files_dirs = currdir.listFiles(new JarFilter());

        for ( int i = 0 ; i < str_files_dirs.length ; i ++ ) {
            //not a dir
            if (!(str_files_dirs[i].isDirectory())){
                counter++;
                jar_name = str_files_dirs[i].getName();
            }
        }
        if (counter != 1){
            throw new PluginLoadException("Found zero or many jars in plugin directory "+currdir);
        }
       else{                            
            //create url to the jar file
            URL file_url;
            try{   
                file_url = new File(p_list.get(num)+"/"+jar_name).toURL();
            }catch (MalformedURLException url_exception){
                throw new PluginLoadException("Error creating URL to the jar file", url_exception);
            }
            //create classloader
            urlcl = new URLClassLoader(new URL[]{file_url});
            Class cls;
            try{
                //get plugin class
                cls = urlcl.loadClass(xml_plugin_object.getXMLConfigValue("/plugin/data/classname"));
            }
            catch (ClassNotFoundException cnf_exception){
                throw new PluginLoadException("Unable to load class, class not found");
            }
            catch (Exception e_exception){
                throw new PluginLoadException(e_exception.getMessage());
            }
            //get interfaces
            Class[] class_array = cls.getInterfaces();               
            //check interfaces
            for (int i = 0; i < class_array.length; i++){
                 if (class_array[i].equals(PLUGIN_INTERFACE_CLASS)){
                     found_interface = true;
                     break;
                 }
            }
            if (!found_interface){
                throw new PluginLoadException("Plugin "+PLUGIN_INTERFACE_CLASS+" not implemented");
            }            
            Class superclass = cls.getSuperclass();
            if (superclass.isAssignableFrom(javax.swing.JPanel.class)){
                found_superclass = true;
            }
            if (!found_superclass){
                throw new PluginLoadException("Unable to load a plugin that is not JPanel subclass");
            }            
            else{
                try{
                    return cls.newInstance();
                }catch (InstantiationException i_exception){
                    throw new PluginLoadException("Unable to create class instance", i_exception);
                }catch (IllegalAccessException ia_exception){
                    throw new PluginLoadException("Unable access class file", ia_exception);
                }
            }
            
        }
        
    }
    /**     * 
     * @return Returns the number of directories under the plugins directory.
     */
    public int getPNumber() {
        return p_number;
    }
    
}

