package org.pitest.pitclipse.pitrunner.service;

import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.model.ModelBuilder;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.pitrunner.server.PitServer;
import org.pitest.pitclipse.pitrunner.server.PitServerProvider;

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
