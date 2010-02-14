/*
 * Created on 13-Feb-2010 
 * Copyright (C) 2010 by Andrea Vacondio.
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
package org.pdfsam.guiclient.business.listeners;

import java.awt.event.ActionListener;
import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;

/**
 * Abstract class for the run buttons listeners
 * 
 * @author Andrea Vacondio
 * 
 */
public abstract class AbstractRunButtonActionListener implements ActionListener {

	/**
	 * 
	 * @param item
	 * @return the suggested destination directory
	 */
	protected File getSiggestedDestinationDirectory(PdfSelectionTableItem item) {
		File retVal = null;
		if (StringUtils.isNotEmpty(Configuration.getInstance().getDefaultWorkingDirectory())) {
			retVal = new File(Configuration.getInstance().getDefaultWorkingDirectory());
		} else {
			retVal = item.getInputFile().getParentFile();
		}
		return retVal;
	}
	
	protected File getSiggestedOutputFile(PdfSelectionTableItem item) {
		File retVal = null;
		if (StringUtils.isNotEmpty(Configuration.getInstance().getDefaultWorkingDirectory())) {
			retVal = new File(Configuration.getInstance().getDefaultWorkingDirectory());
		} else {
			retVal = item.getInputFile().getParentFile();
		}
		return retVal;
	}

}
