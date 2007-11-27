/*
 * TimeParam.java
 *
 * jcmdline Rel. @VERSION@ $Id: TimeParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   TimeParam
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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * A parameter that accepts a time as its value.
 * <p>
 * The format for the time is "HH:mm:ss:SSS", where
 * the seconds and/or milliseconds portion may be left off by the user,
 * in which case they will be defaulted.
 * <P>
 *  Sample Usage:
 *  <pre>
 *     TimeParam startTimeParam = 
 *         new TimeParam("startTime", 
 *                       "start time of report", 
 *                       TimeParam.REQUIRED);
 *     TimeParam endTimeParam = 
 *         new TimeParam("endTime", 
 *                       "end time of report", 
 *                       TimeParam.REQUIRED);
 * 
 *     // Seconds and millis for startTime will both be 0 by default.
 *     // Set the seconds and millis for the end of the report to be the end 
 *     // of a minute.
 *     endTimeParam.setDefaultSeconds(59);
 *     endTimeParam.setDefaultMilliSeconds(999);
 * 
 *     CmdLineHandler cl = new DefaultCmdLineHandler(
 *         "myreport", "generate current activity report",
 *         new Parameter[] {}, 
 *         new Parameter[] { startTimeParam, endTimeParam });
 *     
 *     cl.parse();
 * 
 *     // Don't need to check isSet() because params are REQUIRED
 *     Date today = new Date();
 *     Date stTime = startTimeParam.getDate(today);
 *     Date enTime = endTimeParam.getDate(today);
 *     .
 *     .
 *  </pre>
 *  This will result in a command line that may be executed as:
 *  <pre>
 *   myreport "10:12" "23:34"
 *  </pre>
 *  or
 *  <pre>
 *   myreport "10:12:34:567" "23:34:34:567"
 *  </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: TimeParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see DateParam
 * @see TimeParam
 */
public class TimeParam extends AbstractParameter {

    private static final String sTimeFmt = "HH:mm:ss:SSS";
    private static final String sTimeFmtDisplay = "HH:mm[:ss[:SSS]]";
    private static final DecimalFormat secondFmt = new DecimalFormat("00");
    private static final DecimalFormat msFmt = new DecimalFormat("000");
    private static final long MILLIS_PER_SECOND = 1000;
    private static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
    private static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;

    /**
     * The seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     * @see #setDefaultSeconds(int) setDefaultSeconds()
     * @see #getDefaultSeconds()
     */
    private int defaultSeconds = 0;

    /**
     * The default millisecond value to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     * @see #setDefaultMilliSeconds(int) setDefaultMilliSeconds()
     * @see #getDefaultMilliSeconds()
     */
    private int defaultMilliSeconds = 0;

    /**
     * An array of acceptable time values.  We are bypassing the superclass
     * acceptable values implementation altogether because we do an
     * "approximate" time match using default seconds and milliseconds
     * at the time of the parse.
     */
    private String[] acceptableTimes = null;

    /**
     * constructor - creates single-valued, optional, public
     * parameter
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @throws              IllegalArgumentException if <code>tag</code> 
     *                      or <desc> are invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     */
    public TimeParam(String tag, String desc) {
        this(tag, desc, OPTIONAL, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates single-valued, public parameter which will
     * will be either optional or required, as specified.
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
    public TimeParam(String tag, String desc, boolean optional) {
        this(tag, desc, optional, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a public parameter which will
     * will be either optional or required, and/or multi-valued, as specified.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
     *                      optional, 
     *                      {@link Parameter#REQUIRED REQUIRED} if required
     * @param multiValued   {@link Parameter#MULTI_VALUED MULTI_VALUED} if 
     *                      the parameter can accept multiple values, 
     *                      {@link Parameter#SINGLE_VALUED SINGLE_VALUED} 
     *                      if the parameter can contain only a single value
     * @throws              IllegalArgumentException if any of the specified
     *                      parameters are invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     */
    public TimeParam(String tag, 
                     String desc, 
                     boolean optional, 
                     boolean multiValued) {
        this(tag, desc, optional, multiValued, PUBLIC);
    }

    /**
     * constructor - creates a parameter which will
     * will be either optional or required, single or multi-valued, and
     * hidden or public as specified.
     *
     * @param tag           a unique identifier for this parameter
     * @param desc          a description of the parameter, suitable for display
     *                      in a usage statement
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
     * @throws              IllegalArgumentException if any of the specified
     *                      parameters are invalid.
     * @see                 AbstractParameter#setTag(String) setTag()
     * @see                 AbstractParameter#setDesc(String) setDesc()
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     * @see                 Parameter#HIDDEN HIDDEN
     * @see                 Parameter#PUBLIC PUBLIC
     */
    public TimeParam(String tag, 
                     String desc, 
                     boolean optional, 
                     boolean multiValued,
                     boolean hidden) {

        this.setTag(tag);
        this.setDesc(desc);
        this.setOptional(optional);
        this.setMultiValued(multiValued);
        this.setHidden(hidden);
        this.setOptionLabel(sTimeFmtDisplay);
    }

    /**
     * constructor - creates a single-valued, optional, public,
     * number parameter whose value must be one of the specified values.
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
     */
    public TimeParam(String tag, String desc, String[] acceptableValues) {
        this(tag, desc, acceptableValues, OPTIONAL, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a single-valued, public,
     * number parameter whose value must be one of the specified values,
     * and which is required or optional, as specified.
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
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     */
    public TimeParam(String tag, 
                     String desc, 
                     String[] acceptableValues,
                     boolean optional) {
        this(tag, desc, acceptableValues, optional, SINGLE_VALUED, PUBLIC);
    }

    /**
     * constructor - creates a public
     * number parameter whose value must be one of the specified values,
     * and which is required or optional and/or multi-valued, as specified.
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
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     */
    public TimeParam(String tag, 
                     String desc, 
                     String[] acceptableValues,
                     boolean optional,
                     boolean multiValued) {
        this(tag, desc, acceptableValues, optional, multiValued, PUBLIC);
    }

    /**
     * constructor - creates a 
     * Parameter, all of whose options are specified.
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
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     * @see                 Parameter#HIDDEN HIDDEN
     * @see                 Parameter#PUBLIC PUBLIC
     */
    public TimeParam(String tag, 
                     String desc, 
                     String[] acceptableValues,
                     boolean optional,
                     boolean multiValued,
                     boolean hidden) {
        this.setTag(tag);
        this.setAcceptableValues(acceptableValues);
        this.setDesc(desc);
        this.setOptional(optional);
        this.setMultiValued(multiValued);
        this.setHidden(hidden);
        this.setOptionLabel(sTimeFmtDisplay);
    }


    /**
     * Verifies that <code>value</code> is valid for this entity - called by
     * add/setValue(s)().
     *
     * @param value         the value to be validated
     * @throws              CmdLineException if <code>value</code> is not valid.
     */
    public void validateValue(String val) throws CmdLineException {
        super.validateValue(val);
        String fullval = fullTime(val);
        if (acceptableTimes != null) {
            for (int i = 0; i < acceptableTimes.length; i++) {
                if (fullval.equals(fullTime(acceptableTimes[i]))) {
                    return;
                }
            }
            int maxExpectedAVLen = 200;
            StringBuffer b = new StringBuffer(maxExpectedAVLen);
            for (int i = 0; i < acceptableTimes.length; i++) {
                b.append("\n   " + acceptableTimes[i]);
            }
            throw new CmdLineException(Strings.get(
                    "Parameter.valNotAcceptableVal",
                    new Object[] { val, tag, b.toString() }));
        }
        
        try {
            if (fullval.length() > 12) { throw new Exception(); }
            int n = Integer.parseInt(fullval.substring(0,2));
            if (n < 0 || n > 23) { throw new Exception(); }
            n = Integer.parseInt(fullval.substring(3,5));
            if (n < 0 || n > 59) { throw new Exception(); }
            n = Integer.parseInt(fullval.substring(6,8));
            if (n < 0 || n > 59) { throw new Exception(); }
            n = Integer.parseInt(fullval.substring(9,12));
            if (n < 0 || n > 999) { throw new Exception(); }
        } catch (Exception e) {
            throw new CmdLineException(Strings.get(
                "TimeParam.invalidTimeFormat", 
                new Object[] { val, sTimeFmtDisplay }));
        }
    }

    /**
     * Returns a <code>Date</code> object whose date portion is taken from the 
     * passed <code>Date</code> object, and whose time portion is
     * taken from the first value set.
     *
     * @param datePortion   The date portion of the returned date will match
     *                      the date portion of this object.  Any time
     *                      portion of this object will be discarded, and
     *                      the time associated with the initial value of
     *                      this parameter substituted.
     * @return              See the paramter description.
     */
    public Date getDate(Date datePortion) {
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yy");
        String sDate = fmt.format(datePortion);
        Date ret;
        try {
            fmt = new SimpleDateFormat("MM/dd/yy " + sTimeFmt);
            ret = fmt.parse(sDate + " " + fullTime(getValue()));
        } catch (ParseException e) {
            // should never happen!!
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * Returns <code>Date</code> objects whose date portion is taken from the 
     * passed <code>Date</code> object, and whose time portion is
     * taken from the first value set.
     *
     * @param datePortion   The date portion of the returned dates will match
     *                      the date portion of this object.  Any time
     *                      portion of this object will be discarded, and
     *                      the time associated with the values of
     *                      this parameter substituted.
     * @return              See the paramter description.
     */
    public Date[] getDates(Date datePortion) {
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yy");
        String sDate = fmt.format(datePortion);

        Collection vals = getValues();
        Date[] ret = new Date[vals.size()];
        fmt = new SimpleDateFormat("MM/dd/yy " + sTimeFmt);
        int i = 0;
        for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
            try {
                ret[i] = fmt.parse(sDate + " " + fullTime((String)itr.next()));
                i++;
            } catch (ParseException e) {
                // should never happen!!
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * Gets the number of milliseconds represented by the first value
     * of the parameter.
     * This is equal to:
     * <pre>
     * hours*MILLIS_PER_HOUR + mins*MILLIS_PER_MINUTE + secs*MILLIS_PER_SECOND + ms
     * </pre>
     *
     * @return              the number of milliseconds represented by the 
     *                      first value of the parameter
     */
    public long getMilliValue() {
        String val = fullTime(getValue());
        long ret;
        try {
            ret = calculateMillis(val);
        } catch (NumberFormatException e) {
            // should never happen
            throw new RuntimeException(e);
        }
        return ret;
    }

    /**
     * Gets the number of milliseconds represented by all values.
     *
     * @return              the number of milliseconds represented by all values
     * @see #getMilliValue()
     */
    public long[] getMilliValues() {
        Collection vals = getValues();
        long[] ret = new long[vals.size()];
        int i = 0;
        for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
            String val = fullTime((String)itr.next());
            try {
                ret[i] = calculateMillis(val);
                i++;
            } catch (NumberFormatException e) {
                // should never happen
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * Convenience method to calculate the number of milliseconds a String
     * time represents.
     *
     * @param time          the String time
     * @return              the number of milliseconds represented by <code>
     *                      time</code>
     * @throws  NumberFormatException if <code>time</code> does not parse
     *          as it should
     */
    private long calculateMillis(String val) throws NumberFormatException {
        return Long.parseLong(val.substring(0,2)) * MILLIS_PER_HOUR +
               Long.parseLong(val.substring(3,5)) * MILLIS_PER_MINUTE +
               Long.parseLong(val.substring(6,8)) * MILLIS_PER_SECOND +
               Long.parseLong(val.substring(9,12));
    }

    /**
     * Gets the first value with all time components filled in, from the
     * defaults, if necessary.  This method is guaranteed to return a
     * String in the format "HH:mm:ss:SSS", even if the user did not 
     * specify second or millisecond components (unlike <code>getValue()
     * </code>, which returns exactly what the user specified).
     *
     * @return              see description
     */
    public String getFullValue() {
        return fullTime(getValue());
    }

    /**
     * Gets the values with all time components filled in, from the
     * defaults, if necessary.  This method is guaranteed to return 
     * Strings in the format "HH:mm:ss:SSS", even if the user did not 
     * specify second or millisecond components (unlike <code>getValues()
     * </code>, which returns exactly what the user specified).
     *
     * @return              see description
     */
    public String[] getFullValues() {
        Collection vals = getValues();
        String[] ret = new String[vals.size()];
        int i = 0;
        for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
            ret[i] = fullTime((String)itr.next());
        }
        return ret;
    }

    /**
     * Sets the seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     *
     * @param defaultSeconds      the seconds default to use if not specified 
     *                            by the user
     * @see #getDefaultSeconds()
     */
    public void setDefaultSeconds(int defaultSeconds) {
        if (defaultSeconds < 0 || defaultSeconds > 59) {
            throw new IllegalArgumentException(Strings.get(
                "TimeParam.invalidSeconds", 
                new Object[] { new Integer(defaultSeconds) }));
        }
        this.defaultSeconds = defaultSeconds;
    }

    /**
     * Gets the seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     *
     * @return              the seconds default to use if not specified by 
     *                      the user
     * @see #setDefaultSeconds(int) setDefaultSeconds()
     */
    public int getDefaultSeconds() {
        return defaultSeconds;
    }

    /**
     * Sets the default millisecond value to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     *
     * @param defaultMilliSeconds       the default millisecond value to use if 
     *                                  not specified by the user
     * @see #getDefaultMilliSeconds()
     */
    public void setDefaultMilliSeconds(int defaultMilliSeconds) {
        if (defaultMilliSeconds < 0 || defaultMilliSeconds > 999) {
            throw new IllegalArgumentException(Strings.get(
                "TimeParam.invalidMilliSeconds", 
                new Object[] { new Integer(defaultMilliSeconds) }));
        }
        this.defaultMilliSeconds = defaultMilliSeconds;
    }

    /**
     * Gets the default millisecond value to use if not specified by the user.
     * This will default to 0 if never specified for a TimeParam object.
     *
     * @return              the default millisecond value to use if not 
     *                      specified by the user
     * @see #setDefaultMilliSeconds(int) setDefaultMilliSeconds()
     */
    public int getDefaultMilliSeconds() {
        return defaultMilliSeconds;
    }

    /**
     * Sets acceptable values for this Parameter.
     *
     * @param vals          A Collection of Strings representing
     *                      the acceptable times.  If the seconds and/or
     *                      milliseconds portion of the time is left off,
     *                      the default values will be used when user input
     *                      is compared.
     */
    public void setAcceptableValues(Collection vals) {
        if (vals == null || vals.size() == 0) {
            acceptableTimes = null;
        } else {
            acceptableTimes = new String[vals.size()];
            int i = 0;
            for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
                acceptableTimes[i] = itr.next().toString();
                i++;
            }
        }
    }

    /**
     * Sets acceptable values for this Parameter.
     *
     * @param vals          An array of Strings representing
     *                      the acceptable times.  If the seconds and/or
     *                      milliseconds portion of the time is left off,
     *                      the default values will be used when user input
     *                      is compared.
     */
    public void setAcceptableValues(String[] vals) {
        if (vals == null || vals.length == 0) {
            acceptableTimes = null;
        } else {
            acceptableTimes = vals;
        }
    }

    /**
     * Gets the values that are acceptable for this parameter, if a restricted
     * set exists.  If there is no restricted set of acceptable values, null
     * is returned.
     *
     * @return              a set of acceptable values for the Parameter, or
     *                      null if there is none.
     * @see #setAcceptableValues(String[]) setAcceptableValues()
     */
    public String[] getAcceptableValues() {
        return acceptableTimes;
    }

    /**
     * Converts a String value to its Date equivalent, filling in default
     * seconds and milliseconds as necessary.
     *
     * @param val           the String to be converted
     * @return              the Date object represented by <code>val</code>
     */
    private String fullTime(String val) {
        if (val.length() == 5) {
            val = val + ":" + secondFmt.format(defaultSeconds);
        }
        if (val.length() == 8) {
            val = val + ":" + msFmt.format(defaultMilliSeconds);
        }
        return val;
    }
}
