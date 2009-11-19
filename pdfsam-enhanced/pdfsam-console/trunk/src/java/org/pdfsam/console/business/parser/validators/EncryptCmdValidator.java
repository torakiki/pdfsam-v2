/*
 * Created on 17-Oct-2007
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
package org.pdfsam.console.business.parser.validators;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.PdfFileParam;
import jcmdline.StringParam;
import jcmdline.dto.PdfFile;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.EncryptParsedCommand;
import org.pdfsam.console.business.parser.validators.interfaces.AbstractCmdValidator;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ParseException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;
import com.lowagie.text.pdf.PdfWriter;
/**
 * CmdValidator for the encrypt command
 * @author Andrea Vacondio
 */
public class EncryptCmdValidator extends AbstractCmdValidator {

	public AbstractParsedCommand validateArguments(CmdLineHandler cmdLineHandler) throws ConsoleException {
		
		EncryptParsedCommand parsedCommandDTO = new EncryptParsedCommand();
		
		if(cmdLineHandler != null){
			//-o
			FileParam oOption = (FileParam) cmdLineHandler.getOption(EncryptParsedCommand.O_ARG);
			if ((oOption.isSet())){
	            File outFile = oOption.getFile();
	            ValidationUtility.assertValidDirectory(outFile);
	            parsedCommandDTO.setOutputFile(outFile);		    		
	        }else{
	        	throw new ParseException(ParseException.ERR_NO_O);
	        }
			
			//-p
	        StringParam pOption = (StringParam) cmdLineHandler.getOption(EncryptParsedCommand.P_ARG);
	        if(pOption.isSet()){
	        	parsedCommandDTO.setOutputFilesPrefix(pOption.getValue());
	        }
	        
	        //-apwd
	        StringParam apwdOption = (StringParam) cmdLineHandler.getOption(EncryptParsedCommand.APWD_ARG);
	        if(apwdOption.isSet()){
	        	parsedCommandDTO.setOwnerPwd(apwdOption.getValue());
	        }
	        
	        //-upwd
	        StringParam upwdOption = (StringParam) cmdLineHandler.getOption(EncryptParsedCommand.UPWD_ARG);
	        if(upwdOption.isSet()){
	        	parsedCommandDTO.setUserPwd(upwdOption.getValue());
	        }
	        
	        //-etype
	        StringParam etypeOption = (StringParam) cmdLineHandler.getOption(EncryptParsedCommand.ETYPE_ARG);
	        if(etypeOption.isSet()){
	        	parsedCommandDTO.setEncryptionType(etypeOption.getValue());
	        }
	        
	        //-f - d
			PdfFileParam fOption = (PdfFileParam) cmdLineHandler.getOption(EncryptParsedCommand.F_ARG);
			FileParam dOption = (FileParam) cmdLineHandler.getOption(EncryptParsedCommand.D_ARG);
			if(fOption.isSet() || dOption.isSet()){
				//-f
		        if(fOption.isSet()){
					//validate file extensions
		        	for(Iterator fIterator = fOption.getPdfFiles().iterator(); fIterator.hasNext();){
		        		PdfFile currentFile = (PdfFile) fIterator.next();
		        		ValidationUtility.assertValidPdfExtension(currentFile.getFile().getName());		        	
		        	}
		        	parsedCommandDTO.setInputFileList(FileUtility.getPdfFiles(fOption.getPdfFiles()));
		        }
		        //-d
				if ((dOption.isSet())){
		            File inputDir = dOption.getFile();
		            ValidationUtility.assertValidDirectory(inputDir);
		            parsedCommandDTO.setInputDirectory(inputDir);	
		        }
			}else{
				throw new ParseException(ParseException.ERR_NO_F_OR_D);
			}
	        
	        //-allow
	        StringParam allowOption = (StringParam) cmdLineHandler.getOption(EncryptParsedCommand.ALLOW_ARG);
	        if(allowOption.isSet()){
	        	Hashtable permissionsMap = getPermissionsMap(parsedCommandDTO.getEncryptionType());
	        	int permissions = 0;
	        	if(!permissionsMap.isEmpty()){
		        	for(Iterator permIterator = allowOption.getValues().iterator(); permIterator.hasNext();){
		        		String currentPermission = (String) permIterator.next();
		        		Object value = permissionsMap.get(currentPermission);
		        		if(value != null){
		        			permissions |= ((Integer)value).intValue();
				    	}
		        	}
	        	}
	        	permissionsMap = null;
	        	parsedCommandDTO.setPermissions(permissions);
	        }
		}else{
			throw new ConsoleException(ConsoleException.CMD_LINE_HANDLER_NULL);
		}
		return parsedCommandDTO;	
	}
 
	/**
	 * @param encryptionType encryption algorithm
	 * @return The permissions map based on the chosen encryption
	 */
	private Hashtable getPermissionsMap(String encryptionType){
		Hashtable retMap = new Hashtable(12);
		if(EncryptParsedCommand.E_RC4_40.equals(encryptionType)){
			retMap.put(EncryptParsedCommand.E_PRINT,new Integer(PdfWriter.ALLOW_PRINTING));
			retMap.put(EncryptParsedCommand.E_MODIFY,new Integer(PdfWriter.ALLOW_MODIFY_CONTENTS));
			retMap.put(EncryptParsedCommand.E_COPY,new Integer(PdfWriter.ALLOW_COPY));
			retMap.put(EncryptParsedCommand.E_ANNOTATION,new Integer(PdfWriter.ALLOW_MODIFY_ANNOTATIONS));
		}else{
			retMap.put(EncryptParsedCommand.E_PRINT,new Integer(PdfWriter.ALLOW_PRINTING));
			retMap.put(EncryptParsedCommand.E_MODIFY,new Integer(PdfWriter.ALLOW_MODIFY_CONTENTS));
			retMap.put(EncryptParsedCommand.E_COPY,new Integer(PdfWriter.ALLOW_COPY));
			retMap.put(EncryptParsedCommand.E_ANNOTATION,new Integer(PdfWriter.ALLOW_MODIFY_ANNOTATIONS));
			retMap.put(EncryptParsedCommand.E_FILL,new Integer(PdfWriter.ALLOW_FILL_IN));
			retMap.put(EncryptParsedCommand.E_SCREEN,new Integer(PdfWriter.ALLOW_SCREENREADERS));
			retMap.put(EncryptParsedCommand.E_ASSEMBLY,new Integer(PdfWriter.ALLOW_ASSEMBLY));
			retMap.put(EncryptParsedCommand.E_DPRINT,new Integer(PdfWriter.ALLOW_DEGRADED_PRINTING));
		}
		
		return retMap;
	}
}
