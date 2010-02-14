/*
 * Created on 18-Jun-2008
 * Copyright (C) 2008 by Andrea Vacondio.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.guiclient.business.PagePreviewOpener;
import org.pdfsam.guiclient.business.PagesWorker;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.VisualPdfSelectionActionListener;
import org.pdfsam.guiclient.commons.business.listeners.adapters.PageOpenerMouseAdapter;
import org.pdfsam.guiclient.commons.business.listeners.adapters.VisualPdfSelectionKeyAdapter;
import org.pdfsam.guiclient.commons.business.listeners.adapters.VisualPdfSelectionMouseAdapter;
import org.pdfsam.guiclient.commons.business.listeners.mediators.PagesActionsMediator;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.handlers.VisualListExportTransferHandler;
import org.pdfsam.guiclient.commons.dnd.handlers.VisualListTransferHandler;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.renderers.VisualListRenderer;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.Rotation;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.dto.VisualSelectedItem;
import org.pdfsam.i18n.GettextResource;

/**
 * Customizable panel for a visual page selection
 * @author Andrea Vacondio
 *
 */
public class JVisualPdfPageSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1384691784810385438L;

	private static final Logger log = Logger.getLogger(JVisualPdfPageSelectionPanel.class.getPackage().getName());
	
	public static final int HORIZONTAL_ORIENTATION = 1;
	public static final int VERTICAL_ORIENTATION = 2;
	
	public static final int SINGLE_INTERVAL_SELECTION = ListSelectionModel.SINGLE_INTERVAL_SELECTION;
	public static final int MULTIPLE_INTERVAL_SELECTION = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	public static final int SINGLE_SELECTION = ListSelectionModel.SINGLE_SELECTION;

	public static final int STYLE_TOP_PANEL_HIDE = 0;
	public static final int STYLE_TOP_PANEL_MINIMAL = 1;
	public static final int STYLE_TOP_PANEL_MEDIUM = 2;
	public static final int STYLE_TOP_PANEL_FULL = 3;
	
	public static final int DND_SUPPORT_NONE = 0;
	public static final int DND_SUPPORT_FILES = 1;
	public static final int DND_SUPPORT_JAVAOBJECTS = 2;
	public static final int DND_SUPPORT_FILES_AND_JAVAOBJECTS = 3;
	
	public static final String OUTPUT_PATH_PROPERTY = "defaultOutputPath";	

	private int orientation = HORIZONTAL_ORIENTATION;
	private File selectedPdfDocument = null;
	private String selectedPdfDocumentPassword = "";
	private boolean showButtonPanel = true;
	private int topPanelStyle = STYLE_TOP_PANEL_FULL;
	private boolean showContextMenu = true;
	private int dndSupport = DND_SUPPORT_NONE;
	private int selectionType = SINGLE_INTERVAL_SELECTION;
	private final JMenuItem menuItemSetOutputPath = new JMenuItem();
	/**
	 * if true, deleted items appear with a red cross over 
	 */
	private boolean drawDeletedItems = true;
	//if the JList uses wrap
	private boolean wrap = false;
	
	
	private Configuration config;
	private PagesWorker pagesWorker;
    //menu
	private final JButton loadFileButton = new JButton();
	private final JButton clearButton = new JButton();
	private final JButton zoomInButton = new JButton();
	private final JButton zoomOutButton = new JButton();
	
    private final JLabel documentProperties = new JLabel();    
    private final JVisualSelectionList thumbnailList = new JVisualSelectionList();
    private PdfThumbnailsLoader pdfLoader;
    private VisualPdfSelectionActionListener pdfSelectionActionListener;
    private PagesActionsMediator pageActionListener;
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem menuItemPreview = new JMenuItem();
	private final JPanel topPanel = new JPanel();
	
	//button panel
	private JPanel buttonPanel;
	private JButton undeleteButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JButton rotateButton;
    private JButton rotateAntiButton;
    private JButton reverseButton;

    /**
     * default constructor
     */
	public JVisualPdfPageSelectionPanel() {
		this(HORIZONTAL_ORIENTATION);
	}
	/**
	 * draw deleted items default value (true)
	 * show button panel default value (true)
	 * @param orientation panel orientation
	 */
	public JVisualPdfPageSelectionPanel(int orientation){
		this(orientation, true, true);
	}
	
	/**
	 * @param orientation panel orientation
	 * @param drawDeletedItems if true deleted items appear with a red cross over 
	 * @param showButtonPanel true=shows button panel
	 */
	public JVisualPdfPageSelectionPanel(int orientation, boolean drawDeletedItems, boolean showButtonPanel){
		this(orientation, drawDeletedItems, showButtonPanel, true, STYLE_TOP_PANEL_FULL);
	}

	/**
	 * 
	 * @param orientation panel orientation
	 * @param drawDeletedItems if true deleted items appear with a red cross over 
	 * @param showButtonPanel true=shows button panel
	 * @param showContextMenu
	 * @param topPanelStyle
	 */
	public JVisualPdfPageSelectionPanel(int orientation, boolean drawDeletedItems, boolean showButtonPanel, 
			boolean showContextMenu, int topPanelStyle){
		this(orientation, drawDeletedItems, showButtonPanel, showContextMenu, topPanelStyle, DND_SUPPORT_FILES_AND_JAVAOBJECTS, SINGLE_INTERVAL_SELECTION);
	}
    
	/**
	 * 
	 * @param orientation panel orientation
	 * @param drawDeletedItems if true deleted items appear with a red cross over 
	 * @param showButtonPanel true=shows button panel
	 * @param showContextMenu
	 * @param topPanelStyle top panel style
	 * @param selectionType selection type
	 */
	public JVisualPdfPageSelectionPanel(int orientation, boolean drawDeletedItems, boolean showButtonPanel, 
			boolean showContextMenu, int topPanelStyle, int dndSupport, int selectionType){
		this.orientation = orientation;
		this.config = Configuration.getInstance();
		this.pdfLoader = new PdfThumbnailsLoader(this);
		this.drawDeletedItems = drawDeletedItems;
		this.showButtonPanel = showButtonPanel;
		this.showContextMenu = showContextMenu;
		this.topPanelStyle = topPanelStyle;
		this.dndSupport = dndSupport;		
		this.selectionType = selectionType;
		init();		
	}
	/**
	 * panel initialization
	 */
	private void init(){
		setLayout(new GridBagLayout());
		
		thumbnailList.setDrawDeletedItems(drawDeletedItems);
		if(dndSupport == DND_SUPPORT_FILES){
			thumbnailList.setTransferHandler(new VisualListExportTransferHandler(pdfLoader));
		}else if(dndSupport == DND_SUPPORT_JAVAOBJECTS){
			thumbnailList.setTransferHandler(new VisualListTransferHandler());
		}else if(dndSupport == DND_SUPPORT_FILES_AND_JAVAOBJECTS){
			thumbnailList.setTransferHandler(new VisualListTransferHandler(pdfLoader));
		}else{
			thumbnailList.setTransferHandler(new VisualListExportTransferHandler(null));
		}
		thumbnailList.setDragEnabled(true);
		thumbnailList.setDropMode(DropMode.INSERT);
		pagesWorker = new PagesWorker(thumbnailList);
		thumbnailList.addKeyListener(new VisualPdfSelectionKeyAdapter(pagesWorker));
		thumbnailList.addMouseListener(new PageOpenerMouseAdapter(thumbnailList));
		
		if(showButtonPanel){
			initButtonPanel(pagesWorker);
			initKeyListener();
		}
		
		//JList orientation
		if(HORIZONTAL_ORIENTATION == orientation){
			thumbnailList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		}else{
			if(wrap){
				thumbnailList.setLayoutOrientation(JList.VERTICAL_WRAP);
			}
		}
				
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.setPreferredSize(new Dimension(400,30));
		
	    pdfSelectionActionListener = new VisualPdfSelectionActionListener(this, pdfLoader);
		if(topPanelStyle>=STYLE_TOP_PANEL_FULL){
		    //load button
			loadFileButton.setMargin(new Insets(1, 1, 1, 1));
			loadFileButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Open"));
			loadFileButton.setPreferredSize(new Dimension(100,30));
			loadFileButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Load a pdf document"));
			loadFileButton.setIcon(new ImageIcon(this.getClass().getResource("/images/add.png")));
			loadFileButton.addKeyListener(new EnterDoClickListener(loadFileButton));
			loadFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			loadFileButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			loadFileButton.setActionCommand(VisualPdfSelectionActionListener.ADD);
			loadFileButton.addActionListener(pdfSelectionActionListener);		
		}
		documentProperties.setIcon(new ImageIcon(this.getClass().getResource("/images/info.png")));
		documentProperties.setVisible(false);
		
				
		if(topPanelStyle>=STYLE_TOP_PANEL_MEDIUM){
			clearButton.setMargin(new Insets(1, 1, 1, 1));
			clearButton.setMinimumSize(new Dimension(30,30));
			clearButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Clear"));
			clearButton.setIcon(new ImageIcon(this.getClass().getResource("/images/clear.png")));
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					resetPanel();
				}
	        });
		}
		
		zoomInButton.setMargin(new Insets(1, 1, 1, 1));
		zoomInButton.setMinimumSize(new Dimension(30,30));
		zoomInButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Zoom in"));
		zoomInButton.setIcon(new ImageIcon(this.getClass().getResource("/images/zoomin.png")));
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {            
                try{
                	thumbnailList.incZoomLevel();
            		zoomOutButton.setEnabled(true);
                	if(thumbnailList.getCurrentZoomLevel() >= JVisualSelectionList.MAX_ZOOM_LEVEL){
                		zoomInButton.setEnabled(false);
                	} 
                	((VisualListModel)thumbnailList.getModel()).elementsChanged();
                }
                catch (Exception ex){
                    log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error setting zoom level."), ex); 
                }                
            }
        });		

		zoomOutButton.setMargin(new Insets(1, 1, 1, 1));
		zoomOutButton.setMinimumSize(new Dimension(30,30));
		zoomOutButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Zoom out"));
		zoomOutButton.setIcon(new ImageIcon(this.getClass().getResource("/images/zoomout.png")));
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {               
                try{
                	thumbnailList.deincZoomLevel();
                	zoomInButton.setEnabled(true);
                	if(thumbnailList.getCurrentZoomLevel() <= JVisualSelectionList.MIN_ZOOM_LEVEL){
                		zoomOutButton.setEnabled(false);
                	} 
                	((VisualListModel)thumbnailList.getModel()).elementsChanged();
                }
                catch (Exception ex){
                    log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error setting zoom level."), ex); 
                }                
            }
        });		

		thumbnailList.setModel(new VisualListModel());
		thumbnailList.setCellRenderer(new VisualListRenderer());
		thumbnailList.setVisibleRowCount(-1);
		thumbnailList.setSelectionMode(selectionType);		
		JScrollPane listScroller = new JScrollPane(thumbnailList);		

		//preview item	
		menuItemPreview.setIcon(new ImageIcon(this.getClass().getResource("/images/preview-viewer.png")));
		menuItemPreview.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Preview"));
		menuItemPreview.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	int[] selection = thumbnailList.getSelectedIndices();
            	if(selection!=null && selection.length==1){
            		VisualPageListItem item = (VisualPageListItem) thumbnailList.getModel().getElementAt(selection[0]);
            		PagePreviewOpener.getInstance().openPreview(item.getParentFileCanonicalPath(), item.getDocumentPassword(), item.getPageNumber());
            	}
            }
        });
		
		if(showContextMenu){
			//popup
			final JMenuItem menuItemMoveUp = new JMenuItem();
			menuItemMoveUp.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
			menuItemMoveUp.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Up"));
			menuItemMoveUp.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.MOVE_UP, pagesWorker));
			popupMenu.add(menuItemMoveUp);
			
			final JMenuItem menuItemMoveDown = new JMenuItem();
			menuItemMoveDown.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
			menuItemMoveDown.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Down"));
			menuItemMoveDown.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.MOVE_DOWN, pagesWorker));
			popupMenu.add(menuItemMoveDown);
			
			final JMenuItem menuItemRemove = new JMenuItem();
			menuItemRemove.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
			menuItemRemove.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Delete"));
			menuItemRemove.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.REMOVE, pagesWorker));
			popupMenu.add(menuItemRemove);
			
        	//if elements are physically deleted i don't need this item
        	if(drawDeletedItems){
				final JMenuItem menuItemUndelete = new JMenuItem();
				menuItemUndelete.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
				menuItemUndelete.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Undelete"));
				menuItemUndelete.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.UNDELETE, pagesWorker));
				popupMenu.add(menuItemUndelete);
        	}        	
    		
    		//rotate item	
    		final JMenuItem menuItemRotate = new JMenuItem();
    		menuItemRotate.setIcon(new ImageIcon(this.getClass().getResource("/images/clockwise.png")));
    		menuItemRotate.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate clockwise"));
    		menuItemRotate.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.ROTATE_CLOCK, pagesWorker));
    		popupMenu.add(menuItemRotate);
    		
    		//rotate anticlock item	
    		final JMenuItem menuItemAntiRotate = new JMenuItem();
    		menuItemAntiRotate.setIcon(new ImageIcon(this.getClass().getResource("/images/anticlockwise.png")));
    		menuItemAntiRotate.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate anticlockwise"));
    		menuItemAntiRotate.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.ROTATE_ANTICLOCK, pagesWorker));
    		popupMenu.add(menuItemAntiRotate);
    		
    		//reverse item	
    		final JMenuItem menuItemReverse = new JMenuItem();
    		menuItemReverse.setIcon(new ImageIcon(this.getClass().getResource("/images/reverse.png")));
    		menuItemReverse.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse"));
    		menuItemReverse.addMouseListener(new VisualPdfSelectionMouseAdapter(PagesWorker.REVERSE, pagesWorker));
    		popupMenu.add(menuItemReverse);
    		
        	enableSetOutputPathMenuItem();
        	
        	addPopupShower();
		}
		
		popupMenu.add(menuItemPreview);

		
		if(topPanelStyle>=STYLE_TOP_PANEL_FULL){
			topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			topPanel.add(loadFileButton);
		}
		if(topPanelStyle>=STYLE_TOP_PANEL_MEDIUM){
			topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			topPanel.add(clearButton);
		}
		topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		topPanel.add(documentProperties);
		topPanel.add(Box.createHorizontalGlue());
		topPanel.add(zoomInButton);
		topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		topPanel.add(zoomOutButton);
		
		GridBagConstraints topConstraints = new GridBagConstraints();
		topConstraints.fill = GridBagConstraints.BOTH  ;
		topConstraints.gridx=0;
		topConstraints.gridy=0;
		topConstraints.gridwidth=3;
		topConstraints.gridheight=1;
		topConstraints.insets = new Insets(5,5,5,5);
		topConstraints.weightx=1.0;
		topConstraints.weighty=0.0;		
		if(topPanelStyle>STYLE_TOP_PANEL_HIDE){
			add(topPanel, topConstraints);
		}

		GridBagConstraints thumbConstraints = new GridBagConstraints();
		thumbConstraints.fill = GridBagConstraints.BOTH;
		thumbConstraints.gridx=0;
		thumbConstraints.gridy=1;
		thumbConstraints.gridwidth=(showButtonPanel?2:3);
		thumbConstraints.gridheight=2;
		thumbConstraints.insets = new Insets(5,5,5,5);
		thumbConstraints.weightx=1.0;
		thumbConstraints.weighty=1.0;		
		add(listScroller, thumbConstraints);
		
		if(showButtonPanel){
			GridBagConstraints buttonsConstraints = new GridBagConstraints();
			buttonsConstraints.fill = GridBagConstraints.BOTH;
			buttonsConstraints.gridx=2;
			buttonsConstraints.gridy=1;
			buttonsConstraints.gridwidth=1;
			buttonsConstraints.gridheight=2;
			buttonsConstraints.insets = new Insets(5,5,5,5);
			buttonsConstraints.weightx=0.0;
			buttonsConstraints.weighty=1.0;		
			add(buttonPanel, buttonsConstraints);
		}
	}
	
	
	/**
     * adds a button to the button panel
     * @param button
     */
    private void addButtonToButtonPanel(JButton button){
    	button.setMinimumSize(new Dimension(90, 25));
    	button.setMaximumSize(new Dimension(160, 25));
    	buttonPanel.add(button);
		buttonPanel.add(Box.createRigidArea(new Dimension(0,5)));
    }
    
	private void initButtonPanel(PagesWorker pagesWorker){
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
	    pageActionListener = new PagesActionsMediator(pagesWorker);

	    //move up button
		moveUpButton = new JButton();
		moveUpButton.setMargin(new Insets(2, 2, 2, 2));
		moveUpButton.addActionListener(pageActionListener);        
		moveUpButton.setIcon(new ImageIcon(this.getClass().getResource("/images/up.png")));
		moveUpButton.setActionCommand(PagesWorker.MOVE_UP);
		moveUpButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Up"));
		moveUpButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Move up selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowUp)"));
		moveUpButton.addKeyListener(new EnterDoClickListener(moveUpButton));
		moveUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(moveUpButton);
		
    	//move down button
		moveDownButton = new JButton();
		moveDownButton.addActionListener(pageActionListener);
		moveDownButton.setIcon(new ImageIcon(this.getClass().getResource("/images/down.png")));
		moveDownButton.setActionCommand(PagesWorker.MOVE_DOWN);
		moveDownButton.setMargin(new Insets(2, 2, 2, 2));
		moveDownButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Move Down"));
		moveDownButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Move down selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowDown)"));
		moveDownButton.addKeyListener(new EnterDoClickListener(moveDownButton));
		moveDownButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(moveDownButton);
		
		//delete button
		removeButton = new JButton();
		removeButton.addActionListener(pageActionListener);
		removeButton.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
		removeButton.setActionCommand(PagesWorker.REMOVE);
		removeButton.setMargin(new Insets(2, 2, 2, 2));
		removeButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Delete"));
		removeButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Delete selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Canc)"));
		removeButton.addKeyListener(new EnterDoClickListener(removeButton));
		removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(removeButton);
		
		//undelete button
		if(drawDeletedItems){
			undeleteButton = new JButton();
			undeleteButton.addActionListener(pageActionListener);
			undeleteButton.setIcon(new ImageIcon(this.getClass().getResource("/images/remove.png")));
			undeleteButton.setActionCommand(PagesWorker.UNDELETE);
			undeleteButton.setMargin(new Insets(2, 2, 2, 2));
			undeleteButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Undelete"));
			undeleteButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Undelete selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Ctrl+Z)"));
			undeleteButton.addKeyListener(new EnterDoClickListener(undeleteButton));
			undeleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			addButtonToButtonPanel(undeleteButton);
		}
		
		//rotate button
		rotateButton = new JButton();
		rotateButton.addActionListener(pageActionListener);
		rotateButton.setIcon(new ImageIcon(this.getClass().getResource("/images/clockwise.png")));
		rotateButton.setActionCommand(PagesWorker.ROTATE_CLOCK);
		rotateButton.setMargin(new Insets(2, 2, 2, 2));
		rotateButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate right"));
		rotateButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate clockwise selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowRight)"));
		rotateButton.addKeyListener(new EnterDoClickListener(rotateButton));
		rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(rotateButton);

		//rotate button
		rotateAntiButton = new JButton();
		rotateAntiButton.addActionListener(pageActionListener);
		rotateAntiButton.setIcon(new ImageIcon(this.getClass().getResource("/images/anticlockwise.png")));
		rotateAntiButton.setActionCommand(PagesWorker.ROTATE_ANTICLOCK);
		rotateAntiButton.setMargin(new Insets(2, 2, 2, 2));
		rotateAntiButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate left"));
		rotateAntiButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Rotate anticlockwise selected pages")+" "+GettextResource.gettext(config.getI18nResourceBundle(),"(Alt+ArrowLeft)"));
		rotateAntiButton.addKeyListener(new EnterDoClickListener(rotateAntiButton));
		rotateAntiButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(rotateAntiButton);

		//reverse button
		reverseButton = new JButton();
		reverseButton.addActionListener(pageActionListener);
		reverseButton.setIcon(new ImageIcon(this.getClass().getResource("/images/reverse.png")));
		reverseButton.setActionCommand(PagesWorker.REVERSE);
		reverseButton.setMargin(new Insets(2, 2, 2, 2));
		reverseButton.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse"));
		reverseButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),"Reverse pages order"));
		reverseButton.addKeyListener(new EnterDoClickListener(reverseButton));
		reverseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButtonToButtonPanel(reverseButton);
	}
	
	private void initKeyListener(){
		//key listener
		thumbnailList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_UP)){
                	moveUpButton.doClick();
                } else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_DOWN)){
                	moveDownButton.doClick();
                } else if((e.getKeyCode() == KeyEvent.VK_DELETE)){
                	removeButton.doClick();
                } else if(drawDeletedItems && (e.isControlDown())&& (e.getKeyCode() == KeyEvent.VK_Z)){
                	undeleteButton.doClick();
                } else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_RIGHT)){
                	rotateButton.doClick();
                } else if((e.isAltDown())&& (e.getKeyCode() == KeyEvent.VK_LEFT)){
                	rotateAntiButton.doClick();
                }
            }
        });
	}
	/**
	 * reset the panel
	 */
	public void resetPanel(){
		thumbnailList.setCurrentZoomLevel(JVisualSelectionList.DEFAULT_ZOOM_LEVEL);
		zoomInButton.setEnabled(true);
		zoomOutButton.setEnabled(true);
		((VisualListModel)thumbnailList.getModel()).clearData();
		selectedPdfDocument = null;
		selectedPdfDocumentPassword = "";
		setDocumentPropertiesVisible(false);
		getPdfLoader().cleanCreator();
		
	}
	/**
	 * Set the visible the label that shows document properties
	 * @param visible
	 */
	public void setDocumentPropertiesVisible(boolean visible){
		documentProperties.setVisible(visible);
	}
	
	/**
	 * Set the document properties to be shown as a tooltip of the documentProperties JLabel
	 * @param documetnInfo bean containing document informations
	 */	
	
	public void setDocumentProperties(DocumentInfo documetnInfo){
		if(documetnInfo != null){
			String encrypted = documetnInfo.isEncrypted()?GettextResource.gettext(config.getI18nResourceBundle(),"Yes"):GettextResource.gettext(config.getI18nResourceBundle(),"No");
			documentProperties.setToolTipText( 
	    		"<html><body><b><p>"+GettextResource.gettext(config.getI18nResourceBundle(),"File: ")+"</b>"+documetnInfo.getFileName()+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Pages: ")+"</b>"+documetnInfo.getPages()+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version: ")+"</b>"+(documetnInfo.getPdfVersion()!=null? documetnInfo.getPdfVersion():"")+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Title: ")+"</b>"+StringUtils.trimToEmpty(documetnInfo.getDocumentMetaData().getTitle())+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Author: ")+"</b>"+StringUtils.trimToEmpty(documetnInfo.getDocumentMetaData().getAuthor())+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Creator: ")+"</b>"+StringUtils.trimToEmpty(documetnInfo.getDocumentMetaData().getCreator())+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Producer: ")+"</b>"+StringUtils.trimToEmpty(documetnInfo.getDocumentMetaData().getProducer())+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Encrypted: ")+"</b>"+encrypted+"</p>"+
	    		"</body></html>");
		}
		
	}
	/**
	 * @return the orientation
	 */
	public int getOrientation() {
		return orientation;
	}
	/**
	 * @return the selectedPdfDocument
	 */
	public File getSelectedPdfDocument() {
		return selectedPdfDocument;
	}
	/**
	 * @return the wrap
	 */
	public boolean isWrap() {
		return wrap;
	}
	/**
	 * @return the thumbnailList
	 */
	public JVisualSelectionList getThumbnailList() {
		return thumbnailList;
	}

	/**
	 * @param selectedPdfDocument the selectedPdfDocument to set
	 */
	public void setSelectedPdfDocument(File selectedPdfDocument) {
		this.selectedPdfDocument = selectedPdfDocument;
	}

	/**
	 * @return the dndSupport
	 */
	public int getDndSupport() {
		return dndSupport;
	}
	/**
	 * @return the drawDeletedItems
	 */
	public boolean isDrawDeletedItems() {
		return drawDeletedItems;
	}
	/**
	 * @param drawDeletedItems the drawDeletedItems to set
	 */
	public void setDrawDeletedItems(boolean drawDeletedItems) {
		this.drawDeletedItems = drawDeletedItems;
	}
	/**
	 * @return the pdfLoader
	 */
	public PdfThumbnailsLoader getPdfLoader() {
		return pdfLoader;
	}
	/**
	 * @return the topPanel
	 */
	public JPanel getTopPanel() {
		return topPanel;
	}
	
	/**
	 * @param pages input selection set
	 * @return a String version of the input Set, ready to be used as -u parameter for the console
	 */
	private String getSelectionString(Set<Integer> pages){
		StringBuilder buffer = new StringBuilder();
		for(Integer page : pages){
			buffer.append(page.toString()).append(",");
		}
		return StringUtils.chomp(buffer.toString(), ",");
    }

	/**
	 * 
	 * @return an ordered List of {@link VisualSelectedItem} corresponding to the panel elements.
	 */
    private List<VisualSelectedItem> getSelectedItemsList(){
            List<VisualSelectedItem> retVal = new ArrayList<VisualSelectedItem>();
            List<VisualPageListItem> validElements = ((VisualListModel)thumbnailList.getModel()).getValidElements();
            VisualSelectedItem tmpElement = null;
            Set<Integer> pages = new LinkedHashSet<Integer>();
            for(VisualPageListItem currentElement : validElements){
                    //first element
                    if(tmpElement == null){
                    		tmpElement = new VisualSelectedItem(currentElement.getParentFileCanonicalPath(), currentElement.getDocumentPassword());
                            pages.add(currentElement.getPageNumber());
                    }else{
                            //filename changed
                            if(!tmpElement.getSelectedFile().equals(currentElement.getParentFileCanonicalPath())){
                                    tmpElement.setPagesSelection(getSelectionString(pages));
                                    retVal.add(tmpElement);
                                    tmpElement = new VisualSelectedItem(currentElement.getParentFileCanonicalPath(), currentElement.getDocumentPassword());
                                    pages.clear();
                                    pages.add(currentElement.getPageNumber());
                            }else{
                                    //page already there
                                    if(!pages.add(currentElement.getPageNumber())){
                                            tmpElement.setPagesSelection(getSelectionString(pages));
                                            retVal.add(tmpElement);
                                            tmpElement = new VisualSelectedItem(currentElement.getParentFileCanonicalPath(), currentElement.getDocumentPassword());
                                            pages.clear();
                                            pages.add(currentElement.getPageNumber());
                                    }
                            }
                    }
            }
            tmpElement.setPagesSelection(getSelectionString(pages));
            retVal.add(tmpElement);
            return retVal;
    }
   
    /**
     * A of String List that can be used as input for the console. It contains the -f and -u parameters for this panel
     * @return
     */
	public List<String> getValidConsoleParameters(){
		List<String> retVal = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		for(VisualSelectedItem item : getSelectedItemsList()){
			builder.append(item.getPagesSelection()).append(":");
			retVal.add("-" + ConcatParsedCommand.F_ARG);
			if(StringUtils.isEmpty(item.getPassword())){
				retVal.add(item.getSelectedFile());
			}else{
				retVal.add(item.getSelectedFile()+":"+item.getPassword());
			}
		}
		retVal.add("-"+ConcatParsedCommand.U_ARG);
		retVal.add(builder.toString());
		return retVal;
	}
	
	/**
	 * 
	 * @return true if the panel has valid elements
	 */
	public boolean hasValidElements(){
		List<VisualPageListItem> elements = ((VisualListModel)thumbnailList.getModel()).getValidElements();
		return (elements != null && !elements.isEmpty());
	}
	
	/**
	 * @return a String that can be used as a -r param for the pdfsam-console
	 */
	public String getRotatedElementsString(){
		String retVal = "";
		Collection<VisualPageListItem> validElements = ((VisualListModel)thumbnailList.getModel()).getValidElements();
		if(validElements!=null && !validElements.isEmpty()){
			int i=0;
			for(VisualPageListItem currentElement : validElements){
				i++;
				if(!Rotation.DEGREES_0.equals(currentElement.getRotation())){
					retVal += i+":"+currentElement.getRotation().getDegrees()+","; 
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Add a component on the left of the top panel
	 * @param c
	 */
	public void addToTopPanel(Component c){
		topPanel.removeAll();
		/*TODO fix this*/
		if(c!=null){
			topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			topPanel.add(c);
		}
		if(topPanelStyle>=STYLE_TOP_PANEL_FULL){
			topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			topPanel.add(loadFileButton);
		}
		if(topPanelStyle>=STYLE_TOP_PANEL_MEDIUM){
			topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
			topPanel.add(clearButton);
		}
		topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		topPanel.add(documentProperties);
		topPanel.add(Box.createHorizontalGlue());
		topPanel.add(zoomInButton);
		topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		topPanel.add(zoomOutButton);
	}
	
	/**
	 * 
	 * @return Returns an array of all the selected values, in increasing order based on their indices in the list or an empty array if nothing is selected
	 */
	public VisualPageListItem[] getSelectedElements(){
		Object[] elems = thumbnailList.getSelectedValues();
		VisualPageListItem[] visElems = new VisualPageListItem[elems.length];
		System.arraycopy(elems, 0, visElems, 0, elems.length);
		return visElems;
	}
	
	/**
	 * @param c
	 * @see VisualListModel#appendAllElements(Collection)
	 */
	public void appendElements(Collection<VisualPageListItem> c){
		((VisualListModel)thumbnailList.getModel()).appendAllElements(c);
	}
	
	/**
	 * @param c
	 * @see VisualListModel#prependAllElements(Collection)
	 */
	public void prependElements(Collection<VisualPageListItem> c){
		((VisualListModel)thumbnailList.getModel()).prependAllElements(c);
	}
	/**
	 * Adds a item to the popup menu
	 * @param item
	 */
	public void addMenuItem(JMenuItem item){
		popupMenu.add(item);		
	}
	
	 /**
     * enables the set output path menu item
     */
    public void enableSetOutputPathMenuItem(){
    
			menuItemSetOutputPath.setIcon(new ImageIcon(this.getClass().getResource("/images/set_outfile.png")));
			menuItemSetOutputPath.setText(GettextResource.gettext(config.getI18nResourceBundle(),"Set output file"));
			menuItemSetOutputPath.addMouseListener(new MouseAdapter() {
	            public void mouseReleased(MouseEvent e) {
	                if (selectedPdfDocument != null){
	                    try{
	                    	String defaultOutputPath = selectedPdfDocument.getParent();
	                    	firePropertyChange(OUTPUT_PATH_PROPERTY, "", defaultOutputPath);
	                    }
	                    catch (Exception ex){
	                        log.error(GettextResource.gettext(config.getI18nResourceBundle(),"Error: Unable to get the file path."), ex); 
	                    }
	                }
	              }
	        });
			popupMenu.add(menuItemSetOutputPath);
			
			if(!showContextMenu){
				//show popup
				addPopupShower();
			}
    }
    /**
     * adds the listener that showes the popup
     */
    private void addPopupShower(){
    	//show popup
		thumbnailList.addMouseListener(new MouseAdapter() { 
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
					showMenu(e);
				}
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
					showMenu(e);
				}
            }
            private void showMenu(MouseEvent e) {
            	int[] selection = thumbnailList.getSelectedIndices();
            	if(!(selection!=null && selection.length>1)){
            		thumbnailList.setSelectedIndex(thumbnailList.locationToIndex(e.getPoint()) );
            		selection = thumbnailList.getSelectedIndices();
            	}
            	menuItemPreview.setEnabled(selection!=null && selection.length==1);
            	popupMenu.show(thumbnailList, e.getX(), e.getY() );
            }
        });
    }
    /**
     * remove the set ouput path menu item
     */
    public void disableSetOutputPathMenuItem(){
    	popupMenu.remove(menuItemSetOutputPath);
    }
	/**
	 * @return the selectedPdfDocumentPassword
	 */
	public String getSelectedPdfDocumentPassword() {
		return selectedPdfDocumentPassword;
	}
	/**
	 * @param selectedPdfDocumentPassword the selectedPdfDocumentPassword to set
	 */
	public void setSelectedPdfDocumentPassword(String selectedPdfDocumentPassword) {
		this.selectedPdfDocumentPassword = selectedPdfDocumentPassword;
	}
	/**
	 * @return the clearButton
	 */
	public JButton getClearButton() {
		return clearButton;
	}
	/**
	 * @return the zoomInButton
	 */
	public JButton getZoomInButton() {
		return zoomInButton;
	}
	/**
	 * @return the zoomOutButton
	 */
	public JButton getZoomOutButton() {
		return zoomOutButton;
	}
	/**
	 * @return the undeleteButton
	 */
	public JButton getUndeleteButton() {
		return undeleteButton;
	}
	/**
	 * @return the removeButton
	 */
	public JButton getRemoveButton() {
		return removeButton;
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
	 * @return the rotateButton
	 */
	public JButton getRotateButton() {
		return rotateButton;
	}
	/**
	 * @return the rotateAntiButton
	 */
	public JButton getRotateAntiButton() {
		return rotateAntiButton;
	}
	/**
	 * @return the loadFileButton
	 */
	public JButton getLoadFileButton() {
		return loadFileButton;
	}
	/**
	 * @return the reverseButton
	 */
	public JButton getReverseButton() {
		return reverseButton;
	}
 
}
