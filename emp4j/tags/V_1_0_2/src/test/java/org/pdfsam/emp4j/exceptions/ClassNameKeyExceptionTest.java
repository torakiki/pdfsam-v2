package org.pdfsam.emp4j.exceptions;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Test unit for the ClassNameKeyException
 * @author Andrea Vacondio
 *
 */
public class ClassNameKeyExceptionTest {

	@Test
	public void messageTest(){
		try {
			throw new ClassNameKeyException(1);
		} catch (ClassNameKeyException e) {
			Assert.assertEquals("TEST001 - Unknown exception.", e.getMessage());
		}

		try {
			throw new ClassNameKeyException(2, new String[]{"Test"});
		} catch (ClassNameKeyException e) {
			Assert.assertEquals("TEST002 - Unknown exception Test.", e.getMessage());
		}

		try {
			throw new ClassNameKeyException(3);
		} catch (ClassNameKeyException e) {
			Assert.assertEquals("TEST003 - ", e.getMessage());
		}
	}
}
