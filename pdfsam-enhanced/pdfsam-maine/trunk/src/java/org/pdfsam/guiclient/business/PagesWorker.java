/*
 * Created on 02-JUL-08
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
package org.pdfsam.guiclient.business;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Worker for the pages actions
 * @author Andrea Vacondio
 *
 */
public class PagesWorker {
	
	private static final Logger log = Logger.getLogger(PagesWorker.class.getPackage().getName());
    
    public static final String MOVE_UP = "moveUp";
    public static final String MOVE_DOWN = "moveDown";
    public static final String REMOVE = "remove";
    public static final String UNDELETE = "undelete";
    public static final String ROTATE_CLOCK = "rotateclock";
    public static final String ROTATE_ANTICLOCK = "rotateanticlock";
    public static final String REVERSE = "reverse";
    
    private JVisualSelectionList list;
    
    /**
	 * @param list
	 */
	public PagesWorker(JVisualSelectionList list) {
		super();
		this.list = list;
	}


	/**
	 * execute the login in response of an action
	 * @param action
	 */
	public void execute(String action) {
		if(action!=null){
			int[] selectedIndexes = list.getSelectedIndices();
			if(selectedIndexes != null && selectedIndexes.length>0){
		    	if(MOVE_UP.equals(action)){
		    		((VisualListModel)list.getModel()).moveUpIndexes(selectedIndexes);
		    		if (selectedIndexes[0] > 0){
						list.setSelectionInterval(selectedIndexes[0]-1, selectedIndexes[selectedIndexes.length-1]-1);
		            }
		    	}else if(MOVE_DOWN.equals(action)){
		    		((VisualListModel)list.getModel()).moveDownIndexes(selectedIndexes);
		    		if (selectedIndexes[selectedIndexes.length-1] < (((VisualListModel) list.getModel()).getSize()-1)){
		    			list.setSelectionInterval(selectedIndexes[0]+1, selectedIndexes[selectedIndexes.length-1]+1);   
		           }
		    	}else if(REMOVE.equals(action)){
		    		((VisualListModel)list.getModel()).removeElements(selectedIndexes, !list.isDrawDeletedItems());
		    	}else if(UNDELETE.equals(action)){
		    		((VisualListModel)list.getModel()).undeleteElements(selectedIndexes);
		    	}else if (ROTATE_CLOCK.equals(action)){
                		((VisualListModel)list.getModel()).rotateClockwiseElements(selectedIndexes);
		    	}else if (ROTATE_ANTICLOCK.equals(action)){
                		((VisualListModel)list.getModel()).rotateAnticlockwiseElements(selectedIndexes);
		    	}else if (REVERSE.equals(action)){
            		((VisualListModel)list.getModel()).reverseElements(selectedIndexes);
		    	}
		    	else {
		    	
		    		log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unknown Action"));
		    	}
			}
		}
    }
}
