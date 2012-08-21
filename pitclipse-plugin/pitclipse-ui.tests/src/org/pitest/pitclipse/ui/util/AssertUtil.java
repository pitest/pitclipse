package org.pitest.pitclipse.ui.util;

import static org.junit.Assert.assertEquals;

public class AssertUtil {

	private static final double TOLERANCE = 0.00001d;

	private AssertUtil() {
	}

	public static void assertDoubleEquals(double expected, double actual) {
		assertEquals(expected, actual, TOLERANCE);
	}
}
