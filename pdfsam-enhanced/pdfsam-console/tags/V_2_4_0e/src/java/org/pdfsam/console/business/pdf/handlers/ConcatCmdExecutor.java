/*
 * Created on 28-Oct-2007
 * Copyright (C) 2007 by Andrea Vacondio.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.pdfsam.console.business.ConsoleServicesFacade;
import org.pdfsam.console.business.dto.Bounds;
import org.pdfsam.console.business.dto.PageRotation;
import org.pdfsam.console.business.dto.PdfFile;
import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.business.dto.commands.ConcatParsedCommand;
import org.pdfsam.console.business.pdf.bookmarks.BookmarksProcessor;
import org.pdfsam.console.business.pdf.handlers.interfaces.AbstractCmdExecutor;
import org.pdfsam.console.business.pdf.writers.PdfCopyFieldsConcatenator;
import org.pdfsam.console.business.pdf.writers.PdfSimpleConcatenator;
import org.pdfsam.console.business.pdf.writers.interfaces.PdfConcatenator;
import org.pdfsam.console.exceptions.console.ConcatException;
import org.pdfsam.console.exceptions.console.ConsoleException;
import org.pdfsam.console.exceptions.console.ValidationException;
import org.pdfsam.console.utils.FileUtility;
import org.pdfsam.console.utils.ValidationUtility;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Command executor for the concat command
 * 
 * @author Andrea Vacondio
 */
public class ConcatCmdExecutor extends AbstractCmdExecutor {

    private static final Logger LOG = Logger.getLogger(ConcatCmdExecutor.class.getPackage().getName());

    private static final String FILESET_NODE = "fileset";
    private static final String FILE_NODE = "file";

    private PdfReader pdfReader = null;
    private PdfConcatenator pdfWriter = null;
    private PdfStamper rotationStamper = null;
    private PdfReader rotationReader = null;

    public void execute(AbstractParsedCommand parsedCommand) throws ConsoleException {
        if ((parsedCommand != null) && (parsedCommand instanceof ConcatParsedCommand)) {
            ConcatParsedCommand inputCommand = (ConcatParsedCommand) parsedCommand;
            setPercentageOfWorkDone(0);
            // xml or csv parsing
            PdfFile[] fileList = inputCommand.getInputFileList();
            if (fileList == null || !(fileList.length > 0)) {
                File listFile = inputCommand.getInputCvsOrXmlFile();
                if (listFile != null && listFile.exists()) {
                    fileList = parseListFile(listFile);
                } else if (inputCommand.getInputDirectory() != null) {
                    fileList = getPdfFiles(inputCommand.getInputDirectory());
                }
            }
            // no input file found
            if (fileList == null || !(fileList.length > 0)) {
                throw new ConcatException(ConcatException.CMD_NO_INPUT_FILE);
            }

            // init
            int pageOffset = 0;
            ArrayList master = new ArrayList();
            Document pdfDocument = null;
            int totalProcessedPages = 0;

            try {
                String[] pageSelections = inputCommand.getPageSelections();
                File tmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());
                int length = ArrayUtils.getLength(pageSelections);

                for (int i = 0; i < fileList.length; i++) {

                    String currentPageSelection = ValidationUtility.ALL_STRING;
                    int currentDocumentPages = 0;
                    if (!ArrayUtils.isEmpty(pageSelections) && i <= length) {
                        currentPageSelection = pageSelections[i].toLowerCase();
                    }

                    String[] selectionGroups = StringUtils.split(currentPageSelection, ",");

                    pdfReader = new PdfReader(new RandomAccessFileOrArray(fileList[i].getFile().getAbsolutePath()),
                            fileList[i].getPasswordBytes());
                    pdfReader.removeUnusedObjects();
                    pdfReader.consolidateNamedDestinations();
                    int pdfNumberOfPages = pdfReader.getNumberOfPages();
                    BookmarksProcessor bookmarkProcessor = new BookmarksProcessor(SimpleBookmark.getBookmark(pdfReader), pdfNumberOfPages);

                    List boundsList = getBounds(pdfNumberOfPages, selectionGroups);
                    ValidationUtility.assertNotIntersectedBoundsList(boundsList);
                    String boundsString = "";

                    for (Iterator iter = boundsList.iterator(); iter.hasNext();) {
                        Bounds bounds = (Bounds) iter.next();
                        boundsString += (boundsString.length() > 0) ? "," + bounds.toString() : bounds.toString();

                        // bookmarks
                        List bookmarks = bookmarkProcessor.processBookmarks(bounds.getStart(), bounds.getEnd(),
                                pageOffset);
                        if (bookmarks != null) {
                            master.addAll(bookmarks);
                        }
                        int relativeOffset = (bounds.getEnd() - bounds.getStart()) + 1;
                        currentDocumentPages += relativeOffset;
                        pageOffset += relativeOffset;
                    }

                    // add pages
                    LOG.info(fileList[i].getFile().getAbsolutePath() + ": " + currentDocumentPages
                            + " pages to be added.");
                    if (pdfWriter == null) {
                        if (inputCommand.isCopyFields()) {
                            // step 1: we create a writer
                            pdfWriter = new PdfCopyFieldsConcatenator(new FileOutputStream(tmpFile), inputCommand
                                    .isCompress());
                            LOG.debug("PdfCopyFieldsConcatenator created.");
                            // output document version
                            if (inputCommand.getOutputPdfVersion() != null) {
                                pdfWriter.setPdfVersion(inputCommand.getOutputPdfVersion().charValue());
                            }
                            HashMap meta = pdfReader.getInfo();
                            meta.put("Creator", ConsoleServicesFacade.CREATOR);
                        } else {
                            // step 1: creation of a document-object
                            pdfDocument = new Document(pdfReader.getPageSizeWithRotation(1));
                            // step 2: we create a writer that listens to the document
                            pdfWriter = new PdfSimpleConcatenator(pdfDocument, new FileOutputStream(tmpFile),
                                    inputCommand.isCompress());
                            LOG.debug("PdfSimpleConcatenator created.");
                            // output document version
                            if (inputCommand.getOutputPdfVersion() != null) {
                                pdfWriter.setPdfVersion(inputCommand.getOutputPdfVersion().charValue());
                            }
                            // step 3: we open the document
                            pdfDocument.addCreator(ConsoleServicesFacade.CREATOR);
                            pdfDocument.open();
                        }
                    }
                    // step 4: we add content
                    pdfReader.selectPages(boundsString);
                    pdfWriter.addDocument(pdfReader);
                    // fix 03/07
                    // pdfReader = null;
                    pdfReader.close();
                    pdfWriter.freeReader(pdfReader);
                    totalProcessedPages += currentDocumentPages;
                    LOG.info(currentDocumentPages + " pages processed correctly.");
                    setPercentageOfWorkDone(((i + 1) * WorkDoneDataModel.MAX_PERGENTAGE) / fileList.length);
                }
                if (master.size() > 0) {
                    pdfWriter.setOutlines(master);
                }
                LOG.info("Total processed pages: " + totalProcessedPages + ".");
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
                // rotations
                if (inputCommand.getRotations() != null && inputCommand.getRotations().length > 0) {
                    LOG.info("Applying pages rotation.");
                    File rotatedTmpFile = applyRotations(tmpFile, inputCommand);
                    FileUtility.deleteFile(tmpFile);
                    FileUtility.renameTemporaryFile(rotatedTmpFile, inputCommand.getOutputFile(), inputCommand
                            .isOverwrite());
                } else {
                    FileUtility.renameTemporaryFile(tmpFile, inputCommand.getOutputFile(), inputCommand.isOverwrite());
                }
                LOG.debug("File " + inputCommand.getOutputFile().getCanonicalPath() + " created.");
            } catch (ConsoleException consoleException) {
                throw consoleException;
            } catch (Exception e) {
                throw new ConcatException(e);
            } finally {
                setWorkCompleted();
            }
        } else {
            throw new ConsoleException(ConsoleException.ERR_BAD_COMMAND);
        }

    }

    public void clean() {
        closePdfReader(pdfReader);
        closePdfReader(rotationReader);
        closePdfStamper(rotationStamper);
        if (pdfWriter != null) {
            pdfWriter.close();
        }
    }

    /**
     * Apply pages rotations
     * 
     * @param inputFile
     * @param inputCommand
     * @return temporary file with pages rotation
     */
    private File applyRotations(File inputFile, ConcatParsedCommand inputCommand) throws Exception {

        rotationReader = new PdfReader(inputFile.getAbsolutePath());
        rotationReader.removeUnusedObjects();
        rotationReader.consolidateNamedDestinations();

        int pdfNumberOfPages = rotationReader.getNumberOfPages();
        PageRotation[] rotations = inputCommand.getRotations();
        if (rotations != null && rotations.length > 0) {
            if (rotations.length > 1) {
                for (int i = 0; i < rotations.length; i++) {
                    if (pdfNumberOfPages >= rotations[i].getPageNumber() && rotations[i].getPageNumber() > 0) {
                        PdfDictionary dictionary = rotationReader.getPageN(rotations[i].getPageNumber());
                        int rotation = (rotations[i].getDegrees() + rotationReader.getPageRotation(rotations[i]
                                .getPageNumber())) % 360;
                        dictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
                    } else {
                        LOG.warn("Rotation for page " + rotations[i].getPageNumber() + " ignored.");
                    }
                }
            } else {
                // rotate all
                if (rotations[0].getType() == PageRotation.ALL_PAGES) {
                    int pageRotation = rotations[0].getDegrees();
                    for (int i = 1; i <= pdfNumberOfPages; i++) {
                        PdfDictionary dictionary = rotationReader.getPageN(i);
                        int rotation = (pageRotation + rotationReader.getPageRotation(i)) % 360;
                        dictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
                    }
                } else if (rotations[0].getType() == PageRotation.SINGLE_PAGE) {
                    // single page rotation
                    if (pdfNumberOfPages >= rotations[0].getPageNumber() && rotations[0].getPageNumber() > 0) {
                        PdfDictionary dictionary = rotationReader.getPageN(rotations[0].getPageNumber());
                        int rotation = (rotations[0].getDegrees() + rotationReader.getPageRotation(rotations[0]
                                .getPageNumber())) % 360;
                        dictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
                    } else {
                        LOG.warn("Rotation for page " + rotations[0].getPageNumber() + " ignored.");
                    }
                } else if (rotations[0].getType() == PageRotation.ODD_PAGES) {
                    // odd pages rotation
                    int pageRotation = rotations[0].getDegrees();
                    for (int i = 1; i <= pdfNumberOfPages; i = i + 2) {
                        PdfDictionary dictionary = rotationReader.getPageN(i);
                        int rotation = (pageRotation + rotationReader.getPageRotation(i)) % 360;
                        dictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
                    }
                } else if (rotations[0].getType() == PageRotation.EVEN_PAGES) {
                    // even pages rotation
                    int pageRotation = rotations[0].getDegrees();
                    for (int i = 2; i <= pdfNumberOfPages; i = i + 2) {
                        PdfDictionary dictionary = rotationReader.getPageN(i);
                        int rotation = (pageRotation + rotationReader.getPageRotation(i)) % 360;
                        dictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
                    }
                } else {
                    LOG.warn("Unable to find the rotation type. " + rotations[0]);
                }
            }
            LOG.info("Pages rotation applied.");
        }
        File rotatedTmpFile = FileUtility.generateTmpFile(inputCommand.getOutputFile());

        Character pdfVersion = inputCommand.getOutputPdfVersion();

        if (pdfVersion != null) {
            rotationStamper = new PdfStamper(rotationReader, new FileOutputStream(rotatedTmpFile), inputCommand
                    .getOutputPdfVersion().charValue());
        } else {
            rotationStamper = new PdfStamper(rotationReader, new FileOutputStream(rotatedTmpFile), rotationReader
                    .getPdfVersion());
        }

        HashMap meta = rotationReader.getInfo();
        meta.put("Creator", ConsoleServicesFacade.CREATOR);

        setCompressionSettingOnStamper(inputCommand, rotationStamper);

        rotationStamper.setMoreInfo(meta);
        rotationStamper.close();
        rotationReader.close();
        return rotatedTmpFile;

    }

    /**
     * 
     * @param pdfNumberOfPages
     * @param selections
     * @return a list of valid bounds
     * @throws ConcatException
     */
    private List getBounds(int pdfNumberOfPages, String[] selections) throws ValidationException, ConcatException {
        ArrayList retVal = new ArrayList();
        for (int i = 0; i < selections.length; i++) {
            Bounds bounds = getBounds(pdfNumberOfPages, selections[i]);
            ValidationUtility.assertValidBounds(bounds, pdfNumberOfPages);
            retVal.add(bounds);
        }
        return retVal;
    }

    private Bounds getBounds(int pdfNumberOfPages, String currentPageSelection) throws ConcatException {
        Bounds retVal = new Bounds(1, pdfNumberOfPages);
        if (!(ValidationUtility.ALL_STRING.equals(currentPageSelection))) {
            String[] limits = currentPageSelection.split("-");
            try {
                retVal.setStart(Integer.parseInt(limits[0]));
                // if there's an end limit
                if (limits.length > 1) {
                    retVal.setEnd(Integer.parseInt(limits[1]));
                } else {
                    // difference between '4' and '4-'
                    if (currentPageSelection.indexOf('-') == -1) {
                        retVal.setEnd(Integer.parseInt(limits[0]));
                    } else {
                        retVal.setEnd(pdfNumberOfPages);
                    }
                }
            } catch (NumberFormatException nfe) {
                throw new ConcatException(ConcatException.ERR_SYNTAX, new String[] { "" + currentPageSelection }, nfe);
            }
        }
        return retVal;
    }

    /**
     * Reads the input cvs file and return a File[] of input files
     * 
     * @param inputFile
     *            CSV input file (separator ",")
     * @return PdfFile[] of files
     */
    private PdfFile[] parseCsvFile(File inputFile) throws ConcatException {
        ArrayList retVal = new ArrayList();
        try {
            LOG.debug("Parsing CSV file " + inputFile.getAbsolutePath());
            BufferedReader bufferReader = new BufferedReader(new FileReader(inputFile));
            String temp = "";
            // read file
            while ((temp = bufferReader.readLine()) != null) {
                String[] tmpContent = temp.split(",");
                for (int i = 0; i < tmpContent.length; i++) {
                    if (tmpContent[i].trim().length() > 0) {
                        retVal.add(new PdfFile(tmpContent[i], null));
                    }
                }
            }
            bufferReader.close();
        } catch (IOException e) {
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[] { inputFile
                    .getAbsolutePath() }, e);
        }
        return (PdfFile[]) retVal.toArray(new PdfFile[0]);
    }

    /**
     * Reads the input xml file and return a File[] of input files
     * 
     * @param inputFile
     *            XML input file
     * @return PdfFile[] of files
     */
    private PdfFile[] parseXmlFile(File inputFile) throws ConcatException {
        List fileList = new ArrayList();
        String parentPath = null;
        try {
            LOG.debug("Parsing xml file " + inputFile.getAbsolutePath());
            SAXReader reader = new SAXReader();
            org.dom4j.Document document = reader.read(inputFile);
            List nodes = document.selectNodes("/filelist/*");
            parentPath = inputFile.getParent();
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                Node domNode = (Node) iter.next();
                String nodeName = domNode.getName();
                if (FILESET_NODE.equals(nodeName)) {
                    // found a fileset node
                    fileList.addAll(getFileNodesFromFileset(domNode, parentPath));
                } else if (FILE_NODE.equals(nodeName)) {
                    fileList.add(getPdfFileFromNode(domNode, null));
                } else {
                    LOG.warn("Node type not supported: " + nodeName);
                }
            }
        } catch (Exception e) {
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[] { inputFile
                    .getAbsolutePath() }, e);
        }
        return (PdfFile[]) fileList.toArray(new PdfFile[0]);
    }

    /**
     * given a fileset node returns the PdfFile objects
     * 
     * @param fileSetNode
     * @param parentDir
     * @return a list of PdfFile objects
     * @throws Exception
     */
    private List getFileNodesFromFileset(Node fileSetNode, String parentDir) throws Exception {
        String parentPath = null;
        Node useCurrentDir = fileSetNode.selectSingleNode("@usecurrentdir");
        Node dir = fileSetNode.selectSingleNode("@dir");
        if (dir != null && dir.getText().trim().length() > 0) {
            parentPath = dir.getText();
        } else {
            if (useCurrentDir != null && Boolean.valueOf(useCurrentDir.getText()).booleanValue()) {
                parentPath = parentDir;
            }
        }
        return getPdfFileListFromNode(fileSetNode.selectNodes("file"), parentPath);
    }

    /**
     * @param fileList
     *            Node list of file nodes
     * @param parentPath
     *            parent dir for the files or null
     * @return list of PdfFile
     */
    private List getPdfFileListFromNode(List fileList, String parentPath) throws Exception {
        List retVal = new ArrayList();
        for (int i = 0; fileList != null && i < fileList.size(); i++) {
            Node pdfNode = (Node) fileList.get(i);
            retVal.add(getPdfFileFromNode(pdfNode, parentPath));
        }
        return retVal;
    }

    /**
     * @param pdfNode
     *            input node
     * @param parentPath
     *            file parent path or null
     * @return a PdfFile object given a file node
     * @throws Exception
     */
    private PdfFile getPdfFileFromNode(Node pdfNode, String parentPath) throws Exception {
        PdfFile retVal = null;
        String pwd = null;
        String fileName = null;
        // get filename
        Node fileNode = pdfNode.selectSingleNode("@value");
        if (fileNode != null) {
            fileName = fileNode.getText().trim();
        } else {
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML, new String[] { "Empty file name." });
        }
        // get pwd value
        Node pwdNode = pdfNode.selectSingleNode("@password");
        if (pwdNode != null) {
            pwd = pwdNode.getText();
        }
        if (parentPath != null && parentPath.length() > 0) {
            retVal = new PdfFile(new File(parentPath, fileName), pwd);
        } else {
            retVal = new PdfFile(fileName, pwd);
        }
        return retVal;
    }

    /**
     * Reads the input file and return a File[] of input files
     * 
     * @param listFile
     *            XML or CSV input file
     * @return File[] of files
     */
    private PdfFile[] parseListFile(File listFile) throws ConcatException {
        PdfFile[] retVal = null;
        if (listFile != null && listFile.exists()) {
            if ("xml".equals(getExtension(listFile))) {
                retVal = parseXmlFile(listFile);
            } else if ("csv".equals(getExtension(listFile))) {
                retVal = parseCsvFile(listFile);
            } else {
                throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,
                        new String[] { "Unsupported extension." });
            }
        } else {
            throw new ConcatException(ConcatException.ERR_READING_CSV_OR_XML,
                    new String[] { "Input file doesn't exists." });
        }
        return retVal;
    }

    /**
     * get the extension of the input file
     * 
     * @param f
     * @return the extension
     */
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

}
