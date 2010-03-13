/*
 * DateParam.java
 *
 * jcmdline Rel. @VERSION@ $Id: DateParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   DateParam
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
 * A parameter that accepts a date as its value.
 * <p>
 * The format for the date is taken from the <code>strings</code>
 * ResourceBundle.
 *  <p>
 *  Sample Usage:
 *  <pre>
 *     DateParam startDateParam = 
 *         new DateParam("startDate", 
 *                       "start date of report", 
 *                       DateParam.REQUIRED);
 *     DateParam endDateParam = 
 *         new DateParam("endDate", 
 *                       "end date of report", 
 *                       DateParam.REQUIRED);
 * 
 *     // Time for startDate will be the beginning of the day by default.
 *     // Set the time for the end of the report to be the end of the day.
 *     endDateParam.setDefaultTime(23, 59, 58, 999);
 * 
 *     CmdLineHandler cl = new DefaultCmdLineHandler(
 *         "myreport", "report of activity over days",
 *         new Parameter[] {}, 
 *         new Parameter[] { startDateParam, endDateParam });
 *     
 *     cl.parse();
 * 
 *     // Don't need to check isSet() because params are REQUIRED
 *     Date stDate = startDateParam.getDate();
 *     Date enDate = endDateParam.getDate();
 *     .
 *     .
 *  </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: DateParam.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see DateTimeParam
 * @see TimeParam
 */
public class DateParam extends AbstractParameter {

    private static final String sDateFmt = 
        Strings.get("DateParam.dateFormat");
    private static final String sTimeFmt = "HH:mm:ss:SSS";

    private static final SimpleDateFormat dateFmt = 
        new SimpleDateFormat(sDateFmt);
    private static final SimpleDateFormat dateFmtWTime = 
        new SimpleDateFormat(sDateFmt + " " + sTimeFmt);
    private static final DecimalFormat hmsFmt = new DecimalFormat("00");
    private static final DecimalFormat msFmt = new DecimalFormat("000");

    private Date date = null;

    /**
     * The default hours to be added to the date - defaults to 0
     * @see #setDefaultTime(int,int,int,int) setDefaultTime()
     * @see #getDefaultTime()
     */
    private int defaultHours = 0;

    /**
     * The default minutes to be added to the date - defaults to 0
     * @see #setDefaultTime(int,int,int,int) setDefaultTime()
     * @see #getDefaultTime()
     */
    private int defaultMinutes = 0;

    /**
     * The default seconds to be added to the date - defaults to 0
     * @see #setDefaultTime(int,int,int,int) setDefaultTime()
     * @see #getDefaultTime()
     */
    private int defaultSeconds = 0;

    /**
     * The default milliseconds to be added to the date - defaults to 0
     * @see #setDefaultTime(int,int,int,int) setDefaultTime()
     * @see #getDefaultTime()
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
    public DateParam(String tag, String desc) {
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
    public DateParam(String tag, String desc, boolean optional) {
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
    public DateParam(String tag, 
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
    public DateParam(String tag, 
                     String desc, 
                     boolean optional, 
                     boolean multiValued,
                     boolean hidden) {

        this.setTag(tag);
        this.setDesc(desc);
        this.setOptional(optional);
        this.setMultiValued(multiValued);
        this.setHidden(hidden);
        this.setOptionLabel(sDateFmt);
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
    public DateParam(String tag, String desc, Date[] acceptableValues) {
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
    public DateParam(String tag, 
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
    public DateParam(String tag, 
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
    public DateParam(String tag, 
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
        this.setOptionLabel(sDateFmt);
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
                "DateParam.invalidDate",
                new Object[] { getTag(), sDateFmt }));
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
     * Gets the format used to parse the date/time values.
     *
     * @return              the format used to parse the date/time values
     */
    public static String getParseFormat() {
        return dateFmt.toLocalizedPattern();
    }

    /**
     * Sets the values that will be acceptable for this Parameter using
     * Date objects.  Any time portion of the Date objects will be stripped.
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
     * Sets acceptable values for this Parameter from a Collection of 
     * Date objects.  Any time portion of the Date objects will be stripped.
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
     * @throws  UnsupportedOperationException
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
     * @throws  UnsupportedOperationException
     * @see #setAcceptableDates(Date[])
     * @see #setAcceptableDates(Collection)
     */
    public void setAcceptableValues(String[] vals) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets default values for the time component used to generate the Date
     * value.
     *
     * @param h             the hours - 0-23 - defaults to 0
     * @param m             the minutes - 0-59 - defaults to 0
     * @param s             the seconds - 0-59 - defaults to 0
     * @param ms            the milliseconds - 0-999 - defaults to 0
     * @throws  IllegalArgumentException if any of the parameters are in 
     *          error.
     */
    public void setDefaultTime(int h, int m, int s, int ms) {
        if (h < 0 || h > 23) {
            throw new IllegalArgumentException(Strings.get(
                "DateParam.invalidHours", new Object[] { new Integer(h) }));
        }
        if (m < 0 || m > 59) {
            throw new IllegalArgumentException(Strings.get(
                "DateParam.invalidMinutes", new Object[] { new Integer(m) }));
        }
        if (s < 0 || s > 59) {
            throw new IllegalArgumentException(Strings.get(
                "DateParam.invalidSeconds", new Object[] { new Integer(s) }));
        }
        if (ms < 0 || ms > 999) {
            throw new IllegalArgumentException(Strings.get(
                "DateParam.invalidMilliSeconds", 
                new Object[] { new Integer(ms) }));
        }
        defaultHours = h;
        defaultMinutes = m;
        defaultSeconds = s;
        defaultMilliSeconds = ms;
    }

    /**
     * Gets default values for the time component used to generate the Date
     * value.
     *
     * @return  a 4 element <code>int</code> array, where the elements are
     *          the default hours, minutes, seconds, and milliseconds, in 
     *          that order
     */
    public int[] getDefaultTime() {
        return new int[] { defaultHours, defaultMinutes, 
                           defaultSeconds, defaultMilliSeconds };
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
        String sTime = hmsFmt.format(defaultHours) + ":" +
                       hmsFmt.format(defaultMinutes) + ":" +
                       hmsFmt.format(defaultSeconds) + ":" +
                       msFmt.format(defaultMilliSeconds);
        return dateFmtWTime.parse(val + " " + sTime);
    }
}
