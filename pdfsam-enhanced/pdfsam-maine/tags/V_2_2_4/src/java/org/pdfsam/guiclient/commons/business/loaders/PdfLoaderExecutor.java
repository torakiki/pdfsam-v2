/*
 * Created on 29-Nov-2009
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
package org.pdfsam.guiclient.commons.business.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.pdfsam.guiclient.commons.business.loaders.callable.AddPdfDocument;

/**
 * Executor that executes the load and reload threads
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfLoaderExecutor {

	private ExecutorService executor;

	private List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();

	/**
	 * Submit the callable for execution
	 * 
	 * @param callable
	 * @param hook hook to be executed after the document has been loaded. Can be null.
	 */
	public synchronized void execute(AddPdfDocument callable, PdfDocumentLoadedHook hook) {
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
		}
		//clean the futures if everything is done
		if(!isExecuting()){
			futures.clear();
		}
		futures.add(executor.submit(callable));
		
		if(hook != null){
			executor.submit(new HookedCallable(hook));
		}
	}

	/**
	 * 
	 * @return true if the executor is executing Callable
	 */
	public boolean isExecuting() {
		boolean retVal = false;
		for(Future<Boolean> future : futures){
			if(!future.isDone()){
				retVal = true;
				break;
			}
		}
		return retVal;
	}
	
	/**
	 * Callable hook
	 * @author Andrea Vacondio
	 *
	 */
	private class HookedCallable implements Callable<Boolean>{

		private PdfDocumentLoadedHook hook;
		
		/**
		 * @param hook
		 */
		public HookedCallable(PdfDocumentLoadedHook hook) {
			super();
			this.hook = hook;
		}

		@Override
		public Boolean call() throws Exception {
			hook.afterDocumentLoaded();
			return Boolean.TRUE;
		}
		
	}
}
