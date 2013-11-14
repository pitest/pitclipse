package org.pitest.pitclipse.core.result;

import org.eclipse.swt.widgets.Display;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.core.launch.UpdateMutations;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.model.ModelBuilder;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;

public class MutationsModelNotifier implements ResultNotifier<PitResults> {

	private static final ModelBuilder MODEL_BUILDER = new ModelBuilder(JdtStructureService.INSTANCE);

	@Override
	public void handleResults(PitResults results) {
		MutationsModel mutationModel = MODEL_BUILDER.buildFrom(results);
		Display.getDefault().asyncExec(new UpdateMutations(mutationModel));
	}
}
