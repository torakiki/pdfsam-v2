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
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.guiclient.business.PagesWorker;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.VisualPdfSelectionActionListener;
import org.pdfsam.guiclient.commons.business.listeners.adapters.PageOpenerMouseAdapter;
import org.pdfsam.guiclient.commons.business.listeners.adapters.VisualPdfSelectionKeyAdapter;
import org.pdfsam.guiclient.commons.business.listeners.adapters.VisualPdfSelectionMouseAdapter;
import org.pdfsam.guiclient.commons.business.listeners.mediators.PagesActionsMediator;
import org.pdfsam.guiclient.commons.business.loaders.PdfThumbnailsLoader;
import org.pdfsam.guiclient.commons.components.JVisualSelectionList;
import org.pdfsam.guiclient.commons.dnd.droppers.JVisualSelectionListDropper;
import org.pdfsam.guiclient.commons.dnd.handlers.VisualSelectionListTransferHandler;
import org.pdfsam.guiclient.commons.models.VisualListModel;
import org.pdfsam.guiclient.commons.renderers.VisualListRenderer;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.VisualPageListItem;
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
	
	public static int SINGLE_INTERVAL_SELECTION = ListSelectionModel.SINGLE_INTERVAL_SELECTION;
	public static int MULTIPLE_INTERVAL_SELECTION = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	public static int SINGLE_SELECTION = ListSelectionModel.SINGLE_SELECTION;

	public static int STYLE_TOP_PANEL_HIDE = 0;
	public static int STYLE_TOP_PANEL_MINIMAL = 1;
	public static int STYLE_TOP_PANEL_MEDIUM = 2;
	public static int STYLE_TOP_PANEL_FULL = 3;
	
	public static final String OUTPUT_PATH_PROPERTY = "defaultOutputPath";	
	
	private int orientation = HORIZONTAL_ORIENTATION;
	private File selectedPdfDocument = null;
	private String selectedPdfDocumentPassword = "";
	private boolean showButtonPanel = true;
	private int topPanelStyle = STYLE_TOP_PANEL_FULL;
	private boolean acceptDropFromDifferentComponents = true;
	private boolean showContextMenu = true;
	private boolean canImportFile = true;
	private boolean canImportListObject = true;
	private int selectionType = SINGLE_INTERVAL_SELECTION;
	private final JMenuItem menuItemSetOutputPath = new JMenuItem();
	
	/**
	 * if true deleted items appear with a red cross over 
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
    private DropTarget scrollPanelDropTarget;
    private PdfThumbnailsLoader pdfLoader;
    private VisualPdfSelectionActionListener pdfSelectionActionListener;
    private PagesActionsMediator pageActionListener;
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JPanel topPanel = new JPanel();
	
	//button panel
	private JPanel buttonPanel;
	private JButton undeleteButton;
    private JButton removeButton;
    private JButton moveUpButton;
    private JButton moveDownButton;

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
		this(orientation, drawDeletedItems, showButtonPanel, false, true, STYLE_TOP_PANEL_FULL);
	}

	/**
	 * 
	 * @param orientation panel orientation
	 * @param drawDeletedItems if true deleted items appear with a red cross over 
	 * @param showButtonPanel true=shows button panel
	 * @param acceptDropFromDifferentComponents if true accepts dropping items from other components
	 * @param showContextMenu
	 * @param topPanelStyle
	 */
	public JVisualPdfPageSelectionPanel(int orientation, boolean drawDeletedItems, boolean showButtonPanel, boolean acceptDropFromDifferentComponents, 
			boolean showContextMenu, int topPanelStyle){
		this(orientation, drawDeletedItems, showButtonPanel, acceptDropFromDifferentComponents, showContextMenu, topPanelStyle, true, true, SINGLE_INTERVAL_SELECTION);
	}
    
	/**
	 * 
	 * @param orientation panel orientation
	 * @param drawDeletedItems if true deleted items appear with a red cross over 
	 * @param showButtonPanel true=shows button panel
	 * @param acceptDropFromDifferentComponents if true accepts dropping items from other components
	 * @param showContextMenu
	 * @param topPanelStyle top panel style
	 * @param canImportFile true if can accept file drop
	 * @param canImportListObject true if can accept JVM object
	 * @param selectionType selection type
	 */
	public JVisualPdfPageSelectionPanel(int orientation, boolean drawDeletedItems, boolean showButtonPanel, boolean acceptDropFromDifferentComponents, 
			boolean showContextMenu, int topPanelStyle, boolean canImportFile, boolean canImportListObject, int selectionType){
		this.orientation = orientation;
		this.config = Configuration.getInstance();
		this.pdfLoader = new PdfThumbnailsLoader(this);
		this.drawDeletedItems = drawDeletedItems;
		this.showButtonPanel = showButtonPanel;
		this.showContextMenu = showContextMenu;
		this.acceptDropFromDifferentComponents = acceptDropFromDifferentComponents;
		this.topPanelStyle = topPanelStyle;
		this.canImportFile = canImportFile;
		this.canImportListObject = canImportListObject;
		this.selectionType = selectionType;
		init();		
	}
	/**
	 * panel initialization
	 */
	private void init(){
		setLayout(new GridBagLayout());
		
		thumbnailList.setDrawDeletedItems(drawDeletedItems);
		thumbnailList.setTransferHandler(new VisualSelectionListTransferHandler(pdfLoader, acceptDropFromDifferentComponents, canImportFile, canImportListObject));
		thumbnailList.setDragEnabled(true);
		pagesWorker = new PagesWorker(thumbnailList);
		thumbnailList.addKeyListener(new VisualPdfSelectionKeyAdapter(pagesWorker));
		thumbnailList.addMouseListener(new PageOpenerMouseAdapter(thumbnailList));
		
		if(showButtonPanel){
			initButtonPanel(pagesWorker);
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
		
		if(canImportFile){
			JVisualSelectionListDropper dropper = new JVisualSelectionListDropper(pdfLoader);
			scrollPanelDropTarget = new DropTarget(listScroller,dropper);
		}
		
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
        	
        	enableSetOutputPathMenuItem();
        	
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
	            	popupMenu.show(thumbnailList, e.getX(), e.getY() );
	            }
	        });
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
    	button.setMaximumSize(new Dimension(100, 25));
    	button.setPreferredSize(new Dimension(95, 25));
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
	 * @param properties
	 */	
	
	public void setDocumentProperties(DocumentInfo documetnInfo){
		if(documetnInfo != null){
		String encrypted = GettextResource.gettext(config.getI18nResourceBundle(),"No");
		if(documetnInfo.isEncrypted()){
			encrypted = GettextResource.gettext(config.getI18nResourceBundle(),"Yes");
		}
		documentProperties.setToolTipText( 
	    		"<html><body><b><p>"+GettextResource.gettext(config.getI18nResourceBundle(),"File: ")+"</b>"+documetnInfo.getFileName()+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Pages: ")+"</b>"+documetnInfo.getPages()+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Pdf version: ")+"</b>"+(documetnInfo.getPdfVersion()!=null? documetnInfo.getPdfVersion():"")+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Title: ")+"</b>"+(documetnInfo.getTitle()!=null? documetnInfo.getAuthor():"")+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Author: ")+"</b>"+(documetnInfo.getAuthor()!=null? documetnInfo.getAuthor():"")+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Creator: ")+"</b>"+(documetnInfo.getCreator()!=null? documetnInfo.getCreator():"")+"</p>"+
	    		"<p><b>"+GettextResource.gettext(config.getI18nResourceBundle(),"Producer: ")+"</b>"+(documetnInfo.getProducer()!=null? documetnInfo.getProducer():"")+"</p>"+
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
	 * @return the scrollPanelDropTarget
	 */
	public DropTarget getScrollPanelDropTarget() {
		return scrollPanelDropTarget;
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
	 * 
	 * @return valid elements as a String that can be used as "-u" parameter for the concat console command. Empty String if no valid elements.
	 */
	public String getValidElementsString(){
		String retVal = "";
		Collection<VisualPageListItem> validElements = ((VisualListModel)thumbnailList.getModel()).getValidElements();
		if(validElements!=null && validElements.size()>0){
			StringBuffer buffer = new StringBuffer();
			VisualPageListItem startElement = null;
			VisualPageListItem endElement = null;
			VisualPageListItem previousElement = null;
			for(VisualPageListItem currentElement : validElements){
				if(previousElement == null){
					previousElement = currentElement;
				}
				//time to start a new section
				if(startElement==null){					
					startElement = currentElement;
					endElement = currentElement;
				}else{
					//let's check if it's the next number or not
					if(currentElement.getPageNumber() == (endElement.getPageNumber()+1) && previousElement.getParentFileCanonicalPath().equals(currentElement.getParentFileCanonicalPath())){
						endElement = currentElement;
					}else{
						if (buffer.length()>0 && !buffer.toString().endsWith(":")){
							buffer = buffer.append(',');
						}
						if(startElement.getPageNumber() == endElement.getPageNumber()){
							//just the page number
							buffer = buffer.append(startElement.getPageNumber());
						}else{
							//page range
							buffer = buffer.append(startElement.getPageNumber()).append('-').append(endElement.getPageNumber());
						}
						if(!previousElement.getParentFileCanonicalPath().equals(currentElement.getParentFileCanonicalPath())){
							buffer = buffer.append(':');
						}
						startElement = currentElement;
						endElement = currentElement;
						previousElement = currentElement;
					}
				}
			}
			//check the last elements
			if (buffer.length()>0 && !buffer.toString().endsWith(":")){
				buffer = buffer.append(',');
			}
			if(startElement.getPageNumber() == endElement.getPageNumber()){
				buffer = buffer.append(startElement.getPageNumber());
			}else{
				buffer = buffer.append(startElement.getPageNumber()).append('-').append(endElement.getPageNumber());
			}
			retVal = buffer.append(':').toString();
		}
		return retVal;
	}
	
	/**
	 * 
	 * @return a Collection that can be used as -f parameters string 
	 */
	public Collection<String> getValidElementsFiles(){
		LinkedList<String> retVal = new LinkedList<String>(); 
		Collection<VisualPageListItem> validElements = ((VisualListModel)thumbnailList.getModel()).getValidElements();
		if(validElements!=null && validElements.size()>0){
			VisualPageListItem previousElement = null;
			for(VisualPageListItem currentElement : validElements){
				if(previousElement == null){
					previousElement = currentElement;
				}
				if(!currentElement.getParentFileCanonicalPath().equals(previousElement.getParentFileCanonicalPath())){
					retVal.add("-" + ConcatParsedCommand.F_ARG);
					String fileElem =((previousElement.getDocumentPassword()!=null && previousElement.getDocumentPassword().length()>0)? previousElement.getParentFileCanonicalPath():previousElement.getParentFileCanonicalPath()+":"+previousElement.getDocumentPassword());
					retVal.add(fileElem);
					previousElement = currentElement;
				}
			}
			retVal.add("-" + ConcatParsedCommand.F_ARG);
			String fileElem =((previousElement.getDocumentPassword()!=null && previousElement.getDocumentPassword().length()>0)? previousElement.getParentFileCanonicalPath():previousElement.getParentFileCanonicalPath()+":"+previousElement.getDocumentPassword());
			retVal.add(fileElem);
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
		            	popupMenu.show(thumbnailList, e.getX(), e.getY() );
		            }
		        });
			}
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
 
    	
}
