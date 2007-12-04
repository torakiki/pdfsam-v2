/*
 * DateTimeParam.java
 *
 * jcmdline Rel. @VERSION@ $Id: DateTimeParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   DateTimeParam
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
 * A parameter that accepts a date and time as its value.
 * <p>
 * The format for the date is taken from the <code>strings</code>
 * ResourceBundle.  The format for the time is "HH:mm:ss:SSS", where
 * the seconds and/or milliseconds portion may be left off by the user,
 * in which case they will be defaulted.
 * <P>
 *  Sample Usage:
 *  <pre>
 *     DateTimeParam startTimeParam = 
 *         new DateTimeParam("startTime", 
 *                           "start time of report", 
 *                           DateTimeParam.REQUIRED);
 *     DateTimeParam endTimeParam = 
 *         new DateTimeParam("endTime", 
 *                           "end time of report", 
 *                           DateTimeParam.REQUIRED);
 * 
 *     // Seconds and millis for startTime will both be 0 by default.
 *     // Set the seconds and millis for the end of the report to be the end 
 *     // of a minute.
 *     endTimeParam.setDefaultSeconds(59);
 *     endTimeParam.setDefaultMilliSeconds(999);
 * 
 *     CmdLineHandler cl = new DefaultCmdLineHandler(
 *         "myreport", "generate activity report",
 *         new Parameter[] {}, 
 *         new Parameter[] { startTimeParam, endTimeParam });
 *     
 *     cl.parse();
 * 
 *     // Don't need to check isSet() because params are REQUIRED
 *     Date stTime = startTimeParam.getDate();
 *     Date enTime = endTimeParam.getDate();
 *     .
 *     .
 *  </pre>
 *  This will result in a command line that may be executed as:
 *  <pre>
 *   myreport "09/23/59 10:12" "09/23/59 23:34"
 *  </pre>
 *  or
 *  <pre>
 *   myreport "09/23/59 10:12:34:567" "09/23/59 23:34:34:567"
 *  </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: DateTimeParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see DateParam
 * @see TimeParam
 */
public class DateTimeParam extends AbstractParameter {

    private static final String sDateFmt = 
        Strings.get("DateTimeParam.dateFormat");
    private static final String sTimeFmt = "HH:mm:ss:SSS";
    private static final String sTimeFmtDisplay = "HH:mm[:ss[:SSS]]";

    private static final SimpleDateFormat dateFmt = 
        new SimpleDateFormat(sDateFmt + " " + sTimeFmt);
    private static final DecimalFormat secondFmt = new DecimalFormat("00");
    private static final DecimalFormat msFmt = new DecimalFormat("000");

    private Date date = null;

    /**
     * The seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a DateTimeParam object.
     * @see #setDefaultSeconds(int) setDefaultSeconds()
     * @see #getDefaultSeconds()
     */
    private int defaultSeconds = 0;

    /**
     * The default millisecond value to use if not specified by the user.
     * This will default to 0 if never specified for a DateTimeParam object.
     * @see #setDefaultMilliSeconds(int) setDefaultMilliSeconds()
     * @see #getDefaultMilliSeconds()
     */
    private int defaultMilliSeconds = 0;

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
    public DateTimeParam(String tag, String desc) {
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
    public DateTimeParam(String tag, String desc, boolean optional) {
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
    public DateTimeParam(String tag, 
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
    public DateTimeParam(String tag, 
                         String desc, 
                         boolean optional, 
                         boolean multiValued,
                         boolean hidden) {

        this.setTag(tag);
        this.setDesc(desc);
        this.setOptional(optional);
        this.setMultiValued(multiValued);
        this.setHidden(hidden);
        this.setOptionLabel(sDateFmt + " " + sTimeFmtDisplay);
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
     * @see                 #setAcceptableDates(Date[]) setAcceptableDates()
     */
    public DateTimeParam(String tag, String desc, Date[] acceptableValues) {
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
     * @see                 #setAcceptableDates(Date[]) setAcceptableDates()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     */
    public DateTimeParam(String tag, 
                         String desc, 
                         Date[] acceptableValues,
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
     * @see                 #setAcceptableDates(Date[]) setAcceptableDates()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     */
    public DateTimeParam(String tag, 
                         String desc, 
                         Date[] acceptableValues,
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
     * @see                 #setAcceptableDates(Date[]) setAcceptableDates()
     * @see                 Parameter#OPTIONAL OPTIONAL
     * @see                 Parameter#REQUIRED REQUIRED
     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
     * @see                 Parameter#HIDDEN HIDDEN
     * @see                 Parameter#PUBLIC PUBLIC
     */
    public DateTimeParam(String tag, 
                         String desc, 
                         Date[] acceptableValues,
                         boolean optional,
                         boolean multiValued,
                         boolean hidden) {
        this.setTag(tag);
        this.setAcceptableDates(acceptableValues);
        this.setDesc(desc);
        this.setOptional(optional);
        this.setMultiValued(multiValued);
        this.setHidden(hidden);
        this.setOptionLabel(sDateFmt + " " + sTimeFmtDisplay);
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
        try {
            stringToDate(val);
        } catch (ParseException e) {
            throw new CmdLineException(Strings.get(
                "DateTimeParam.invalidDate",
                new Object[] { getTag(), sDateFmt + " " + sTimeFmtDisplay }));
        }
    }

    /**
     * Returns the value of this Parameter as a java.util.Date object.
     * Should there be more than one value for this Parameter, the first
     * value will be returned.
     *
     * @return              the value of this Parameter as a Date object
     */
    public Date getDate() {
        String sVal = getValue();
        Date date = null;
        if (sVal != null) {
            try {
                date = stringToDate(sVal);
            } catch (ParseException e) {
                // Should never get here because all values would have been 
                // parsed as part of validateValue().
                throw new RuntimeException(e);
            }
        }
        return date;
    }

    /**
     * Returns the values of this Parameter as java.util.Date objects.
     *
     * @return              The values of this Parameter as Date objects.  Note
     *                      the the return value may be an empty array if no
     *                      values have been set.
     */
    public Date[] getDates() {
        Collection sVals = getValues();
        Date[] dates = new Date[sVals.size()];
        int i = 0;
        for (Iterator itr = sVals.iterator(); itr.hasNext(); ) {
            try {
                dates[i] = stringToDate((String)itr.next());
                i++;
            } catch (ParseException e) {
                // Should never get here because all values would have been 
                // parsed as part of validateValue().
                throw new RuntimeException(e);
            }
        }
        return dates;
    }

    /**
     * Sets the seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a DateTimeParam object.
     *
     * @param defaultSeconds      the seconds default to use if not specified 
     *                            by the user
     * @see #getDefaultSeconds()
     */
    public void setDefaultSeconds(int defaultSeconds) {
        this.defaultSeconds = defaultSeconds;
    }

    /**
     * Gets the seconds default to use if not specified by the user.
     * This will default to 0 if never specified for a DateTimeParam object.
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
     * This will default to 0 if never specified for a DateTimeParam object.
     *
     * @param defaultMilliSeconds       the default millisecond value to use if 
     *                                  not specified by the user
     * @see #getDefaultMilliSeconds()
     */
    public void setDefaultMilliSeconds(int defaultMilliSeconds) {
        this.defaultMilliSeconds = defaultMilliSeconds;
    }

    /**
     * Gets the default millisecond value to use if not specified by the user.
     * This will default to 0 if never specified for a DateTimeParam object.
     *
     * @return              the default millisecond value to use if not 
     *                      specified by the user
     * @see #setDefaultMilliSeconds(int) setDefaultMilliSeconds()
     */
    public int getDefaultMilliSeconds() {
        return defaultMilliSeconds;
    }

    /**
     * Gets the format used to parse the date/time values.
     *
     * @return              the format used to parse the date/time values
     */
    public static String getParseFormat() {
        return dateFmt.toLocalizedPattern();
    }

    /**
     * Sets the values that will be acceptable for this Parameter using
     * Date objects.
     *
     * @param dates         an array of acceptable dates
     */
    public void setAcceptableDates(Date[] dates) {
        String[] sDates = new String[dates.length];
        for (int i = 0; i < dates.length; i++) {
            sDates[i] = dateFmt.format(dates[i]);
        }
        super.setAcceptableValues(sDates);
    }

    /**
     * Gets the acceptable values as Date objects.
     *
     * @return              The acceptable values as an array of Date objects.
     *                      Note that null is returned if acceptable
     *                      values have not been set.
     */
    public Date[] getAcceptableDates() {
        String[] sVals = getAcceptableValues();
        if (sVals == null) {
            return null;
        }
        Date[] dates = new Date[sVals.length];
        for (int i = 0; i < sVals.length; i++) {
            try {
                dates[i] = stringToDate(sVals[i]);
            } catch (Exception e) {
                // should never get here - acceptable values parsed on the 
                // way in
                throw new RuntimeException(e);
            }
        }
        return dates;
    }

    /**
     * Sets acceptable values for this Parameter.
     *
     * @param vals          a Collection of java.util.Date objects representing
     *                      the acceptable values.
     * @throws  ClassCastException if any member of <code>vals</code> is not
     *          a Date object.
     */
    public void setAcceptableDates(Collection vals) {
        String[] sVals = new String[vals.size()];
        int i = 0;
        for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
            date = (Date)itr.next();
            sVals[i] = dateFmt.format(date);
            i++;
        }
        super.setAcceptableValues(sVals);
    }

    /**
     * Unsupported.  This method is unsupported because, with the date format
     * coming from a resource bundle, it is not reasonable to code string
     * values for acceptable values - better to use Date objects.
     *
     * @throws  UnsupportedOperationException;
     * @see #setAcceptableDates(Date[])
     * @see #setAcceptableDates(Collection)
     */
    public void setAcceptableValues(Collection vals) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported.  This method is unsupported because, with the date format
     * coming from a resource bundle, it is not reasonable to code string
     * values for acceptable values - better to use Date objects.
     *
     * @throws  UnsupportedOperationException;
     * @see #setAcceptableDates(Date[])
     * @see #setAcceptableDates(Collection)
     */
    public void setAcceptableValues(String[] vals) {
        throw new UnsupportedOperationException();
    }

    /**
     * Converts a String value to its Date equivalent, filling in default
     * seconds and milliseconds as necessary.
     *
     * @param val           the String to be converted
     * @return              the Date object represented by <code>val</code>
     * @throws  ParseException if <code>val</code> will not parse to a Date.
     */
    private Date stringToDate(String val) throws ParseException {
        if (val.length() == sDateFmt.length() + 6) {
            val = val + ":" + secondFmt.format(defaultSeconds);
        }
        if (val.length() == sDateFmt.length() + 9) {
            val = val + ":" + msFmt.format(defaultMilliSeconds);
        }
        return dateFmt.parse(val);
    }
}
