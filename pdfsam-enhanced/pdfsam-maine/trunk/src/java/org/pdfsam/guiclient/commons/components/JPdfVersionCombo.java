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
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComboBox;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.utils.PdfVersionUtility;
import org.pdfsam.i18n.GettextResource;
/**
 * Combo box for the output pdf version choice
 * @author Andrea Vacondio
 */
public class JPdfVersionCombo extends JComboBox {

	private static final long serialVersionUID = -5004011941231451770L;
	public static final String SAME_AS_SOURCE = "1000";
	private static final Logger log = Logger.getLogger(JPdfVersionCombo.class.getPackage().getName());
	
	private Configuration config;
	private boolean addSameAsSourceItem = false;
	private Vector filterVersions = new Vector();
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
		ArrayList values = PdfVersionUtility.getVersionsList();
		for(Iterator it = values.iterator(); it.hasNext();){
			addItem((StringItem) it.next());
		}		
		setSelectedIndex(getModel().getSize()-3);
		this.fullSize = this.getModel().getSize();
		this.setEnabled(true);
	}
	
	/**
	 * removes items with lower version then <code>version</code>
	 * @param version versionFilter
	 */
	public synchronized void addVersionFilter(Integer version){
		ArrayList removeList = new ArrayList();
		log.debug("Adding filter "+version);
		this.filterVersions.add(version);
		Integer minFilter = (Integer) Collections.min(filterVersions);
		for(int i =0; i<this.getItemCount(); i++){
			StringItem currentItem = (StringItem)getItemAt(i);
			if(currentItem!=null && minFilter.compareTo(new Integer(currentItem.getId()))>0){
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
	 * remove the filter
	 * @param version versionFilter
	 */
	public synchronized void removeVersionFilter(Integer version){
		log.debug("Removing filter "+version);
		if(this.filterVersions.remove(version)){
			if(filterVersions.isEmpty()){
				removeFilters();
			}else{
				Integer minFilter = (Integer) Collections.min(filterVersions);
				if(minFilter.compareTo(version)>0){
					this.removeAllItems();
					this.init(addSameAsSourceItem);
					this.addVersionFilter(minFilter);
				}
			}
		}
	}
	
	/**
	 * Enables every item
	 */
	public synchronized void removeFilters(){
		if(this.getModel().getSize()<fullSize){
			this.filterVersions.clear();
			this.removeAllItems();
			this.init(addSameAsSourceItem);			
		}
	}
	
}
