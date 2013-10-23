package org.pitest.pitclipse.pitrunner;

import java.io.ByteArrayOutputStream;

import org.pitest.pitclipse.pitrunner.client.PitClient;
import org.pitest.pitclipse.pitrunner.server.PitServer;

public final class PitRunnerTestContext {

	private int portNumber;
	private PitRequest request;
	private PitServer pitServer;
	private PitRequest transmittedRequest;
	private PitClient client;
	private PitResults results;
	private PitResults transmittedResults;
	private ByteArrayOutputStream outputStream;

	public PitRequest getRequest() {
		return request;
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

	public void setRequest(PitRequest request) {
		this.request = request;
	}

	public PitServer getPitServer() {
		return pitServer;
	}

	public void setTransmittedRequest(PitRequest pitRequest) {
		this.transmittedRequest = pitRequest;
	}

	public PitRequest getTransmittedRequest() {
		return transmittedRequest;
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

	public void setOutputStream(ByteArrayOutputStream byteOutputStream) {
		this.outputStream = byteOutputStream;

	}

	public ByteArrayOutputStream getOutputStream() {
		return outputStream;
	}

}