package org.pitest.pitclipse.pitrunner.client;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions;

@Immutable
public class PitCommunicator implements Runnable {

	private final PitOptions options;
	private final PitResultHandler resultHandler;
	private final PitClient client;

	public PitCommunicator(PitClient client, PitOptions options,
			PitResultHandler resultHandler) {
		this.client = client;
		this.options = options;
		this.resultHandler = resultHandler;
	}

	public void run() {
		try {
			client.connect();
			client.sendOptions(options);
			resultHandler.handle(client.receiveResults());
		} finally {
			client.close();
		}
	}

}
