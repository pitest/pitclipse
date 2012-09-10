package org.pitest.pitclipse.ui.swtbot;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.extension.point.PitUiUpdate;
import org.pitest.pitclipse.ui.swtbot.ResultsParser.Summary;

public class PitResultNotifier implements ResultNotifier<PitUiUpdate> {
	public void handleResults(PitUiUpdate results) {
		ResultsParser parser = new ResultsParser(results.getHtml());
		Summary summary = parser.getSummary();
		PitResultsView view = PitResultsView.builder()
				.withClassesTested(summary.getClasses())
				.withTotalCoverage(summary.getCodeCoverage())
				.withMutationCoverage(summary.getMutationCoverage()).build();
		try {
			PitNotifier.INSTANCE.notifyResults(view);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
