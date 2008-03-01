/*
 * Created on 03-Nov-2007
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
package org.pdfsam.guiclient.utils.filters;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Filters superclass
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractFileFilter extends FileFilter implements java.io.FileFilter{
	
	private boolean acceptDirectory = true;
	
	/**
	 * If true the filter accepts directories
	 * @param acceptDirectory
	 */
	public AbstractFileFilter(boolean acceptDirectory){
		this.acceptDirectory = acceptDirectory;
	}
	
	public AbstractFileFilter(){
		this.acceptDirectory = true;
	}
	
	
	public boolean accept(File f) {
		boolean retVal = false;
		if (f!=null){
	       if (f.isDirectory()){
	       	retVal = acceptDirectory;
	       }else{
		       String extension = getExtension(f);
		       retVal = getAcceptedExtension().equals(extension);		       
	       }
		}
		return retVal;
	}

	/**
	 * 
	 * @return the accepted extension
	 */
	public abstract String getAcceptedExtension(); 
    
	/**
     * Get the extension of a file.
     */  
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
