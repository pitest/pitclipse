package org.pitest.pitclipse.ui.view;

import static org.pitest.pitclipse.pitrunner.model.MutationsModel.EMPTY_MODEL;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.view.mutations.PitMutationsView;

public class PitExecutionStarted implements ResultNotifier<PitRuntimeOptions> {
	@Override
	public void handleResults(PitRuntimeOptions options) {
		PitMutationsView mutationsView = new PitViewFinder().getMutationsView();
		mutationsView.updateWith(EMPTY_MODEL);
	}
}
