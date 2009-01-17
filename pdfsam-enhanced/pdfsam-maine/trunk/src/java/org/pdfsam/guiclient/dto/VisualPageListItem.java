/*
 * Created on 18-Jun-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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
package org.pdfsam.guiclient.dto;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.utils.ConversionUtility;
import org.pdfsam.guiclient.utils.paper.PaperFormatUtility;
/**
 * DTO representing a page of a document 
 * @author Andrea Vacondio
 *
 */
public class VisualPageListItem implements Serializable, Cloneable {

	private static final long serialVersionUID = 7598120284619680606L;
	
	public static final Image HOURGLASS =  new ImageIcon(VisualPageListItem.class.getResource("/images/hourglass.png")).getImage();

	private Image thumbnail = HOURGLASS;
	private Image rotatedThumbnail = null;
	private int pageNumber;
	private boolean deleted = false;
	private String parentFileCanonicalPath = "";
	private String documentPassword = "";
	private Rotation rotation = Rotation.DEGREES_0;
	private String originalDocumentSize = "";

	public VisualPageListItem() {
	}

	/**
	 * @param pageNumber
	 */
	public VisualPageListItem(int pageNumber) {
		this(HOURGLASS, pageNumber);
	}
	/**
	 * @param pageNumber
	 * @param parentFileCanonicalPath
	 */
	public VisualPageListItem(int pageNumber, String parentFileCanonicalPath) {
		this(HOURGLASS, pageNumber, false, parentFileCanonicalPath, null);
	}
	/**
	 * @param pageNumber
	 * @param parentFileCanonicalPath
	 * @param documentPassword
	 */
	public VisualPageListItem(int pageNumber, String parentFileCanonicalPath, String documentPassword) {
		this(HOURGLASS, pageNumber, false, parentFileCanonicalPath, documentPassword);
	}

	/**
	 * @param thumbnail
	 * @param pageNumber
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber) {
		this(thumbnail, pageNumber, false);
	}
	/**
	 * @param thumbnail
	 * @param pageNumber
	 * @param deleted
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted){
		this(thumbnail, pageNumber, deleted, "", null);
	}
	/**
	 * @param thumbnail
	 * @param pageNumber
	 * @param deleted
	 * @param parentFileCanonicalPath
	 * @param documentPassword 
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted, String parentFileCanonicalPath, String documentPassword) {
		this(thumbnail, pageNumber, deleted, parentFileCanonicalPath, documentPassword, Rotation.DEGREES_0);
	}
	
	/**
	 * @param thumbnail
	 * @param pageNumber
	 * @param deleted
	 * @param parentFileCanonicalPath
	 * @param documentPassword
	 * @param rotation
	 */
	public VisualPageListItem(Image thumbnail, int pageNumber, boolean deleted, String parentFileCanonicalPath, String documentPassword, Rotation rotation) {
		super();
		this.thumbnail = thumbnail;
		this.pageNumber = pageNumber;
		this.deleted = deleted;
		this.parentFileCanonicalPath = parentFileCanonicalPath;
		this.documentPassword = documentPassword;
		this.rotation = rotation;
		this.rotatedThumbnail = null;
	}
	/**
	 * @return the thumbnail
	 */
	public Image getThumbnail() {
		return thumbnail;
	}
	/**
	 * @param thumbnail the thumbnails to set
	 */
	public void setThumbnail(Image thumbnail) {
		this.thumbnail = thumbnail;
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
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the parentFileCanonicalPath
	 */
	public String getParentFileCanonicalPath() {
		return parentFileCanonicalPath;
	}

	/**
	 * @param parentFileCanonicalPath the parentFileCanonicalPath to set
	 */
	public void setParentFileCanonicalPath(String parentFileCanonicalPath) {
		this.parentFileCanonicalPath = parentFileCanonicalPath;
	}	

	/**
	 * @return the documentPassword
	 */
	public String getDocumentPassword() {
		return documentPassword;
	}

	/**
	 * @param documentPassword the documentPassword to set
	 */
	public void setDocumentPassword(String documentPassword) {
		this.documentPassword = documentPassword;
	}
	
	/**
	 * @return the rotation
	 */
	public Rotation getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	/**
	 * rotate clockwise the item
	 */
	public void rotateClockwise(){
		this.rotation = rotation.rotateClockwise();		
	}

	/**
	 * rotate anticlockwise the item
	 */
	public void rotateAnticlockwise(){
		this.rotation = rotation.rotateAnticlockwise();
	}

	/**
	 * @return true if this item is rotated
	 */
	public boolean isRotated(){
		return (!rotation.equals(Rotation.DEGREES_0));
	}
	
	/**
	 * @return if the item has a 180 degrees rotation
	 */
	public boolean isFullyRotated(){
		return rotation.equals(Rotation.DEGREES_180);
	}
	
	/**
	 * @return the rotatedThumbnail
	 */
	public Image getRotatedThumbnail() {
		return rotatedThumbnail;
	}

	/**
	 * @param rotatedThumbnail the rotatedThumbnail to set
	 */
	public void setRotatedThumbnail(Image rotatedThumbnail) {
		this.rotatedThumbnail = rotatedThumbnail;
	}

	/**
	 * @return the current thumbnail based on the rotation
	 */
	public Image getCurrentThumbnail(){
		Image retVal = null;
		if(isRotated() && rotatedThumbnail!=null){
			retVal = rotatedThumbnail;
		}else{
			retVal = thumbnail;
		}
		return retVal;
	}		
	

	/**
	 * @return the originalDocumentSize
	 */
	public String getOriginalDocumentSize() {
		return originalDocumentSize;
	}

	/**
	 * @param originalDocumentSize the imageDimensions to set
	 */
	public void setOriginalDocumentSize(String originalDocumentSize) {
		this.originalDocumentSize = originalDocumentSize;
	}

	/**
	 * Set the original document size in a string format
	 * @param width
	 * @param height
	 * @param resolution the resolution
	 */
	public void setOriginalDocumentSize(double width, double height, int resolution) {
		double width2 = Math.round(ConversionUtility.toCentimeters(width/(double)resolution) * 10);
		double height2 = Math.round(ConversionUtility.toCentimeters(height/(double)resolution) * 10);
		this.originalDocumentSize = PaperFormatUtility.getFormat(width2,height2);
	}

	/**
	 * set the original dimension taking the screen resolution
	 * @param width
	 * @param height
	 */
	public void setOriginalDocumentSize(double width, double height) {
		setOriginalDocumentSize(width, height, Configuration.getInstance().getScreenResolution());
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
	    
	    retValue.append("VisualPageListItem ( ")
	        .append(super.toString())
	        .append(OPEN).append("thumbnail=").append(this.thumbnail).append(CLOSE)
	        .append(OPEN).append("rotatedThumbnail=").append(this.rotatedThumbnail).append(CLOSE)
	        .append(OPEN).append("pageNumber=").append(this.pageNumber).append(CLOSE)
	        .append(OPEN).append("deleted=").append(this.deleted).append(CLOSE)
	        .append(OPEN).append("parentFileCanonicalPath=").append(this.parentFileCanonicalPath).append(CLOSE)
	        .append(OPEN).append("documentPassword=").append(this.documentPassword).append(CLOSE)
	        .append(OPEN).append("rotation=").append(this.rotation).append(CLOSE)
	        .append(OPEN).append("originalDocumentSize=").append(this.originalDocumentSize).append(CLOSE)
	        .append(" )");
	    
	    return retValue.toString();
	}
	
	public Object clone(){
		VisualPageListItem retVal = new VisualPageListItem(thumbnail, pageNumber, deleted, parentFileCanonicalPath, documentPassword, rotation);
		retVal.setOriginalDocumentSize(originalDocumentSize);
		retVal.setRotatedThumbnail(rotatedThumbnail);
		return retVal;
	}


}
