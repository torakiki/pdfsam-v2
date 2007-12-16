/*
 * PosixCmdLineParser.java
 *
 * Classes:
 *   public   PosixCmdLineParser
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Used to parse the parameters associated with an executable's
 * command line.
 * <P>
 * The following semantics are used throughout the documentation for this
 * class.  A command line <i>parameter</i> refers to either a command line 
 * <i>option</i>, or a command line <i>argument</i>.  
 * <P>A command line <i>option</i> is 
 * preceded by either a '-',
 * or a '--', and may, optionally, have an associated value separated from the
 * option "tag" by a space or an '='.  Command line options end with the
 * first <i>parameter</i> (that has not already been parsed as an 
 * option or option value) that
 * does <b>not</b> start with a '-' or a '--', or when a '--' appears by itself 
 * as a parameter.  A '--' must be specified
 * alone to signal the end of options when the 
 * first command line <i>argument</i> starts with a '-'.
 * <P>Command line
 * <i>arguments</i> are what are left on the command line after all 
 * options have been processed.
 * <P>
 * This class is used as in the following example of a 'cat' facsimile in java:
 * <P>
 * <b>Command Line Parsing</b>
 * <P>
 * The {@link #parse(String[], Map, List) parse()} 
 * method parses option tags in a 
 * case insensitive manner.
 * It will accept truncated option tags as long as the tag remains
 * un-ambiguous, execpt for hidden options.  The tag for hidden options
 * must be fully specified.  An option's value may be separated from its'
 * tag by a space or an '='.  A BooleanParam may be specified either without
 * a value (in which case it is set to <code>true</code>), or with an '='
 * followed by its value.  If a BooleanParam is specified more than once,
 * the final specification takes precedence.
 * <P>
 * The following command lines are all equivalent:
 * <pre>
 * java Concat -delete -out myoutfile infile1 infile2
 * java Concat -d -o myoutfile infile1 infile2
 * java Concat -delete=true -o myoutfile infile1 infile2
 * java Concat -d=true -o=myoutfile infile1 infile2
 * java Concat -Delete -OUT myoutfile infile1 infile2
 * </pre>
 * Any problem found while <code>parse()</code> processes the command
 * line will cause a {@link CmdLineException} to be thrown.
 * <P>
 * Information on using CmdLineParsers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: PosixCmdLineParser.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see Parameter
 */
public class PosixCmdLineParser implements CmdLineParser {


    /**
     * a usage formatter suitable for this CmdLineParser's format
     * @see #setUsageFormatter(UsageFormatter) setUsageFormatter()
     * @see #getUsageFormatter()
     */
    private UsageFormatter usageFormatter;

    /**
     * constructor
     */
    public PosixCmdLineParser () {
        setUsageFormatter(new TextUsageFormatter());
    }

    /**
     * parse the specified command line arguments
     *
     * @param params   command line arguments passed to the main() method
     *                 of CmdLineParser's creating class.
     * @return  This method will exit, rather than returning, if one of the
     *          following conditions is met:
     *          <ul>
     *          <li><i>-help</i>,
     *              <i>-help!</i>,
     *              <i>-version</i>,
     *              <i>-h</i>, or
     *              <i>-?</i>,
     *              are amongst the command line arguments - the
     *              appropriate information is displayed on stdout,
     *              and the program exits with status 0.
     *          <li>a command line argument is incorrectly specified -
     *              an error message is displayed and the program
     *              exits with status 1.
     *          <li>a required command line argument is missing - an error 
     *              message is displayed and the program exits with status 1.
     *          </ul>
     * @throws  CmdLineException in case of any parse error.
     */
    public void parse(String[] clargs, Map opts, List args) 
            throws CmdLineException {
        if (clargs == null) {
            clargs = new String[] {};
        }
        int i = processOptions(clargs, opts);
        processArguments(i, clargs, args);
    }

    /**
     * Sets a usage formatter suitable for this CmdLineParser's format.
     *
     * @param usageFormatter    a usage formatter suitable for this 
     *                          CmdLineParser's format
     * @see #getUsageFormatter()
     */
    public void setUsageFormatter(UsageFormatter usageFormatter) {
        this.usageFormatter = usageFormatter;
    }

    /**
     * Gets a usage formatter suitable for this CmdLineParser's format.
     *
     * @return              a usage formatter suitable for this 
     *                      CmdLineParser's format
     * @see #setUsageFormatter(UsageFormatter) setUsageFormatter()
     */
    public UsageFormatter getUsageFormatter() {
        return usageFormatter;
    }

    /**
     * Processes the command line options.
     * <P>This method has package visibility in order that it may be called
     * by unit tests.
     *
     * @param params        the command line arguments
     * @return              the number of command line arguments that were
     *                      "used up" in option processing
     * @throws              CmdLineException if any processing errors are 
     *                      encountered.
     */
     private int processOptions(String[] params, Map options) 
            throws CmdLineException {
        String tag;
        String val;
        int equalsIdx;
        Parameter p;
        int tagIdx;
        int i;
        for (i = 0; i < params.length; i++) {
            val = null;

            if (params[i].equals("--")) {  // end of options
                i++;
                break;      
            } else if (params[i].startsWith("-")) {   // have an option
                tagIdx = 1; 
                if (params[i].startsWith("--")) {
                    tagIdx = 2;
                }
                if (params[i].length() == tagIdx) {
                    throw new CmdLineException(
                        Strings.get("PosixCmdLineParser.optionNoTag"));
                }
                tag = params[i].substring(tagIdx);
                // See if we have an option specified as <tag>=<value>
                if ((equalsIdx = tag.indexOf("=")) != -1) {
                    val = tag.substring(equalsIdx + 1);
                    tag = tag.substring(0, equalsIdx);
                }
                p = findMatchingOption(tag, options);
                if (p instanceof OptionTakesNoValue) {
                    if (val == null) {
                        val = ((OptionTakesNoValue) p).getDefaultValue();
                    }
                } else if (val == null) {
                    if (i == params.length - 1) {
                        throw new CmdLineException(
                            Strings.get("PosixCmdLineParser.missingOptionValue",
                                        new Object[] { tag }));
                    }
                    val = params[++i];
                }
                p.addValue(val);
            } else {
                break;      // end of options
            }
        }
        return i;
    }

    /**
     * processes the command line arguments (what is left on the command line
     * after all options and their values have been processed)
     *
     * @param stIdx         the index into <code>params</code> where processing
     *                      is to start
     * @param params        the command line parameters
     * @throws              CmdLineException if any processing errors are 
     *                      encountered.
     */
    private void processArguments(int stIdx, String[] params, List args) 
            throws CmdLineException {
        int argIdx = 0;
        Parameter p;
        for (int pIdx = stIdx; pIdx < params.length; pIdx++) {
            if (argIdx >= args.size()) {
                throw new CmdLineException(
                        Strings.get("PosixCmdLineParser.extraArg", 
                                    new Object[] { params[pIdx] }));
            }
            p = (Parameter) args.get(argIdx);
            p.addValue(params[pIdx]);
            if (! p.isMultiValued()) {
                argIdx++;
            }
        }
    }

    /**
     * Find an option that matches the specified tag.  The tag may be an
     * abbreviation for the option.  Abbreviations will work as long as they
     * are unique enough to match one, and only one, option.  Comparison is
     * done in a case-insensitive manner.
     *
     * @param tag           the option tag to be matched
     * @return              The associated Parameter object.  If a matching 
     *                      Parameter
     *                      is not found, this method will issue the appropriate
     *                      error message and the program will exit with 
     *                      exit status 1.
     * @throws  CmdLineException if an option tag is ambiguous or not defined.
     */
    private Parameter findMatchingOption(String tag, Map options) 
            throws CmdLineException {
        String lctag = tag.toLowerCase();
        String fulltag = null;
        String tmptag;
        if (options.containsKey(lctag)) {
            return (Parameter) options.get(lctag);
        }
        for (Iterator itr = options.keySet().iterator(); itr.hasNext(); ) {
            tmptag = (String)itr.next();
            if (((Parameter) options.get(tmptag)).isHidden()) {
                // hidden options must be fully specified
                continue;
            }
            if (tmptag.startsWith(lctag)) {
                if (fulltag != null) {
                    throw new CmdLineException(
                        Strings.get("PosixCmdLineParser.ambiguousOption",
                                    new Object[] { "-" + tag }));
                }
                fulltag = tmptag;
            }
        }
        if (fulltag == null) {
            throw new CmdLineException(
                Strings.get("PosixCmdLineParser.invalidOption",
                            new Object[] { tag }));
        }
        return (Parameter)options.get(fulltag);
    }
}
