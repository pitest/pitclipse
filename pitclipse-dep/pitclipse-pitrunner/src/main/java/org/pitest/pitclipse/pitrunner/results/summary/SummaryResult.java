package org.pitest.pitclipse.pitrunner.results.summary;

public class SummaryResult {

	public static final SummaryResult EMPTY = new SummaryResult(0);
	private final int coverage;

	public SummaryResult(int coverage) {
		this.coverage = coverage;
	}

	public SummaryResult update(ClassSummary classSummary) {
		return this;
	}

}
