package org.pitest.pitclipse.runner.results.summary;

import org.junit.Test;
import org.pitest.pitclipse.example.Foo;

import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.aCoveredMutationOnFoo;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.aSummary;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.anEmptyCoverageDatabase;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.anUncoveredMutationOnFoo;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.fooHasFullLineCoverage;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.fooHasNoLineCoverage;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.CoverageState.given;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.empty;

public class SummaryResultListenerTest {
    @Test
    public void noResultsReturnsAnEmptyResult() {
        given(anEmptyCoverageDatabase())
            .andNoMutations()
            .whenPitIsExecuted()
            .thenTheResultsAre(empty());
    }

    @Test
    public void anUncoveredMutationResultReturnsZeroLineCoverage() {
        given(fooHasNoLineCoverage())
                .and(anUncoveredMutationOnFoo())
                .whenPitIsExecuted()
                .thenTheResultsAre(
                        aSummary().withCoverageOf(Foo.class.getCanonicalName(), 0, 1, 0, 1)
            );
    }

    @Test
    public void aCoveredMutationResultReturnsLineCoverage() {
        given(fooHasFullLineCoverage())
                .and(aCoveredMutationOnFoo())
                .whenPitIsExecuted()
                .thenTheResultsAre(
                        aSummary().withCoverageOf(Foo.class.getCanonicalName(), 1, 1, 1, 1)
            );
    }
}