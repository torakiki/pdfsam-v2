/*
 * OptionTakesNoValue.java
 *
 * Classes:
 *   public   OptionTakesNoValue
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
 * An interface indicating an option Paramter that requires no value on the
 * command line (as for the BooleanParam).
 * <P>
 * When Parmeters implementing this interface are be used as command 
 * line options, they may be specified in two ways:
 * <pre>
 *    -optTag
 *  or
 *    -optTag=value
 * </pre>
 * If the first style is used, the option Parameter is passed the return
 * value of {@link #getDefaultValue()} as its value.
 * <p>
 * Specification of an option Paramter as:
 * <pre>
 *    -optTag value
 * </pre>
 * is not valid, and will cause parse errors.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: OptionTakesNoValue.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 */
public interface OptionTakesNoValue {
    public String getDefaultValue();
}
