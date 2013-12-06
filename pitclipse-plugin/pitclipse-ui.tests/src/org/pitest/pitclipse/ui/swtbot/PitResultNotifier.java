package org.pitest.pitclipse.ui.swtbot;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.PitclipseTestActivator;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;
import org.pitest.pitclipse.ui.swtbot.ResultsParser.Summary;

public class PitResultNotifier implements ResultNotifier<PitUiUpdate> {
	@Override
	public void handleResults(PitUiUpdate updateEvent) {
		if (testsAreInProgress())
			notifiyTestsOfHtmlResults(updateEvent);
	}

	private void notifiyTestsOfHtmlResults(PitUiUpdate updateEvent) {
		PitResultsView view = buildResultsView(updateEvent);
		tryNotifyResults(view);
	}

	private PitResultsView buildResultsView(PitUiUpdate results) {
		ResultsParser parser = new ResultsParser(results.getHtml());
		Summary summary = parser.getSummary();
		PitResultsView view = PitResultsView.builder().withClassesTested(summary.getClasses())
				.withTotalCoverage(summary.getCodeCoverage()).withMutationCoverage(summary.getMutationCoverage())
				.build();
		return view;
	}

	private void tryNotifyResults(PitResultsView view) {
		try {
			PitNotifier.INSTANCE.notifyResults(view);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean testsAreInProgress() {
		return PitclipseTestActivator.getDefault().areTestsInProgress();
	}
}
