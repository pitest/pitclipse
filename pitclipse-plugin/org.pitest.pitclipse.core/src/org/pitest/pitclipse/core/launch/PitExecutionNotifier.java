package org.pitest.pitclipse.core.launch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;
import org.pitest.pitclipse.pitrunner.client.PitClient;
import org.pitest.pitclipse.pitrunner.client.PitClientProvider;
import org.pitest.pitclipse.pitrunner.client.PitCommunicator;
import org.pitest.pitclipse.pitrunner.client.PitResultHandler;

public class PitExecutionNotifier implements ResultNotifier<PitRuntimeOptions> {

	private static final ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	public void handleResults(PitRuntimeOptions runtimeOptions) {
		PitResultHandler resultHandler = new ExtensionPointResultHandler();
		PitClient client = new PitClientProvider().getClient(runtimeOptions
				.getPortNumber());
		executorService.execute(new PitCommunicator(client, runtimeOptions
				.getOptions(), resultHandler));
	}

}
