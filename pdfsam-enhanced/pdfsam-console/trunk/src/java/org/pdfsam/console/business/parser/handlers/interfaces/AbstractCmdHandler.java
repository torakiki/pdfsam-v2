/*
 * Created on 21-Sep-2007
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
package org.pdfsam.console.business.parser.handlers.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;

/**
 * Abstract CmdHandler to be extended to handle input arguments based on the input command (concat, split, ecc ecc)
 * @author Andrea Vacondio
 */
public abstract class AbstractCmdHandler implements CmdHandler{
	
    /**
     * help text commonly used by any handler
     */
	private static final String commonHelpText =
		"'-log' to set a log file.\n"+
    	"'-overwrite' to overwrite output file if already exists.\n"+
        "'-pdfversion version' to set the output document pdf version. Possible values {["+AbstractParsedCommand.VERSION_1_2+"], ["+AbstractParsedCommand.VERSION_1_3+"], ["+AbstractParsedCommand.VERSION_1_4+"], ["+AbstractParsedCommand.VERSION_1_5+"], ["+AbstractParsedCommand.VERSION_1_6+"], ["+AbstractParsedCommand.VERSION_1_7+"]}\n"+
    	"'-compress' to compress output file.\n\n";	
    
	/**
	 * options commonly used by any handler
	 */
	private final List commonOptions = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam(AbstractParsedCommand.PDFVERSION_ARG,   
                   "pdf version of the output document/s.",
                   new String[] { Character.toString(AbstractParsedCommand.VERSION_1_2), Character.toString(AbstractParsedCommand.VERSION_1_3), Character.toString(AbstractParsedCommand.VERSION_1_4), Character.toString(AbstractParsedCommand.VERSION_1_5), Character.toString(AbstractParsedCommand.VERSION_1_6), Character.toString(AbstractParsedCommand.VERSION_1_7)},
                   StringParam.OPTIONAL, 
                   StringParam.SINGLE_VALUED),
            new BooleanParam(AbstractParsedCommand.OVERWRITE_ARG, "overwrite existing output file"),
            new BooleanParam(AbstractParsedCommand.COMPRESSED_ARG, "compress output file")
			}));
	
	private VersionCmdLineHandler commandLineHandler = null;

    
	/**
	 * Constructor
	 */
	public AbstractCmdHandler(){	

	}

	
	/**
	 * @return Help examples message for this handler
	 */
	public abstract String getHelpExamples();

	/**
	 * @return Options for this handler
	 */
	public abstract Collection getOptions();
	
	/**
	 * @return Arguments for this handler
	 */
	public abstract Collection getArguments();
	
	/**
	 * @return Description for the command of this handler
	 */
	public abstract String getCommandDescription();


	
	/**
	 * @return the command line handler for this handler
	 */
	public CmdLineHandler getCommandLineHandler() {
		if(commandLineHandler == null){
			ArrayList options = new ArrayList();
			if(getOptions() != null){
				options.addAll(getOptions());
			}
			options.addAll(commonOptions); 
			commandLineHandler = new VersionCmdLineHandler(ConsoleServicesFacade.CREATOR,new HelpCmdLineHandler(getHelpMessage()+commonHelpText+getHelpExamples(),ConsoleServicesFacade.getLicense(),"",COMMAND,getCommandDescription(),options,getArguments()));
			commandLineHandler.setDieOnParseError(false);
		}
		return commandLineHandler;
	}
	
}
