/*
 * Created on 27-DEC-2008
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
package org.pdfsam.plugin.split.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.pdfsam.console.utils.PdfUtility;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;
/**
 * Panel with the bookmarks level combo and the button to fill it
 * @author Andrea Vacondio
 *
 */
public class JBLevelCombo extends JPanel {
	
	private static final long serialVersionUID = -1413124292303614507L;
	
	private static final Logger LOG = Logger.getLogger(JBLevelCombo.class.getPackage().getName());
	
	private JComboBox levelCombo;
	private JButton fillCombo;
	private JPdfSelectionPanel inputPanel;
	private Thread filler = null;
	
	public JBLevelCombo(JPdfSelectionPanel inputPanel) {
		this.inputPanel = inputPanel;
		init();
	}
	
	private void init(){
		levelCombo = new JComboBox();
		levelCombo.setPreferredSize(new Dimension(45,20));
		fillCombo = new JButton();
		
		fillCombo.setText("< "+GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Fill from document"));
		levelCombo.setEditable(true);
		
		fillCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				try {
						PdfSelectionTableItem[] items = inputPanel.getTableRows();
						if (items != null && items.length == 1) {
							if(filler == null){								 
								 filler = new Thread(new FillThread(items[0]));
								 filler.start();
							}
						} 
						else {
							JOptionPane.showMessageDialog(getParent(), GettextResource.gettext(Configuration.getInstance()
									.getI18nResourceBundle(), "Please select a pdf document."), GettextResource.gettext(
											Configuration.getInstance().getI18nResourceBundle(), "Warning"), JOptionPane.WARNING_MESSAGE);
						}
				} catch (Exception ex) {
					LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
				}
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(Box.createHorizontalGlue());
		add(levelCombo);
		add(Box.createRigidArea(new Dimension(10, 0)));
		add(fillCombo);

		
	}
	/**
	 * Fills the levels combo
	 * @author Andrea Vacondio
	 *
	 */
	private class FillThread implements Runnable {
		
		private PdfSelectionTableItem item;
		
		/**
		 * @param item
		 */
		public FillThread(PdfSelectionTableItem item) {
			super();
			this.item = item;
		}

		public void run() {
			fillCombo.setEnabled(false);
			levelCombo.setEnabled(false);
			fillCombo.setToolTipText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Getting bookmarks max depth"));		
			try {
				levelCombo.removeAllItems();
				byte[] password = null;
				if((item.getPassword()) != null && (item.getPassword()).length()>0){
					password = item.getPassword().getBytes();
				}
				PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(item.getInputFile().getAbsolutePath()),password);				
				pdfReader.consolidateNamedDestinations();
				List bookmarks = SimpleBookmark.getBookmark(pdfReader);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				SimpleBookmark.exportToXML(bookmarks, out, "UTF-8", false);
				ByteArrayInputStream input = new ByteArrayInputStream(out.toByteArray());			
				int maxDepth = PdfUtility.getMaxBookmarksDepth(input);
				for(int i = 1; i<=maxDepth; i++){
					levelCombo.addItem(i+"");
				}
			} catch (Throwable t) {
				LOG.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Unable to retrieve bookmarks maximum depth."), t);
			} finally {
				fillCombo.setEnabled(true);
				levelCombo.setEnabled(true);
				fillCombo.setToolTipText(null);
				filler = null;
			}

		}

	}
	
	public void resetComponent(){
		levelCombo.removeAllItems();
		setEnabled(isEnabled());
	}

	/**
	 * @return selected index
	 * @see javax.swing.JComboBox#getSelectedIndex()
	 */
	public int getSelectedIndex() {
		return levelCombo.getSelectedIndex();
	}

	/**
	 * @return selected item
	 * @see javax.swing.JComboBox#getSelectedItem()
	 */
	public Object getSelectedItem() {
		return levelCombo.getSelectedItem();
	}

	/**
	 * 
	 * @see javax.swing.JComboBox#removeAllItems()
	 */
	public void removeAllItems() {
		levelCombo.removeAllItems();
	}

	/**
	 * @param anObject
	 * @see javax.swing.JComboBox#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object anObject) {
		levelCombo.setSelectedItem(anObject);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		levelCombo.setEnabled(enabled);
		fillCombo.setEnabled(enabled);			
	}

	/**
	 * @return the levelCombo
	 */
	public JComboBox getLevelCombo() {
		return levelCombo;
	}

	/**
	 * @return the fillCombo
	 */
	public JButton getFillCombo() {
		return fillCombo;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#requestFocus()
	 */
	public void requestFocus() {
		levelCombo.requestFocus();
	}
	
	
}
