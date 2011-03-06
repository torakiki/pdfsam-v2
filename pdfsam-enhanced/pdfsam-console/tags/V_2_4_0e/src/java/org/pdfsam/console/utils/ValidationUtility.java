/*
 * Created on 25-Jul-2009
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
package org.pdfsam.console.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.pdfsam.console.business.dto.Bounds;
import org.pdfsam.console.business.dto.PageLabel;
import org.pdfsam.console.business.dto.PageRotation;
import org.pdfsam.console.exceptions.console.ValidationException;

/**
 * Utility for the arguments validation
 * @author Andrea Vacondio
 */
public final class ValidationUtility {

	private static final Logger LOG = Logger.getLogger(ValidationUtility.class.getPackage().getName());

	public static final String ALL_STRING = "all";
	public static final String ODD_STRING = "odd";
	public static final String EVEN_STRING = "even";

	private static final String SELECTION_REGEXP = "(?:(?:[\\d]+[-]?[\\d]*)(?:,(?:[\\d]+[-]?[\\d]*))*)+";

	public static final String PDF_EXTENSION = ".pdf";

	private ValidationUtility() {
		// no constructor
	}

	/**
	 * all, odd and even pages rotation cannot be mixed together or with single pages rotations
	 * @param inputString
	 *        the input command line string for the -r param
	 * @param allowSinglePagesRotation
	 *        if true single pages rotation are allowd, if false only all, odd end even pages rotations are allowed
	 * @return the rotations array
	 * @throws ValidationException
	 */
	public static PageRotation[] getPagesRotation(final String inputString, final boolean allowSinglePagesRotation) throws ValidationException {
		ArrayList retVal = new ArrayList();
		try {
			if (inputString != null && inputString.length() > 0) {
				String[] rotateParams = inputString.split(",");
				for (int i = 0; i < rotateParams.length; i++) {
					String currentRotation = rotateParams[i];
					if (currentRotation.length() > 3) {
						String[] rotationParams = currentRotation.split(":");
						if (rotationParams.length == 2) {
							String pageNumber = rotationParams[0].trim();
							int degrees = Integer.parseInt(rotationParams[1]) % 360;
							// must be a multiple of 90
							if ((degrees % 90) != 0) {
								throw new ValidationException(ValidationException.ERR_DEGREES_NOT_ALLOWED, new String[] { Integer.toString(degrees) });
							}
							// rotate all
							if (ALL_STRING.equals(pageNumber)) {
								if (!retVal.isEmpty()) {
									LOG.warn("Page rotation for every page found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.ALL_PAGES));
								break;
							} else if (ODD_STRING.equals(pageNumber)) {
								if (!retVal.isEmpty()) {
									LOG.warn("Page rotation for odd pages found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.ODD_PAGES));
								break;
							} else if (EVEN_STRING.equals(pageNumber)) {
								if (!retVal.isEmpty()) {
									LOG.warn("Page rotation for even pages found, other rotations removed");
									retVal.clear();
								}
								retVal.add(new PageRotation(PageRotation.NO_PAGE, degrees, PageRotation.EVEN_PAGES));
								break;
							} else {
								if (allowSinglePagesRotation) {
									retVal.add(new PageRotation(Integer.parseInt(pageNumber), degrees));
								}
							}
						} else {
							throw new ValidationException(ValidationException.ERR_PARAM_ROTATION, new String[] { currentRotation });
						}
					} else {
						throw new ValidationException(ValidationException.ERR_PARAM_ROTATION, new String[] { currentRotation });
					}
				}
			}
		} catch (Exception e) {
			throw new ValidationException(ValidationException.ERR_WRONG_ROTATION, e);
		}
		return (PageRotation[]) retVal.toArray(new PageRotation[retVal.size()]);
	}

	/**
	 * @param inputString
	 * @return the PageLabel object resulting by the -l option value
	 */
	public static PageLabel getPageLabel(final String inputString) throws ValidationException {
		PageLabel retVal = null;
		if (inputString != null && inputString.length() > 0) {
			String[] values = inputString.split(":");
			if (values.length >= 2) {
				try {
					retVal = new PageLabel();
					retVal.setPageNumber(Integer.parseInt(values[0]));
					if (values.length == 3) {
						retVal.setLogicalPageNumber(Integer.parseInt(values[2]));
					}
				} catch (Exception e) {
					throw new ValidationException(ValidationException.ERR_WRONG_PAGE_LABEL, new String[] { inputString }, e);
				}

				// style
				retVal.setStyle(getPageLabelStyle(values[1]));
			} else {
				throw new ValidationException(ValidationException.ERR_WRONG_PAGE_LABEL, new String[] { inputString });
			}
		}
		return retVal;

	}

	/**
	 * @param inputString
	 * @return a valid page label style
	 * @throws ValidationException
	 *         if the input string is not a valid label style
	 */
	private static String getPageLabelStyle(final String inputString) throws ValidationException {
		String retVal = null;
		if (inputString != null && inputString.length() > 0) {
			if (PageLabel.ARABIC.equals(inputString) || PageLabel.EMPTY.equals(inputString) || PageLabel.LLETTER.equals(inputString)
					|| PageLabel.LROMAN.equals(inputString) || PageLabel.ULETTER.equals(inputString) || PageLabel.UROMAN.equals(inputString)) {
				retVal = inputString;
			} else {
				throw new ValidationException(ValidationException.ERR_UNK_LABEL_STYLE, new String[] { inputString });
			}
		} else {
			throw new ValidationException(ValidationException.ERR_UNK_LABEL_STYLE, new String[] { inputString });
		}
		return retVal;
	}

	/**
	 * Overloaded {@link ValidationUtility#getPagesRotation(String, boolean)}. Allow single pages rotation.
	 * @param inputString
	 * @return the rotations array
	 * @throws ValidationException
	 * @see {@link ValidationUtility#getPagesRotation(String, boolean)}
	 */
	public static PageRotation[] getPagesRotation(final String inputString) throws ValidationException {
		return getPagesRotation(inputString, true);
	}

	/**
	 * check if the given file path identifies a pdf format file
	 * @param inputFileName
	 * @throws ValidationException
	 *         if not a pdf format
	 */
	public static void assertValidPdfExtension(final String inputFileName) throws ValidationException {
		if (!((inputFileName.toLowerCase().endsWith(PDF_EXTENSION)) && (inputFileName.length() > PDF_EXTENSION.length()))) {
			throw new ValidationException(ValidationException.ERR_NOT_PDF, new String[] { inputFileName });
		}
	}

	/**
	 * check if the given file is a directory
	 * @param inputDir
	 * @throws ValidationException
	 *         if not a directory
	 */
	public static void assertValidDirectory(File inputDir) throws ValidationException {
		if (!inputDir.isDirectory()) {
			throw new ValidationException(ValidationException.ERR_NOT_DIR, new String[] { inputDir.getAbsolutePath() });
		}
	}

	/**
	 * validates the selections array. Every array item must be a valid selection string.
	 * @param selections
	 * @throws ValidationException
	 */
	public static void assertValidPageSelectionsArray(String[] selections) throws ValidationException {
		if (!isValidPageSelectionsArray(selections)) {
			throw new ValidationException(ValidationException.ERR_ILLEGAL_U);
		}
	}

	/**
	 * validates the selections array
	 * @param selections
	 * @return true if the array is valid
	 */
	public static boolean isValidPageSelectionsArray(String[] selections) {
		boolean retVal = true;
		if (!ArrayUtils.isEmpty(selections)) {
			Pattern pattern = Pattern.compile(SELECTION_REGEXP, Pattern.CASE_INSENSITIVE);
			for (int i = 0; i < selections.length; i++) {
				String currentSelection = selections[i];
				if (!ALL_STRING.equalsIgnoreCase(currentSelection)) {
					if (!(pattern.matcher(currentSelection).matches())) {
						retVal = false;
						break;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * validates the input Bounds object
	 * @param bounds
	 * @param pdfNumberOfPages
	 *        number of total pages
	 * @throws ValidationException
	 */
	public static void assertValidBounds(Bounds bounds, int pdfNumberOfPages) throws ValidationException {
		if (bounds.getStart() <= 0) {
			throw new ValidationException(ValidationException.ERR_NOT_POSITIVE, new String[] { Integer.toString(bounds.getStart()), bounds.toString() });
		} else if (bounds.getEnd() > pdfNumberOfPages) {
			throw new ValidationException(ValidationException.ERR_CANNOT_MERGE, new String[] { Integer.toString(bounds.getEnd()) });
		} else if (bounds.getStart() > bounds.getEnd()) {
			throw new ValidationException(ValidationException.ERR_START_BIGGER_THAN_END, new String[] { Integer.toString(bounds.getStart()),
					Integer.toString(bounds.getEnd()), bounds.toString() });
		}
	}

	/**
	 * validates the input bounds list ensuring that there is no intersections between the objects of the input list
	 * @param bounds
	 */
	public static void assertNotIntersectedBoundsList(List bounds) throws ValidationException {
		for (int i = 0; i < bounds.size(); i++) {
			Bounds victim = (Bounds) bounds.get(i);
			for (int j = i + 1; j < bounds.size(); j++) {
				Bounds current = (Bounds) bounds.get(j);
				if (victim.intersects(current)) {
					throw new ValidationException(ValidationException.ERR_BOUNDS_INTERSECTS, new String[] { victim.toString(),
							current.toString() });
				}
			}
		}
	}
}
