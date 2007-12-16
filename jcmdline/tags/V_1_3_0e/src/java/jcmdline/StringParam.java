/*
 * StringParam.java
 *
 * Classes:
 *   public   StringParam
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
 * Encapsulate a command line parameter whose value will be a string.
 * <p>
 * Usage:
 * <pre>
 * public class Sample {
 *     public static void main(String[] args) {
 * 
 *         // command line arguments
 *         StringParam patternArg =
 *             new StringParam("pattern", "the pattern to match",
 *                             StringParam.REQUIRED);
 *         // other param definitions omitted
 *         .
 *         .
 *         CmdLineHandler cl =
 *             new VersionCmdLineHandler("V 5.2",
 *             new HelpCmdLineHandler(helpText,
 *                 "kindagrep",
 *                 "find lines in files containing a specified pattern",
 *                 new Parameter[] { ignorecaseOpt, listfilesOpt },
 *                 new Parameter[] { patternArg, filesArg } ));
 *         cl.parse(args);
 *         .
 *         .
 *         // don't need to check patternArg.isSet() because is REQUIRED
 *         String pattern = patternArg.getValue();
 * 
 * </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: StringParam.java,v 1.3 2005/02/06 23:42:23 lglawrence Exp $
 * @see CmdLineParser
 */
public class StringParam extends AbstractParameter {

    /**
     * the default label that will represent option values for this Parameter
     * when displaying usage.  The following demonstrates a possible usage
     * excerpt for a StringParam option, where the option label is '&lt;s&gt;':
     * <pre>
     *    suffix &lt;s&gt; Specifies the file suffix to use for all output files
     *               produced by this program.
     * </pre>
     * @see AbstractParameter#setOptionLabel(String) setOptionLabel()
     * @see "<i>StringParam.defaultOptionLabel</i> in 'strings' properties file"
     */
    public static final String DEFAULT_OPTION_LABEL = 
            Strings.get("StringParam.defaultOptionLabel");

    /**
     * the value of the minimum or maximum length if they have not been
     * explicitly specified.  This value may be compared with the results of
     * getMinValLen() or getMaxValLen() to see whether a min/max had been
     * set, but will cause an Exception to be thrown if passed to 
     * setMaxValLen() or setMinValLen().
     */
    public static final int UNSPECIFIED_LENGTH = -1;

    /**
     * the maximum acceptable string length for the parameter value - if not
     * specified, defaults to StringParam.UNSPECIFIED_LENGTH, which permits
     * the value to be any length.
     */
    protected int maxValLen = UNSPECIFIED_LENGTH;

    /**
     * the minimum acceptable string length for the parameter value - if not
     * specified, defaults to 0.
     */
    protected int minValLen = UNSPECIFIED_LENGTH;

    /**
     * constructor - creates single-valued, optional, parameter accepting a 
     * string value of any length
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @throws              IllegalArgumentException if <code>tag</code> 
     *                      or <desc> are invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     */
    public StringParam(String tag, String desc) {
        this(tag, desc, 0, -1, OPTIONAL, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates single-valued, parameter accepting a string value 
     * of any length, and either optional or required, as specified.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @throws              IllegalArgumentException if any of the specified
     *                      parameters are invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     */
    public StringParam(String tag, String desc, boolean optional) {
        this(tag, desc, 0, -1, optional, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, optional, parameter accepting 
     * a value whose length is within the specified minimum and maximum lengths.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param minValLen     the minimum acceptable length
     * @param maxValLen     the maximum acceptable length, or a negative number
     *                      if there is no maximum length limit
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 #setMaxValLen(int) setMaxValLen()
     */
    public StringParam(String tag, String desc, int minValLen, int maxValLen) {
        this(tag, desc, minValLen, maxValLen, OPTIONAL, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, parameter accepting a value 
     * whose length is within the specified minimum and maximum lengths, 
     * and is optional or required, as specified.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param minValLen     the minimum acceptable length
     * @param maxValLen     the maximum acceptable length, or a negative number
     *                      if there is no maximum length limit
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 #setMaxValLen(int) setMaxValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     */
    public StringParam(String tag, 
                     String desc,
                     int minValLen, 
                     int maxValLen, 
                     boolean optional) {
        this(tag, desc, minValLen, maxValLen, optional, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, parameter accepting a value 
     * whose length is within the specified minimum and maximum lengths, and 
     * optional and/or multi-valued, as specified
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param minValLen     the minimum acceptable length
     * @param maxValLen     the maximum acceptable length, or a negative number
     *                      if there is no maximum length limit
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @param multiValued   {@link Parameter#MULTI_VALUED MULTI_VALUED} if 
     *                      the parameter can accept multiple values, 
     *                      {@link Parameter#SINGLE_VALUED SINGLE_VALUED} 
     *                      if the parameter can contain only a single value
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 #setMaxValLen(int) setMaxValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     */
    public StringParam(String tag, 
                     String desc,
                     int minValLen, 
                     int maxValLen, 
                     boolean optional,
                     boolean multiValued) {
        this(tag, desc, minValLen, maxValLen, optional, multiValued, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, parameter accepting a value 
     * whose length is within the specified minimum and maximum lengths, 
     * with all other options specified.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param minValLen     the minimum acceptable length
     * @param maxValLen     the maximum acceptable length, or a negative number
     *                      if there is no maximum length limit
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @param multiValued   {@link Parameter#MULTI_VALUED MULTI_VALUED} if 
     *                      the parameter can accept multiple values, 
     *                      {@link Parameter#SINGLE_VALUED SINGLE_VALUED} 
     *                      if the parameter can contain only a single value
     * @param hidden        {@link Parameter#HIDDEN HIDDEN} if parameter is 
     *                      not to be listed in the usage, 
     *                      {@link Parameter#PUBLIC PUBLIC} otherwise.
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 #setMaxValLen(int) setMaxValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     * @see                 Parameter#HIDDEN HIDDEN
     * @see                 Parameter#PUBLIC PUBLIC
     */
    public StringParam(String tag, 
                     String desc,
                     int minValLen, 
                     int maxValLen, 
                     boolean optional,
                     boolean multiValued,
                     boolean hidden) {
        this.setTag(tag);
        this.setMinValLen(minValLen);
        if (maxValLen >= 0) {
            this.setMaxValLen(maxValLen);
        }
        this.setDesc(desc);
        this.optional = optional;
        this.multiValued = multiValued;
        this.hidden = hidden;
        this.setOptionLabel(DEFAULT_OPTION_LABEL);
    }

    /**
     * constructor - creates a single-valued, optional, string parameter whose
     * value must be one of the specified values.
     *
     * @param tag           the tag associated with this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param acceptableValues  the acceptable values for the parameter
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setAcceptableValues(String[]) setAcceptableValues()
     * @see                 #setMinValLen(int) setMinValLen()
     */
    public StringParam(String tag, String desc, String[] acceptableValues) {
        this(tag, desc, acceptableValues, OPTIONAL, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, string parameter whose
     * value must be a specified value, and which is optional
     * or required, as specified.
     *
     * @param tag           the tag associated with this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param acceptableValues  the acceptable values for the parameter
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setAcceptableValues(String[]) setAcceptableValues()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     */
    public StringParam(String tag, 
                     String desc,
                     String[] acceptableValues, 
                     boolean optional) {
        this(tag, desc, acceptableValues, optional, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a string parameter whose value must be a 
     * specified value and is optional and/or * multi-valued, as specified.
     *
     * @param tag           the tag associated with this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param acceptableValues  the acceptable values for the parameter
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @param multiValued   {@link Parameter#MULTI_VALUED MULTI_VALUED} if 
     *                      the parameter can accept multiple values, 
     *                      {@link Parameter#SINGLE_VALUED SINGLE_VALUED} 
     *                      if the parameter can contain only a single value
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setAcceptableValues(String[]) setAcceptableValues()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     */
    public StringParam(String tag, 
                     String desc,
                     String[] acceptableValues, 
                     boolean optional,
                     boolean multiValued) {
        this(tag, desc, acceptableValues, optional, multiValued, PUBLIC);
    }

    /**
     * constructor - creates a string parameter whose value must be a 
     * specified value and all other options are as specified.
     *
     * @param tag           the tag associated with this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param acceptableValues  the acceptable values for the parameter
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @param multiValued   {@link Parameter#MULTI_VALUED MULTI_VALUED} if 
     *                      the parameter can accept multiple values, 
     *                      {@link Parameter#SINGLE_VALUED SINGLE_VALUED} 
     *                      if the parameter can contain only a single value
     * @param hidden        {@link Parameter#HIDDEN HIDDEN} if parameter is 
     *                      not to be listed in the usage, 
     *                      {@link Parameter#PUBLIC PUBLIC} otherwise.
     * @throws              IllegalArgumentException if any parameter is 
     *                      invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 #setAcceptableValues(String[]) setAcceptableValues()
     * @see                 #setMinValLen(int) setMinValLen()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     * @see                 Parameter#HIDDEN HIDDEN
     * @see                 Parameter#PUBLIC PUBLIC
     */
    public StringParam(String tag, 
                     String desc,
                     String[] acceptableValues, 
                     boolean optional,
                     boolean multiValued,
                     boolean hidden) {
        setTag(tag);
        setDesc(desc);
        setAcceptableValues(acceptableValues);
        this.optional = optional;
        this.multiValued = multiValued;
        this.hidden = hidden;
        this.setOptionLabel(DEFAULT_OPTION_LABEL);
    }

    /**
     * Validates a prospective value with regards to the minimum and maximum
     * values and the acceptableValues - called by add/setValue(s)().
     *
     * @param val           the prospective value to validate
     * @throws              CmdLineException if 
     *                      <code>value
     *                      </code> is not valid with regard to the minimum and
     *                      maximum lengths, and the acceptableValues.
     */
    public void validateValue(String val) throws CmdLineException {
        super.validateValue(val);
        if (minValLen != UNSPECIFIED_LENGTH && val.length() < minValLen) {
            throw new CmdLineException(Strings.get(
                    "StringParam.valTooShort",
                    new Object[] { tag, new Integer(minValLen) }));
        }
        if (maxValLen != UNSPECIFIED_LENGTH && val.length() > maxValLen) {
            throw new CmdLineException(Strings.get(
                    "StringParam.valTooLong",
                    new Object[] { tag, new Integer(maxValLen) }));
        }
    }

    /**
     * gets the value of the minimum acceptable length for the string value
     *
     * @return              the minimum acceptable length for string value
     *                      This will be equal to 0 if it had never been set.
     */
    public int getMinValLen() {
        return minValLen;
    }

    /**
     * sets the value of the minimum acceptable length for the string value
     *
     * @param minValLen     the minimum acceptable length 
     * @throws              IllegalArgumentException if 
     *                      <code>minValLen</code>
     *                      less than 0, or is greater than <code>maxValLen
     *                      </code>.
     */
    public void setMinValLen(int minValLen) {
        if (minValLen < 0) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.minTooSmall", new Object[] { tag }));
        }
        if (maxValLen != UNSPECIFIED_LENGTH && minValLen > maxValLen) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.maxLessThanMin", new Object[] { tag }));
        }
        this.minValLen = minValLen;
    }

    /**
     * gets the value of the maximum acceptable length for the string value
     *
     * @return              The maximum acceptable length for string value.
     *                      This will be equal to StingParam.UNSPECIFIED_LENGTH
     *                      if it had never been set.
     */
    public int getMaxValLen() {
        return maxValLen;
    }

    /**
     * sets the value of the maximum acceptable length for the string value
     *
     * @param maxValLen     the maximum acceptable length 
     * @throws              IllegalArgumentException if <code>maxValLen
     *                      </code>
     *                      less than 0, or is less than <code>minValLen
     *                      </code>.
     */
    public void setMaxValLen(int maxValLen) {
        if (maxValLen < 0) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.maxTooSmall", new Object[] { tag }));
        }
        if (minValLen != UNSPECIFIED_LENGTH && maxValLen < minValLen) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.maxLessThanMin", new Object[] { tag }));
        }
        this.maxValLen = maxValLen;
    }
    /**
     * sets the value of the maximum acceptable length for the string value
     *
     * @param maxValLen     the maximum acceptable length 
     * @throws              IllegalArgumentException if <code>maxValLen
     *                      </code>
     *                      less than 0, or is less than <code>minValLen
     *                      </code>.
     * @deprecated          This method deprecated in favor of setMaxValLen(),
     *                      which is more in line with the naming convetions
     *                      used elsewhere in this class.
     * @see                 #setMaxValLen(int) setMaxValLen()
     */
    public void setMaxLength(int maxValLen) {
        if (maxValLen < 0) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.maxTooSmall", new Object[] { tag }));
        }
        if (minValLen != UNSPECIFIED_LENGTH && maxValLen < minValLen) {
            throw new IllegalArgumentException(Strings.get(
                    "StringParam.maxLessThanMin", new Object[] { tag }));
        }
        this.maxValLen = maxValLen;
    }
}
