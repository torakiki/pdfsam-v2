/*
 * Created on 02-Nov-2007
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
package org.pdfsam.console;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;

/**
 * Shell client for the console
 * @author Andrea Vacondio
 *
 */
public class ConsoleClient {

	private static final Logger log = Logger.getLogger(ConsoleClient.class.getPackage().getName());
	private static ConsoleServicesFacade serviceFacade;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			serviceFacade = new ConsoleServicesFacade();
			if (serviceFacade != null){
				AbstractParsedCommand parsedCommand = serviceFacade.parseAndValidate(args);
				if(parsedCommand != null){
					serviceFacade.execute(parsedCommand);
				}
			}else{
				log.fatal("Unable to reach services, service facade is null.");
			}
		}catch(Exception e){
			log.fatal("Error executing ConsoleClient", e);
		}
	}

}
