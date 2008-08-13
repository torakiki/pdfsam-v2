/*
 * Created on 16-Oct-2007
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
import java.util.List;

import jcmdline.BooleanParam;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;

/**
 * Handler for the concat command
 * @author Andrea Vacondio
 *
 */
public class ConcatCmdHandler extends AbstractCmdHandler {

	private  final String commandDescription = "Merge together pdf documents.";
	/**
	 * options for the concat handler
	 */
	private final List concatOptions = new ArrayList(Arrays.asList(new Parameter[] {
	            new FileParam(ConcatParsedCommand.O_ARG,
	                          "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
	                          ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
	                          FileParam.REQUIRED, 
	                          FileParam.SINGLE_VALUED),
	            new PdfFileParam(ConcatParsedCommand.F_ARG,
	                          "pdf files to concat: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf:password)",
	                          FileParam.IS_READABLE,
	                          FileParam.OPTIONAL, 
	                          FileParam.MULTI_VALUED),
	            new StringParam(ConcatParsedCommand.U_ARG,   
	                          "page selection script. You can set a subset of pages to merge. Accepted values: \"all\" or \"num1-num2\" or \"num-\" or \"num1,num2-num3..\" (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:all:), (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -f /tmp/file3.pdf -u all:12-14:32,12-14,4,34-:) to merge file1.pdf and pages 12,13,14 of file2.pdf. If -u is not set default behaviour is to merge document completely",
	                          StringParam.OPTIONAL),                                             
	            new FileParam(ConcatParsedCommand.L_ARG,
							  "xml or csv file containing pdf files list to concat. If csv file in comma separated value format; if xml file <filelist><file value=\"filepath\" /></filelist>",
	                          FileParam.IS_FILE & FileParam.IS_READABLE,
	                          FileParam.OPTIONAL,
	                          FileParam.SINGLE_VALUED),            
	            new BooleanParam(ConcatParsedCommand.COPYFIELDS_ARG, "input pdf documents contain forms (high memory usage)")                          
    })); 
	
	/**
	 * Arguments for the concat handler
	 */
	private final List concatArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {[concat]}",
                    new String[] { ConcatParsedCommand.COMMAND_CONCAT },
                    StringParam.REQUIRED),
    }));
    
    /**
     * The help text for the concat handler
     */
    private static final String concatHelpText = "Concatenate pdf files. \n"+
        "you must specify the '-o /home/user/outfile.pdf' option to set the output file and the source file list:\n"+
        "'-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list or at least one file to concat (use filename:password if the file is password protected).\n"+
        "'-l /tmp/list.csv' a csv file containing the list of files to concat, separated by a comma.\n"+
        "'-l /tmp/list.xml' a xml file containing the list of files to concat, <filelist><file value=\"filepath\" /></filelist>\n"+
        "'-u All:All:3-15' is optional to set pages selection. You can set a subset of pages to merge. Accepted values: \"all\" or \"num1-num2\" (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:all:), (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:12-14:) to merge file1.pdf and pages 12,13,14 of file2.pdf. If -u is not set default behaviour is to merge document completely\n"+
        "Note: You can use only one of these options not both in the same command line\n";
    
    /**
     *  example text for the concat handler
     */
    private final String concatExample = 
    	"Example: java -jar pdfsam-console-VERSION.jar -o /tmp/outfile.pdf -f /tmp/1.pdf:password -f /tmp/2.pdf concat\n"+
        "Example: java -jar pdfsam-console-VERSION.jar -l c:\\docs\\list.csv concat";
    
	public Collection getArguments() {
		return concatArguments;
	}

	public String getHelpExamples() {
		return concatExample;
	}

	public String getHelpMessage() {
		return concatHelpText;
	}

	public Collection getOptions() {
		return concatOptions;
	}
	
	public String getCommandDescription() {
		return commandDescription;
	}
}
