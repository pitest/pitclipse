package org.pitest.pitclipse.ui.notify;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.ui.view.PitView;
import org.pitest.pitclipse.ui.view.PitViewFinder;

public class PitResultsNotifier implements ResultNotifier<PitResults> {

	public void handleResults(PitResults results) {
		PitView view = new PitViewFinder().getSummaryView();
		view.update(results.getHtmlResultFile());
	}

}
