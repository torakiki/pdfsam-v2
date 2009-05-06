/*
 * Created on 16-Jan-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.utils.paper;

import java.io.Serializable;
/**
 * Models a paper format
 * @author Andrea Vacondio
 *
 */
public class PaperFormat implements Serializable {

	private static final long serialVersionUID = 6615660207188886347L;
	
	private double width = 0;
	private double height = 0;
	private String description = "";
	private double horizontalTolerance = 0.0;	
	private double verticalTolerance = 0.0;	
	
	/**
	 * 
	 */
	public PaperFormat() {
		super();
	}
	/**
	 * 
	 * @param width
	 * @param height
	 * @param description
	 * @param horizontalTolerance
	 * @param verticalTolerance
	 */
	public PaperFormat(double width, double height, String description, double horizontalTolerance, double verticalTolerance) {
		super();
		this.description = description;
		this.height = height;
		this.horizontalTolerance = horizontalTolerance;
		this.verticalTolerance = verticalTolerance;
		this.width = width;
	}
	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}
	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * @return the horizontalTolerance
	 */
	public double getHorizontalTolerance() {
		return horizontalTolerance;
	}
	/**
	 * @param horizontalTolerance the horizontalTolerance to set
	 */
	public void setHorizontalTolerance(double horizontalTolerance) {
		this.horizontalTolerance = horizontalTolerance;
	}
	/**
	 * @return the verticalTolerance
	 */
	public double getVerticalTolerance() {
		return verticalTolerance;
	}
	/**
	 * @param verticalTolerance the verticalTolerance to set
	 */
	public void setVerticalTolerance(double verticalTolerance) {
		this.verticalTolerance = verticalTolerance;
	}
	/**
	 * @param obj
	 * @return true if the input object is tolerated by this format
	 */
	public boolean isTolerable(PaperFormat obj){
		boolean retVal = false;
		if(obj!=null){
			if((obj.getWidth()<=(width+horizontalTolerance))&&(obj.getWidth()>=(width-horizontalTolerance))){
				retVal = ((obj.getHeight()<=(height+horizontalTolerance))&&(obj.getHeight()>=(height-horizontalTolerance)));
			}
		}
		return retVal;
	}
	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String OPEN = "[";
	    final String CLOSE = "]";
	    
	    StringBuffer retValue = new StringBuffer();
	    
	    retValue.append("PaperFormat ( ")
	        .append(super.toString())
	        .append(OPEN).append("width=").append(this.width).append(CLOSE)
	        .append(OPEN).append("height=").append(this.height).append(CLOSE)
	        .append(OPEN).append("description=").append(this.description).append(CLOSE)
	        .append(OPEN).append("horizontalTolerance=").append(this.horizontalTolerance).append(CLOSE)
	        .append(OPEN).append("verticalTolerance=").append(this.verticalTolerance).append(CLOSE)
	        .append(" )");
	    
	    return retValue.toString();
	}
	
	
}
