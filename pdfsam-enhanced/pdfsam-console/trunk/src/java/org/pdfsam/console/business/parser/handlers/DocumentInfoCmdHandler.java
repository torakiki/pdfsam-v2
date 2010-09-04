/*
 * Created on 25-Oct-2009
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
import org.pdfsam.console.business.dto.commands.DocumentInfoParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;

public class DocumentInfoCmdHandler extends AbstractCmdHandler {

	private static final String COMMAND_DESCRIPTION = "Set the pdf document info.";
	
	/**
	 * Options for the doc info handler
	 */
	private final List docinfoOptions = new ArrayList(Arrays.asList(new Parameter[] {
            new FileParam(DocumentInfoParsedCommand.O_ARG,
                    "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
                    ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                    FileParam.REQUIRED, 
                    FileParam.SINGLE_VALUED),
            new PdfFileParam(DocumentInfoParsedCommand.F_ARG,
                   "input pdf file to split",
                   FileParam.IS_READABLE,
                   FileParam.REQUIRED, 
                   FileParam.SINGLE_VALUED),                    
            new StringParam(DocumentInfoParsedCommand.TITLE_ARG,
                    "document title",             
                    StringParam.OPTIONAL),
            new StringParam(DocumentInfoParsedCommand.AUTHOR_ARG,
                    "document author",             
                    StringParam.OPTIONAL),
            new StringParam(DocumentInfoParsedCommand.SUBJECT_ARG,
                    "document subject",             
                    StringParam.OPTIONAL),
            new StringParam(DocumentInfoParsedCommand.KEYWORDS_ARG,
                    "document keywords",             
                    StringParam.OPTIONAL)
	}));
    	
    /**
     * Help text for the doc info handler
     */
    private static final String DOCINFO_HELP_TEXT = "Set the pdf document info. \n"+ 
	    "You must specify '-f /home/user/infile.pdf' option to set the input file you want to set the attibutes (use filename:password if the file is password protected).\n" +
        "you must specify the '-o /home/user/outfile.pdf' option to set the output file\n"+
	    "'-title string' to specify the document title to set.\n"+
	    "'-subject string' to specify the document subject to set.\n"+
	    "'-keywords string' to specify the document keywords to set.\n"+
    	"'-author string' to specify the document author to set.\n";
    
	/**
     * Arguments for the docinfo handler
     */
	private final List docinfoArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+DocumentInfoParsedCommand.COMMAND_SETDOCINFO+"]}",
                    new String[] { DocumentInfoParsedCommand.COMMAND_SETDOCINFO },
                    StringParam.REQUIRED),              
	}));
	
    /**
     * Examples text for the split handler
     */
    public static final String DOCINFO_EXAMPLE = 
	    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /out.pdf -title \"The title\" -author John -keywords \"some keyword here\" "+DocumentInfoParsedCommand.COMMAND_SETDOCINFO+"\n";
    
	public Collection getArguments() {
		return docinfoArguments;
	}

	public String getCommandDescription() {
		return COMMAND_DESCRIPTION;
	}

	public String getHelpExamples() {
		return DOCINFO_EXAMPLE;
	}

	public Collection getOptions() {
		return docinfoOptions;
	}

	public String getHelpMessage() {
		return DOCINFO_HELP_TEXT;
	}

}
