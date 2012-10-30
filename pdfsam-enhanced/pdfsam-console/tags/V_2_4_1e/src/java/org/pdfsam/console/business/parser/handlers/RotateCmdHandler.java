/*
 * Created on 25-Jul-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
import org.pdfsam.console.business.dto.commands.RotateParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * handler for the rotate command
 * @author Andrea Vacondio
 *
 */
public class RotateCmdHandler extends AbstractCmdHandler {

	private static final String COMMAND_DESCRIPTION = "Rotate pdf documents.";

	/**
	 * Options for the rotate handler
	 */
	private final List rotateOptions = new ArrayList(Arrays.asList(new Parameter[] {
		          new FileParam(RotateParsedCommand.O_ARG,
			                      "output directory",
			                      ((FileParam.IS_DIR & FileParam.EXISTS)),
			                      FileParam.REQUIRED, 
			                      FileParam.SINGLE_VALUED),
		          new PdfFileParam(RotateParsedCommand.F_ARG,
		                          "pdf files to rotate: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
		                          FileParam.IS_READABLE,
		                          FileParam.REQUIRED, 
		                          FileParam.MULTI_VALUED),
                  new StringParam(RotateParsedCommand.P_ARG,   
                          		  "prefix for the output files name",
                                  StringParam.OPTIONAL),
                  new StringParam(RotateParsedCommand.R_ARG,   
            			  		  "pages rotation. You can set pages rotation. Accepted string is \"pages:rotationdegrees\" where pages can be one among \"all\", \"odd\", \"even\" and where rotationdegrees can be \"90\", \"180\" or \"270\". Pages will be rotate clockwise",
            			  		  StringParam.REQUIRED),                                   
	  })); 

	 /**
     * The arguments for rotate command
     */
	private final List rotateArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+RotateParsedCommand.COMMAND_ROTATE +"]}",
                    new String[] { RotateParsedCommand.COMMAND_ROTATE },
                    StringParam.REQUIRED),
    }));
	
	/**
	 * help text for the rotate handler
	 */
	private static final String ROTATE_HELP_TEXT = "Rotate pdf files. \n"+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list to rotate (use filename:password if the file is password protected).\n"+
    "You must specify '-r all:90' to set pages rotation. (EX. -r all:90 will rotate all pages of 90 degrees clockwise. -r even:270 will rotate even pages of 170 degrees clockwise.)\n"+
    "'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n";
	
   /**
     *  example text for the rotate handler
     */
    private static final String ROTATE_EXAMPLE = 
    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /tmp -r odd:180 -overwrite rotate\n";

	public Collection getArguments() {
		return rotateArguments;
	}

	public String getCommandDescription() {
		return COMMAND_DESCRIPTION;
	}

	public String getHelpExamples() {
		return ROTATE_EXAMPLE;
	}

	public Collection getOptions() {
		return rotateOptions;
	}

	public String getHelpMessage() {
		return ROTATE_HELP_TEXT;
	}

}
