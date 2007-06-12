/*
 * Created on 20-Feb-2006
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
package it.pdfsam.utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;
/**
 * Filter for the JFileChooser. Used by split plugin to chose output directory.
 * @author Andrea Vacondio
 * @see javax.swing.JFileChooser
 *
 */
public class DirFilter extends FileFilter {

    public boolean accept(File f) {
        if (f.isDirectory()){
            return true;
        }
        else{
            return false;
        }

    }

    public String getDescription() {
        return "Direcotries";
    }

}
