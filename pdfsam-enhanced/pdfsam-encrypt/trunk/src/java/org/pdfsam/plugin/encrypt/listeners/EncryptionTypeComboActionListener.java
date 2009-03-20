/*
 * Created on 03-Jan-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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
package org.pdfsam.plugin.encrypt.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.utils.EncryptionUtility;
import org.pdfsam.plugin.encrypt.GUI.EncryptMainGUI;
/**
 * Listener to enable/disable versionCombo items depending on the encryption algorithm selected
 * @author Andrea Vacondio
 *
 */
public class EncryptionTypeComboActionListener implements ItemListener {

	private JCheckBox allowAllCheck;
	private JCheckBox[] permissionsCheck;
	private JPdfVersionCombo versionCombo;
	
	private static final Integer aesFilter = new Integer(""+AbstractParsedCommand.VERSION_1_6);
	private static final Integer rc4_128Filter = new Integer(""+AbstractParsedCommand.VERSION_1_4);
	
	public EncryptionTypeComboActionListener(JCheckBox allowAllCheck, JCheckBox[] permissionsCheck, JPdfVersionCombo versionCombo){
		this.allowAllCheck = allowAllCheck;
		this.permissionsCheck = permissionsCheck;
		this.versionCombo = versionCombo;
	}
	
	public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED){
			if(!allowAllCheck.isSelected()){
				Object source = e.getSource();
				if(source instanceof JComboBox){
					String encType = (String)e.getItem();
					if(encType.equals(EncryptionUtility.RC4_40)){
				        permissionsCheck[EncryptMainGUI.PRINT].setEnabled(true);
				        permissionsCheck[EncryptMainGUI.DPRINT].setEnabled(false);
				        permissionsCheck[EncryptMainGUI.COPY].setEnabled(true);
				        permissionsCheck[EncryptMainGUI.MODIFY].setEnabled(true);
				        permissionsCheck[EncryptMainGUI.ANNOTATION].setEnabled(true);
				        permissionsCheck[EncryptMainGUI.FILL].setEnabled(false);
				        permissionsCheck[EncryptMainGUI.SCREEN].setEnabled(false);
				        permissionsCheck[EncryptMainGUI.ASSEMBLY].setEnabled(false);
					}else{
						for(int i=0; i<permissionsCheck.length; i++){
							permissionsCheck[i].setEnabled(true);
						}
						if(encType.equals(EncryptionUtility.RC4_128)){
							versionCombo.addVersionFilter(rc4_128Filter);
						}else if(encType.equals(EncryptionUtility.AES_128)){
							versionCombo.addVersionFilter(aesFilter);
						}
					}
				}
			}	
		}else if(e.getStateChange() == ItemEvent.DESELECTED){
			Object source = e.getSource();
			if(source instanceof JComboBox){
				String encType = (String)e.getItem();
				if(encType.equals(EncryptionUtility.RC4_128)){
					versionCombo.removeVersionFilter(rc4_128Filter);
				}else if(encType.equals(EncryptionUtility.AES_128)){
					versionCombo.removeVersionFilter(aesFilter);
				}
			}
		}
	}
	
}
