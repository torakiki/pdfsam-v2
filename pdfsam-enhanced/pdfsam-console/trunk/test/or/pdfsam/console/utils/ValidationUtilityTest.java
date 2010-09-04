/*
 * Created on 21-Nov-2009
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
package or.pdfsam.console.utils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.pdfsam.console.business.dto.Bounds;
import org.pdfsam.console.exceptions.console.ValidationException;
import org.pdfsam.console.utils.ValidationUtility;
/**
 * Test unit for the validation utility
 * @author Andrea Vacondio
 *
 */
public class ValidationUtilityTest extends TestCase {

	private static final String[] VALID_SELECTION = new String[]{"All", "2-5", "6-", "39", "6-3"};
	private static final String[] WRONG_STRING_SELECTION = new String[]{"Test"};
	
	private static final Bounds VALID_BOUNDS = new Bounds(23,40);
	private static final Bounds NEGATIVE_BOUNDS = new Bounds(-2,5);
	private static final Bounds UPSIDEDOWN_BOUNDS = new Bounds(10,5);
	
	public void testAssertValidPageSelectionsArray(){
		
		try {
			ValidationUtility.assertValidPageSelectionsArray(VALID_SELECTION);
		} catch (ValidationException e) {
			Assert.fail(e.getMessage());
		}
		
		try {
			ValidationUtility.assertValidPageSelectionsArray(WRONG_STRING_SELECTION);
			Assert.fail("Validation failed to detect wrong selection");
		} catch (ValidationException e) {
		}
	}
	
	public void testAssertValidBounds(){
		
		try {
			ValidationUtility.assertValidBounds(VALID_BOUNDS, 50);
		} catch (ValidationException e) {
			Assert.fail(e.getMessage());
		}

		try {
			ValidationUtility.assertValidBounds(VALID_BOUNDS, 30);
			Assert.fail("Validation failed to detect out of bounds limit");
		} catch (ValidationException e) {
		}

		try {
			ValidationUtility.assertValidBounds(NEGATIVE_BOUNDS, 30);
			Assert.fail("Validation failed to detect negative bounds");
		} catch (ValidationException e) {
		}
		
		try {
			ValidationUtility.assertValidBounds(UPSIDEDOWN_BOUNDS, 30);
			Assert.fail("Validation failed to detect upside down bounds");
		} catch (ValidationException e) {
		}
	}
	
	public void testassertNotIntersectedBoundsList(){
		List validList = new ArrayList();
		validList.add(new Bounds(2,5));
		validList.add(new Bounds(7,30));
		validList.add(new Bounds(34,34));
		validList.add(new Bounds(60,62));
		
		try {
			ValidationUtility.assertNotIntersectedBoundsList(validList);
		} catch (ValidationException e) {
			Assert.fail(e.getMessage());
		}
		
		List unvalidList = new ArrayList();
		unvalidList.add(new Bounds(2,8));
		unvalidList.add(new Bounds(7,30));
		try {
			ValidationUtility.assertNotIntersectedBoundsList(unvalidList);
			Assert.fail("Validation failed to detect intersected bounds");
		} catch (ValidationException e) {
		}
		
		List secondUnvalidList = new ArrayList();
		secondUnvalidList.add(new Bounds(2,2));
		secondUnvalidList.add(new Bounds(2,3));
		try {
			ValidationUtility.assertNotIntersectedBoundsList(secondUnvalidList);
			Assert.fail("Validation failed to detect intersected bounds");
		} catch (ValidationException e) {
		}
	}
}
