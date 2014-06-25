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
import java.awt.dnd.DropTarget;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.commons.business.actions.AddSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.ClearSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.DocumentPropertiesSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.MoveDownSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.MoveUpSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.ReloadDocumentSelectionTableAction;
import org.pdfsam.guiclient.commons.business.actions.RemoveSelectionTableAction;
import org.pdfsam.guiclient.commons.business.listeners.adapters.PdfSelectionMouseHeaderAdapter;
import org.pdfsam.guiclient.commons.business.listeners.adapters.TableShowPopupMouseAdapter;
import org.pdfsam.guiclient.commons.business.loaders.PdfLoader;
import org.pdfsam.guiclient.commons.components.JPdfSelectionTable;
import org.pdfsam.guiclient.commons.components.JPdfSelectionToolTipHeader;
import org.pdfsam.guiclient.commons.dnd.droppers.JPdfSelectionTableDropper;
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
 * 
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

	private boolean showRemoveButton = false;

	private boolean showMoveButtons = false;

	private int maxSelectableFiles = 0;

	private int showedColums;

	private final JPdfSelectionTable mainTable = new JPdfSelectionTable();

	private AbstractPdfSelectionTableModel tableModel;

	private final JList workInProgressList = new JList();

	private JScrollPane tableScrollPane;

	private JScrollPane wipListScrollPane;

	private Configuration config;

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

	private boolean setOutputPathMenuItemEnabled = false;

	/**
	 * default constructor shows every button and permits an unlimited number of
	 * selected input documents
	 */
	public JPdfSelectionPanel() {
		this(UNLIMTED_SELECTABLE_FILE_NUMBER, SimplePdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true);
	}

	/**
	 * showEveryButton is true if maxSelectableFiles is > 1
	 * 
	 * @param maxSelectableFiles
	 * @param showedColums
	 */
	public JPdfSelectionPanel(int maxSelectableFiles, int showedColums) {
		this(maxSelectableFiles, showedColums, (maxSelectableFiles > 1));
	}

	/**
	 * @param maxSelectableFiles
	 * @param showedColums
	 * @param showEveryButton
	 *            if true shows every button, if false hide remove button and
	 *            move buttons
	 */
	public JPdfSelectionPanel(int maxSelectableFiles, int showedColums, boolean showEveryButton) {
		this(maxSelectableFiles, showedColums, showEveryButton, false, false);
	}

	/**
	 * @param maxSelectableFiles
	 * @param showedColums
	 * @param showRemoveButton
	 *            if true shows the remove button
	 * @param showMoveButtons
	 *            if true shows the move buttons
	 */
	public JPdfSelectionPanel(int maxSelectableFiles, int showedColums, boolean showRemoveButton,
			boolean showMoveButtons) {
		this(maxSelectableFiles, showedColums, false, showRemoveButton, showMoveButtons);
	}

	/**
	 * Full constructor
	 * 
	 * @param maxSelectableFiles
	 * @param showedColums
	 * @param showEveryButton
	 * @param showRemoveButton
	 * @param showMoveButtons
	 */
	private JPdfSelectionPanel(int maxSelectableFiles, int showedColums, boolean showEveryButton,
			boolean showRemoveButton, boolean showMoveButtons) {
		this.config = Configuration.getInstance();
		this.maxSelectableFiles = maxSelectableFiles;
		this.showedColums = showedColums;
		this.showEveryButton = showEveryButton;
		this.showRemoveButton = showRemoveButton;
		this.showMoveButtons = showMoveButtons;
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

	private void init() {
		setLayout(new GridBagLayout());

		if (maxSelectableFiles > 1) {
			tableModel = new SortablePdfSelectionTableModel(showedColums, maxSelectableFiles);
		} else {
			tableModel = new SimplePdfSelectionTableModel(showedColums, maxSelectableFiles);
		}
		mainTable.setModel(tableModel);
		mainTable.setDragEnabled(true);
		mainTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		mainTable.setRowHeight(20);
		mainTable.setRowMargin(5);
		mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		mainTable.setSelectionForeground(Color.BLACK);
		mainTable.setSelectionBackground(new Color(211, 221, 222));
		mainTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		mainTable.setGridColor(Color.LIGHT_GRAY);
		mainTable.setIntercellSpacing(new Dimension(3, 3));
		mainTable.setDefaultRenderer(String.class, new JPdfSelectionTableRenderer());

		TableColumnModel mainTableColModel = mainTable.getColumnModel();
		mainTableColModel.getColumn(AbstractPdfSelectionTableModel.PASSWORD).setCellEditor(
				new DefaultCellEditor(new JPasswordField()));

		TableColumn tc = mainTableColModel.getColumn(AbstractPdfSelectionTableModel.ROW_NUM);
		tc.setPreferredWidth(25);
		tc.setMaxWidth(35);

		// header tooltip
		JPdfSelectionToolTipHeader toolTipHeader = new JPdfSelectionToolTipHeader(mainTableColModel);
		toolTipHeader.setReorderingAllowed(false);
		toolTipHeader.setToolTips(tableModel.getToolTips());
		mainTable.setTableHeader(toolTipHeader);
		if (maxSelectableFiles > 1) {
			toolTipHeader.setDefaultRenderer(new ArrowHeaderRenderer(tableModel, toolTipHeader.getDefaultRenderer()));
			toolTipHeader.addMouseListener(new PdfSelectionMouseHeaderAdapter(tableModel));
		}

		tableScrollPane = new JScrollPane(mainTable);
		tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// drag and drop
		JPdfSelectionTableDropper dropper = new JPdfSelectionTableDropper(loader);
		tableDropTarget = new DropTarget(tableScrollPane, dropper);
		scrollPanelDropTarget = new DropTarget(mainTable, dropper);

		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		initAddButton();

		ReloadDocumentSelectionTableAction reloadAction = new ReloadDocumentSelectionTableAction(mainTable, loader);
		popupMenu.add(new JMenuItem(reloadAction));
		addToMainTableKeyBindings(reloadAction);
		DocumentPropertiesSelectionTableAction documentPropertiesAction = new DocumentPropertiesSelectionTableAction(mainTable);
		popupMenu.add(new JMenuItem(documentPropertiesAction));
		addToMainTableKeyBindings(documentPropertiesAction);

		if (showEveryButton) {
			initRemoveButton();
			initMoveUpButton();
			initMoveDownButton();
		} else {
			if (showRemoveButton) {
				initRemoveButton();
			}
			if (showMoveButtons) {
				initMoveUpButton();
				initMoveDownButton();
			}
		}

		initClearButton();

		mainTable.addMouseListener(new TableShowPopupMouseAdapter(popupMenu, mainTable));

		// work in progress list
		workInProgressList.setFocusable(false);
		workInProgressList.setModel(new DefaultListModel());
		workInProgressList.setBackground(this.getBackground());
		wipListScrollPane = new JScrollPane(workInProgressList);
		int wipHeight = 30;
		if (isSingleSelectableFile()) {
			wipHeight = 18;
		}
		wipListScrollPane.setMaximumSize(new Dimension(1500, wipHeight));
		wipListScrollPane.setPreferredSize(new Dimension(700, wipHeight));
		wipListScrollPane.setMinimumSize(new Dimension(300, wipHeight));
		wipListScrollPane.setBorder(BorderFactory.createEmptyBorder());

		GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.fill = GridBagConstraints.BOTH;
		tableConstraints.gridx = 0;
		tableConstraints.gridy = 0;
		tableConstraints.gridwidth = 2;
		tableConstraints.gridheight = 2;
		tableConstraints.insets = new Insets(5, 5, 5, 5);
		tableConstraints.weightx = 1.0;
		tableConstraints.weighty = 1.0;
		add(tableScrollPane, tableConstraints);

		GridBagConstraints buttonsConstraints = new GridBagConstraints();
		buttonsConstraints.fill = GridBagConstraints.BOTH;
		buttonsConstraints.gridx = 2;
		buttonsConstraints.gridy = 0;
		buttonsConstraints.gridwidth = 1;
		buttonsConstraints.gridheight = 1;
		buttonsConstraints.insets = new Insets(5, 5, 5, 5);
		buttonsConstraints.weightx = 0.0;
		buttonsConstraints.weighty = 1.0;
		add(buttonPanel, buttonsConstraints);

		GridBagConstraints wipConstraints = new GridBagConstraints();
		wipConstraints.fill = GridBagConstraints.BOTH;
		wipConstraints.gridx = 0;
		wipConstraints.gridy = 2;
		wipConstraints.gridwidth = 3;
		wipConstraints.gridheight = 1;
		wipConstraints.insets = new Insets(1, 5, 1, 5);
		wipConstraints.weightx = 1.0;
		wipConstraints.weighty = 0.0;
		add(wipListScrollPane, wipConstraints);
	}

	/**
	 * add a text to say the user we are working
	 */
	public synchronized void addWipText(final String wipText) {

		Runnable runner = new Runnable() {
			public void run() {
				((DefaultListModel) workInProgressList.getModel()).addElement(wipText);
			}
		};
		SwingUtilities.invokeLater(runner);
	}

	/**
	 * remove the text to say the user we are working
	 */
	public synchronized void removeWipText(final String wipText) {

		Runnable runner = new Runnable() {
			public void run() {
				if (!((DefaultListModel) workInProgressList.getModel()).removeElement(wipText)) {
					log.debug(GettextResource.gettext(config.getI18nResourceBundle(), "Unable to remove JList text ")
							+ wipText);
				}
			}
		};
		SwingUtilities.invokeLater(runner);
	}

	/**
	 * removes every element from the list
	 */
	public synchronized void removeWipTextAll() {

		Runnable runner = new Runnable() {
			public void run() {
				((DefaultListModel) workInProgressList.getModel()).removeAllElements();
			}
		};
		SwingUtilities.invokeLater(runner);
	}

	/**
	 * @return true if some thread is loading a pdf document
	 */
	public boolean isAdding() {
		return loader.isExecuting();
	}

	/**
	 * adds a item to the table
	 * 
	 * @param item
	 */
	public synchronized void addTableRow(PdfSelectionTableItem item) {
		((AbstractPdfSelectionTableModel) mainTable.getModel()).addRow(item);
		log.info(GettextResource.gettext(config.getI18nResourceBundle(), "File selected: ")
				+ item.getInputFile().getName());
	}

	/**
	 * update an item to the table
	 * 
	 * @param index
	 * @param item
	 */
	public synchronized void updateTableRow(int index, PdfSelectionTableItem item) {
		((AbstractPdfSelectionTableModel) mainTable.getModel()).updateRowAt(index, item);
		log.info(GettextResource.gettext(config.getI18nResourceBundle(), "File reloaded: ")
				+ item.getInputFile().getName());
	}

	/**
	 * adds a button to the button panel
	 * 
	 * @param button
	 */
	private void addButtonToButtonPanel(JButton button) {
		button.setMargin(new Insets(2, 2, 2, 2));
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		button.setMinimumSize(new Dimension(120, 25));
		button.setMaximumSize(new Dimension(160, 25));
		addEnterKeyBinding(button);
		buttonPanel.add(button);
		buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
	}

	/**
	 * binds the enter key to the action when the button has focus
	 * 
	 * @param button
	 */
	private void addEnterKeyBinding(JButton button) {
		Action action = button.getAction();
		if (action != null) {
			button.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action.getValue(Action.NAME));
			button.getActionMap().put(action.getValue(Action.NAME), action);
		}
	}

	/**
	 * @return rows of the model
	 */
	public PdfSelectionTableItem[] getTableRows() {
		return ((AbstractPdfSelectionTableModel) mainTable.getModel()).getRows();
	}

	/**
	 * binds the accelerator key of the action to the main table
	 * 
	 * @param action
	 */
	private void addToMainTableKeyBindings(Action action) {
		if (action != null) {
			Object keyStroke = action.getValue(Action.ACCELERATOR_KEY);
			if (keyStroke != null) {
				KeyStroke stroke = (KeyStroke) keyStroke;
				mainTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(stroke, action.getValue(Action.NAME));
				mainTable.getActionMap().put(action.getValue(Action.NAME), action);
			}
		}
	}

	/**
	 * initialize the remove button
	 */
	private void initRemoveButton() {
		// remove button
		RemoveSelectionTableAction removeAction = new RemoveSelectionTableAction(mainTable);
		removeFileButton.setAction(removeAction);
		addButtonToButtonPanel(removeFileButton);
		addToMainTableKeyBindings(removeAction);
		popupMenu.add(new JMenuItem(removeAction));
	}

	/**
	 * initialize the add button
	 */
	private void initAddButton() {
		// add button
		AddSelectionTableAction addAction = new AddSelectionTableAction(loader, (maxSelectableFiles == 1));
		addFileButton.setAction(addAction);
		addButtonToButtonPanel(addFileButton);
		addToMainTableKeyBindings(addAction);

	}

	/**
	 * initialize the moveUpButton
	 */
	private void initMoveUpButton() {
		// move up button
		MoveUpSelectionTableAction moveUpAction = new MoveUpSelectionTableAction(mainTable);
		moveUpButton.setAction(moveUpAction);
		addButtonToButtonPanel(moveUpButton);
		addToMainTableKeyBindings(moveUpAction);
		popupMenu.add(new JMenuItem(moveUpAction));
	}

	/**
	 * initialize the move down button
	 */
	private void initMoveDownButton() {
		// move down button
		MoveDownSelectionTableAction moveDownAction = new MoveDownSelectionTableAction(mainTable);
		moveDownButton.setAction(moveDownAction);
		addButtonToButtonPanel(moveDownButton);
		addToMainTableKeyBindings(moveDownAction);
		popupMenu.add(new JMenuItem(moveDownAction));
	}

	/**
	 * initialize the clear button
	 */
	private void initClearButton() {
		// clear button
		ClearSelectionTableAction clearAction = new ClearSelectionTableAction(mainTable);
		clearButton.setAction(clearAction);
		addButtonToButtonPanel(clearButton);
		addToMainTableKeyBindings(clearAction);
	}

	/**
	 * @return the pdf loader
	 */
	public PdfLoader getLoader() {
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

	/**
	 * @return the setOutputPathMenuItemEnabled
	 */
	public boolean isSetOutputPathMenuItemEnabled() {
		return setOutputPathMenuItemEnabled;
	}

	/**
	 * @return true if it's a single selectable file panel
	 */
	public boolean isSingleSelectableFile() {
		return (SINGLE_SELECTABLE_FILE == maxSelectableFiles);
	}

	/**
	 * @param action
	 *            adds an item to the popup menu for the given action
	 * 
	 */
	public void addPopupMenuAction(AbstractAction action) {
		if (action != null) {
			addToMainTableKeyBindings(action);
			popupMenu.add(new JMenuItem(action));
		}
	}

	/**
	 * If true, the selection table default renderer will show a tooltip message
	 * when the document is not opened with full permissions
	 * 
	 * @param required
	 */
	public void setFullAccessRequired(boolean required) {
		mainTable.setDefaultRenderer(String.class, new JPdfSelectionTableRenderer(required));
	}

	/**
	 * Clear the selection table
	 */
	public void clearSelectionTable() {
		((AbstractPdfSelectionTableModel) getMainTable().getModel()).clearData();
	}
}
