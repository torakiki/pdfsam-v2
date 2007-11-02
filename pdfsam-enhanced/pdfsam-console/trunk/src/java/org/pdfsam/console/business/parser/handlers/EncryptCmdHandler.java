/*
 * Created on 16-Oct-2007
 * Copyright (C) 2007 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
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
import jcmdline.StringParam;

import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.parser.handlers.interfaces.AbstractCmdHandler;
/**
 * Handler for the encrypt command
 * @author Andrea Vacondio
 */
public class EncryptCmdHandler extends AbstractCmdHandler {
	
	private  final String commandDescription = "Merge together pdf documents.";
	
	/**
	 * Options for the encrypt handler
	 */
	private final List encryptOptions = new ArrayList(Arrays.asList(new Parameter[] {
	            new FileParam("o",
		                      "output directory",
		                      ((FileParam.IS_DIR & FileParam.EXISTS)),
		                      FileParam.REQUIRED, 
		                      FileParam.SINGLE_VALUED),
	            new FileParam("f",
	                          "pdf files to encrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
	                          FileParam.IS_FILE & FileParam.IS_READABLE,
	                          FileParam.REQUIRED, 
	                          FileParam.MULTI_VALUED),
	            new StringParam("p",   
	                          "prefix for the output files name",
	                           StringParam.OPTIONAL), 
	            new StringParam("apwd",   
	                           "administrator password for the document",
	                            StringParam.OPTIONAL), 
	            new StringParam("upwd",   
	                            "user password for the document",
	                            StringParam.OPTIONAL),                            
	           new StringParam("allow",
	                          "permissions: a list of permissions",
							  new String[] { EncryptParsedCommand.E_PRINT, EncryptParsedCommand.E_MODIFY, EncryptParsedCommand.E_COPY, EncryptParsedCommand.E_ANNOTATION, EncryptParsedCommand.E_FILL, EncryptParsedCommand.E_SCREEN, EncryptParsedCommand.E_ASSEMBLY, EncryptParsedCommand.E_DPRINT },
	                          FileParam.OPTIONAL, 
	                          FileParam.MULTI_VALUED),						   
	           new StringParam("etype",   
	                           "encryption angorithm. If omitted it uses rc4_128",
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
                    new String[] { "encrypt" },
                    StringParam.REQUIRED),
    }));
    
	/**
	 * Help text for the encrypt command
	 */
	private static final String encryptHelpText =  "Encrypt pdf files. "+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf -f /tmp/file3.pdf [...]' to specify a file list to encrypt.\n"+
    "'-apwd password' to set the owner password.\n"+
    "'-upwd password' to set the user password.\n"+
    "'-allow permission' to set the permissions list. Possible values {["+EncryptParsedCommand.E_PRINT+"], ["+EncryptParsedCommand.E_ANNOTATION+"], ["+EncryptParsedCommand.E_ASSEMBLY+"], ["+EncryptParsedCommand.E_COPY+"], ["+EncryptParsedCommand.E_DPRINT+"], ["+EncryptParsedCommand.E_FILL+"], ["+EncryptParsedCommand.E_MODIFY+"], ["+EncryptParsedCommand.E_SCREEN+"]}\n"+
	"'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n"+
	"Available prefix variables: [TIMESTAMP], [BASENAME].\n"+
    "'-etype ' to set the encryption angorithm. If omitted it uses rc4_128. Possible values {["+EncryptParsedCommand.E_AES_128+"], ["+EncryptParsedCommand.E_RC4_128+"], ["+EncryptParsedCommand.E_RC4_40+"]}\n";
	
    /**
     *  example text for the encrypt handler
     */
    private final String encryptExample = 
    "Example: java -jar pdfsam-console-VERSION.jar -f /tmp/1.pdf -o /tmp -apwd hello -upwd word -allow print -allow fill -etype rc4_128 -p encrypted_ encrypt\n";
 
	
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
