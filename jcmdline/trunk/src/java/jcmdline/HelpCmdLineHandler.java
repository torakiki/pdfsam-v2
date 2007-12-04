/*
 * HelpCmdLineHandler.java
 *
 * Classes:
 *   public   HelpCmdLineHandler
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
 * A CmdLineHandler Decorator class that implements help options that
 * display verbose help messages.
 * <P>
 * Options are provided to display a regular help message (which includes
 * the regular usage associated with the command) or a help message that
 * includes the hidden parameters in its usage and, optionally, additional
 * help text specific to the hidden parameters.
 * <p>
 * The implemented options are BooleanParams whose tags are defined by
 * "HelpCmdLineHandler.help.tag" and "HelpCmdLineHandler.helpHidden.tag"
 * in the <i>strings.properties</i> file (set to "help" and "help!", in English).
 * <P>
 * Should the user specify the -help option on the command line, the command's
 * usage, followed by the more verbose help text specified to this Object's
 * constructor, is printed to stdout, and <code>System.exit(0)</code> is called.
 * <P>
 * Should the -help! option be specified the same is done, but hidden
 * parameter information and help is displayed as well.
 * <P>
 * <b>Sample Usage</b>
 * The following creates a CmdLineHandler that uses multiple Decorators 
 * to enable the help options supported by this class, the version option
 * implemented by the {@link VersionCmdLineHandler} class, and the usage 
 * options implemented by the {@link DefaultCmdLineHandler} class:
 * <pre>
 * public static void main(String[] args) {
 * 
 *     Parameter[] arguments = new Parameter[] {
 *         new StringParam("pattern", "the pattern to match", 
 *                         StringParam.REQUIRED),
 *         new FileParam("file",
 *                       "a file to be processed - defaults to stdin",
 *                       FileParam.IS_FILE & FileParam.IS_READABLE,
 *                       FileParam.OPTIONAL,
 *                       FileParam.MULTI_VALUED)
 *     };
 *     Parameter[] opts = new Parameter[] {
 *         new BooleanParam("ignorecase", "ignore case while matching"),
 *         new BooleanParam("listFiles", "list filenames containing pattern")
 *     };
 *     String helpText = "This command prints to stdout all lines within " +
 *                       "the specified files that contain the specified " +
 *                       "pattern.\n\n" +
 *                       "Optionally, the matching may be done without " +
 *                       "regard to case (using the -ignorecase option).\n\n" +
 *                       "If the -listFiles option is specified, only the " +
 *                       "names of the files containing the pattern will be " +
 *                       "listed.";
 * 
 *     CmdLineHandler cl =
 *         new VersionCmdLineHandler("V 5.2",
 *         new HelpCmdLineHandler(helpText,
 *             "grep",
 *             "find lines in files containing a specified pattern",
 *             opts,
 *             arguments));
 *     cl.parse(args);
 *     .
 *     .
 * </pre>
 * <P>
 * <P>
 * <a name="helptext"><b>Help Text Formatting</b></a>
 * <P>
 * The help text for a command will be formatted for output such that:
 * <ul>
 * <li>All lines will be word wrapped at the appropriate line length by the
 *     formatter - it is suggested
 *     that the user let the program handle line wrapping for best results,
 *     except when a new paragraph is starting.
 * <li>Newlines (\n), and leading spaces that immediately follow a
 *     newline, in the text will be honored - it is recommended that
 *     they be used to mark new paragraphs, and when formatting/indenting
 *     lines.
 * </ul>
 * <P>
 * Information on using CmdLineHandlers can be found in the jcmdline
 * <a href="doc-files/userguide.html">User Guide</a>.
 *
 * @author          Lynne Lawrence
 * @version         2007-Dec-04 modified by Andrea Vacondio, added -license param
 * @see CmdLineHandler
 * @see AbstractHandlerDecorator
 */
public class HelpCmdLineHandler extends AbstractHandlerDecorator {

    /**
     * a parameter that will cause standard help to be displayed
     */
    private BooleanParam helpOpt;
    
    /**
     * a parameter that will cause license to be displayed
     */
    private BooleanParam licenseOpt;

    /**
     * a parameter that will cause help including hidden information to be 
     * displayed
     */
    private BooleanParam hiddenHelpOpt;

    /**
     * the command's help
     */
    private String help;
    
    /**
     * the license
     */
    private String license;

    /**
     * the command's help for hidden Parameters
     */
    private String hiddenHelp;

    /**
     * constructor
     *
     * @param help          the command's help - see <a href="#helptext">Help 
     *                      Text Formatting</a>.
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     * @throws  IllegalArgumentException if <code>help</code> is null or
     *          empty.
     */
    public HelpCmdLineHandler (String help,
    						   String license,
                               String hiddenHelp, 
                               CmdLineHandler handler) {
        super(handler);
        if (help == null || help.length() == 0) {
            throw new IllegalArgumentException(Strings.get(
                "HelpCmdLineHandler.helpEmptyError"));
        }
        this.help = help;
        if (license == null || license.length() == 0) {
            this.license = Strings.get("HelpCmdLineHandler.license.nolicense");
        } else {
            this.license = license;
        }
        if (hiddenHelp == null || hiddenHelp.length() == 0) {
            this.hiddenHelp = null;
        } else {
            this.hiddenHelp = hiddenHelp;
        }
        helpOpt = new BooleanParam(
            Strings.get("HelpCmdLineHandler.help.tag"),
            Strings.get("HelpCmdLineHandler.help.desc"));
        helpOpt.setIgnoreRequired(true);
        licenseOpt = new BooleanParam(
                Strings.get("HelpCmdLineHandler.license.tag"),
                Strings.get("HelpCmdLineHandler.license.desc"));
        licenseOpt.setIgnoreRequired(true);        
        hiddenHelpOpt = new BooleanParam(
            Strings.get("HelpCmdLineHandler.helpHidden.tag"),
            Strings.get("HelpCmdLineHandler.helpHidden.desc"),
            BooleanParam.HIDDEN);
        hiddenHelpOpt.setIgnoreRequired(true);
        setCustomOptions(new Parameter[] { helpOpt, hiddenHelpOpt, licenseOpt });
    }

    /**
     * constructor
     *
     * @param help          the command's help - see <a href="#helptext">Help 
     *                      Text Formatting</a>.
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     * @throws  IllegalArgumentException if <code>help</code> is null or
     *          empty.
     */
    public HelpCmdLineHandler (String help, CmdLineHandler handler) {
        this(help, null, null, handler);
    }

    /**
     * 
     * @param help          the command's help - see <a href="#helptext">Help 
     *                      Text Formatting</a>.
     * @param license 		the license informations                     
     * @param handler       the CmdLineHandler to which most functionality
     *                      will be delegated
     * @throws  IllegalArgumentException if <code>help</code> is null or
     *          empty.
     */
    public HelpCmdLineHandler (String help, String license, CmdLineHandler handler) {
        this(help, license, null, handler);
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
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
    public HelpCmdLineHandler (String help,
                               String cmdName, 
                               String cmdDesc, 
                               Parameter[] options,
                               Parameter[] args,
                               CmdLineParser parser) {

        this(help, null, null,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args, parser));
    }

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
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
    public HelpCmdLineHandler (String help,
                               String cmdName, 
                               String cmdDesc, 
                               Parameter[] options,
                               Parameter[] args) {
        this(help, null, null,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }

    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
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
    public HelpCmdLineHandler (String help,
                               String cmdName, 
                               String cmdDesc, 
                               Collection options,
                               Collection args) {
        this(help, null, null,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
    public HelpCmdLineHandler (String help,
                               String hiddenHelp,
                               String cmdName, 
                               String cmdDesc, 
                               Parameter[] options,
                               Parameter[] args,
                               CmdLineParser parser) {

        this(help, null, hiddenHelp,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args, parser));
    }
    
    /**
       *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param license		license informations                     
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
    public HelpCmdLineHandler (String help,
    		String license,
            String hiddenHelp,
            String cmdName, 
            String cmdDesc, 
            Parameter[] options,
            Parameter[] args,
            CmdLineParser parser) {

		this(help, license, hiddenHelp,
		new DefaultCmdLineHandler(cmdName, cmdDesc, options, args, parser));
	}

    /**
     * constructor - creates a new DefaultCmdLineHandler as its delegate
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
    public HelpCmdLineHandler (String help,
                               String hiddenHelp,
                               String cmdName, 
                               String cmdDesc, 
                               Parameter[] options,
                               Parameter[] args) {
        this(help, null, hiddenHelp,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }

    /**
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param license		license informations                     
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
     * @see DefaultCmdLineHandler     */
    public HelpCmdLineHandler (String help,
    		String license,
            String hiddenHelp,
            String cmdName, 
            String cmdDesc, 
            Parameter[] options,
            Parameter[] args) {
	this(help, license, hiddenHelp,
	new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
	}
    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param license		license informations                     
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
    public HelpCmdLineHandler (String help,
                               String hiddenHelp,
                               String cmdName, 
                               String cmdDesc, 
                               Collection options,
                               Collection args) {
        this(help, null, hiddenHelp,
             new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * constructor - uses the PosixCmdLineParser to parse the command line
     *
     * @param help          the command's help - see <a href="#helptext">
     *                      Help Text Formatting</a>.
     * @param hiddenHelp    the command's help for hidden Parameters - may
     *                      be null or empty
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
    public HelpCmdLineHandler (String help,
    		String license,
            String hiddenHelp,
            String cmdName, 
            String cmdDesc, 
            Collection options,
            Collection args) {
		this(help, license, hiddenHelp,
		new DefaultCmdLineHandler(cmdName, cmdDesc, options, args));
    }
    
    /**
     * Called following the call to <code>parse()</code> of this class's
     * contained CmdLineHandler.  This method checks for its option whether
     * <code>parseStatus</code> is true or not.
     *
     * @param parseStatus   The result of the <code>parse()</code> call to this
     *                      class's contained CmdLineHandler.
     * @return  This method will call <code>System.exit(0)</code>, rather 
     *          than returning, if its option is set.
     *          Otherwise, <code>parseStatus</code> is returned.
     */
    protected boolean processParsedOptions(boolean parseOk) {
        if ( helpOpt.isTrue() || hiddenHelpOpt.isTrue() || licenseOpt.isTrue()) {
            if ( helpOpt.isTrue() || hiddenHelpOpt.isTrue() ) {
	            System.out.println(getUsage(hiddenHelpOpt.isTrue()));
	            int lineLen = getParser().getUsageFormatter().getLineLength();
	            StringFormatHelper sHelper = StringFormatHelper.getHelper();
	            System.out.println(
	                "\n" + sHelper.formatBlockedText(help, 0, lineLen));
	            if (hiddenHelpOpt.isTrue() && hiddenHelp != null) {
	                System.out.println(
	                    "\n" + sHelper.formatBlockedText(hiddenHelp, 0, lineLen));
	            }
            }else{
            	if(licenseOpt.isTrue()){
            		if (license != null) {
            			int lineLen = getParser().getUsageFormatter().getLineLength();
            			StringFormatHelper sHelper = StringFormatHelper.getHelper();
    	                System.out.println(
    	                    "\n" + sHelper.formatBlockedText(license, 0, lineLen));
    	            }
            	}
            }
            System.exit(0);
        }
        return parseOk;
    }
}
