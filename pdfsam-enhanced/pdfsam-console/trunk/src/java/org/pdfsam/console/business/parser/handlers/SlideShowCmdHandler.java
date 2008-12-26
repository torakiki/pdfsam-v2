/*
 * Created on 07-Mar-2008
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
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.Transition;
import org.pdfsam.console.business.dto.commands.SlideShowParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the slide show command
 * @author Andrea Vacondio
 */
public class SlideShowCmdHandler extends AbstractCmdHandler {

	private static final String commandDescription = "Set slide show options.";

	/**
	 * Options for the slide show handler
	 */
	private final List slideshowOptions = new ArrayList(Arrays.asList(new Parameter[] {
	            new FileParam(SlideShowParsedCommand.O_ARG,
	                    	  "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
	                    	  ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
	                     	  FileParam.REQUIRED, 
	                    	  FileParam.SINGLE_VALUED),
	            new PdfFileParam(SlideShowParsedCommand.F_ARG,
	                    	  "input pdf file to set the slide show options",
	                    	  FileParam.IS_READABLE,
	                    	  FileParam.REQUIRED, 
	                    	  FileParam.SINGLE_VALUED), 
              new StringParam(SlideShowParsedCommand.P_ARG,   
    		     	  		  "prefix for the output files name",
    		     	  		  StringParam.OPTIONAL), 	                    	  
               new StringParam(SlideShowParsedCommand.T_ARG,   
            		     	  "slideshow transition effect definition",
            		     	  12,
            		     	  50,
            		     	  StringParam.OPTIONAL,
            		     	  StringParam.MULTI_VALUED), 	                          
	           new StringParam(SlideShowParsedCommand.DT_ARG,
	        		   		  "slideshow default transition effect definition",
	        		   		  10,
	        		   		  50,
	        		   		  StringParam.OPTIONAL, 
							  StringParam.SINGLE_VALUED),						   
	           new FileParam(SlideShowParsedCommand.L_ARG,
						      "xml file containing all the transitions effects definitions",
						      FileParam.IS_FILE & FileParam.IS_READABLE,
						      FileParam.OPTIONAL,
				              FileParam.SINGLE_VALUED),            
              new BooleanParam(SlideShowParsedCommand.FULLSCREEN_ARG, "open the document in fullscreen mode")
	  })); 

	/**
	 * Help text for the slide show command
	 */
	private static final String slideshowHelpText =  "Set slide show options for the pdf document. \n"+ 
    "You must specify '-f /home/user/infile.pdf' option to set the input file you want to split (use filename:password if the file is password protected).\n" +
	"You must specify '-o /home/user/out.pdf' to set the output file.\n"+
    "'-t transition' to set the slide show transition options. Syntax: transitiontype:transitiondurationinsec:pagedisplaydurationinsec:pagenumber.\n"+
    "Possible transitiontype values { "+Transition.T_BLINDH+", "+Transition.T_BLINDV+", "+Transition.T_BTWIPE+", "+Transition.T_DGLITTER+", "+Transition.T_DISSOLVE+", "+Transition.T_INBOX+", "+Transition.T_LRGLITTER+", "+Transition.T_LRWIPE+", "+Transition.T_OUTBOX+", "+Transition.T_RLWIPE+", "+Transition.T_SPLITHIN+", "+Transition.T_SPLITHOUT+", "+Transition.T_SPLITVIN+", "+Transition.T_SPLITVOUT+", "+Transition.T_TBGLITTER+", "+Transition.T_TBWIPE+"} \n"+
    "Example: blindv:1:3:57 uses a 1 second long vertical blind to display page number 57 for 3 seconds\n"+
    "'-dt defaulttransition' to set the slide show default transition used for every pages except those specified with '-t'\n"+
    "Syntax: transitiontype:transitiondurationinsec:pagedisplaydurationinsec.\n"+
    "Example: blindv:1:3 uses a 1 second long vertical blind to display pages for 3 seconds\n"+
    "'-l /tmp/transitions.xml' a xml file containing all the transitions effects definitions \n"+
    "Example: <transitions defaulttype=\"transitiontype\" defaulttduration=\"transitiondurationinsec\" defaultduration=\"pagedisplaydurationinsec\"><transition type=\"transitiontype\" tduration=\"transitiondurationinsec\" duration=\"pagedisplaydurationinsec\" pagenumber=\"number\" /></transitions>\n"+
    "'-"+SlideShowParsedCommand.FULLSCREEN_ARG+"' open the document in full screen mode\n";
	
	 /**
     *  example text for the slide show handler
     */
    private static final String slideshowExample = 
    "Example: java -jar pdfsam-console-VERSION.jar -f /tmp/1.pdf -o /tmp/out.pdf -fullscreen -dt dissolve:1:5 -t wipel2r:1:5:20 -t wiper2l:1:5:21 -overwrite slideshow\n";
	
	 /**
     * The arguments for slide show command
     */
	private final List slideshowArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+SlideShowParsedCommand.COMMAND_SLIDESHOW +"]}",
                    new String[] { SlideShowParsedCommand.COMMAND_SLIDESHOW },
                    StringParam.REQUIRED),
    }));
	
	public Collection getArguments() {		
		return slideshowArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return slideshowExample;
	}

	public Collection getOptions() {
		return slideshowOptions;
	}

	public String getHelpMessage() {
		return slideshowHelpText;
	}

}
