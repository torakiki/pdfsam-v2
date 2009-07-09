package org.pdfsam.console.business.dto;

import java.io.Serializable;

/**
 * Maps the limit of the concat command, start and end
 * @author Andrea Vacondio
 *
 */
public class Bounds implements Serializable {

	private static final long serialVersionUID = 1093984828590806028L;

	private int start;
	private int end;

	public Bounds() {
	}
	/**
	 * @param end
	 * @param start
	 */
	public Bounds(int end, int start) {
		this.end = end;
		this.start = start;
	}
	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}
	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String toString(){
		return start+"-"+end;
	}
}
