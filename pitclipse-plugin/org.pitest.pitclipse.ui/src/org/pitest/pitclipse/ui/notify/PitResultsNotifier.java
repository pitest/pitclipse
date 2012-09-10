package org.pitest.pitclipse.ui.notify;

import java.io.File;

import org.pitest.pitclipse.core.extension.point.PitCoreResults;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.view.PitView;
import org.pitest.pitclipse.ui.view.PitViewFinder;

public class PitResultsNotifier implements ResultNotifier<PitCoreResults> {

	public void handleResults(PitCoreResults results) {
		PitView view = new PitViewFinder().getView();
		view.update(new File(results.getUri()));
	}

}
