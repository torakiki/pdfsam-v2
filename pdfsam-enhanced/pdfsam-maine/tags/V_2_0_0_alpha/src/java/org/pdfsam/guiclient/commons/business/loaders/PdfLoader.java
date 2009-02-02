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
	  
	private JPdfSelectionPanel panel;
    private JFileChooser fileChooser = null;
    private WorkQueue workQueue = null;
	
	public PdfLoader(JPdfSelectionPanel panel){
		this.panel = panel;
		workQueue = new WorkQueue();
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
		            	addFiles(fileChooser.getSelectedFiles());
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
    		workQueue.execute(new AddThread(file, password, pageSelection));
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
    		workQueue.execute(new ReloadThread(file, password, pageSelection, index));
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
    
    public synchronized void addFiles(File[] files){
    	 if (files != null){
         	for (int i=0; i < files.length; i++){
         		workQueue.execute(new AddThread(files[i]));
         	}
         }     
    }
    
    	
    /**
     * adds files to the selectionTable
     * @param files File objects list
     * @param ordered files are added keeping order 
     */
    public void addFiles(List<File> files, boolean ordered){
    	if (files != null && !files.isEmpty()){
    		addFiles((File[])files.toArray(new File[files.size()]));
   	 	}     
    }
    
    /**
     * Add files without keeping order
     * @param files
     */   
    public void addFiles(List<File> files){
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
                    //fix 04/11/08 for memory usage
                    pdfReader = new PdfReader(new RandomAccessFileOrArray(fileToAdd.getAbsolutePath()), (password != null)?password.getBytes():null);                	
                    tableItem.setEncrypted(pdfReader.isEncrypted());
                    tableItem.setPagesNumber(Integer.toString(pdfReader.getNumberOfPages()));
                    tableItem.setPdfVersion(pdfReader.getPdfVersion());
                    tableItem.setSyntaxErrors(pdfReader.isRebuilt());
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
     * Work queue used to load documents  
     * @author Andrea Vacondio
     *
     */
    private static class WorkQueue{
        
        private final SingleWorker singleThread;

        private final LinkedList<Runnable> singleQueue = new LinkedList<Runnable>();
        private int running = 0;
        
        private Object mutex = new Object();
        
        public WorkQueue(){
            singleThread = new SingleWorker();
            singleThread.start();
        }
        
        /**
         * Execute and addThread
         * @param r
         */
        public void execute(Runnable r) {
        	synchronized (mutex){           		
	           	singleQueue.addLast(r);
	           	mutex.notifyAll();
           	}
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
