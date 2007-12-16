/*
 * DefaultCmdLineHandler.java
 *
 * Classes:
 *   public   DefaultCmdLineHandler
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

import java.util.Collection;

/**
 * A Decorator class that implements command line options for the 
 * display of the command's usage.
 * These options are (using the default 'strings' properties file):
 *    <pre>
 *    -?         prints usage to stdout (optional)
 *    -h         prints usage to stdout (optional)
 *    -h!        prints usage (including hidden options) to stdout (optional)
 *               (hidden)
 *    </pre>
 * <P>
 * Should any of these options be specified by the user, the usage (with or
 * without the hidden options, as appropriate) will be displayed and 
 * System.exit(0) will be called.
 * <P>
 * Information on using CmdLineHandlers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: DefaultCmdLineHandler.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see Parameter
 * @see BasicCmdLineHandler
 * @see CmdLineParser
 */
public class DefaultCmdLineHandler extends AbstractHandlerDecorator {


    /**
     * a parameter that will cause the usage to be displayed
     */
    private BooleanParam usageParam1;

    /**
     * a second parameter that will cause the usage to be displayed
     */
    private BooleanParam usageParam2;

    /**
     * a parameter that will cause the usage to be displayed, including
     * hidden options
     */
    private BooleanParam hiddenUsageParam;

    /**
     * constructor
     *
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     */
    public DefaultCmdLineHandler (CmdLineHandler handler) {
        super(handler);
        usageParam1 = new BooleanParam(
            Strings.get("DefaultCmdLineHandler.usageOptTag1"),
            Strings.get("DefaultCmdLineHandler.usageOptDesc"));
        usageParam1.setIgnoreRequired(true);
        usageParam2 = new BooleanParam(
            Strings.get("DefaultCmdLineHandler.usageOptTag2"),
            Strings.get("DefaultCmdLineHandler.usageOptDesc"));
        usageParam2.setIgnoreRequired(true);
        hiddenUsageParam = new BooleanParam(
            Strings.get("DefaultCmdLineHandler.hiddenUsageOptTag"),
            Strings.get("DefaultCmdLineHandler.hiddenUsageOptDesc"),
            BooleanParam.HIDDEN);
        hiddenUsageParam.setIgnoreRequired(true);
        setCustomOptions(new Parameter[] { usageParam1, 
                                           usageParam2, 
                                           hiddenUsageParam });
    }

    /**
     * constructor - creates a new BasicCmdLineHandler as its delegate
     *
     * @param cmdName       the name of the command
     * @param cmdDesc       a short description of the command
     * @param options       a collection of Parameter objects, describing the
     *                      command's command-line options
     * @param args          a collection of Parameter objects, describing the
     *                      command's command-line arguments (what is left on 
     *                      the command line after all options and their 
     *                      parameters have been processed)
     * @param parser        a CmdLineParser to be used to parse the command line
     * @throws              IllegalArgumentException if any of the 
     *                      parameters are not correctly specified.
     * @see BasicCmdLineHandler
     */
    public DefaultCmdLineHandler (String cmdName, 
                           String cmdDesc, 
                           Parameter[] options,
                           Parameter[] args,
                           CmdLineParser parser) {

        this(new BasicCmdLineHandler(cmdName, cmdDesc, options, args, parser));
    }

    /**
     * constructor - creates a new BasicCmdLineHandler as its delegate
     *
     * @param cmdName       the name of the command
     * @param cmdDesc       a short description of the command
     * @param options       a collection of Parameter objects, describing the
     *                      command's command-line options
     * @param args          a collection of Parameter objects, describing the
     *                      command's command-line arguments (what is left on 
     *                      the command line after all options and their 
     *                      parameters have been processed)
     * @throws              IllegalArgumentException if any of the 
     *                      parameters are not correctly specified.
     * @see BasicCmdLineHandler
     */
    public DefaultCmdLineHandler (String cmdName, 
                           String cmdDesc, 
                           Parameter[] options,
                           Parameter[] args) {
        this(new BasicCmdLineHandler(cmdName, cmdDesc, options, args));
    }

    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param cmdName       the name of the command creating this 
     *                      BasicCmdLineHandler
     * @param cmdDesc       a short description of the command's purpose
     * @param options       a collection of Parameter objects, describing the
     *                      command's command-line options
     * @param args          a collection of Parameter objects, describing the
     *                      command's command-line arguments (what is left on 
     *                      the command line after all options and their 
     *                      parameters have been processed)
     * @throws              IllegalArgumentException if any of the 
     *                      parameters are not correctly specified.
     * @see                 #setCmdName(String) setCmdName()
     * @see                 #setCmdDesc(String) setCmdDesc()
     * @see                 #setOptions(Parameter[]) setOptions()
     * @see                 PosixCmdLineParser
     */
    public DefaultCmdLineHandler (String cmdName, 
                          String cmdDesc, 
                          Collection options,
                          Collection args) {
        this(new BasicCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * Called following the call to <code>parse()</code> of this class's
     * contained CmdLineHandler.  This method checks for its options even if
     * <code>parseStatus</code> is false.
     *
     * @param parseStatus   The result of the <code>parse()</code> call to this
     *                      class's contained CmdLineHandler.
     * @return  This method will call <code>System.exit(0)</code>, 
     *          rather than returning, if one of its
     *          supported options (<i>-h</i>, <i>-h!</i>, or <i>-?</i>) is 
     *          specified.  Otherwise, <code>parseStatus</code> is returned.
     */
    protected boolean processParsedOptions(boolean parseStatus) {
        if (usageParam1.isSet() || usageParam2.isSet()) {
            System.out.println(getUsage(false));
            System.exit(0);
        } else if (hiddenUsageParam.isSet()) {
            System.out.println(getUsage(true));
            System.exit(0);
        }
        return parseStatus;
    }

}
