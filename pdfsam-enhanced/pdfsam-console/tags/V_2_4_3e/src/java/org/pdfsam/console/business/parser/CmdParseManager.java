/*
 * Created on 21-Sep-2007
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.parser;

import jcmdline.CmdLineHandler;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.dto.commands.DecryptParsedCommand;
import org.pdfsam.console.business.dto.commands.DocumentInfoParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.dto.commands.MixParsedCommand;
import org.pdfsam.console.business.dto.commands.PageLabelsParsedCommand;
import org.pdfsam.console.business.dto.commands.RotateParsedCommand;
import org.pdfsam.console.business.dto.commands.SetViewerParsedCommand;
import org.pdfsam.console.business.dto.commands.SlideShowParsedCommand;
import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.dto.commands.UnpackParsedCommand;
import org.pdfsam.console.business.parser.handlers.ConcatCmdHandler;
import org.pdfsam.console.business.parser.handlers.DecryptCmdHandler;
import org.pdfsam.console.business.parser.handlers.DefaultCmdHandler;
import org.pdfsam.console.business.parser.handlers.DocumentInfoCmdHandler;
import org.pdfsam.console.business.parser.handlers.EncryptCmdHandler;
import org.pdfsam.console.business.parser.handlers.MixCmdHandler;
import org.pdfsam.console.business.parser.handlers.PageLabelsCmdHandler;
import org.pdfsam.console.business.parser.handlers.RotateCmdHandler;
import org.pdfsam.console.business.parser.handlers.SetViewerCmdHandler;
import org.pdfsam.console.business.parser.handlers.SlideShowCmdHandler;
import org.pdfsam.console.business.parser.handlers.SplitCmdHandler;
import org.pdfsam.console.business.parser.handlers.UnpackCmdHandler;
import org.pdfsam.console.business.parser.handlers.interfaces.CmdHandler;
import org.pdfsam.console.business.parser.validators.ConcatCmdValidator;
import org.pdfsam.console.business.parser.validators.DecryptCmdValidator;
import org.pdfsam.console.business.parser.validators.DocumentInfoCmdValidator;
import org.pdfsam.console.business.parser.validators.EncryptCmdValidator;
import org.pdfsam.console.business.parser.validators.MixCmdValidator;
import org.pdfsam.console.business.parser.validators.PageLabelsCmdValidator;
import org.pdfsam.console.business.parser.validators.RotateCmdValidator;
import org.pdfsam.console.business.parser.validators.SetViewerCmdValidator;
import org.pdfsam.console.business.parser.validators.SlideShowCmdValidator;
import org.pdfsam.console.business.parser.validators.SplitCmdValidator;
import org.pdfsam.console.business.parser.validators.UnpackCmdValidator;
import org.pdfsam.console.business.parser.validators.interfaces.CmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;

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
	private final StopWatch stopWatch = new StopWatch();

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
		else if(EncryptParsedCommand.COMMAND_ENCRYPT.equals(inputCommand)){
			cmdHandler = new EncryptCmdHandler();
			cmdValidator = new EncryptCmdValidator();
		}
		else if(UnpackParsedCommand.COMMAND_UNPACK.equals(inputCommand)){
			cmdHandler = new UnpackCmdHandler();
			cmdValidator = new UnpackCmdValidator();
		}
		else if(SetViewerParsedCommand.COMMAND_SETVIEWER.equals(inputCommand)){
			cmdHandler = new SetViewerCmdHandler();
			cmdValidator = new SetViewerCmdValidator();
		}
		else if(SlideShowParsedCommand.COMMAND_SLIDESHOW.equals(inputCommand)){
			cmdHandler = new SlideShowCmdHandler();
			cmdValidator = new SlideShowCmdValidator();
		}
		else if(DecryptParsedCommand.COMMAND_DECRYPT.equals(inputCommand)){
			cmdHandler = new DecryptCmdHandler();
			cmdValidator = new DecryptCmdValidator();
		}
		else if(RotateParsedCommand.COMMAND_ROTATE.equals(inputCommand)){
			cmdHandler = new RotateCmdHandler();
			cmdValidator = new RotateCmdValidator();
		}
		else if(PageLabelsParsedCommand.COMMAND_PAGELABELS.equals(inputCommand)){
			cmdHandler = new PageLabelsCmdHandler();
			cmdValidator = new PageLabelsCmdValidator();
		}
		else if(DocumentInfoParsedCommand.COMMAND_SETDOCINFO.equals(inputCommand)){
			cmdHandler = new DocumentInfoCmdHandler();
			cmdValidator = new DocumentInfoCmdValidator();
		}
		else{
			cmdHandler = new DefaultCmdHandler();
		}
	}

	/**
	 * Perform command line parsing
	 * @return true if parsed correctly
	 * @throws ConsoleException
	 */
	public boolean parse() throws ConsoleException {
		stopWatch.reset();
		stopWatch.start();
		boolean retVal = false;
		try {
			if (cmdHandler != null) {
				CmdLineHandler cmdLineHandler = cmdHandler.getCommandLineHandler();
				log.debug("Starting arguments parsing.");
				if (cmdLineHandler != null) {
					retVal = cmdLineHandler.parse(inputArguments);
					if (!retVal) {
						throw new ParseException(ParseException.ERR_PARSE, new String[] { cmdLineHandler.getParseError() });
					}
				} else {
					throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
				}
			} else {
				throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
			}
		} finally {
			stopWatch.stop();
			log.debug("Command '" + getInputCommand() + "' parsed in "
					+ DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true));
		}
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
	public AbstractParsedCommand validate() throws ConsoleException {
		stopWatch.reset();
		stopWatch.start();
		AbstractParsedCommand retVal = null;
		try {
			if (cmdHandler != null) {
				CmdLineHandler cmdLineHandler = cmdHandler.getCommandLineHandler();
				log.debug("Starting arguments validation.");
				if (cmdLineHandler != null) {
					if (cmdValidator != null) {
						retVal = cmdValidator.validate(cmdLineHandler);
					} else {
						throw new ConsoleException(ConsoleException.CMD_LINE_VALIDATOR_NULL);
					}
				} else {
					throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
				}
			} else {
				throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
			}
		} finally {
			stopWatch.stop();
			log.debug("Command '" + getInputCommand() + "' validated in "
					+ DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true));
		}
		return retVal;
	}
}
