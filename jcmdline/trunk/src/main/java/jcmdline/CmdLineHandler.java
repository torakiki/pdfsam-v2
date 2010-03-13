/*
 * CmdLineHandler.java
 *
 * Classes:
 *   public   CmdLineHandler
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
 * Interface that describes the API for a command line handler.  A command
 * line handler is an object that is passed information concerning the 
 * required structure of command's command line and then can be used to
 * parse the command line, taking action as configured.
 * <P>
 * Information on using CmdLineHandlers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: CmdLineHandler.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see Parameter
 * @see CmdLineParser
 * @see UsageFormatter
 */
public interface CmdLineHandler {

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
    public void setDieOnParseError(boolean val) ;

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
    public boolean getDieOnParseError() ;

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
    public boolean parse(String[] clargs) ;

    /**
     * Sets the parser to be used to parse the command line.
     *
     * @param parser      the parser to be used to parse the command line
     * @see #getParser()
     */
    public void setParser(CmdLineParser parser) ;

    /**
     * Gets the parser to be used to parse the command line.
     *
     * @return              the parser to be used to parse the command line
     * @see #setParser(CmdLineParser) setParser()
     */
    public CmdLineParser getParser() ;

    /**
     * sets the value of the arguments (what is left on the command line after
     * all options, and their parameters, have been processed) associated 
     * with the command
     *
     * @param args          A Collection of {@link Parameter} objects.  This may
     *                      be null if the command accepts no command line 
     *                      arguments.
     */
    public void setArgs(Parameter[] args) ;

    /**
     * Adds a command line arguement.
     *
     * @param arg           the new command line argument
     * @throws              IllegalArgumentException if <code>arg</code>
     *                      is null.
     */
    public void addArg(Parameter arg) ;

    /**
     * gets the value of the arguments (what is left on the command line after
     * all options, and their parameters, have been processed) associated 
     * with the command
     *
     * @return              the command's options
     */
    public List getArgs() ;

    /**
     * gets the argument specified by <code>tag</code>
     *
     * @param tag           identifies the argument to be returned
     * @return              The argument associated with <code>tag</code>.  
     *                      If no matching argument is found, null is returned.
     */
    public Parameter getArg(String tag) ;

    /**
     * Sets the value of the options associated with the command
     *
     * @param options       A Collection of {@link Parameter} objects.  This may
     *                      be null if the command accepts no command line 
     *                      options.
     */
    public void setOptions(Parameter[] options) ;

    /**
     * Adds a command line option.
     *
     * @param opt           the new command line option
     * @throws              IllegalArgumentException if the tag associated with
     *                      <code>opt</code> has already been defined for an
     *                      option.
     */
    public void addOption(Parameter opt) ;

    /**
     * gets the value of the options associated with the command
     *
     * @return              the command's options
     */
    public Collection getOptions() ;

    /**
     * gets the option specified by <code>tag</code>
     *
     * @param tag           identifies the option to be returned
     * @return              the option associated with <code>tag</code>
     */
    public Parameter getOption(String tag) ;

    /**
     * sets a description of the command's purpose
     *
     * @param cmdDesc     a short description of the command's purpose
     * @throws              IllegalArgumentException if <code>cmdDesc
     *                      </code> is null or of 0 length.
     */
    public void setCmdDesc(String cmdDesc) ;

    /**
     * gets a description of the command's purpose
     *
     * @return              the command's description
     */
    public String getCmdDesc() ;

    /**
     * sets the value of the command name associated with this CmdLineHandler
     *
     * @param cmdName       the name of the command associated with this 
     *                      CmdLineHandler
     * @throws              IllegalArgumentException if cmdName is null,
     *                      or of 0 length
     */
    public void setCmdName(String cmdName) ;

    /**
     * gets the value of the command name associated with this CmdLineHandler
     *
     * @return              the command name
     */
    public String getCmdName() ;

    /**
     * Gets the usage statement associated with the command.
     *
     * @param hidden        indicates whether hidden options are to be included
     *                      in the usage.
     * @return              the usage statement associated with the command
     */
    public String getUsage(boolean hidden) ;

    /**
     * Sets the error message from the last call to parse().
     *
     * @param parseError      the error message from the last call to parse()
     * @see #getParseError()
     */
    public void setParseError(String parseError) ;

    /**
     * Gets the error message from the last call to parse().
     *
     * @return              the error message from the last call to parse()
     * @see #setParseError(String) setParseError()
     */
    public String getParseError() ;

    /**
     * Prints the usage, followed by the specified error message, to stderr
     * and exits the program with exit status = 1.  The error message will
     * be prefaced with 'ERROR: '.
     *
     * @param errMsg        the error message
     * @return              Doesn't return - exits the program with exit status
     *                      of 1.
     */
    public void exitUsageError(String errMsg) ;
}
