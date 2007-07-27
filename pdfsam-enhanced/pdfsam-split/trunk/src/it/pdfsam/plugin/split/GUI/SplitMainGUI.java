/*
 * Created on 17-Feb-2006
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
package it.pdfsam.plugin.split.GUI;

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.MainConsole;
import it.pdfsam.console.tools.CmdParser;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.panels.LogPanel;
import it.pdfsam.plugin.split.component.JSplitRadioButton;
import it.pdfsam.plugin.split.listener.EnterDoClickListener;
import it.pdfsam.plugin.split.listener.RadioListener;
import it.pdfsam.util.DirFilter;
import it.pdfsam.util.PdfFilter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.dom4j.Element;
import org.dom4j.Node;
/**
 * Plugable JPanel provides a GUI for split functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class SplitMainGUI extends AbstractPlugIn{
    

	private static final long serialVersionUID = -8334827604350849363L;
	private JTextField out_prefix_text;
    private SpringLayout s_panel_layout;
    private SpringLayout destination_panel_layout;
    private JTextField dest_folder_text;
    private JTextField this_page_text_field;
    private JTextField n_pages_text_field;
    private JHelpLabel checks_help_label;
    private SpringLayout options_pane_layout;
    private JTextField source_text_field;
    private JHelpLabel prefix_help_label;
    private JHelpLabel destination_help_label; 
    private SpringLayout split_spring_layout;
    private String  split_type = "";
    private ResourceBundle i18n_messages;
    private Configuration config;
    private MainConsole mc;
    
//file_chooser    
    private final JFileChooser browse_file_chooser = new JFileChooser();
    private final JFileChooser browse_dest_file_chooser = new JFileChooser();

//button
    private final JButton browse_button = new JButton();
    private final JButton browse_dest_button = new JButton();        
    private final JButton run_button = new JButton();
//key_listeners
    private final EnterDoClickListener browse_enterkey_listener = new EnterDoClickListener(browse_button);
    private final EnterDoClickListener browsed_enterkey_listener = new EnterDoClickListener(browse_dest_button);
    private final EnterDoClickListener run_enterkey_listener = new EnterDoClickListener(run_button);
    
//split_radio
    private final JSplitRadioButton burst_radio = new JSplitRadioButton(CmdParser.S_BURST);
    private final JSplitRadioButton every_n_radio = new JSplitRadioButton(CmdParser.S_NSPLIT);        
    private final JSplitRadioButton even_radio = new JSplitRadioButton(CmdParser.S_EVEN);
    private final JSplitRadioButton odd_radio = new JSplitRadioButton(CmdParser.S_ODD);
    private final JSplitRadioButton this_page_radio = new JSplitRadioButton(CmdParser.S_SPLIT);
//radio
    private final JRadioButton same_as_source_radio = new JRadioButton();
    private final JRadioButton choose_a_folder_radio = new JRadioButton();
    private ButtonGroup split_options_radio_group;
//checks
    private final JCheckBox overwrite_checkbox = new JCheckBox();
    private final JCheckBox output_compressed_check = new JCheckBox();
    
//focus policy 
    private final SplitFocusPolicy split_focus_policy = new SplitFocusPolicy();

//panels
    private final JPanel split_options_panel = new JPanel();
    private final JPanel destination_panel = new JPanel();
    private final JPanel output_options_panel = new JPanel();

//labels    
    private final JLabel source_file_label = new JLabel();
    private final JLabel split_options_label = new JLabel();
    private final JLabel dest_folder_label = new JLabel();
    private final JLabel output_options_label = new JLabel();
    private final JLabel out_prefix_label = new JLabel();
    
    private final ThreadGroup run_threads = new ThreadGroup("run threads");
   
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_NAME = "Split";
    private final String PLUGIN_VERSION = "0.3.0e";
    
/**
 * Constructor
 *
 */    
    public SplitMainGUI() {
        super();
        initialize();
    }

    private void initialize() {
    	setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	config = Configuration.getInstance();
        i18n_messages = config.getI18nResourceBundle();
        mc = config.getMainConsole();
        setPanelIcon("/images/split.png");
//        
        split_spring_layout = new SpringLayout();
        setLayout(split_spring_layout);
        source_text_field = new JTextField();
        source_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        add(source_text_field);
//FILE_CHOOSER
        browse_file_chooser.setFileFilter(new PdfFilter());
        browse_file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        browse_dest_file_chooser.setFileFilter(new DirFilter());
        browse_dest_file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//END_FILE_CHOOSER
        
        source_file_label.setText(GettextResource.gettext(i18n_messages,"Source file:"));
        add(source_file_label);

        browse_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
        browse_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
        browse_button.setMargin(new Insets(2, 2, 2, 2));
        browse_button.setToolTipText(GettextResource.gettext(i18n_messages,"Select a PDF file to split"));
        browse_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int return_val = browse_file_chooser.showOpenDialog(browse_button.getParent());
                File chosen_file = null;                
                if (return_val == JFileChooser.APPROVE_OPTION){
                    chosen_file = browse_file_chooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosen_file != null){
                    try{
                        source_text_field.setText(chosen_file.getAbsolutePath());
                    }
                    catch (Exception ex){
                        fireLogPropertyChanged("Error: "+ex.getMessage(), LogPanel.LOG_ERROR); 
                        
                    }
                }
                
            }
        });        
 
        add(browse_button);
//SPLIT_SECTION
        options_pane_layout = new SpringLayout();
        split_options_panel.setLayout(options_pane_layout);
        split_options_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(split_options_panel);

        burst_radio.setText(GettextResource.gettext(i18n_messages,"Burst (split into single pages)"));
        split_options_panel.add(burst_radio);

        every_n_radio.setText(GettextResource.gettext(i18n_messages,"Split every \"n\" pages"));
        split_options_panel.add(every_n_radio);
        
        n_pages_text_field = new JTextField();
        n_pages_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        n_pages_text_field.setEnabled(false);
        split_options_panel.add(n_pages_text_field);        
        
        even_radio.setText(GettextResource.gettext(i18n_messages,"Split even pages"));      
        split_options_panel.add(even_radio);
        
        odd_radio.setText(GettextResource.gettext(i18n_messages,"Split odd pages"));
        split_options_panel.add(odd_radio);
        
        this_page_radio.setText(GettextResource.gettext(i18n_messages,"Split after these pages"));
        split_options_panel.add(this_page_radio);
        
        this_page_text_field = new JTextField();
        this_page_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        this_page_text_field.setEnabled(false);
        split_options_panel.add(this_page_text_field);
        String helpText = 
        		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Split options")+"</b><ul>" +
        		"<li><b>"+GettextResource.gettext(i18n_messages,"Burst")+":</b> "+GettextResource.gettext(i18n_messages,"Explode the pdf document into single pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(i18n_messages,"Split every \"n\" pages")+":</b> "+GettextResource.gettext(i18n_messages,"Split the document every \"n\" pages")+".</li>" +
        		"<li><b>"+GettextResource.gettext(i18n_messages,"Split even pages")+":</b> "+GettextResource.gettext(i18n_messages,"Split the document every even page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(i18n_messages,"Split odd pages")+":</b> "+GettextResource.gettext(i18n_messages,"Split the document every odd page")+".</li>" +
        		"<li><b>"+GettextResource.gettext(i18n_messages,"Split after these pages")+":</b> "+GettextResource.gettext(i18n_messages,"Split the document after page numbers (num1-num2-num3..)")+".</li>" +
        		"</ul></body></html>";
        checks_help_label = new JHelpLabel(helpText, true);
        split_options_panel.add(checks_help_label);        
//END_SPLIT_SECTION
        
        
        split_options_label.setText(GettextResource.gettext(i18n_messages,"Split options:"));
        add(split_options_label);
//RADIO_LISTENERS
        /*This listeners enable or disable text field based on what you select*/
        RadioListener disable_all_listener = new RadioListener(this, n_pages_text_field, this_page_text_field, RadioListener.DISABLE_ALL);
        RadioListener disable_first_listener = new RadioListener(this, n_pages_text_field, this_page_text_field, RadioListener.DISABLE_FIRST);
        RadioListener disable_second_listener = new RadioListener(this, n_pages_text_field, this_page_text_field, RadioListener.DISABLE_SECOND);
        burst_radio.addActionListener(disable_all_listener);
        every_n_radio.addActionListener(disable_second_listener);
        even_radio.addActionListener(disable_all_listener);
        odd_radio.addActionListener(disable_all_listener);
        this_page_radio.addActionListener(disable_first_listener);
//END_RADIO_LISTENERS 
//DESTINATION_PANEL
        destination_panel_layout = new SpringLayout();
        destination_panel.setLayout(destination_panel_layout);
        destination_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destination_panel);
//END_DESTINATION_PANEL        
//DESTINATION_RADIOS
        
        dest_folder_label.setText(GettextResource.gettext(i18n_messages,"Destination folder:"));
        add(dest_folder_label);

        same_as_source_radio.setText(GettextResource.gettext(i18n_messages,"Same as source"));
        destination_panel.add(same_as_source_radio);

        choose_a_folder_radio.setSelected(true);
        choose_a_folder_radio.setText(GettextResource.gettext(i18n_messages,"Choose a folder"));
        destination_panel.add(choose_a_folder_radio);
//END_DESTINATION_RADIOS        
//RADIOGROUP
        split_options_radio_group = new ButtonGroup();
        split_options_radio_group.add(burst_radio);
        split_options_radio_group.add(every_n_radio);
        split_options_radio_group.add(even_radio);
        split_options_radio_group.add(odd_radio);
        split_options_radio_group.add(this_page_radio);
        final ButtonGroup output_radio_group = new ButtonGroup();
        output_radio_group.add(same_as_source_radio);
        output_radio_group.add(choose_a_folder_radio);  
//END_RADIOGROUP

        dest_folder_text = new JTextField();
        dest_folder_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destination_panel.add(dest_folder_text);
//CHECK_BOX
        overwrite_checkbox.setText(GettextResource.gettext(i18n_messages,"Overwrite if already exists"));
        overwrite_checkbox.setSelected(true);
        destination_panel.add(overwrite_checkbox);
        
        output_compressed_check.setText(GettextResource.gettext(i18n_messages,"Compress output file"));
        output_compressed_check.setSelected(true);
        destination_panel.add(output_compressed_check);
        
//END_CHECK_BOX 
        browse_dest_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int return_val = browse_dest_file_chooser.showOpenDialog(browse_dest_button.getParent());
                File chosen_file = null;                
                if (return_val == JFileChooser.APPROVE_OPTION){
                    chosen_file = browse_dest_file_chooser.getSelectedFile();
                }
                //write the destination in text field
                if (chosen_file != null){
                    try{
                        dest_folder_text.setText(chosen_file.getAbsolutePath());
                    }
                    catch (Exception ex){
                        fireLogPropertyChanged("Error: "+ex.getMessage(), LogPanel.LOG_ERROR); 
                        
                    }
                }
                
            }
        });
        browse_dest_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
        browse_dest_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
        browse_dest_button.setMargin(new Insets(2, 2, 2, 2));
        destination_panel.add(browse_dest_button);
//HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(i18n_messages,"Use the same output folder as the input file or choose a folder.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want compressed output files.")+"</p>"+
    		"</body></html>";
	    destination_help_label = new JHelpLabel(helpTextDest, true);
	    destination_panel.add(destination_help_label);
//END_HELP_LABEL_DESTINATION  
	    
        output_options_label.setText(GettextResource.gettext(i18n_messages,"Output options:"));
        add(output_options_label);
//S_PANEL
        output_options_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        s_panel_layout = new SpringLayout();
        output_options_panel.setLayout(s_panel_layout);
        add(output_options_panel);

        out_prefix_label.setText(GettextResource.gettext(i18n_messages,"Output file names prefix:"));
        output_options_panel.add(out_prefix_label);

        out_prefix_text = new JTextField();
        out_prefix_text.setText("pdfsam_");
        out_prefix_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        output_options_panel.add(out_prefix_text);
//END_S_PANEL
//HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(i18n_messages,"If it contains \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(i18n_messages,"Ex. prefix_[BASENAME]_[CURRENTPAGE] generates prefix_FileName_005.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(i18n_messages,"If it doesn't contain \"[CURRENTPAGE]\" or \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(i18n_messages,"Available variables: [CURRENTPAGE], [TIMESTAMP], [BASENAME].")+"</p>"+
    		"</body></html>";
	    prefix_help_label = new JHelpLabel(helpTextPrefix, true);
	    output_options_panel.add(prefix_help_label);
//END_HELP_LABEL_PREFIX
//RUN_BUTTON
        //listener
        run_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (run_threads.activeCount() > 0){
                    fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Please wait while all files are processed..") , LogPanel.LOG_INFO);
                    return;
                }
                final LinkedList args = new LinkedList();
                args.add("-f");
                //validation and permission check are demanded to the CmdParser object
                try{
                    args.add(source_text_field.getText());
                    args.add("-p");
                    args.add(out_prefix_text.getText());
                    args.add("-s");
                    args.add(split_type);
                    //check if is needed page option
                    if (split_type.equals(CmdParser.S_SPLIT)){
                        args.add("-n");
                        args.add(this_page_text_field.getText());                        
                    }else{
                        if (split_type.equals(CmdParser.S_NSPLIT)){
                            args.add("-n");
                            args.add(n_pages_text_field.getText());                        
                        }                        
                    }                    
                    args.add("-o");
                    //check radio for output options
                    if (same_as_source_radio.isSelected()){
                        File source_file = new File(source_text_field.getText());
                        args.add(source_file.getParent());
                    }else{
                        args.add(dest_folder_text.getText());
                    }
                    if (overwrite_checkbox.isSelected()) args.add("-overwrite");
                    if (output_compressed_check.isSelected()) args.add("-compressed"); 
                    args.add("split"); 
                }catch(Exception any_ex){    
                    fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
                    //any_ex.printStackTrace();
                }      
                    //cast array
                    Object[] myObjectArray = args.toArray();
                    final String[] myStringArray = new String[myObjectArray.length];
                    for (int i = 0; i < myStringArray.length; i++) {
                        myStringArray[i] = myObjectArray[i].toString();
                    }
                    //run split in its own thread              
                    final Thread run_thread = new Thread(run_threads, "run") {
                         public void run() {
                          try{
                              String out_msg = mc.mainAction(myStringArray, true);
                              fireLogPropertyChanged("Command Line: "+args.toString() , LogPanel.LOG_DETAILEDINFO);
                              fireLogPropertyChanged(out_msg , LogPanel.LOG_INFO);
                          }catch(Exception any_ex){    
                              fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
                              //any_ex.printStackTrace();
                          }                       
                     }
                    };
                    run_thread.start();   
            }
        });
        run_button.setMargin(new Insets(2, 2, 2, 2));
        run_button.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
        run_button.setText(GettextResource.gettext(i18n_messages,"Run"));
        run_button.setToolTipText(GettextResource.gettext(i18n_messages,"Split selected file"));
        add(run_button);
//END_RUN_BUTTON
        
//RADIO_LISTENERS
        same_as_source_radio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dest_folder_text.setEnabled(false);
                browse_dest_button.setEnabled(false);
            }
        });
        
        choose_a_folder_radio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dest_folder_text.setEnabled(true);
                browse_dest_button.setEnabled(true);
            }
        });
//END_RADIO_LISTENERS
//ENTER_KEY_LISTENERS
        browse_button.addKeyListener(browse_enterkey_listener);
        browse_dest_button.addKeyListener(browsed_enterkey_listener);
        run_button.addKeyListener(run_enterkey_listener);
        out_prefix_text.addKeyListener(run_enterkey_listener);
//END_ENTER_KEY_LISTENERS
        setLayout();
    }

 
    /**
     * @return Returns the Plugin author.
     */
    public String getPluginAuthor() {
        return PLUGIN_AUTHOR;
    }


    /**
     * @return Returns the Plugin name.
     */
    public String getPluginName() {
        return PLUGIN_NAME;
    }

	   /**
     * @return Returns the version.
     */
    public String getVersion() {
        return PLUGIN_VERSION;
    }
	
    public Node getJobNode(Node arg0) throws SaveJobException {
		try{
			if (arg0 != null){
				Element file_source = ((Element)arg0).addElement("source");
				file_source.addAttribute("value", source_text_field.getText());
				
				Element split_option = ((Element)arg0).addElement("split_option");
				if(split_options_radio_group.getSelection() != null){
					split_option.addAttribute("value",split_options_radio_group.getSelection().getActionCommand());
				}
				Element split_npages = ((Element)arg0).addElement("npages");
				split_npages.addAttribute("value", n_pages_text_field.getText());			
			
				Element split_thispage = ((Element)arg0).addElement("thispage");
				split_thispage.addAttribute("value", this_page_text_field.getText());			

				Element file_destination = ((Element)arg0).addElement("destination");
				file_destination.addAttribute("value", dest_folder_text.getText());			
				
				Element file_prefix = ((Element)arg0).addElement("prefix");
				file_prefix.addAttribute("value", out_prefix_text.getText());	
				
				Element file_overwrite = ((Element)arg0).addElement("overwrite");
				file_overwrite.addAttribute("value", overwrite_checkbox.isSelected()?"true":"false");
			}
			return arg0;
		}
		catch (Exception ex){
            throw new SaveJobException(ex.getMessage(), ex);                     
        }
	}

    public void loadJobNode(Node arg0) throws LoadJobException {		
			try{	
				Node file_source = (Node) arg0.selectSingleNode("source/@value");
				if (file_source != null){
					source_text_field.setText(file_source.getText());
				}

				Node split_option = (Node) arg0.selectSingleNode("split_option/@value");
				if (split_option != null){
					if(split_option.getText().equals(burst_radio.getSplitCommand()))  burst_radio.doClick();
					else if(split_option.getText().equals(every_n_radio.getSplitCommand()))  every_n_radio.doClick();
					else if(split_option.getText().equals(even_radio.getSplitCommand()))  even_radio.doClick();
					else if(split_option.getText().equals(odd_radio.getSplitCommand()))  odd_radio.doClick();
					else if(split_option.getText().equals(this_page_radio.getSplitCommand()))  this_page_radio.doClick();
				}
				Node split_npages = (Node) arg0.selectSingleNode("npages/@value");
				if (split_npages != null){
					n_pages_text_field.setText(split_npages.getText());
				}

				Node split_thispage = (Node) arg0.selectSingleNode("thispage/@value");
				if (split_thispage != null){
					this_page_text_field.setText(split_thispage.getText());
				}

				Node file_destination = (Node) arg0.selectSingleNode("destination/@value");
				if (file_destination != null){
					dest_folder_text.setText(file_destination.getText());
					choose_a_folder_radio.doClick();
				}else{
					same_as_source_radio.doClick();
				}

				Node file_overwrite = (Node) arg0.selectSingleNode("overwrite/@value");
				if (file_overwrite != null){
					overwrite_checkbox.setSelected(file_overwrite.getText().equals("true"));
				}

				Node file_prefix = (Node) arg0.selectSingleNode("prefix/@value");
				if (file_prefix != null){
					out_prefix_text.setText(file_prefix.getText());
				}
                fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Split section loaded."), LogPanel.LOG_INFO);                     
            }
			catch (Exception ex){
                fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR);                     
			}			 				
	}
	  
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
//      LAYOUT
        split_spring_layout.putConstraint(SpringLayout.SOUTH, source_text_field, 40, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.EAST, source_text_field, -120, SpringLayout.EAST, this);
        split_spring_layout.putConstraint(SpringLayout.NORTH, source_text_field, 20, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.WEST, source_text_field, 5, SpringLayout.WEST, this);
        split_spring_layout.putConstraint(SpringLayout.WEST, source_file_label, 0, SpringLayout.WEST, source_text_field);
        split_spring_layout.putConstraint(SpringLayout.NORTH, source_file_label, 5, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
        split_spring_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, source_text_field);
        split_spring_layout.putConstraint(SpringLayout.EAST, browse_button, -20, SpringLayout.EAST, this);        
        split_spring_layout.putConstraint(SpringLayout.WEST, browse_button, -88, SpringLayout.EAST, browse_button);
        split_spring_layout.putConstraint(SpringLayout.SOUTH, split_options_panel, 185, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.EAST, split_options_panel, -5, SpringLayout.EAST, this);
        split_spring_layout.putConstraint(SpringLayout.NORTH, split_options_panel, 70, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.WEST, split_options_panel, 0, SpringLayout.WEST, source_text_field);
        split_spring_layout.putConstraint(SpringLayout.SOUTH, split_options_label, 0, SpringLayout.NORTH, split_options_panel);
        split_spring_layout.putConstraint(SpringLayout.NORTH, split_options_label, 5, SpringLayout.SOUTH, browse_button);
        split_spring_layout.putConstraint(SpringLayout.WEST, split_options_label, 0, SpringLayout.WEST, source_text_field);
        split_spring_layout.putConstraint(SpringLayout.NORTH, dest_folder_label, 5, SpringLayout.SOUTH, split_options_panel);
        split_spring_layout.putConstraint(SpringLayout.WEST, dest_folder_label, 0, SpringLayout.WEST, split_options_panel);
        split_spring_layout.putConstraint(SpringLayout.SOUTH, destination_panel, 305, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.EAST, destination_panel, 0, SpringLayout.EAST, split_options_panel);
        split_spring_layout.putConstraint(SpringLayout.NORTH, destination_panel, 205, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.WEST, destination_panel, 0, SpringLayout.WEST, split_options_panel);
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, same_as_source_radio, 25, SpringLayout.NORTH, same_as_source_radio);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, same_as_source_radio, 1, SpringLayout.NORTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.WEST, same_as_source_radio, 10, SpringLayout.WEST, destination_panel);        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, choose_a_folder_radio, 0, SpringLayout.SOUTH, same_as_source_radio);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, choose_a_folder_radio, 0, SpringLayout.NORTH, same_as_source_radio);
        destination_panel_layout.putConstraint(SpringLayout.WEST, choose_a_folder_radio, 20, SpringLayout.EAST, same_as_source_radio);
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, dest_folder_text, 50, SpringLayout.NORTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, dest_folder_text, 30, SpringLayout.NORTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.EAST, dest_folder_text, -105, SpringLayout.EAST, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.WEST, dest_folder_text, 5, SpringLayout.WEST, destination_panel);
        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, overwrite_checkbox, 15, SpringLayout.NORTH, overwrite_checkbox);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, overwrite_checkbox, 5, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.WEST, overwrite_checkbox, 0, SpringLayout.WEST, dest_folder_text);
        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, output_compressed_check, 15, SpringLayout.NORTH, output_compressed_check);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, output_compressed_check, 5, SpringLayout.SOUTH, overwrite_checkbox);
        destination_panel_layout.putConstraint(SpringLayout.WEST, output_compressed_check, 0, SpringLayout.WEST, dest_folder_text);

        destination_panel_layout.putConstraint(SpringLayout.SOUTH, browse_dest_button, 0, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.EAST, browse_dest_button, -10, SpringLayout.EAST, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, browse_dest_button, -25, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.WEST, browse_dest_button, -98, SpringLayout.EAST, destination_panel);
        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_help_label, -1, SpringLayout.SOUTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.EAST, destination_help_label, -1, SpringLayout.EAST, destination_panel);
        
        split_spring_layout.putConstraint(SpringLayout.EAST, output_options_label, 0, SpringLayout.EAST, destination_panel);
        split_spring_layout.putConstraint(SpringLayout.WEST, output_options_label, 0, SpringLayout.WEST, destination_panel);
        split_spring_layout.putConstraint(SpringLayout.NORTH, output_options_label, 5, SpringLayout.SOUTH, destination_panel);
		
        split_spring_layout.putConstraint(SpringLayout.SOUTH, output_options_panel, 390, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.EAST, output_options_panel, 0, SpringLayout.EAST, destination_panel);
        split_spring_layout.putConstraint(SpringLayout.NORTH, output_options_panel, 0, SpringLayout.SOUTH, output_options_label);
        split_spring_layout.putConstraint(SpringLayout.WEST, output_options_panel, 0, SpringLayout.WEST, output_options_label);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, out_prefix_label, 25, SpringLayout.NORTH, output_options_panel);
        s_panel_layout.putConstraint(SpringLayout.NORTH, out_prefix_label, 5, SpringLayout.NORTH, output_options_panel);
        s_panel_layout.putConstraint(SpringLayout.WEST, out_prefix_label, 5, SpringLayout.WEST, output_options_panel);
        s_panel_layout.putConstraint(SpringLayout.EAST, out_prefix_text, -10, SpringLayout.EAST, output_options_panel);
        s_panel_layout.putConstraint(SpringLayout.SOUTH, out_prefix_text, 0, SpringLayout.SOUTH, out_prefix_label);
        s_panel_layout.putConstraint(SpringLayout.NORTH, out_prefix_text, 0, SpringLayout.NORTH, out_prefix_label);
        s_panel_layout.putConstraint(SpringLayout.WEST, out_prefix_text, 15, SpringLayout.EAST, out_prefix_label);

        s_panel_layout.putConstraint(SpringLayout.SOUTH, prefix_help_label, -1, SpringLayout.SOUTH, output_options_panel);
        s_panel_layout.putConstraint(SpringLayout.EAST, prefix_help_label, -1, SpringLayout.EAST, output_options_panel);
        
        split_spring_layout.putConstraint(SpringLayout.SOUTH, run_button, 425, SpringLayout.NORTH, this);
        split_spring_layout.putConstraint(SpringLayout.EAST, run_button, 0, SpringLayout.EAST, output_options_panel);
        split_spring_layout.putConstraint(SpringLayout.WEST, run_button, 0, SpringLayout.WEST, browse_button);
        split_spring_layout.putConstraint(SpringLayout.NORTH, run_button, 400, SpringLayout.NORTH, this);

//      RADIO_LAYOUT
        options_pane_layout.putConstraint(SpringLayout.NORTH, burst_radio, 10, SpringLayout.NORTH, split_options_panel);
        options_pane_layout.putConstraint(SpringLayout.WEST, burst_radio, 10, SpringLayout.WEST, split_options_panel);
        options_pane_layout.putConstraint(SpringLayout.NORTH, every_n_radio, 0, SpringLayout.SOUTH, burst_radio);
        options_pane_layout.putConstraint(SpringLayout.WEST, every_n_radio, 0, SpringLayout.WEST, burst_radio);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, n_pages_text_field, 0, SpringLayout.SOUTH, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.EAST, n_pages_text_field, 45, SpringLayout.EAST, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.NORTH, n_pages_text_field, 0, SpringLayout.NORTH, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.WEST, n_pages_text_field, 5, SpringLayout.EAST, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.EAST, even_radio, 0, SpringLayout.EAST, n_pages_text_field);
        options_pane_layout.putConstraint(SpringLayout.WEST, even_radio, 0, SpringLayout.WEST, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.NORTH, even_radio, 0, SpringLayout.SOUTH, every_n_radio);
        options_pane_layout.putConstraint(SpringLayout.EAST, odd_radio, 0, SpringLayout.EAST, even_radio);
        options_pane_layout.putConstraint(SpringLayout.WEST, odd_radio, 0, SpringLayout.WEST, even_radio);
        options_pane_layout.putConstraint(SpringLayout.NORTH, odd_radio, 0, SpringLayout.SOUTH, even_radio);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, this_page_radio, 0, SpringLayout.NORTH, n_pages_text_field);
        options_pane_layout.putConstraint(SpringLayout.WEST, this_page_radio, 20, SpringLayout.EAST, burst_radio);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, this_page_text_field, 0, SpringLayout.SOUTH, this_page_radio);
        options_pane_layout.putConstraint(SpringLayout.EAST, this_page_text_field, 75, SpringLayout.EAST, this_page_radio);
        options_pane_layout.putConstraint(SpringLayout.NORTH, this_page_text_field, 0, SpringLayout.NORTH, this_page_radio);
        options_pane_layout.putConstraint(SpringLayout.WEST, this_page_text_field, 5, SpringLayout.EAST, this_page_radio);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, checks_help_label, -1, SpringLayout.SOUTH, split_options_panel);
        options_pane_layout.putConstraint(SpringLayout.EAST, checks_help_label, -1, SpringLayout.EAST, split_options_panel);         
    }

    /**
     * @param split_type The split_type to set.
     */
    public void setSplitType(String split_type) {
        this.split_type = split_type;
    }
    
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)split_focus_policy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for split panel
     *
     */
    public class SplitFocusPolicy extends FocusTraversalPolicy {
        public SplitFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(source_text_field)){
                return browse_button;
            }
            else if (aComponent.equals(browse_button)){
                return burst_radio;
            }
            else if (aComponent.equals(burst_radio)){
                return every_n_radio;
            }
            else if (aComponent.equals(every_n_radio)){
                if (n_pages_text_field.isEnabled()){
                    return n_pages_text_field;
                }else{
                    return even_radio;
                }
            }        
            else if (aComponent.equals(n_pages_text_field)){
                return even_radio;
            }
            else if (aComponent.equals(even_radio)){
                return odd_radio;
            }
            else if (aComponent.equals(odd_radio)){
                return this_page_radio;
            }
            else if (aComponent.equals(this_page_radio)){
                if (this_page_text_field.isEnabled()){
                    return this_page_text_field;
                }else{
                    return same_as_source_radio;
                }
            }
            else if (aComponent.equals(this_page_text_field)){
                return same_as_source_radio;
            }
            else if (aComponent.equals(same_as_source_radio)){
                return choose_a_folder_radio;
            }
            else if (aComponent.equals(choose_a_folder_radio)){
                if (dest_folder_text.isEnabled()){
                    return dest_folder_text;
                }else{
                    return out_prefix_text;
                }                
            }
            else if (aComponent.equals(dest_folder_text)){
                return browse_dest_button;
            }
            else if (aComponent.equals(browse_dest_button)){
                return overwrite_checkbox;
            }
            else if (aComponent.equals(overwrite_checkbox)){
                return out_prefix_text;
            }
            else if (aComponent.equals(out_prefix_text)){
                return run_button;
            }
            else if (aComponent.equals(run_button)){
                return source_text_field;
            }
            return source_text_field;
        }
        
        public Component getComponentBefore(Container CycleRootComp, Component aComponent){
            
            if (aComponent.equals(source_text_field)){
                return run_button;
            }
            else if (aComponent.equals(run_button)){
                return out_prefix_text;
            }
            else if (aComponent.equals(out_prefix_text)){
                return overwrite_checkbox;
            }
            else if (aComponent.equals(overwrite_checkbox)){
                if (browse_dest_button.isEnabled()){
                    return browse_dest_button;
                }else{
                    return choose_a_folder_radio;
                }                
            }
            else if (aComponent.equals(browse_dest_button)){
                return dest_folder_text;
            }
            else if (aComponent.equals(dest_folder_text)){
                return choose_a_folder_radio;
            }
            else if (aComponent.equals(choose_a_folder_radio)){
                return same_as_source_radio;
            }        
            else if (aComponent.equals(same_as_source_radio)){
                if (this_page_text_field.isEnabled()){
                    return this_page_text_field;
                }else{
                    return this_page_radio;
                }
            }
            else if (aComponent.equals(this_page_text_field)){
                return this_page_radio;
            }
            else if (aComponent.equals(this_page_radio)){
                return odd_radio;
            }
            else if (aComponent.equals(odd_radio)){
                return even_radio;
            }
            else if (aComponent.equals(even_radio)){
                if (n_pages_text_field.isEnabled()){
                    return n_pages_text_field;
                }else{
                    return every_n_radio;
                }
            }
            else if (aComponent.equals(n_pages_text_field)){
                return every_n_radio;
            }
            else if (aComponent.equals(every_n_radio)){
                return burst_radio;
            }
            else if (aComponent.equals(burst_radio)){
                return browse_button;
            }
            else if (aComponent.equals(browse_button)){
                return source_text_field;
            }
            return source_text_field;
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return source_text_field;
        }

        public Component getLastComponent(Container CycleRootComp){
            return run_button;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return source_text_field;
        }
    }
 
   
}
