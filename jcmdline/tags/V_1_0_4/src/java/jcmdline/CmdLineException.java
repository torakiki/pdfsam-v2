/*
 * CmdLineException.java
 *
 * Classes:
 *   public   CmdLineException
 *   
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the Java jcmdline (command line management) package.
 *
 * The Initial Developer of the Original Code is Lynne Lawrence.
 * 
 * Portions created by the Initial Developer are Copyright (C) 2002
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):  Lynne Lawrence <lgl@visuallink.com>
 *
 * ***** END LICENSE BLOCK *****
 */

package jcmdline;

import java.text.MessageFormat;

/**
 * An Exception that indicates a command line processing error.
 * <P>
 * Sample Usage:
 * <P>
 * To use this for an exception in package 'mypackage',
 * a properties file called 'errors.properties' might be created in 
 * directory 'mypackage' with the following properties:
 * <pre>
 *     # Contents of errors.properties file
 *
 *     PercentTooLow: \
 *     Percent must be greater than {0}, got {1}.
 * 
 *     PercentTooHigh: \
 *     Percent must be less than {0}, got {1}.
 * </pre>
 * <P>
 * Throwing an exception would be done as in the following:
 * <pre>
 * if (percent &gt; 100) {
 *     ResourceBundle rb = ResourceBundle.getBundle("mypackage.errors");
 *     String msg = rb.getString("PercentTooHigh");
 *     throw new CmdLineException(msg,
 *                                new Object[] { new Integer(100),
 *                                               new Integer(percent) });
 * }
 * </pre>
 * <P>
 * The package may now be modified to accomodate a French Locale by creating
 * a file 'errors_fr.properties' in directory 'mypackage' that contains all
 * messages in 'errors.properties', converted to French.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: CmdLineException.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public class CmdLineException extends Exception {

	private static final long serialVersionUID = 7450308469367315829L;

	/**
     * constructor
     *
     * @param message       message associated with the exception
     */
    public CmdLineException(String message) {
        super(message);
    }

    /**
     * constructor
     *
     * @param message       message associated with the exception
     * @param params        parameters to be plugged into <code>message</code>
     */
    public CmdLineException(String message, Object[] params) {
        super(MessageFormat.format(message, params));
    }
}
