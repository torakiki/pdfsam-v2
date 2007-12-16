/*
 * Strings.java
 *
 * jcmdline Rel. @VERSION@ $Id: Strings.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 *
 * Classes:
 *   public   Strings
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

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * A helper class used to obtain Strings for this package.  Strings are
 * retrieved from the ResourceBundle in "jcmdline.strings" which may be 
 * localized as necessary.
 *
 * @author          Lynne Lawrence
 * @version         jcmdline Rel. @VERSION@ $Id: Strings.java,v 1.2 2002/12/07 14:22:06 lglawrence Exp $
 * @see java.util.ResourceBundle
 */
public class Strings {

    /**
     * The resource bundle for this package.
     */
    private static final ResourceBundle rb  = 
        ResourceBundle.getBundle("jcmdline.strings");

    /**
     * The String that prefaces return values when a key is not defined
     * in the ResourceBundle.  Note that this is checked for in the unit
     * tests (see BetterTestCase) so a change in methodology here will
     * require a change to the unit tests.
     */
    private static final String missingKeyMsg = 
         rb.getString("Strings.missingKey") + " ";

    /**
     * Gets a String, filling in the supplied parameters.
     *
     * @param key           the key with which to look up the String in the
     *                      ResourceBundle
     * @param params        parameters to be plugged into the String
     * @return              The String, associated with <code>key</code>, with
     *                      the supplied <code>params</code> plugged in as
     *                      described for <code>java.text.MessageFormat</code>.
     *                      Should <code>key</code> not exist in the 
     *                      ResourceBundle a String containing the key and a list
     *                      of the parameters is returned.
     * @see java.text.MessageFormat
     * @see java.util.ResourceBundle
     */
    public static String get(String key, Object[] params) {
        String ret = missingKeyMsg + key;
        try {
            ret = rb.getString(key);
            ret = MessageFormat.format(ret, params);
        } catch (Exception e) {
            ret += "; params: ";
            for (int i = 0; i < params.length; i++) {
                ret += "[" + params[i] + "] ";
            }
        }
        return ret;
    }

    /**
     * Gets a String.
     *
     * @param key           the key with which to look up the String in the
     *                      ResourceBundle
     * @return              The String, associated with <code>key</code>.
     *                      Should <code>key</code> not exist in the 
     *                      ResourceBundle a String consisting of the key
     *                      is returned.
     * @see java.util.ResourceBundle
     */
    public static String get(String key) {
        String ret = missingKeyMsg + key;
        try {
            ret = rb.getString(key);
        } catch (Exception e) {
            // ignore - return the key
        }
        return ret;
    }
}
