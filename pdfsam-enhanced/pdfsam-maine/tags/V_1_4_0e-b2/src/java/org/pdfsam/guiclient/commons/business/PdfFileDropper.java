/*
 * Created on 23-Nov-2007
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
package org.pdfsam.guiclient.commons.business;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.util.List;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * Drag&Drop class
 * @author Andrea Vacondio
 *
 */
public class PdfFileDropper extends DropTargetAdapter {
	
	private static final Logger log = Logger.getLogger(PdfFileDropper.class.getPackage().getName());

	private PdfLoader loader;

	public PdfFileDropper(PdfLoader loader){
		this.loader = loader;		
	}
	
	public void drop(DropTargetDropEvent e)  {
		try {
            DropTargetContext context = e.getDropTargetContext();
            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable t = e.getTransferable();
            Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
            if (data instanceof List) {
                List files = (List)data;
                loader.addFiles(files);
            }
            context.dropComplete(true);
        }       
        catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error: "), ex);
        }	
    }

}
