/*
 * Created on 21-June-2006
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.emp4j.messages.interfaces;
/**
 * Interface for the message source
 * @author Andrea Vacondio
 *
 */
public interface InquirableMessagesSource {
	
	/**
	 * @param exceptionTypeKey Exception type key
	 * @param exceptionErrorCode Exception error code
	 * @param args Strings to be substituted to the message
	 * @return the localized exception message 
	 * @throws Exception
	 */
	public String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception;
	
	/**
	 * @param exceptionTypeKey Exception type key
	 * @param exceptionErrorCode Exception error code
	 * @param args Strings to be substituted to the message
	 * @return the exception message 
	 * @throws Exception
	 */
	public String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args) throws Exception;
	
	/**
	 * @param exceptionTypeKey Exception type key
	 * @param exceptionErrorCode Exception error code
	 * @return the localized exception message 
	 * @throws Exception
	 */
	public String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception;
	
	/**
	 * @param exceptionTypeKey Exception type key
	 * @param exceptionErrorCode Exception error code
	 * @return the exception message 
	 * @throws Exception
	 */
	public String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode) throws Exception;
}
