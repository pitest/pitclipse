package org.pitest.pitclipse.ui.util;

public class StepUtil {

	private StepUtil() {
	}

	public static void safeSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// Swallowed - no pretence of handling this. Unlikely to hit this in
			// test code.
		}
	}
}
