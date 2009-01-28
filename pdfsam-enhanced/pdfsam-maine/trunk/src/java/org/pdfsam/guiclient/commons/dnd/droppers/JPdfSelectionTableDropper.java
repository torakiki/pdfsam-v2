/*
 * Created on 27-Jun-2008
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
package org.pdfsam.guiclient.commons.dnd.droppers;

import java.io.File;
import java.util.List;

import org.pdfsam.guiclient.commons.business.loaders.PdfLoader;
/**
 * Dropper for the JPdfSelectionTable
 * @author Andrea Vacondio
 */
public class JPdfSelectionTableDropper extends AbstractDropper {

	private PdfLoader loader;

	public JPdfSelectionTableDropper(PdfLoader loader){
		this.loader = loader;		
	}
	
	protected void executeDrop(List<File> arg0){
            loader.addFiles(arg0, true);
	}
}
