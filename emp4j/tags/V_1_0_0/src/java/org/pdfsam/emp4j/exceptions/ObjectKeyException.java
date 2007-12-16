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
 * It behaves getting error messages from the ExceptionMessageHandler using <code>exceptionKeyCode</code> key Object.
 * If you want to use <code>ObjectKeyException</code> as a superclass, your <code>InquirableMessagesSource</code> must be able to use
 * the Object key to get the exception type description.
 * @author Andrea Vacondio
 * @see org.pdfsam.emp4j.messages.interfaces.InquirableMessagesSource
 */
public class ObjectKeyException extends ParentEmp4jException {

	private static final long serialVersionUID = -8205856107721458720L;
	private static final int DEFAULT_ERROR_CODE = 0x01;

    /**
 	 * It doesn't use the emp4j framework to get the exception message.
	 * @param t
     */
    public ObjectKeyException(Throwable t) {
    	super(t);
    }
    
    /**
     * Constructor using the DEFAULT_ERROR_CODE for this class 
     * @param exceptionKeyCode Object key to get the exception type description
     * @param e
     */
    public ObjectKeyException(Object exceptionKeyCode, Throwable e) {
    	super(exceptionKeyCode, ObjectKeyException.DEFAULT_ERROR_CODE, e);
    }

    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param e
     */
    public ObjectKeyException(Object exceptionKeyCode, int exceptionErrorCode, Throwable e) {
        super(exceptionKeyCode, exceptionErrorCode, null, e);
    }
    
    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitue place holders
     * @param e
     */
    public ObjectKeyException(Object exceptionKeyCode, int exceptionErrorCode, String[] args, Throwable e) {
        super(exceptionKeyCode, exceptionErrorCode, args, e);
    }

    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     */
    public ObjectKeyException(Object exceptionKeyCode,int exceptionErrorCode) {
    	super(exceptionKeyCode, exceptionErrorCode);
    }
   
    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitue place holders
     */
    public ObjectKeyException(Object exceptionKeyCode, int exceptionErrorCode, String[] args) {
        super(exceptionKeyCode, exceptionErrorCode, args);
    }    

}
