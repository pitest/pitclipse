package org.pitest.pitclipse.ui.behaviours.steps;

import java.util.Date;
import java.util.Random;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

public enum TestFactory {
	TEST_FACTORY;

	private static final String ALPHA_NUMS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
	private final Random random = new Random(new Date().getTime());

	public DetectionStatus aDetectionStatus() {
		DetectionStatus[] statuses = DetectionStatus.values();
		return aRandomElementOf(statuses);
	}

	private <T> T aRandomElementOf(T[] elements) {
		if (null == elements || elements.length == 0) {
			return null;
		}
		int randomIndex = random.nextInt(elements.length);
		return elements[randomIndex];
	}

	public String aStringOfLength(int length) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char randomChar = ALPHA_NUMS.charAt(random.nextInt(ALPHA_NUMS.length()));
			result.append(randomChar);
		}
		return result.toString();
	}

	public String aPackage() {
		return aStringOfLength(3) + "." + aStringOfLength(4) + "." + aStringOfLength(5);
	}

	public String aClass() {
		return aStringOfLength(10);
	}

	public int aRandomInt() {
		return random.nextInt();
	}

	public boolean aRandomBoolean() {
		return random.nextBoolean();
	}
}
