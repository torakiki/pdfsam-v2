/*
 * Created on 19-Aug-2009
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
import org.pdfsam.console.business.dto.PageLabel;
import org.pdfsam.console.business.dto.commands.PageLabelsParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * handler for the pagelabels command
 * @author Andrea Vacondio
 *
 */
public class PageLabelsCmdHandler extends AbstractCmdHandler {

	private static final String COMMAND_DESCRIPTION = "Set page labels for a pdf document.";

	/**
	 * Options for the page labels handler
	 */
	private final List pageLabelsOptions = new ArrayList(Arrays.asList(new Parameter[] {
				 new FileParam(PageLabelsParsedCommand.O_ARG,
			                    "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
			                    ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
			                    FileParam.REQUIRED, 
			                    FileParam.SINGLE_VALUED),
		          new PdfFileParam(PageLabelsParsedCommand.F_ARG,
		                          "pdf files to set page labels",
		                          FileParam.IS_READABLE,
		                          FileParam.REQUIRED, 
		                          FileParam.SINGLE_VALUED),
                  new StringParam(PageLabelsParsedCommand.L_ARG,   
            			  		  "labels. You can set page labels. Accepted string is \"pageindex:style:logicalnumber\" where pageindex is the index of the starting page within the document, style is the label style and logicalnumber (optional) is the first logical page number ",
            			  		  7,
            			  		  -1,
            			  		  StringParam.REQUIRED,
            			  		  StringParam.MULTI_VALUED),                                   
                  new StringParam(PageLabelsParsedCommand.LP_ARG,   
		    			  		  "label prefix. You can set page a label prefix. Accepted string is \"pageindex:prefix\" where pageindex is a page index that match a page index set with the "+PageLabelsParsedCommand.L_ARG+" parameter and prefix is the prefix for this numbering ",
		    			  		  1,
		    			  		  -1,
		    			  		  StringParam.OPTIONAL,
            			  		  StringParam.MULTI_VALUED),                                   
	  })); 
	
	 /**
     * The arguments for page labels command
     */
	private final List pageLabelsArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+PageLabelsParsedCommand.COMMAND_PAGELABELS +"]}",
                    new String[] { PageLabelsParsedCommand.COMMAND_PAGELABELS },
                    StringParam.REQUIRED),
    }));	
	
	/**
	 * help text for the page labels handler
	 */
	private static final String PAGE_LABELS_HELP_TEXT = "Set page labels for a pdf file. \n"+ 
    "You must specify '-f /home/user/infile.pdf' option to set the input file you want to set page labels (use filename:password if the file is password protected).\n" +
	"You must specify '-o /home/user/out.pdf' to set the output file.\n"+
    "You must specify '-l 1:arabic' to set pages labels. (EX. -l 1:arabic -l 5:uroman:1  will number the document pages starting from the first page with arabic numbers and starting from the fifth page (first logical page) with upper case roman numbers\n"+
    "Possible style values { "+PageLabel.ARABIC+", "+PageLabel.EMPTY+", "+PageLabel.LLETTER+", "+PageLabel.LROMAN+", "+PageLabel.ULETTER+", "+PageLabel.UROMAN+"} \n"+
    "'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n"+
    "'-lp 1:AP' to specify a prefix for the labal numbering that starts at page 1. This parameter is ignored if there is no -l option that starts at page 1\n"+
    "(EX. -lp 1:AP -lp 5:X will set prefix \"AP\" for the numbering starting at page 1 and the prefix \"X\" for the one starting at page 5)";
	
   /**
     *  example text for the page labels handler
     */
    private static final String PAGE_LABELS_EXAMPLE = 
    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /tmp -r odd:180 -overwrite rotate\n";
    
	public Collection getArguments() {
		return pageLabelsArguments;
	}

	public String getCommandDescription() {
		return COMMAND_DESCRIPTION;
	}

	public String getHelpExamples() {
		return PAGE_LABELS_EXAMPLE;
	}

	public Collection getOptions() {
		return pageLabelsOptions;
	}

	public String getHelpMessage() {
		return PAGE_LABELS_HELP_TEXT;
	}

}
