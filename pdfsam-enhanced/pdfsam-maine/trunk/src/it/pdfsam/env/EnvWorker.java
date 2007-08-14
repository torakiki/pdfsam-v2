/*
 * Created on 19-Dec-2006
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
package it.pdfsam.env;

import it.pdfsam.GUI.MainGUI;
import it.pdfsam.abstracts.AbstractPlugIn;
import it.pdfsam.abstracts.LogWriter;
import it.pdfsam.configuration.Configuration;
import it.pdfsam.gnu.gettext.GettextResource;
import it.pdfsam.panels.LogPanel;
import it.pdfsam.util.XmlFilter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
/**
 * Environment manipulation class
 * @author a.vacondio
 *
 */
public class EnvWorker implements LogWriter{

	private ResourceBundle i18n_messages;

	private JFileChooser file_chooser;

	private AbstractPlugIn[] pl_panel;
	
	private PropertyChangeSupport pcs;
	
	private String log_msg;

	public EnvWorker(AbstractPlugIn[] pl_panel) {
		this.pl_panel = pl_panel;		
		this.i18n_messages = Configuration.getInstance().getI18nResourceBundle();
		this.pcs = new PropertyChangeSupport(this);
		
		file_chooser = new JFileChooser();
		file_chooser.setFileFilter(new XmlFilter());
		file_chooser.setApproveButtonText(GettextResource.gettext(i18n_messages, "Save job"));
		file_chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	}

	/**
	 * Saves the environment
	 * 
	 */
	public void saveJob() {
		try{
			file_chooser.setApproveButtonText(GettextResource.gettext(i18n_messages, "Save job"));
			int return_val = file_chooser.showOpenDialog(null);
			File chosenFile = null;
			if (return_val == JFileChooser.APPROVE_OPTION) {
				chosenFile = file_chooser.getSelectedFile();
				if (chosenFile != null) {
					try {
						Document document = DocumentHelper.createDocument();
						Element root = document.addElement("pdfsam_saved_jobs");
						root.addAttribute("version", MainGUI.NAME);
						root.addAttribute("savedate", new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
						for (int i = 0; i < pl_panel.length; i++) {
							Element node = (Element) root.addElement("plugin");
							node.addAttribute("class", pl_panel[i].getClass().getName());
							node.addAttribute("name", pl_panel[i].getPluginName());
							pl_panel[i].getJobNode(node);
							fireLogPropertyChanged(GettextResource.gettext(i18n_messages, pl_panel[i].getPluginName()+ " job node loaded."),
									LogPanel.LOG_DEBUG);
						}
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(chosenFile));
						OutputFormat format = OutputFormat.createPrettyPrint();
						format.setEncoding("UTF-8");
						XMLWriter xml_file_writer = new XMLWriter(bos, format);
						xml_file_writer.write(document);
						xml_file_writer.flush();
						xml_file_writer.close();
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Job saved."),
								LogPanel.LOG_INFO);
					} catch (Exception ex) {
						fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error: ") + ex.getMessage(),
								LogPanel.LOG_ERROR);

					}
				}
			}
		}
		catch(RuntimeException re){
			fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "RuntimeError:")
					+ " Unable to load environment. "+re.getMessage(), LogPanel.LOG_ERROR);			
		}
		catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error:")
					+ " Unable to load environment. "+e.getMessage(), LogPanel.LOG_ERROR);
		}
	}

	/**
	 * loads environment
	 */
	public void loadJobs() {
		file_chooser.setApproveButtonText(GettextResource.gettext(i18n_messages, "Load environment"));
		int return_val = file_chooser.showOpenDialog(null);
		if (return_val == JFileChooser.APPROVE_OPTION) {
			final File chosen_file = file_chooser.getSelectedFile();
			loadJobs(chosen_file); 
		}
	}

	/**
	 * loads environment
	 * 
	 * @param chosen_file
	 *            path to the xml file to load
	 */
	private void loadJobs(File chosen_file) {
		try{
			if (chosen_file != null && chosen_file.canRead() && chosen_file.exists()) {
				try {
					SAXReader reader = new SAXReader();
					Document document = reader.read(chosen_file);
					for (int i = 0; i < pl_panel.length; i++) {
						Node node = document.selectSingleNode("/pdfsam_saved_jobs/plugin[@class=\""
								+ pl_panel[i].getClass().getName() + "\"]");
						pl_panel[i].loadJobNode(node);
					}
				} catch (Exception ex) {
					fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error: ") + ex.getMessage(),
							LogPanel.LOG_ERROR);
				}
			} else {
				fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error:")
						+ " Unable to load environment", LogPanel.LOG_ERROR);
			}
		}
		catch(Exception e){
			fireLogPropertyChanged(GettextResource.gettext(i18n_messages, "Error:")
					+ " Unable to load environment", LogPanel.LOG_ERROR);
		}
	}

	/**
	 * loads environment
	 * 
	 * @param file_path
	 *            path to the xml file to load
	 */
	public void loadJobs(String file_path) {
		File chosen_file = new File(file_path);
		loadJobs(chosen_file);

	}
	
	/**
	 * delegated
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
	    pcs.addPropertyChangeListener(listener);
	}
	/**
	 * delegated
	 * @param listener
	 */	 
	public void addPropertyChangeListener(String porpertyName, PropertyChangeListener listener){
	    pcs.addPropertyChangeListener(porpertyName, listener);
	}	
	/**
	 * delegated
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
    	pcs.removePropertyChangeListener(listener); 	
    }
	/**
	 * delegated
	 * @param listener
	 */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    	pcs.removePropertyChangeListener(propertyName, listener);    	
    }

	public void fireLogPropertyChanged(String log_msg, int log_level) {
        this.log_msg = log_msg;
        pcs.firePropertyChange("LOG", -1, log_level);		
	}

	public String getLogMsg() {
		return this.log_msg;
	}

}
