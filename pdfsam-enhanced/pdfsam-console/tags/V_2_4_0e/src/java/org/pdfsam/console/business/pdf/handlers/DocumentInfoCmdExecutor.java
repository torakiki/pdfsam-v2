/*
 * Created on 06-Nov-2009
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
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.DocumentInfoParsedCommand;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.utils.FileUtility;

import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.RandomAccessFileOrArray;

/**
 * Executor for the set document info command
 * @author Andrea Vacondio
 */
public class DocumentInfoCmdExecutor extends AbstractCmdExecutor {

	private static final Logger LOG = Logger.getLogger(DocumentInfoCmdExecutor.class.getPackage().getName());

	private static final String TITLE = PdfName.decodeName(PdfName.TITLE.toString());
	private static final String AUTHOR = PdfName.decodeName(PdfName.AUTHOR.toString());
	private static final String SUBJECT = PdfName.decodeName(PdfName.SUBJECT.toString());
	private static final String KEYWORDS = PdfName.decodeName(PdfName.KEYWORDS.toString());

	private PdfReader pdfReader = null;
	private PdfStamper pdfStamper = null;

	public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
		if ((parsedCommand != null) && (parsedCommand instanceof DocumentInfoParsedCommand)) {
			DocumentInfoParsedCommand inputCommand = (DocumentInfoParsedCommand) parsedCommand;
			setPercentageOfWorkDone(0);
			try {
				File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
				pdfReader = new PdfReader(new RandomAccessFileOrArray(inputCommand.getInputFile().getFile().getAbsolutePath()), inputCommand
						.getInputFile().getPasswordBytes());
				pdfReader.removeUnusedObjects();
				pdfReader.consolidateNamedDestinations();

				// version
				LOG.debug("Creating a new document.");
				Character pdfVersion = inputCommand.getOutputPdfVersion();
				if (pdfVersion != null) {
					pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), inputCommand.getOutputPdfVersion().charValue());
				} else {
					pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(tmpFile), pdfReader.getPdfVersion());
				}

				HashMap meta = pdfReader.getInfo();
				meta.put("Creator", ConsoleServicesFacade.CREATOR);
				if (inputCommand.getAuthor() != null) {
					meta.put(AUTHOR, inputCommand.getAuthor());
				}
				if (inputCommand.getSubject() != null) {
					meta.put(SUBJECT, inputCommand.getSubject());
				}
				if (inputCommand.getTitle() != null) {
					meta.put(TITLE, inputCommand.getTitle());
				}
				if (inputCommand.getKeywords() != null) {
					meta.put(KEYWORDS, inputCommand.getKeywords());
				}

				setCompressionSettingOnStamper(inputCommand, pdfStamper);

				pdfStamper.setMoreInfo(meta);
				pdfStamper.close();
				pdfReader.close();

				FileUtility.renameTemporaryFile(tmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite());
				LOG.debug("File " + inputCommand.getOutputFile().getCanonicalPath() + " created.");

			} catch (Exception e) {
				throw new ConsoleException(e);
			} finally {
				setWorkCompleted();
			}
		} else {
			throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
		}

	}

	public void clean(){
		closePdfReader(pdfReader);
		closePdfStamper(pdfStamper);
	}
}
