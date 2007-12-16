/*
 * StringFormatHelper.java
 *
 * jcmdline Rel. @VERSION@ $Id: StringFormatHelper.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   StringFormatHelper
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

/**
 * A class used to facilitate common String formatting tasks.
 * <P>
 * Objects of this class contain no data.  As such, this class is
 * implemented as a Singleton.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: StringFormatHelper.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public class StringFormatHelper {

    /**
     * The one and only instance of the StringFormatHelper
     */
    private static StringFormatHelper helper;

    /**
     * constructor - private
     */
    private StringFormatHelper() {
    }

    /**
     * Gets the one and only instance of the StringFormatHelper
     * @return              the one and only instance of the StringFormatHelper
     */
    public static StringFormatHelper getHelper() {
        if (helper == null) {
            helper = new StringFormatHelper();
        }
        return helper;
    }

    /**
     * Formats a string with a hanging indent.
     * <P>The returned String is formated such that:
     * <ul>
     * <li>all lines it contains have length &lt;= <code>lineLen</code>
     * <li>all lines except for the first on are indented <code>indent</code>
     *     spaces
     * </ul>
     *
     * @param indent        the number of spaces to indent all but the 
     *                      first line (may be 0)
     * @param lineLen     the maximum line length
     * @param s             the string to be formatted
     * @return              the formatted string
     * @throws  IllegalArgumentException if <code>lineLen</code> is less than
     *          <code>indent</code> or if <code>lineLen</code> is less than 0.
     */
    public String formatHangingIndent(String s, int indent, int lineLen) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (lineLen <= indent) {
            throw new IllegalArgumentException(Strings.get(
                "StringFormatHelper.lineLenLessThanIndent"));
        }
        if (lineLen <= 0) {
            throw new IllegalArgumentException(Strings.get(
                "StringFormatHelper.lineLenZero"));
        }

        // guesstimate required buffer length, leaving enough room for line 
        // terminators
        StringBuffer sb = new StringBuffer(
            s.length() + (s.length()/lineLen-indent) + 20);
        
        String[] a = breakString(s, lineLen);
        sb.append(a[0]);
        if (a[1] != null) {
            sb.append(formatBlockedText(a[1], indent, lineLen));
        }
        return sb.toString();
    }

    /**
     * Splits the specified String into lines that are indented by the 
     * specified indent and are of length less than or equal to the 
     * specified line length.
     * <P> 
     * If <code>s</code> is null or empty, an empty String is returned.
     *
     * @param s             the String to be formatted
     * @param indent        the length of the indent for the text block
     * @param lineLen       the maximum line length for the text block, 
     *                      including the indent
     * @return              the formatted text block
     * @throws  IllegalArgumentException if <code>lineLen</code> is less than
     *          <code>indent</code> or if <code>lineLen</code> is less than 0.
     */
    public String formatBlockedText(String s, int indent, int lineLen) {
        if (s == null || s.length() == 0) {
            return "";
        }

        if (lineLen <= indent) {
            throw new IllegalArgumentException(Strings.get(
                "StringFormatHelper.lineLenLessThanIndent"));
        }
        if (lineLen <= 0) {
            throw new IllegalArgumentException(Strings.get(
                "StringFormatHelper.lineLenZero"));
        }

        // guesstimate required buffer length, leaving enough room for line 
        // terminators
        StringBuffer sb = new StringBuffer(
            s.length() + (s.length()/lineLen-indent) + 20);

        StringBuffer indentBuf = new StringBuffer(indent);
        for (int i = 0; i < indent; i++) {
            indentBuf.append(" ");
        }

        int splitLen = lineLen - indent;

        String[] a = breakString(s, splitLen);
        while (a[1] != null) {
            sb.append(indentBuf).append(a[0]);
            a = breakString(a[1], splitLen);
        }
        sb.append(indentBuf).append(a[0]);
        return sb.toString();
    }

    /**
     * Breaks a String along word boundarys to the specified maximum length.
     * <p>
     * This method returns an array of two strings:
     * <p>
     * The first String is a line, of length less than <code>maxLen</code>.
     * If this String is not the entire passed String, it will
     * be terminated with a newline.
     * <P>
     * The second String is the remainder of the original String.  If the
     * original String had been broken on a space (as opposed to a newline
     * that had been in the original String) all leading spaces will have
     * been removed.  If there is no remainder, null is returned as the 
     * second String and no newline will have been appended to the first
     * String.
     *
     * @param s             The String to be broken.  If null, will be
     *                      converted to an empty string.
     * @param maxLen        the maximum line length of the first returned
     *                      string
     * @return              see the method description
     */
    protected String[]  breakString(String s, int maxLen) {
        s = (s == null) ? "" : s;
        String line = null;
        String remainder = null;
        int idx;
        if ((idx = s.indexOf('\n')) != -1 && idx <= maxLen) {
            idx++;  // point to next char
            line = s.substring(0, idx);
            if (idx < s.length()) {
                remainder = s.substring(idx);
            }
        } else if (s.length() <= maxLen) {
            line = s;
        } else if ((idx = s.lastIndexOf(' ', maxLen)) != -1) {
            line = s.substring(0, idx);
            while (idx < s.length() && s.charAt(idx) == ' ') {
                idx++;
            }
            if (idx < s.length()) {
                line += "\n";
                remainder = s.substring(idx);
            }
        } else {
            line = s.substring(0, maxLen) + "\n";
            remainder = s.substring(maxLen);
        }
        return new String[] { line, remainder };
    }

    /**
     * Formats a "labeled list" (like a bullet or numbered list, only with
     * labels for each item).
     * <P>
     * Example:
     * <p><pre>
     * System.out.println(formatLabeledList(
     *    new String[] { "old_file", "new_file" },
     *    new String[] { "the name of the file to copy - this file " +
     *                       "must already exist, be readable, and " +
     *                       "end with '.html'",
     *                   "the name of the file to receive the copy" },
     *    " = ", 20, 80));
     * </pre>
     * produces....
     * <pre>
     * old_file = the name of the file to copy - this file must already exist, be
     *            readable, and end with '.html'
     * new_file = the name of the file to copy to
     * </pre>
     *
     * @param labels        An array of labels.
     * @param texts         An array of texts to go with the labels.
     * @param divider       The divider to go between the labels and texts.  This
     *                      will be right-aligned against the texts.
     * @param maxIndent     Specifies the maximum indent for the text to be
     *                      written out.  If the combination of a label and
     *                      divider is longer than maxIndent, the text will
     *                      be written out in a block starting on the line
     *                      following the label and divider, rather than on the
     *                      same line.
     * @param lineLen       The maximum length of returned lines.
     * @return              The formatted list.  It will be terminated with a
     *                      newline.
     * @throws  IllegalArgumentException if <code>labels</code> and <code>text
     *          </code> do not have the same number of elements.
     */
    public String formatLabeledList(String[] labels,
                                    String[] texts,
                                    String divider,
                                    int maxIndent,
                                    int lineLen) {

        if (labels.length != texts.length) {
            throw new IllegalArgumentException(Strings.get(
                "StringFormatHelper.labelDescriptionError", 
                new Object[] { new Integer(labels.length), 
                               new Integer(texts.length) }));
        }

        // Figure out description indents
        int indent = 0;
        int dividerlen = divider.length();
        int currlen;
        for (int i = 0; i < labels.length; i++) {
            currlen = labels[i].length() + dividerlen;
            if (currlen > maxIndent) {
                continue;
            } else if (currlen > indent) {
                indent = currlen;
            }
        }
        // All labels+divider > maxIndent? - use indent of 10
        indent = (indent == 0) ? 10 : indent;

        // will fit 20 80-char lines without expansion
        StringBuffer list = new StringBuffer(1600);
        // will fit 5 lines per list item without expansion
        StringBuffer item = new StringBuffer(400);

        for (int i = 0; i < labels.length; i++) {
            item.delete(0, item.length());
            item.append(labels[i]);
            int spacefill = indent - divider.length();
            while (item.length() < spacefill) {
                item.append(' ');
            }
            item.append(divider);
            if (item.length() > indent) {
                item.append("\n");
            }
            item.append(texts[i]);
            list.append(formatHangingIndent(item.toString()+ "\n",
                                            indent, 
                                            lineLen));
        }
        return list.toString();
    }
}
