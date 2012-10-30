/*
 * Created on 20-Aug-2009
 * Copyright (C) 2009 by Andrea Vacondio.
 *
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.pdf.handlers;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.PageLabel;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.PageLabelsParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.PageLabelsException;
import org.pdfsam.console.utils.FileUtility;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfPageLabels;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * Executes the page labels command
 * 
 * @author Andrea Vacondio
 * 
 */
public class PageLabelsCmdExecutor extends AbstractCmdExecutor {

    private static final Logger LOG = Logger.getLogger(PageLabelsCmdExecutor.class.getPackage().getName());

    private PdfReader pdfReader = null;
    private PdfCopy pdfWriter = null;

    public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
        if ((parsedCommand != null) && (parsedCommand instanceof PageLabelsParsedCommand)) {

            PageLabelsParsedCommand inputCommand = (PageLabelsParsedCommand) parsedCommand;
            setPercentageOfWorkDone(0);
            Document currentDocument;
            try {
                File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
                LOG.debug("Opening " + inputCommand.getInputFile().getFile().getAbsolutePath());
                pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile()
                        .getAbsolutePath()), inputCommand.getInputFile().getPasswordBytes());
                pdfReader.removeUnusedObjects();
                pdfReader.consolidateNamedDestinations();
                int n = pdfReader.getNumberOfPages();
                currentDocument = new Document(pdfReader.getPageSizeWithRotation(1));

                pdfWriter = new PdfCopy(currentDocument, new FileOutputStream(tmpFile));

                // set compressed
                setCompressionSettingOnWriter(inputCommand, pdfWriter);
                // set pdf version
                setPdfVersionSettingOnWriter(inputCommand, pdfWriter, Character.valueOf(pdfReader.getPdfVersion()));

                // set creator
                currentDocument.addCreator(ConsoleServicesFacade.CREATOR);
                currentDocument.open();

                for (int count = 1; count <= n; count++) {
                    pdfWriter.addPage(pdfWriter.getImportedPage(pdfReader, count));
                }
                pdfReader.close();
                pdfWriter.freeReader(pdfReader);

                // set labels
                PdfPageLabels pageLabels = new PdfPageLabels();
                PageLabel[] labels = inputCommand.getLabels();
                // last step is creating the file
                int stepsNumber = labels.length + 1;
                for (int i = 0; i < labels.length; i++) {
                    if (labels[i].getPageNumber() <= n) {
                        pageLabels.addPageLabel(labels[i].getPageNumber(), getPageLabelStyle(labels[i].getStyle()),
                                labels[i].getPrefix(), labels[i].getLogicalPageNumber());
                    } else {
                        LOG.warn("Page number out of range, label starting at page " + labels[i].getPageNumber()
                                + " will be ignored");
                    }
                    setPercentageOfWorkDone((i * WorkDoneDataModel.MAX_PERGENTAGE) / stepsNumber);
                }
                pdfWriter.setPageLabels(pageLabels);

                currentDocument.close();
                pdfWriter.close();
                FileUtility.renameTemporaryFile(tmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite());
                LOG.debug("Page labels set on file " + inputCommand.getOutputFile());
            } catch (Exception e) {
                throw new PageLabelsException(e);
            } finally {
                setWorkCompleted();
            }
        } else {
            throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
        }

    }

    /**
     * mapping from the console style variable to the iText variable
     * 
     * @param style
     * @return iText style variable
     */
    private int getPageLabelStyle(String style) {
        int retVal = PdfPageLabels.DECIMAL_ARABIC_NUMERALS;

        if (PageLabel.ARABIC.equals(style)) {
            retVal = PdfPageLabels.DECIMAL_ARABIC_NUMERALS;
        } else if (PageLabel.EMPTY.equals(style)) {
            retVal = PdfPageLabels.EMPTY;
        } else if (PageLabel.LLETTER.equals(style)) {
            retVal = PdfPageLabels.LOWERCASE_LETTERS;
        } else if (PageLabel.LROMAN.equals(style)) {
            retVal = PdfPageLabels.LOWERCASE_ROMAN_NUMERALS;
        } else if (PageLabel.ULETTER.equals(style)) {
            retVal = PdfPageLabels.UPPERCASE_LETTERS;
        } else if (PageLabel.UROMAN.equals(style)) {
            retVal = PdfPageLabels.UPPERCASE_ROMAN_NUMERALS;
        }
        return retVal;
    }

    public void clean() {
        closePdfReader(pdfReader);
        closePdfWriter(pdfWriter);
    }
}
