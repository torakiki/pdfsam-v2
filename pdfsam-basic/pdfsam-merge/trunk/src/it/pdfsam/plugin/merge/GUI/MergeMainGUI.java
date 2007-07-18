/*
 * Created on 06-Feb-2006
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


package it.pdfsam.plugin.merge.GUI;

import it.pdfsam.components.JHelpLabel;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.MainConsole;
import it.pdfsam.console.events.WorkDoneEvent;
import it.pdfsam.console.interfaces.WorkDoneListener;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.interfaces.PlugablePanel;
import it.pdfsam.listeners.EnterDoClickListener;
import it.pdfsam.plugin.merge.component.JMergeTable;
import it.pdfsam.plugin.merge.component.JMergeToolTipHeader;
import it.pdfsam.plugin.merge.component.PageColumnRender;
import it.pdfsam.plugin.merge.listener.MoveActionListener;
import it.pdfsam.plugin.merge.listener.RemoveActionListener;
import it.pdfsam.plugin.merge.model.MergeTableModel;
import it.pdfsam.plugin.merge.type.MergeItemType;
import it.pdfsam.plugin.merge.type.TableTransferHandler;
import it.pdfsam.utils.PdfFilter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumnModel;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Plugable JPanel provides a GUI for merge functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class MergeMainGUI extends JPanel implements WorkDoneListener,PlugablePanel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1525753714701236844L;
    private JList wip_list;
    private JTextField destination_text_field;
    private MergeTableModel modello_merge_table = new MergeTableModel();
    private JMergeTable merge_table;
    private SpringLayout spring_layout_merge_panel;
    private String log_msg;
    private String log_color;
    private JFileChooser file_chooser;
    private JFileChooser browse_file_chooser;
    private SpringLayout destination_panel_layout;
    private JPanel destination_panel = new JPanel();
    private SpringLayout option_panel_layout;
    private JPanel option_panel = new JPanel();
    private JCheckBox overwrite_checkbox = new JCheckBox();
    private ResourceBundle i18n_messages;
    private Configuration config;
    private MainConsole mc;
    private JCheckBox merge_type_check = new JCheckBox();
    private JCheckBox output_compressed_check = new JCheckBox();
    private JHelpLabel mergetype_help_label;
    private JHelpLabel destination_help_label;
    private final JScrollPane merge_table_scroll_panel = new JScrollPane();
    private final JLabel destination_label = new JLabel();
    private final JLabel option_label = new JLabel();
    private final DefaultListModel list_model = new DefaultListModel();  

//Buttons
    private final JButton add_file_button = new JButton();
    private final JButton remove_file_button = new JButton();
    private final JButton move_up_button = new JButton();
    private final JButton move_down_button = new JButton();
    private final JButton run_button = new JButton();
    private final JButton browse_button = new JButton();
    private final JButton clear_button = new JButton();
//keylisteners
    private final EnterDoClickListener add_enterkey_listener = new EnterDoClickListener(add_file_button);
    private final EnterDoClickListener remove_enterkey_listener = new EnterDoClickListener(remove_file_button);
    private final EnterDoClickListener moveu_enterkey_listener = new EnterDoClickListener(move_up_button);
    private final EnterDoClickListener moved_enterkey_listener = new EnterDoClickListener(move_down_button);
    private final EnterDoClickListener run_enterkey_listener = new EnterDoClickListener(run_button);
    private final EnterDoClickListener browse_enterkey_listener = new EnterDoClickListener(browse_button);
    private final EnterDoClickListener clear_enterkey_listener = new EnterDoClickListener(clear_button);
    
    private final ThreadGroup add_or_run_threads = new ThreadGroup("add and run threads");
    private final JProgressBar progress_bar = new JProgressBar();


//focus policy 
    private final MergeFocusPolicy merge_focus_policy = new MergeFocusPolicy();
    
    private final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private final String PLUGIN_NAME = "Merge";
    private final String PLUGIN_VERSION = "0.5.0";
    
    
    /**
     * Constructor
     */
    public MergeMainGUI() {
        super();          
        initialize();             
    }
    
    /**
     * Panel initialization   
     */
    private void initialize() {
    	config = Configuration.getInstance();
        i18n_messages = config.getI18nResourceBundle();
        mc = config.getMainConsole();
        
        //set focus  policy
        setFocusable(false);
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        spring_layout_merge_panel = new SpringLayout();
        setLayout(spring_layout_merge_panel);

        merge_table_scroll_panel.setOpaque(false);
        new DropTarget(merge_table_scroll_panel, new FileDropper());
        new DropTarget(merge_table, new FileDropper());
        add(merge_table_scroll_panel);
//MERGE_TABLE_MODEL
        String[] i18n_column_names = {GettextResource.gettext(i18n_messages,"File name"),
                GettextResource.gettext(i18n_messages,"Path"),
                        GettextResource.gettext(i18n_messages,"Pages"),
                                GettextResource.gettext(i18n_messages,"Page Selection")};
        modello_merge_table.setColumnNames(i18n_column_names);
//MERGE_TABLE
        merge_table = new JMergeTable();
        merge_table.setModel(modello_merge_table);
        merge_table.setDragEnabled(true);
        merge_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        merge_table.setRowHeight(18);
        merge_table.setRowMargin(5);
        merge_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        merge_table.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        merge_table.setSelectionForeground(Color.BLACK);
        merge_table.setSelectionBackground(new Color(211, 221, 222));
        merge_table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //merge_table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        merge_table.setGridColor(Color.LIGHT_GRAY);
        merge_table.setIntercellSpacing(new Dimension(7, 2));
        merge_table.setTransferHandler(new TableTransferHandler());
//END_MERGE_TABLE        
//PAGE_COLUMN_RENDER (to show encrypt image)        
        TableColumnModel merge_table_col_model = merge_table.getColumnModel();
        PageColumnRender pc_render = new PageColumnRender();
        merge_table_col_model.getColumn(MergeTableModel.PAGES).setCellRenderer(pc_render);
//END_PAGE_COLUMN_RENDER
//TOOLTIP_HEADER        
        JMergeToolTipHeader tool_tip_header = new JMergeToolTipHeader(merge_table_col_model);
        tool_tip_header.setToolTipStrings(modello_merge_table.getToolTips());
        merge_table.setTableHeader(tool_tip_header);        
//END_TOOLTIP_HEADER        
        merge_table_scroll_panel.setViewportView(merge_table);
        merge_table.getTableHeader().setReorderingAllowed(false);
//END_MERGE_TABLE        
//ADD_FILE_CHOOSER        
        file_chooser = new JFileChooser();
        file_chooser.setFileFilter(new PdfFilter());
        file_chooser.setMultiSelectionEnabled(true);
//END_ADD_FILE_CHOOSER
//BROWSE_FILE_CHOOSER        
        browse_file_chooser = new JFileChooser();
        browse_file_chooser.setFileFilter(new PdfFilter());
        browse_file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//END_BROWSE_FILE_CHOOSER        
//ADD_BUTTON
        add_file_button.setMargin(new Insets(2, 2, 2, 2));
        add_file_button.setToolTipText(GettextResource.gettext(i18n_messages,"Add a pdf to the list"));
        add_file_button.setIcon(new ImageIcon(this.getClass().getResource("/images/add.png")));
        add_file_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {                
               final Thread add_thread = new Thread(add_or_run_threads, "add") {
                    
                   private String wip_text;
                    
                    public void run() {
                        //Open FileChooser with pdf filter
                        int return_val = file_chooser.showOpenDialog(add_file_button.getParent());
                        File[] files = null;
                        if (return_val == JFileChooser.APPROVE_OPTION){
                            files = file_chooser.getSelectedFiles();
                        }
                        //something has been selected
                        //fix 22/03
                        if (files != null){
                            //String num_pages;
                            for (int i=0; i < files.length; i++){
                                wip_text = GettextResource.gettext(i18n_messages,"Please wait while reading ")+files[i].getName()+" ...";
                                addWipText(wip_text);
                                addTableRow(files[i]);
                                removeWipText(wip_text);
                            }
                        }
                    }
                };                
                add_thread.start();                
            }
        });
        
        add_file_button.setText(GettextResource.gettext(i18n_messages,"Add"));
        add(add_file_button);
//END_ADD_BUTTON
//REMOVE_BUTTON
        remove_file_button.setMargin(new Insets(2, 2, 2, 2));
        remove_file_button.setToolTipText(GettextResource.gettext(i18n_messages,"Remove a pdf from the list"));
        remove_file_button.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
        //listener
        remove_file_button.addActionListener(new RemoveActionListener(merge_table,this));
        remove_file_button.setText(GettextResource.gettext(i18n_messages,"Remove"));
        add(remove_file_button);
//END_REMOVE_BUTTON
//BUTTON_UP        
        move_up_button.setMargin(new Insets(2, 2, 2, 2));
        move_up_button.addActionListener(new MoveActionListener(merge_table,this,MoveActionListener.MOVE_UP));        
        move_up_button.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
        move_up_button.setText(GettextResource.gettext(i18n_messages,"Move Up"));
        move_up_button.setToolTipText(GettextResource.gettext(i18n_messages,"Move up selected pdf file"));
        add(move_up_button);
//END_BUTTON_UP
//BUTTON_DOWN  
        //listener
        move_down_button.addActionListener(new MoveActionListener(merge_table,this,MoveActionListener.MOVE_DOWN));
        move_down_button.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
        move_down_button.setMargin(new Insets(2, 2, 2, 2));
        move_down_button.setText(GettextResource.gettext(i18n_messages,"Move Down"));
        move_down_button.setToolTipText(GettextResource.gettext(i18n_messages,"Move down selected pdf file"));
        add(move_down_button);
//END_BUTTON_DOWN
//BUTTON_CLEAR        
        clear_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ((MergeTableModel) merge_table.getModel()).clearData();
            }
        });
        clear_button.setIcon(new ImageIcon(this.getClass().getResource("/images/clear.png")));
        clear_button.setMargin(new Insets(2, 2, 2, 2));
        clear_button.setText(GettextResource.gettext(i18n_messages,"Clear"));
        clear_button.setToolTipText(GettextResource.gettext(i18n_messages,"Remove every pdf file from the merge list"));
        add(clear_button);
//END_BUTTON_CLEAR        
        destination_text_field = new JTextField();
        destination_text_field.setDropTarget(null);
        destination_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
        destination_panel.add(destination_text_field);

        destination_label.setText(GettextResource.gettext(i18n_messages,"Destination output file:"));
        add(destination_label);
        
//POPUP_MENU
        final JPopupMenu popupMenu = new JPopupMenu();

        final JMenuItem menu_item_remove = new JMenuItem();
        menu_item_remove.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
        menu_item_remove.setText(GettextResource.gettext(i18n_messages,"Remove"));
        menu_item_remove.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                remove_file_button.doClick(0);
                }
        });

        final JMenuItem menu_item_move_up = new JMenuItem();
        menu_item_move_up.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
        menu_item_move_up.setText(GettextResource.gettext(i18n_messages,"Move Up"));
        menu_item_move_up.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                move_up_button.doClick(0);
                }
        });

        final JMenuItem menu_item_move_down = new JMenuItem();
        menu_item_move_down.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
        menu_item_move_down.setText(GettextResource.gettext(i18n_messages,"Move Down"));
        menu_item_move_down.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                move_down_button.doClick(0);
                }
        });

        final JMenuItem menu_item_set_output = new JMenuItem();
        menu_item_set_output.setIcon(new ImageIcon(this.getClass().getResource("/images/set_outfile.png")));
        menu_item_set_output.setText(GettextResource.gettext(i18n_messages,"Set output file"));
        menu_item_set_output.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int selected_row = merge_table.getSelectedRow();
                if ( selected_row != -1){
                    try{
                        File tmp_file = new File(((MergeTableModel) merge_table.getModel()).getRow(merge_table.getSelectedRow()).getFilePath());
                        //get the filename with or withour separator
                        String file_name=(tmp_file.getParent().endsWith(File.separator))? "merged_file.pdf": File.separator+"merged_file.pdf"; 
                        destination_text_field.setText(tmp_file.getParent() + file_name);
                    }
                    catch (Exception exept){
                        fireLogActionPerformed(GettextResource.gettext(i18n_messages,"Error: Unable to get the file path."), "FF0000"); 
                    }
                }
                }
        });
        
        popupMenu.add(menu_item_remove);
        popupMenu.add(menu_item_move_up);
        popupMenu.add(menu_item_move_down);
        popupMenu.add(menu_item_set_output);
//END_POPUP_MENU
//LISTENER_POPUP
        merge_table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    showMenu(e);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    showMenu(e);
            }
            private void showMenu(MouseEvent e) {
                popupMenu.show(merge_table, e.getX(), e.getY());
                /*mostra il menu e seleziona la riga*/
                Point p = e.getPoint();
                merge_table.setRowSelectionInterval(merge_table.rowAtPoint(p), merge_table.rowAtPoint(p));
            }
        });
//END_LISTENER_POPUP
//OPTION_PANEL
        option_panel_layout = new SpringLayout();
        option_panel.setLayout(option_panel_layout);
        option_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(option_panel);
        
//END_OPTION_PANEL 
        option_label.setText(GettextResource.gettext(i18n_messages,"Merge options:"));
        add(option_label);
        
        merge_type_check.setText(GettextResource.gettext(i18n_messages,"PDF documents contain forms"));
        merge_type_check.setSelected(false);
        option_panel.add(merge_type_check);     

        String helpText = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Merge type")+"</b><ul>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Unchecked")+":</b> "+GettextResource.gettext(i18n_messages,"Use this merge type for standard pdf documents")+".</li>" +
    		"<li><b>"+GettextResource.gettext(i18n_messages,"Checked")+":</b> "+GettextResource.gettext(i18n_messages,"Use this merge type for pdf documents containing forms")+"." +
    		"<br><b>"+GettextResource.gettext(i18n_messages,"Note")+":</b> "+GettextResource.gettext(i18n_messages,"Setting this option the documents will be completely loaded in memory")+".</li>" +
    		"</ul></body></html>";
	    mergetype_help_label = new JHelpLabel(helpText, true);
	    option_panel.add(mergetype_help_label);
//DESTINATION_PANEL
        destination_panel_layout = new SpringLayout();
        destination_panel.setLayout(destination_panel_layout);
        destination_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        add(destination_panel);
//END_DESTINATION_PANEL  
//CHECK_COMPRESSION        
        output_compressed_check.setText(GettextResource.gettext(i18n_messages,"Compress output file"));
        output_compressed_check.setSelected(true);
        destination_panel.add(output_compressed_check);
        
//BROWSE_BUTTON        
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
                        fireLogActionPerformed(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), "FF0000"); 
                        
                    }
                }
                
            }
        });        
        browse_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
        destination_panel.add(browse_button);
//END_BROWSE_BUTTON
//CHECK_BOX
        overwrite_checkbox.setText(GettextResource.gettext(i18n_messages,"Overwrite if already exists"));
        overwrite_checkbox.setSelected(true);
        destination_panel.add(overwrite_checkbox);
//END_CHECK_BOX  
//HELP_LABEL_DESTINATION        
        String helpTextDest = 
    		"<html><body><b>"+GettextResource.gettext(i18n_messages,"Destination output file")+"</b>" +
    		"<p>"+GettextResource.gettext(i18n_messages,"Browse or enter the full path to the destination output file.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want to overwrite the output file if it already exists.")+"</p>"+
    		"<p>"+GettextResource.gettext(i18n_messages,"Check the box if you want a compressed output file.")+"</p>"+
    		"</body></html>";
	    destination_help_label = new JHelpLabel(helpTextDest, true);
	    destination_panel.add(destination_help_label);
//END_HELP_LABEL_DESTINATION	    
//RUN_BUTTON
        run_button.addActionListener(new ActionListener() {            
            public void actionPerformed(ActionEvent e) {
                if (add_or_run_threads.activeCount() > 0){
                    fireLogActionPerformed(GettextResource.gettext(i18n_messages,"Please wait while all files are processed..") , "000000");
                    return;
                }
                //clean the wip_list
                if(!(list_model.isEmpty())){
                    list_model.removeAllElements();  
                }                
                final LinkedList args = new LinkedList();                
                args.add("-o");
                //validation and permission check are demanded to the CmdParser object
                try{
                    //if no extension given
                    if ((destination_text_field.getText().length() > 0) && (destination_text_field.getText().lastIndexOf('.') == -1)){
                        destination_text_field.setText(destination_text_field.getText()+".pdf");
                    }
                    args.add(destination_text_field.getText());
                    String page_sel_string = "";
                    for (int i = 0; i < merge_table.getModel().getRowCount(); i++){
                        String page_selection = ((String)merge_table.getModel().getValueAt(i, MergeTableModel.PAGESELECTION)).trim();
                        //it's a string like 12-32,45
                        //new feature 23-otc-2006 A.V.
                        if(page_selection.indexOf(",") != 0){
                            String[] selections_array = page_selection.split(",");
                            for(int j = 0; j<selections_array.length; j++){
                                String tmp_string = selections_array[j].trim();
                                if((tmp_string != null)&&(!tmp_string.equals(""))){
                                    args.add("-f");
                                    args.add(merge_table.getModel().getValueAt(i, MergeTableModel.PATH));
                                    page_sel_string += (tmp_string.matches("[\\d]+"))? tmp_string+"-"+tmp_string+":" : tmp_string+":";
                                }                                
                            }
                        
                        }else{
                            args.add("-f");
                            args.add(merge_table.getModel().getValueAt(i, MergeTableModel.PATH));
                            page_sel_string += (page_selection.matches("[\\d]+"))? page_selection+"-"+page_selection+":" : page_selection+":";
                        }                    }
                    args.add("-u");
                    args.add(page_sel_string);
                    if (overwrite_checkbox.isSelected()) args.add("-overwrite");
                    if (merge_type_check.isSelected()) args.add("-copyfields");
                    if (output_compressed_check.isSelected()) args.add("-compressed");
                    args.add("concat");
                }catch(Exception any_ex){    
                    fireLogActionPerformed("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), "FF0000");
                    //any_ex.printStackTrace();
                }                          
                //cast array
                Object[] myObjectArray = args.toArray();
                final String[] myStringArray = new String[myObjectArray.length];
                for (int i = 0; i < myStringArray.length; i++) {
                    myStringArray[i] = myObjectArray[i].toString();
                }
                progress_bar.setValue(0); 
                progress_bar.setString("0 %");
                //run concat in its own thread              
                final Thread run_thread = new Thread(add_or_run_threads, "run") {
                     public void run() {
                      try{                           
                            mc.addWorkDoneListener((WorkDoneListener)MergeMainGUI.this);
                            MergeMainGUI.this.addWipText(GettextResource.gettext(i18n_messages,"Please wait while all files are processed.."));
                            String out_msg = mc.mainAction(myStringArray, true);
                            MergeMainGUI.this.removeWipText(GettextResource.gettext(i18n_messages,"Please wait while all files are processed.."));
                            fireLogActionPerformed("Command Line: "+args.toString()+"<br>"+ out_msg , "000000");
                     }catch(Exception any_ex){
                             MergeMainGUI.this.removeWipTextAll();
                            fireLogActionPerformed("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), "FF0000");
                     }
                    }
               };
               run_thread.start();                
            }
        });
        run_button.setMargin(new Insets(2, 2, 2, 2));
        run_button.setToolTipText(GettextResource.gettext(i18n_messages,"Execute pdf merge"));
        run_button.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
        run_button.setText(GettextResource.gettext(i18n_messages,"Run"));
        add(run_button);
//END_RUN_BUTTON
//KEY_LISTENER
        add_file_button.addKeyListener(add_enterkey_listener);
        remove_file_button.addKeyListener(remove_enterkey_listener);
        run_button.addKeyListener(run_enterkey_listener);
        clear_button.addKeyListener(clear_enterkey_listener);
        browse_button.addKeyListener(browse_enterkey_listener);
        move_up_button.addKeyListener(moveu_enterkey_listener);
        move_down_button.addKeyListener(moved_enterkey_listener);
        
        destination_text_field.addKeyListener(run_enterkey_listener);
 /*manage keystroke over the table ALT+ARROW_UP, ALT+ARROW_DOWN, ALT+CANC*/       
        merge_table.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_UP)){
                    move_up_button.doClick();
                }
                else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_DOWN)){
                    move_down_button.doClick();
                }
                else if((e.getKeyCode() == KeyEvent.VK_DELETE)){
                    remove_file_button.doClick();
                }
            }
        });

//END_KEY_LISTENER
//WIP_LIST
        wip_list = new JList();
        wip_list.setFocusable(false);
        wip_list.setModel(list_model); 
        wip_list.setBackground(this.getBackground());
        add(wip_list);
//END_WIP_LIST 
//PROGRESS_BAR
        add(progress_bar);
        progress_bar.setMaximum(100);
        progress_bar.setValue(0);
        progress_bar.setStringPainted(true);
        progress_bar.setString("0 %");
//END_PROGRESS_BAR        
//LAYOUT
        setLayout();

//END_LAYOUT
    }
    
    public void addTableRow(File file_to_add){
            if (file_to_add != null){
                boolean encrypt = false;
                String num_pages = "";
                try{
                    //fix 03/07 for memory usage
                    PdfReader pdf_reader = new PdfReader(new RandomAccessFileOrArray(file_to_add.getAbsolutePath()), null);
                    encrypt = pdf_reader.isEncrypted();
                    // we retrieve the total number of pages
                    num_pages = Integer.toString(pdf_reader.getNumberOfPages());
                    pdf_reader.close();
                }
                catch (Exception ex){
                    num_pages = ex.getMessage();                    
                }
                try{
                    modello_merge_table.addRow(new MergeItemType(file_to_add.getName(),file_to_add.getAbsolutePath(), num_pages,"All",encrypt ));
                    fireLogActionPerformed(GettextResource.gettext(i18n_messages,"File selected: ")+file_to_add.getName(), "#000000");
                }
                catch (Exception ex){
                    fireLogActionPerformed(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), "#000000"); 
                    
                }
            }
        }
    
    /**
     * Update log text area
     * @param log_msg Log message
     * @param log_color Log text color
     */
    public void fireLogActionPerformed(String log_msg, String log_color) {
        this.log_msg = log_msg;
        this.log_color = log_color;
        this.firePropertyChange("LOG", null, "LOG_UPDATED");
    }
    
    /**
     * Set plugin layout for each component
     *
     */
    private void setLayout(){
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, merge_table_scroll_panel, 175, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, merge_table_scroll_panel, -110, SpringLayout.EAST, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, merge_table_scroll_panel, 30, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, merge_table_scroll_panel, 5, SpringLayout.WEST, this);
        
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, add_file_button, 25, SpringLayout.NORTH, add_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, add_file_button, -5, SpringLayout.EAST, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, add_file_button, 0, SpringLayout.NORTH, merge_table_scroll_panel);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, add_file_button, -93, SpringLayout.EAST, add_file_button);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, remove_file_button, 25, SpringLayout.NORTH, remove_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, remove_file_button, 5, SpringLayout.SOUTH, add_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, remove_file_button, 0, SpringLayout.EAST, add_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, remove_file_button, 0, SpringLayout.WEST, add_file_button);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, move_up_button, 25, SpringLayout.NORTH, move_up_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, move_up_button, 5, SpringLayout.SOUTH, remove_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, move_up_button, 0, SpringLayout.EAST, remove_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, move_up_button, 0, SpringLayout.WEST, remove_file_button);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, move_down_button, 25, SpringLayout.NORTH, move_down_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, move_down_button, 5, SpringLayout.SOUTH, move_up_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, move_down_button, 0, SpringLayout.EAST, move_up_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, move_down_button, 0, SpringLayout.WEST, move_up_button);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, clear_button, 25, SpringLayout.NORTH, clear_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, clear_button, 5, SpringLayout.SOUTH, move_down_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, clear_button, 0, SpringLayout.EAST, move_down_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, clear_button, 0, SpringLayout.WEST, move_down_button);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, option_panel, 235, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, option_panel, 0, SpringLayout.EAST, add_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, option_panel, 200, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, option_panel, 0, SpringLayout.WEST, merge_table_scroll_panel);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, option_label, 0, SpringLayout.NORTH, option_panel);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, option_label, 0, SpringLayout.WEST, option_panel);

        option_panel_layout.putConstraint(SpringLayout.SOUTH, merge_type_check, 20, SpringLayout.NORTH, option_panel);
        option_panel_layout.putConstraint(SpringLayout.EAST, merge_type_check, -40, SpringLayout.EAST, option_panel);
        option_panel_layout.putConstraint(SpringLayout.NORTH, merge_type_check, 5, SpringLayout.NORTH, option_panel);
        option_panel_layout.putConstraint(SpringLayout.WEST, merge_type_check, 5, SpringLayout.WEST, option_panel);

        option_panel_layout.putConstraint(SpringLayout.SOUTH, mergetype_help_label, -1, SpringLayout.SOUTH, option_panel);
        option_panel_layout.putConstraint(SpringLayout.EAST, mergetype_help_label, -1, SpringLayout.EAST, option_panel);

        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, destination_panel, 335, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, destination_panel, 0, SpringLayout.EAST, add_file_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, destination_panel, 255, SpringLayout.NORTH, this);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, destination_panel, 0, SpringLayout.WEST, merge_table_scroll_panel);

        destination_panel_layout.putConstraint(SpringLayout.EAST, destination_text_field, -105, SpringLayout.EAST, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, destination_text_field, 5, SpringLayout.NORTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_text_field, 30, SpringLayout.NORTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.WEST, destination_text_field, 5, SpringLayout.WEST, destination_panel);

        destination_panel_layout.putConstraint(SpringLayout.SOUTH, overwrite_checkbox, 15, SpringLayout.NORTH, overwrite_checkbox);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, overwrite_checkbox, 5, SpringLayout.SOUTH, destination_text_field);
        destination_panel_layout.putConstraint(SpringLayout.WEST, overwrite_checkbox, 0, SpringLayout.WEST, destination_text_field);

        destination_panel_layout.putConstraint(SpringLayout.SOUTH, output_compressed_check, 15, SpringLayout.NORTH, output_compressed_check);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, output_compressed_check, 5, SpringLayout.SOUTH, overwrite_checkbox);
        destination_panel_layout.putConstraint(SpringLayout.WEST, output_compressed_check, 0, SpringLayout.WEST, overwrite_checkbox);

        destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_help_label, -1, SpringLayout.SOUTH, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.EAST, destination_help_label, -1, SpringLayout.EAST, destination_panel);
        
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, destination_label, 0, SpringLayout.NORTH, destination_panel);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, destination_label, 0, SpringLayout.WEST, destination_panel);
        
        destination_panel_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
        destination_panel_layout.putConstraint(SpringLayout.EAST, browse_button, -5, SpringLayout.EAST, destination_panel);
        destination_panel_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, destination_text_field);
        destination_panel_layout.putConstraint(SpringLayout.WEST, browse_button, -93, SpringLayout.EAST, destination_panel);        
        
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, run_button, 25, SpringLayout.NORTH, run_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, run_button, 0, SpringLayout.EAST, clear_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, run_button, 0, SpringLayout.WEST, clear_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, run_button, 10, SpringLayout.SOUTH, destination_panel);
        
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, wip_list, 60, SpringLayout.NORTH, wip_list);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, wip_list, -5, SpringLayout.WEST, run_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, wip_list, 0, SpringLayout.NORTH, run_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, wip_list, 0, SpringLayout.WEST, destination_panel);
        
        spring_layout_merge_panel.putConstraint(SpringLayout.SOUTH, progress_bar, 15, SpringLayout.NORTH, progress_bar);
        spring_layout_merge_panel.putConstraint(SpringLayout.EAST, progress_bar, 0, SpringLayout.EAST, run_button);
        spring_layout_merge_panel.putConstraint(SpringLayout.NORTH, progress_bar, 0, SpringLayout.SOUTH, wip_list);
        spring_layout_merge_panel.putConstraint(SpringLayout.WEST, progress_bar, 0, SpringLayout.WEST, wip_list);

    }
    
    /**
     * sets the language and init the panel
     * @deprecated now language is taken by the configuration singleton
     */
    public void init(String language_code) {
        /*language = language_code;
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        initialize();*/
    }
    
    
    /**
     * add a text to say the user we are working
     */
    private void addWipText(final String wip_text){
        
        Runnable runner = new Runnable() {
            public void run() {
                list_model.addElement(wip_text);
            }
        };
        SwingUtilities.invokeLater(runner);
    }
    /**
     * remove the text to say the user we are working
     */
    private void removeWipText(final String wip_text){
        
        Runnable runner = new Runnable() {
            public void run() {
                list_model.removeElement(wip_text);
            }
        };
        SwingUtilities.invokeLater(runner);
    }
    /**
     * removes every element from the list
     */
    private void removeWipTextAll(){
        
        Runnable runner = new Runnable() {
            public void run() {
                list_model.removeAllElements();
            }
        };
        SwingUtilities.invokeLater(runner);
    }    
    /**
     * @return Returns the log_color.
     */
    public String getLogColor() {
        return log_color;
    }
    /**
     * @return Returns the log_msg.
     */
    public String getLogMsg() {
        return log_msg;
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
        return (FocusTraversalPolicy)merge_focus_policy;
        
    }
    
    /**
     * 
     * @author Andrea Vacondio
     * Focus policy for merge panel
     *
     */
    public class MergeFocusPolicy extends FocusTraversalPolicy {
        public MergeFocusPolicy(){
            super();
        }
        
        public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
            if (aComponent.equals(add_file_button)){
                return remove_file_button;
            }
            else if (aComponent.equals(remove_file_button)){
                return move_up_button;
            }
            else if (aComponent.equals(move_up_button)){
                return move_down_button;
            }
            else if (aComponent.equals(move_down_button)){
                return clear_button;
            }        
            else if (aComponent.equals(clear_button)){
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
                return add_file_button;
            }
            return add_file_button;
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
                return clear_button;
            }
            else if (aComponent.equals(clear_button)){
                return move_down_button;
            }
            else if (aComponent.equals(move_down_button)){
                return move_up_button;
            }
            else if (aComponent.equals(move_up_button)){
                return remove_file_button;
            }        
            else if (aComponent.equals(remove_file_button)){
                return add_file_button;
            }
            else if (aComponent.equals(add_file_button)){
                return run_button;
            }
            return add_file_button;
        }
        
        public Component getDefaultComponent(Container CycleRootComp){
            return add_file_button;
        }

        public Component getLastComponent(Container CycleRootComp){
            return run_button;
        }

        public Component getFirstComponent(Container CycleRootComp){
            return add_file_button;
        }
    }
    
    public class FileDropper extends DropTargetAdapter {

        public void drop(DropTargetDropEvent e) {
            try {
                DropTargetContext context = e.getDropTargetContext();
                e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                Transferable t = e.getTransferable();
                Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
                if (data instanceof List) {
                    List list = (List)data;
                    for (int k=0; k<list.size(); k++) {
                        Object dataLine = list.get(k);
                        if (dataLine instanceof File){
                            addTableRow((File)dataLine);
                        }
                    }
                }
                context.dropComplete(true);
            }
            catch (UnsupportedFlavorException ufe){                
            }
            catch (Exception ex) {
                fireLogActionPerformed(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), "FF0000");
            }
         }
    }

    /**
     * mange and event that notify a change in percentage of the work done
     */
    public void percentageOfWorkDoneChanged(WorkDoneEvent wde) {
        final int perc = wde.getPercentageDone();
        if (wde.getType() == WorkDoneEvent.PERCENTAGE_CHANGE){
            
            Runnable runner = new Runnable() {
                public void run() {
                    progress_bar.setValue(perc);
                    progress_bar.setString(Integer.toString(perc)+" %");
                }
            };
            SwingUtilities.invokeLater(runner);
        }
    }
 
    public void workingIndeterminate(WorkDoneEvent wde){
    	if (wde.getType() == WorkDoneEvent.WORK_INDETERMINATE){
    		progress_bar.setIndeterminate(true);
        }
    }
    
    public void workCompleted(WorkDoneEvent wde) {
        if (wde.getType() == WorkDoneEvent.WORK_DONE){
        	progress_bar.setIndeterminate(false);
        	progress_bar.setValue(0);
        	progress_bar.setString("");
        }
    }

    public Icon getIcon() {
        try{
            return new ImageIcon(this.getClass().getResource("/images/merge.png"));
        }catch (Exception e){
            return null;            
        }
    }    
}
