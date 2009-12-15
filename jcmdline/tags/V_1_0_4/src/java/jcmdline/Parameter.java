/*
 * Parameter.java
 *
 * Interface:
 *   public   Parameter
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
 * Interface for command line parameters.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: Parameter.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public interface Parameter {

    /**
     * when used as a value for the <code>hidden</code> indicator, 
     * indicates that a parameter is public, and its description will be
     * listed in the usage.
     */
    public static final boolean PUBLIC = false;

    /**
     * when used as a value for the <code>hidden</code> indicator, 
     * indicates that a parameter is hidden, and its description will 
     * <b>not</b> be listed in the usage.
     */
    public static final boolean HIDDEN = true;

    /**
     * when used as a value for the <code>optional</code> indicator,
     * specifies that an parameter is optional
     */
    public static final boolean OPTIONAL = true;

    /**
     * when used as a value for the <code>optional</code> indicator,
     * specifies that an parameter is required
     */
    public static final boolean REQUIRED = false;

    /**
     * when used as a value for the <code>multiValued</code> indicator,
     * specifies that an parameter accepts mulitiple values
     */
    public static final boolean MULTI_VALUED = true;

    /**
     * when used as a value for the <code>multiValued</code> indicator,
     * specifies that a parameter accepts only one value
     */
    public static final boolean SINGLE_VALUED = false;

    /**
     * gets an indicator that the parameter's value has been set
     *
     * @return              true if the parameter's value has been set, false 
     *                      otherwise
     */
    public boolean isSet() ;

    /**
     * gets the value of the hidden indicator
     *
     * @return          true ({@link #HIDDEN}) if the parameter is a 
     *                  hidden parameter
     */
    public boolean isHidden() ;

    /**
     * sets the value of the hidden indicator
     *
     * @param hidden    true ({@link #HIDDEN}) if the parameter is a 
     *                  hidden parameter
     */
    public void setHidden(boolean hidden) ;

    /**
     * gets the value of the parameter's description
     *
     * @return              this parameter's description
     */
    public String getDesc() ;

    /**
     * sets the value of this parameter's description
     *
     * @param desc      a description of the parameter, suitable for display in 
     *                  the command's usage
     * @throws          IllegalArgumentException if <code>desc</code> is
     *                  fewer than 5 charaters.
     */
    public void setDesc(String desc) throws IllegalArgumentException ;

    /**
     * gets the value of tag
     *
     * @return          a unique identifier for this parameter
     */
    public String getTag() ;

    /**
     * sets the value of tag
     *
     * @param tag       a unique identifier for this parameter.  If the 
     *                  parameter is used as an option, it will be used to 
     *                  identify the option on the command line.  In the case
     *                  where the parameter is used as an argument, it will
     *                  only be used to identify the argument in the usage 
     *                  statement.  Tags must be made up of any character but 
     *                  '='.
     * @throws          IllegalArgumentException if the length of <code>tag
     *                  </code> is less than 1, or <code>tag</code> contains an
     *                  invalid character.
     */
    public void setTag(String tag) throws IllegalArgumentException ;

    /**
     * gets the value of multiValued indicator
     *
     * @return              true if the parameter can have multiple values
     */
    public boolean isMultiValued() ;

    /**
     * sets the value of the multiValued indicator
     *
     * @param multiValued      true if the parameter can have multiple values
     */
    public void setMultiValued(boolean multiValued) ;

    /**
     * returns the value of the optional indicator
     *
     * @return              true if the parameter is optional
     */
    public boolean isOptional() ;
    
    /**
     * indicates whether or not the parameter is optional
     *
     * @param optional      true if the parameter is optional
     */
    public void setOptional(boolean optional) ;

    /**
     * Gets the values that are acceptable for this parameter, if a restricted
     * set exists.  If there is no restricted set of acceptable values, null
     * is returned.
     *
     * @return              a set of acceptable values for the Parameter, or
     *                      null if there is none.
     */
    public String[] getAcceptableValues() ;

    /**
     * Sets the values that are acceptable for this parameter, if a restricted
     * set exists.  A null <code>vals</code> value, or an empty <code>vals</code>
     * array, will result in any previously set acceptable values being cleared.
     *
     * @param vals          the new acceptable values
     * @see #getAcceptableValues()
     */
    public void setAcceptableValues(String[] vals) ;

    /**
     * Sets the values that are acceptable for this parameter, if a restricted
     * set exists.  A null <code>vals</code> value, or an empty <code>vals</code>
     * Collection, will result in any previously set acceptable values being 
     * cleared.
     * <P>
     * The <code>toString()</code> values of the Objects in <code>vals</code> 
     * will be used for the acceptable values.
     *
     * @param vals          the new acceptable values
     */
    public void setAcceptableValues(Collection vals) ;

    /**
     * adds the specified string as a value for this entity
     *
     * @param value the value to be added
     * @throws      CmdLineException if the value of the entity
     *              has already been set and <code>multiValued</code> is
     *              not <code>true</code>, or if 
     *              {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void addValue(String value) throws CmdLineException ;

    /**
     * Sets the value of the parameter to the specified string.
     *
     * @param value         the new value of the parameter
     * @throws      if {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void setValue(String value) throws CmdLineException ;

    /**
     * Sets the values of the parameter to those specified.
     *
     * @param values        A collection of String objects to be used as the
     *                      parameter's values.
     * @throws      ClassCastException if the Collection contains object that
     *              are not Strings.
     * @throws      CmdLineException if more than one value is specified
     *              and <code>multiValued</code> is not <code>true</code>, or 
     *              if {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void setValues(Collection values) throws CmdLineException ;

    /**
     * Sets the values of the parameter to those specified.
     *
     * @param values        The String objects to be used as the
     *                      parameter's values.
     * @throws      CmdLineException if more than one value is specified
     *              and <code>multiValued</code> is not <code>true</code>, or 
     *              if {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void setValues(String[] values) throws CmdLineException ;

    /**
     * verifies that <code>value</code> is valid for this entity
     *
     * @param value         the value to be validated
     * @throws              CmdLineException if <code>value</code> is not valid.
     */
    public void validateValue(String value) throws CmdLineException;

    /**
     * The value of the parameter, in the case where the parameter is not 
     * multi-valued.  For a multi-valued parameter, the first value specified
     * is returned.
     *
     * @return              The value of the parameter as a String, or null if
     *                      the paramter has not been set.
     * @see                 #getValues()
     */
    public String getValue() ;

    /**
     * gets the values associated with this Parameter
     *
     * @return              The values associated with this Parameter.  Note
     *                      that this might be an empty Collection if the 
     *                      Parameter has not been set.
     * @see #isSet()
     */
    public Collection getValues() ;

    /**
     * gets the value of optionLabel
     *
     * @return              the string used as a label for the parameter's
     *                      value
     */
    public String getOptionLabel() ;

    /**
     * Sets the value of optionLabel.
     * This label will be used when the usage for the command is displayed.
     * For instance, a date parameter might use "&lt;mm/dd/yy&gt;".  This could
     * then be displayed as in the following usage.
     * <PRE>
     * st_date &lt;mm/dd/yy&gt;  the start date of the report
     * </PRE>
     * The default is the empty string.
     * @param optionLabel       The string used as a label for the parameter's
     *                          value.  If null, an empty string is used.
     * @see #getOptionLabel()
     */
    public void setOptionLabel(String optionLabel) ;

    /**
     * Gets the flag indicating that during parse, missing required 
     * Parameters are ignored
     * if this Parameter is set.  Typically used by Parameters that cause an 
     * action then call System.exit(), like "-help".
     *
     * @return              <code>true</code> if missing required Parameters
     *                      will be ignored when this Parameter is set.
     */
    public boolean getIgnoreRequired() ;

    /**
     * Sets a flag such that during parse, missing required Parameters are 
     * ignored
     * if this Parameter is set.  Typically used by Parameters that cause an 
     * action then call System.exit(), like "-help".
     *
     * @param ignoreRequired    set to <code>true</code> to ignore missing
     *                          required Parameters if this Parameter is set
     * @see #getIgnoreRequired()
     */
    public void setIgnoreRequired(boolean ignoreRequired) ;
}

