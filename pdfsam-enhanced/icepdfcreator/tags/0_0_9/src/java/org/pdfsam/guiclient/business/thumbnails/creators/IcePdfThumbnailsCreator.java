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
package org.pdfsam.guiclient.business.thumbnails.creators;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.icepdf.core.SecurityCallback;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PInfo;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.pdfsam.guiclient.business.thumbnails.callables.IcdPdfCreatorCloser;
import org.pdfsam.guiclient.business.thumbnails.callables.IcePdfThumbnailCallable;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.DocumentInfo;
import org.pdfsam.guiclient.dto.DocumentPage;
import org.pdfsam.guiclient.dto.VisualPageListItem;
import org.pdfsam.guiclient.exceptions.ThumbnailCreationException;
import org.pdfsam.guiclient.utils.DialogUtility;
import org.pdfsam.i18n.GettextResource;

/**
 * thumbnails creator using ICDpdf
 * 
 * @author Andrea Vacondio
 * 
 */
public class IcePdfThumbnailsCreator extends AbstractThumbnailCreator {

    public static final int ICEPDF_RESOLUTION = 72;
    private static final String ICEPDF_CREATOR_NAME = "ICEpdf";

    private static final Logger log = Logger.getLogger(IcePdfThumbnailsCreator.class.getPackage().getName());

    private Document pdfDocument = null;

    @Override
    protected void finalizeThumbnailsCreation() throws ThumbnailCreationException {
    }

    @Override
    protected Callable<Boolean> getCloserTask() throws ThumbnailCreationException {
        Callable<Boolean> retVal = null;
        if (pdfDocument != null) {
            retVal = new IcdPdfCreatorCloser(pdfDocument);
        }
        return retVal;
    }

    @Override
    protected DocumentInfo getDocumentInfo() throws ThumbnailCreationException {
        File inputFile = getInputFile();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setFileName(inputFile.getAbsolutePath());
        documentInfo.setPages(pdfDocument.getNumberOfPages());
        // TODO how to find the version and encryption
        documentInfo.setPdfVersion("");
        documentInfo.setEncrypted(pdfDocument.getSecurityManager() != null);
        PInfo info = pdfDocument.getInfo();
        if (info != null) {
            documentInfo.getDocumentMetaData().setAuthor(info.getAuthor());
            documentInfo.getDocumentMetaData().setCreator(info.getCreator());
            documentInfo.getDocumentMetaData().setTitle(info.getTitle());
            documentInfo.getDocumentMetaData().setProducer(info.getProducer());
        }
        return documentInfo;
    }

    @Override
    protected Vector<VisualPageListItem> getDocumentModel(List<DocumentPage> template)
            throws ThumbnailCreationException {
        int pages = pdfDocument.getNumberOfPages();
        File inputFile = getInputFile();
        Vector<VisualPageListItem> modelList = null;
        try {
            if (pages > 0 && inputFile != null) {
                modelList = new Vector<VisualPageListItem>(pages);
                if (template == null || template.size() <= 0) {
                    for (int i = 1; i <= pages; i++) {
                        modelList.add(new VisualPageListItem(i, inputFile.getCanonicalPath(), getProvidedPassword()));
                    }
                } else {
                    for (DocumentPage page : template) {
                        if (page.getPageNumber() > 0 && page.getPageNumber() <= pages) {
                            VisualPageListItem currentItem = new VisualPageListItem(page.getPageNumber(), inputFile
                                    .getCanonicalPath(), getProvidedPassword());
                            currentItem.setDeleted(page.isDeleted());
                            currentItem.setRotation(page.getRotation());
                            modelList.add(currentItem);
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                    .getI18nResourceBundle(), "Error opening pdf document")
                    + " " + inputFile.getAbsolutePath(), ioe);
        }
        return modelList;
    }

    @Override
    protected Collection<? extends Callable<Boolean>> getGenerationTasks(Vector<VisualPageListItem> modelList)
            throws ThumbnailCreationException {
        ArrayList<IcePdfThumbnailCallable> tasks = null;
        if (pdfDocument != null && modelList != null && modelList.size() > 0) {
            tasks = new ArrayList<IcePdfThumbnailCallable>(modelList.size());
            for (VisualPageListItem pageItem : modelList) {
                tasks.add(new IcePdfThumbnailCallable(pdfDocument, pageItem, getPanel(), getCurrentId()));
            }
        }
        return tasks;
    }

    @Override
    protected void initThumbnailsCreation() throws ThumbnailCreationException {
    }

    @Override
    protected boolean openInputDocument() throws ThumbnailCreationException {
        boolean retVal = false;
        File inputFile = getInputFile();
        if (inputFile != null && inputFile.exists() && inputFile.isFile()) {
            try {
                pdfDocument = openDocument(inputFile, getProvidedPassword());
                retVal = true;
            } catch (Exception e) {
                throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                        .getI18nResourceBundle(), "Error opening pdf document")
                        + " " + inputFile.getAbsolutePath(), e);
            }
        } else {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Input file doesn't exists or is a directory"));
        }
        return retVal;
    }

    /**
     * Opens the input document with the given password
     * 
     * @param inputFile
     * @param providedPwd
     * @return
     * @throws PDFException
     * @throws PDFSecurityException
     * @throws IOException
     * @throws NullPointerException
     *             if the inputFile is null
     */
    private Document openDocument(final File inputFile, final String providedPwd) throws PDFException,
            PDFSecurityException, IOException, NullPointerException {
        Document retVal = null;
        retVal = new Document();
        retVal.setSecurityCallback(new ModalSecurityCallbak(inputFile, providedPwd));
        retVal.setFile(inputFile.getCanonicalPath());
        return retVal;
    }

    @Override
    public String getCreatorIdentifier() {
        return IcePdfThumbnailsCreator.class.getName();
    }

    @Override
    public String getCreatorName() {
        return ICEPDF_CREATOR_NAME;
    }

    @Override
    public BufferedImage getPageImage(File fileName, String password, int page, int rotation)
            throws ThumbnailCreationException {
        BufferedImage retVal = null;
        if (fileName != null && fileName.exists() && fileName.isFile()) {
            try {
                Document pdfDocument = openDocument(fileName, password);
                retVal = (BufferedImage) pdfDocument.getPageImage(page - 1, GraphicsRenderingHints.SCREEN,
                        Page.BOUNDARY_CROPBOX, rotation, 1);
            } catch (Exception e) {
                throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                        .getI18nResourceBundle(), "Error opening pdf document")
                        + " " + fileName.getAbsolutePath(), e);
            }
        } else {
            throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                    .getI18nResourceBundle(), "Input file doesn't exists or is a directory"));
        }
        return retVal;
    }

    @Override
    public int getResolution() {
        return ICEPDF_RESOLUTION;
    }

    @Override
    public BufferedImage getThumbnail(File inputFile, String password, int page, float resizePercentage)
            throws ThumbnailCreationException {
        BufferedImage retVal = null;
        if (inputFile != null && inputFile.exists() && inputFile.isFile()) {
            try {
                Document pdfDocument = openDocument(inputFile, password);
                retVal = (BufferedImage) pdfDocument.getPageImage(page - 1, GraphicsRenderingHints.SCREEN,
                        Page.BOUNDARY_CROPBOX, 0, resizePercentage);
            } catch (Exception e) {
                throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                        .getI18nResourceBundle(), "Error opening pdf document")
                        + " " + inputFile.getAbsolutePath(), e);
            }
        } else {
            throw new ThumbnailCreationException(GettextResource.gettext(Configuration.getInstance()
                    .getI18nResourceBundle(), "Input file doesn't exists or is a directory"));
        }
        return retVal;
    }

    /**
     * Callback for the password input
     * 
     * @author Andrea Vacondio
     * 
     */
    private class ModalSecurityCallbak implements SecurityCallback {

        private File inputFile;
        private String password;

        /**
         * @param inputFile
         * @param password
         */
        public ModalSecurityCallbak(File inputFile, String password) {
            super();
            this.inputFile = inputFile;
            this.password = password;
        }

        @Override
        public String requestPassword(Document document) {
            // a password already provided?
            if (password == null) {
                // ask for a password
                password = DialogUtility.askForDocumentPasswordDialog(getPanel(), inputFile.getName());
                setProvidedPassword(password);
            }
            return password;
        }
    }
}
