/*
 * PdfFileParam.java
 *
 * Classes:
 *   public   FileParam
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
 * Contributor(s):  Andrea Vacondio <andrea.vacondio@gmail.com>
 *
 * ***** END LICENSE BLOCK *****
 */
package jcmdline;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jcmdline.dto.PdfFile;

/**
 * Encapsulate a command line parameter whose value will be the name of
 * a pdf document amd its password (Ex. file.pdf:pwd).  Attributes, such as whether 
 * it must be readable, etc, may be specified and will be validated.
 * <p>
 * Usage:
 * <pre>
 * public static void main(String[] args) {
 * 
 *     PdfFileParam filesArg =
 *         new PdfFileParam("file",
 *                       "a file to be processed - defaults to stdin",
 *                       PdfFileParam.IS_READABLE,
 *                       PdfFileParam.OPTIONAL,
 *                       PdfFileParam.MULTI_VALUED);
 * 
 *    
 * }
 * </pre>
 *
 * @author          Andrea Vacondio
 * @version         jcmdline Rel. @VERSION@ $Id: PdfFileParam.java
 * @see CmdLineParser
 */
public class PdfFileParam extends AbstractParameter {


	    // Note: attributes are specified with kind of a "reverse map" so
	    // that they can be "ANDed" together when set, which is more natural
	    // since that is the way they are processed.

	    /**
	     * indicates that no file/dir attributes are required or will be checked
	     * @see #setAttributes(int) setAttributes()
	     */
	    public static final int NO_ATTRIBUTES = 0xffff;

	    /**
	     * indicates that a file specified as a value for this 
	     * PdfFileParam must exist
	     * @see #setAttributes(int) setAttributes()
	     */
	    public static final int EXISTS = 0xfffe;

	    /**
	     * indicates that a value specified for this PdfFileParam must name an 
	     * existing file for which the caller has read access
	     * @see #setAttributes(int) setAttributes()
	     */
	    public static final int IS_READABLE = 0xffef;

	    /**
	     * indicates that a value specified for this PdfFileParam must name an 
	     * existing file for which the caller has write access
	     * @see #setAttributes(int) setAttributes()
	     */
	    public static final int IS_WRITEABLE = 0xffdf;

	    /**
	     * the default label that will represent option values for this Parameter.
	     * The following demonstrates a possible usage for a PdfFileParam option, 
	     * where the option label is '&lt;file&gt;':
	     * <pre>
	     *    out &lt;file&gt;  the output file
	     * </pre>
	     * @see AbstractParameter#setOptionLabel(String) setOptionLabel()
	     * @see "<i>PdfFileParam.defaultFileOptionLabel</i> in 'strings' properties 
	     *      file"
	     */
	    public static final String DEFAULT_FILE_OPTION_LABEL = 
	            Strings.get("PdfFileParam.defaultFileOptionLabel");

	    /**
	     * Attributes which a file/directory value must have
	     * @see #setAttributes(int) setAttributes()
	     * @see #getAttributes()
	     */
	    private int attributes;

	    /**
	     * constructor - creates single-valued, optional, public parameter 
	     * which accepts any valid file name as its value
	     *
	     * @param tag           a unique identifier for this parameter
	     * @param desc          a description of the parameter, suitable for display
	     *                      in a usage statement
	     * @throws              IllegalArgumentException if <code>tag</code> 
	     *                      or <desc> are invalid.
	     * @see                 AbstractParameter#setTag(String) setTag()
	     * @see                 AbstractParameter#setDesc(String) setDesc()
	     */
	    public PdfFileParam(String tag, String desc) {
	        this(tag, desc, NO_ATTRIBUTES, OPTIONAL, SINGLE_VALUED, PUBLIC);
	    }

	    /**
	     * constructor - creates single-valued, public parameter which accepts 
	     * any valid file name as its value and is optional or 
	     * required, as specified.
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
	     * @see                 Parameter#OPTIONAL OPTIONAL
	     * @see                 Parameter#REQUIRED REQUIRED
	     */
	    public PdfFileParam(String tag, String desc, boolean optional) {
	        this(tag, desc, NO_ATTRIBUTES, optional, SINGLE_VALUED, PUBLIC);
	    }

	    /**
	     * constructor - creates a single-valued, optional, public, parameter 
	     * accepts a file name with the specified attributes.
	     *
	     * @param tag           a unique identifier for this parameter
	     * @param desc          a description of the parameter, suitable for display
	     *                      in a usage statement
	     * @param attributes    the attributes that must apply to a file or
	     *                      directory specified as a value to this FileParam
	     * @throws              IllegalArgumentException if any parameter is 
	     *                      invalid.
	     * @see                 AbstractParameter#setTag(String) setTag()
	     * @see                 AbstractParameter#setDesc(String) setDesc()
	     * @see                 #setAttributes(int) setAttributes()
	     */
	    public PdfFileParam(String tag, String desc, int attributes) {
	        this(tag, desc, attributes, OPTIONAL, SINGLE_VALUED, PUBLIC);
	    }

	    /**
	     * constructor - creates a single-valued, public, parameter 
	     * that accepts a file name with the specified attributes, 
	     * and which is required or optional, as specified.
	     *
	     * @param tag           a unique identifier for this parameter
	     * @param desc          a description of the parameter, suitable for display
	     *                      in a usage statement
	     * @param attributes    the attributes that must apply to a file or
	     *                      directory specified as a value to this FileParam
	     * @param optional      {@link Parameter#OPTIONAL OPTIONAL} if 
	     *                      optional, 
	     *                      {@link Parameter#REQUIRED REQUIRED} if required
	     * @throws              IllegalArgumentException if any parameter is 
	     *                      invalid.
	     * @see                 AbstractParameter#setTag(String) setTag()
	     * @see                 AbstractParameter#setDesc(String) setDesc()
	     * @see                 #setAttributes(int) setAttributes()
	     * @see                 Parameter#OPTIONAL OPTIONAL
	     * @see                 Parameter#REQUIRED REQUIRED
	     */
	    public PdfFileParam(String tag, 
	                    String desc, 
	                    int attributes,
	                    boolean optional) {
	        this(tag, desc, attributes, optional, SINGLE_VALUED, PUBLIC);
	    }

	    /**
	     * constructor - creates a public parameter that accepts a file 
	     * name with the specified attributes, and which is required 
	     * or optional and/or multi-valued, as specified.
	     *
	     * @param tag           a unique identifier for this parameter
	     * @param desc          a description of the parameter, suitable for display
	     *                      in a usage statement
	     * @param attributes    the attributes that must apply to a file or
	     *                      directory specified as a value to this FileParam
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
	     * @see                 #setAttributes(int) setAttributes()
	     * @see                 Parameter#OPTIONAL OPTIONAL
	     * @see                 Parameter#REQUIRED REQUIRED
	     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
	     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
	     */
	    public PdfFileParam(String tag, 
	                    String desc, 
	                    int attributes, 
	                    boolean optional,
	                    boolean multiValued) {
	        this(tag, desc, attributes, optional, multiValued, PUBLIC);
	    }

	    /**
	     * constructor - creates a parameter that accepts a file 
	     * name with the specified attributes, and which is required or optional 
	     * and/or multi-valued or hidden, as specified.
	     * <P>
	     * If the <code>IS_DIR</code> attribute is specified, the option label for
	     * this FileParam will be set to {@link #DEFAULT_DIR_OPTION_LABEL}, 
	     * else it will be {@link #DEFAULT_FILE_OPTION_LABEL}.
	     *
	     * @param tag           a unique identifier for this parameter
	     * @param desc          a description of the parameter, suitable for display
	     *                      in a usage statement
	     * @param attributes    the attributes that must apply to a file or
	     *                      directory specified as a value to this FileParam
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
	     * @see                 AbstractParameter#setOptionLabel(String) setOptionLabel()
	     * @see                 #setAttributes(int) setAttributes()
	     * @see                 Parameter#OPTIONAL OPTIONAL
	     * @see                 Parameter#REQUIRED REQUIRED
	     * @see                 Parameter#SINGLE_VALUED SINGLE_VALUED
	     * @see                 Parameter#MULTI_VALUED MULTI_VALUED
	     * @see                 Parameter#HIDDEN HIDDEN
	     * @see                 Parameter#PUBLIC PUBLIC
	     */
	    public PdfFileParam(String tag, 
	                    String desc, 
	                    int attributes, 
	                    boolean optional,
	                    boolean multiValued,
	                    boolean hidden) {
	        this.setTag(tag);
	        this.setAttributes(attributes);
	        this.setDesc(desc);
	        this.optional = optional;
	        this.multiValued = multiValued;
	        this.hidden = hidden;
	        this.setOptionLabel(DEFAULT_FILE_OPTION_LABEL);
	    }
	    

	    /**
	     * Gets the value of the PdfFileParam as a PdfFileParam object.  If the PdfFileParam is 
	     * multi-valued, the first value is returned.
	     *
	     * @return              the value as a PdfFile object
	     * @throws              RuntimeException if the value of the FileParam
	     *                      has not been set.
	     * @see Parameter#isSet()
	     */
	    public PdfFile getPdfFile() {
	        PdfFile retVal = null;
	    	if (!set) {
	            throw new RuntimeException(Strings.get(
	                    "PdfFileParam.valueNotSet", new Object[] { tag }));
	        }
	        retVal = getPdfFile((String)values.get(0));
	        return retVal;
	    }

	    /**
	     * Gets the values of the PdfFileParam as a Collection of PdfFile objects.  
	     * If the PdfFileParam has no values, an empty Collection is returned.
	     *
	     * @return              a Collection of PdfFile objects, possibly empty
	     * @see Parameter#isSet()
	     */
	    public Collection getPdfFiles() {
	        ArrayList vals = new ArrayList(values.size());
	        for (Iterator itr = values.iterator(); itr.hasNext(); ) {
	        	vals.add(getPdfFile((String)itr.next()));
	        }
	        return vals;
	    }

	    /**
	     * Validates a prospective value for the PdfFileParam - called by 
	     * add/setValue(s)().  All of the attributes are validated and the a 
	     * CmdLineException is thrown if any are not satisfied.
	     *
	     * @param val           the value to validate
	     * @throws              CmdLineException if <code>value</code> is not valid.
	     * @see #setAttributes(int) setAttributes()
	     */
	    public void validateValue(String val) throws CmdLineException {
	        super.validateValue(val);
	        PdfFile f = null;
	        try {
	        	f = getPdfFile(val);
	        } catch (Exception e) {
	            throwIllegalValueException(val);
	        }
	        if (attrSpecified(EXISTS) && !f.getFile().exists()) {
	            throwIllegalValueException(val);
	        }	       
	        if (attrSpecified(IS_READABLE) && !f.getFile().canRead()) {
	            throwIllegalValueException(val);
	        }
	        if (attrSpecified(IS_WRITEABLE) && !f.getFile().canWrite()) {
	            throwIllegalValueException(val);
	        }
	    }

	    /**
	     * Indicates whether an attribute has been specified for this FileParam.
	     *
	     * @param attr          one of {@link #NO_ATTRIBUTES}, {@link #EXISTS}, 
	     *                      {@link #IS_READABLE}, or 
	     *                      {@link #IS_WRITEABLE}
	     * @return              <code>true</code> if the attribute is set, 
	     *                      <code>false</code> if the attribute is not set or
	     *                      <code>attr</code> is not a valid attribute
	     */
	    public boolean attrSpecified(int attr) {
	        if (!(attr == EXISTS ||
	              attr == NO_ATTRIBUTES ||
	              attr == IS_READABLE ||
	              attr == IS_WRITEABLE)) {
	            return false;
	        }
	        return (((attributes | attr) ^ 0xffff) != 0);
	    }

	    /**
	     * Sets the value of attributes.  Multiple attributes may be specified
	     * by ANDing them together.  If multiple attributes are specified,
	     * all conditions must be met for a parameter value to be considered
	     * valid.  For example:
	     * <pre>
	     *    FileParam fp = new FileParam("tempDir", 
	     *            "a directory in which temporary files can be stored", 
	     *            FileParam.IS_DIR & FileParam.IS_WRITEABLE);
	     * </pre>
	     * In this case, a valid parameter value would have to be both a
	     * directory and writeable.
	     * <P>
	     * Specify <code>NO_ATTRIBUTES</code> if none of the other attributes is 
	     * required.
	     *
	     * @param attributes    a combination of {@link #NO_ATTRIBUTES},
	     *                      {@link #EXISTS}, 
	     *                      {@link #IS_READABLE}, and {@link #IS_WRITEABLE}
	     * @throws      IllegalArgumentException if the attributes value
	     *              is invalid.
	     * @see #getAttributes()
	     */
	    public void setAttributes(int attributes) {
	        if ((attributes ^ 0xffff) >= ((IS_WRITEABLE ^ 0xffff) * 2)) {
	            throw new IllegalArgumentException(Strings.get(
	                    "PdfFileParam.invalidAttributes",
	                    new Object[] { new Integer(attributes) }));
	        }
	        this.attributes = attributes;
	    }

	    /**
	     * gets the value of attributes
	     *
	     * @return              The attributes specified for this FileParam
	     * @see #setAttributes(int) setAttributes()
	     */
	    public int getAttributes() {
	        return attributes;
	    }

	    /**
	     * Throws a nicely formatted error message when an invalid value is
	     * attempted to be validated.
	     *
	     * @param val           the value that failed validation
	     * @return              doesn't - throws a CmdLineException
	     * @throws              CmdLineException - that's its goal!
	     */
	    private void throwIllegalValueException (String val) 
	            throws CmdLineException {
	    	String s1 = Strings.get("FileParam.file");
	        String s2;
	        if (attrSpecified(EXISTS) ||
	            attrSpecified(IS_READABLE) ||
	            attrSpecified(IS_WRITEABLE)) {

	            s2 = Strings.get("PdfFileParam.an_existing");
	        } else {
	            s2 = Strings.get("PdfFileParam.a");
	        }
	        String s3 = "";
	        if (attrSpecified(IS_READABLE)) {
	            if (attrSpecified(IS_WRITEABLE)) {
	                s3 = Strings.get("FileParam.readable_writeable");
	            } else {
	                s3 = Strings.get("PdfFileParam.readable");
	            }
	        } else if (attrSpecified(IS_WRITEABLE)) {
	            s3 = Strings.get("PdfFileParam.writeable");
	        }
	        throw new CmdLineException(Strings.get(
	            "PdfFileParam.illegalValue",new Object[] { s2, s1, s3, val, tag }));
	    }
	    
	    /**
	     * @param value
	     * @return the PdfFile
	     */
	    private PdfFile getPdfFile(String value){
	    	PdfFile retVal = null;
	    	int k = value.toLowerCase().lastIndexOf(".pdf:");
	    	if (k < 0){
	    		retVal =  new PdfFile(new File(value),"");
	    	}else{
	    		retVal =  new PdfFile(new File(value.substring(0, k+4)),value.substring(k+5, value.length()));
	    	}
	    	return retVal;
	    }

}
