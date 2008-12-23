/*
 * Created on 24-Dec-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.business;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Runnable to execute the command
 * @author Andrea Vacondio
 *
 */
public class WorkThread implements Runnable{

	private static final Logger log = Logger.getLogger(WorkThread.class.getPackage().getName());
	
	private String[] myStringArray;
	private Configuration config;
	
	public WorkThread(String[] myStringArray){
		this.myStringArray = myStringArray;
		config = Configuration.getInstance();
	}
					
	public void run() {
    	 try{
			AbstractParsedCommand cmd = config.getConsoleServicesFacade().parseAndValidate(myStringArray);
			if(cmd != null){
				config.getConsoleServicesFacade().execute(cmd);	
				SoundPlayer.getInstance().playSound();
			}else{
				log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Command validation returned an empty value."));
			}
			log.info(GettextResource.gettext(config.getI18nResourceBundle(),"Command executed."));
		}catch(Throwable t){    
			log.error("Command Line: "+commandToString(), t);
			SoundPlayer.getInstance().playErrorSound();
		}                                         
	}
	
	/**
	 * @return String representation of the input command
	 */
	public String commandToString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		for(int i = 0; i<myStringArray.length; i++){
			buf.append(myStringArray[i]+" ");
		}
		buf.append("]");			
		return buf.toString();
	}
}
