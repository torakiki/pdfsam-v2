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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
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
	
	public WorkThread(String[] myStringArray){
		this.myStringArray = myStringArray;
	}
					
	public void run() {
    	 try{
    		ConsoleServicesFacade serviceFacade = Configuration.getInstance().getConsoleServicesFacade();
			AbstractParsedCommand cmd = serviceFacade.parseAndValidate(myStringArray);
			if(cmd != null){
				serviceFacade.execute(cmd);
				SoundPlayer.getInstance().playSound();
			}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Command validation returned an empty value."));
			}
			log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Command executed."));
		}catch(Throwable t){    
			log.error("Command Line: "+commandToString(), t);
			SoundPlayer.getInstance().playErrorSound();
		}                                         
	}
	
	/**
	 * @return String representation of the input command
	 */
	private String commandToString() {
		String command = StringUtils.join(myStringArray, " ");
		if(command.length() > 1000){
			command = command.substring(0, 1000) +" ...";
		}
		return command;
	}
}
