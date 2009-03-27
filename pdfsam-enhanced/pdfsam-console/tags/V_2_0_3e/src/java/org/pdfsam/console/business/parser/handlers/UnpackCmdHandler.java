/*
 * Created on 30-Jan-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
import java.util.List;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;

import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.UnpackParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the unpack command
 * @author Andrea Vacondio
 */
public class UnpackCmdHandler extends AbstractCmdHandler {
	
	private static final String commandDescription = "Extract attachments from pdf documents.";

	/**
	 * Options for the unpack handler
	 */
	private final List unpackOptions = new ArrayList(Arrays.asList(new Parameter[] {
		          new FileParam(UnpackParsedCommand.O_ARG,
			                      "output directory",
			                      ((FileParam.IS_DIR & FileParam.EXISTS)),
			                      FileParam.REQUIRED, 
			                      FileParam.SINGLE_VALUED),
		          new PdfFileParam(UnpackParsedCommand.F_ARG,
		                          "pdf files to unpack: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
		                          FileParam.IS_READABLE,
		                          FileParam.OPTIONAL, 
		                          FileParam.MULTI_VALUED),
	              new FileParam(UnpackParsedCommand.D_ARG,
			                      "input directory",
			                      ((FileParam.IS_DIR & FileParam.EXISTS)),
			                      FileParam.OPTIONAL, 
			                      FileParam.SINGLE_VALUED),
			      new BooleanParam(AbstractParsedCommand.OVERWRITE_ARG, "overwrite existing output file"),	
			      new FileParam(AbstractParsedCommand.LOG_ARG,
				                  "text file to log output messages",
				                  ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
				                  FileParam.OPTIONAL,
				                  FileParam.SINGLE_VALUED)
	  })); 

	 /**
     * The arguments for unpack command
     */
	private final List unpackArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {[unpack]}",
                    new String[] { UnpackParsedCommand.COMMAND_UNPACK },
                    StringParam.REQUIRED),
    }));

	/**
	 * help text for the unpack handler
	 */
	private static final String unpackHelpText = "Extract attachments from pdf documents. \n"+
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list to unpack (use filename:password if the file is password protected).\n"+
    "'-d /home/filedir' to set a input directory. Every pdf document wil be unpacked.\n"+
    "'-log' to set a log file.\n"+
	"'-overwrite' to overwrite output file if already exists.\n";

   /**
     *  example text for the unpack handler
     */
    private static final String unpackExample = 
    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /tmp -d /tmp/files -overwrite unpack\n";

	private VersionCmdLineHandler commandLineHandler = null;	
	
	public Collection getArguments() {		
		return unpackArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return unpackExample;
	}

	public Collection getOptions() {
		return unpackOptions;
	}

	public String getHelpMessage() {
		return unpackHelpText;
	}

	/**
	 * override parent method
	 * @return the command line handler for this handler
	 */
	public CmdLineHandler getCommandLineHandler() {
		if(commandLineHandler == null){			
			commandLineHandler = new VersionCmdLineHandler(ConsoleServicesFacade.CREATOR,new HelpCmdLineHandler(getHelpMessage()+getHelpExamples(),ConsoleServicesFacade.LICENSE,"",COMMAND,getCommandDescription(),getOptions(),getArguments()));
			commandLineHandler.setDieOnParseError(false);
		}
		return commandLineHandler;
	}
}
