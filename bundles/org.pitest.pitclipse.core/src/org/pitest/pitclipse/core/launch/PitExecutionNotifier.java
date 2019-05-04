package org.pitest.pitclipse.core.launch;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.client.PitCommunicator;
import org.pitest.pitclipse.runner.client.PitResultHandler;
import org.pitest.pitclipse.runner.server.PitServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PitExecutionNotifier implements ResultNotifier<PitRuntimeOptions> {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void handleResults(PitRuntimeOptions runtimeOptions) {
        PitResultHandler resultHandler = new ExtensionPointResultHandler();
        PitServer server = new PitServer(runtimeOptions.getPortNumber());
        PitRequest request = PitRequest.builder().withPitOptions(runtimeOptions.getOptions())
                .withProjects(runtimeOptions.getMutatedProjects()).build();
        executorService.execute(new PitCommunicator(server, request, resultHandler));
    }
}
