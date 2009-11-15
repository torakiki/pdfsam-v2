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
import jcmdline.Parameter;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the encrypt command
 * @author Andrea Vacondio
 */
public class EncryptCmdHandler extends AbstractCmdHandler {
	
	private static final String commandDescription = "Encrypt pdf documents.";
	
	/**
	 * Options for the encrypt handler
	 */
	private final List encryptOptions = new ArrayList(Arrays.asList(new Parameter[] {
	            new FileParam(EncryptParsedCommand.O_ARG,
		                      "output directory",
		                      ((FileParam.IS_DIR & FileParam.EXISTS)),
		                      FileParam.REQUIRED, 
		                      FileParam.SINGLE_VALUED),
	            new PdfFileParam(EncryptParsedCommand.F_ARG,
	                          "pdf files to encrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
	                          FileParam.IS_READABLE,
	                          FileParam.OPTIONAL, 
	                          FileParam.MULTI_VALUED),
	            new StringParam(EncryptParsedCommand.P_ARG,   
	                          "prefix for the output files name",
	                           StringParam.OPTIONAL), 
	            new StringParam(EncryptParsedCommand.APWD_ARG,   
	                           "administrator password for the document",
	                            StringParam.OPTIONAL), 
	            new StringParam(EncryptParsedCommand.UPWD_ARG,   
	                            "user password for the document",
	                            StringParam.OPTIONAL),       
                new FileParam(EncryptParsedCommand.D_ARG,
		      		  		    "directory containing pdf files to encrypt.",
			                    FileParam.IS_DIR & FileParam.IS_READABLE,
			                    FileParam.OPTIONAL,
			                    FileParam.SINGLE_VALUED),                                   		  	                            
	            new StringParam(EncryptParsedCommand.ALLOW_ARG,
	                            "permissions: a list of permissions. { "+EncryptParsedCommand.E_PRINT+", "+EncryptParsedCommand.E_MODIFY+", "+EncryptParsedCommand.E_COPY+", "+EncryptParsedCommand.E_ANNOTATION+", "+EncryptParsedCommand.E_FILL+", "+EncryptParsedCommand.E_SCREEN+", "+EncryptParsedCommand.E_ASSEMBLY+", "+EncryptParsedCommand.E_DPRINT+"} ", 
							    new String[] { EncryptParsedCommand.E_PRINT, EncryptParsedCommand.E_MODIFY, EncryptParsedCommand.E_COPY, EncryptParsedCommand.E_ANNOTATION, EncryptParsedCommand.E_FILL, EncryptParsedCommand.E_SCREEN, EncryptParsedCommand.E_ASSEMBLY, EncryptParsedCommand.E_DPRINT },
							    StringParam.OPTIONAL, 
							    StringParam.MULTI_VALUED),						   
	            new StringParam(EncryptParsedCommand.ETYPE_ARG,   
	                            "encryption angorithm {"+EncryptParsedCommand.E_RC4_40+", "+EncryptParsedCommand.E_RC4_128+", "+EncryptParsedCommand.E_AES_128+"}. If omitted it uses "+EncryptParsedCommand.E_RC4_128,
	                            new String[] { EncryptParsedCommand.E_RC4_40, EncryptParsedCommand.E_RC4_128, EncryptParsedCommand.E_AES_128},
	                            StringParam.OPTIONAL, 
	                            StringParam.SINGLE_VALUED)
	  })); 

	 /**
     * The arguments for encrypt command
     */
	private final List encryptArguments = new ArrayList(Arrays.asList(new Parameter[] {
            new StringParam("command",   
                    "command to execute {[encrypt]}",
                    new String[] { EncryptParsedCommand.COMMAND_ENCRYPT },
                    StringParam.REQUIRED),
    }));
    
	/**
	 * Help text for the encrypt command
	 */
	private static final String encryptHelpText =  "Encrypt pdf files. \n"+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf:password -f /tmp/file3.pdf [...]' to specify a file list to encrypt (use filename:password if the file is password protected).\n"+
    "'-apwd password' to set the owner password.\n"+
    "'-upwd password' to set the user password.\n"+
    "'-d /tmp' a directory containing the pdf files to encrypt.\n"+    
    "'-allow permission' to set the permissions list. Possible values {["+EncryptParsedCommand.E_PRINT+"], ["+EncryptParsedCommand.E_ANNOTATION+"], ["+EncryptParsedCommand.E_ASSEMBLY+"], ["+EncryptParsedCommand.E_COPY+"], ["+EncryptParsedCommand.E_DPRINT+"], ["+EncryptParsedCommand.E_FILL+"], ["+EncryptParsedCommand.E_MODIFY+"], ["+EncryptParsedCommand.E_SCREEN+"]}\n"+
	"'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n"+
	"Available prefix variables: [TIMESTAMP], [BASENAME].\n"+
    "'-etype ' to set the encryption angorithm. If omitted it uses rc4_128. Possible values {["+EncryptParsedCommand.E_AES_128+"], ["+EncryptParsedCommand.E_RC4_128+"], ["+EncryptParsedCommand.E_RC4_40+"]}\n";
	
    /**
     *  example text for the encrypt handler
     */
    private static final String encryptExample = 
    "Example: java -jar pdfsam-console-"+ConsoleServicesFacade.VERSION+".jar -f /tmp/1.pdf -o /tmp -apwd hello -upwd word -allow print -allow fill -etype rc4_128 -p encrypted_ encrypt\n";
 
	
	public Collection getArguments() {
		return encryptArguments;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public String getHelpExamples() {
		return encryptExample;
	}

	public Collection getOptions() {
		return encryptOptions;
	}

	public String getHelpMessage() {
		return encryptHelpText;
	}

}
