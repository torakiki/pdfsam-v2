/*
 * Created on 24-Dec-2007
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
package org.pdfsam.guiclient.commons.business;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Executes the commands
 * @author Andrea Vacondio
 */
public class WorkExecutor {

	private static final Logger log = Logger.getLogger(WorkExecutor.class.getPackage().getName());
	
	private static WorkExecutor executor;
    private Configuration config;	
    private WorkQueue workQueue;
	
	private WorkExecutor(){
		workQueue = new WorkQueue(1);
		workQueue.startWorkQueue();
		config = Configuration.getInstance();
	}
	
	public static synchronized WorkExecutor getInstance() { 
		if (executor == null){
			executor = new WorkExecutor();
		}
		return executor;
	}
	

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cannot clone WorkExecutor object.");
	}

    /**
     * Executes the input WorkThread
     * @param r
     */
    public void execute(WorkThread r){
    	workQueue.execute(r);
    }
    
    /**
     * @return number of running threads
     */
    public int getRunningThreads(){
    	return workQueue.getRunning();
    }    
    
    public class WorkQueue{
    	
        private final PoolWorker[] threads;
        private final LinkedList queue;
        private int running = 0;

        /**
         * Default pool size = 1
         */
        public WorkQueue(){
        	this(1);
        }
        
        /**
         * @param nThreads pool size 
         */
        public WorkQueue(int nThreads){
            queue = new LinkedList();
            threads = new PoolWorker[nThreads];

        }
        
        /**
         * starts the work queue
         */
        public void startWorkQueue(){
        	if (threads != null){
	            for (int i=0; i<threads.length; i++) {
	                threads[i] = new PoolWorker();
	                threads[i].start();
	            }
        	}
        }
        
        public synchronized void incRunningCounter(){
        	running++;
        }
        
        public synchronized void deincRunningCounter(){
        	running--;        	
        }
        
        /**
         * @return number of running threads
         */
        public int getRunning(){
        	return running;
        }
        
        public void execute(Runnable r) {
            synchronized(queue) {
                queue.addLast(r);
                queue.notify();
            }
        }

        private class PoolWorker extends Thread {
            public void run() {
                Runnable r;

                while (true) {
                    synchronized(queue) {
                        while (queue.isEmpty()) {
                            try {
                                queue.wait();
                            }
                            catch (InterruptedException ignored){}
                        }
                        r = (Runnable) queue.removeFirst();
                        
                    }
                    if(r != null){
                    	try {
                    		incRunningCounter();
                    		r.run();                         		
                    	}catch (RuntimeException e) {
                    		log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: "),e);
                    	}
                    	finally{
                    		deincRunningCounter();                	  
                    	}
               	 	} 
                }
            }
        }
    }
}
