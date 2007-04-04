/*
 * Created on 06-Feb-2006
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package it.pdfsam.interfaces;

import java.awt.FocusTraversalPolicy;

import javax.swing.Icon;

/**
 * Provides an interface to plugable panels
 * @author Andrea Vacondio
 * @see it.pdfsam.utils.PlugInsLoader
 */

public interface PlugablePanel{

    /**
     * sets the language and init the panel
     * @param language_code language to set
     * @deprecated
     */
    public void init(String language_code);
    /**
     * @return Plugin Author Name
     */
    public String getPluginAuthor();
    /**
     * @return Plugin Name
     */
    public String getPluginName();
    /**
     * @return Color for the log message
     */
    public String getLogColor();
    /**
     * @return Log Message
     */
    public String getLogMsg();
    /**
     * @return Plugin version
     */
    public String getVersion();
    
    /**
     * @return Plugin FocusTraversalPolicy
     */
    public FocusTraversalPolicy getFocusPolicy();
    /**
     * @return the icon for the tabbed panel
     */
    public Icon getIcon();
    

}
