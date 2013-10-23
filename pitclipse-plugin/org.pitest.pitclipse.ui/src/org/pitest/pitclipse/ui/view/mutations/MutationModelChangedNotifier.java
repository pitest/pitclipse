package org.pitest.pitclipse.ui.view.mutations;

import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.ui.view.PitViewFinder;

public class MutationModelChangedNotifier implements ResultNotifier<MutationsModel> {

	@Override
	public void handleResults(MutationsModel results) {
		PitMutationsView mutationsView = new PitViewFinder().getMutationsView();
		mutationsView.updateWith(results);
	}

}
