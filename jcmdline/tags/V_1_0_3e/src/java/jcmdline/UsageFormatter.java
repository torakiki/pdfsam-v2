/*
 * UsageFormatter.java
 *
 * Classes:
 *   public   UsageFormatter
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

import java.util.List;
import java.util.Map;

/**
 * Used to format a command's usage.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: UsageFormatter.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see CmdLineHandler
 */
public interface UsageFormatter {

    /**
     * gets the usage for the command
     *
     * @param showHidden    if <code>true</code>, hidden parameters will be
     *                      displayed
     * @return              the usage for the command
     */
    public String formatUsage(String cmdName,
                              String cmdDesc,
                              Map opts,
                              List args,
                              boolean showHidden) ;

    /**
     * Gets an error message, reformatted in a manner to "go well with"
     * the usage statement.  For instance,
     * <pre>
     *    formatErrorMsg("invalid filename")
     * </pre>
     * Might return:
     * <pre>
     *    <b>ERROR:</b> invalid filename
     * </pre>
     *
     * @param msg           the text of the error message
     * @return              the reformatted error message
     */
    public String formatErrorMsg(String msg) ;

    /**
     * Sets the maximum line length to use for usage display.  The maximum
     * line length defaults to 80 if this method is not called to set it
     * otherwise.
     *
     * @param lineLength      the maximum line length to use for usage display
     * @see #getLineLength()
     */
    public void setLineLength(int lineLength) ;

    /**
     * Gets the maximum line length to use for usage display.
     *
     * @return              the maximum line length to use for usage display
     * @see #setLineLength(int) setLineLength()
     */
    public int getLineLength() ;

}

