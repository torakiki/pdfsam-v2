/*
 * Created on 21-Sep-2007
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
package org.pdfsam.console.business.parser;

import jcmdline.CmdLineHandler;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.parser.handlers.ConcatCmdHandler;
import org.pdfsam.console.business.parser.handlers.DefaultCmdHandler;
import org.pdfsam.console.business.parser.handlers.EncryptCmdHandler;
import org.pdfsam.console.business.parser.handlers.MixCmdHandler;
import org.pdfsam.console.business.parser.handlers.SplitCmdHandler;
import org.pdfsam.console.business.parser.handlers.interfaces.CmdHandler;
import org.pdfsam.console.business.parser.validators.ConcatCmdValidator;
import org.pdfsam.console.business.parser.validators.EncryptCmdValidator;
import org.pdfsam.console.business.parser.validators.MixCmdValidator;
import org.pdfsam.console.business.parser.validators.SplitCmdValidator;
import org.pdfsam.console.business.parser.validators.interfaces.CmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.TimeUtility;

/**
 * Main command parser
 * @author Andrea Vacondio
 *
 */
public class CmdParseManager {    	 
    
	private final Logger log = Logger.getLogger(CmdParseManager.class.getPackage().getName());
	
	private String[] inputArguments;
	private CmdHandler cmdHandler;
	private CmdValidator cmdValidator;

	public CmdParseManager() {
		setInputArguments(null);		
	}
	
	public CmdParseManager(String[] inputArguments) {
		setInputArguments(inputArguments);		
	}

	/**
	 * @return the input command
	 */
	private String getInputCommand() {
		String retVal;
		retVal = (inputArguments != null && inputArguments.length > 0)? inputArguments[inputArguments.length-1]: "";
		return retVal;
	}			
	
	/**
	 * Sets the input arguments creating the properCmdHandler and CmdValidator
	 * @param inputArguments
	 */
	public void setInputArguments(String[] inputArguments){
		this.inputArguments = inputArguments;
		String inputCommand = getInputCommand();
		if(MixParsedCommand.COMMAND_MIX.equals(inputCommand)){
			cmdHandler = new MixCmdHandler();
			cmdValidator = new MixCmdValidator();
		}
		else if(ConcatParsedCommand.COMMAND_CONCAT.equals(inputCommand)){
			cmdHandler = new ConcatCmdHandler();
			cmdValidator = new ConcatCmdValidator();
		}
		else if(SplitParsedCommand.COMMAND_SPLIT.equals(inputCommand)){
			cmdHandler = new SplitCmdHandler();
			cmdValidator = new SplitCmdValidator();
		}
		else if(EncryptParsedCommand.COMMAND_ECRYPT.equals(inputCommand)){
			cmdHandler = new EncryptCmdHandler();
			cmdValidator = new EncryptCmdValidator();
		}else{
			cmdHandler = new DefaultCmdHandler();
		}
	}

	/**
	 * Perform command line parsing
	 * @return true if parsed correctely
	 * @throws ConsoleException
	 */
	public boolean parse() throws ConsoleException{
		long start = System.currentTimeMillis();
		boolean retVal = false;
		if(cmdHandler != null){
			CmdLineHandler cmdLineHandler = cmdHandler.getCommandLineHandler();
			log.debug("Starting arguments parsing.");
			if(cmdLineHandler != null){
				retVal = cmdLineHandler.parse(inputArguments);
				if(!retVal){
					throw new ParseException(ParseException.ERR_PARSE, new String[]{cmdLineHandler.getParseError()});				
				}
			}else{
				throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
			}
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		log.debug("Command '"+getInputCommand()+"' parsed in "+TimeUtility.format(System.currentTimeMillis()-start));
		return retVal;
	}
	
	/**
	 * Perform command line parsing for the input arguments
	 * @return true if parsed correctly
	 * @throws ConsoleException
	 */
	public boolean parse(String[] inputArguments) throws ConsoleException{
		setInputArguments(inputArguments);
		return parse();		
	}
	/**
	 * Perform command validation
	 * @return parsed command
	 * @throws ConsoleException
	 */
	public AbstractParsedCommand validate() throws ConsoleException{
		long start = System.currentTimeMillis();
		AbstractParsedCommand retVal = null;
		if(cmdHandler != null){
			CmdLineHandler cmdLineHandler = cmdHandler.getCommandLineHandler();
			log.debug("Starting arguments validation.");
			if(cmdLineHandler != null){
				if(cmdValidator != null){
					retVal = cmdValidator.validate(cmdLineHandler);
				}else{
					throw new ConsoleException(ConsoleException.CMD_LINE_VALIDATOR_NULL);
				}
			}else{
				throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
			}
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		log.debug("Command '"+getInputCommand()+"' validated in "+TimeUtility.format(System.currentTimeMillis()-start));
		return retVal;
	}
}
