package org.pitest.pitclipse.runner.server;

public enum PitServerProviderImpl implements PitServerProvider {
    INSTANCE;

    @Override
    public PitServer newServerFor(int port) {
        return new PitServer(port);
    }
}
