/*
 * Created on 10-Jul-2006
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
package it.pdfsam.console.tools;

import java.io.Serializable;
import java.util.Comparator;
/**
 * 
 * Class used to compare page numbers when "split after these pages" option is selected
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfSplit#doSplit()
 * 
 */
public class StrNumComparator implements Comparator, Serializable {

	private static final long serialVersionUID = 3414191133281918424L;

	/**
     * Constructor
     */
    public StrNumComparator(){
    }

    /**
     * Implements Comparator
     */
    public int compare(Object obj1, Object obj2){
        try{
            return new Integer((String)obj1).compareTo(new Integer((String)obj2));
        }catch(Exception e){
            return 0;
        }
    }
}
