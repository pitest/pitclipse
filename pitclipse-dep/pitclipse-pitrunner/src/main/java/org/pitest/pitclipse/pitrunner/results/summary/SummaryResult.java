package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

public class SummaryResult {

	public static final SummaryResult EMPTY = new SummaryResult();
	private final ImmutableList<ClassSummary> summaries;

	private SummaryResult() {
		this(ImmutableList.<ClassSummary> of());
	}

	private SummaryResult(ImmutableList<ClassSummary> summaries) {
		this.summaries = summaries;
	}

	public SummaryResult update(ClassSummary classSummary) {
		Builder<ClassSummary> b = ImmutableList.builder();
		b.addAll(getSummaries());
		b.add(classSummary);
		return new SummaryResult(b.build());
	}

	@Override
	public String toString() {
		return "SummaryResult [summaries=" + getSummaries() + "]";
	}

	public ImmutableList<ClassSummary> getSummaries() {
		return summaries;
	}
}
