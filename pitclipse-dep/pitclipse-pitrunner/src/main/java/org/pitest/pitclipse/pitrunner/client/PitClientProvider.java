package org.pitest.pitclipse.pitrunner.client;

import javax.annotation.concurrent.Immutable;

@Immutable
public class PitClientProvider {

	public PitClient getClient(int portNumber) {
		return new PitClient(portNumber);
	}

}
