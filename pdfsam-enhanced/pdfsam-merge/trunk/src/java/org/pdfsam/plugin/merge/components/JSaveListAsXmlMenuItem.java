/*
 * Created on 15-Apr-2008
 * Copyright (C) 20078 by Andrea Vacondio.
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
package org.pdfsam.plugin.merge.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.utils.filters.XmlFilter;
import org.pdfsam.i18n.GettextResource;
/**
 * Menu item to add the "save as xml" capability to the JPdfSelectionPanel
 * @author Andrea Vacondio
 */
public class JSaveListAsXmlMenuItem extends JMenuItem {

	private static final long serialVersionUID = -2401309408949258117L;

	private static final Logger log = Logger.getLogger(JSaveListAsXmlMenuItem.class.getPackage().getName());
	
	private JPdfSelectionPanel selectionPanel;
	private JFileChooser fileChooser = null;;

	/**
	 * @param selectionPanel
	 */
	public JSaveListAsXmlMenuItem(JPdfSelectionPanel selectionPanel) {
		super();
		this.selectionPanel = selectionPanel;
		this.init();
	}
	
	/**
	 * initialize
	 */
	private void init(){
		setIcon(new ImageIcon(this.getClass().getResource("/images/saveXml.png")));
		setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Export as xml"));
		addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
            	PdfSelectionTableItem[] rows = selectionPanel.getTableRows();
                if (rows != null && rows.length>0){
                    try{
                    	lazyInitJFileChooser();
                    	if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	                    	File selectedFile = fileChooser.getSelectedFile();	
	        				if(selectedFile.getName().toLowerCase().lastIndexOf(".xml") == -1){
	        					selectedFile = new File(selectedFile.getParent(), selectedFile.getName()+".xml");
	        				}
	        				writeXmlFile(rows, selectedFile);
						}
                    }
                    catch (Exception ex){
                        log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to save xml file."), ex); 
                    }
                }
              }
        });
	}
	
	/**
	 * lazily initialize the JFileChooser
	 */
	private void lazyInitJFileChooser(){
		if(fileChooser == null){
			fileChooser = new JFileChooser(Configuration.getInstance().getDefaultWorkingDir());
			fileChooser.setFileFilter(new XmlFilter());
			fileChooser.setApproveButtonText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Ok"));
		}
	}
	
	/**
	 * Save the xml file
	 * @param rows
	 * @param selectedFile
	 * @throws Exception
	 */
	public void writeXmlFile(PdfSelectionTableItem[] rows, File selectedFile) throws Exception{
			if (selectedFile != null && rows != null){
					Document document = DocumentHelper.createDocument();
					Element root = document.addElement("filelist");					
					for (int i = 0; i<rows.length; i++) {
						PdfSelectionTableItem row = rows[i];
						Element node = (Element) root.addElement("file");
						node.addAttribute("value", row.getInputFile().getAbsolutePath());
						String pwd = row.getPassword();
						if(pwd != null && pwd.length()>0){
							node.addAttribute("password", pwd);
						}
					}
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(selectedFile));
					OutputFormat format = OutputFormat.createPrettyPrint();
					format.setEncoding("UTF-8");
					XMLWriter xmlWriter = new XMLWriter(bos, format);
					xmlWriter.write(document);
					xmlWriter.flush();
					xmlWriter.close();				
					log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "File xml saved."));
			}else{
				log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error saving xml file, output file is null."));
			}		
	}
}
