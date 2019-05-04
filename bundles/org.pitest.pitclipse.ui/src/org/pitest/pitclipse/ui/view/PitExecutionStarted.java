package org.pitest.pitclipse.ui.view;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.ui.view.mutations.MutationsView;

import static org.pitest.pitclipse.runner.model.MutationsModel.EMPTY_MODEL;

public class PitExecutionStarted implements ResultNotifier<PitRuntimeOptions> {
    @Override
    public void handleResults(PitRuntimeOptions options) {
        MutationsView mutationsView = PitViewFinder.INSTANCE.getMutationsView();
        mutationsView.updateWith(EMPTY_MODEL);
    }
}
