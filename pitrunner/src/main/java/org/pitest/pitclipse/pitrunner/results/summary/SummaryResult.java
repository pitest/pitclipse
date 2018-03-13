package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.pitclipse.reloc.guava.base.MoreObjects;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

import java.io.Serializable;
import java.util.List;

public class SummaryResult implements Serializable {

    public static final SummaryResult EMPTY = new SummaryResult();
    private static final long serialVersionUID = 5598868063090306204L;
    private final ImmutableList<ClassSummary> summaries;

    private SummaryResult() {
        this(ImmutableList.<ClassSummary>of());
    }

    private SummaryResult(ImmutableList<ClassSummary> summaries) {
        this.summaries = summaries;
    }

    public SummaryResult update(ClassSummary classSummary) {
        Builder<ClassSummary> builder = ImmutableList.builder();
        builder.addAll(getSummaries());
        builder.add(classSummary);
        return new SummaryResult(builder.build());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("summaries", summaries).toString();
    }

    public List<ClassSummary> getSummaries() {
        return summaries;
    }
}
