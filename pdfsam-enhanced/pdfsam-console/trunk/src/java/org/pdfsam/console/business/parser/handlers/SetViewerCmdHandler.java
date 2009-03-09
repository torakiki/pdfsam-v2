/*
 * Created on 06-Mar-2008
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

import org.pdfsam.console.business.dto.commands.SetViewerParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the setviewer command
 * @author Andrea Vacondio
 */
public class SetViewerCmdHandler extends AbstractCmdHandler {

	private static final String commandDescription = "Set vewer options for the pdf documents.";
	
	/**
	 * Options for the setviewer handler
	 */
	private final List setviewerOptions = new ArrayList(Arrays.asList(new Parameter[] {
	            new FileParam(SetViewerParsedCommand.O_ARG,
		                      "output directory",
		                      ((FileParam.IS_DIR & FileParam.EXISTS)),
		                      FileParam.REQUIRED, 
		                      FileParam.SINGLE_VALUED),
	            new PdfFileParam(SetViewerParsedCommand.F_ARG,
	                          "pdf files to set the viewer options: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
	                          FileParam.IS_READABLE,
	                          FileParam.OPTIONAL, 
	                          FileParam.MULTI_VALUED),	 
               new StringParam(SetViewerParsedCommand.P_ARG,   
            		     	  "prefix for the output files name",
            		     	  StringParam.OPTIONAL), 	                          
	           new StringParam(SetViewerParsedCommand.L_ARG,
	                          "layout for the viewer. { "+SetViewerParsedCommand.L_ONECOLUMN+", "+SetViewerParsedCommand.L_SINGLEPAGE+", "+SetViewerParsedCommand.L_TWOCOLUMNLEFT+", "+SetViewerParsedCommand.L_TWOCOLUMNRIGHT+", "+SetViewerParsedCommand.L_TWOPAGELEFT+", "+SetViewerParsedCommand.L_TWOPAGERIGHT+"} ", 
							  new String[] { SetViewerParsedCommand.L_ONECOLUMN, SetViewerParsedCommand.L_SINGLEPAGE, SetViewerParsedCommand.L_TWOCOLUMNLEFT, SetViewerParsedCommand.L_TWOCOLUMNRIGHT, SetViewerParsedCommand.L_TWOPAGELEFT, SetViewerParsedCommand.L_TWOPAGERIGHT},
							  StringParam.OPTIONAL, 
							  StringParam.SINGLE_VALUED),						   
	           new StringParam(SetViewerParsedCommand.M_ARG,   
	                           "open mode for the viewer {"+SetViewerParsedCommand.M_ATTACHMENTS+", "+SetViewerParsedCommand.M_FULLSCREEN+", "+SetViewerParsedCommand.M_NONE+", "+SetViewerParsedCommand.M_OCONTENT+", "+SetViewerParsedCommand.M_OUTLINES+", "+SetViewerParsedCommand.M_THUMBS+"}. If omitted it uses "+SetViewerParsedCommand.M_NONE,
	                           new String[] { SetViewerParsedCommand.M_ATTACHMENTS, SetViewerParsedCommand.M_FULLSCREEN, SetViewerParsedCommand.M_NONE, SetViewerParsedCommand.M_OCONTENT, SetViewerParsedCommand.M_OUTLINES, SetViewerParsedCommand.M_THUMBS},
	                           StringParam.OPTIONAL, 
	                           StringParam.SINGLE_VALUED),
	           new StringParam(SetViewerParsedCommand.NFSM_ARG,   
                       		  "non full screen mode for the viewer when exiting full screen mode {"+SetViewerParsedCommand.NFSM_NONE+", "+SetViewerParsedCommand.NFSM_OCONTENT+", "+SetViewerParsedCommand.NFSM_OUTLINES+", "+SetViewerParsedCommand.NFSM_THUMBS+"}. If omitted it uses "+SetViewerParsedCommand.NFSM_NONE,
                       		  new String[] { SetViewerParsedCommand.NFSM_NONE, SetViewerParsedCommand.NFSM_OCONTENT, SetViewerParsedCommand.NFSM_OUTLINES, SetViewerParsedCommand.NFSM_THUMBS},
                       		  StringParam.OPTIONAL, 
                       		  StringParam.SINGLE_VALUED),
	          new StringParam(SetViewerParsedCommand.DIRECTION_ARG,   
                       		  "direction {"+SetViewerParsedCommand.D_L2R+", "+SetViewerParsedCommand.D_R2L+"}. If omitted it uses "+SetViewerParsedCommand.D_L2R,
                       		  new String[] { SetViewerParsedCommand.D_L2R, SetViewerParsedCommand.D_R2L},
                       		  StringParam.OPTIONAL, 
                       		  StringParam.SINGLE_VALUED),
              new FileParam(SetViewerParsedCommand.D_ARG,
            		  		  "directory containing pdf files to set the viewer options.",
			                  FileParam.IS_DIR & FileParam.IS_READABLE,
			                  FileParam.OPTIONAL,
			                  FileParam.SINGLE_VALUED),                                   		  
              new BooleanParam(SetViewerParsedCommand.HIDEMENU_ARG, "hide the menu bar"),
              new BooleanParam(SetViewerParsedCommand.HIDETOOLBAR_ARG, "hide the toolbar"),
              new BooleanParam(SetViewerParsedCommand.HIDEWINDOWUI_ARG, "hide user interface elements"),
              new BooleanParam(SetViewerParsedCommand.FITWINDOW_ARG, "resize the window to fit the page size"),
              new BooleanParam(SetViewerParsedCommand.CENTERWINDOW_ARG, "center of the screen"),
              new BooleanParam(SetViewerParsedCommand.DOCTITLE_ARG, "display document title metadata as window title"),
              new BooleanParam(SetViewerParsedCommand.NOPRINTSCALING_ARG, "no page scaling in print dialog")                           
	  })); 
	
	/**
	 * Help text for the setviewer command
	 */
	private static final String setviewerHelpText =  "Set vewer options for the pdf documents. \n"+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list do apply the viewer options (use filename:password if the file is password protected).\n"+
	"'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n"+
	"Available prefix variables: [TIMESTAMP], [BASENAME].\n"+
    "'-layout chosenlayout' to set the viewer layout for the document. Possible values { "+SetViewerParsedCommand.L_ONECOLUMN+", "+SetViewerParsedCommand.L_SINGLEPAGE+", "+SetViewerParsedCommand.L_TWOCOLUMNLEFT+", "+SetViewerParsedCommand.L_TWOCOLUMNRIGHT+", "+SetViewerParsedCommand.L_TWOPAGELEFT+", "+SetViewerParsedCommand.L_TWOPAGERIGHT+"} \n"+
    "'-mode chosenmode' to set the viewer mode for the document. Possible values {"+SetViewerParsedCommand.M_ATTACHMENTS+", "+SetViewerParsedCommand.M_FULLSCREEN+", "+SetViewerParsedCommand.M_NONE+", "+SetViewerParsedCommand.M_OCONTENT+", "+SetViewerParsedCommand.M_OUTLINES+", "+SetViewerParsedCommand.M_THUMBS+"}. If omitted it uses "+SetViewerParsedCommand.M_NONE+"\n"+
    "'-nfsmode chosennonfullscreenmode' to set the viewer mode for the document when exiting full screen mode. Possible values {"+SetViewerParsedCommand.NFSM_NONE+", "+SetViewerParsedCommand.NFSM_OCONTENT+", "+SetViewerParsedCommand.NFSM_OUTLINES+", "+SetViewerParsedCommand.NFSM_THUMBS+"}. If omitted it uses "+SetViewerParsedCommand.NFSM_NONE+" \n"+
    "'-direction chosendirection' to set the viewer direction. Possible values {"+SetViewerParsedCommand.D_L2R+", "+SetViewerParsedCommand.D_R2L+"}\n"+
    "'-d /tmp' a directory containing the pdf files to set the viewer options.\n"+
    "'-"+SetViewerParsedCommand.HIDEMENU_ARG+"' hide the menu bar.\n"+
    "'-"+SetViewerParsedCommand.HIDETOOLBAR_ARG+"' hide the toolbar.\n"+
    "'-"+SetViewerParsedCommand.HIDEWINDOWUI_ARG+"' hide the user interface elements (scrollbars, ...).\n"+
    "'-"+SetViewerParsedCommand.FITWINDOW_ARG+"' resize the window to fit the page displayed.\n"+
    "'-"+SetViewerParsedCommand.CENTERWINDOW_ARG+"' display the document window at the center of the screen.\n"+
    "'-"+SetViewerParsedCommand.DOCTITLE_ARG+"' display document title metadata as window title (filename otherwise).\n"+
    "'-"+SetViewerParsedCommand.NOPRINTSCALING_ARG+"' no page scaling in print dialog\n";
	
	 /**
     *  example text for the setviewer handler
     */
    private static final String setviewerExample = 
    "Example: java -jar pdfsam-console-VERSION.jar -f /tmp/1.pdf -o /tmp -layout onecolumn -mode fullscreen -nfsmode nfsoutlines -direction l2r -hidemenu -displaydoctitle -noprintscaling -overwrite setviewer\n";

	 /**
     * The arguments for setviewer command
     */
	private final List setviewerArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {["+SetViewerParsedCommand.COMMAND_SETVIEWER +"]}",
                    new String[] { SetViewerParsedCommand.COMMAND_SETVIEWER },
                    StringParam.REQUIRED),
    }));
	
	public Collection getArguments() {
		return setviewerArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return setviewerExample;
	}

	public Collection getOptions() {
		return setviewerOptions;
	}

	public String getHelpMessage() {
		return setviewerHelpText;
	}

}
