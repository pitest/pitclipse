package org.pitest.pitclipse.ui.behaviours.pageobjects;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.pitest.pitclipse.ui.behaviours.StepException;
import org.pitest.pitclipse.ui.swtbot.PitNotifier;
import org.pitest.pitclipse.ui.swtbot.PitResultsView;

public class PitView {

	private final SWTWorkbenchBot bot;
	private PitResultsView lastResults = null;

	public PitView(SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void waitForUpdate() {
		PitNotifier notifier = PitNotifier.INSTANCE;
		try {
			lastResults = notifier.getResults();
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
