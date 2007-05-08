/*
 * Created on 08-Jan-2007
 * Copyright notice: this code is based on Split and Burst classes by Mark Thompson. Copyright (c) 2002 Mark Thompson.
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
package it.pdfsam.console.tools.pdf;

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.events.WorkDoneEvent;
import it.pdfsam.console.exception.ConsoleException;
import it.pdfsam.console.tools.LogFormatter;

import java.io.File;

/**
 * 
 * Superclass used to create a pdf tool.
 * @author Andrea Vacondio
 * 
 */
public abstract class GenericPdfTool {

	private MainConsole source;
	protected String out_message = "";;
	 
	public GenericPdfTool(MainConsole source){
		this.source = source;
	}
	
	   /**
     * fire a work indeterminate event say it's finish is indeterminate
     */
	protected void workingIndeterminate(){
        //source must not be null 
        if (source != null){
            if (source instanceof MainConsole){
                source.fireWorkDoneEvent(new WorkDoneEvent(this, WorkDoneEvent.WORK_INDETERMINATE));
            }
            
        }
    }
    
    /**
     * fire a work done event
     */
    protected void workCompleted(){
        //source must not be null 
        if (source != null){
            if (source instanceof MainConsole){
                source.fireWorkDoneEvent(new WorkDoneEvent(this, WorkDoneEvent.WORK_DONE));
            }
            
        }
    }
    
	/**
	 * fire a work done event with the new percentage of work done
	 * 
	 * @param percentage
	 */
    protected void percentageChanged(int percentage) {
		// source must not be null
		if (source != null) {
			if (source instanceof MainConsole) {
				source.fireWorkDoneEvent(new WorkDoneEvent(this,
						WorkDoneEvent.PERCENTAGE_CHANGE, percentage));
			}

		}
	}	
	 
    /**
     * fire a work done event with the new percentage of work done
     * @param percentage
     * @param pages 
     */
    protected void percentageChanged(int percentage, int pages){
        //source must not be null 
        if (source != null){
            if (source instanceof MainConsole){
                source.fireWorkDoneEvent(new WorkDoneEvent(this, WorkDoneEvent.PERCENTAGE_CHANGE, percentage, pages));
            }
            
        }
    }    
    /**
     * @return Returns the out_message.
     */
    public String getOutMessage() {
        return out_message;
    }
    
    /**
     * @return Returns the out_message with <br> html tag.
     */
    public String getOutHTMLMessage() {
        return out_message.replaceAll(">","&gt;").replaceAll("<", "&lt;").replaceAll("\n", "<br>");
    }
    
    /**
     * rename temporary file to output file
     * @param tmp_o_file temporary file to rename
     * @param o_file file to rename to
     * @param overwrite_boolean overwrite exsisting file
     */
    protected void renameTemporaryFile(File tmp_o_file, File o_file, boolean overwrite_boolean){
    	if(tmp_o_file != null && o_file != null){
	    	try{
			       if (o_file.exists()){
			          //check if overwrite is allowed
			          if (overwrite_boolean){
			                 o_file.delete();
			          }else{
			        	  out_message += LogFormatter.formatMessage("OverwriteNotAllowed: Cannot overwrite output file (-overwrite option not passed), a temporary file has been created ("+tmp_o_file.getName()+").\n");
			          }		                
			       }
		    	   if(!tmp_o_file.renameTo(o_file)){
		    		   out_message += LogFormatter.formatMessage("IOError: Unable to rename temporary file "+tmp_o_file.getName()+" to "+o_file.getName()+".\n");
			       }
	         }
	         catch(Exception e){
	         	out_message += LogFormatter.formatMessage("Exception renaming "+tmp_o_file.getName()+" to "+o_file.getName()+": "+e.getMessage()+".\n");
	         }
         }else{
        	 out_message += LogFormatter.formatMessage("Exception renaming temporary file, source or destination are null.\n");
         }
    }
    
    /**
     * Execute the tool command. On error an exception is thrown.
     * @throws ConsoleException
     */
    public abstract void execute() throws ConsoleException;
}
