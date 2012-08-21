package org.pitest.pitclipse.ui.util;

public final class VerifyUtil {

	private VerifyUtil() {
	}

	public static <T> boolean isNull(T someObject) {
		return null == someObject;
	}

	public static <T> boolean isNotNull(T someObject) {
		return null != someObject;
	}

}
