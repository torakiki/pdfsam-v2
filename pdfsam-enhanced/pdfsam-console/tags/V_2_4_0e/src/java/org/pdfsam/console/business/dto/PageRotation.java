/*
 * Created on 12-Jan-2009
 * Copyright (C) 2009 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Model for a page rotation
 * @author Andrea Vacondio
 *
 */
public class PageRotation implements Serializable {

	private static final long serialVersionUID = -5026985405000664237L;

	//rotations
	public static final int DEGREES_0 = 0;
	public static final int DEGREES_90 = 90;
	public static final int DEGREES_180 = 180;
	public static final int DEGREES_270 = 270;
	
	//no page number
	public static final int NO_PAGE = -1;
	
	//types
	public static final int SINGLE_PAGE = 0;
	public static final int ODD_PAGES = 1;
	public static final int EVEN_PAGES = 2;
	public static final int ALL_PAGES = 3;
	
	private int pageNumber;
	private int degrees = DEGREES_0;
	private int type = SINGLE_PAGE;

	public PageRotation() {
	}

	/**
	 * type is SINGLE_PAGE
	 * @param degrees
	 * @param pageNumber
	 */
	public PageRotation(int pageNumber, int degrees) {
		super();
		this.degrees = degrees;
		this.pageNumber = pageNumber;
	}

	/**
	 * @param degrees
	 * @param pageNumber
	 * @param type
	 */
	public PageRotation(int pageNumber, int degrees, int type) {
		super();
		this.degrees = degrees;
		this.pageNumber = pageNumber;
		this.type = type;
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	/**
	 * @return the degrees
	 */
	public int getDegrees() {
		return degrees;
	}
	/**
	 * @param degrees the degrees to set
	 */
	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	public String toString() {
		return new ToStringBuilder(this).append(pageNumber).append(degrees).append(type).toString();
	}
	
	
}
