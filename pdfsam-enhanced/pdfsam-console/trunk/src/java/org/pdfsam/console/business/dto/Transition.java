/*
 * Created on 08-Mar-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
 */package org.pdfsam.console.business.dto;

import java.io.Serializable;

/**
 * DTO that maps a transition
 * @author Andrea Vacondio
 */
public class Transition implements Serializable {

	private static final long serialVersionUID = 1064001681041759674L;

	/**
     *  Out Vertical Split
     */
    public static final String T_SPLITVOUT = "splitvout";
    /**
     *  Out Horizontal Split
     */
    public static final String T_SPLITHOUT = "splithout";
    /**
     *  In Vertical Split
     */
    public static final String T_SPLITVIN = "splitvin";
    /**
     *  In Horizontal Split
     */
    public static final String T_SPLITHIN = "splithin";
    /**
     *  Vertical Blinds
     */
    public static final String T_BLINDV = "blindv";
    /**
     *  Vertical Blinds
     */
    public static final String T_BLINDH = "blindh";
    /**
     *  Inward Box
     */
    public static final String T_INBOX = "inwardbox";
    /**
     *  Outward Box
     */
    public static final String T_OUTBOX = "outwardbox";
    /**
     *  Left-Right Wipe
     */
    public static final String T_LRWIPE = "wipel2r";
    /**
     *  Right-Left Wipe
     */
    public static final String T_RLWIPE = "wiper2l";
    /**
     *  Bottom-Top Wipe
     */
    public static final String T_BTWIPE = "wipeb2t";
    /**
     *  Top-Bottom Wipe
     */
    public static final String T_TBWIPE = "wipet2b";
    /**
     *  Dissolve
     */
    public static final String T_DISSOLVE = "dissolve";
    /**
     *  Left-Right Glitter
     */
    public static final String T_LRGLITTER = "glitterl2r";
    /**
     *  Top-Bottom Glitter
     */
    public static final String T_TBGLITTER = "glittert2b";
    /**
     *  Diagonal Glitter
     */
    public static final String T_DGLITTER = "glitterd";
    
    public static final int EVERY_PAGE = 0;
    
    private int pageNumber = EVERY_PAGE; 
    private int transitionDuration = 1;
    private String transition = "";
    private int duration = 3;
    
	/**
	 * @param pageNumber
	 * @param transitionDuration
	 * @param transition
	 * @param duration
	 */
	public Transition(int pageNumber, int transitionDuration, String transition, int duration) {
		this.pageNumber = pageNumber;
		this.transitionDuration = transitionDuration;
		this.transition = transition;
		this.duration = duration;
	}

	public Transition() {
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
	 * @return the transitionDuration
	 */
	public int getTransitionDuration() {
		return transitionDuration;
	}

	/**
	 * @param transitionDuration the transitionDuration to set
	 */
	public void setTransitionDuration(int transitionDuration) {
		this.transitionDuration = transitionDuration;
	}

	/**
	 * @return the transition
	 */
	public String getTransition() {
		return transition;
	}

	/**
	 * @param transition the transition to set
	 */
	public void setTransition(String transition) {
		this.transition = transition;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
    
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + duration;
		result = prime * result + pageNumber;
		result = prime * result
				+ ((transition == null) ? 0 : transition.hashCode());
		result = prime * result + transitionDuration;
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
		final Transition other = (Transition) obj;
		if (duration != other.duration)
			return false;
		if (pageNumber != other.pageNumber)
			return false;
		if (transition == null) {
			if (other.transition != null)
				return false;
		} else if (!transition.equals(other.transition))
			return false;
		if (transitionDuration != other.transitionDuration)
			return false;
		return true;
	}

	public String toString(){
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());		
		retVal.append("[transition="+transition+"]");
		retVal.append("[duration="+duration+"]");
		retVal.append("[transitionDuration="+transitionDuration+"]");
		retVal.append("[pageNumber="+pageNumber+"]");
		return retVal.toString();
	} 
	
}
