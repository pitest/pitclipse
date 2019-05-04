package org.pitest.pitclipse.core.result;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.core.launch.UpdateMutations;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.model.ModelBuilder;
import org.pitest.pitclipse.runner.model.MutationsModel;

public class MutationsModelNotifier implements ResultNotifier<PitResults> {

    private static final ModelBuilder MODEL_BUILDER = new ModelBuilder(JdtStructureService.INSTANCE);

    @Override
    public void handleResults(PitResults results) {
        MutationsModel mutationModel = MODEL_BUILDER.buildFrom(results);
        Job.create("Reporting detected mutations", monitor -> {
            new UpdateMutations(mutationModel).run();
            return new Status(IStatus.OK, "org.pitest.pitclipse.core", "ok");
        }).schedule();
    }
}
