/*
 * Created on 19-Aug-2009
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
/**
 * DTO to store page label settings
 * @author Andrea Vacondio
 *
 */
public class PageLabel implements Serializable {
	
	private static final long serialVersionUID = 4519061789420708363L;
	
	public static final String ARABIC = "arabic";
	public static final String UROMAN = "uroman";
	public static final String LROMAN = "lroman";
	public static final String ULETTER = "uletter";
	public static final String LLETTER = "lletter";
	public static final String EMPTY = "empty";
	
	private int pageNumber = 1;
	private int logicalPageNumber = 1;
	private String prefix;
	private String style = ARABIC;
	
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
	 * @return the logicalPageNumber
	 */
	public int getLogicalPageNumber() {
		return logicalPageNumber;
	}
	/**
	 * @param logicalPageNumber the logicalPageNumber to set
	 */
	public void setLogicalPageNumber(int logicalPageNumber) {
		this.logicalPageNumber = logicalPageNumber;
	}
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}
	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("PageLabel [logicalPageNumber=");
		buffer.append(logicalPageNumber);
		buffer.append(", pageNumber=");
		buffer.append(pageNumber);
		buffer.append(", prefix=");
		buffer.append(prefix);
		buffer.append(", style=");
		buffer.append(style);
		buffer.append("]");
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + logicalPageNumber;
		result = prime * result + pageNumber;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageLabel other = (PageLabel) obj;
		if (logicalPageNumber != other.logicalPageNumber)
			return false;
		if (pageNumber != other.pageNumber)
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		return true;
	}
	
}
