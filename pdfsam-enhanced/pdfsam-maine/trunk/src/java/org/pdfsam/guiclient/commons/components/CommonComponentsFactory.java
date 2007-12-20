/*
 * Created on 28-Nov-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.components;

import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Factory for components commonly used by plugins
 * @author Andrea Vacondio
 *
 */
public class CommonComponentsFactory {

	public static final int RUN_BUTTON_TYPE = 1;
	public static final int BROWSE_BUTTON_TYPE = 2;

	public static final int OVERWRITE_CHECKBOX_TYPE = 1;
	public static final int COMPRESS_CHECKBOX_TYPE = 2;
	
	private static CommonComponentsFactory instance = null;
	private Configuration config;
	
	private CommonComponentsFactory(){
		config = Configuration.getInstance();
	}
	
	/**
	 * @return the instance of CommonComponentsFactory
	 */
	public static synchronized CommonComponentsFactory getInstance() { 
		if (instance == null){
			instance = new CommonComponentsFactory();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param buttonType
	 * @return a button instance
	 */
	public synchronized JButton createButton(int buttonType){
		JButton retVal = new JButton();
		
		switch(buttonType){
		
		case RUN_BUTTON_TYPE:
			retVal.setMargin(new Insets(2, 2, 2, 2));
			retVal.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Execute pdf alternate mix"));
			retVal.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Run"));
			break;
		
		case BROWSE_BUTTON_TYPE:
			retVal.setMargin(new Insets(2, 2, 2, 2));
			retVal.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Browse"));
			break;
		
		default:
			break;
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param checkboxType
	 * @return a JCheckBox instance
	 */
	public synchronized JCheckBox createCheckBox(int checkboxType){
		JCheckBox retVal = new JCheckBox();
		
		switch(checkboxType){
		
		case COMPRESS_CHECKBOX_TYPE:
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Compress output file/files"));
			break;
		
		case OVERWRITE_CHECKBOX_TYPE:
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Overwrite if already exists"));
			retVal.setSelected(true);
			break;
		
		default:
			break;
		}
		
		return retVal;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone configuration object.");
	}
}
