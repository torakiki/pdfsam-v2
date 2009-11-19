/*
 * Created on 21-oct-2007
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
package org.pdfsam.console.business.pdf;

import java.util.Observable;
import java.util.Observer;
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
import org.pdfsam.console.business.pdf.handlers.AlternateMixCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.ConcatCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.DecryptCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.DocumentInfoCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.EncryptCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.PageLabelsCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.RotateCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.SetViewerCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.SlideShowCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.SplitCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.UnpackCmdExecutor;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
/**
 * Manager for the commands execution 
 * @author Andrea Vacondio
 *
 */
public class CmdExecuteManager extends Observable implements Observer{

	private final Logger log = Logger.getLogger(CmdExecuteManager.class.getPackage().getName());
	
	private AbstractCmdExecutor cmdExecutor = null;
	private final StopWatch stopWatch = new StopWatch();
	
	/**
	 * Executes the input parsed command
	 * @param parsedCommand
	 * @throws ConsoleException
	 */
	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		stopWatch.reset();
		stopWatch.start();
		try {
			if (parsedCommand != null) {
				cmdExecutor = getExecutor(parsedCommand);
				if (cmdExecutor != null) {
					cmdExecutor.addObserver(this);
					cmdExecutor.execute(parsedCommand);
				} else {
					throw new ConsoleException(ConsoleException.CMD_LINE_EXECUTOR_NULL, new String[] { "" + parsedCommand.getCommand() });
				}
			} else {
				throw new ConsoleException(ConsoleException.CMD_LINE_NULL);
			}
		} finally {
			stopWatch.stop();
			log.info("Command '" + parsedCommand.getCommand() + "' executed in "
					+ DurationFormatUtils.formatDurationWords(stopWatch.getTime(), true, true));
		}
	}	
	 /**
	  * forward the WorkDoneDataModel to the observers
	  */
	public void update(Observable arg0, Object arg1) {
		setChanged();
		notifyObservers(arg1);
	}

	/**
	 * @param parsedCommand
	 * @return an instance of the proper executor for the parsed command
	 */	
	private AbstractCmdExecutor getExecutor(AbstractParsedCommand parsedCommand){
		AbstractCmdExecutor retVal;
		if(MixParsedCommand.COMMAND_MIX.equals(parsedCommand.getCommand())){
			retVal = new AlternateMixCmdExecutor();
		}else if(SplitParsedCommand.COMMAND_SPLIT.equals(parsedCommand.getCommand())){
			retVal = new SplitCmdExecutor();
		}else if(EncryptParsedCommand.COMMAND_ENCRYPT.equals(parsedCommand.getCommand())){
			retVal = new EncryptCmdExecutor();
		}else if(ConcatParsedCommand.COMMAND_CONCAT.equals(parsedCommand.getCommand())){
			retVal = new ConcatCmdExecutor();
		}else if(UnpackParsedCommand.COMMAND_UNPACK.equals(parsedCommand.getCommand())){
			retVal = new UnpackCmdExecutor();
		}else if(SetViewerParsedCommand.COMMAND_SETVIEWER.equals(parsedCommand.getCommand())){
			retVal = new SetViewerCmdExecutor();
		}else if(SlideShowParsedCommand.COMMAND_SLIDESHOW.equals(parsedCommand.getCommand())){
			retVal = new SlideShowCmdExecutor();
		}else if(DecryptParsedCommand.COMMAND_DECRYPT.equals(parsedCommand.getCommand())){
			retVal = new DecryptCmdExecutor();
		}else if(RotateParsedCommand.COMMAND_ROTATE.equals(parsedCommand.getCommand())){
			retVal = new RotateCmdExecutor();
		}else if(PageLabelsParsedCommand.COMMAND_PAGELABELS.equals(parsedCommand.getCommand())){
			retVal = new PageLabelsCmdExecutor();
		}else if(DocumentInfoParsedCommand.COMMAND_SETDOCINFO.equals(parsedCommand.getCommand())){
			retVal = new DocumentInfoCmdExecutor();
		}else {
			retVal = null;
		}
		return retVal;
	}
}
