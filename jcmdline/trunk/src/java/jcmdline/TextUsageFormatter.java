/*
 * TextUsageFormatter.java
 *
 * Classes:
 *   public   TextUsageFormatter
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Used to format a command's usage.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: TextUsageFormatter.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see CmdLineHandler
 */
public class TextUsageFormatter implements UsageFormatter {


    /**
     * the maximum line length to use for usage display - defaults to 80.
     * @see #setLineLength(int) setLineLength()
     * @see #getLineLength()
     */
    private int lineLength = 80;

    /**
     * a Helper for usage formatting
     */
    private StringFormatHelper sHelper = StringFormatHelper.getHelper();

    /**
     * constructor
     */
    public TextUsageFormatter () {
    }

    /**
     * gets the usage for the command
     *
     * @param cmdName       the command name
     * @param cmdDesc       the command description
     * @param opts          a Map of command options; keys are the option
     *                      tags, values are the option objects
     * @param args          the command arguments
     * @param showHidden    if <code>true</code>, hidden parameters will be
     *                      displayed
     * @return              the usage for the command
     */
    public String formatUsage(String cmdName,
                              String cmdDesc,
                              Map opts,
                              List args,
                              boolean showHidden) {
        String s;
        int maxExpectedStringLen = 2048;
        StringBuffer sb = new StringBuffer(maxExpectedStringLen);

        // Format command name and description

        s = sHelper.formatHangingIndent(cmdName + " - " + cmdDesc,
                                        cmdName.length() + 3, 
                                        lineLength);
        sb.append(s + "\n\n");

        // Format options and arguments

        int lineStartIdx = sb.length();
        sb.append(Strings.get("TextUsageFormatter.usage"))
          .append(" ")
          .append(cmdName) 
          .append(" ");
        if (opts.size() > 0) {
            sb.append(haveRequiredOpt(opts) 
                    ? Strings.get("TextUsageFormatter.usageWReqOpt")
                    : Strings.get("TextUsageFormatter.usageWOReqOpt"))
              .append(" ");
        }

        if (args.size() > 0) {
            StringBuffer sb2 = argsOnOneLine(args, showHidden);

            if (sb.length() - lineStartIdx + sb2.length() > lineLength) {
                sb2 = argsOnSeparateLines(args, 
                                          sb.length() - lineStartIdx, 
                                          showHidden);
            }
            sb.append(sb2.toString())
              .append("\n")
              .append(getArgDescriptions(args, showHidden));
        } 

        if (opts.size() > 0) {
            sb.append("\n\n");
            if (args.size() > 0) {
                  sb.append(Strings.get("TextUsageFormatter.optIntroWArgs"));
            } else {
                  sb.append(Strings.get("TextUsageFormatter.optIntroNoArgs"));
            }
            sb.append("\n\n")
              .append(getOptDescriptions(opts, showHidden));

            sb.append("\n\n")
              .append(sHelper.formatHangingIndent(
                      Strings.get("TextUsageFormatter.stdOptionHelp"),
                      0, lineLength));
        }

        return sb.toString();
    }

    /**
     * Gets an error message, reformatted in a manner to "go well with"
     * the usage statement.  This implementation returns:
     * <pre>
     *    ERROR: invalid filename
     * </pre>
     * when called as:
     * <pre>
     *    formatErrorMsg("invalid filename")
     * </pre>
     *
     * @param msg           the text of the error message
     * @return              the reformatted error message
     */
    public String formatErrorMsg(String msg) {
        String error = Strings.get("TextUsageFormatter.errorPrefix");
        return sHelper.formatHangingIndent(error + " " + msg,
                                           error.length() + 1, 
                                           lineLength);
    }

    public String formatText(String text, int indent, int lineLen) {
        return sHelper.formatBlockedText(text, indent, lineLen);
    }

    /**
     * Sets the maximum line length to use for usage display - default is 80.
     *
     * @param lineLength      the maximum line length to use for usage display
     * @see #getLineLength()
     */
    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
    }

    /**
     * Gets the maximum line length to use for usage display.
     *
     * @return              the maximum line length to use for usage display
     * @see #setLineLength(int) setLineLength()
     */
    public int getLineLength() {
        return lineLength;
    }

    /**
     * Returns <code>true</code> if any of the command line options are 
     * required.
     *
     * @return              true if any option is required
     */
    private boolean haveRequiredOpt(Map options) {
        boolean haveRequiredOpt = false;
        for (Iterator itr = options.values().iterator(); itr.hasNext(); ) {
            if (!((Parameter) itr.next()).isOptional()) {
                haveRequiredOpt = true;
                break;
            }
        }
        return haveRequiredOpt;
    }

    /**
     * Get command line arguments, one per line, with each line but the
     * first having the specified indent.
     *
     * @param indent        the indent
     * @return              the formatted arguments
     */
    private StringBuffer argsOnSeparateLines(List args, 
                                             int indent, 
                                             boolean showHidden) {
        int maxExpectedStringLen = 240;
        StringBuffer sb2 = new StringBuffer(maxExpectedStringLen);
        Parameter p;

        int optIdx = 0;
        sb2 = new StringBuffer(maxExpectedStringLen);
        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (p.isHidden() && !showHidden) {
                continue;
            }
            for (int i = 0; i < optIdx; i++) {
                sb2.append(' ');
            }
            sb2.append(argTagToString(p)).append(" \\\n");
            optIdx = indent;
        }
        // trim off trailing space, backslash, and newline
        return sb2.delete(sb2.length() - 3, sb2.length());
    }

    /**
     * Get command line arguments, all on one line
     *
     * @return              the formatted arguments
     */
    private StringBuffer argsOnOneLine(List args, boolean showHidden) {
        int maxExpectedStringLen = 800;
        StringBuffer sb2 = new StringBuffer(maxExpectedStringLen);
        Parameter p;

        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (p.isHidden() && !showHidden) {
                continue;
            }
            sb2.append(argTagToString(p)).append(" ");
        }
        // trim off trailing space
        if (sb2.length() > 0) {
            sb2.deleteCharAt(sb2.length() - 1);
        }
        return sb2;
    }

    /**
     * Get a command line argument's tag, enclosing in brackets if optional,
     * and twice, followed by '...', if multi-valued.
     *
     * @param p             the argument
     * @return              the argument's tag, as a string
     */
    private String argTagToString(Parameter p) {
        int maxExpectedStringLen = 50;
        StringBuffer sb2 = new StringBuffer(maxExpectedStringLen);

        String argstr;
        if (p.isOptional()) {
            argstr = "[" + p.getTag() + "]";
        } else {
            argstr = p.getTag();
        }
        sb2.append(argstr);
        if (p.isMultiValued()) {
            sb2.append("," + argstr + "...");
        }
        return sb2.toString();
    }

    /**
     * Gets the argument descriptions as a String.
     *
     * @param showHidden    true if hidden arguments are to be included.
     * @return              the argument descriptions
     */
    private String getArgDescriptions(List args, boolean showHidden) {
        StringBuffer sb = new StringBuffer(1024);
        Parameter p;
        
        if (args.size() == 0) {
            return "";
        }
        sb.append("\n")
          .append(Strings.get("TextUsageFormatter.where"))
          .append("\n\n");

        ArrayList tags = new ArrayList(args.size());
        ArrayList desc = new ArrayList(args.size());
        for (Iterator itr = args.iterator(); itr.hasNext(); ) {
            p = (Parameter) itr.next();
            if (p.isHidden() && !showHidden) {
                continue;
            }
            tags.add(p.getTag());
            desc.add(p.getDesc() + " (" +
                     ((p.isOptional()) 
                        ? Strings.get("TextUsageFormatter.optional")
                        : Strings.get("TextUsageFormatter.required"))
                     + ")" +
                     ((p.isHidden()) 
                       ? (" (" + 
                          Strings.get("TextUsageFormatter.hidden") + ")")
                       : ""));
        }
        sb.append(sHelper.formatLabeledList(
            (String[])tags.toArray(new String[tags.size()]),
            (String[])desc.toArray(new String[desc.size()]),
            " = ",
            20,
            lineLength));

        // remove trailing newline
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * Gets the option descriptions as a String. 
     *
     * @param showHidden    true if hidden options are to be included.
     * @return              the option descriptions
     */
    private String getOptDescriptions(Map options, boolean showHidden) {

        if (options.size() == 0) {
            return "";
        }
        ArrayList sortedOptions = new ArrayList(options.keySet());
        Collections.sort(sortedOptions);

        ArrayList labels = new ArrayList(options.size());
        ArrayList desc = new ArrayList(options.size());
        Parameter p;
        for (Iterator itr = sortedOptions.iterator(); itr.hasNext(); ) {
            p = (Parameter) options.get(itr.next());
            if (p.isHidden() && !showHidden) {
                continue;
            }
            labels.add("-" + p.getTag() + " " + p.getOptionLabel());
            desc.add(p.getDesc() + " (" +
                     ((p.isOptional()) 
                        ? Strings.get("TextUsageFormatter.optional")
                        : Strings.get("TextUsageFormatter.required"))
                     + ")" +
                     ((p.isHidden()) 
                       ? (" (" + 
                          Strings.get("TextUsageFormatter.hidden") + ")")
                       : ""));
        }
        StringBuffer sb = new StringBuffer(
            (sHelper.formatLabeledList(
                (String[])labels.toArray(new String[labels.size()]),
                (String[])desc.toArray(new String[desc.size()]),
                " ",
                20,
                lineLength)));
        // remove trailing newline
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
