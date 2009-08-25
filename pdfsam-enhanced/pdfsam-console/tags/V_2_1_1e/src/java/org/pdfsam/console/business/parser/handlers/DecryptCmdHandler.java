/*
 * Created on 30-Oct-2008
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

import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.DecryptParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the decrypt command
 * @author Andrea Vacondio
 */
public class DecryptCmdHandler extends AbstractCmdHandler {
	
	private static final String commandDescription = "Decrypt pdf documents.";

	/**
	 * Options for the decrypt handler
	 */
	private final List decryptOptions = new ArrayList(Arrays.asList(new Parameter[] {
		          new FileParam(DecryptParsedCommand.O_ARG,
			                      "output directory",
			                      ((FileParam.IS_DIR & FileParam.EXISTS)),
			                      FileParam.REQUIRED, 
			                      FileParam.SINGLE_VALUED),
		          new PdfFileParam(DecryptParsedCommand.F_ARG,
		                          "pdf files to decrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
		                          FileParam.IS_READABLE,
		                          FileParam.REQUIRED, 
		                          FileParam.MULTI_VALUED),
                  new StringParam(DecryptParsedCommand.P_ARG,   
                          		  "prefix for the output files name",
                                  StringParam.OPTIONAL)
	  })); 

	 /**
     * The arguments for decrypt command
     */
	private final List decryptArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {[decrypt]}",
                    new String[] { DecryptParsedCommand.COMMAND_DECRYPT },
                    StringParam.REQUIRED),
    }));
	
	/**
	 * help text for the decrypt handler
	 */
	private static final String decryptHelpText = "Decrypt pdf files. \n"+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list to decrypt (use filename:password if the file is password protected).\n"+
	"'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n";
	
   /**
     *  example text for the decrypt handler
     */
    private static final String decryptExample = 
    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /tmp -overwrite decrypt\n";

	public Collection getArguments() {
		return decryptArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return decryptExample;
	}

	public Collection getOptions() {
		return decryptOptions;
	}

	public String getHelpMessage() {
		return decryptHelpText;
	}

}
