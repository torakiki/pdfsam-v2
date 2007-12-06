/*
 * Created on 30-Nov-2007
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
package org.pdfsam.plugin.split.components;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Combo for the size selection
 * @author Andrea Vacondio
 *
 */
public class JSplitSizeCombo extends JComboBox {

	private static final long serialVersionUID = 1342525636090510279L;

	private Pattern pattern = Pattern.compile("^(\\d+[.[0-9]+]*)(\\s*)([KB||MB]+)", Pattern.CASE_INSENSITIVE );
	
	public static final String KB = "KB";
	public static final String MB = "MB";
	
	public JSplitSizeCombo(){
		init();
	}
	
	private void init(){
		setEditable(true);
		addItem("");
		addItem("500 "+KB);
		addItem("1 "+MB);
		addItem("3 "+MB);
		addItem("5 "+MB);
		addItem("10 "+MB);
	}
	
	/**
	 * @return Bytes of the selected item
	 * @throws Exception
	 */
	public long getSelectedBytes() throws Exception{
		long retVal = 0;
		if(isValidSelectedItem()){
			Matcher m = pattern.matcher((String)getSelectedItem());
			m.reset();
			m.matches();
			BigDecimal value = new BigDecimal(m.group(1));
			String unit = m.group(3);
			if (KB.equals(unit.toUpperCase())){
				value = value.multiply(new BigDecimal(1024));
			}else if (MB.equals(unit.toUpperCase())){
				value = value.multiply(new BigDecimal(1024*1024));
			}else{
				throw new Exception(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Invalid unit: ")+unit);
			}
			retVal = value.longValue();
		}
		return retVal;
	}
	
	/**
	 * @return true if the selected item is valid
	 */
	public boolean isValidSelectedItem(){
		pattern.matcher((String)getSelectedItem()).reset();
		return pattern.matcher((String)getSelectedItem()).matches();
	}
	
	/**
	 * @return if a not empty item is selected
	 */
	public boolean isSelectedItem(){
		return (getSelectedItem()!=null && ((String)getSelectedItem()).trim().length()>0);
	}
}
