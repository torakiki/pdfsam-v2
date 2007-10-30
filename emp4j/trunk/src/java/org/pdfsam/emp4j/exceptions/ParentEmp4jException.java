/*
 * Created on 30-August-2007
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

import org.pdfsam.emp4j.providers.ExceptionMessageProvider;
/**
 * Parent exception with already implemented protected methods getExceptionMessage and getLocalizedExceptionMessage.
 * @author Andrea Vacondio
 * @see org.pdfsam.emp4j.providers.ExceptionMessageProvider
 */
public class ParentEmp4jException extends Exception {

	private static final long serialVersionUID = 1665312932892362305L;
	protected String errorMessage;	
	protected String localizedErrorMessage;	

	/**
	 * Empty constructor.
	 * It doesn't use the emp4j framework to get the exception message.
	 */
	public ParentEmp4jException(){		
	}
	
	/**
	 * It doesn't use the emp4j framework to get the exception message.
	 * @param errorMessage 
	 */
	public ParentEmp4jException(String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	/**
	 * It doesn't use the emp4j framework to get the exception message.
	 * @param t
	 */
	public ParentEmp4jException(Throwable t){
		super(t);
	}
	
	/**
	 * It doesn't use the emp4j framework to get the exception message.
	 * @param errorMessage
	 * @param t
	 */
	public ParentEmp4jException(String errorMessage, Throwable t){
		super(t);
		this.errorMessage = errorMessage;
	}
	
    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param e
     */
    public ParentEmp4jException(Object exceptionKeyCode, int exceptionErrorCode, Throwable e) {
        this(exceptionKeyCode, exceptionErrorCode, null, e);
    }
    
    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitue placeholders
     * @param e
     */
    public ParentEmp4jException(Object exceptionKeyCode, int exceptionErrorCode, String[] args, Throwable e) {
        super(e);
        errorMessage = getExceptionMessage(exceptionKeyCode, exceptionErrorCode, args);
        localizedErrorMessage = getLocalizedExceptionMessage(exceptionKeyCode, exceptionErrorCode, args);
    }

    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     */
    public ParentEmp4jException(Object exceptionKeyCode,int exceptionErrorCode) {
    	this(exceptionKeyCode, exceptionErrorCode, new String[0]);
    }
   
    /**
     * @param exceptionKeyCode Object key to get the exception type description
     * @param exceptionErrorCode
     * @param args arguments used by the MessageFormat to substitute place holders
     */
    public ParentEmp4jException(Object exceptionKeyCode, int exceptionErrorCode, String[] args) {
        super();
        errorMessage = getExceptionMessage(exceptionKeyCode, exceptionErrorCode, args);
        localizedErrorMessage  = getLocalizedExceptionMessage(exceptionKeyCode, exceptionErrorCode, args);
    }
    
    /**
     * 
     * @param exceptionTypeKey
     * @param exceptionErrorCode
     * @param args
     * @return message
     */
    protected String getExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args){
    	String retVal = "";
    	try{
    		ExceptionMessageProvider emp = ExceptionMessageProvider.getInstance();
    		if (emp != null){
    			retVal = ExceptionMessageProvider.getInstance().getExceptionMessage(exceptionTypeKey, exceptionErrorCode, args);
    		}else{
    			retVal = "ExceptionMessageProvider is null: exceptionTypeKey="+exceptionTypeKey.toString()+" exceptionErrorCode="+exceptionErrorCode;
    		}
    	}catch(Exception ex){
    		retVal = "Unable to get Exception message ["+ex.getMessage()+"] exceptionTypeKey="+exceptionTypeKey.toString()+" exceptionErrorCode="+exceptionErrorCode;
    	}
    	return retVal ;
    }
    
    /**
     * 
     * @param exceptionTypeKey
     * @param exceptionErrorCode
     * @param args
     * @return localized message
     */
    protected String getLocalizedExceptionMessage(Object exceptionTypeKey, int exceptionErrorCode, String[] args){
    	String retVal = "";
    	try{
    		ExceptionMessageProvider emp = ExceptionMessageProvider.getInstance();
    		if (emp != null){
    			retVal = ExceptionMessageProvider.getInstance().getLocalizedExceptionMessage(exceptionTypeKey, exceptionErrorCode, args);
    		}else{
    			retVal = "ExceptionMessageProvider is null: exceptionTypeKey="+exceptionTypeKey.toString()+" exceptionErrorCode="+exceptionErrorCode;
    		}
    	}catch(Exception ex){
    		retVal = "Unable to get Localized Exception message ["+ex.getMessage()+"] exceptionTypeKey="+exceptionTypeKey.toString()+" exceptionErrorCode="+exceptionErrorCode;
    	}
    	return retVal ;
    }
    
    public String getMessage(){
    	return errorMessage;
    }
    
    public String getLocalizedMessage(){
    	return localizedErrorMessage;
    }
}
