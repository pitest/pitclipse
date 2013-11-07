package org.pitest.pitclipse.pitrunner.server;

public enum PitServerProviderImpl implements PitServerProvider {
	INSTANCE;

	@Override
	public PitServer newServerFor(int port) {
		return new PitServer(port);
	}
}
