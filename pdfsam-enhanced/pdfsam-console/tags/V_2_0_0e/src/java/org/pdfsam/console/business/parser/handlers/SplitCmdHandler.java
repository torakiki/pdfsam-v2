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

import jcmdline.FileParam;
import jcmdline.LongParam;
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.commands.SplitParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the split command
 * @author Andrea Vacondio
 *
 */
public class SplitCmdHandler extends AbstractCmdHandler {

	private  final String commandDescription = "Split a pdf document.";
	
	/**
	 * Options for the split handler
	 */
	private final List splitOptions = new ArrayList(Arrays.asList(new Parameter[] {
            new FileParam(SplitParsedCommand.O_ARG,
                    "output directory",
                    ((FileParam.IS_DIR & FileParam.EXISTS)),
                    FileParam.REQUIRED, 
                    FileParam.SINGLE_VALUED),
            new PdfFileParam(SplitParsedCommand.F_ARG,
                   "input pdf file to split",
                   FileParam.IS_READABLE,
                   FileParam.REQUIRED, 
                   FileParam.SINGLE_VALUED),
            new StringParam(SplitParsedCommand.P_ARG,   
                    "prefix for the output files name",
                            StringParam.OPTIONAL),                      
            new StringParam(SplitParsedCommand.S_ARG,   
                    "split type {["+SplitParsedCommand.S_BURST+"], ["+SplitParsedCommand.S_ODD+"], ["+SplitParsedCommand.S_EVEN+"], ["+SplitParsedCommand.S_SPLIT+"], ["+SplitParsedCommand.S_NSPLIT+"], ["+SplitParsedCommand.S_SIZE+"]}",
                    new String[] { SplitParsedCommand.S_BURST, SplitParsedCommand.S_ODD, SplitParsedCommand.S_EVEN, SplitParsedCommand.S_SPLIT, SplitParsedCommand.S_NSPLIT, SplitParsedCommand.S_SIZE },
                            StringParam.REQUIRED),
            new StringParam(SplitParsedCommand.N_ARG,
                    "page number to split at if -s is "+SplitParsedCommand.S_SPLIT +" or " + SplitParsedCommand.S_NSPLIT ,             
                    StringParam.OPTIONAL),
            new LongParam(SplitParsedCommand.B_ARG,
                    "size in bytes to split at if -s is "+SplitParsedCommand.S_SIZE ,             
                    LongParam.OPTIONAL)                    
	}));
    
	/**
     * Arguments for the split handler
     */
	private final List splitArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {[split]}",
                    new String[] { SplitParsedCommand.COMMAND_SPLIT },
                    StringParam.REQUIRED),              
	}));
    
    /**
     * Help text for the split handler
     */
    private static final String splitHelpText = "Split pdf file. \n"+ 
	    "You must specify '-f /home/user/infile.pdf' option to set the input file you want to split (use filename:password if the file is password protected).\n" +
	    "You must specify '-o /home/user' to set the output directory.\n"+
	    "You must specify '-s split_type' to set the split type. Possible values: {["+SplitParsedCommand.S_BURST+"], ["+SplitParsedCommand.S_ODD+"], ["+SplitParsedCommand.S_EVEN+"], ["+SplitParsedCommand.S_SPLIT+"], ["+SplitParsedCommand.S_NSPLIT+"]}\n"+
	    "'-p prefix_' to specify a prefix for output names of files. If it contains \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[CURRENTPAGE] generates FileName_prefix_005.pdf)\n"+
	    "Available prefix variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME].\n"+
	    "'-n number' to specify a page number to split at if -s is SPLIT or NSPLIT.\n"+
    	"'-b number' to specify a number of bytes to split at if -s is SIZE.\n";
    
    /**
     * Examples text for the split handler
     */
    public static final String splitExamples = 
	    "Example: java -jar pdfsam-console-VERSION.jar -f /tmp/1.pdf -o /tmp -s BURST -p splitted_ split\n"+
	    "Example: java -jar pdfsam-console-VERSION.jar -f /tmp/1.pdf -o /tmp -s NSPLIT -n 4 split\n";
    
	public Collection getArguments() {
		return splitArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return splitExamples;
	}

	public String getHelpMessage() {
		return splitHelpText;
	}

	public Collection getOptions() {
		return splitOptions;
	}
}
