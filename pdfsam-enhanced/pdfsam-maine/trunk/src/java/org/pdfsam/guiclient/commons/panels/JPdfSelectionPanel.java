/*
 * Created on 18-Nov-2007
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
package org.pdfsam.guiclient.commons.panels;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.PdfFileDropper;
import org.pdfsam.guiclient.commons.business.PdfLoader;
import org.pdfsam.guiclient.commons.business.listeners.PdfSelectionMouseHeaderAdapter;
import org.pdfsam.guiclient.commons.business.listeners.PdfSelectionTableActionListener;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.components.JPdfSelectionToolTipHeader;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.models.SimplePdfSelectionTableModel;
import org.pdfsam.guiclient.commons.models.SortablePdfSelectionTableModel;
import org.pdfsam.guiclient.commons.renderers.ArrowHeaderRenderer;
import org.pdfsam.guiclient.commons.renderers.JPdfSelectionTableRenderer;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;
/**
 * Customizable Panel for the selection of pdf documents
 * @author Andrea Vacondio
 */
public class JPdfSelectionPanel extends JPanel {

	private static final long serialVersionUID = 7231708747828566035L;

	private static final Logger log = Logger.getLogger(JPdfSelectionPanel.class.getPackage().getName());
	
	public static final int UNLIMTED_SELECTABLE_FILE_NUMBER = Integer.MAX_VALUE;
	public static final int SINGLE_SELECTABLE_FILE = 1;
	public static final int DOUBLE_SELECTABLE_FILE = 2;
	
	public static final String OUTPUT_PATH_PROPERTY = "defaultOutputPath";
	
	private boolean showEveryButton = false;
	private int maxSelectableFiles = 0;
	private int showedColums;
	private String defaultOutputPath = "";
	
	private final JPdfSelectionTable mainTable = new JPdfSelectionTable();
	private AbstractPdfSelectionTableModel tableModel;
	private final JList workInProgressList = new JList();
	private JScrollPane tableScrollPane;
	private JScrollPane wipListScrollPane;
	private Configuration config;
	private PdfSelectionTableActionListener pdfSelectionTableListener;
	private final JPanel buttonPanel = new JPanel();
	private final JPopupMenu popupMenu = new JPopupMenu();
	private PdfLoader loader = null;
	private DropTarget tableDropTarget;
	private DropTarget scrollPanelDropTarget;
	
	private final JButton addFileButton = new JButton();
    private final JButton removeFileButton = new JButton();
    private final JButton moveUpButton = new JButton();
    private final JButton moveDownButton = new JButton();
    private final JButton clearButton = new JButton();
    
    //keylisteners
    private final EnterDoClickListener addEnterKeyListener = new EnterDoClickListener(addFileButton);
    private final EnterDoClickListener removeEnterKeyListener = new EnterDoClickListener(removeFileButton);
    private final EnterDoClickListener moveuEnterKeyListener = new EnterDoClickListener(moveUpButton);
    private final EnterDoClickListener movedEnterKeyListener = new EnterDoClickListener(moveDownButton);
    private final EnterDoClickListener clearEnterKeyListener = new EnterDoClickListener(clearButton);
	
	
	/**
	 * default constructor shows every button and permits an unlimited number of selected input documents
	 */
	public JPdfSelectionPanel(){
		this(UNLIMTED_SELECTABLE_FILE_NUMBER, SimplePdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true);
	}
	
	/**
	 * showEveryButton is true if maxSelectableFiles is > 1
	 * @param maxSelectableFiles
	 * @param showedColums
	 */
	public JPdfSelectionPanel(int maxSelectableFiles, int showedColums){
		this(maxSelectableFiles,showedColums, (maxSelectableFiles>1));
	}
	
	public JPdfSelectionPanel(int maxSelectableFiles, int showedColums, boolean showEveryButton){
		this.config = Configuration.getInstance();
		this.maxSelectableFiles = maxSelectableFiles;
		this.showedColums = showedColums;
		this.showEveryButton = showEveryButton;
		loader = new PdfLoader(this);
		init();
	}

	/**
	 * @return the showEveryButton
	 */
	public boolean isShowEveryButton() {
		return showEveryButton;
	}

	/**
	 * @return the maxSelectableFiles
	 */
	public int getMaxSelectableFiles() {
		return maxSelectableFiles;
	}

	/**
	 * @return the mainTable
	 */
	public JPdfSelectionTable getMainTable() {
		return mainTable;
	}
	
	private void init(){
		setLayout(new GridBagLayout());
				
		if(maxSelectableFiles>1){
			tableModel = new SortablePdfSelectionTableModel(showedColums, maxSelectableFiles);
		}else{
			tableModel = new SimplePdfSelectionTableModel(showedColums, maxSelectableFiles);
		}
		mainTable.setModel(tableModel);
		mainTable.setDragEnabled(true);
		mainTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		mainTable.setRowHeight(20);
		mainTable.setRowMargin(5);
		mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		mainTable.setSelectionForeground(Color.BLACK);
		mainTable.setSelectionBackground(new Color(211, 221, 222));
		mainTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mainTable.setGridColor(Color.LIGHT_GRAY);
		mainTable.setIntercellSpacing(new Dimension(3, 3));
		mainTable.setDefaultRenderer(String.class, new JPdfSelectionTableRenderer());
		
		TableColumnModel mainTableColModel = mainTable.getColumnModel();
	    mainTableColModel.getColumn(AbstractPdfSelectionTableModel.PASSWORD).setCellEditor(new DefaultCellEditor(new JPasswordField()));
	    
	    //header tooltip
	    JPdfSelectionToolTipHeader toolTipHeader = new JPdfSelectionToolTipHeader(mainTableColModel);
	    toolTipHeader.setReorderingAllowed(false);
	    toolTipHeader.setToolTips(tableModel.getToolTips());
	    mainTable.setTableHeader(toolTipHeader); 
	    if(maxSelectableFiles>1){
		    toolTipHeader.setDefaultRenderer(new ArrowHeaderRenderer(tableModel, toolTipHeader.getDefaultRenderer()));
	    	toolTipHeader.addMouseListener(new PdfSelectionMouseHeaderAdapter(tableModel));
		}
	    
	    tableScrollPane = new JScrollPane(mainTable);
	    tableScrollPane.setPreferredSize(new Dimension(600,350));
	    tableScrollPane.setMinimumSize(new Dimension(200,50));
	    tableScrollPane.setMaximumSize(new Dimension(1500,800));
		pdfSelectionTableListener = new PdfSelectionTableActionListener(this, loader);
		
		//drag and drop
		PdfFileDropper dropper = new PdfFileDropper(loader);
		tableDropTarget = new DropTarget(tableScrollPane,dropper);
		scrollPanelDropTarget = new DropTarget(mainTable, dropper);
        
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		//add button
		addFileButton.setMargin(new Insets(2, 2, 2, 2));
		addFileButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Add a pdf to the list"));
		addFileButton.setIcon(new ImageIcon(this.getClass().getResource("/images/add.png")));
		if(maxSelectableFiles>1){
			addFileButton.setActionCommand(PdfSelectionTableActionListener.ADD);
		}else{
			addFileButton.setActionCommand(PdfSelectionTableActionListener.ADDSINGLE);
		}
		addFileButton.addActionListener(pdfSelectionTableListener);
		addFileButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Add"));
		addFileButton.addKeyListener(addEnterKeyListener);
		addFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(addFileButton);
		
		//remove button
		removeFileButton.setMargin(new Insets(2, 2, 2, 2));
		removeFileButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Remove a pdf from the list")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Canc)"));
		removeFileButton.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
		removeFileButton.setActionCommand(PdfSelectionTableActionListener.REMOVE);
		removeFileButton.addActionListener(pdfSelectionTableListener);
        removeFileButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Remove"));
        removeFileButton.addKeyListener(removeEnterKeyListener);
        removeFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(removeFileButton);
		addButtonToButtonPanel(removeFileButton);
		
		final JMenuItem menuItemRemove = new JMenuItem();
		menuItemRemove.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
		menuItemRemove.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Remove"));
		menuItemRemove.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	removeFileButton.doClick(0);
            }
	    });
		popupMenu.add(menuItemRemove);
		
		if(showEveryButton){
			//move up button
			moveUpButton.setMargin(new Insets(2, 2, 2, 2));
			moveUpButton.addActionListener(pdfSelectionTableListener);        
			moveUpButton.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
			moveUpButton.setActionCommand(PdfSelectionTableActionListener.MOVE_UP);
			moveUpButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Up"));
			moveUpButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Move up selected pdf file")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowUp)"));
			moveUpButton.addKeyListener(moveuEnterKeyListener);
			moveUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(moveUpButton);
			addButtonToButtonPanel(moveUpButton);
			final JMenuItem menuItemMoveUp = new JMenuItem();
			menuItemMoveUp.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
			menuItemMoveUp.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Up"));
			menuItemMoveUp.addMouseListener(new MouseAdapter() {
	            public void mouseReleased(MouseEvent e) {
	            	moveUpButton.doClick(0);
	            }
	        });
			popupMenu.add(menuItemMoveUp);
			
			//move down button
			moveDownButton.addActionListener(pdfSelectionTableListener);
			moveDownButton.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
			moveDownButton.setActionCommand(PdfSelectionTableActionListener.MOVE_DOWN);
			moveDownButton.setMargin(new Insets(2, 2, 2, 2));
			moveDownButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Down"));
			moveDownButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Move down selected pdf file")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowDown)"));
			moveDownButton.addKeyListener(movedEnterKeyListener);
			moveDownButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(moveDownButton);
			addButtonToButtonPanel(moveDownButton);
			final JMenuItem menuItemMoveDown = new JMenuItem();
	        menuItemMoveDown.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
	        menuItemMoveDown.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Down"));
	        menuItemMoveDown.addMouseListener(new MouseAdapter() {
	            public void mouseReleased(MouseEvent e) {
	            	moveDownButton.doClick(0);
	            }
	        });
	        popupMenu.add(menuItemMoveDown);
	        
			//clear button
			clearButton.addActionListener(pdfSelectionTableListener);
			clearButton.setIcon(new ImageIcon(this.getClass().getResource("/images/clear.png")));
			clearButton.setActionCommand(PdfSelectionTableActionListener.CLEAR);
			clearButton.setMargin(new Insets(2, 2, 2, 2));
			clearButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear"));
			clearButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Remove every pdf file from the merge list"));
			clearButton.addKeyListener(clearEnterKeyListener);
			clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(clearButton);
			addButtonToButtonPanel(clearButton);
			
			//set out file popup
			final JMenuItem menuItemSetOutputPath = new JMenuItem();
			menuItemSetOutputPath.setIcon(new ImageIcon(this.getClass().getResource("/images/set_outfile.png")));
			menuItemSetOutputPath.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Set output file"));
			menuItemSetOutputPath.addMouseListener(new MouseAdapter() {
	            public void mouseReleased(MouseEvent e) {
	                if (mainTable.getSelectedRow() != -1){
	                    try{
	                    	String previousValue = defaultOutputPath;
	                    	defaultOutputPath = ((AbstractPdfSelectionTableModel) mainTable.getModel()).getRow(mainTable.getSelectedRow()).getInputFile().getParent();
	                    	firePropertyChange(OUTPUT_PATH_PROPERTY, previousValue, defaultOutputPath);
	                    }
	                    catch (Exception ex){
	                        log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: Unable to get the file path."), ex); 
	                    }
	                }
	              }
	        });
			popupMenu.add(menuItemSetOutputPath);
			
			//key listener
			mainTable.addKeyListener(new KeyAdapter() {
	            public void keyPressed(KeyEvent e) {
	                if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_UP)){
	                	moveUpButton.doClick();
	                }
	                else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_DOWN)){
	                	moveDownButton.doClick();
	                }
	                else if((e.getKeyCode() == KeyEvent.VK_DELETE)){
	                	removeFileButton.doClick();
	                }
	            }
	        });
		}else{
			//key listener
			mainTable.addKeyListener(new KeyAdapter() {
	            public void keyPressed(KeyEvent e) {
	                if((e.getKeyCode() == KeyEvent.VK_DELETE)){
	                	removeFileButton.doClick();
	                }
	            }
	        });
		}
				
		
		mainTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    showMenu(e);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    showMenu(e);
            }
            private void showMenu(MouseEvent e) {
                popupMenu.show(mainTable, e.getX(), e.getY());
                Point p = e.getPoint();
                mainTable.setRowSelectionInterval(mainTable.rowAtPoint(p), mainTable.rowAtPoint(p));
            }
        });
		
		//work in progress list
		workInProgressList.setFocusable(false);
		workInProgressList.setModel(new DefaultListModel()); 
		workInProgressList.setBackground(this.getBackground());
		wipListScrollPane = new JScrollPane(workInProgressList);
		int wipHeight = 30;
		if(SINGLE_SELECTABLE_FILE == maxSelectableFiles){
			wipHeight = 18;
		}
		wipListScrollPane.setMaximumSize(new Dimension(1500, wipHeight));
		wipListScrollPane.setPreferredSize(new Dimension(700,wipHeight));
		wipListScrollPane.setMinimumSize(new Dimension(300,wipHeight));		
		wipListScrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.fill = GridBagConstraints.BOTH;
		tableConstraints.gridx=0;
		tableConstraints.gridy=0;
		tableConstraints.gridwidth=2;
		tableConstraints.gridheight=2;
		tableConstraints.insets = new Insets(5,5,5,5);
		tableConstraints.weightx=1.0;
		tableConstraints.weighty=1.0;		
		add(tableScrollPane, tableConstraints);
		
		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsConstraints.fill = GridBagConstraints.BOTH;
		buttonsConstraints.gridx=2;
		buttonsConstraints.gridy=0;
		buttonsConstraints.gridwidth=1;
		buttonsConstraints.gridheight=1;
		buttonsConstraints.insets = new Insets(5,5,5,5);
		buttonsConstraints.weightx=0.0;
		buttonsConstraints.weighty=1.0;		
		add(buttonPanel, buttonsConstraints);		

		GridBagConstraints wipConstraints = new GridBagConstraints();
		wipConstraints.fill = GridBagConstraints.BOTH;
		wipConstraints.gridx=0;
		wipConstraints.gridy=2;
		wipConstraints.gridwidth=3;
		wipConstraints.gridheight=1;
		wipConstraints.insets = new Insets(1,5,1,5);
		wipConstraints.weightx=1.0;
		wipConstraints.weighty=0.0;		
		add(wipListScrollPane, wipConstraints);
	}
	
	/**
     * add a text to say the user we are working
     */
    public synchronized void addWipText(final String wipText){
        
        Runnable runner = new Runnable() {
            public void run() {
            	((DefaultListModel)workInProgressList.getModel()).addElement(wipText);
            }
        };
        SwingUtilities.invokeLater(runner);
    }
    /**
     * remove the text to say the user we are working
     */
    public synchronized void removeWipText(final String wipText){
        
        Runnable runner = new Runnable() {
            public void run() {
            	if(!((DefaultListModel)workInProgressList.getModel()).removeElement(wipText)){
            		log.debug(GettextResource.gettext(config.getI18nResourceBundle(),"Unable to remove JList text ")+wipText);
            	}
            }
        };
        SwingUtilities.invokeLater(runner);
    }
    /**
     * removes every element from the list
     */
    public synchronized void removeWipTextAll(){
        
        Runnable runner = new Runnable() {
            public void run() {
            	((DefaultListModel)workInProgressList.getModel()).removeAllElements();
            }
        };
        SwingUtilities.invokeLater(runner);
    }

    /**
     * @return true if some thread is loading a pdf document
     */
    public boolean isAdding(){
    	return (loader.getRunningThreadsNumber()>0);
    }
    /**
     * adds a item to the table
     * @param item
     */
    public synchronized void addTableRow(PdfSelectionTableItem item){
    	((AbstractPdfSelectionTableModel)mainTable.getModel()).addRow(item);
        log.info(GettextResource.gettext(config.getI18nResourceBundle(),"File selected: ")+item.getInputFile().getName());
    }

    /**
     * adds a button to the button panel
     * @param button
     */
    private void addButtonToButtonPanel(JButton button){
    	button.setMinimumSize(new Dimension(90, 25));
    	button.setMaximumSize(new Dimension(100, 25));
    	button.setPreferredSize(new Dimension(95, 25));
    	buttonPanel.add(button);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
    }
    
    /**
     * @return rows of the model
     */
    public PdfSelectionTableItem[] getTableRows(){
    	return ((AbstractPdfSelectionTableModel)mainTable.getModel()).getRows();
    }
    
    /**
     * @return the pdf loader
     */
    public PdfLoader getLoader(){
    	return loader;
    }

	/**
	 * @return the addFileButton
	 */
	public JButton getAddFileButton() {
		return addFileButton;
	}

	/**
	 * @return the removeFileButton
	 */
	public JButton getRemoveFileButton() {
		return removeFileButton;
	}

	/**
	 * @return the moveUpButton
	 */
	public JButton getMoveUpButton() {
		return moveUpButton;
	}

	/**
	 * @return the moveDownButton
	 */
	public JButton getMoveDownButton() {
		return moveDownButton;
	}

	/**
	 * @return the clearButton
	 */
	public JButton getClearButton() {
		return clearButton;
	}

	/**
	 * @return the tableDropTarget
	 */
	public DropTarget getTableDropTarget() {
		return tableDropTarget;
	}

	/**
	 * @return the scrollPanelDropTarget
	 */
	public DropTarget getScrollPanelDropTarget() {
		return scrollPanelDropTarget;
	}
    
    
}
