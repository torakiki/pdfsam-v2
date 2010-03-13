/*
 * BasicCmdLineHandler.java
 *
 * Classes:
 *   public   BasicCmdLineHandler
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Used to define, parse, and validate the parameters associated with an 
 * executable's command line.
 * <P>
 * Objects of this class use a {@link CmdLineParser} to do the actual 
 * parsing of the command line.  By default, this class uses a
 * {@link PosixCmdLineParser}.
 * <p>
 * The following semantics are used throughout the documentation for this
 * class.  A command line <i>parameter</i> refers to either a command line 
 * <i>option</i>, or a command line <i>argument</i>.  
 * <P>
 * A command line <i>option</i> is identified by a <i>tag</i> and may or 
 * not have an associated value.  For instance, in the following posix-type
 * option, "outfile" is the <i>tag</i> and "/tmp/myoutfile" is the
 * <i>value</i>:
 * <pre>
 *  --outfile /tmp/myoutfile
 * </pre>
 * <P>Command line
 * <i>arguments</i> are what are left on the command line after all 
 * options have been processed.  For example, again using a posix style
 * command line, "filename1" and "filename2" would be the <i>arguments</i>:
 * <pre>
 *  -e 's/red/blue/' filename1 filename2
 * </pre>
 * <P>
 * Parameters may be designated as <i>hidden</i>.
 * <i>Hidden parameters</i> are those that can be specified, 
 * but whose documentation
 * is not displayed to the user when a normal usage is printed.
 * <P>
 * This class is used as in the following example of a 'cat' facsimile in java:
 * <pre>
 * public class Concat {
 *     static FileParam outfile = new FileParam(
 *             &quot;out&quot;,
 *             &quot;a file to receive the concatenated files (default is stdout)&quot;);
 *     static BooleanParam delete = new BooleanParam(
 *             &quot;delete&quot;,
 *             &quot;specifies that all of the original files are to be deleted&quot;);
 *     static FileParam infiles = new FileParam(
 *             &quot;filename&quot;,
 *             &quot;files to be concatenated&quot;,
 *             FileParam.IS_FILE &amp; FileParam.IS_READABLE,
 *             FileParam.REQUIRED,
 *             FileParam.MULTI_VALUED);
 * 
 *     public static void main(String[] args) {
 *         outfile.setOptionLabel(&quot;outfile&quot;);
 *         BasicCmdLineHandler clp = new BasicCmdLineHandler(
 *                 &quot;Concat&quot;, &quot;concatenates the specified files&quot;,
 *                 new Parameter[] { outfile, delete },
 *                 new Parameter[] { infiles });
 *         clp.parse(args);
 * 
 *         if (outfile.isSet()) {
 *             ....
 *         } else {
 *             ...
 *         }
 *         for (Iterator itr = infiles.getFiles().iterator(); itr.hasNext(); ) {
 *             ...
 *         }
 *         if (delete.isTrue()) {
 *             ...
 *         }
 *     }
 * }
 * 
 * </pre>
 * <P>
 * This class implements no options on its own.  It it typically used in
 * conjunction with one or more of the {@link AbstractHandlerDecorator}
 * classes that provide some useful options.
 * <P>
 * <b>Post Processing Command Line Parameters</b>
 * <P>
 * It may be the case that none of the supplied Parameter types fully
 * accomodates a program's needs.  For instance, a program may require
 * a filename option that is an html filename, ending with '.html'.  In
 * this case, the programmer has the options of creating their own 
 * Parameter subclass, or post-processing the returned FileParam parameter
 * and generating their own error message.  The 
 * {@link #exitUsageError(String) exitUsageError()} method is 
 * provided so that programs that post-process parameters can take
 * the same exit as would be taken for "normal" parameter processing
 * failures.  For instance, in the case just described, the following
 * code could be used to exit the program if the specified file did not
 * end with '.html' (<code>myfile</code> 
 * being a FileParam object, and <code>cl</code>
 * being the BasicCmdLineHandler object):
 * <pre>
 *    cl.parse(args);
 *    if (! myfile.getFile().getPath().endsWith(&quot;.html&quot;)) {
 *        cl.exitUsageError(&quot;Filename specified for '&quot; +
 *                          myfile.getTag() + &quot;' must end with '.html'&quot;);
 *    }
 * </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: BasicCmdLineHandler.java,v 1.4 2005/02/06 13:19:19 lglawrence Exp $
 * @see Parameter
 * @see CmdLineParser
 */
public class BasicCmdLineHandler implements CmdLineHandler {


    /**
     * the name of the command whose command line is to be parsed
     */
    private String cmdName;

    /**
     * a short description of the command's purpose
     */
    private String cmdDesc;

    /**
     * the options associated with the command
     */
    private HashMap options = new HashMap();

    /**
     * the arguments (after the options have been processed) associated 
     * with the command
     */
    private ArrayList args = new ArrayList();

    /**
     * the parser to be used to parse the command line
     * @see #setParser(CmdLineParser) setParser()
     * @see #getParser()
     */
    private CmdLineParser parser;

    /**
     * indicates that the command usage should be displayed, and System.exit(1)
     * called in the case of a parse error.  
     * @see     #setDieOnParseError(boolean) setDieOnParseError()
     */
    private boolean dieOnParseError = true;

    /**
     * the error message from the last call to parse()
     * @see #setParseError(String) setParseError()
     * @see #getParseError()
     */
    private String parseError;

    /**
     * constructor
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
     * @see                 #setCmdName(String) setCmdName()
     * @see                 #setCmdDesc(String) setCmdDesc()
     * @see                 #setOptions(Parameter[]) setOptions()
     * @see                 #setArgs(Parameter[]) setArgs()
     * @see                 #setParser(CmdLineParser) setParser()
     */
    public BasicCmdLineHandler (String cmdName, 
                           String cmdDesc, 
                           Parameter[] options,
                           Parameter[] args,
                           CmdLineParser parser) {

        setCmdName(cmdName);
        setCmdDesc(cmdDesc);
        setOptions(options);
        setArgs(args);
        setParser(parser);
    }

    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
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
     * @see                 #setCmdName(String) setCmdName()
     * @see                 #setCmdDesc(String) setCmdDesc()
     * @see                 #setOptions(Parameter[]) setOptions()
     * @see                 #setArgs(Parameter[]) setArgs()
     * @see                 PosixCmdLineParser
     */
    public BasicCmdLineHandler (String cmdName, 
                           String cmdDesc, 
                           Parameter[] options,
                           Parameter[] args) {
        this(cmdName, cmdDesc, options, args, new PosixCmdLineParser());
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
    public BasicCmdLineHandler (String cmdName, 
                          String cmdDesc, 
                          Collection options,
                          Collection args) {
        this(cmdName, 
             cmdDesc, 
             (Parameter[]) options.toArray(new Parameter[0]), 
             (Parameter[]) args.toArray(new Parameter[0]));
    }

    /**
     * Parse the specified command line arguments.  This method will fail if:
     * <ul>
     *    <li>the CmdLineParser is unable to parse the command line
     *        parameters into the required options and arguments.
     *    <li>a required Parameter has not been set by the parser.
     * </ul>
     *
     * @param clargs   command line arguments passed to the main() method
     *                 of BasicCmdLineHandler's creating class.
     * @return  If <code>dieOnParseError</code> is set to <code>false</code>,
     *          this method will return true if there are no parse errors.  If
     *          there are parse errors, <code>false</code>is returned and 
     *          an appropriate error message may be obtained by calling
     *          {@link #getParseError()}.
     *          <P>
     *          If <code>dieOnParseError</code> is set to <code>true</code>
     *          and the method fails, the program will exit with exit code 1
     *          after printing the usage to stderr.
     */
    public boolean parse(String[] clargs) {
        if (clargs == null) {
            clargs = new String[] {};
        }
        try {
            parser.parse(clargs, options, args);
            if (!canSkipRequiredCheck()) {
                checkForRequired();
            }
        } catch (CmdLineException e) {
            if (dieOnParseError) {
                exitUsageError(e.getMessage());
            } else {
                parseError = e.getMessage();
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the parser to be used to parse the command line.
     *
     * @param parser      the parser to be used to parse the command line
     * @see #getParser()
     */
    public void setParser(CmdLineParser parser) {
        this.parser = parser;
    }

    /**
     * Gets the parser to be used to parse the command line.
     *
     * @return              the parser to be used to parse the command line
     * @see #setParser(CmdLineParser) setParser()
     */
    public CmdLineParser getParser() {
        return parser;
    }

    /**
     * Sets a flag indicating that the program should exit in the case of
     * a parse error (after displaying the usage and an error message) -
     * defaults to <code>true</code>.
     *
     * @param val           <code>true</code> (the default) if the <code>
     *                      parse</code> method should call System.exit() in
     *                      case of a parse error, <code>false</code> if 
     *                      <code>parse()</code> should return to the user 
     *                      for error processing.
     * @see     #parse(String[]) parse()
     */
    public void setDieOnParseError(boolean val) {
        dieOnParseError = val;
    }

    /**
     * Gets a flag indicating that the program should exit in the case of
     * a parse error (after displaying the usage and an error message).
     *
     * @return              <code>true</code> (the default) if the <code>
     *                      parse</code> method should call System.exit() in
     *                      case of a parse error, <code>false</code> if 
     *                      <code>parse()</code> should return to the user 
     *                      for error processing.
     * @see     #parse(String[]) parse()
     */
    public boolean getDieOnParseError() {
        return dieOnParseError;
    }

    /**
     * Prints the usage, followed by the specified error message, to stderr
     * and exits the program with exit status = 1.  The error message will
     * be prefaced with 'ERROR: '.
     *
     * @param errMsg        the error message
     * @return              Doesn't return - exits the program with exit status
     *                      of 1.
     */
    public void exitUsageError(String errMsg) {
        System.err.println(getUsage(false));
        System.err.println(
            "\n" + parser.getUsageFormatter().formatErrorMsg(errMsg));
        quitProgram(1);
    }

    /**
     * sets the value of the arguments (what is left on the command line after
     * all options, and their parameters, have been processed) associated 
     * with the command
     *
     * @param args          A Collection of {@link Parameter} objects.  This may
     *                      be null if the command accepts no command line 
     *                      arguments.
     */
    public void setArgs(Parameter[] args) {
        this.args.clear();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                addArg(args[i]);
            }
        }
    }

    /**
     * Adds a command line arguement.  Command line arguments must be added
     * in the order that they will be specified on the command line.
     *
     * @param arg           the new command line argument
     * @throws  IllegalArgumentException if:
     *          <ul>
     *              <li><code>arg</code> is null 
     *              <li>the previously added argument was multi-valued (only 
     *                  the last argument can be multi-valued)
     *              <li><code>arg</code> is a required argument but the 
     *                  previous argument was optional
     *          </ul>
     */
    public void addArg(Parameter arg) {
        if (arg == null) {
            throw new IllegalArgumentException(Strings.get(
                "BasicCmdLineHandler.nullArgNotAllowed"));
        }
        if (args.size() > 0) {
            Parameter lastArg = (Parameter) args.get(args.size() - 1);
            if (lastArg.isMultiValued()) {
                throw new IllegalArgumentException(Strings.get(
                    "BasicCmdLineHandler.multiValueArgNotLast",
                    new Object[] { lastArg.getTag() }));
            }
            if (! arg.isOptional() && lastArg.isOptional()) {
                throw new IllegalArgumentException(Strings.get(
                    "BasicCmdLineHandler.requiredArgAfterOptArg",
                    new Object[] { arg.getTag(), lastArg.getTag() }));
            }
        }
        args.add(arg);
    }

    /**
     * gets the value of the arguments (what is left on the command line after
     * all options, and their parameters, have been processed) associated 
     * with the command
     *
     * @return              the command's options
     */
    public List getArgs() {
        return args;
    }

    /**
     * gets the argument specified by <code>tag</code>
     *
     * @param tag           identifies the argument to be returned
     * @return              The argument associated with <code>tag</code>.  
     *                      If no matching argument is found, null is returned.
     */
    public Parameter getArg(String tag) {
        Parameter arg;
        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            arg = (Parameter) itr.next();
            if (arg.getTag().equals(tag)) {
                return arg;
            }
        }
        return null;
    }

    /**
     * Sets the value of the options associated with the command
     *
     * @param options       A Collection of {@link Parameter} objects.  This may
     *                      be null if the command accepts no command line 
     *                      options.
     */
    public void setOptions(Parameter[] options) {
        this.options.clear();
        if (options != null) {
            for (int i = 0; i < options.length; i++) {
                addOption(options[i]);
            }
        }
    }

    /**
     * Adds a command line option.  
     *
     * @param opt           the new command line option
     * @throws              NullPointerException if <code>opt</code> is null.
     * @throws              IllegalArgumentException if an option with the 
     *                      same tag has already been added.
     */
    public void addOption(Parameter opt) {
        if (opt == null) {
            throw new NullPointerException();
        }
        if (options.containsKey(opt.getTag())) {
            throw new IllegalArgumentException(Strings.get(
                    "BasicCmdLineHandler.duplicateOption", 
                    new Object[] { opt.getTag() }));
        }
        options.put(opt.getTag().toLowerCase(), opt);
    }

    /**
     * gets the value of the options associated with the command
     *
     * @return              the command's options
     */
    public Collection getOptions() {
        return options.values();
    }

    /**
     * gets the option specified by <code>tag</code>
     *
     * @param tag           identifies the option to be returned
     * @return              the option associated with <code>tag</code>
     */
    public Parameter getOption(String tag) {
        return (Parameter) options.get(tag);
    }

    /**
     * sets a description of the command's purpose
     *
     * @param cmdDesc     a short description of the command's purpose
     * @throws              IllegalArgumentException if <code>cmdDesc
     *                      </code> is null or of 0 length.
     */
    public void setCmdDesc(String cmdDesc) {
        if (cmdDesc == null || cmdDesc.length() <= 0) {
            throw new IllegalArgumentException(Strings.get(
                    "BasicCmdLineHandler.cmdDescTooShort", 
                    new Object[] { cmdDesc }));
        }
        this.cmdDesc = cmdDesc;
    }

    /**
     * gets a description of the command's purpose
     *
     * @return              the command's description
     */
    public String getCmdDesc() {
        return cmdDesc;
    }

    /**
     * sets the value of the command name associated with this BasicCmdLineHandler
     *
     * @param cmdName       the name of the command associated with this 
     *                      BasicCmdLineHandler
     * @throws              IllegalArgumentException if cmdName is null,
     *                      or of 0 length
     */
    public void setCmdName(String cmdName) {
        if (cmdName == null || cmdName.length() <= 0) {
            throw new IllegalArgumentException(Strings.get(
                    "BasicCmdLineHandler.cmdNameTooShort", 
                    new Object[] { cmdName }));
        }
        this.cmdName = cmdName;
    }

    /**
     * gets the value of the command name associated with this 
     * BasicCmdLineHandler
     *
     * @return              the command name
     */
    public String getCmdName() {
        return cmdName;
    }

    /**
     * Gets the usage statement associated with the command.
     *
     * @param hidden        indicates whether hidden options are to be included
     *                      in the usage.
     * @return              the usage statement associated with the command
     */
    public String getUsage(boolean hidden) {
        return parser.getUsageFormatter()
                     .formatUsage(cmdName, cmdDesc, options, args, hidden);
    }

    /**
     * Sets the error message from the last call to parse().
     *
     * @param parseError      the error message from the last call to parse()
     * @see #getParseError()
     */
    public void setParseError(String parseError) {
        this.parseError = parseError;
    }

    /**
     * Gets the error message from the last call to parse().
     *
     * @return              the error message from the last call to parse()
     * @see #setParseError(String) setParseError()
     */
    public String getParseError() {
        return parseError;
    }

    /**
     * Indicates whether the check for required Parameters can be skipped
     * due to a Parameter with flag <code>ignoreRequired = true</code> 
     * having been set.
     *
     * @return              true if the check for required parameters may be
     *                      skipped
     */
    private boolean canSkipRequiredCheck() {
        Parameter p;
        for (Iterator itr = options.values().iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (p.getIgnoreRequired() && p.isSet()) {
                return true;
            }
        }
        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            p = (Parameter)itr.next();
            if (p.getIgnoreRequired() && p.isSet()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies that all required options and arguments have been specified.
     *
     * @throws  CmdLineException if a required option or argument is not set.
     */
    private void checkForRequired() throws CmdLineException {
        Parameter p;

        for (Iterator itr = options.values().iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (!p.isOptional() && !p.isSet()) {
                throw new CmdLineException(Strings.get(
                    "BasicCmdLineHandler.missingRequiredOpt", 
                    new Object[] { p.getTag() }));
            }
        }
        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (!p.isOptional() && !p.isSet()) {
                throw new CmdLineException(Strings.get(
                    "BasicCmdLineHandler.missingRequiredArg", 
                    new Object[] { p.getTag() }));
            }
        }
    }

    /**
     * Exits the program with the specified exit status.
     *
     * @param exitStatus    the program's exit status
     * @return              this method never returns - the program is
     *                      terminated
     */
    private void quitProgram(int exitStatus) {
        System.exit(exitStatus);
    }
}
