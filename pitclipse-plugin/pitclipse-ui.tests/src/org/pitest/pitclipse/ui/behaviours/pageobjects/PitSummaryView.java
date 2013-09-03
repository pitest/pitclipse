package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.pitest.pitclipse.ui.behaviours.StepException;
import org.pitest.pitclipse.ui.swtbot.PitNotifier;
import org.pitest.pitclipse.ui.swtbot.PitResultsView;

public class PitSummaryView {

	private PitResultsView lastResults = null;

	public PitSummaryView() {
	}

	public void waitForUpdate() {
		try {
			lastResults = PitNotifier.INSTANCE.getResults();
		} catch (InterruptedException e) {
			throw new StepException(e);
		}
	}

	public int getClassesTested() {
		return lastResults.getClassesTested();
	}

	public double getOverallCoverage() {
		return lastResults.getTotalCoverage();
	}

	public double getMutationCoverage() {
		return lastResults.getMutationCoverage();
	}
}
