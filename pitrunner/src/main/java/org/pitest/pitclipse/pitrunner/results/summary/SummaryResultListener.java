package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.classinfo.ClassInfo;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.Dispatcher;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

import java.util.List;

public class SummaryResultListener implements MutationResultListener {

    private SummaryResult result = SummaryResult.EMPTY;
    private final Dispatcher<SummaryResult> dispatcher;
    private final CoverageDatabase coverage;

    public SummaryResultListener(Dispatcher<SummaryResult> dispatcher, CoverageDatabase coverage) {
        this.dispatcher = dispatcher;
        this.coverage = coverage;
    }

    @Override
    public void runStart() {
        result = SummaryResult.EMPTY;
    }

    @Override
    public void handleMutationResult(ClassMutationResults results) {
        List<ClassName> classUnderTest = ImmutableList.of(results.getMutatedClass());
        int coveredLines = coverage.getNumberOfCoveredLines(classUnderTest);
        for (ClassInfo info : coverage.getClassInfo(classUnderTest)) {
            ClassSummary classSummary = ClassSummary.from(results, info, coveredLines);
            result = result.update(classSummary);
        }
    }

    @Override
    public void runEnd() {
        dispatcher.dispatch(result);
    }
}