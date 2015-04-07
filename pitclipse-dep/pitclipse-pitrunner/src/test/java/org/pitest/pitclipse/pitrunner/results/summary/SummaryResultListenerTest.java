package org.pitest.pitclipse.pitrunner.results.summary;

import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.aCoveredMutationOnFoo;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.aSummary;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestData.anUncoveredMutationOnFoo;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.empty;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.given;
import static org.pitest.pitclipse.pitrunner.results.summary.SummaryResultListenerTestSugar.givenNoMutations;

import org.junit.Ignore;
import org.junit.Test;

public class SummaryResultListenerTest {
	@Test
	public void noResultsReturnsAnEmptyResult() {
		givenNoMutations().whenPitIsExecuted().thenTheResultsAre(empty());
	}

	@Test
	public void anUncoveredMutationResultReturnsZeroCoverage() {
		given(anUncoveredMutationOnFoo()).whenPitIsExecuted().thenTheResultsAre(aSummary().withCoverageOf(0));
	}

	@Ignore
	@Test
	public void aCoveredMutationResultReturnsCoverage() {
		given(aCoveredMutationOnFoo()).whenPitIsExecuted().thenTheResultsAre(aSummary().withCoverageOf(100));
	}
}