/*
 * Created on 25-Nov-2007
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
package org.pdfsam.guiclient.commons.components;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComboBox;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.i18n.GettextResource;
/**
 * Combo box for the output pdf version choice
 * @author Andrea Vacondio
 */
public class JPdfVersionCombo extends JComboBox {

	private static final long serialVersionUID = -5004011941231451770L;
	public static final String SAME_AS_SOURCE = "1000";
	
	private Configuration config;
	private boolean addSameAsSourceItem = false;
	/**
	 * Size of the model when full
	 */
	private int fullSize = 0;
	
	public JPdfVersionCombo(){
		this(false);
	}
	
	public JPdfVersionCombo(boolean addSameAsSourceItem){
		config = Configuration.getInstance();
		init(addSameAsSourceItem);
	}
	
	/**
	 * Component initialization
	 */
	private void init(boolean addSameAsSourceItem){
		this.addSameAsSourceItem = addSameAsSourceItem;
		if(addSameAsSourceItem){
			addItem(new StringItem(SAME_AS_SOURCE, GettextResource.gettext(config.getI18nResourceBundle(),"Same as input document")));			
		}
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_2), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.2 (Acrobat 3)")));
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_3), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.3 (Acrobat 4)")));
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_4), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.4 (Acrobat 5)")));
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_5), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.5 (Acrobat 6)")));
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_6), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.6 (Acrobat 7)")));
		addItem(new StringItem(Character.toString(AbstractParsedCommand.VERSION_1_7), GettextResource.gettext(config.getI18nResourceBundle(),"Version 1.7 (Acrobat 8)")));
		setSelectedIndex(getModel().getSize()-3);
		this.fullSize = this.getModel().getSize();
		this.setEnabled(true);
	}
	
	/**
	 * removes items with lower version then <code>version</code>
	 * @param version
	 */
	public void removeLowerVersionItems(int version){
		ArrayList removeList = new ArrayList();
		for(int i =0; i<this.getItemCount(); i++){
			StringItem currentItem = (StringItem)getItemAt(i);
			if(currentItem!=null && version > Integer.parseInt(currentItem.getId())){
				removeList.add(getItemAt(i));
			}
		}
		if(removeList.size()>0){
			for(Iterator iter = removeList.iterator(); iter.hasNext();){
				StringItem currentItem = (StringItem)iter.next();
				this.removeItem(currentItem);
			}	
		}
		//if it's empty i disable
		if(this.getItemCount() == 0){
			this.setEnabled(false);
		}
	}
	
	/**
	 * Enables every item
	 */
	public void enableAll(){
		if(this.getModel().getSize()<fullSize){
			this.removeAllItems();
			this.init(addSameAsSourceItem);			
		}
	}
	
}
