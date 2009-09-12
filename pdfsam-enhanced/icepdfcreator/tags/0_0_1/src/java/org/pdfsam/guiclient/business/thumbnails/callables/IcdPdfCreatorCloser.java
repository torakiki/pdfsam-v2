/*
 * Created on 06-Sep-2009
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
package org.pdfsam.guiclient.business.thumbnails.callables;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.icepdf.core.pobjects.Document;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;
/**
 * called to close the ICEpdf resources
 * @author Andrea Vacondio
 *
 */
public class IcdPdfCreatorCloser implements Callable<Boolean> {
	
	private static final Logger log = Logger.getLogger(IcdPdfCreatorCloser.class.getPackage().getName());

	private Document pdfDocument;
	
	public IcdPdfCreatorCloser(Document pdfDocument) {
		super();
		this.pdfDocument = pdfDocument;
	}
	
	public Boolean call() {	
		Boolean retVal = Boolean.FALSE;
		try{			
			if(pdfDocument!=null){
				pdfDocument.dispose();				
				pdfDocument = null;
				retVal = Boolean.TRUE;
			}					
        }catch (Exception e) {
    		log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Unable to close thumbnail creator"),e);
    	}
        return retVal;
	}
}
