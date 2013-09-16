package org.pitest.pitclipse.pitrunner.client;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.server.PitServer;
import org.pitest.pitclipse.pitrunner.server.PitServer.PitServerException;

@Immutable
public class PitCommunicator implements Runnable {

	private final PitOptions options;
	private final PitResultHandler resultHandler;
	private final PitServer server;

	public PitCommunicator(PitServer server, PitOptions options,
			PitResultHandler resultHandler) {
		this.server = server;
		this.options = options;
		this.resultHandler = resultHandler;
	}

	@Override
	public void run() {
		try {
			server.listen();
			server.sendOptions(options);
			resultHandler.handle(server.receiveResults());
		} finally {
			try {
				server.close();
			} catch (IOException e) {
				throw new PitServerException(e);
			}
		}
	}

}
