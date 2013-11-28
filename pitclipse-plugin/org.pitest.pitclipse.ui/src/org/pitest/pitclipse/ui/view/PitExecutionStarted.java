package org.pitest.pitclipse.ui.view;

import static org.pitest.pitclipse.pitrunner.model.MutationsModel.EMPTY_MODEL;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.view.mutations.MutationsView;

public class PitExecutionStarted implements ResultNotifier<PitRuntimeOptions> {
	@Override
	public void handleResults(PitRuntimeOptions options) {
		MutationsView mutationsView = PitViewFinder.INSTANCE.getMutationsView();
		mutationsView.updateWith(EMPTY_MODEL);
	}
}
