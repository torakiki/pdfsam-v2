/*
 * Created on 09-Feb-2006
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

package it.pdfsam.console;

import it.pdfsam.console.events.WorkDoneEvent;
import it.pdfsam.console.interfaces.WorkDoneListener;
import it.pdfsam.console.tools.CmdParser;
import it.pdfsam.console.tools.PdfConcat;
import it.pdfsam.console.tools.PdfSplit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.event.EventListenerList;

import jcmdline.BooleanParam;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.lowagie.text.Document;
/**
 * Console class.
 * It takes input arguments, parse tham and execute the right operation on pdf files.
 * 
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.CmdParser
 * @see it.pdfsam.console.tools.PdfConcat
 * @see it.pdfsam.console.tools.PdfSplit
 * @see it.pdfsam.console.exception.ParseException
 * @see it.pdfsam.console.exception.SplitException
 */
public class MainConsole{
 
    //constants used to get the split mode
    final static public String S_BURST = "BURST";
    final static public String S_SPLIT = "SPLIT";    
    final static public String S_NSPLIT = "NSPLIT";    
    final static public String S_EVEN = "EVEN";    
    final static public String S_ODD = "ODD";
    //list of listeners
    private EventListenerList listeners = new EventListenerList();
    /**
     * Console version
     */
    public static final String VERSION = "0.5.5"; 
    /**
     * The options available for this program
     */
    private final Parameter[] opts = new Parameter[] {
        new FileParam("o",
                      "merge: pdf output file. if it doesn't exist it's created, if it exists it must be writeable\nsplit: output directory",
                      ((FileParam.IS_DIR) | (FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                      FileParam.REQUIRED, 
                      FileParam.SINGLE_VALUED),
        new FileParam("f",
                      "REQUIRED IN SPLIT MODE\nmerge: pdf files to concat. a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)\nsplit: input pdf file to split",
                      FileParam.IS_FILE & FileParam.IS_READABLE,
                      FileParam.OPTIONAL, 
                      FileParam.MULTI_VALUED),
        new StringParam("u",   
                      "merge: page selection script. You can set a subset of pages to merge. Accepted values: \"all\" or \"num1-num2\" (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:all:), (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:12-14:) to merge file1.pdf and pages 12,13,14 of file2.pdf. If -u is not set default behaviour is to merge document completely",
                      StringParam.OPTIONAL),                      
        new FileParam("l",
                      "merge: csv file containing pdf files list to concat in comma separated value format",
                      FileParam.IS_FILE & FileParam.IS_READABLE,
                      FileParam.OPTIONAL,
                      FileParam.SINGLE_VALUED),
        new StringParam("p",   
                        "split: prefix for the output files name",
                        StringParam.OPTIONAL),                      
        new StringParam("s",   
                      "REQUIRED IN SPLIT MODE\nsplit: split type {["+MainConsole.S_BURST+"], ["+MainConsole.S_ODD+"], ["+MainConsole.S_EVEN+"], ["+MainConsole.S_SPLIT+"], ["+MainConsole.S_NSPLIT+"]}",
                      new String[] { MainConsole.S_BURST, MainConsole.S_ODD, MainConsole.S_EVEN, MainConsole.S_SPLIT, MainConsole.S_NSPLIT },
                      StringParam.OPTIONAL),
        new StringParam("n",
                      "split: page number to spli at if -s is "+MainConsole.S_SPLIT +" or " + MainConsole.S_NSPLIT ,             
                      StringParam.OPTIONAL),
        new FileParam("log",
                      "text file to log output messages",
                      ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                      FileParam.OPTIONAL,
                      FileParam.SINGLE_VALUED),
        new BooleanParam("overwrite", "merge: overwrite existing output file")
    };
  
    /**
     * The arguments this program takes
     */
    private final Parameter[] arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[concat], [split]}",
                    new String[] { "concat", "split" },
                    StringParam.REQUIRED),

    };
    
    /**
     * The help text for this program
     */
    public static final String helpText = "This tools can be used to concat or split pdf files. If the  'concat' command is given "+
        "you must use the '-o /home/user/outfile.pdf' option to set the output file and you must specify the source:\n"+
        "'-f /tmp/file1.pdf /tmp/file2.pdf -f /tmp/file3.pdf [...]' to specify a file list to concat.\n"+
        "'-l /tmp/list.csv' a csv file containing the list of files to concat, separated by a comma.\n"+
        "Note: You can use only one of these options not both in the same command line\n\n\n"+
        "Example: Pdfsam -o /tmp/outfile.pdf -f /tmp/1.pdf -f /tmp/2.pdf concat\n"+
        "Example: Pdfsam -o /tmp/outfile.pdf -l c:\\docs\\list.csv concat";
       
    public static void main(String[] args){
        try{
            
            MainConsole mc = new MainConsole();            
            System.out.println(mc.mainAction(args, true));
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    /**
     * It takes input parameters, parse tham. If no exception is thrown it executes the right function (split, concat..)
     * and return the output message.
     * 
     * @param args Arguments String array.
     * @param html_output  If the output message is HTML or plain text.
     * @return Output message.
     * @throws Exception If something goes wrong an exception is thrown.
     */
    public String mainAction(String[] args, boolean html_output) throws Exception{
        String out_msg = "";
        //command parser creation
        CmdParser cmdp = new CmdParser("Ver. "+MainConsole.VERSION, helpText, "Pdfsam", "Concat or split pdf files",
                opts,
                arguments, args);
        //parsing
        cmdp.Parse();
        //if it's a concat
        if ((cmdp.getInputCommand()) == CmdParser.C_CONCAT){
            //and it a -f option
            if (cmdp.getInputOption() == CmdParser.F_OPT){
                PdfConcat pdf_concatenator = new PdfConcat(cmdp.getCFValue(), cmdp.getOValue(), cmdp.getCUValue(), cmdp.COverwrite(), this);
                pdf_concatenator.doConcat();
                if (html_output) {
                    out_msg = pdf_concatenator.getOutHTMLMessage();
                }
                else{
                    out_msg = pdf_concatenator.getOutMessage();
                }
            }
            else if(cmdp.getInputOption() == CmdParser.L_OPT){
                File csv = cmdp.getCLValue();
                Collection file_list = parseCsvFile(csv);
                if (file_list == null){
                    out_msg = "Error reading csv file-";
                }else{
                    PdfConcat pdf_concatenator = new PdfConcat(file_list, cmdp.getOValue(), cmdp.getCUValue(), cmdp.COverwrite(), this);
                    pdf_concatenator.doConcat();
                    if (html_output) {
                        out_msg = pdf_concatenator.getOutHTMLMessage();
                    }
                    else{
                        out_msg = pdf_concatenator.getOutMessage();
                    }
                }
            }
        }
        else if ((cmdp.getInputCommand()) == CmdParser.C_SPLIT){
            PdfSplit pdf_splitter = new PdfSplit(cmdp.getOValue(), cmdp.getSFValue(), cmdp.getSPValue(), cmdp.getSSValue(), cmdp.getSNumberPageValue(), this);
            pdf_splitter.doSplit();
            if (html_output) {
                out_msg = pdf_splitter.getOutHTMLMessage();
            }
            else{
                out_msg = pdf_splitter.getOutMessage();
            }
        }
        //try to write on output file
        try{
            File log_out_file =cmdp.getLogValue(); 
            if (log_out_file != null){
                /*writer con encoding UTF-8*/
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(log_out_file), "UTF8"));
                writer.write(out_msg.replaceAll("<br>", "\n"));
                writer.close();
            }
        }catch (Exception e){
            out_msg += "Unable to write on log output file-"; 
        }
        return out_msg;
    }
    
    /**
     * Reads the input cvs file and return a Collection of files
     * @param csv_file CSV input file (separator ",")
     * @return Collection of files absolute path
     */
    private Collection parseCsvFile(File csv_file){
        String cache_content = "";
        try {
            FileReader file_reader = new FileReader(csv_file);
            BufferedReader buffer_reader = new BufferedReader(file_reader);
            String temp = "";
            //read file
            while ((temp = buffer_reader.readLine()) != null){
                cache_content += temp;            
            }
            buffer_reader.close();
          }
       catch (IOException e) {
            return null;
       }
       //gives back the collection
       return Arrays.asList(cache_content.split(","));            
   }
   
   /**
    * Sets the Meta data "Creator" of the document
    * @param doc_to_set The document to set the creator
    */ 
   public static void setDocumentCreator(Document doc_to_set){
       doc_to_set.addCreator("pdfsam-console (Ver. " +MainConsole.VERSION+ ")");
   }
   
   /**
    * Adds a listener to the list
    * @param wdl listener to add
    */
   public void addWorkDoneListener(WorkDoneListener wdl) {
       listeners.add(WorkDoneListener.class, wdl);
   }
   
   /**
    * Removes a listener to the list
    * @param wdl listener to remove
    */
   public void removeWorkDoneListener(WorkDoneListener wdl) {
       listeners.remove(WorkDoneListener.class, wdl);
   }
   
   /**
    * Tells the listeners that the percentage of work done has changed
    * @param wde Event
    */
   public void fireWorkDoneEvent(WorkDoneEvent wde)
   {
        Object[] listeners_list = listeners.getListenerList();
        // loop through each listener and pass on the event if needed
        for (int i = listeners_list.length-2; i>=0; i-=2){
             if (listeners_list[i]==WorkDoneListener.class)                 
             {
                 wde.dispatch((WorkDoneListener)listeners_list[i+1]);
                 //  ((WorkDoneListener)listeners_list[i]).percentageOfWorkDoneChanged(wde);
             }            
        }
   }
}
