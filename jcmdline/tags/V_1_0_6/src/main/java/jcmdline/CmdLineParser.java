/*
 * CmdLineParser.java
 *
 * jcmdline Rel. @VERSION@ $Id: CmdLineParser.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   CmdLineParser
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
 * Interface desribing the API used between the CmdLineHandler and its 
 * command line parser.
 * <P>
 * Implementations of this interface are intended to be passed to the 
 * constructor of a CmdLineHandler, which will then use the implementation
 * to parse the command line to its composite elements.
 * <P>
 * Information on using CmdLineParsers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: CmdLineParser.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public interface CmdLineParser {

    /**
     * Parses the passed command line arguments into sets of options and
     * arguments.  Following a call to this method, the appropriate values
     * of the parameters and options will be set.
     *
     * @param clargs        the command line arguments
     * @param opts          the expected command line options
     * @param args          the expected command line arguments (what is left
     *                      on the command line after the options have been
     *                      processed.
     * @throws  CmdLineException if the <code>clargs</code> fail to parse
     *          properly into the expected options and arguments.
     */
    public void parse(String clargs[], Map opts, List args)
        throws CmdLineException;

    /**
     * Sets a usage formatter suitable for this CmdLineParser's format.
     *
     * @param usageFormatter    a usage formatter suitable for this 
     *                          CmdLineParser's format
     * @see #getUsageFormatter()
     */
    public void setUsageFormatter(UsageFormatter usageFormatter) ;

    /**
     * Gets a usage formatter suitable for this CmdLineParser's format.
     *
     * @return              a usage formatter suitable for this 
     *                      CmdLineParser's format
     * @see #setUsageFormatter(UsageFormatter) setUsageFormatter()
     */
    public UsageFormatter getUsageFormatter() ;

}
