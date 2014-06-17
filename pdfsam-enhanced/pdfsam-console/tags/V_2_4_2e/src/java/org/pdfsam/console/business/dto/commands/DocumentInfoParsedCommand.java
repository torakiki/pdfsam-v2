/*
 * Created on 25-Oct-2009
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
package org.pdfsam.console.business.dto.commands;

import java.io.File;

import org.pdfsam.console.business.dto.PdfFile;

/**
 * SetInfo parsed command dto filled by parsing service and used by worker service
 * @author Andrea Vacondio
 */
public class DocumentInfoParsedCommand extends AbstractParsedCommand {

	private static final long serialVersionUID = -821506712008814804L;

	public static final String F_ARG = "f";
	public static final String O_ARG = "o";
	public static final String TITLE_ARG = "title";
	public static final String AUTHOR_ARG = "author";
	public static final String SUBJECT_ARG = "subject";
	public static final String KEYWORDS_ARG = "keywords";

	private File outputFile;
	private PdfFile inputFile;
	private String title;
	private String author;
	private String subject;
	private String keywords;

	public DocumentInfoParsedCommand() {
		super();
	}

	/**
	 * @param outputFile
	 * @param inputFile
	 * @param title
	 * @param author
	 * @param subject
	 * @param keywords
	 */
	public DocumentInfoParsedCommand(File outputFile, PdfFile inputFile, String title, String author, String subject, String keywords) {
		super();
		this.outputFile = outputFile;
		this.inputFile = inputFile;
		this.title = title;
		this.author = author;
		this.subject = subject;
		this.keywords = keywords;
	}

	/**
	 * Full constructor
	 * @param outputFile
	 * @param inputFile
	 * @param title
	 * @param author
	 * @param subject
	 * @param keywords
	 * @param overwrite
	 * @param compress
	 * @param outputPdfVersion
	 */
	public DocumentInfoParsedCommand(File outputFile, PdfFile inputFile, String title, String author, String subject, String keywords,
			boolean overwrite, boolean compress, char outputPdfVersion) {
		super(overwrite, compress, outputPdfVersion);
		this.outputFile = outputFile;
		this.inputFile = inputFile;
		this.title = title;
		this.author = author;
		this.subject = subject;
		this.keywords = keywords;
	}

	/**
	 * @return the outputFile
	 */
	public File getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile
	 *        the outputFile to set
	 */
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the inputFile
	 */
	public PdfFile getInputFile() {
		return inputFile;
	}

	/**
	 * @param inputFile
	 *        the inputFile to set
	 */
	public void setInputFile(PdfFile inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *        the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *        the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *        the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the keywords
	 */
	public String getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords
	 *        the keywords to set
	 */
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCommand() {
		return COMMAND_SETDOCINFO;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer retVal = new StringBuffer();
		retVal.append(super.toString());
		retVal.append((outputFile == null) ? "" : "[outputDir=" + outputFile.getAbsolutePath() + "]");
		retVal.append((inputFile == null) ? "" : "[inputFile=" + inputFile + "]");
		retVal.append((title == null) ? "" : "[title=" + title + "]");
		retVal.append((author == null) ? "" : "[author=" + author + "]");
		retVal.append((subject == null) ? "" : "[subject=" + subject + "]");
		retVal.append((keywords == null) ? "" : "[keywords=" + keywords + "]");
		retVal.append("[command=" + getCommand() + "]");
		return retVal.toString();
	}

}
