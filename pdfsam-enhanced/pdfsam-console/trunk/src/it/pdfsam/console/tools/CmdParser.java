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
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;

import com.lowagie.text.pdf.PdfWriter;
/**
 * Parser and core for the console. It creates a command line handler and parses input args.
 * If input command is correct (split or concat) it creates the right command line handler for the selected command
 * and it parses input args. If everything is correct, the right command is executed; an exception is thrwon otherwise. 
 * 
 * @author Andrea Vacondio
 * @see it.pdfsam.console.tools.pdf.PdfSplit
 * @see it.pdfsam.console.tools.pdf.PdfConcat
 * @see it.pdfsam.console.tools.pdf.PdfEncrypt
 * @see it.pdfsam.console.tools.pdf.PdfAlternateMix
 */
public class CmdParser {
 
    //constants used to get the split mode
    final static public String S_BURST = "BURST";
    final static public String S_SPLIT = "SPLIT";    
    final static public String S_NSPLIT = "NSPLIT";    
    final static public String S_EVEN = "EVEN";    
    final static public String S_ODD = "ODD";
	
    //constants used to get the encrypt mode
    final static public String E_PRINT = "print";
    final static public String E_MODIFY = "modify";    
    final static public String E_COPY = "copy";    
    final static public String E_ANNOTATION = "modifyannotations";    
    final static public String E_SCREEN = "screenreaders";
    final static public String E_FILL = "fill";    
    final static public String E_ASSEMBLY = "assembly";
    final static public String E_DPRINT = "degradedprinting";
	
    //constants used to set the encrypt algorithm
    final static public String E_RC4_40 = "rc4_40";    
    final static public String E_RC4_128 = "rc4_128";
    final static public String E_AES_128 = "aes_128";
    
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

//ENCRYPT
	//-p value
	private String ep_value;
	//-apwd value
	private String eapwd_value;
	//-upwd value
	private String eupwd_value;
    //-f value
    private Collection ef_value;
    // -allow value
    private int eallow_opts;
    //-etype value
    private String etype_value;
	
//  MIX
    //-f1 value
    private File mf1_value;
    //-f2 value
    private File mf2_value;
    //-reversefirst value
    private boolean mreverse1_value = false;
    //-reversesecond value
    private boolean mreverse2_value = false;
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
    final static public byte C_ECRYPT = 0x03;  
    final static public byte C_MIX = 0x04;  
    
    final static private String COMMAND = "java -jar pdfsam-console";
    final static private String DESCRIPTION = "concat, split, encrypt, mix pdf files";
    
    
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
						  "xml or csv file containing pdf files list to concat. If csv file in comma separated value format; if xml file <filelist><file value=\"filepath\" /></filelist>",
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
    
    //encrypt options if encrypt command is given
    private final Parameter[] encrypt_opts = new Parameter[] {
            new FileParam("o",
                    "output directory",
                    ((FileParam.IS_DIR & FileParam.EXISTS)),
                    FileParam.REQUIRED, 
                    FileParam.SINGLE_VALUED),
            new FileParam("f",
                          "pdf files to encrypt: a list of existing pdf files (EX. -f /tmp/file1.pdf -f /tmp/file2.pdf)",
                          FileParam.IS_FILE & FileParam.IS_READABLE,
                          FileParam.REQUIRED, 
                          FileParam.MULTI_VALUED),
            new StringParam("p",   
                          "prefix for the output files name",
                           StringParam.OPTIONAL), 
            new StringParam("apwd",   
                           "administrator password for the document",
                            StringParam.OPTIONAL), 
            new StringParam("upwd",   
                            "user password for the document",
                            StringParam.OPTIONAL),                            
           new StringParam("allow",
                          "permissions: a list of permissions",
						  new String[] { CmdParser.E_PRINT, CmdParser.E_MODIFY, CmdParser.E_COPY, CmdParser.E_ANNOTATION, CmdParser.E_FILL, CmdParser.E_SCREEN, CmdParser.E_ASSEMBLY, CmdParser.E_DPRINT },
                          FileParam.OPTIONAL, 
                          FileParam.MULTI_VALUED),						   
           new FileParam("log",
                          "text file to log output messages",
                          ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED),
           new StringParam("etype",   
                           "encryption angorithm. If omitted it uses rc4_128",
                           new String[] { CmdParser.E_RC4_40, CmdParser.E_RC4_128, CmdParser.E_AES_128},
                           FileParam.OPTIONAL, 
                           FileParam.SINGLE_VALUED),
           new BooleanParam("overwrite", "overwrite existing output file"),
           new BooleanParam("compressed", "compress output file")
    };
    
    //mix options if mix command is given
    private final Parameter[] mix_opts = new Parameter[] {
            new FileParam("o",
                    "pdf output file: if it doesn't exist it's created, if it exists it must be writeable",
                    ((FileParam.DOESNT_EXIST) | (FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_WRITEABLE)),
                    FileParam.REQUIRED, 
                    FileParam.SINGLE_VALUED),
            new FileParam("f1",
                   "first input pdf file to split",
                   FileParam.IS_FILE & FileParam.IS_READABLE,
                   FileParam.REQUIRED, 
                   FileParam.SINGLE_VALUED),
            new FileParam("f2",
                   "second input pdf file to split",
                   FileParam.IS_FILE & FileParam.IS_READABLE,
                   FileParam.REQUIRED, 
                   FileParam.SINGLE_VALUED),
            new BooleanParam("reversefirst", "reverse first input file"),
            new BooleanParam("reversesecond", "reverse second input file"),
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
     * The arguments for encrypt command
     */
    private final Parameter[] encrypt_arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[encrypt]}",
                    new String[] { "encrypt" },
                    StringParam.REQUIRED),
    };    

    /**
     * The arguments for mix command
     */
    private final Parameter[] mix_arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[mix]}",
                    new String[] { "mix" },
                    StringParam.REQUIRED),
    };    

    /**
     * The arguments this program takes
     */
    private final Parameter[] arguments = new Parameter[] {
            new StringParam("command",   
                    "command to execute {[concat], [split], [encrypt], [mix]}",
                    new String[] { "concat", "split", "encrypt", "mix" },
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
    public static final String encrypt_helpText = "Encrypt pdf files. "+ 
    "You must specify '-o /home/user' to set the output directory.\n"+
    "You must specify '-f /tmp/file1.pdf /tmp/file2.pdf -f /tmp/file3.pdf [...]' to specify a file list to encrypt.\n"+
    "'-apwd password' to set the owner password.\n"+
    "'-upwd password' to set the user password.\n"+
    "'-allow permission' to set the permissions list. Possible values {["+CmdParser.E_PRINT+"], ["+CmdParser.E_ANNOTATION+"], ["+CmdParser.E_ASSEMBLY+"], ["+CmdParser.E_COPY+"], ["+CmdParser.E_DPRINT+"], ["+CmdParser.E_FILL+"], ["+CmdParser.E_MODIFY+"], ["+CmdParser.E_SCREEN+"]}\n\n\n"+
	"'-p prefix_' to specify a prefix for output names of files. If it contains \"[TIMESTAMP]\" it performs variable substitution. (Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf)\n"+
	"Available prefix variables: [TIMESTAMP], [BASENAME].\n"+
    "'-etype ' to set the encryption angorithm. If omitted it uses rc4_128. Possible values {["+CmdParser.E_AES_128+"], ["+CmdParser.E_RC4_128+"], ["+CmdParser.E_RC4_40+"]}\n\n\n"+
    "'-overwrite' to overwrite output file if already exists.\n"+
    "'-compress' to compress output file.\n"+
    "Example: java -jar pdfsam-console.jar -f /tmp/1.pdf -o /tmp -apwd hallo -upwd word -allow print -allow fill -etype rc4_128 -p encrypted_ encrypt\n";
    
    /**
     * The help text for this program
     */
    public static final String mix_helpText = "Mix alternate two pdf files. "+
    	"You must specify '-o /home/user/out.pdf' to set the output file.\n"+
	    "You must specify '-f1 /home/user/infile1.pdf' option to set the first input file.\n" +
	    "You must specify '-f2 /home/user/infile2.pdf' option to set the second input file.\n" +
        "'-reversefirst' reverse the first input file.\n"+
        "'-reversesecond' reverse the second input file.\n"+
        "'-overwrite' to overwrite output file if already exists.\n"+
        "'-compress' to compress output file.\n"+
        "Example: java -jar pdfsam-console.jar -o /tmp/outfile.pdf -f1 /tmp/1.pdf -f2 /tmp/2.pdf -reversesecond mix\n";

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
            }else if (i_command.equals("encrypt")){
                input_command = CmdParser.C_ECRYPT;
                //create a new handler specific for encrypt
                command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.encrypt_helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,encrypt_opts,encrypt_arguments));
            }else if (i_command.equals("mix")){
                input_command = CmdParser.C_MIX;
                //create a new handler specific for mix
                command_line_handler = new VersionCmdLineHandler("pdfsam-console ver."+MainConsole.VERSION,new HelpCmdLineHandler(CmdParser.mix_helpText,CmdParser.COMMAND,CmdParser.DESCRIPTION,mix_opts,mix_arguments));
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
        	}else if (i_command.equals("encrypt")){
        		ParseEncryptCommand();
        	}else if (i_command.equals("mix")){
        		ParseMixCommand();
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
                    throw new ParseException("ParseConcatCommand: input list file not a csv format.");
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
                if (!(p.matcher(u_opts.getValue()).matches())){
                    throw new ParseException("ParseConcatCommand: -u value parsing error. The string must be \"num1-num2:\" or \"all:\" repeated for each pdf file in input.");
                }
                else{
                    cu_value = u_opts.getValue();
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
            if (!(o_opts.isSet())){
                throw new ParseException("OutputNotFound: missing or illegal -o option.");
            }
            //output is given
            else{
                File out_file;
                out_file = o_opts.getFile();
                //output is given but is not a pdf file
                if (!(out_file.isDirectory())){
                    throw new ParseException("ParseSplitCommand: output is not a directory.");  
                }
                else{
                    o_value = out_file;
                }
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
            if (!(input_file.getPath().toLowerCase().endsWith(".pdf"))){
                throw new ParseException("ParseSplitCommand: input file not a pdf format.");  
            }
            else{
                sf_value = input_file;
            }
//END_PARSE -f 
//PARSE -s
            if (!(s_opts.isSet())){
                throw new ParseException("SplitTypeNotFound: not -s option given.");
            }
            else{
                ss_value = s_opts.getValue();
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
     * Parser for the command line input with the "encrypt" argument. Input is validated and, if no exception is thrown,
     * is processed. Files extension must be of the right type. 
     * @return true if the command is parsed correctly, exception otherwise.
     * @throws Exception
     */
    private boolean ParseEncryptCommand() throws Exception{
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
                if (!(out_file.isDirectory())){
                    throw new ParseException("ParseEncryptCommand: output is not a directory.");  
                }
                else{
                    o_value = out_file;
                }
            }
//END_PARSE -o
//PARSE -log
            FileParam log_opts = (FileParam) command_line_handler.getOption("log");
            if (log_opts.isSet()){
                log_value = log_opts.getFile();
            }
//END_PARSE -log            
            StringParam p_opts = (StringParam) command_line_handler.getOption("p");
            StringParam a_pwd_opts = (StringParam) command_line_handler.getOption("apwd");
            StringParam u_pwd_opts = (StringParam) command_line_handler.getOption("upwd");
            StringParam etype_opts = (StringParam) command_line_handler.getOption("etype");
            FileParam f_opts = (FileParam) command_line_handler.getOption("f");
//PARSE -p            
            if (p_opts.isSet()){
                ep_value = p_opts.getValue();
            }else{
                ep_value = "";
            }
//END_PARSE -p
//PARSE -apwd            
            if (a_pwd_opts.isSet()){
                eapwd_value = a_pwd_opts.getValue();
            }else{
                eapwd_value = "";
            }
//END_PARSE -apwdp
//PARSE -upwd            
            if (u_pwd_opts.isSet()){
                eupwd_value = u_pwd_opts.getValue();
            }else{
                eupwd_value = "";
            }
//END_PARSE -p
//PARSE -f            
            File input_file = f_opts.getFile();
            if (!(input_file.getPath().toLowerCase().endsWith(".pdf"))){
                throw new ParseException("ParseEncryptCommand: input file "+input_file.getName()+" is not a pdf format.");  
            }
            ef_value = f_opts.getFiles();           
//END_PARSE -f 
//PARSE_STRENGTH
            //default value
            etype_value = CmdParser.E_RC4_128;
            if ((etype_opts.isSet())){
            	etype_value = etype_opts.getValue();
            }
//END_PARSE_STRENGTH
//PARSE -allow
            StringParam allow_opts = (StringParam) command_line_handler.getOption("allow");
            if (allow_opts.isSet()){
			    //To populate permissions HashMap based on enc type (40-128 bit)
            	HashMap permissionMap = getPermissionMap(etype_value);
			    Collection permissions_list = allow_opts.getValues();
			    Iterator it = permissions_list.iterator();
			    int tmp_permissions = 0;
			    while (it.hasNext()){
			    	String a_value = (String)it.next();
			    	Object value = permissionMap.get(a_value);
			    	if(value != null){
			    		tmp_permissions |= ((Integer)value).intValue();
			    	}
			    }
			    eallow_opts = tmp_permissions;
            }
//END_PARSE -allow   
//PARSE -overwrite            
            overwrite_value = ((BooleanParam) command_line_handler.getOption("overwrite")).isTrue();
//END PARSE -overwrite            
//PARSE -compress            
            compressed_value = ((BooleanParam) command_line_handler.getOption("compressed")).isTrue();
//END PARSE -compress
            return true;
    }    


    /**
     * Parser for the command line input with the "mix" argument. Input is validated and, if no exception is thrown,
     * is processed. Files extension must be of the right type. 
     * @return true if the command is parsed correctly, exception otherwise.
     * @throws Exception
     */
    private boolean ParseMixCommand() throws Exception{
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
        			throw new ParseException("ParseMixCommand: output file not a pdf format.");	
        		}
                else if(out_file.getName().toLowerCase().equals(".pdf")){
                    throw new ParseException("ParseMixCommand: no output file name.");  
                }
                else{
                    o_value = out_file;
                }
            }
//END_PARSE -o
//PARSE -f            
    		FileParam f1_opts = (FileParam) command_line_handler.getOption("f1");           
            if(f1_opts.isSet()){
            	File input_file = f1_opts.getFile();
                if (!(input_file.getPath().toLowerCase().endsWith(".pdf"))){
                    throw new ParseException("ParseMixCommand: input file "+input_file.getName()+" is not a pdf format.");  
                }
                input_option = CmdParser.F_OPT;
                mf1_value = f1_opts.getFile();
            }else{
            	throw new ParseException("ParseMixCommand: -f1 not set.");	
            }
            FileParam f2_opts = (FileParam) command_line_handler.getOption("f2");           
            if(f2_opts.isSet()){
            	File input_file = f2_opts.getFile();
                if (!(input_file.getPath().toLowerCase().endsWith(".pdf"))){
                    throw new ParseException("ParseMixCommand: input file "+input_file.getName()+" is not a pdf format.");  
                }
                input_option = CmdParser.F_OPT;
                mf2_value = f2_opts.getFile();
            }else{
            	throw new ParseException("ParseMixCommand: -f2 not set.");	
            }           
//END_PARSE -f
//PARSE -log
            FileParam log_opts = (FileParam) command_line_handler.getOption("log");
            if (log_opts.isSet()){
                log_value = log_opts.getFile();
            }
//END_PARSE -log     
//PARSE -reverse            
            mreverse1_value = ((BooleanParam) command_line_handler.getOption("reversefirst")).isTrue();
            mreverse2_value = ((BooleanParam) command_line_handler.getOption("reversesecond")).isTrue();
//END PARSE -reverse            
//PARSE -overwrite            
            overwrite_value = ((BooleanParam) command_line_handler.getOption("overwrite")).isTrue();
//END PARSE -overwrite
//PARSE -compress            
            compressed_value = ((BooleanParam) command_line_handler.getOption("compressed")).isTrue();
//END PARSE -compress
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
     * @return Returns the -f option value in encrypt command.
     */
    public Collection getEFValue() {
        return ef_value;
    }	
	
    /**
     * @return Returns the ep_value.
     */
    public String getEPValue() {
        return ep_value;
    }

    /**
     * @return Returns the -etype option value in encrypt command.
     */
    public String getETypeValue() {
        return etype_value;
    }

    /**
     * @return the -allow value
     */
	public int getEAllowValue() {
		return eallow_opts;
	}

	/**
	 * @return -apdw value
	 */
	public String getEApwdValue() {
		return eapwd_value;
	}

	/**
	 * @return -updw value
	 */
	public String getEUpwdValue() {
		return eupwd_value;
	}
	
    /**
     * @return Returns the mf1_value.
     */
    public File getMF1Value() {
        return mf1_value;
    }

    /**
     * @return Returns the mf2_value.
     */
    public File getMF2Value() {
        return mf2_value;
    }
    
    /**
     * @return Returns the -reversefirst option value in concat command.
     */
    public boolean MReverseFirst() {
        return mreverse1_value;
    }
    
    /**
     * @return Returns the -reversesecond option value in concat command.
     */
    public boolean MReverseSecond() {
        return mreverse2_value;
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
	/**
	 * @param encAlg encryption algorithm
	 * @return The permissions map based on the chosen encryption
	 */
	private HashMap getPermissionMap(String encAlg){
		HashMap retMap = new HashMap();
		if(encAlg.equals(CmdParser.E_AES_128) || encAlg.equals(CmdParser.E_RC4_128)){
			retMap.put(CmdParser.E_PRINT,new Integer(PdfWriter.AllowPrinting));
			retMap.put(CmdParser.E_MODIFY,new Integer(PdfWriter.AllowModifyContents));
			retMap.put(CmdParser.E_COPY,new Integer(PdfWriter.AllowCopy));
			retMap.put(CmdParser.E_ANNOTATION,new Integer(PdfWriter.AllowModifyAnnotations));
			retMap.put(CmdParser.E_FILL,new Integer(PdfWriter.AllowFillIn));
			retMap.put(CmdParser.E_SCREEN,new Integer(PdfWriter.AllowScreenReaders));
			retMap.put(CmdParser.E_ASSEMBLY,new Integer(PdfWriter.AllowAssembly));
			retMap.put(CmdParser.E_DPRINT,new Integer(PdfWriter.AllowDegradedPrinting));
	    }else{
			retMap.put(CmdParser.E_PRINT,new Integer(PdfWriter.AllowPrinting));
			retMap.put(CmdParser.E_MODIFY,new Integer(PdfWriter.AllowModifyContents));
			retMap.put(CmdParser.E_COPY,new Integer(PdfWriter.AllowCopy));
			retMap.put(CmdParser.E_ANNOTATION,new Integer(PdfWriter.AllowModifyAnnotations));
	    }
		return retMap;
	}
}
