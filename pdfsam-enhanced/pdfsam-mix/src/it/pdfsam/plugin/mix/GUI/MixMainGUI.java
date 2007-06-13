/*
 * Created on 12-Jan-2007
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
package it.pdfsam.plugin.mix.GUI;

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.MainConsole;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.listeners.EnterDoClickListener;
import it.pdfsam.panels.LogPanel;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.dom4j.Element;
import org.dom4j.Node;

/** 
 * Plugable JPanel provides a GUI for alternate mix functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class MixMainGUI extends AbstractPlugIn{
	

	private static final long serialVersionUID = -21589235901766769L;
	private SpringLayout destination_panel_layout;
	private JPanel destination_panel = new JPanel();
	private JCheckBox overwrite_checkbox = new JCheckBox();
	private JCheckBox reverse_first_checkbox = new JCheckBox();
	private JCheckBox reverse_second_checkbox = new JCheckBox();
	private JTextField destination_text_field;
	private JTextField first_text_field;
	private JTextField second_text_field;
	private JHelpLabel destination_help_label;
	private SpringLayout spring_layout_mix_panel;
	private ResourceBundle i18n_messages;
	private Configuration config;
	private MainConsole mc;	
	private JFileChooser browse_file_chooser;

	private final MixFocusPolicy mix_focus_policy = new MixFocusPolicy();
	//buttons
	private final JButton run_button = new JButton();
	private final JButton browse_first_button = new JButton();
	private final JButton browse_second_button = new JButton();
	private final JButton browse_button = new JButton();
	
	private final JLabel destination_label = new JLabel();
	private final JLabel first_label = new JLabel();
	private final JLabel second_label = new JLabel();

	private final EnterDoClickListener run_enterkey_listener = new EnterDoClickListener(run_button);
	private final EnterDoClickListener browse_enterkey_listener = new EnterDoClickListener(browse_button);
	private final EnterDoClickListener browse_first_enterkey_listener = new EnterDoClickListener(browse_button);
	private final EnterDoClickListener browse_second_enterkey_listener = new EnterDoClickListener(browse_second_button);

    private final ThreadGroup run_threads = new ThreadGroup("run threads");

	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_NAME = "Alternate Mix";
	private static final String PLUGIN_VERSION = "0.0.7e";
	
	/**
	 * Constructor
	 */
	public MixMainGUI() {
		super();          
		initialize();

	}
	
	
	private void initialize() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		config = Configuration.getInstance();
		i18n_messages = config.getI18nResourceBundle();
		mc = config.getMainConsole();
		setPanelIcon("/images/mix.png");
		//set focus  policy
		setFocusable(false);
		spring_layout_mix_panel = new SpringLayout();
		setLayout(spring_layout_mix_panel);
		
//		BROWSE_FILE_CHOOSER        
		browse_file_chooser = new JFileChooser();
		browse_file_chooser.setFileFilter(new PdfFilter());
		browse_file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		END_BROWSE_FILE_CHOOSER         
		browse_first_button.setMargin(new Insets(2, 2, 2, 2));
		browse_first_button.setToolTipText(GettextResource.gettext(i18n_messages,"Browse filesystem for the first file to mix"));
		browse_first_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
		browse_first_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browse_file_chooser.showOpenDialog(browse_first_button.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browse_file_chooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosen_file != null){
					try{
						first_text_field.setText(chosen_file.getAbsolutePath());
					}
					catch (Exception ex){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

					}
				}

			}
		});        
		browse_first_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
		add(browse_first_button);
//		END_BROWSE_BUTTON
//		BROWSE_BUTTON        
		browse_second_button.setMargin(new Insets(2, 2, 2, 2));
		browse_second_button.setToolTipText(GettextResource.gettext(i18n_messages,"Browse filesystem for the second file to mix"));
		browse_second_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
		browse_second_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browse_file_chooser.showOpenDialog(browse_second_button.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browse_file_chooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosen_file != null){
					try{
						second_text_field.setText(chosen_file.getAbsolutePath());
					}
					catch (Exception ex){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

					}
				}

			}
		});        
		browse_second_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
		add(browse_second_button);
		

		destination_label.setText(GettextResource.gettext(i18n_messages,"Destination output file:"));
		add(destination_label);

		first_label.setText(GettextResource.gettext(i18n_messages,"First pdf document:"));
		add(first_label);

		second_label.setText(GettextResource.gettext(i18n_messages,"Second pdf document:"));
		add(second_label);
		
//CHECK_BOX		
		reverse_first_checkbox.setText(GettextResource.gettext(i18n_messages,"Reverse this document"));
		reverse_first_checkbox.setSelected(false);
		add(reverse_first_checkbox);

		reverse_second_checkbox.setText(GettextResource.gettext(i18n_messages,"Reverse this document"));
		reverse_second_checkbox.setSelected(true);
		add(reverse_second_checkbox);
//END_CHECK_BOX

//		DESTINATION_PANEL
		destination_panel_layout = new SpringLayout();
		destination_panel.setLayout(destination_panel_layout);
		destination_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
		add(destination_panel);
//		END_DESTINATION_PANEL   
		first_text_field = new JTextField();
		first_text_field.setDropTarget(null);
		first_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		add(first_text_field);

		second_text_field = new JTextField();
		second_text_field.setDropTarget(null);
		second_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		add(second_text_field);
		
		destination_text_field = new JTextField();
		destination_text_field.setDropTarget(null);
		destination_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		destination_panel.add(destination_text_field);
		
//		BROWSE_BUTTON        
		browse_button.setMargin(new Insets(2, 2, 2, 2));
		browse_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
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
						destination_text_field.setText(chosen_file.getAbsolutePath());
					}
					catch (Exception ex){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

					}
				}

			}
		});        
		browse_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
		destination_panel.add(browse_button);
//		END_BROWSE_BUTTON
		
//		CHECK_BOX
		overwrite_checkbox.setText(GettextResource.gettext(i18n_messages,"Overwrite if already exists"));
		overwrite_checkbox.setSelected(true);
		destination_panel.add(overwrite_checkbox);
//		END_CHECK_BOX 
//      HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Destination output file")+"</b>" +
    		"<p>"+GettextResource.gettext(i18n_messages,"Browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want to overwrite the output file if it already exists.")+"</p>"+
    		"</body></html>";
	    destination_help_label = new JHelpLabel(helpTextDest, true);
	    destination_panel.add(destination_help_label);
//END_HELP_LABEL_DESTINATION 		
//		RUN_BUTTON
		run_button.addActionListener(new ActionListener() {            
			public void actionPerformed(ActionEvent e) {
				final LinkedList args = new LinkedList(); 
				try{
//					add footer
					if((first_text_field.getText() != null)&&(!first_text_field.getText().equals(""))){
						args.add("-f1");
						args.add(first_text_field.getText());
					} 
					if((second_text_field.getText() != null)&&(!second_text_field.getText().equals(""))){
						args.add("-f2");
						args.add(second_text_field.getText());
					} 

					args.add("-o");
//					if no extension given
					if (destination_text_field.getText().lastIndexOf('.') == -1){
						destination_text_field.setText(destination_text_field.getText()+".pdf");
					}
					args.add(destination_text_field.getText());

					if (overwrite_checkbox.isSelected()) args.add("-overwrite");

					if (reverse_first_checkbox.isSelected()) args.add("-reversefirst");

					if (reverse_second_checkbox.isSelected()) args.add("-reversesecond");

					args.add ("mix");
				}catch(Exception any_ex){    
					fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
				} 
//				cast array
				Object[] myObjectArray = args.toArray();
				final String[] myStringArray = new String[myObjectArray.length];
				for (int i = 0; i < myStringArray.length; i++) {
					myStringArray[i] = myObjectArray[i].toString();
				}
//				run concat in its own thread              
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
		run_button.setToolTipText(GettextResource.gettext(i18n_messages,"Execute pdf alternate mix"));
		run_button.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
		run_button.setText(GettextResource.gettext(i18n_messages,"Run"));
		add(run_button);
//		END_RUN_BUTTON		
		
		destination_text_field.addKeyListener(run_enterkey_listener);
		run_button.addKeyListener(run_enterkey_listener);
		browse_button.addKeyListener(browse_enterkey_listener);
		browse_first_button.addKeyListener(browse_first_enterkey_listener);
		browse_second_button.addKeyListener(browse_second_enterkey_listener);
		
		setLayout();
	}

	/**
	 * Set plugin layout for each component
	 *
	 */
	private void setLayout(){
		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, first_label, 25, SpringLayout.NORTH, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, first_label, 175, SpringLayout.WEST, first_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, first_label, 5, SpringLayout.NORTH, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, first_label, 5, SpringLayout.WEST, this);

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, first_text_field, 20, SpringLayout.NORTH, first_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, first_text_field, -112, SpringLayout.EAST, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, first_text_field, 0, SpringLayout.SOUTH, first_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, first_text_field, 0, SpringLayout.WEST, first_label);

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, browse_first_button, 25, SpringLayout.NORTH, browse_first_button);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, browse_first_button, -20, SpringLayout.EAST, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, browse_first_button, 0, SpringLayout.NORTH, first_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, browse_first_button, -88, SpringLayout.EAST, browse_first_button);        

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, reverse_first_checkbox, 20, SpringLayout.NORTH, reverse_first_checkbox);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, reverse_first_checkbox, 0, SpringLayout.EAST, first_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, reverse_first_checkbox, 2, SpringLayout.SOUTH, first_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, reverse_first_checkbox, 0, SpringLayout.WEST, first_label);        

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, second_label, 20, SpringLayout.NORTH, second_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, second_label, 0, SpringLayout.EAST, reverse_first_checkbox);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, second_label, 25, SpringLayout.SOUTH, reverse_first_checkbox);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, second_label, 0, SpringLayout.WEST, reverse_first_checkbox);

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, second_text_field, 20, SpringLayout.NORTH, second_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, second_text_field, -112, SpringLayout.EAST, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, second_text_field, 0, SpringLayout.SOUTH, second_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, second_text_field, 0, SpringLayout.WEST, second_label);

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, browse_second_button, 25, SpringLayout.NORTH, browse_second_button);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, browse_second_button, -20, SpringLayout.EAST, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, browse_second_button, 0, SpringLayout.NORTH, second_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, browse_second_button, -88, SpringLayout.EAST, browse_second_button);        

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, reverse_second_checkbox, 20, SpringLayout.NORTH, reverse_second_checkbox);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, reverse_second_checkbox, 0, SpringLayout.EAST, second_label);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, reverse_second_checkbox, 2, SpringLayout.SOUTH, second_text_field);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, reverse_second_checkbox, 0, SpringLayout.WEST, second_label);        

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, destination_panel, 70, SpringLayout.NORTH, destination_panel);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, destination_panel, -7, SpringLayout.EAST, this);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, destination_panel, 35, SpringLayout.SOUTH, reverse_second_checkbox);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, destination_panel, 0, SpringLayout.WEST, reverse_second_checkbox);

		destination_panel_layout.putConstraint(SpringLayout.EAST, destination_text_field, -105, SpringLayout.EAST, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, destination_text_field, 5, SpringLayout.NORTH, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_text_field, 30, SpringLayout.NORTH, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.WEST, destination_text_field, 5, SpringLayout.WEST, destination_panel);

		destination_panel_layout.putConstraint(SpringLayout.SOUTH, overwrite_checkbox, 30, SpringLayout.NORTH, overwrite_checkbox);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, overwrite_checkbox, 1, SpringLayout.SOUTH, destination_text_field);
		destination_panel_layout.putConstraint(SpringLayout.WEST, overwrite_checkbox, 0, SpringLayout.WEST, destination_text_field);

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, destination_label, 0, SpringLayout.NORTH, destination_panel);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, destination_label, 0, SpringLayout.WEST, destination_panel);

		destination_panel_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
		destination_panel_layout.putConstraint(SpringLayout.EAST, browse_button, -10, SpringLayout.EAST, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, destination_text_field);
		destination_panel_layout.putConstraint(SpringLayout.WEST, browse_button, -88, SpringLayout.EAST, browse_button);        

		destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_help_label, -1, SpringLayout.SOUTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.EAST, destination_help_label, -1, SpringLayout.EAST, destination_panel);                

		spring_layout_mix_panel.putConstraint(SpringLayout.SOUTH, run_button, 25, SpringLayout.NORTH, run_button);
		spring_layout_mix_panel.putConstraint(SpringLayout.EAST, run_button, 0, SpringLayout.EAST, browse_first_button);
		spring_layout_mix_panel.putConstraint(SpringLayout.WEST, run_button, 0, SpringLayout.WEST, browse_first_button);
		spring_layout_mix_panel.putConstraint(SpringLayout.NORTH, run_button, 10, SpringLayout.SOUTH, destination_panel);

	}

	/**
	 * @return the Plugin author
	 */
	public String getPluginAuthor(){
		return PLUGIN_AUTHOR;
	}

	/**
	 * @return the Plugin name
	 */    
	public String getPluginName(){
		return PLUGIN_NAME;
	}

	/**
	 * @return the Plugin version
	 */    
	public String getVersion(){
		return PLUGIN_VERSION;
	}
	
	/**
	 * @return the FocusTraversalPolicy associated with the plugin
	 */
	public FocusTraversalPolicy getFocusPolicy(){
		return (FocusTraversalPolicy)mix_focus_policy;

	}

	public Node getJobNode(Node arg0) throws SaveJobException {
		try{
			if (arg0 != null){
				Element first_node = ((Element)arg0).addElement("first");
				first_node.addAttribute("value", first_text_field.getText());			

				Element second_node = ((Element)arg0).addElement("second");
				second_node.addAttribute("value", second_text_field.getText());			

				Element file_destination = ((Element)arg0).addElement("destination");
				file_destination.addAttribute("value", destination_text_field.getText());			

				Element reverse_first = ((Element)arg0).addElement("reverse_first");
				reverse_first.addAttribute("value", reverse_first_checkbox.isSelected()?"true":"false");

				Element reverse_second = ((Element)arg0).addElement("reverse_second");
				reverse_second.addAttribute("value", reverse_second_checkbox.isSelected()?"true":"false");
				
				Element file_overwrite = ((Element)arg0).addElement("overwrite");
				file_overwrite.addAttribute("value", overwrite_checkbox.isSelected()?"true":"false");
			}
			return arg0;
		}
		catch (Exception ex){
			throw new SaveJobException(ex.getMessage(), ex);                     
		}
	}

	public void loadJobNode(Node arg) throws LoadJobException {
		final Node arg0 = arg;
		try{
			Node first_node = (Node) arg0.selectSingleNode("first/@value");
			if (first_node != null){
				first_text_field.setText(first_node.getText());
			}
			Node second_node = (Node) arg0.selectSingleNode("second/@value");
			if (second_node != null){
				second_text_field.setText(second_node.getText());
			}
			Node file_destination = (Node) arg0.selectSingleNode("destination/@value");
			if (file_destination != null){
				destination_text_field.setText(file_destination.getText());
			}
			Node file_overwrite = (Node) arg0.selectSingleNode("overwrite/@value");
			if (file_overwrite != null){
				overwrite_checkbox.setSelected(file_overwrite.getText().equals("true"));
			}
			Node reverse_first = (Node) arg0.selectSingleNode("reverse_first/@value");
			if (reverse_first != null){
				reverse_first_checkbox.setSelected(reverse_first.getText().equals("true"));
			}
			Node reverse_second = (Node) arg0.selectSingleNode("reverse_second/@value");
			if (reverse_second != null){
				reverse_second_checkbox.setSelected(reverse_second.getText().equals("true"));
			}

			fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"AlternateMix section loaded."), LogPanel.LOG_INFO);                     
		}
		catch (Exception ex){
			fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR);                     
		}

	}


	/**
	 * 
	 * @author Andrea Vacondio
	 * Focus policy for alternate_mix panel
	 *
	 */
	public class MixFocusPolicy extends FocusTraversalPolicy {
		public MixFocusPolicy(){
			super();
		}

		public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
			if (aComponent.equals(first_text_field)){
				return browse_first_button;
			}
			else if (aComponent.equals(browse_first_button)){
				return reverse_first_checkbox;
			}        
			else if (aComponent.equals(reverse_first_checkbox)){
				return second_text_field;
			}
			else if (aComponent.equals(second_text_field)){
				return browse_second_button;
			}
			else if (aComponent.equals(browse_second_button)){
				return reverse_second_checkbox;
			}
			else if (aComponent.equals(reverse_second_checkbox)){
				return destination_text_field;
			}
			else if (aComponent.equals(destination_text_field)){
				return browse_button;
			}
			else if (aComponent.equals(browse_button)){
				return overwrite_checkbox;
			}            
			else if (aComponent.equals(overwrite_checkbox)){
				return run_button;
			}
			else if (aComponent.equals(run_button)){
				return first_text_field;
			}
			return first_text_field;
		}

		public Component getComponentBefore(Container CycleRootComp, Component aComponent){

			if (aComponent.equals(run_button)){
				return overwrite_checkbox;
			}
			else if (aComponent.equals(overwrite_checkbox)){
				return browse_button;
			}
			else if (aComponent.equals(browse_button)){
				return destination_text_field;
			}
			else if (aComponent.equals(destination_text_field)){
				return reverse_second_checkbox;
			}
			else if (aComponent.equals(reverse_second_checkbox)){
				return browse_second_button;
			}
			else if (aComponent.equals(browse_second_button)){
				return second_text_field;
			}
			else if (aComponent.equals(second_text_field)){
				return reverse_first_checkbox;
			}
			else if (aComponent.equals(reverse_first_checkbox)){
				return browse_first_button;
			}
			else if (aComponent.equals(browse_first_button)){
				return first_text_field;
			}
			else if (aComponent.equals(first_text_field)){
				return run_button;
			}
			return first_text_field;
		}

		public Component getDefaultComponent(Container CycleRootComp){
			return first_text_field;
		}

		public Component getLastComponent(Container CycleRootComp){
			return run_button;
		}

		public Component getFirstComponent(Container CycleRootComp){
			return first_text_field;
		}
	}

}
