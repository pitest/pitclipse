package org.pitest.pitclipse.core.launch;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.client.PitResultHandler;

public class ExtensionPointResultHandler implements PitResultHandler {

    public void handle(PitResults results) {
        Job.create("Reporting Pit results", monitor -> {
            new UpdateExtensions(results).run();
            return new Status(IStatus.OK, "org.pitest.pitclipse.core.launch", "ok");
        }).schedule();
    }

}
