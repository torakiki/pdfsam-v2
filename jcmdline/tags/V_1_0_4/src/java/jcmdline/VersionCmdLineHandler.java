/*
 * VersionCmdLineHandler.java
 *
 * Classes:
 *   public   VersionCmdLineHandler
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
 * A CmdLineHandler Decorator class that implements a -version option.
 * <P>
 * The implemented option is a BooleanParam whose tag is defined by
 * "VersionCmdLineHandler.version.tag" in the <i>strings.properties</i>
 * file (set to "version", in English).
 * <P>
 * Should the user specify this option on the command line, the command's
 * version is printed to stdout, and <code>System.exit(0)</code> is called.
 * <P>
 * Information on using CmdLineHandlers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: VersionCmdLineHandler.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see CmdLineHandler
 * @see AbstractHandlerDecorator
 */
public class VersionCmdLineHandler extends AbstractHandlerDecorator {

    /**
     * a parameter that will cause the usage to be displayed
     */
    private BooleanParam versionOpt;

    /**
     * the command's version
     */
    private String version;

    /**
     * constructor
     *
     * @param version       the command's version
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     * @throws  IllegalArgumentException if <code>version</code> is null or
     *          empty.
     */
    public VersionCmdLineHandler (String version, CmdLineHandler handler) {
        super(handler);
        if (version == null || version.length() == 0) {
            throw new IllegalArgumentException(Strings.get(
                "VersionCmdLineHandler.versionEmptyError"));
        }
        this.version = version;
        versionOpt = new BooleanParam(
            Strings.get("VersionCmdLineHandler.version.tag"),
            Strings.get("VersionCmdLineHandler.version.desc"));
        versionOpt.setIgnoreRequired(true);
        setCustomOptions(new Parameter[] { versionOpt });
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param version       the command's version
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
     * @see DefaultCmdLineHandler
     */
    public VersionCmdLineHandler (String version,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Parameter[] options,
                                  Parameter[] args,
                                  CmdLineParser parser) {

        this(version,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args, parser));
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param version       the command's version
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
     * @see DefaultCmdLineHandler
     */
    public VersionCmdLineHandler (String version,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Parameter[] options,
                                  Parameter[] args) {
        this(version, 
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }

    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param version       the command's version
     * @param cmdName       the name of the command creating this 
     *                      DefaultCmdLineHandler
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
    public VersionCmdLineHandler (String version,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Collection options,
                                  Collection args) {
        this(version, 
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * Called following the call to <code>parse()</code> of this class's
     * contained CmdLineHandler.  This method only checks for its option if
     * <code>parseStatus</code> is true.
     *
     * @param parseStatus   The result of the <code>parse()</code> call to this
     *                      class's contained CmdLineHandler.
     * @return  This method will call <code>System.exit(0)</code>, rather 
     *          than returning, if its option is set.
     *          Otherwise, <code>parseStatus</code> is returned.
     */
    protected boolean processParsedOptions(boolean parseOk) {
        if (parseOk) {
            if (versionOpt.isTrue()) {
                System.out.println(version);
                System.exit(0);
            }
        }
        return parseOk;
    }
}
