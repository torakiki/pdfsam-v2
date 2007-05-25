/*
 * Created on 14-Dec-2006
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
package it.pdfsam.plugin.encrypt.GUI;

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.MainConsole;
import it.pdfsam.console.tools.CmdParser;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnterDoClickListener;
import it.pdfsam.panels.LogPanel;
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
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
 * Plugable JPanel provides a GUI for encrypt functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class EncryptMainGUI extends AbstractPlugIn{
    
	private static final long serialVersionUID = -9119811050006714263L;
	
	private JTextField out_prefix_text;
    private SpringLayout e_panel_layout;
    private SpringLayout destination_panel_layout;
    private JTextField dest_folder_text;
    private SpringLayout options_pane_layout;
    private JTextField source_text_field;
    private SpringLayout encrypt_spring_layout;
    private ResourceBundle i18n_messages;
    private JTextField user_pwd_field;
	private JTextField owner_pwd_field; 
	private JComboBox encrypt_type;
    private JHelpLabel prefix_help_label;
    private JHelpLabel destination_help_label; 	
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
    
//encrypt_check
	private final JCheckBox[] permissions_check = new JCheckBox[8];
    private final JCheckBox allowall_check = new JCheckBox();
    private final JCheckBox overwrite_checkbox = new JCheckBox();
//radio
    private final JRadioButton same_as_source_radio = new JRadioButton();
    private final JRadioButton choose_a_folder_radio = new JRadioButton();
//focus policy 
    private final EncryptFocusPolicy encrypt_focus_policy = new EncryptFocusPolicy();

//panels
    private final JPanel encrypt_options_panel = new JPanel();
    private final JPanel destination_panel = new JPanel();
    private final JPanel output_options_panel = new JPanel();

//labels    
    final JLabel source_file_label = new JLabel();
    final JLabel encrypt_options_label = new JLabel();
    final JLabel dest_folder_label = new JLabel();
    final JLabel output_options_label = new JLabel();
    final JLabel out_prefix_label = new JLabel();
    final JLabel owner_pwd_label = new JLabel();
    final JLabel user_pwd_label = new JLabel();
    final JLabel encrypt_type_label = new JLabel();
    
    private final ThreadGroup run_threads = new ThreadGroup("run threads");
   
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";    
    private final String PLUGIN_NAME = "Encrypt";
    private final String PLUGIN_VERSION = "0.1.5e";
	
	private final static String RC4_40 = "RC4-40b";
	private final static String RC4_128 = "RC4-128b";
	private final static String AES_128 = "AES-128b";
	
	private final static int DPRINT = 0;
	private final static int PRINT = 1;
	private final static int COPY = 2;
	private final static int MODIFY = 3;
	private final static int FILL = 4;
	private final static int SCREEN = 5;
	private final static int ASSEMBLY = 6;
	private final static int ANNOTATION = 7;
    
/**
 * Constructor
 *
 */    
    public EncryptMainGUI() {
        super();
        initialize();
        
    }

    private void initialize() {
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
    	config = Configuration.getInstance();
        i18n_messages = config.getI18nResourceBundle();
        mc = config.getMainConsole();
        setPanelIcon("/images/encrypt.png");
//        
        encrypt_spring_layout = new SpringLayout();
        setLayout(encrypt_spring_layout);
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
        browse_button.setToolTipText(GettextResource.gettext(i18n_messages,"Select a PDF file to encrypt"));
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
//ENCRYPT_SECTION
        options_pane_layout = new SpringLayout();
        encrypt_options_panel.setLayout(options_pane_layout);
        encrypt_options_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(encrypt_options_panel);

		owner_pwd_label.setText(GettextResource.gettext(i18n_messages,"Owner password:"));
		encrypt_options_panel.add(owner_pwd_label);
		
        owner_pwd_field = new JTextField();
        owner_pwd_field.setToolTipText(GettextResource.gettext(i18n_messages,"Owner password (Max 32 chars long)"));
        owner_pwd_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        encrypt_options_panel.add(owner_pwd_field);

		user_pwd_label.setText(GettextResource.gettext(i18n_messages,"User password:"));
		encrypt_options_panel.add(user_pwd_label);

        user_pwd_field = new JTextField();
        user_pwd_field.setToolTipText(GettextResource.gettext(i18n_messages,"User password (Max 32 chars long)"));
        user_pwd_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        encrypt_options_panel.add(user_pwd_field);

        encrypt_type_label.setText(GettextResource.gettext(i18n_messages,"Encryption angorithm:"));
		encrypt_options_panel.add(encrypt_type_label);		

        String[] eTypes = {EncryptMainGUI.RC4_40, EncryptMainGUI.RC4_128, EncryptMainGUI.AES_128};
        encrypt_type = new JComboBox(eTypes);
		encrypt_options_panel.add(encrypt_type);		

        permissions_check[EncryptMainGUI.PRINT] = new JCheckBox(GettextResource.gettext(i18n_messages,"Print"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.PRINT]);

        permissions_check[EncryptMainGUI.DPRINT] = new JCheckBox(GettextResource.gettext(i18n_messages,"Low quality print"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.DPRINT]);

        permissions_check[EncryptMainGUI.COPY] = new JCheckBox(GettextResource.gettext(i18n_messages,"Copy or extract"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.COPY]);

        permissions_check[EncryptMainGUI.MODIFY] = new JCheckBox(GettextResource.gettext(i18n_messages,"Modify"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.MODIFY]);

        permissions_check[EncryptMainGUI.ANNOTATION] = new JCheckBox(GettextResource.gettext(i18n_messages,"Add or modify text annotations"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.ANNOTATION]);

        permissions_check[EncryptMainGUI.FILL] = new JCheckBox(GettextResource.gettext(i18n_messages,"Fill form fields"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.FILL]);

        permissions_check[EncryptMainGUI.SCREEN] = new JCheckBox(GettextResource.gettext(i18n_messages,"Extract for use by accessibility dev."));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.SCREEN]);

        permissions_check[EncryptMainGUI.ASSEMBLY] = new JCheckBox(GettextResource.gettext(i18n_messages,"Manipulate pages and add bookmarks"));
        encrypt_options_panel.add(permissions_check[EncryptMainGUI.ASSEMBLY]);

        allowall_check.setText(GettextResource.gettext(i18n_messages,"Allow all"));
        encrypt_options_panel.add(allowall_check);
//END_ENCRYPT_SECTION
        encrypt_options_label.setText(GettextResource.gettext(i18n_messages,"Encrypt options:"));
        add(encrypt_options_label);

//UNSELECT_OTHERS_LISTENER
		allowall_check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
					if(allowall_check.isSelected()){
						for(int i=0; i<permissions_check.length; i++){
							permissions_check[i].setEnabled(false);
						}
					}else{					
						String encType = (String)encrypt_type.getSelectedItem();
	        			if(encType.equals(EncryptMainGUI.RC4_40)){
	        		        permissions_check[EncryptMainGUI.PRINT].setEnabled(true);
	        		        permissions_check[EncryptMainGUI.DPRINT].setEnabled(false);
	        		        permissions_check[EncryptMainGUI.COPY].setEnabled(true);
	        		        permissions_check[EncryptMainGUI.MODIFY].setEnabled(true);
	        		        permissions_check[EncryptMainGUI.ANNOTATION].setEnabled(true);
	        		        permissions_check[EncryptMainGUI.FILL].setEnabled(false);
	        		        permissions_check[EncryptMainGUI.SCREEN].setEnabled(false);
	        		        permissions_check[EncryptMainGUI.ASSEMBLY].setEnabled(false);
	        			}else{
							for(int i=0; i<permissions_check.length; i++){
								permissions_check[i].setEnabled(true);
							}
						}
					}
			}
        });
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
//CHECKGROUP
        final ButtonGroup output_radio_group = new ButtonGroup();
        output_radio_group.add(same_as_source_radio);
        output_radio_group.add(choose_a_folder_radio);  
//END_CHECKGROUP

        dest_folder_text = new JTextField();
        dest_folder_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        destination_panel.add(dest_folder_text);

//CHECK_BOX
        overwrite_checkbox.setText(GettextResource.gettext(i18n_messages,"Overwrite if already exists"));
        overwrite_checkbox.setSelected(true);
        destination_panel.add(overwrite_checkbox);
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
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Destination output directory")+"</b>" +
    		"<p>"+GettextResource.gettext(i18n_messages,"Use the same output folder as the input file or choose a fodler.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"To choose a folder browse or enter the full path to the destination output directory.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want to overwrite the output files if they already exist.")+"</p>"+
    		"</body></html>";
	    destination_help_label = new JHelpLabel(helpTextDest, true);
	    destination_panel.add(destination_help_label);
//END_HELP_LABEL_DESTINATION         
        output_options_label.setText(GettextResource.gettext(i18n_messages,"Output options:"));
        add(output_options_label);
        
        encrypt_type.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {	 
        		if(!allowall_check.isSelected()){
        			String encType = (String)encrypt_type.getSelectedItem();
        			if(encType.equals(EncryptMainGUI.RC4_40)){
        		        permissions_check[EncryptMainGUI.PRINT].setEnabled(true);
        		        permissions_check[EncryptMainGUI.DPRINT].setEnabled(false);
        		        permissions_check[EncryptMainGUI.COPY].setEnabled(true);
        		        permissions_check[EncryptMainGUI.MODIFY].setEnabled(true);
        		        permissions_check[EncryptMainGUI.ANNOTATION].setEnabled(true);
        		        permissions_check[EncryptMainGUI.FILL].setEnabled(false);
        		        permissions_check[EncryptMainGUI.SCREEN].setEnabled(false);
        		        permissions_check[EncryptMainGUI.ASSEMBLY].setEnabled(false);
        			}else{
						for(int i=0; i<permissions_check.length; i++){
							permissions_check[i].setEnabled(true);
						}
					}
				}
        	}
        });
        encrypt_type.setSelectedIndex(1);
//S_PANEL
        output_options_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        e_panel_layout = new SpringLayout();
        output_options_panel.setLayout(e_panel_layout);
        add(output_options_panel);

        out_prefix_label.setText(GettextResource.gettext(i18n_messages,"Output file names prefix:"));
        output_options_panel.add(out_prefix_label);

        out_prefix_text = new JTextField();
        out_prefix_text.setText("pdfsam_");
        out_prefix_text.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        output_options_panel.add(out_prefix_text);
//END_S_PANEL
//      HELP_LABEL_PREFIX       
        String helpTextPrefix = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Output files prefix")+"</b>" +
    		"<p> "+GettextResource.gettext(i18n_messages,"If it contains \"[TIMESTAMP]\" it performs variable substitution.")+"</p>"+
    		"<p> "+GettextResource.gettext(i18n_messages,"Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(i18n_messages,"If it doesn't contain \"[TIMESTAMP]\" it generates oldstyle output file names.")+"</p>"+
    		"<br><p> "+GettextResource.gettext(i18n_messages,"Available variables: [TIMESTAMP], [BASENAME].")+"</p>"+
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
                //validation and permission check are demanded to the CmdParser object
                try{
					args.addAll(getEncPermissions(permissions_check, allowall_check));
					args.add("-f");
                    args.add(source_text_field.getText());
                    args.add("-p");
                    args.add(out_prefix_text.getText());
                    args.add("-apwd");
                    args.add(owner_pwd_field.getText());
                    args.add("-upwd");
                    args.add(user_pwd_field.getText());
                    //check if is needed page option
					args.add("-etype");
					args.add(getEncAlg((String)encrypt_type.getSelectedItem()));
                    args.add("-o");
                    //check radio for output options
                    if (same_as_source_radio.isSelected()){
                        File source_file = new File(source_text_field.getText());
                        args.add(source_file.getParent());
                    }else{
                        args.add(dest_folder_text.getText());
                    }
                    if (overwrite_checkbox.isSelected()) args.add("-overwrite");
                    args.add("encrypt"); 
                }catch(Exception any_ex){    
                    fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
                }      
                    //cast array
                    Object[] myObjectArray = args.toArray();
                    final String[] myStringArray = new String[myObjectArray.length];
                    for (int i = 0; i < myStringArray.length; i++) {
                        myStringArray[i] = myObjectArray[i].toString();
                    }
                    //run concat in its own thread              
                    final Thread run_thread = new Thread(run_threads, "run") {
                         public void run() {
                          try{
                              String out_msg = mc.mainAction(myStringArray, true);
                              fireLogPropertyChanged("Command Line: "+args.toString() , LogPanel.LOG_DETAILEDINFO);
                              fireLogPropertyChanged(out_msg , LogPanel.LOG_INFO);
                          }catch(Exception any_ex){    
                              fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
                          }
                     }
                    };
                    run_thread.start();   
            }
        });
        run_button.setMargin(new Insets(2, 2, 2, 2));
        run_button.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
        run_button.setText(GettextResource.gettext(i18n_messages,"Run"));
        run_button.setToolTipText(GettextResource.gettext(i18n_messages,"Encrypt selected file"));
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
				
				Element allowall = ((Element)arg0).addElement("allowall");
				if(allowall_check.isSelected()){
					allowall.addAttribute("value","true");
				}
				else{
					Element permissions = ((Element)arg0).addElement("permissions");
					for (int i=0; i<=EncryptMainGUI.ANNOTATION; i++){
						if(permissions_check[i].isSelected()){
							Element enabled = permissions.addElement("enabled");
							enabled.addAttribute("value", Integer.toString(i));
						}
					}
				}
				
				Element owner_pwd = ((Element)arg0).addElement("ownerpwd");
				owner_pwd.addAttribute("value", owner_pwd_field.getText());			

				Element enc_type = ((Element)arg0).addElement("enctype");
				enc_type.addAttribute("value", (String)encrypt_type.getSelectedItem());			
				
				Element user_pwd = ((Element)arg0).addElement("usrpwd");
				user_pwd.addAttribute("value", user_pwd_field.getText());			

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

				Node allow_all = (Node) arg0.selectSingleNode("allowall/@value");
				if(allow_all != null && allow_all.getText().equals("true")){
					allowall_check.doClick();
				}else{
					Node permissions = (Node) arg0.selectSingleNode("permissions");
					if (permissions != null) {
						List listEnab = permissions.selectNodes("enabled");
						for(int j=0; listEnab!= null && j<listEnab.size(); j++){
							Node enabledNode = (Node) listEnab.get(j);
							if(enabledNode != null){
								permissions_check[Integer.parseInt(enabledNode.selectSingleNode("@value").getText())].doClick();
							}
						}
					}
				}				
				Node enc_type = (Node) arg0.selectSingleNode("enctype/@value");
				if (enc_type != null){
					encrypt_type.setSelectedItem((String)enc_type.getText());
				}

				Node user_pwd = (Node) arg0.selectSingleNode("usrpwd/@value");
				if (user_pwd != null){
					user_pwd_field.setText(user_pwd.getText());
				}

				Node owner_pwd = (Node) arg0.selectSingleNode("ownerpwd/@value");
				if (owner_pwd != null){
					owner_pwd_field.setText(owner_pwd.getText());
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
                fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Encrypt section loaded."), LogPanel.LOG_INFO);                     
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
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, source_text_field, 40, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, source_text_field, -120, SpringLayout.EAST, this);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, source_text_field, 20, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, source_text_field, 5, SpringLayout.WEST, this);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, source_file_label, 0, SpringLayout.WEST, source_text_field);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, source_file_label, 5, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, source_text_field);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, browse_button, -20, SpringLayout.EAST, this);        
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, browse_button, -88, SpringLayout.EAST, browse_button);
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, encrypt_options_panel, 205, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, encrypt_options_panel, -5, SpringLayout.EAST, this);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, encrypt_options_panel, 70, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, encrypt_options_panel, 0, SpringLayout.WEST, source_text_field);
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, encrypt_options_label, 0, SpringLayout.NORTH, encrypt_options_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, encrypt_options_label, 5, SpringLayout.SOUTH, browse_button);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, encrypt_options_label, 0, SpringLayout.WEST, source_text_field);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, dest_folder_label, 5, SpringLayout.SOUTH, encrypt_options_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, dest_folder_label, 0, SpringLayout.WEST, encrypt_options_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, destination_panel, 320, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, destination_panel, 0, SpringLayout.EAST, encrypt_options_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, destination_panel, 230, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, destination_panel, 0, SpringLayout.WEST, encrypt_options_panel);
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
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, overwrite_checkbox, 30, SpringLayout.NORTH, overwrite_checkbox);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, overwrite_checkbox, 1, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.WEST, overwrite_checkbox, 0, SpringLayout.WEST, dest_folder_text);        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, browse_dest_button, 0, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.EAST, browse_dest_button, -10, SpringLayout.EAST, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, browse_dest_button, -25, SpringLayout.SOUTH, dest_folder_text);
        destination_panel_layout.putConstraint(SpringLayout.WEST, browse_dest_button, -98, SpringLayout.EAST, destination_panel);

        destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_help_label, -1, SpringLayout.SOUTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.EAST, destination_help_label, -1, SpringLayout.EAST, destination_panel);

        encrypt_spring_layout.putConstraint(SpringLayout.EAST, output_options_label, 0, SpringLayout.EAST, destination_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, output_options_label, 0, SpringLayout.WEST, destination_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, output_options_label, 5, SpringLayout.SOUTH, destination_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, output_options_panel, 390, SpringLayout.NORTH, this);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, output_options_panel, 0, SpringLayout.EAST, destination_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, output_options_panel, 0, SpringLayout.SOUTH, output_options_label);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, output_options_panel, 0, SpringLayout.WEST, output_options_label);
        e_panel_layout.putConstraint(SpringLayout.SOUTH, out_prefix_label, 25, SpringLayout.NORTH, output_options_panel);
        e_panel_layout.putConstraint(SpringLayout.NORTH, out_prefix_label, 5, SpringLayout.NORTH, output_options_panel);
        e_panel_layout.putConstraint(SpringLayout.WEST, out_prefix_label, 5, SpringLayout.WEST, output_options_panel);
        e_panel_layout.putConstraint(SpringLayout.EAST, out_prefix_text, -10, SpringLayout.EAST, output_options_panel);
        e_panel_layout.putConstraint(SpringLayout.SOUTH, out_prefix_text, 0, SpringLayout.SOUTH, out_prefix_label);
        e_panel_layout.putConstraint(SpringLayout.NORTH, out_prefix_text, 0, SpringLayout.NORTH, out_prefix_label);
        e_panel_layout.putConstraint(SpringLayout.WEST, out_prefix_text, 15, SpringLayout.EAST, out_prefix_label);
        
        e_panel_layout.putConstraint(SpringLayout.SOUTH, prefix_help_label, -1, SpringLayout.SOUTH, output_options_panel);
        e_panel_layout.putConstraint(SpringLayout.EAST, prefix_help_label, -1, SpringLayout.EAST, output_options_panel);
        
        encrypt_spring_layout.putConstraint(SpringLayout.SOUTH, run_button, 25, SpringLayout.NORTH, run_button);
        encrypt_spring_layout.putConstraint(SpringLayout.EAST, run_button, 0, SpringLayout.EAST, browse_button);
        encrypt_spring_layout.putConstraint(SpringLayout.NORTH, run_button, 5, SpringLayout.SOUTH, output_options_panel);
        encrypt_spring_layout.putConstraint(SpringLayout.WEST, run_button, 0, SpringLayout.WEST, browse_button);

//      RADIO_LAYOUT
        options_pane_layout.putConstraint(SpringLayout.SOUTH, owner_pwd_label, 20, SpringLayout.NORTH, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.EAST, owner_pwd_label, 140, SpringLayout.WEST, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.NORTH, owner_pwd_label, 10, SpringLayout.NORTH, encrypt_options_panel);
        options_pane_layout.putConstraint(SpringLayout.WEST, owner_pwd_label, 10, SpringLayout.WEST, encrypt_options_panel);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, owner_pwd_field, 0, SpringLayout.SOUTH, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.EAST, owner_pwd_field, 140, SpringLayout.WEST, owner_pwd_field);
        options_pane_layout.putConstraint(SpringLayout.NORTH, owner_pwd_field, 0, SpringLayout.NORTH, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.WEST, owner_pwd_field, 5, SpringLayout.EAST, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, user_pwd_label, 0, SpringLayout.SOUTH, owner_pwd_field);
        options_pane_layout.putConstraint(SpringLayout.EAST, user_pwd_label, 100, SpringLayout.WEST, user_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.NORTH, user_pwd_label, 0, SpringLayout.NORTH, owner_pwd_field);
        options_pane_layout.putConstraint(SpringLayout.WEST, user_pwd_label, 5, SpringLayout.EAST, owner_pwd_field);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, user_pwd_field, 0, SpringLayout.SOUTH, user_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.EAST, user_pwd_field, 140, SpringLayout.WEST, user_pwd_field);
        options_pane_layout.putConstraint(SpringLayout.NORTH, user_pwd_field, 0, SpringLayout.NORTH, user_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.WEST, user_pwd_field, 5, SpringLayout.EAST, user_pwd_label);

        options_pane_layout.putConstraint(SpringLayout.SOUTH, encrypt_type_label, 20, SpringLayout.NORTH, encrypt_type_label);
        options_pane_layout.putConstraint(SpringLayout.EAST, encrypt_type_label, 0, SpringLayout.EAST, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.NORTH, encrypt_type_label, 5, SpringLayout.SOUTH, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.WEST, encrypt_type_label, 0, SpringLayout.WEST, owner_pwd_label);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, encrypt_type, 0, SpringLayout.SOUTH, encrypt_type_label);
        options_pane_layout.putConstraint(SpringLayout.EAST, encrypt_type, 100, SpringLayout.WEST, encrypt_type);
        options_pane_layout.putConstraint(SpringLayout.NORTH, encrypt_type, 0, SpringLayout.NORTH, encrypt_type_label);
        options_pane_layout.putConstraint(SpringLayout.WEST, encrypt_type, 5, SpringLayout.EAST, encrypt_type_label);
        
        options_pane_layout.putConstraint(SpringLayout.SOUTH, allowall_check, 20, SpringLayout.NORTH, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.EAST, allowall_check, 170, SpringLayout.WEST, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.NORTH, allowall_check, 5, SpringLayout.SOUTH, encrypt_type_label);
        options_pane_layout.putConstraint(SpringLayout.WEST, allowall_check, 0, SpringLayout.WEST, encrypt_type_label);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[3], 0, SpringLayout.SOUTH, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[3], 230, SpringLayout.WEST, permissions_check[3]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[3], 0, SpringLayout.NORTH, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[3], 5, SpringLayout.EAST, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[7], 0, SpringLayout.SOUTH, permissions_check[3]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[7], 230, SpringLayout.WEST, permissions_check[7]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[7], 0, SpringLayout.NORTH, permissions_check[3]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[7], 0, SpringLayout.EAST, permissions_check[3]);
        
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[0], 20, SpringLayout.NORTH, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[0], 0, SpringLayout.EAST, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[0], 0, SpringLayout.SOUTH, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[0], 0, SpringLayout.WEST, allowall_check);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[1], 0, SpringLayout.SOUTH, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[1], 0, SpringLayout.EAST, permissions_check[3]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[1], 0, SpringLayout.NORTH, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[1], 5, SpringLayout.EAST, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[2], 0, SpringLayout.SOUTH, permissions_check[1]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[2], 0, SpringLayout.EAST, permissions_check[7]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[2], 0, SpringLayout.NORTH, permissions_check[1]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[2], 0, SpringLayout.EAST, permissions_check[1]);
        
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[4], 20, SpringLayout.NORTH, permissions_check[4]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[4], 0, SpringLayout.EAST, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[4], 0, SpringLayout.SOUTH, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[4], 0, SpringLayout.WEST, permissions_check[0]);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[5], 0, SpringLayout.SOUTH, permissions_check[4]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[5], 0, SpringLayout.EAST, permissions_check[1]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[5], 0, SpringLayout.NORTH, permissions_check[4]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[5], 0, SpringLayout.WEST, permissions_check[1]);
        options_pane_layout.putConstraint(SpringLayout.SOUTH, permissions_check[6], 0, SpringLayout.SOUTH, permissions_check[4]);
        options_pane_layout.putConstraint(SpringLayout.EAST, permissions_check[6], 0, SpringLayout.EAST, permissions_check[2]);
        options_pane_layout.putConstraint(SpringLayout.NORTH, permissions_check[6], 0, SpringLayout.NORTH, permissions_check[4]);
        options_pane_layout.putConstraint(SpringLayout.WEST, permissions_check[6], 0, SpringLayout.WEST, permissions_check[2]);

    }
    
	/**
	*@return Console parameter for the selected encryption algorithm from the JComboBox
	*/
	private String getEncAlg(String combo_enc){
		String retval = CmdParser.E_RC4_40;
		if(combo_enc != null){
			if(combo_enc.equals(EncryptMainGUI.RC4_40)){
				retval = CmdParser.E_RC4_40;
			}else if(combo_enc.equals(EncryptMainGUI.RC4_128)){
				retval = CmdParser.E_RC4_128;
			}else if(combo_enc.equals(EncryptMainGUI.AES_128)){
				retval = CmdParser.E_AES_128;
			}
		}
		return retval;			
	}	
	
	/**
	*@return <code>LinkedList</code> containing permissions parameters
	*/
	private LinkedList getEncPermissions(JCheckBox[] pChecks, JCheckBox allowAll){
		LinkedList ret = new LinkedList();
		if(allowAll.isSelected()){
			ret.add("-allow");
			ret.add("print");
			ret.add("-allow");
			ret.add("modify");
			ret.add("-allow");
			ret.add("copy");
			ret.add("-allow");
			ret.add("modifyannotations");
			ret.add("-allow");
			ret.add("screenreaders");
			ret.add("-allow");
			ret.add("fill");
			ret.add("-allow");
			ret.add("assembly");
			ret.add("-allow");
			ret.add("degradedprinting");
		}
		else{
			if(	pChecks[EncryptMainGUI.PRINT].isSelected()){
				ret.add("-allow");
				ret.add("print");			
			}
			if(	pChecks[EncryptMainGUI.DPRINT].isSelected()){
				ret.add("-allow");
				ret.add("degradedprinting");			
			}
			if(	pChecks[EncryptMainGUI.COPY].isSelected()){
				ret.add("-allow");
				ret.add("copy");			
			}
			if(	pChecks[EncryptMainGUI.MODIFY].isSelected()){
				ret.add("-allow");
				ret.add("modify");			
			}
			if(	pChecks[EncryptMainGUI.FILL].isSelected()){
				ret.add("-allow");
				ret.add("fill");			
			}
			if(	pChecks[EncryptMainGUI.SCREEN].isSelected()){
				ret.add("-allow");
				ret.add("screenreaders");			
			}
			if(	pChecks[EncryptMainGUI.ASSEMBLY].isSelected()){
				ret.add("-allow");
				ret.add("assembly");			
			}
			if(	pChecks[EncryptMainGUI.ANNOTATION].isSelected()){
				ret.add("-allow");
				ret.add("modifyannotations");			
			}
		}
		return ret;
	}
    
    public FocusTraversalPolicy getFocusPolicy(){
        return (FocusTraversalPolicy)encrypt_focus_policy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for encrypt panel
     *
     */
    public class EncryptFocusPolicy extends FocusTraversalPolicy {
        public EncryptFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(source_text_field)){
                return browse_button;
            }
            else if (aComponent.equals(browse_button)){
                return owner_pwd_field;
            }
            else if (aComponent.equals(owner_pwd_field)){
                return user_pwd_field;
            }
            else if (aComponent.equals(user_pwd_field)){
                return encrypt_type;
            }
            else if (aComponent.equals(encrypt_type)){
                return allowall_check;
            }
            else if (aComponent.equals(allowall_check)){
                if (allowall_check.isSelected()){
                    return same_as_source_radio;
                }else{
                    return permissions_check[0];
                }
            }        
            else if (aComponent.equals(permissions_check[0])){
                return permissions_check[1];
            }
            else if (aComponent.equals(permissions_check[1])){
                return permissions_check[2];
            }
            else if (aComponent.equals(permissions_check[2])){
                return permissions_check[3];
            }
            else if (aComponent.equals(permissions_check[3])){
                return permissions_check[4];
            }
            else if (aComponent.equals(permissions_check[4])){
                return permissions_check[5];
            }
            else if (aComponent.equals(permissions_check[5])){
                return permissions_check[6];
            }
            else if (aComponent.equals(permissions_check[6])){
                return permissions_check[7];
            }
            else if (aComponent.equals(permissions_check[7])){
                return same_as_source_radio;
            }
            else if (aComponent.equals(same_as_source_radio)){
                return choose_a_folder_radio;
            }
            else if (aComponent.equals(choose_a_folder_radio)){
            	if (same_as_source_radio.isSelected()){
                    return out_prefix_text;
                }else{
                    return source_text_field;
                } 
            }
            else if (aComponent.equals(source_text_field)){
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
            	if (same_as_source_radio.isSelected()){
                    return choose_a_folder_radio;
                }else{
                    return browse_dest_button;
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
                if (allowall_check.isSelected()){
                    return allowall_check;
                }else{
                    return permissions_check[7];
                }
            }
            else if (aComponent.equals(permissions_check[7])){
                return permissions_check[6];
            }
            else if (aComponent.equals(permissions_check[6])){
                return permissions_check[5];
            }
            else if (aComponent.equals(permissions_check[5])){
                return permissions_check[4];
            }
            else if (aComponent.equals(permissions_check[4])){
                return permissions_check[3];
            }
            else if (aComponent.equals(permissions_check[3])){
                return permissions_check[2];
            }
            else if (aComponent.equals(permissions_check[2])){
                return permissions_check[1];
            }
            else if (aComponent.equals(permissions_check[1])){
                return permissions_check[0];
            }
            else if (aComponent.equals(permissions_check[0])){
                return allowall_check;
            }
            else if (aComponent.equals(allowall_check)){
                return encrypt_type;
            }
            else if (aComponent.equals(encrypt_type)){
                return user_pwd_field;
            }
            else if (aComponent.equals(user_pwd_field)){
                return owner_pwd_field;
            }
            else if (aComponent.equals(owner_pwd_field)){
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
