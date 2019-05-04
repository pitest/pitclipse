package org.pitest.pitclipse.runner.server;

public interface PitServerProvider {
    PitServer newServerFor(int port);
}
