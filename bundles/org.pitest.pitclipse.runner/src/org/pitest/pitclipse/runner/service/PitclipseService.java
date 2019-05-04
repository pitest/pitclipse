package org.pitest.pitclipse.runner.service;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.model.ModelBuilder;
import org.pitest.pitclipse.runner.model.MutationsModel;
import org.pitest.pitclipse.runner.server.PitServer;
import org.pitest.pitclipse.runner.server.PitServerProvider;

public class PitclipseService {
    private final PitServerProvider serverProvider;
    private final ModelBuilder modelBuilder;

    public PitclipseService(PitServerProvider serverProvider, ModelBuilder modelBuilder) {
        this.serverProvider = serverProvider;
        this.modelBuilder = modelBuilder;
    }

    public MutationsModel analyse(int port, PitRequest request) {
        PitServer server = serverProvider.newServerFor(port);
        server.sendRequest(request);
        PitResults results = server.receiveResults();
        return modelBuilder.buildFrom(results);
    }
}
