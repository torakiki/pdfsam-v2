/*
 * Created on 31-Mar-2009
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
package org.pdfsam.guiclient.business.thumbnails.executors;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.IdManager;
import org.pdfsam.guiclient.business.thumbnails.callables.JPodThmbnailCallable;
import org.pdfsam.guiclient.commons.panels.JVisualPdfPageSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.i18n.GettextResource;

import de.intarsys.pdf.pd.PDPage;

/**
 * Singleton that executes the thumbnails creations
 * @author Andrea Vacondio
 *
 */
public class JPodThumbnailsExecutor {

	private static final Logger log = Logger.getLogger(JPodThumbnailsExecutor.class.getPackage().getName());
	private static JPodThumbnailsExecutor instance = null;
	
	private  ExecutorService executor = null;
	
	
	private JPodThumbnailsExecutor(){	
		executor = Executors.newFixedThreadPool(Configuration.getInstance().getThumbCreatorPoolSize());
	}

	public static synchronized JPodThumbnailsExecutor getInstance() { 
		if (instance == null){
			instance = new JPodThumbnailsExecutor();
		}
		return instance;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone JPodThumbnailsExecutor object.");
	}
	
	/**
	 * Submit the thumbnail creation
	 * @param pdPage
	 * @param pageItem
	 * @param panel
	 * @param id
	 */
	public synchronized void submit(PDPage pdPage, VisualPageListItem pageItem , final JVisualPdfPageSelectionPanel panel, final long id){
		getExecutor().submit(new JPodThmbnailCallable(pdPage, pageItem, panel, id));	
	}
	
	/**
	 * Executes r
	 * @param r
	 */
	public synchronized void execute(Runnable r){
		getExecutor().execute(r);
	}
	
	/**
	 * @return the executor for the given ID
	 */
	private ExecutorService getExecutor(){
		if(executor == null || executor.isShutdown()){
			executor = Executors.newFixedThreadPool(Configuration.getInstance().getThumbCreatorPoolSize());
		}
		return executor;
	}	
	
	/**
	 * run all the tasks and than the closeTask
	 * @param tasks
	 * @param closeTask
	 * @param id
	 */
    public void invokeAll(Collection<? extends Callable<Boolean>> tasks, Callable<Boolean> closeTask, long id){
    	Thread t = new Thread(new Invoker(tasks, closeTask, id));
    	t.start();
	}
	
    /**
     * Used to invoke the thumbnails generation
     * @author Andrea Vacondio
     *
     */
	private class Invoker implements Runnable{

		private Collection<? extends Callable<Boolean>> tasks; 
		private Callable<Boolean> closeTask;
		private long id;
		
		/**
		 * @param tasks
		 * @param closeTask
		 * @param id
		 */
		public Invoker(Collection<? extends Callable<Boolean>> tasks, Callable<Boolean> closeTask, long id) {
			super();
			this.tasks = tasks;
			this.closeTask = closeTask;
			this.id = id;
		}

		@Override
		public void run() {
			try{
				if(tasks != null && tasks.size()>0){
					long startTime = System.currentTimeMillis();
					getExecutor().invokeAll(tasks);
					//close 
					if(closeTask != null){
						getExecutor().submit(closeTask);
					}
					if(!IdManager.getInstance().isCancelledExecution(id)){
						log.debug(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Thumbnails generated in "+(System.currentTimeMillis() - startTime)+"ms"));
					}
					IdManager.getInstance().removeCancelledExecution(id);
				}
			}catch(InterruptedException ie){
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to generate thumbnail"),ie);
			}
			
		}
		
	}
}
