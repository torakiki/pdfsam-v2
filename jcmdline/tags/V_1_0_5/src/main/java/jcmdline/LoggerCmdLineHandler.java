/*
 * LoggerCmdLineHandler.java
 *
 * Classes:
 *   public   LoggerCmdLineHandler
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

import java.io.OutputStream;
import java.util.Collection;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * A CmdLineHandler Decorator class that implements a logging option that
 * implements rudimentary support for the java.util.logging package.
 * <P>
 * The implemented option is a StringParam whose tag is defined by
 * "LoggerCmdLineHandler.logOpt.tag" in the <i>strings.properties</i>
 * file (set to "log", in English).
 * The acceptable values for the parameter are set to the localized
 * strings that are valid logging levels as defined by the <code>
 * java.util.logging.Level</code> class.
 * <p>
 * Should the user set this option
 * a StreamHandler is added to the root logger and the 
 * logging level of the root logger and the StreamHandler are set
 * to that specified on the command line.  It is possible to set a 
 * Formatter for the log messages (the default is <code>
 * java.util.logging.SimpleFormatter</code>) with the 
 * {@link #setLogFormatter(Formatter) setLogFormatter()} method.  
 * This method, if used, must be called prior to any calls to 
 * <code>parse()</code>.
 * <P>
 * Information on using CmdLineHandlers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: LoggerCmdLineHandler.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see CmdLineHandler
 * @see AbstractHandlerDecorator
 * @see java.util.logging.Logger
 */
public class LoggerCmdLineHandler extends AbstractHandlerDecorator {

    /**
     * a parameter that will cause the root logger level to be set
     */
    private Parameter logOpt;

    /**
     * the OutputStream to which to write log messages
     */
    private OutputStream stream;

    /**
     * the formatter for log messages, defaults to 
     * <code>java.util.logging.SimpleFormatter</code>
     * @see #setLogFormatter(Formatter) setLogFormatter()
     * @see #getLogFormatter()
     */
    private Formatter logFormatter;

    /**
     * constructor
     *
     * @param stream        the OutputStream to which to write log messages
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     * @throws  IllegalArgumentException if <code>stream</code> is null
     */
    public LoggerCmdLineHandler (OutputStream stream, CmdLineHandler handler) {
        super(handler);
        if (stream == null) {
            throw new IllegalArgumentException(Strings.get(
                "LoggerCmdLineHandler.streamNullError"));
        }
        this.stream = stream;
        String[] validVals = new String[] { 
            Level.ALL.getLocalizedName(),
            Level.OFF.getLocalizedName(),
            Level.FINEST.getLocalizedName(),
            Level.FINER.getLocalizedName(),
            Level.FINE.getLocalizedName(),
            Level.CONFIG.getLocalizedName(),
            Level.INFO.getLocalizedName(),
            Level.WARNING.getLocalizedName(),
            Level.SEVERE.getLocalizedName()};
        logFormatter = new SimpleFormatter();
        logOpt = new  StringParam(
            Strings.get("LoggerCmdLineHandler.logOpt.tag"),
            Strings.get("LoggerCmdLineHandler.logOpt.desc", validVals),
            validVals,
            Parameter.OPTIONAL);
        setCustomOptions(new Parameter[] { logOpt });
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param stream        the OutputStream to which to write log messages
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
    public LoggerCmdLineHandler ( OutputStream stream,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Parameter[] options,
                                  Parameter[] args,
                                  CmdLineParser parser) {

        this(stream, new DefaultCmdLineHandler(
                cmdName, cmdDesc, options, args, parser));
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param stream        the OutputStream to which to write log messages
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
    public LoggerCmdLineHandler ( OutputStream stream,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Parameter[] options,
                                  Parameter[] args) {
        this(stream, 
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }

    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param stream        the OutputStream to which to write log messages
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
    public LoggerCmdLineHandler ( OutputStream stream,
                                  String cmdName, 
                                  String cmdDesc, 
                                  Collection options,
                                  Collection args) {
        this(stream,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * Called following the call to <code>parse()</code> of this class's
     * contained CmdLineHandler.  This method only checks for its option if
     * <code>parseStatus</code> is true.
     * <P>
     * This method adds a ConsoleHandler to the root logger and sets the 
     * logging level of the root logger to that specified on the command line.
     *
     * @param parseStatus   The result of the <code>parse()</code> call to this
     *                      class's contained CmdLineHandler.
     * @return  This method will call <code>System.exit(0)</code>, rather 
     *          than returning, if its option is set.
     *          Otherwise, <code>parseStatus</code> is returned.
     */
    protected boolean processParsedOptions(boolean parseOk) {
        if (parseOk) {
            if (logOpt.isSet()) {
                Level level = Level.parse(logOpt.getValue());
                Handler h = new StreamHandler(stream, logFormatter);
                h.setLevel(level);
                Logger rootLogger = Logger.getLogger("");
                rootLogger.addHandler(h);
                rootLogger.setLevel(level);
            }
        }
        return parseOk;
    }

    /**
     * Sets the formatter for log messages, defaults to <code>
     * java.util.logging.SimpleFormatter</code>.  This method must be
     * called prior to calling <code>parse()</code> - calling this method after
     * the command line has been parsed will have no effect.
     *
     * @param logFormatter      the formatter for log messages
     * @throws  IllegalArgumentException if <code>logFormatter</code> is null
     * @see #getLogFormatter()
     */
    public void setLogFormatter(Formatter logFormatter) {
        if (logFormatter == null) {
            throw new IllegalArgumentException(Strings.get(
                "LoggerCmdLineHandler.logFormatterNullError"));
        }
        this.logFormatter = logFormatter;
    }

    /**
     * Gets the formatter for log messages, defaults to <code>
     * java.util.logging.SimpleFormatter</code>.
     *
     * @return              the formatter for log messages
     * @see #setLogFormatter(Formatter) setLogFormatter()
     */
    public Formatter getLogFormatter() {
        return logFormatter;
    }

}
