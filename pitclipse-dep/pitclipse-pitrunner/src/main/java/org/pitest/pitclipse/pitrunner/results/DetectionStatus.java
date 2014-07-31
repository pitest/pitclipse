package org.pitest.pitclipse.pitrunner.results;

public enum DetectionStatus {

	KILLED, SURVIVED, TIMED_OUT, NON_VIABLE, MEMORY_ERROR, NOT_STARTED, STARTED, RUN_ERROR, NO_COVERAGE;

	public static DetectionStatus fromValue(String v) {
		return valueOf(v);
	}

}
