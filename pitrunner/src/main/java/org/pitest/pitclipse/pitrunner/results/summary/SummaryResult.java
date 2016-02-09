package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SummaryResult implements Serializable {

	public static final SummaryResult EMPTY = new SummaryResult();
	private static final long serialVersionUID = 5598868063090306204L;
	private final ImmutableList<ClassSummary> summaries;

	private SummaryResult() {
		this(ImmutableList.<ClassSummary>of());
	}

	private SummaryResult(ImmutableList<ClassSummary> summaries) { this.summaries = summaries;	}

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

	public List<ClassSummary> getSummaries() {
		return summaries;
	}
}
