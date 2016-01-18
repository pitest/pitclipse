package org.pitest.pitclipse.pitrunner.results.summary;

import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.aCoveredMutationOnFoo;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.aSummary;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.anEmptyCoverageDatabase;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.anUncoveredMutationOnFoo;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.fooHasFullLineCoverage;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.fooHasNoLineCoverage;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.empty;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.CoverageState.given;

import org.junit.Test;
import org.pitest.pitclipse.example.Foo;

public class SummaryResultListenerTest {
	@Test
	public void noResultsReturnsAnEmptyResult() {
		given(anEmptyCoverageDatabase()).andNoMutations().whenPitIsExecuted().thenTheResultsAre(empty());
	}

	@Test
	public void anUncoveredMutationResultReturnsZeroLineCoverage() {
		given(fooHasNoLineCoverage()).and(anUncoveredMutationOnFoo()).whenPitIsExecuted()
				.thenTheResultsAre(aSummary().withCoverageOf(Foo.class.getCanonicalName(), 0, 0));
	}

	@Test
	public void aCoveredMutationResultReturnsLineCoverage() {
		given(fooHasFullLineCoverage()).and(aCoveredMutationOnFoo()).whenPitIsExecuted()
				.thenTheResultsAre(aSummary().withCoverageOf(Foo.class.getCanonicalName(), 100, 100));
	}
}