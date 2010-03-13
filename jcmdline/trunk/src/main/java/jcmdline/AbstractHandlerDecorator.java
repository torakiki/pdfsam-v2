/*
 * AbstractHandlerDecorator.java
 *
 * Classes:
 *   public   AbstractHandlerDecorator
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
import java.util.List;

/**
 * An abstract class implementing the Decorator design pattern for 
 * decoration of CmdLineHandler subclasses.  This 
 * class implements all methods of the CmdLineHandler interface by 
 * delegating them to a contained instance of CmdLineHandler.
 * The intended use for this class is specification and implementation of
 * a command option that can be reused, and can be used in series with
 * others of the same ilk.  More information on CmdLineHandlers
 * and decorator classes can be found in the 
 * <a href="doc-files/userguide.html#clh">
 * jcmdline User Guide</a>.
 * <P>
 * <b>Subclassing this Class</b>
 * <p>
 * In order to subclass this class, do the following:
 * <ul>
 *    <li>With regard to the subclass constructor(s):
 *        <ul>
 *        <li>All constructors should accept a <code>CmdLineHandler</code>
 *            argument or create a <code>CmdLineHandler</code> to which
 *            most method calls will be delegated.  
 *        <li>They should include a call to this class's 
 *            constructor, passing their contained <code>CmdLineHandler</code>
 *            along as an argument (<code>super(handler)</code>) as
 *            the first line of the constructor.
 *        <li>In the constructor of the subclass, call setCustomOptions, 
 *            specifying the options supported by the subclass.  This ensures 
 *            that the options are added to the contained handler, and that 
 *            they will be restored should setOptions() be called on the 
 *            contained handler.
 *        </ul>
 *    <li>Implement the processParsedOptions() method.  This method will be
 *        called after the command line has been parsed by the contained
 *        handler.  It is in this method that a subclass should do its
 *        option processing.
 * </ul>
 * An example of a subclass follows (note that, but for the lack of additional
 * constructors, this is basically how the {@link VersionCmdLineHandler}
 * is implemented).
 * <pre>
 *  public class VersionCmdLineHandler 
 *          extends AbstractHandlerDecorator {
 *  
 *      private BooleanParam versionOpt;
 *      private String version;
 *  
 *      public MyCmdLineHandler (String version, 
 *                               CmdLineHandler handler) {
 *          super(handler);
 *          if (version == null || version.length() == 0) {
 *              throw new IllegalArgumentException(
 *                          "version must be specified");
 *          }
 *          this.version = version;
 *          versionOpt = new BooleanParam(
 *              Strings.get("version"),
 *              Strings.get("displays the version and exits"));
 *          versionOpt.setIgnoreRequired(true);
 *          setCustomOptions(new Parameter[] { versionOpt });
 *      }
 *  
 *      protected boolean processParsedOptions(boolean parseOk) {
 *          if (parseOk) {
 *              if (versionOpt.isTrue()) {
 *                  System.out.println(version);
 *                  System.exit(0);
 *              }
 *          }
 *          return parseOk;
 *      }
 *  }
 * </pre>
 * <p>
 * <b>A Note on Option Processing</b>
 * <p>
 * CmdLineHandler decorator classes are particularly useful for options
 * that perform a task and call System.exit(), such as -help or 
 * -version options.  Options such as these should have the <code>
 * ignoreRequired</code> attribute set to <code>true</code>.  If not,
 * and the command has required parameters, the option will never
 * be accepted unless the required parameters are also specified.
 * <p>
 * <b>Notes on the {@link #processParsedOptions(boolean) processParsedOptions()}
 * method</b>
 * <p>
 * The following should be kept in mind when coding the <code>
 * processParsedOptions()</code> method:
 * <ul>
 *    <li>This method may never be reached.  If the <code>dieOnParseError</code>
 *        option is set <code>true</code> for the contained CmdLineHandler
 *        (which it is by default), a parse error will cause the program
 *        to exit before calling any <code>processParsedOptions()</code> 
 *        methods.
 *    <li>If this method post-processes its option value and finds it to
 *        be in error, it should either call {@link #exitUsageError(String)
 *        exitUsageError()} or return false, as appropriate based on 
 *        the value returned by {@link #getDieOnParseError()}.  For example:
 *        <pre>
 *    if ((errorMsg = postProcess(myOpt.getValue()) != null) {
 *        // oops - have an error condition
 *        if (getDieOnParseError()) {
 *            exitUsageError(errorMsg);
 *        } else {
 *            return false;
 *        }
 *    }
 *        </pre>
 *    <li>If this method does not call <code>System.exit()</code> or return
 *        its own error, it should
 *        always return the parse error that was passed to it.
 * </ul>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: AbstractHandlerDecorator.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public abstract class AbstractHandlerDecorator implements CmdLineHandler {

    /**
     * the instance of CmdLineHandler this class decorates.
     */
    private CmdLineHandler handler;

    /**
     * options specific to a subclass
     * @see #setCustomOptions(Parameter[]) setCustomOptions()
     * @see #getCustomOptions()
     */
    private Parameter[] customOptions;

    /**
     * constructor
     *
     * @param handler       the CmdLineHandler to which most methods will be
     *                      delegated
     */
    protected AbstractHandlerDecorator(CmdLineHandler handler) {
        this.handler = handler;
    }

    /**
     * Sets options specific to a subclass.
     *
     * @param customOptions      options specific to a subclass
     * @see #getCustomOptions()
     */
    protected void setCustomOptions(Parameter[] customOptions) {
        if (customOptions == null) {
            customOptions = new Parameter[] {};
        }
        this.customOptions = customOptions;
        for (int i = 0; i < customOptions.length; i++) {
            handler.addOption(customOptions[i]);
        }
    }

    /**
     * Gets options specific to a subclass.
     *
     * @return              options specific to a subclass
     * @see #setCustomOptions(Parameter[]) setCustomOptions()
     */
    protected Parameter[] getCustomOptions() {
        return customOptions;
    }

    /**
     * Called from the parse() method after the command line has been
     * parsed.  This is where a subclass performs processing specific to
     * its custom options.
     *
     * @param parseStatus   the results of the parse() call. Note that 
     *                      if <code>dieOnParseError</code> is set, or some
     *                      other AbstractHandlerDecorator exits first, this
     *                      method may never be called.
     * @return              <code>true</code> if there were no problems 
     *                      concerning the custom options, else
     *                      <code>false</code>.  The return value from this
     *                      method will be returned from 
     *                      {@link #parse(String[]) parse()}.
     */
    protected abstract boolean processParsedOptions(boolean parseStatus) ;

    /**
     * Sets a flag indicating that the program should exit in the case of
     * a parse error (after displaying the usage and an error message).
     * This flag defaults to <code>true</code>.
     *
     * @param val           <code>true</code> (the default) if the <code>
     *                      parse</code> method should call System.exit() in
     *                      case of a parse error, <code>false</code> if 
     *                      <code>parse()</code> should return to the user 
     *                      for error processing.
     * @see     #parse(String[]) parse()
     */
    public void setDieOnParseError(boolean val) {
        handler.setDieOnParseError(val);
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
        return handler.getDieOnParseError();
    }

    /**
     * parse the specified command line arguments
     *
     * @param clargs   command line arguments passed to the main() method
     *                 of CmdLineHandler's creating class.
     * @return  This method will exit, rather than returning, if one of the
     *          following conditions is met:
     *          <ul>
     *          <li><i>-h</i>, or
     *              <i>-h!</i>, or
     *              <i>-?</i>,
     *              are amongst the command line arguments - the
     *              appropriate information is displayed on stdout,
     *              and the program exits with status 0.
     *          <li>OR, dieOnParseError is set to true AND:
     *          <ul>
     *              <li>a command line argument is incorrectly specified -
     *                  an error message is displayed and the program
     *                  exits with status 1.
     *              <li>a required command line argument is missing - an error 
     *                  message is displayed and the program exits with status 1.
     *          </ul>
     *          </ul>
     *          If <code>dieOnParseError</code> is set to <code>false</code>,
     *          this method will return true if there are no parse errors.  If
     *          there are parse errors, <code>false</code>is returned and 
     *          an appropriate error message may be obtained by calling
     *          {@link #getParseError()}.
     */
    public boolean parse(String[] clargs) {
        boolean parseStatus =  handler.parse(clargs);
        return processParsedOptions(parseStatus);
    }

    /**
     * Sets the parser to be used to parse the command line.
     *
     * @param parser      the parser to be used to parse the command line
     * @see #getParser()
     */
    public void setParser(CmdLineParser parser) {
        handler.setParser(parser);
    }

    /**
     * Gets the parser to be used to parse the command line.
     *
     * @return              the parser to be used to parse the command line
     * @see #setParser(CmdLineParser) setParser()
     */
    public CmdLineParser getParser() {
        return handler.getParser();
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
        handler.setArgs(args);
    }

    /**
     * Adds a command line arguement.
     *
     * @param arg           the new command line argument
     * @throws              IllegalArgumentException if <code>arg</code>
     *                      is null.
     */
    public void addArg(Parameter arg) {
        handler.addArg(arg);
    }

    /**
     * gets the value of the arguments (what is left on the command line after
     * all options, and their parameters, have been processed) associated 
     * with the command
     *
     * @return              the command's options
     */
    public List getArgs() {
        return handler.getArgs();
    }

    /**
     * gets the argument specified by <code>tag</code>
     *
     * @param tag           identifies the argument to be returned
     * @return              The argument associated with <code>tag</code>.  
     *                      If no matching argument is found, null is returned.
     */
    public Parameter getArg(String tag) {
        return handler.getArg(tag);
    }

    /**
     * Sets the value of the options associated with the command
     *
     * @param options       A Collection of {@link Parameter} objects.  This may
     *                      be null if the command accepts no command line 
     *                      options.
     */
    public void setOptions(Parameter[] options) {
        handler.setOptions(options);
        for (int i = 0; i < customOptions.length; i++) {
            handler.addOption(customOptions[i]);
        }
    }

    /**
     * Adds a command line option.  If an option with same tag has already been
     * added to this CmdLineHandler, this new option will override the old.
     *
     * @param opt           the new command line option
     * @throws              IllegalArgumentException if the tag associated with
     *                      <code>opt</code> has already been defined for an
     *                      option.
     */
    public void addOption(Parameter opt) {
        handler.addOption(opt);
    }

    /**
     * gets the value of the options associated with the command
     *
     * @return              the command's options
     */
    public Collection getOptions() {
        return handler.getOptions();
    }

    /**
     * gets the option specified by <code>tag</code>
     *
     * @param tag           identifies the option to be returned
     * @return              the option associated with <code>tag</code>
     */
    public Parameter getOption(String tag) {
        return handler.getOption(tag);
    }

    /**
     * sets a description of the command's purpose
     *
     * @param cmdDesc     a short description of the command's purpose
     * @throws              IllegalArgumentException if <code>cmdDesc
     *                      </code> is null or of 0 length.
     */
    public void setCmdDesc(String cmdDesc) {
        handler.setCmdDesc(cmdDesc);
    }

    /**
     * gets a description of the command's purpose
     *
     * @return              the command's description
     */
    public String getCmdDesc() {
        return handler.getCmdDesc();
    }

    /**
     * sets the value of the command name associated with this CmdLineHandler
     *
     * @param cmdName       the name of the command associated with this 
     *                      CmdLineHandler
     * @throws              IllegalArgumentException if cmdName is null,
     *                      or of 0 length
     */
    public void setCmdName(String cmdName) {
        handler.setCmdName(cmdName);
    }

    /**
     * gets the value of the command name associated with this CmdLineHandler
     *
     * @return              the command name
     */
    public String getCmdName() {
        return handler.getCmdName();
    }

    /**
     * Gets the usage statement associated with the command.
     *
     * @param hidden        indicates whether hidden options are to be included
     *                      in the usage.
     * @return              the usage statement associated with the command
     */
    public String getUsage(boolean hidden) {
        return handler.getUsage(hidden);
    }

    /**
     * Sets the error message from the last call to parse().
     *
     * @param parseError      the error message from the last call to parse()
     * @see #getParseError()
     */
    public void setParseError(String parseError) {
        handler.setParseError(parseError);
    }

    /**
     * Gets the error message from the last call to parse().
     *
     * @return              the error message from the last call to parse()
     */
    public String getParseError() {
        return handler.getParseError();
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
        handler.exitUsageError(errMsg);
    }
}
