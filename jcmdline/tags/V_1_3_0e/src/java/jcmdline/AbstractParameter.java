/*
 * AbstractParameter.java
 *
 * Classes:
 *   public   AbstractParameter
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
import java.util.Collection;
import java.util.Iterator;

/**
 * Base class for command line parameters.
 * <P>
 *  To implement a concrete Parameter class by subclassing this class,
 *  the following should be done:
 *  <ul>
 *     <li>Override the {@link #validateValue(String) validateValue()} method.
 *         This method is called any time an attempt is made to add or set
 *         values.
 *     <li>Implement constructors applicable for the new type of Parameter.
 *     <li>Call {@link #setOptionLabel(String) setOptionLabel()} from the
 *         constructors to set a
 *         reasonable option label for the new type of parameter.
 *     <li>Provide type-specific access methods for retrieval of the Parameter
 *         value.  For instance, {@link FileParam} provides the 
 *         <code>getFiles()</code> method to retrieve its values as <code>File
 *         </code> objects.
 *  </ul>
 * <p>
 * A simple Parameter class that accepts only strings of a specified length
 * might look as follows:
 * <pre>
 * public class FixedLenParam extends AbstractParameter {
 * 
 *     private int length;
 * 
 *     public FixedLenParam(String tag, String desc, int length) {
 *         setTag(tag);
 *         setDesc(desc);
 *         this.length = length;
 *         setOptionLabel("&lt;s&gt;");
 *     }
 * 
 *     public void validateValue(String val) throws CmdLineException {
 *         super.validateValue(val); // check acceptable values, etc..
 *         if (val.length() != length) {
 *             throw new CmdLineException(getTag() + " must be a string of " +
 *                                        length + " characters in length");
 *         }
 * 
 *     }
 * }
 * </pre>
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: AbstractParameter.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public abstract class AbstractParameter implements Parameter {

    /**
     * the tag which uniquely identifies the parameter, and will be used to
     * identify the parameter on the command line if the parameter is used
     * as an option
     */
    protected String tag;

    /**
     * Indicates whether or not the parameter is optional.  The default is 
     * <code>true</code>, indicating that the parameter is optional.
     */
    protected boolean optional = true;

    /**
     * Indicates whether the parameter can have multiple values.  
     * The default is
     * false, indicating that the parameter can only accept a single value.
     */
    protected boolean multiValued = false;

    /**
     * a description of the parameter to be displayed in the usage */
    protected String desc;

    /**
     * indicates that the parameter is hidden and will not be displayed in the
     * normal usage - default is <code>false</code>
     */
    protected boolean hidden = false;

    /**
     * indicates that the value of the parameter has been set
     */
    protected boolean set;

    /**
     * the value(s) of the entity
     */
    protected ArrayList values = new ArrayList();

    /**
     * The label that should be used for a Parameter option's value in the
     * usage
     * @see #setOptionLabel(String) setOptionLabel()
     * @see #getOptionLabel()
     */
    protected String optionLabel = null;

    /**
     * a set of restricted values the Parameter may take
     * @see #setAcceptableValues(String[]) setAcceptableValues()
     * @see #getAcceptableValues()
     */
    protected String[] acceptableValues;

    /**
     * During parse, ignore missing required Parameters if this Parameter is 
     * set.  Typically used by Parameters that cause an action then call 
     * System.exit(), like "-help".
     * @see #setIgnoreRequired(boolean) setIgnoreRequired()
     * @see #getIgnoreRequired()
     */
    protected boolean ignoreRequired;

    /**
     * gets an indicator that the parameter's value has been set
     *
     * @return              true if the parameter's value has been set, false 
     *                      otherwise
     */
    public boolean isSet() {
        return set;
    }

    /**
     * sets the value of the hidden indicator
     *
     * @param hidden    true ({@link #HIDDEN}) if the parameter is a 
     *                  hidden parameter
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * gets the value of the hidden indicator
     *
     * @return          true ({@link #HIDDEN}) if the parameter is a 
     *                  hidden parameter
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * sets the value of this parameter's description
     *
     * @param desc      a description of the parameter, suitable for display in 
     *                  the command's usage
     * @throws          IllegalArgumentException if <code>desc</code> is
     *                  fewer than 5 charaters.
     */
    public void setDesc(String desc) throws IllegalArgumentException {
        int minDescLen = 5;
        if (desc.length() < minDescLen) {
            throw new IllegalArgumentException(Strings.get(
                    "AbstractParameter.descTooShort", new Object[] { tag }));
        }
        this.desc = desc;
    }

    /**
     * gets the value of the parameter's description
     *
     * @return              this parameter's description
     */
    public String getDesc() {
        return desc;
    }

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
    public void setTag(String tag) throws IllegalArgumentException {
        if (tag == null || tag.length() < 1) {
            throw new IllegalArgumentException(Strings.get(
                    "AbstractParameter.emptyTag"));
        }
        if (tag.indexOf("=") != -1) {
            throw new IllegalArgumentException(Strings.get(
                    "AbstractParameter.illegalCharInTag",
                    new Object[] { tag, "=" }));
        }
        this.tag = tag;
    }

    /**
     * gets the value of tag
     *
     * @return          a unique identifier for this parameter
     * @see             #setTag(String) setTag()
     */
    public String getTag() {
        return tag;
    }

    /**
     * sets the value of the multiValued indicator
     *
     * @param multiValued      true if the parameter can have multiple values
     */
    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    /**
     * gets the value of multiValued indicator
     *
     * @return              true if the parameter can have multiple values
     */
    public boolean isMultiValued() {
        return multiValued;
    }

    /**
     * indicates whether or not the parameter is optional
     *
     * @param optional      true if the parameter is optional
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * returns the value of the optional indicator
     *
     * @return              true if the parameter is optional
     */
    public boolean isOptional() {
        return optional;
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
        return acceptableValues;
    }

    /**
     * Sets the values that are acceptable for this parameter, if a restricted
     * set exists.  A null <code>vals</code> value, or an empty <code>vals</code>
     * array, will result in any previously set acceptable values being cleared.
     *
     * @param vals          the new acceptable values
     * @see #getAcceptableValues()
     */
    public void setAcceptableValues(String[] vals) {
        if (vals == null || vals.length == 0) {
            acceptableValues = null;
        } else {
            acceptableValues = vals;
        }
    }

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
    public void setAcceptableValues(Collection vals) {
        if (vals == null || vals.size() == 0) {
            acceptableValues = null;
        } else {
            acceptableValues = new String[vals.size()];
            int i = 0;
            for (Iterator itr = vals.iterator(); itr.hasNext(); ) {
                acceptableValues[i] = itr.next().toString();
                i++;
            }
        }
    }

    /**
     * Adds the specified string as a value for this entity after calling
     * validateValue() to validate.
     *
     * @param value the value to be added
     * @throws      CmdLineException if the value of the entity
     *              has already been set and <code>multiValued</code> is
     *              not <code>true</code>, or if 
     *              {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void addValue(String value) throws CmdLineException {
        if (values.size() >= 1 && !multiValued) {
            throw new CmdLineException(Strings.get(
                    "AbstractParameter.specifiedMoreThanOnce", 
                    new Object[] { tag }));
        }
        validateValue(value);
        values.add(value);
        set = true;
    }

    /**
     * Sets the value of the parameter to the specified string after calling
     * validateValue() to validate.
     *
     * @param value         the new value of the parameter
     * @throws      if {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void setValue(String value) throws CmdLineException {
        values.clear();
        addValue(value);        // Let addValue() validate
    }

    /**
     * Sets the values of the parameter to those specified after calling
     * validateValue() to validate.
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
    public void setValues(Collection values) throws CmdLineException {
        this.values.clear();
        for (Iterator itr = values.iterator(); itr.hasNext(); ) {
            addValue((String) itr.next());  // let addValue() validate
        }
    }

    /**
     * Sets the values of the parameter to those specified after calling
     * validateValue() to validate.
     *
     * @param values        The String objects to be used as the
     *                      parameter's values.
     * @throws      CmdLineException if more than one value is specified
     *              and <code>multiValued</code> is not <code>true</code>, or 
     *              if {@link #validateValue(String) validateValue()}
     *              detects a problem.
     */
    public void setValues(String[] values) throws CmdLineException {
        this.values.clear();
        for (int i = 0; i < values.length; i++) {
            addValue(values[i]);  // let addValue() validate
        }
    }

    /**
     * Verifies that <code>value</code> is valid for this entity.
     * <p>
     * The implementation in AbstractParameter is to verify that, if there
     * are specific acceptableValues associated with the Parameter, the 
     * value is one of those specified.  Any additional validation must
     * be done by a subclass.  Because the validation performed by this
     * method is applicable to most, if not all Parameters, it is recommended
     * that subclasses call it from within their override methods:
     * <pre>
     * public void validateValue(String value) throws CmdLineException {
     *    super.validateValue(value);
     *    // do some subclass-specific validation
     *    .
     *    .
     * }
     * </pre>
     *
     * @param value         the value to be validated
     * @throws              CmdLineException if <code>value</code> is not valid.
     */
    public void validateValue(String value) throws CmdLineException {
        if (acceptableValues != null) {
            for (int i = 0; i < acceptableValues.length; i++) {
                if (value.equals(acceptableValues[i])) {
                    return;
                }
            }
            int maxExpectedAVLen = 200;
            StringBuffer b = new StringBuffer(maxExpectedAVLen);
            for (int i = 0; i < acceptableValues.length; i++) {
                b.append("\n   " + acceptableValues[i]);
            }
            throw new CmdLineException(Strings.get(
                    "Parameter.valNotAcceptableVal",
                    new Object[] { value, tag, b.toString() }));
        }
    }

    /**
     * The value of the parameter, in the case where the parameter is not 
     * multi-valued.  For a multi-valued parameter, the first value specified
     * is returned.
     *
     * @return              The value of the parameter as a String, or null if
     *                      the paramter has not been set.
     * @see                 #getValues()
     */
    public String getValue() {
        if (values.size() == 0) {
            return null;
        }
        return (String) values.get(0);
    }

    /**
     * gets the values associated with this Parameter
     *
     * @return              The values associated with this Parameter.  Note
     *                      that this might be an empty Collection if the 
     *                      Parameter has not been set.
     * @see #isSet()
     */
    public Collection getValues() {
        return values;
    }

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
    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    /**
     * gets the value of optionLabel
     *
     * @return              the string used as a label for the parameter's
     *                      value
     * @see #setOptionLabel(String) setOptionLabel()
     */
    public String getOptionLabel() {
        return ((optionLabel == null) ? "" : optionLabel);
    }

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
    public void setIgnoreRequired(boolean ignoreRequired) {
        this.ignoreRequired = ignoreRequired;
    }

    /**
     * Gets the flag indicating that during parse, missing required 
     * Parameters are ignored
     * if this Parameter is set.  Typically used by Parameters that cause an 
     * action then call System.exit(), like "-help".
     *
     * @return              <code>true</code> if missing required Parameters
     *                      will be ignored when this Parameter is set.
     * @see #setIgnoreRequired(boolean) setIgnoreRequired()
     */
    public boolean getIgnoreRequired() {
        return ignoreRequired;
    }
}

