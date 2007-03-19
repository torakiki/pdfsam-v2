/*
 * Created on 20-Nov-2006
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


package it.pdfsam.plugin.coverfooter.GUI;

import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.console.MainConsole;
import it.pdfsam.console.tools.HtmlTags;
import it.pdfsam.exceptions.LoadJobException;
import it.pdfsam.exceptions.SaveJobException;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.panels.LogPanel;
import it.pdfsam.plugin.coverfooter.component.JCoverFooterTable;
import it.pdfsam.plugin.coverfooter.component.JCoverFooterToolTipHeader;
import it.pdfsam.plugin.coverfooter.component.PageColumnRender;
import it.pdfsam.plugin.coverfooter.listener.EnterDoClickListener;
import it.pdfsam.plugin.coverfooter.listener.RemoveActionListener;
import it.pdfsam.plugin.coverfooter.model.CoverFooterTableModel;
import it.pdfsam.plugin.coverfooter.thread.DoJobThread;
import it.pdfsam.plugin.coverfooter.type.CoverFooterItemType;
import it.pdfsam.plugin.coverfooter.type.TableTransferHandler;
import it.pdfsam.util.DirFilter;
import it.pdfsam.util.PdfFilter;

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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableColumnModel;

import org.dom4j.Element;
import org.dom4j.Node;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
/**
 * Plugable JPanel provides a GUI for spring_layout_cover_footer_panel functions.
 * @author Andrea Vacondio
 * @see it.pdfsam.interfaces.PlugablePanel
 * @see javax.swing.JPanel
 */
public class CoverFooterMainGUI extends AbstractPlugIn{


	private static final long serialVersionUID = -5213142341580648314L;
	private JList wip_list;
	private JTextField destination_text_field;
	private JTextField cover_text_field;
	private JTextField footer_text_field;
	private CoverFooterTableModel modello_cover_table = new CoverFooterTableModel();
	private JCoverFooterTable cover_table;
	private SpringLayout spring_layout_cover_footer_panel;
	private JFileChooser file_chooser;
	private JFileChooser browse_file_chooser;
	private JFileChooser browse_dir_chooser;
	private SpringLayout destination_panel_layout;
	private JPanel destination_panel = new JPanel();
	private JCheckBox overwrite_checkbox = new JCheckBox();
	private ResourceBundle i18n_messages;
	private Configuration config;
	private MainConsole mc;

//	Buttons
	private final JButton add_file_button = new JButton();
	private final JButton remove_file_button = new JButton();
	private final JButton run_button = new JButton();
	private final JButton browse_cover_button = new JButton();
	private final JButton browse_footer_button = new JButton();
	private final JButton browse_button = new JButton();
	private final JButton clear_button = new JButton();
//	keylisteners
	private final EnterDoClickListener add_enterkey_listener = new EnterDoClickListener(add_file_button);
	private final EnterDoClickListener remove_enterkey_listener = new EnterDoClickListener(remove_file_button);
	private final EnterDoClickListener run_enterkey_listener = new EnterDoClickListener(run_button);
	private final EnterDoClickListener browse_enterkey_listener = new EnterDoClickListener(browse_button);
	private final EnterDoClickListener browse_cover_enterkey_listener = new EnterDoClickListener(browse_cover_button);
	private final EnterDoClickListener browse_footer_enterkey_listener = new EnterDoClickListener(browse_footer_button);
	private final EnterDoClickListener clear_enterkey_listener = new EnterDoClickListener(clear_button);

	private final ThreadGroup add_or_run_threads = new ThreadGroup("add and run threads");

//	focus policy 
	private final CoverFooterFocusPolicy cover_footer_focus_policy = new CoverFooterFocusPolicy();
	private final JScrollPane cover_table_scroll_panel = new JScrollPane();
	private final JLabel destination_label = new JLabel();
	private final JLabel cover_label = new JLabel();
	private final JLabel footer_label = new JLabel();
	private final DefaultListModel list_model = new DefaultListModel();

	private static final String ALL_STRING = "All";
	private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
	private static final String PLUGIN_NAME = "Cover and Footer";
	private static final String PLUGIN_VERSION = "0.1.4e";

	/**
	 * Constructor
	 */
	public CoverFooterMainGUI() {
		super();          
		initialize();

	}

	/**
	 * Panel initialization   
	 */
	private void initialize() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		config = Configuration.getInstance();
		i18n_messages = config.getI18nResourceBundle();
		mc = config.getMainConsole();
		setPanelIcon("/images/cover_footer.png");
		//set focus  policy
		setFocusable(false);
		spring_layout_cover_footer_panel = new SpringLayout();
		setLayout(spring_layout_cover_footer_panel);

		cover_table_scroll_panel.setOpaque(false);
		new DropTarget(cover_table_scroll_panel, new FileDropper());
		new DropTarget(cover_table, new FileDropper());
		add(cover_table_scroll_panel);
//		MERGE_TABLE_MODEL
		String[] i18n_column_names = {GettextResource.gettext(i18n_messages,"File name"),
				GettextResource.gettext(i18n_messages,"Path"),
				GettextResource.gettext(i18n_messages,"Pages"),
				GettextResource.gettext(i18n_messages,"Page Selection")};
		modello_cover_table.setColumnNames(i18n_column_names);
//		MERGE_TABLE
		cover_table = new JCoverFooterTable();
		cover_table.setModel(modello_cover_table);
		cover_table.setDragEnabled(true);
		cover_table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		cover_table.setRowHeight(18);
		cover_table.setRowMargin(5);
		cover_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		cover_table.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		cover_table.setSelectionForeground(Color.BLACK);
		cover_table.setSelectionBackground(new Color(211, 221, 222));
		cover_table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//cover_table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		cover_table.setGridColor(Color.LIGHT_GRAY);
		cover_table.setIntercellSpacing(new Dimension(7, 2));
		cover_table.setTransferHandler(new TableTransferHandler());
//		END_MERGE_TABLE        
//		PAGE_COLUMN_RENDER (to show encrypt image)        
		TableColumnModel cover_table_col_model = cover_table.getColumnModel();
		PageColumnRender pc_render = new PageColumnRender();
		cover_table_col_model.getColumn(CoverFooterTableModel.PAGES).setCellRenderer(pc_render);
//		END_PAGE_COLUMN_RENDER
//		TOOLTIP_HEADER        
		JCoverFooterToolTipHeader tool_tip_header = new JCoverFooterToolTipHeader(cover_table_col_model);
		tool_tip_header.setToolTipStrings(modello_cover_table.getToolTips());
		cover_table.setTableHeader(tool_tip_header);        
//		END_TOOLTIP_HEADER        
		cover_table_scroll_panel.setViewportView(cover_table);
		cover_table.getTableHeader().setReorderingAllowed(false);
//		END_MERGE_TABLE        
//		ADD_FILE_CHOOSER        
		file_chooser = new JFileChooser();
		file_chooser.setFileFilter(new PdfFilter());
		file_chooser.setMultiSelectionEnabled(true);
//		END_ADD_FILE_CHOOSER
//		BROWSE_FILE_CHOOSER        
		browse_file_chooser = new JFileChooser();
		browse_file_chooser.setFileFilter(new PdfFilter());
		browse_file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		END_BROWSE_FILE_CHOOSER  
//		BROWSE_DIR_CHOOSER        
		browse_dir_chooser = new JFileChooser();
		browse_dir_chooser.setFileFilter(new DirFilter());
		browse_dir_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		END_BROWSE_DIR_CHOOSER        
//		ADD_BUTTON
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
//		END_ADD_BUTTON
//		REMOVE_BUTTON
		remove_file_button.setMargin(new Insets(2, 2, 2, 2));
		remove_file_button.setToolTipText(GettextResource.gettext(i18n_messages,"Remove a pdf from the list"));
		remove_file_button.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
		//listener
		remove_file_button.addActionListener(new RemoveActionListener(cover_table,this));
		remove_file_button.setText(GettextResource.gettext(i18n_messages,"Remove"));
		add(remove_file_button);
//		END_REMOVE_BUTTON
//		BUTTON_CLEAR        
		clear_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((CoverFooterTableModel) cover_table.getModel()).clearData();
			}
		});
		clear_button.setIcon(new ImageIcon(this.getClass().getResource("/images/clear.png")));
		clear_button.setMargin(new Insets(2, 2, 2, 2));
		clear_button.setText(GettextResource.gettext(i18n_messages,"Clear"));
		clear_button.setToolTipText(GettextResource.gettext(i18n_messages,"Remove every pdf file from the list"));
		add(clear_button);
//		END_BUTTON_CLEAR        
		destination_text_field = new JTextField();
		destination_text_field.setDropTarget(null);
		destination_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		destination_panel.add(destination_text_field);

		cover_text_field = new JTextField();
		cover_text_field.setDropTarget(null);
		cover_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		add(cover_text_field);

		footer_text_field = new JTextField();
		footer_text_field.setDropTarget(null);
		footer_text_field.setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
		add(footer_text_field);

		destination_label.setText(GettextResource.gettext(i18n_messages,"Destination output directory:"));
		add(destination_label);

		cover_label.setText(GettextResource.gettext(i18n_messages,"Cover pdf file:"));
		add(cover_label);

		footer_label.setText(GettextResource.gettext(i18n_messages,"Footer pdf file:"));
		add(footer_label);

//		POPUP_MENU
		final JPopupMenu popupMenu = new JPopupMenu();

		final JMenuItem menu_item_remove = new JMenuItem();
		menu_item_remove.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
		menu_item_remove.setText(GettextResource.gettext(i18n_messages,"Remove"));
		menu_item_remove.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				remove_file_button.doClick(0);
			}
		});

		final JMenuItem menu_item_set_output = new JMenuItem();
		menu_item_set_output.setIcon(new ImageIcon(this.getClass().getResource("/images/set_outfile.png")));
		menu_item_set_output.setText(GettextResource.gettext(i18n_messages,"Set output file"));
		menu_item_set_output.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				int selected_row = cover_table.getSelectedRow();
				if ( selected_row != -1){
					try{
						File tmp_file = new File(((CoverFooterTableModel) cover_table.getModel()).getRow(cover_table.getSelectedRow()).getFilePath());
						//get the filename with or withour separator
						String file_name=(tmp_file.getParent().endsWith(File.separator))? "spring_layout_cover_footer_paneld_file.pdf": File.separator+"spring_layout_cover_footer_paneld_file.pdf"; 
						destination_text_field.setText(tmp_file.getParent() + file_name);
					}
					catch (Exception exept){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: Unable to get the file path."), LogPanel.LOG_ERROR); 
					}
				}
			}
		});

		popupMenu.add(menu_item_remove);
		popupMenu.add(menu_item_set_output);
//		END_POPUP_MENU
//		LISTENER_POPUP
		cover_table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}
			private void showMenu(MouseEvent e) {
				popupMenu.show(cover_table, e.getX(), e.getY());
				/*mostra il menu e seleziona la riga*/
				Point p = e.getPoint();
				cover_table.setRowSelectionInterval(cover_table.rowAtPoint(p), cover_table.rowAtPoint(p));
			}
		});
//		END_LISTENER_POPUP
//		DESTINATION_PANEL
		destination_panel_layout = new SpringLayout();
		destination_panel.setLayout(destination_panel_layout);
		destination_panel.setBorder(new MatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
		add(destination_panel);
//		END_DESTINATION_PANEL         
//		BROWSE_BUTTON        
		browse_button.setMargin(new Insets(2, 2, 2, 2));
		browse_button.setToolTipText(GettextResource.gettext(i18n_messages,"Browse filesystem for a destination file"));
		browse_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
		browse_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browse_dir_chooser.showOpenDialog(browse_button.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browse_dir_chooser.getSelectedFile();
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
//		COVER_BROWSE_BUTTON        
		browse_cover_button.setMargin(new Insets(2, 2, 2, 2));
		browse_cover_button.setToolTipText(GettextResource.gettext(i18n_messages,"Browse filesystem for a cover file"));
		browse_cover_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
		browse_cover_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browse_file_chooser.showOpenDialog(browse_cover_button.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browse_file_chooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosen_file != null){
					try{
						cover_text_field.setText(chosen_file.getAbsolutePath());
					}
					catch (Exception ex){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

					}
				}

			}
		});        
		browse_cover_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
		add(browse_cover_button);
//		END_BROWSE_BUTTON
//		BROWSE_BUTTON        
		browse_footer_button.setMargin(new Insets(2, 2, 2, 2));
		browse_footer_button.setToolTipText(GettextResource.gettext(i18n_messages,"Browse filesystem for a footer file"));
		browse_footer_button.setIcon(new ImageIcon(this.getClass().getResource("/images/browse.png")));
		browse_footer_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int return_val = browse_file_chooser.showOpenDialog(browse_footer_button.getParent());
				File chosen_file = null;                
				if (return_val == JFileChooser.APPROVE_OPTION){
					chosen_file = browse_file_chooser.getSelectedFile();
				}
				//write the destination in text field
				if (chosen_file != null){
					try{
						footer_text_field.setText(chosen_file.getAbsolutePath());
					}
					catch (Exception ex){
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

					}
				}

			}
		});        
		browse_footer_button.setText(GettextResource.gettext(i18n_messages,"Browse"));
		add(browse_footer_button);
//		END_BROWSE_BUTTON        
//		CHECK_BOX
		overwrite_checkbox.setText(GettextResource.gettext(i18n_messages,"Overwrite if already exists"));
		overwrite_checkbox.setSelected(true);
		destination_panel.add(overwrite_checkbox);
//		END_CHECK_BOX        
//		RUN_BUTTON
		run_button.addActionListener(new ActionListener() {            
			public void actionPerformed(ActionEvent e) {
				if (add_or_run_threads.activeCount() > 0){
					fireLogPropertyChanged( GettextResource.gettext(i18n_messages,"Please wait while all files are processed..") , LogPanel.LOG_INFO);
					return;
				}
				List thread_list = new ArrayList();				
				//clean the wip_list
				if(!(list_model.isEmpty())){
					list_model.removeAllElements();  
				}                
				final LinkedList args = new LinkedList();                
				final LinkedList args1 = new LinkedList();

				if (overwrite_checkbox.isSelected()) args.add("-overwrite");
				if (cover_table.getModel().getRowCount() > 0){
					//validation and permission check are demanded to the CmdParser object
					for (int i = 0; i < cover_table.getModel().getRowCount(); i++){
						try{          
							args1.clear();
							args1.add("-o");
							args1.add(destination_text_field.getText()+File.separator+((String)cover_table.getModel().getValueAt(i, CoverFooterTableModel.FILENAME)).trim());
							String page_sel_string = "";
							args1.addAll(args);
							//add cover
							if((cover_text_field.getText() != null)&&(!cover_text_field.getText().equals(""))){
								args1.add("-f");
								args1.add(cover_text_field.getText());
								page_sel_string += "All:";
							} 
							String page_selection = ((String)cover_table.getModel().getValueAt(i, CoverFooterTableModel.PAGESELECTION)).trim();
							//it's a string like 12-32,45
							//new feature 23-otc-2006 A.V.
							if(page_selection.indexOf(",") != 0){
								String[] selections_array = page_selection.split(",");
								for(int j = 0; j<selections_array.length; j++){
									String tmp_string = selections_array[j].trim();
									if((tmp_string != null)&&(!tmp_string.equals(""))){
										args1.add("-f");
										args1.add(cover_table.getModel().getValueAt(i, CoverFooterTableModel.PATH));
										page_sel_string += (tmp_string.matches("[\\d]+"))? tmp_string+"-"+tmp_string+":" : tmp_string+":";
									}                                
								}

							}else{
								args1.add("-f");
								args1.add(cover_table.getModel().getValueAt(i, CoverFooterTableModel.PATH));
								page_sel_string += (page_selection.matches("[\\d]+"))? page_selection+"-"+page_selection+":" : page_selection+":";
							}
							//add footer
							if((footer_text_field.getText() != null)&&(!footer_text_field.getText().equals(""))){
								args1.add("-f");
								args1.add(footer_text_field.getText());
								page_sel_string += "All:";
							} 
							args1.add("-u");
							args1.add(page_sel_string);
							args1.add ("concat"); 
						}catch(Exception any_ex){    
							fireLogPropertyChanged("Command Line: "+args.toString()+"<br>Exception "+HtmlTags.disable(any_ex.toString()), LogPanel.LOG_ERROR);
						}                          						
						//run concat in its own thread  
						thread_list.add(new Thread(add_or_run_threads, new DoJobThread(mc, CoverFooterMainGUI.this, new LinkedList(args1), i18n_messages), "CoverAndFooter_"+i));
					}
				}else{
					fireLogPropertyChanged("Error: no document selected.", LogPanel.LOG_ERROR);
				}
				//starts all thread
				for(int j = 0; j<thread_list.size(); j++){
					((Thread)thread_list.get(j)).start();
				}
			}
		});
		run_button.setMargin(new Insets(2, 2, 2, 2));
		run_button.setToolTipText(GettextResource.gettext(i18n_messages,"Execute pdf add cover footer"));
		run_button.setIcon(new ImageIcon(this.getClass().getResource("/images/run.png")));
		run_button.setText(GettextResource.gettext(i18n_messages,"Run"));
		add(run_button);
//		END_RUN_BUTTON
//		KEY_LISTENER
		add_file_button.addKeyListener(add_enterkey_listener);
		remove_file_button.addKeyListener(remove_enterkey_listener);
		run_button.addKeyListener(run_enterkey_listener);
		clear_button.addKeyListener(clear_enterkey_listener);
		browse_button.addKeyListener(browse_enterkey_listener);
		browse_cover_button.addKeyListener(browse_cover_enterkey_listener);
		browse_footer_button.addKeyListener(browse_footer_enterkey_listener);


		destination_text_field.addKeyListener(run_enterkey_listener);
		/*manage keystroke over the table ALT+ARROW_UP, ALT+ARROW_DOWN, ALT+CANC*/       
		cover_table.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if((e.getKeyCode() == KeyEvent.VK_DELETE)){
					remove_file_button.doClick();
				}
			}
		});

//		END_KEY_LISTENER
//		WIP_LIST
		wip_list = new JList();
		wip_list.setFocusable(false);
		wip_list.setModel(list_model); 
		wip_list.setBackground(this.getBackground());
		add(wip_list);
//		END_WIP_LIST 
//		LAYOUT
		setLayout();

//		END_LAYOUT
	}

	private void addTableRow(File file_to_add){
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
				modello_cover_table.addRow(new CoverFooterItemType(file_to_add.getName(),file_to_add.getAbsolutePath(), num_pages,CoverFooterMainGUI.ALL_STRING,encrypt ));
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"File selected: ")+file_to_add.getName(), LogPanel.LOG_INFO);
			}
			catch (Exception ex){
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

			}
		}
	}

	private void addTableRowsFromNode(Node file_node){
		if (file_node != null){
			boolean encrypt = false;
			String num_pages = "";
			String page_selection = "";
			File file_to_add = null;
			try{
				Node file_name = (Node) file_node.selectSingleNode("@name");
				if (file_name != null){
					file_to_add = new File(file_name.getText());
				}
			}
			catch (Exception ex){
				file_to_add = null;
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR);                     
			}
			try{
				if (file_to_add != null){
					PdfReader pdf_reader = new PdfReader(new RandomAccessFileOrArray(file_to_add.getAbsolutePath()), null);
					encrypt = pdf_reader.isEncrypted();
					// we retrieve the total number of pages
					num_pages = Integer.toString(pdf_reader.getNumberOfPages());
					pdf_reader.close();
				}
			}
			catch (Exception ex){
				num_pages = ex.getMessage();                    
			}
			try{
				Node file_pages = (Node) file_node.selectSingleNode("@pageselection");
				if (file_pages != null){
					page_selection = file_pages.getText();
				}else{
					page_selection = CoverFooterMainGUI.ALL_STRING;
				}
			}
			catch (Exception ex){
				page_selection = CoverFooterMainGUI.ALL_STRING;                
			}
			try{				
				modello_cover_table.addRow(new CoverFooterItemType(file_to_add.getName(),file_to_add.getAbsolutePath(), num_pages,page_selection,encrypt ));
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"File selected: ")+file_to_add.getName(), LogPanel.LOG_INFO);
			}
			catch (Exception ex){
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR); 

			}
		}
	}		

	/**
	 * Set plugin layout for each component
	 *
	 */
	private void setLayout(){
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, cover_label, 25, SpringLayout.NORTH, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, cover_label, -5, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, cover_label, 5, SpringLayout.NORTH, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, cover_label, 5, SpringLayout.WEST, this);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, cover_text_field, 20, SpringLayout.NORTH, cover_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, cover_text_field, -110, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, cover_text_field, 0, SpringLayout.SOUTH, cover_label);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, cover_text_field, 0, SpringLayout.WEST, cover_label);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, browse_cover_button, 25, SpringLayout.NORTH, browse_cover_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, browse_cover_button, -7, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, browse_cover_button, 0, SpringLayout.NORTH, cover_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, browse_cover_button, -88, SpringLayout.EAST, browse_cover_button);        

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, footer_label, 20, SpringLayout.NORTH, footer_label);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, footer_label, 0, SpringLayout.EAST, cover_label);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, footer_label, 5, SpringLayout.SOUTH, cover_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, footer_label, 0, SpringLayout.WEST, cover_label);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, footer_text_field, 20, SpringLayout.NORTH, footer_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, footer_text_field, -110, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, footer_text_field, 0, SpringLayout.SOUTH, footer_label);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, footer_text_field, 0, SpringLayout.WEST, footer_label);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, browse_footer_button, 25, SpringLayout.NORTH, browse_footer_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, browse_footer_button, -7, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, browse_footer_button, 0, SpringLayout.NORTH, footer_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, browse_footer_button, -88, SpringLayout.EAST, browse_footer_button);        

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, cover_table_scroll_panel, 125, SpringLayout.NORTH, cover_table_scroll_panel);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, cover_table_scroll_panel, -110, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, cover_table_scroll_panel, 10, SpringLayout.SOUTH, footer_text_field);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, cover_table_scroll_panel, 0, SpringLayout.WEST, footer_text_field);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, add_file_button, 25, SpringLayout.NORTH, add_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, add_file_button, -7, SpringLayout.EAST, this);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, add_file_button, 0, SpringLayout.NORTH, cover_table_scroll_panel);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, add_file_button, -88, SpringLayout.EAST, add_file_button);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, remove_file_button, 25, SpringLayout.NORTH, remove_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, remove_file_button, 5, SpringLayout.SOUTH, add_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, remove_file_button, 0, SpringLayout.EAST, add_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, remove_file_button, 0, SpringLayout.WEST, add_file_button);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, clear_button, 25, SpringLayout.NORTH, clear_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, clear_button, 5, SpringLayout.SOUTH, remove_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, clear_button, 0, SpringLayout.EAST, remove_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, clear_button, 0, SpringLayout.WEST, remove_file_button);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, destination_panel, 70, SpringLayout.NORTH, destination_panel);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, destination_panel, 0, SpringLayout.EAST, add_file_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, destination_panel, 25, SpringLayout.SOUTH, cover_table_scroll_panel);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, destination_panel, 0, SpringLayout.WEST, cover_table_scroll_panel);

		destination_panel_layout.putConstraint(SpringLayout.EAST, destination_text_field, -105, SpringLayout.EAST, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, destination_text_field, 5, SpringLayout.NORTH, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.SOUTH, destination_text_field, 30, SpringLayout.NORTH, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.WEST, destination_text_field, 5, SpringLayout.WEST, destination_panel);

		destination_panel_layout.putConstraint(SpringLayout.SOUTH, overwrite_checkbox, 30, SpringLayout.NORTH, overwrite_checkbox);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, overwrite_checkbox, 1, SpringLayout.SOUTH, destination_text_field);
		destination_panel_layout.putConstraint(SpringLayout.WEST, overwrite_checkbox, 0, SpringLayout.WEST, destination_text_field);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, destination_label, 0, SpringLayout.NORTH, destination_panel);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, destination_label, 0, SpringLayout.WEST, destination_panel);

		destination_panel_layout.putConstraint(SpringLayout.SOUTH, browse_button, 25, SpringLayout.NORTH, browse_button);
		destination_panel_layout.putConstraint(SpringLayout.EAST, browse_button, -10, SpringLayout.EAST, destination_panel);
		destination_panel_layout.putConstraint(SpringLayout.NORTH, browse_button, 0, SpringLayout.NORTH, destination_text_field);
		destination_panel_layout.putConstraint(SpringLayout.WEST, browse_button, -80, SpringLayout.EAST, browse_button);        

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, run_button, 25, SpringLayout.NORTH, run_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, run_button, 0, SpringLayout.EAST, clear_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, run_button, 0, SpringLayout.WEST, clear_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, run_button, 10, SpringLayout.SOUTH, destination_panel);

		spring_layout_cover_footer_panel.putConstraint(SpringLayout.SOUTH, wip_list, 30, SpringLayout.NORTH, wip_list);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.EAST, wip_list, -5, SpringLayout.WEST, run_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.NORTH, wip_list, 0, SpringLayout.NORTH, run_button);
		spring_layout_cover_footer_panel.putConstraint(SpringLayout.WEST, wip_list, 0, SpringLayout.WEST, destination_panel);

	}


	/**
	 * add a text to say the user we are working
	 */
	public void addWipText(final String wip_text){

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
	public void removeWipText(final String wip_text){

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
	public void removeWipTextAll(){

		Runnable runner = new Runnable() {
			public void run() {
				list_model.removeAllElements();
			}
		};
		SwingUtilities.invokeLater(runner);
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
		return (FocusTraversalPolicy)cover_footer_focus_policy;

	}

	public Node getJobNode(Node arg0) throws SaveJobException {
		try{
			if (arg0 != null){
				Element cover_node = ((Element)arg0).addElement("cover");
				cover_node.addAttribute("value", cover_text_field.getText());			

				Element footer_node = ((Element)arg0).addElement("footer");
				footer_node.addAttribute("value", footer_text_field.getText());			

				Element filelist = ((Element)arg0).addElement("filelist");

				for (int i = 0; i < cover_table.getModel().getRowCount(); i++){
					Element file_node = ((Element)filelist).addElement("file");
					file_node.addAttribute("name",(String) cover_table.getModel().getValueAt(i, CoverFooterTableModel.PATH));
					file_node.addAttribute("pageselection",(String) cover_table.getModel().getValueAt(i, CoverFooterTableModel.PAGESELECTION));
				}

				Element file_destination = ((Element)arg0).addElement("destination");
				file_destination.addAttribute("value", destination_text_field.getText());			

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
		final Thread loadnode_thread = 
			new Thread(add_or_run_threads, "load") {
			private String wip_text;
			public void run() {			
				try{	
					Node cover_node = (Node) arg0.selectSingleNode("cover/@value");
					if (cover_node != null){
						cover_text_field.setText(cover_node.getText());
					}
					Node footer_node = (Node) arg0.selectSingleNode("footer/@value");
					if (footer_node != null){
						footer_text_field.setText(footer_node.getText());
					}
					Node file_destination = (Node) arg0.selectSingleNode("destination/@value");
					if (file_destination != null){
						destination_text_field.setText(file_destination.getText());
					}
					Node file_overwrite = (Node) arg0.selectSingleNode("overwrite/@value");
					if (file_overwrite != null){
						overwrite_checkbox.setSelected(file_overwrite.getText().equals("true"));
					}
					modello_cover_table.clearData();
					List file_list = arg0.selectNodes("filelist/file");
					wip_text = GettextResource.gettext(i18n_messages,"Please wait while reading ")+" ...";
					addWipText(wip_text);
					for (int i = 0; file_list != null && i < file_list.size(); i++) {
						Node file_node = (Node) file_list.get(i);
						addTableRowsFromNode(file_node);
					}
					removeWipText(wip_text);
					fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"CoverAndFooter section loaded."), LogPanel.LOG_INFO);                     
				}
				catch (Exception ex){
					fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR);                     
				}
			}
		};                
		loadnode_thread.start(); 				
	}

	/**
	 * 
	 * @author Andrea Vacondio
	 * Focus policy for cover_footer panel
	 *
	 */
	public class CoverFooterFocusPolicy extends FocusTraversalPolicy {
		public CoverFooterFocusPolicy(){
			super();
		}

		public Component getComponentAfter(Container CycleRootComp, Component aComponent){            
			if (aComponent.equals(add_file_button)){
				return remove_file_button;
			}
			else if (aComponent.equals(remove_file_button)){
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
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages,"Error: ")+ex.getMessage(), LogPanel.LOG_ERROR);
			}
		}
	}

}
