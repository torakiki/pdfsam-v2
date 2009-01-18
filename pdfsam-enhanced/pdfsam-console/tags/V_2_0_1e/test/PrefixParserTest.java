import org.pdfsam.console.utils.PrefixParser;

import junit.framework.TestCase;

/**
 * Test unit for the prefix parser
 * @author Andrea Vacondio
 *
 */
public class PrefixParserTest extends TestCase {

	
	public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplexFILENUMBERComplex() {
		try{
			PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER##]_test", "filename");
			String actual = parser.generateFileName(new Integer(20), new Integer(2));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "020_02_test.pdf", actual);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
	
	/**
	 * File number exceed the given patter
	 */
	public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplexFILENUMBERComplex2() {
		try{
			PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER##]_test", "filename");
			String actual = parser.generateFileName(new Integer(20), new Integer(222));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "020_222_test.pdf", actual);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testGenerateFileNameIntegerIntegerCURRENTPAGEComplex() {
		try{
			PrefixParser parser = new PrefixParser("[CURRENTPAGE###]_[FILENUMBER]_test", "filename");
			String actual = parser.generateFileName(new Integer(20), new Integer(1));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "020_1_test.pdf", actual);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testGenerateFileNameIntegerIntegerCURRENTPAGESimple() {
		try{
			PrefixParser parser = new PrefixParser("[CURRENTPAGE]_[FILENUMBER]_test", "filename");
			String actual = parser.generateFileName(new Integer(20), new Integer(1));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "20_1_test.pdf", actual);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
	
	public void testGenerateFileNameIntegerCURRENTPAGEComplex() {
		try{
			PrefixParser parser = new PrefixParser("[FILENUMBER]_test[CURRENTPAGE####]_", "filename");
			String actual = parser.generateFileName(new Integer(20));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "[FILENUMBER]_test0020_.pdf", actual);			
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

	public void testGenerateFileNameIntegerCURRENTPAGESimple() {
		try{
			PrefixParser parser = new PrefixParser("[FILENUMBER]_test[CURRENTPAGE]_", "filename");
			String actual = parser.generateFileName(new Integer(20));
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "[FILENUMBER]_test20_.pdf", actual);
			
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
	
	public void testGenerateFileNameSimple() {
		try{
			PrefixParser parser = new PrefixParser("prefix_", "filename");
			String actual = parser.generateFileName();
			assertNotNull("Actual is null", actual);
			assertEquals("Actual is "+actual, "prefix_filename.pdf", actual);
			
		}catch(Exception e){
			fail(e.getMessage());
		}
	}
	
	public void testGenerateFileName() {
		try{
			PrefixParser parser = new PrefixParser("[TIMESTAMP]", "filename");
			String actual = parser.generateFileName();
			assertNotNull("Actual is null", actual);			
		}catch(Exception e){
			fail(e.getMessage());
		}
	}

}
