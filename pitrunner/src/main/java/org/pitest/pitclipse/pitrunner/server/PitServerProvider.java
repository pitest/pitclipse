package org.pitest.pitclipse.pitrunner.server;

public interface PitServerProvider {
	PitServer newServerFor(int port);
}
