/*
 * Created on 08-Nov-2007
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
package org.pdfsam.guiclient;

import java.io.InputStream;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.gui.frames.JMainFrame;
/**
 * GUI Client for the console
 * @author a.vacondio
 *
 */
public class GuiClient extends JFrame {

	private static final long serialVersionUID = -3608998690519362986L;

	private static final Logger log = Logger.getLogger(GuiClient.class.getPackage().getName());	
	private static final String PROPERTY_FILE = "pdfsam.properties";
	
    public static final String AUTHOR = "Andrea Vacondio";
	public static final String UNIXNAME = "pdfsam";

	private static final String NAME = "PDF Split and Merge";
	private static final String VERSION_TYPE_PROPERTY = "pdfsam.version";
	private static final String VERSION_TYPE_DEFAULT = "basic";
	
	private static final String VERSION_PROPERTY = "pdfsam.jar.version";
	private static final String VERSION_DEFAULT = "1.0.0-b3";	
	
	private static final String BUILDDATE_PROPERTY = "pdfsam.builddate";
	private static final String BUILDDATE_DEFAULT = "";	

	private static JMainFrame clientGUI;
	private static Properties defaultProps = new Properties();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			loadApplicationProperties();
			clientGUI = new JMainFrame();
			clientGUI.setVisible(true);
		} catch (Throwable t) {
			log.fatal("Error:", t);
		}
	}

	/**
	 * load application properties
	 */
	private static void loadApplicationProperties(){
		try{
			InputStream is = GuiClient.class.getClassLoader().getResourceAsStream(PROPERTY_FILE);
			defaultProps.load(is);
		}catch(Exception e){
			log.error("Unable to load pdfsam properties.", e);
		}
	}
	
	/**
	 * @return application version
	 */
	public static String getVersion(){
		return defaultProps.getProperty(VERSION_PROPERTY, VERSION_DEFAULT);
	}
	
	/**
	 * @return application name
	 */
	public static String getApplicationName(){
		return NAME+" "+defaultProps.getProperty(VERSION_TYPE_PROPERTY, VERSION_TYPE_DEFAULT);
	}
	
	/**
	 * @return application version type (basic or enhanced)
	 */
	public static String getVersionType(){
		return defaultProps.getProperty(VERSION_TYPE_PROPERTY, VERSION_TYPE_DEFAULT);
	}
	
	/**
	 * @return application build date
	 */
	public static String getBuildDate(){
		return defaultProps.getProperty(BUILDDATE_PROPERTY, BUILDDATE_DEFAULT);
	}
}
