/*
 * Created on 20-June-2007
 * Copyright (C) 2006 by Andrea Vacondio.
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
package org.pdfsam.emp4j.messages.interfaces;

import java.io.Serializable;
import java.text.MessageFormat;

import org.dom4j.Node;
/**
 * Abstract message source
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractMessagesSource implements InquirableMessagesSource, Serializable {

	public AbstractMessagesSource(Node configurationNode){
		
	}
	
	public abstract String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception;

	public abstract String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception;
	
	public String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception{
		String errorString = getLocalizedExceptionMessage(exceptionTypeKey, exceptionErrorCode);
		if(args != null && args.length > 0){
			errorString = MessageFormat.format(errorString, args);
		}
		return errorString;
	}
	
	public String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception{
		String errorString = getExceptionMessage(exceptionTypeKey, exceptionErrorCode);
		if(args != null && args.length > 0){
			errorString = MessageFormat.format(errorString, args);
		}
		return errorString;
	}

}
