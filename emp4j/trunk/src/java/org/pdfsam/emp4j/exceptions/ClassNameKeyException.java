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
package org.pdfsam.emp4j.exceptions;

/**
 * It behaves getting error messages from the ExceptionMessageHandler using the class name as a key String.
 * If you want to use <code>ClassNameKeyException</code> as a superclass, your <code>InquirableMessagesSource</code> must use the class name as key to get 
 * the exception type description.
 * @author Andrea Vacondio
 * @see org.pdfsam.emp4j.messages.interfaces.InquirableMessagesSource
 */
public class ClassNameKeyException extends ParentEmp4jException {

	private static final long serialVersionUID = -6413113623011278491L;

    /**
     * @param e
     */
    public ClassNameKeyException(Throwable e) {
    	 super(e);
    }

    /**
     * @param exceptionErrorCode
     * @param e
     */
    public ClassNameKeyException(int exceptionErrorCode, Throwable e) {
        this(exceptionErrorCode, null, e);
    }
    
    /**
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitue placeholders
     * @param e
     */
    public ClassNameKeyException(int exceptionErrorCode, String[] args, Throwable e) {
        super(e);
        errorMessage = getExceptionMessage(getClass().getName(), exceptionErrorCode, args);
        localizedErrorMessage = getLocalizedExceptionMessage(getClass().getName(), exceptionErrorCode, args);
    }

    /**
     * @param exceptionErrorCode
     */
    public ClassNameKeyException(int exceptionErrorCode) {
        this(exceptionErrorCode, new String[0]);
    }
    
    /**
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitue placeholders
     */
    public ClassNameKeyException(int exceptionErrorCode, String[] args) {
        super();
        errorMessage = getExceptionMessage(getClass().getName(), exceptionErrorCode, args);
        localizedErrorMessage = getLocalizedExceptionMessage(getClass().getName(), exceptionErrorCode, args);
    }    
}
