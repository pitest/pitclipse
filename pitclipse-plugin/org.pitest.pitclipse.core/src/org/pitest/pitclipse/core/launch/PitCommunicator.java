package org.pitest.pitclipse.core.launch;

import javax.annotation.concurrent.Immutable;

import org.eclipse.swt.widgets.Display;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.client.PitClient;

@Immutable
public class PitCommunicator implements Runnable {

	private final int portNumber;
	private final PitOptions options;

	public PitCommunicator(int portNumber, PitOptions options) {
		this.portNumber = portNumber;
		this.options = options;
	}

	public void run() {
		PitClient client = new PitClient(portNumber);
		try {
			client.connect();
			client.sendOptions(options);
			PitResults results = client.receiveResults();
			Display.getDefault().asyncExec(
					new UpdateExtensions(results.getResultFile()
							.getParentFile()));
		} finally {
			client.close();
		}
	}

}
