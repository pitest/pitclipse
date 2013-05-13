package org.pitest.pitclipse.pitrunner;

import org.pitest.pitclipse.pitrunner.client.PitClient;
import org.pitest.pitclipse.pitrunner.server.PitServer;

public final class PitRunnerTestContext {

	private int portNumber;
	private PitOptions options;
	private PitServer pitServer;
	private PitOptions transmittedOptions;
	private PitClient client;
	private PitResults results;
	private PitResults transmittedResults;

	public PitOptions getOptions() {
		return options;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPitServer(PitServer pitServer) {
		this.pitServer = pitServer;
	}

	public void setPortNumber(int port) {
		portNumber = port;
	}

	public void setOptions(PitOptions options) {
		this.options = options;
	}

	public PitServer getPitServer() {
		return pitServer;
	}

	public void setTransmittedOptions(PitOptions transmittedOptions) {
		this.transmittedOptions = transmittedOptions;
	}

	public PitOptions getTransmittedOptions() {
		return transmittedOptions;
	}

	public void setPitClient(PitClient client) {
		this.client = client;
	}

	public PitClient getPitClient() {
		return client;
	}

	public PitResults getResults() {
		return results;
	}

	public void setResults(PitResults results) {
		this.results = results;
	}

	public PitResults getTransmittedResults() {
		return transmittedResults;
	}

	public void setTransmittedResults(PitResults transmittedResults) {
		this.transmittedResults = transmittedResults;
	}

}