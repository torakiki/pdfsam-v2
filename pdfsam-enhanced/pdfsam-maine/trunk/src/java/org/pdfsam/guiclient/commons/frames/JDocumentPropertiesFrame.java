/*
 * Created on 18-Mar-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.guiclient.commons.frames;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultEditorKit;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.business.actions.HideFrameAction;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.i18n.GettextResource;

/**
 * Frame that shows document properties
 * 
 * @author Andrea Vacondio
 * 
 */
public class JDocumentPropertiesFrame extends JFrame {

	private static final long serialVersionUID = -3836869268177748519L;

	private static final Logger log = Logger.getLogger(JDocumentPropertiesFrame.class.getPackage().getName());

	private static final int HEIGHT = 480;

	private static final int WIDTH = 640;

	private static JDocumentPropertiesFrame instance = null;

	private final JPanel mainPanel = new JPanel();

	private JScrollPane mainScrollPanel;

	private JTextPane textInfoArea;

	private JPopupMenu jPopupMenu = new JPopupMenu();

	private JDocumentPropertiesFrame() {
		initialize();
	}

	public static synchronized JDocumentPropertiesFrame getInstance() {
		if (instance == null) {
			instance = new JDocumentPropertiesFrame();
		}
		return instance;
	}

	public synchronized void showProperties(PdfSelectionTableItem props) {
		StringBuilder sb = new StringBuilder();
		if (props != null) {
			sb.append("<html><head></head><body style=\"margin: 3\"><b>");
			sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File name"));
			sb.append(":</b> ");
			sb.append(props.getInputFile().getAbsolutePath());
			sb.append("<br>\n<b>");
			sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Number of pages"));
			sb.append(":</b> ");
			sb.append(props.getPagesNumber());
			sb.append("<br>\n<b>");
			sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File size"));
			sb.append(":</b> ");
			sb.append(props.getFileSize());
			sb.append("B<br>\n<b>");
			sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Pdf version"));
			sb.append(":</b> ");
			sb.append(props.getPdfVersionDescription());
			sb.append("<br>\n<b>");
			sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Encryption"));
			sb.append(":</b> ");
			sb.append((props.isEncrypted() ? props.getEncryptionAlgorithm() : GettextResource.gettext(Configuration
					.getInstance().getI18nResourceBundle(), "Not encrypted")));
			sb.append("<br>\n");
			if (props.isEncrypted()) {
				sb.append("<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Permissions"));
				sb.append(":</b> ");
				sb.append(props.getPermissions());
				sb.append("<br>\n");
			}

			if (props.getDocumentMetaData() != null) {
				sb.append("<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Title"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getTitle());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Author"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getAuthor());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Subject"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getSubject());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Producer"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getProducer());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Creator"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getCreator());
				sb.append("<br>\n<b>");
				sb
						.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
								"Creation date"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getCreationDate());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
						"Modification date"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getModificationDate());
				sb.append("<br>\n<b>");
				sb.append(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Keywords"));
				sb.append(":</b> ");
				sb.append(props.getDocumentMetaData().getKeywords());
				sb.append("<br>\n");
			}
			sb.append("</body></html>");
			textInfoArea.setMargin(new Insets(5, 5, 5, 5));
			textInfoArea.setText(sb.toString());
			setVisible(true);
		}
	}

	private void initialize() {
		try {
			URL iconUrl = this.getClass().getResource("/images/info.png");
			setTitle(GettextResource
					.gettext(Configuration.getInstance().getI18nResourceBundle(), "Document properties"));
			setIconImage(new ImageIcon(iconUrl).getImage());
			setSize(WIDTH, HEIGHT);
			setExtendedState(JFrame.NORMAL);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			JMenuBar menuBar = new JMenuBar();
			JMenu menuFile = new JMenu();
			menuFile.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File"));
			menuFile.setMnemonic(KeyEvent.VK_F);

			JMenuItem closeItem = new JMenuItem();
			closeItem.setAction(new HideFrameAction(this));

			menuFile.add(closeItem);
			menuBar.add(menuFile);
			getRootPane().setJMenuBar(menuBar);

			textInfoArea = new JTextPane();
			textInfoArea.setFont(new Font("Dialog", Font.PLAIN, 9));
			textInfoArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			textInfoArea.setContentType("text/html");
			textInfoArea.setEditable(false);

			mainPanel.add(textInfoArea);
			mainScrollPanel = new JScrollPane(textInfoArea);

			getContentPane().add(mainScrollPanel);

			JMenuItem menuCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
			menuCopy.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Copy"));
			menuCopy.setIcon(new ImageIcon(this.getClass().getResource("/images/edit-copy.png")));
			jPopupMenu.add(menuCopy);

			textInfoArea.setComponentPopupMenu(jPopupMenu);

			// centered
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension screenSize = tk.getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			if (screenWidth > WIDTH && screenHeight > HEIGHT) {
				setLocation((screenWidth - WIDTH) / 2, (screenHeight - HEIGHT) / 2);
			}

		} catch (Exception e) {
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
					"Error creating properties panel."), e);
		}
	}

}
