/*
 * Created on 18-Oct-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.console.business.parser.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.CmdHandler;
/**
 * Default handler 
 * @author Andrea Vacondio
 *
 */
public class DefaultCmdHandler implements CmdHandler {

	private VersionCmdLineHandler commandLineHandler = null;
	
	private static final String commandDescription = "merge, split, mix, setviewer, unpack, encrypt, slideshow, decrypt, rotate, pagelabels.";
	
	 /**
     * The default arguments 
     */
	private final List concatArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+AbstractParsedCommand.COMMAND_CONCAT+"], ["+AbstractParsedCommand.COMMAND_SPLIT+"], ["+AbstractParsedCommand.COMMAND_ENCRYPT+"], ["+AbstractParsedCommand.COMMAND_MIX+"], ["+AbstractParsedCommand.COMMAND_UNPACK+"], ["+AbstractParsedCommand.COMMAND_SETVIEWER+"], ["+AbstractParsedCommand.COMMAND_SLIDESHOW+"], ["+AbstractParsedCommand.COMMAND_DECRYPT+"], ["+AbstractParsedCommand.COMMAND_ROTATE+"], ["+AbstractParsedCommand.COMMAND_PAGELABELS+"]}",
                    new String[] { AbstractParsedCommand.COMMAND_CONCAT, AbstractParsedCommand.COMMAND_SPLIT, AbstractParsedCommand.COMMAND_ENCRYPT, AbstractParsedCommand.COMMAND_MIX, AbstractParsedCommand.COMMAND_UNPACK, AbstractParsedCommand.COMMAND_SETVIEWER , AbstractParsedCommand.COMMAND_DECRYPT, AbstractParsedCommand.COMMAND_ROTATE , AbstractParsedCommand.COMMAND_PAGELABELS },
                    StringParam.REQUIRED)
    }));
	
	/**
	 * default help text 
	 */
    private static final String helpText = COMMAND+" -h [command] for commands help. ";
    
	public Collection getArguments() {
		return concatArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return "";
	}

	public String getHelpMessage() {
		return helpText;
	}

	public Collection getOptions() {
		return Collections.EMPTY_LIST;
	}

	public CmdLineHandler getCommandLineHandler() {
		if(commandLineHandler == null){ 
			commandLineHandler = new VersionCmdLineHandler(ConsoleServicesFacade.CREATOR,new HelpCmdLineHandler(getHelpMessage(),ConsoleServicesFacade.getLicense(),"",COMMAND,getCommandDescription(),getOptions(),getArguments()));
			commandLineHandler.setDieOnParseError(false);
		}
		return commandLineHandler;
	}

}
