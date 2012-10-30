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
package or.pdfsam.console.utils.prefix;

import junit.framework.TestCase;

import org.pdfsam.console.utils.perfix.FileNameRequest;
import org.pdfsam.console.utils.perfix.PrefixParser;

/**
 * Test unit for the prefix parser
 * 
 * @author Andrea Vacondio
 * 
 */
public class PrefixParserTest extends TestCase {

    public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplexFILENUMBERComplex() {
        try {
            PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER##]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(2), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "020_02_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplexFILENUMBERComplexWithStart() {
        try {
            PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER###25]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(2), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "020_027_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * File number exceed the given patter
     */
    public void testGenerateFileNameIntegerIntegerBASENAMEComplexFILENUMBERComplex() {
        try {
            PrefixParser parser = new PrefixParser("[BASENAME]_[FILENUMBER##]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(222), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "filename_222_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplexFILENUMBERComplex2() {
        try {
            PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER##]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(222), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "020_222_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplex() {
        try {
            PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(1), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "020_1_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerIntegerCURRENTPAGESimple() {
        try {
            PrefixParser parser = new PrefixParser("[CURRENTPAGE]_[FILENUMBER]_test", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), new Integer(1), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "20_1_test.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerCURRENTPAGEComplex() {
        try {
            PrefixParser parser = new PrefixParser("[FILENUMBER]_test[CURRENTPAGE####]_", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), null, null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "[FILENUMBER]_test0020_.pdf", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerCURRENTPAGESimple() {
        try {
            PrefixParser parser = new PrefixParser("[FILENUMBER]_test[CURRENTPAGE]_", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), null, null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "[FILENUMBER]_test20_.pdf", actual);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameIntegerFILENUMBERSimpleWithStart() {
        try {
            PrefixParser parser = new PrefixParser("[FILENUMBER25]_test[CURRENTPAGE]_", "filename");
            FileNameRequest request = new FileNameRequest(null, new Integer(20), null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "45_test[CURRENTPAGE]_.pdf", actual);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameCURRENTPAGESimple() {
        try {
            PrefixParser parser = new PrefixParser("prefix_", "filename");
            FileNameRequest request = new FileNameRequest(new Integer(20), null, null);
            String actual = parser.generateFileName(request);
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "20_prefix_filename.pdf", actual);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileNameSimple() {
        try {
            PrefixParser parser = new PrefixParser("prefix_", "filename");
            String actual = parser.generateFileName();
            assertNotNull("Actual is null", actual);
            assertEquals("Actual is " + actual, "prefix_filename.pdf", actual);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateFileName() {
        try {
            PrefixParser parser = new PrefixParser("[TIMESTAMP]", "filename");
            String actual = parser.generateFileName();
            System.out.print(actual);
            assertNotNull("Actual is null", actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
