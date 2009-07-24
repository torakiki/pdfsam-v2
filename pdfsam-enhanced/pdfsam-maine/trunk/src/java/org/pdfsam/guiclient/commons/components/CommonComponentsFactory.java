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
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.pdfsam.guiclient.commons.components.listeners.DefaultMouseListener;
import org.pdfsam.guiclient.commons.components.listeners.PrefixMouseListener;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Factory for components commonly used by plugins
 * @author Andrea Vacondio
 *
 */
public class CommonComponentsFactory {

	public static final int SIMPLE_TEXT_FIELD_TYPE = 0;
	public static final int DESTINATION_TEXT_FIELD_TYPE = 1;
	public static final int PREFIX_TEXT_FIELD_TYPE = 2;
	public static final int PREFIX_TEXT_FIELD_TYPE_FULL_MENU = 3;
	
	public static final int RUN_BUTTON_TYPE = 1;
	public static final int BROWSE_BUTTON_TYPE = 2;
	public static final int ADD_BUTTON_TYPE = 3;

	public static final int OVERWRITE_CHECKBOX_TYPE = 1;
	public static final int COMPRESS_CHECKBOX_TYPE = 2;
	public static final int DONT_PRESERVER_ORDER_CHECKBOX_TYPE = 3;
	
	public static final int PDF_VERSION_LABEL = 1;
	
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
			retVal.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Run"));
			break;
		
		case BROWSE_BUTTON_TYPE:
			retVal.setMargin(new Insets(2, 2, 2, 2));
			retVal.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Browse"));
			break;
		
		case ADD_BUTTON_TYPE:
			retVal.setMargin(new Insets(2, 2, 2, 2));
			retVal.setIcon(new ImageIcon(this.getClass().getResource("/images/add.png")));
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Add"));
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
			retVal.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version required:")+" 1.5");			
			break;
		
		case OVERWRITE_CHECKBOX_TYPE:
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Overwrite if already exists"));
			retVal.setSelected(true);
			break;
		
		case DONT_PRESERVER_ORDER_CHECKBOX_TYPE:
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Don't preserve file order (fast load)"));
			break;

		default:
			break;
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param labelType
	 * @return a JLabel instance
	 */
	public synchronized JLabel createLabel(int labelType){
		JLabel retVal = new JLabel();
		
		switch(labelType){
		
		case PDF_VERSION_LABEL:
			retVal.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Output document pdf version:"));
			break;		

		default:
			break;
		}
		
		return retVal;
	}
	
	/**
	 * 
	 * @param textFieldType
	 * @return a JTextField instance
	 */
	public synchronized JTextField createTextField(int textFieldType){
		JTextField retVal = new JTextField();
		retVal.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		switch(textFieldType){
		
		case SIMPLE_TEXT_FIELD_TYPE:
		case DESTINATION_TEXT_FIELD_TYPE:
			retVal.addMouseListener(new DefaultMouseListener());
			break;		

		case PREFIX_TEXT_FIELD_TYPE:
			retVal.addMouseListener(new PrefixMouseListener(PrefixMouseListener.BASIC_MENU, retVal));
			retVal.setText("pdfsam_");
			break;		

		case PREFIX_TEXT_FIELD_TYPE_FULL_MENU:
			retVal.addMouseListener(new PrefixMouseListener(PrefixMouseListener.FULL_MENU, retVal));
			retVal.setText("pdfsam_");
			break;		

		default:
			break;
		}
		
		return retVal;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone ComponentFactory object.");
	}
}
