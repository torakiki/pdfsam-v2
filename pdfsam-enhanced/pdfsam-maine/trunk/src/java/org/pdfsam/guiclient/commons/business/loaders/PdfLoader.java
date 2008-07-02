/*
 * Created on 21-Nov-2007
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
package org.pdfsam.guiclient.commons.business.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.utils.filters.PdfFilter;
import org.pdfsam.i18n.GettextResource;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Business class whose job is to load pdf file to PdfSelectionTableItem
 * @author Andrea Vacondio
 *
 */
public class PdfLoader {

	private static final Logger log = Logger.getLogger(PdfLoader.class.getPackage().getName());
	
    //threshold to low priority addThread 
    private static long LOW_PRIORITY_SIZE = 5*1024*1024; //5MB
    
	private JPdfSelectionPanel panel;
    private JFileChooser fileChooser = null;
    private WorkQueue workQueue = null;
	
	public PdfLoader(JPdfSelectionPanel panel){
		this.panel = panel;
		
		//number of threads in workqueue based on the number of selectable documents
		if(panel.getMaxSelectableFiles() <= 1){
			workQueue = new WorkQueue(1, 1);
		}else{
			workQueue = new WorkQueue(10, 1);			
//			fileChooser.setAccessory(CommonComponentsFactory.getInstance().createCheckBox(CommonComponentsFactory.DONT_PRESERVER_ORDER_CHECKBOX_TYPE));
		}
        
	}    
    
    /**
     * adds a file or many files depending on the value of singleSelection
     */
    public void showFileChooserAndAddFiles(boolean singleSelection){
    	if(panel.getMainTable().getModel().getRowCount() >= panel.getMaxSelectableFiles()){
    		JOptionPane.showMessageDialog(panel,
    				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selection table is full, please remove some pdf document."),
    				GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Table full"),
    				JOptionPane.INFORMATION_MESSAGE);
    	}else{
    		lazyInitFileChooser();
			if(singleSelection){
				fileChooser.setMultiSelectionEnabled(false);
			}else{
				fileChooser.setMultiSelectionEnabled(true);
			}
			if(!(workQueue.getRunning()>0)){
		        if (fileChooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION){
		            if(fileChooser.isMultiSelectionEnabled()){
		            	addFiles(fileChooser.getSelectedFiles(),true);
		            }else{
		            	addFile(fileChooser.getSelectedFile());
		            }
		        }
			}else{
				log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please wait while all files are processed.."));
			}
    	}
    }
    
    /**
     * adds multiple selected files
     */
    public void showFileChooserAndAddFiles(){
    	showFileChooserAndAddFiles(false);
    }
    
    /**
     * Lazy JFileChooser initialization
     */
    private void lazyInitFileChooser(){
    	if(fileChooser == null){
    		fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
            fileChooser.setFileFilter(new PdfFilter());
            fileChooser.setMultiSelectionEnabled(true);
    	}
    }
    /**
     * add a file to the selectionTable
     * @param file input file
     * @param password password
     * @param pageSelection page selection
     */
    public synchronized void addFile(File file, String password, String pageSelection){
    	if (file != null){
    		workQueue.execute(new AddThread(file, password, pageSelection), WorkQueue.SINGLE);
    	}
    }
    
    /**
     * add a file to the selectionTable
     * @param file input file
     * @param password password
     */
    public void addFile(File file, String password){
    	this.addFile(file, password, null);
    }

    /**
     * add a file to the selectionTable
     * @param file input file
     */
    public void addFile(File file){
    	this.addFile(file, null, null);
    }
    /**
     * reload a file to the selectionTable
     * @param file input file
     * @param password password
     * @param pageSelection page selection
     */
    public synchronized void reloadFile(File file, String password, String pageSelection, int index){
    	if (file != null){
    		workQueue.execute(new ReloadThread(file, password, pageSelection, index), (file.length()<(LOW_PRIORITY_SIZE))? WorkQueue.HIGH: WorkQueue.LOW);
    	}
    }
    
    /**
     * reload a file to the selectionTable
     * @param file input file
     * @param password password
     */
    public void reloadFile(File file, String password, int index){
    	this.reloadFile(file, password, null, index);
    }

    /**
     * reload a file to the selectionTable
     * @param file input file
     */
    public void reloadFile(File file, int index){
    	this.reloadFile(file, null, null, index);
    }
    
    /**
     * adds files to the selectionTable
     * @param files
     * @param ordered files are added keeping order
     */
    
    public synchronized void addFiles(File[] files, boolean ordered){
    	 if (files != null){
         	for (int i=0; i < files.length; i++){
         		Integer priority = (ordered)?WorkQueue.SINGLE: (files[i].length()<(LOW_PRIORITY_SIZE))? WorkQueue.HIGH: WorkQueue.LOW;
         		workQueue.execute(new AddThread(files[i]), priority);
         	}
         }     
    }
    
    /**
     * Add files without keeping order
     * @param files
     */
    public void addFiles(File[] files){
    	addFiles(files, false);
    }
    	
    /**
     * adds files to the selectionTable
     * @param files File objects list
     * @param ordered files are added keeping order 
     */
    public void addFiles(List files, boolean ordered){
    	if (files != null && !files.isEmpty()){
    		addFiles((File[])files.toArray(new File[files.size()]), ordered);
   	 	}     
    }
    
    /**
     * Add files without keeping order
     * @param files
     */   
    public void addFiles(List files){
    	addFiles(files,false);
    }
    
    /**
     * @return number of running threads
     */
    public int getRunningThreadsNumber(){
    	return workQueue.getRunning();
    }
    
    /**
     * Runnable to load pdf documents
     * @author Andrea Vacondio
     *
     */
    private class AddThread implements Runnable{
    	String wipText;
    	File inputFile;
    	String password;
    	String pageSelection;
    	
    	public AddThread(File inputFile){
    		this(inputFile, null, null);
    	}
    	
    	public AddThread(File inputFile, String password){
    		this(inputFile, password, null);
    	}
    	
    	public AddThread(File inputFile, String password, String pageSelection){
    		this.inputFile = inputFile;
    		this.pageSelection = pageSelection;
    		this.password = password;
    	}
    	
    	public void run() {					
    		try{
    			 if (inputFile != null){
						if(new PdfFilter(false).accept(inputFile)){
			    			wipText = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please wait while reading")+" "+inputFile.getName()+" ...";
			                panel.addWipText(wipText);			                
			                panel.addTableRow(getPdfSelectionTableItem(inputFile, password, pageSelection));
			                panel.removeWipText(wipText);
						}else{
							log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selected file is not a pdf document.")+" "+inputFile.getName());
						}
                }
	   		 }catch(Throwable e){
	   			 log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "),e); 
	   		 }	
		 }
    		
  	  /**
         * 
         * @param fileToAdd file to add
         * @param password password to open the file
         * @return the item to add to the table
         */
        PdfSelectionTableItem getPdfSelectionTableItem(File fileToAdd, String password, String pageSelection){
        	PdfSelectionTableItem tableItem = null;
        	PdfReader pdfReader = null;
            if (fileToAdd != null){
            	tableItem = new PdfSelectionTableItem();
            	tableItem.setInputFile(fileToAdd);
            	tableItem.setPassword(password);
            	tableItem.setPageSelection(pageSelection);
                try{
                    //fix 03/07 for memory usage
                    pdfReader = new PdfReader(new RandomAccessFileOrArray(new FileInputStream(fileToAdd)), (password != null)?password.getBytes():null);
                    tableItem.setEncrypted(pdfReader.isEncrypted());
                    tableItem.setPagesNumber(Integer.toString(pdfReader.getNumberOfPages()));
                    tableItem.setPdfVersion(pdfReader.getPdfVersion());
                }
                catch (Exception e){
                	tableItem.setLoadedWithErrors(true);
                	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error loading ")+fileToAdd.getAbsolutePath()+" :", e);
                }
                finally{
                	if(pdfReader != null){
                		pdfReader.close();
    					pdfReader = null;
                	}
                }               
            }
            return tableItem;    
        }              
    }
    
    /**
     * Runnable to reload pdf documents
     * @author Andrea Vacondio
     *
     */
    private class ReloadThread extends AddThread{
    	
    	private int index = 0;

		/**
		 * @param inputFile
		 * @param password
		 * @param pageSelection
		 */
		public ReloadThread(File inputFile, String password, String pageSelection, int index) {
			super(inputFile, password, pageSelection);
			this.index = index;
		}

		/**
		 * @param inputFile
		 * @param password
		 */
		public ReloadThread(File inputFile, String password, int index) {
			this(inputFile, password, null, index);			
		}

		/**
		 * @param inputFile
		 */
		public ReloadThread(File inputFile, int index) {
			this(inputFile, null, null, index);
		}

		public void run() {					
    		try{
    			 if (inputFile != null){
						if(new PdfFilter(false).accept(inputFile)){
			    			wipText = GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Please wait while reading")+" "+inputFile.getName()+" ...";
			                panel.addWipText(wipText);			                
			                panel.updateTableRow(index, getPdfSelectionTableItem(inputFile, password, pageSelection));
			                panel.removeWipText(wipText);
						}else{
							log.warn(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Selected file is not a pdf document.")+" "+inputFile.getName());
						}
                }
	   		 }catch(Throwable e){
	   			 log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "),e); 
	   		 }	
		 }    	
    }
    
    /**
     * Work queue used to manage concurrent pdf documents load. It loads concurrently small sized documents and one by one big sized ones  
     * @author Andrea Vacondio
     *
     */
    private static class WorkQueue{
    	
    	public static final Integer HIGH = new Integer(0);
    	public static final Integer LOW = new Integer(1);
    	public static final Integer SINGLE = new Integer(2);
        
    	private final int highNThreads;
    	private final int lowNThreads;
    	
        private final HighWorker[] highThreads;
        private final LowWorker[] lowThreads;
        private final SingleWorker singleThread;
        
        private final LinkedList highQueue = new LinkedList();
        private final LinkedList lowQueue = new LinkedList();
        private final LinkedList singleQueue = new LinkedList();
        private int highRunning = 0;
        private int running = 0;
        
        private Object mutex = new Object();
        
        /**
         * @param highNThreads number of high priority thread
         * @param lowNThreads number of low priority thread
         */
        public WorkQueue(int highNThreads, int lowNThreads){
            this.highNThreads = highNThreads;
            this.lowNThreads = lowNThreads;
            
            highThreads = new HighWorker[this.highNThreads];
            lowThreads = new LowWorker[this.lowNThreads];
            singleThread = new SingleWorker();
            singleThread.start();
            
            for (int i=0; i<this.highNThreads; i++) {
            	highThreads[i] = new HighWorker();
            	highThreads[i].start();
            }
            for (int i=0; i<this.lowNThreads; i++) {
            	lowThreads[i] = new LowWorker();
            	lowThreads[i].start();
            }            
        }
        
        /**
         * Execute and addThread with the given priority
         * @param r
         * @param priority
         */
        public void execute(Runnable r, Integer priority) {
        	synchronized (mutex){
	           	if(HIGH.equals(priority)){
	           		highQueue.addLast(r); 
	           	}else if(SINGLE.equals(priority)){	           		
	           		singleQueue.addLast(r);
	           	}else{
	           		lowQueue.addLast(r);
	           	}
	           	mutex.notifyAll();
           	}
        }
        
        public synchronized void incHighRunningCounter(){
        	highRunning++;
        	incRunningCounter();
        }
        
        public synchronized void deincHighRunningCounter(){
        	highRunning--;  
        	deincRunningCounter();
        }
        
        public synchronized void incRunningCounter(){
        	running++;
        }
        
        public synchronized void deincRunningCounter(){
        	running--;        	
        }
        
        public int getRunning(){
        	return running;
        }
        
        /**
         * High priority thread
         * @author Andrea Vacondio
         *
         */
        private class HighWorker extends Thread {
        	
        	public void run() {        		            
        		Runnable r = null;
        		
                while (true) {
                    synchronized(mutex) {
                         while(highQueue.isEmpty()) {
                        	 try{
                        		 mutex.wait();
                             }
                             catch (InterruptedException ignored){}
                         }
                         r = (Runnable) highQueue.removeFirst();
                     }

                    if(r != null){
                    	try {                    	
                    		 incHighRunningCounter();
                    		 r.run();                         		
	                   }catch (RuntimeException e) {
	                	   log.error(e);
	                   }
	                   finally{
	                	   deincHighRunningCounter();                	  
	                   }
                    }                         
                }
        	}
        }
        
        /**
         * Low priority thread
         * @author Andrea Vacondio
         *
         */
        private class LowWorker extends Thread {
        	
        	public void run() {        		            
        		Runnable r = null;
        		
                while (true) {
                    synchronized(mutex) {
                         while(lowQueue.isEmpty() || highRunning>0) {
                        	 try{
                        		 if(lowQueue.isEmpty()){
                        			 mutex.wait();
                        		 }else{
                        			 mutex.wait(1000);
                        		 }
                        		 
                             }
                             catch (InterruptedException ignored){}
                         }
                         r = (Runnable) lowQueue.removeFirst();
                     }

                    if(r != null){
                    	try {
                    		incRunningCounter();
                    		r.run();                         		
                    	}catch (RuntimeException e) {
                    		log.error(e);
                    	}
                    	finally{
                    		deincRunningCounter();                	  
                    	}
               	 	} 
                }
        	}
        }
        
        /**
         * Single priority thread
         * @author Andrea Vacondio
         *
         */
        private class SingleWorker extends Thread {
        	
        	public void run() {        		            
        		Runnable r = null;
        		
                while (true) {
                    synchronized(mutex) {
                         while(singleQueue.isEmpty()) {
                        	 try{
                        		 mutex.wait();
                             }
                             catch (InterruptedException ignored){}
                         }
                         r = (Runnable) singleQueue.removeFirst();
                     }

                    if(r != null){
                    	try {
                    		incRunningCounter();
                    		r.run();                         		
                    	}catch (RuntimeException e) {
                    		log.error(e);
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
