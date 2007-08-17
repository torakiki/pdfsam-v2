/*
 * Created on 10-Feb-2006
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
package it.pdfsam.console.tools;

import it.pdfsam.console.MainConsole;
import it.pdfsam.console.exception.ParseException;

import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;
/**
 * Parser and core for the console. It creates a command line handler and parses input args.
 * If input command is correct (split or concat) it creates the right command line handler for the selected command
 * and it parses input args. If everything is correct, the right command is executed; an exception is thrwon otherwise. 
 * 
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfSplit
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 */
public class CmdParser {
 
    //constants used to get the split mode
    final static public String S_BURST = "BURST";
    final static public String S_SPLIT = "SPLIT";    
    final static public String S_NSPLIT = "NSPLIT";    
    final static public String S_EVEN = "EVEN";    
    final static public String S_ODD = "ODD";
	    
    //handler    
    private CmdLineHandler command_line_handler;
    //input arguments
    private String[] in_args;
//CONCAT
    //-f value
    private Collection cf_value;
    //-l value
    private File cl_value;
    //-u value
    private String cu_value;
    //-copyfields value
    private boolean copyfields_value = false;
    
//SPLIT
    //-p value
    private String sp_value;
    //-l value
    private File sf_value;
    //-s value
    private String ss_value;
    //number_page argument
    private String sn_value;

//ANY    
    //-o value
    private File o_value;
    //-log value
    private File log_value = null;
    //-overwrite value
    private boolean overwrite_value = false;
    //-compressed value
    private boolean compressed_value = false;
    
    //input command
    private byte input_command = 0x00;
    //input option
    private byte input_option = 0x00;
    
    //constants
    final static public byte F_OPT = 0x01;
    final static public byte L_OPT = 0x02;

    //constants
    final static public byte C_CONCAT = 0x01;
    final static public byte C_SPLIT = 0x02;    
 
    
    final static private String COMMAND = "java -jar pdfsam-console";
    final static private String DESCRIPTION = "concat, split pdf files";
    
    
    //concat options if concat command is given
    private final Parameter[] concat_opts = new Parameter[] {
            new FileParam("o",
                          "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
                          ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                          FileParam.REQUIRED, 
                          FileParam.SINGLE_VALUED),
            new FileParam("f",
                          "pdf files to concat: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
                          FileParam.IS_FILE & FileParam.IS_READABLE,
                          FileParam.OPTIONAL, 
                          FileParam.MULTI_VALUED),
            new StringParam("u",   
                          "page selection script. You can set a subset of pages to merge. Accepted values: \"all\" or \"num1-num2\" (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:all:), (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:12-14:) to merge file1.pdf and pages 12,13,14 of file2.pdf. If -u is not set default behaviour is to merge document completely",
                          StringParam.OPTIONAL),                                             
            new FileParam("l",
						  "xml or csv file containing pdf files list to concat. If cvs file in comma separated value format; if xml file <filelist><file value=\"filepath\" /></filelist>",
                          FileParam.IS_FILE & FileParam.IS_READABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED),
            new FileParam("log",
                          "text file to log output messages",
                          ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED),
            new BooleanParam("overwrite", "overwrite existing output file"),
            new BooleanParam("compressed", "compress output file"),
            new BooleanParam("copyfields", "input pdf documents contain forms (high memory usage)")                          
            };
            
    //split options if slit command is given
    private final Parameter[] split_opts = new Parameter[] {
            new FileParam("o",
                    "output directory",
                    ((FileParam.IS_DIR & FileParam.EXISTS)),
                    FileParam.REQUIRED, 
                    FileParam.SINGLE_VALUED),
            new FileParam("f",
                   "input pdf file to split",
                   FileParam.IS_FILE & FileParam.IS_READABLE,
                   FileParam.REQUIRED, 
                   FileParam.SINGLE_VALUED),
            new StringParam("p",   
                    "prefix for the output files name",
                            StringParam.OPTIONAL),                      
            new StringParam("s",   
                    "split type {["+CmdParser.S_BURST+"], ["+CmdParser.S_ODD+"], ["+CmdParser.S_EVEN+"], ["+CmdParser.S_SPLIT+"], ["+CmdParser.S_NSPLIT+"]}",
                    new String[] { CmdParser.S_BURST, CmdParser.S_ODD, CmdParser.S_EVEN, CmdParser.S_SPLIT, CmdParser.S_NSPLIT },
                            StringParam.REQUIRED),
            new StringParam("n",
                    "page number to spli at if -s is "+CmdParser.S_SPLIT +" or " + CmdParser.S_NSPLIT ,             
                    StringParam.OPTIONAL),
            new FileParam("log",
                    "text file to log output messages",
                    ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                    FileParam.OPTIONAL,
                    FileParam.SINGLE_VALUED),
            new BooleanParam("overwrite", "overwrite existing output file"),
            new BooleanParam("compressed", "compress output file")
    };    

    /**
     * The arguments for split command
     */
    private final Parameter[] split_arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[split]}",
                    new String[] { "split" },
                    StringParam.REQUIRED),              
    };

    /**
     * The arguments for concat command
     */
    private final Parameter[] concat_arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[concat]}",
                    new String[] { "concat" },
                    StringParam.REQUIRED),
    };
    
    /**
     * The arguments this program takes
     */
    private final Parameter[] arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[concat], [split]}",
                    new String[] { "concat", "split"},
                    StringParam.REQUIRED),

    };
    
    /**
     * The help text for this program
     */
    public static final String concat_helpText = "Concatenate pdf files. "+
        "you must specify the '-o /home/user/outfile.pdf' option to set the output file and the source file list:\n"+
        "'-f /tmp/file1.pdf /tmp/file2.pdf -f /tmp/file3.pdf [...]' to specify a file list or at least one file to concat.\n"+
        "'-l /tmp/list.csv' a csv file containing the list of files to concat, separated by a comma.\n"+
        "'-l /tmp/list.xml' a xml file containing the list of files to concat, <filelist><file value=\"filepath\" /></filelist>\n"+
        "'-u All:All:3-15' is optional to set pages selection. You can set a subset of pages to merge. Accepted values: \"all\" or \"num1-num2\" (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:all:), (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf -u all:12-14:) to merge file1.pdf and pages 12,13,14 of file2.pdf. If -u is not set default behaviour is to merge document completely\n"+
        "Note: You can use only one of these options not both in the same command line\n\n\n"+
        "'-overwrite' to overwrite output file if already exists.\n"+
        "'-compress' to compress output file.\n"+
        "'-copyfields' to deal with forms. Use this if input documents contain forms. This option will lead to a high memory usage.\n"+        
        "Example: java -jar pdfsam-console.jar -o /tmp/outfile.pdf -f /tmp/1.pdf -f /tmp/2.pdf concat\n"+
        "Example: java -jar pdfsam-console.jar -l c:\\docs\\list.csv concat";
    
    /**
     * The help text for this program
     */
    public static final String split_helpText = "Split pdf file. "+ 
	    "You must specify '-f /home/user/infile.pdf' option to set the input file you want to split.\n" +
	    "You must specify '-o /home/user' to set the output directory.\n"+
	    "You must specify '-s split_type' to set the split type. Possible values: {["+CmdParser.S_BURST+"], ["+CmdParser.S_ODD+"], ["+CmdParser.S_EVEN+"], ["+CmdParser.S_SPLIT+"], ["+CmdParser.S_NSPLIT+"]}\n"+
	    "'-p prefix_' to specify a prefix for output names of files. If it contains \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[CURRENTPAGE] generates FileName_prefix_005.pdf)\n"+
	    "Available prefix variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME].\n"+
	    "'-n number' to specify a page number to splip at if -s is SPLIT or NSPLIT.\n\n\n"+
        "'-overwrite' to overwrite output file if already exists.\n"+
        "'-compress' to compress output file.\n"+
	    "Example: java -jar pdfsam-console.jar -f /tmp/1.pdf -o /tmp -s BURST -p splitted_ split\n"+
	    "Example: java -jar pdfsam-console.jar -f /tmp/1.pdf -o /tmp -s NSPLIT -n 4 split\n";
  
 
    /**
     * The help text for this program
     */
    public static final String helpText = CmdParser.COMMAND+" -h [command] for commands help. ";
    
    /**
     * Costructor
     * @param input_args Arguments to parse
     */
    public CmdParser(String[] input_args ){
        //cmd handler creation
        in_args = input_args;
    }
    
    /**
     * Parse the in_args to get the input command type. It creates the right command handler for split or concat command
     * and it parses in_args again. Executes concat or split of pdf files if everything is ok, an exception is thrown otherwise.
     */
    public void parse() throws Exception{
    	String i_command  = "";
        if(in_args == null || in_args.length == 0){
            //create a new handler
            command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,null,arguments));
        }else{
            i_command = in_args[in_args.length-1];
            //parse command
            if (i_command.equals("concat")){
                input_command = CmdParser.C_CONCAT;
                //create a new handler specific for concat
                command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.concat_helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,concat_opts,concat_arguments));
            }else if (i_command.equals("split")){
                input_command = CmdParser.C_SPLIT;
                //create a new handler specific for split
                command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.split_helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,split_opts,split_arguments));
            }
	        else{
	            //create a new handler
	            command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,null,arguments));
            }
         }
        command_line_handler.setDieOnParseError(false);
        if(command_line_handler.parse(in_args)){
        	if (i_command.equals("concat")){
                ParseConcatCommand();        		
        	}else if (i_command.equals("split")){
                ParseSplitCommand();        		
        	}
        }else{
            throw new ParseException("ParseError: "+command_line_handler.getParseError());
        }
    }

    /**
     * Parser for the command line input with the "concat" argument. Input is validated and, if no exception is thrown,
     * is processed. Files extension must be of the right type. Options -l and -f can't be together in the input line. 
     * @return true if the command is parsed correctly, exception otherwise.
     * @throws Exception
     */
    private boolean ParseConcatCommand() throws Exception{
//PARSE -o
        FileParam o_opts = (FileParam) command_line_handler.getOption("o");
            //no output option given
            if (!(o_opts.isSet())){
                throw new ParseException("OutputNotFound: missing or illegal -o option.");
            }
            //output is given
            else{
                File out_file;
                out_file = o_opts.getFile();
                //output is given but is not a pdf file
                if (!(out_file.getPath().toLowerCase().endsWith(".pdf"))){
        			throw new ParseException("ParseConcatCommand: output file not a pdf format.");	
        		}
                else if(out_file.getName().toLowerCase().equals(".pdf")){
                    throw new ParseException("ParseConcatCommand: no output file name.");  
                }
                else{
                    o_value = out_file;
                }
            }
//END_PARSE -o
//PARSE -l -f            
    		FileParam l_opts = (FileParam) command_line_handler.getOption("l");
    		FileParam f_opts = (FileParam) command_line_handler.getOption("f");
            //both not set, no input given
            if ((!(l_opts.isSet())) && (!(f_opts.isSet()))){
                throw new ParseException("InputNotFound: no -f or -l option given.");
            }
            //both are set
            else if ((l_opts.isSet()) && (f_opts.isSet())){
                throw new ParseException("TooManyInputSources: both -f and -l options given.");
            }            //-l option error: no csv file given
            else if ((!(f_opts.isSet())) && (!(l_opts.getFile().getPath().toLowerCase().endsWith(".csv"))) && !(l_opts.getFile().getPath().toLowerCase().endsWith(".xml"))){
                throw new ParseException("ParseConcatCommand: input list file not a csv or xml format.");
            }
            //only f_opts is set
            else if ((f_opts.isSet()) && (!(l_opts.isSet()))){
                File input_file = f_opts.getFile();
                if (!(input_file.getPath().toLowerCase().endsWith(".pdf"))){
                    throw new ParseException("ParseConcatCommand: input file "+input_file.getName()+" is not a pdf format.");  
                }
                input_option = CmdParser.F_OPT;
                cf_value = f_opts.getFiles();
            }
            //only l_opts is set
            else if ((l_opts.isSet()) && (!(f_opts.isSet()))){
                //-l option error: no csv file given
                if (!(l_opts.getFile().getPath().toLowerCase().endsWith(".csv"))&& !(l_opts.getFile().getPath().toLowerCase().endsWith(".xml"))){
                    throw new ParseException("ParseConcatCommand: input list file not a csv or xml format.");
                }else{
                    input_option = CmdParser.L_OPT;
                    cl_value = l_opts.getFile();
                }
            }
//END_PARSE -l -f
//PARSE -log
            FileParam log_opts = (FileParam) command_line_handler.getOption("log");
            if (log_opts.isSet()){
                log_value = log_opts.getFile();
            }
//END_PARSE -log            
//PARSE -u            
            StringParam u_opts = (StringParam) command_line_handler.getOption("u");            
            //if it's set we proceed with validation
            if (u_opts.isSet()){
                //regexp pattern
                Pattern p = Pattern.compile("(([0-9]*[-][0-9]*[:])|(all:))*", Pattern.CASE_INSENSITIVE);
                if ((p.matcher(u_opts.getValue()).matches())) {
                    cu_value = u_opts.getValue();
                } else {
                    throw new ParseException("ParseConcatCommand: -u value parsing error. The string must be \"num1-num2:\" or \"all:\" repeated for each pdf file in input.");
                }
            }
//END_PARSE -u
//PARSE -overwrite            
            overwrite_value = ((BooleanParam) command_line_handler.getOption("overwrite")).isTrue();
//END PARSE -overwrite
//PARSE -compress            
            compressed_value = ((BooleanParam) command_line_handler.getOption("compressed")).isTrue();
//END PARSE -compress
//PARSE -copyfields            
            copyfields_value = ((BooleanParam) command_line_handler.getOption("copyfields")).isTrue();
//END PARSE -copyfields  
            return true;
    }
    
    /**
     * Parser for the command line input with the "split" argument. Input is validated and, if no exception is thrown,
     * is processed. Files extension must be of the right type. 
     * @return true if the command is parsed correctly, exception otherwise.
     * @throws Exception
     */
    private boolean ParseSplitCommand() throws Exception{
//PARSE -o
        FileParam o_opts = (FileParam) command_line_handler.getOption("o");
            //no output option given
            if ((o_opts.isSet())) {
                File out_file;
                out_file = o_opts.getFile();
                //output is given but is not a pdf file
                if (!(out_file.isDirectory())){
                    throw new ParseException("ParseSplitCommand: output is not a directory.");  
                }
                else{
                    o_value = out_file;
                }
            } else {
                throw new ParseException("OutputNotFound: missing or illegal -o option.");
            }
//END_PARSE -o
//PARSE -log
            FileParam log_opts = (FileParam) command_line_handler.getOption("log");
            if (log_opts.isSet()){
                log_value = log_opts.getFile();
            }
//END_PARSE -log            
            StringParam p_opts = (StringParam) command_line_handler.getOption("p");
            FileParam f_opts = (FileParam) command_line_handler.getOption("f");
            StringParam s_opts = (StringParam) command_line_handler.getOption("s");
            StringParam n_opts = (StringParam)command_line_handler.getOption("n");
//PARSE -p            
            if (p_opts.isSet()){
                sp_value = p_opts.getValue();
            }else{
                sp_value = "";
            }
//END_PARSE -p
//PARSE -f            
            File input_file;
            input_file = f_opts.getFile();
            //input is given but is not a pdf file
            if ((input_file.getPath().toLowerCase().endsWith(".pdf"))) {
                sf_value = input_file;
            } else {
                throw new ParseException("ParseSplitCommand: input file not a pdf format.");  
            }
//END_PARSE -f 
//PARSE -s
            if ((s_opts.isSet())) {
                ss_value = s_opts.getValue();
            } else {
                throw new ParseException("SplitTypeNotFound: not -s option given.");
            }
//END_PARSE -s
//PARSE -n            
            if((ss_value.equals(CmdParser.S_SPLIT)) || (ss_value.equals( CmdParser.S_NSPLIT))){   
                if (n_opts.isSet()){
                    sn_value = n_opts.getValue().trim();
                    //if nsplit -n option must be a number
                    if (ss_value.equals( CmdParser.S_NSPLIT)){
                        try{
                            Integer.parseInt(sn_value);
                        }catch (NumberFormatException nfe){
                            throw new ParseException("ParseSplitCommand: -n option not a numeric value.");
                        }
                    }
                    //if split i must validate the sequence
                    if (ss_value.equals( CmdParser.S_SPLIT)){
                      /*                Pattern p = Pattern.compile("(([0-9]*[-][0-9]*[:])|(all:))*", Pattern.CASE_INSENSITIVE);
                if (!(p.matcher(u_opts.getValue()).matches())){
                    throw new ParseException("ParseConcatCommand: -u value parsing error. The string must be \"num1-num2:\" or \"all:\" repeated for each pdf file in input.");
                }
*/  
                        //i can use "," or " " or "-" as separator
                    	sn_value = sn_value.replaceAll(",","-").replaceAll(" ","-");
                        Pattern p = Pattern.compile("([0-9]+)([-][0-9]+)*");
                        if (!(p.matcher(sn_value).matches())){
                            throw new ParseException("ParseSplitCommand: -n option must be a number or a sequence number-number-number...");
                        }
                    }
                }else{
                    throw new ParseException("ParseSplitCommand: unable to find required option -n for the selected split mode.");
                }
            }else{
                if (n_opts.isSet()){
                    throw new ParseException("ParseSplitCommand: unnecessary option -n for the selected split mode.");
                }else{
                    sn_value = "0";
                }
            }
//END_PARSE -n
//PARSE -compress            
            compressed_value = ((BooleanParam) command_line_handler.getOption("compressed")).isTrue();
//END PARSE -compress            
//PARSE -overwrite            
            overwrite_value = ((BooleanParam) command_line_handler.getOption("overwrite")).isTrue();
//END PARSE -overwrite            
            return true;
    }    
      
    
    /**
     * @return Returns the -f option value in concat command.
     */
    public Collection getCFValue() {
        return cf_value;
    }

    /**
     * @return Returns the -l option value in concat command.
     */
    public File getCLValue() {
        return cl_value;
    }
    
    /**
     * @return Returns the -u option value in concat command.
     */
    public String getCUValue() {
        return cu_value;
    }
    
    /**
     * @return	Returns the -copyfields option value in concat command.
     */
    public boolean isCCopyFields(){
    	return copyfields_value;
    }
    
    /**
     * @return Returns the -overwrite option value in concat command.
     * @deprecated use <code>isOverwrite()</code>.
     */
    public boolean COverwrite() {
        return overwrite_value;
    }
    
    /**
     * @return Returns the -o option value. in concat command
     */
    public File getOValue() {
        return o_value;
    }

    /**
     * @return Returns the -o option value. in concat command
     */
    public File getLogValue() {
        return log_value;
    }
    
    /**
     * @return Returns the input_option.
     */
    public byte getInputOption() {
        return input_option;
    }

    /**
     * @return Returns the input_command.
     */
    public byte getInputCommand() {
        return input_command;
    }

    /**
     * @return Returns the sf_value.
     */
    public File getSFValue() {
        return sf_value;
    }

    /**
     * @return Returns the snumber_pages_value.
     */
    public String getSNumberPageValue() {
        return sn_value;
    }

    /**
     * @return Returns the sp_value.
     */
    public String getSPValue() {
        return sp_value;
    }

    /**
     * @return Returns the ss_value.
     */
    public String getSSValue() {
        return ss_value;
    }
	
    /**
     * @return Returns the -overwrite option value.
     */
    public boolean isOverwrite() {
        return overwrite_value;
    }
 
    /**
     * @return Returns the -compress option value.
     */
    public boolean isCompressed() {
        return compressed_value;
    }
    
    /**
     * @return Returns the -overwrite option value in concat command.
     * @deprecated use <code>isOverwrite()</code>. This method is no longer working
     */
    public boolean MOverwrite() {
        return false;
    }
}
