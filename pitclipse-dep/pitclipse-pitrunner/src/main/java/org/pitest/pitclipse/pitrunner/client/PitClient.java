package org.pitest.pitclipse.pitrunner.client;

import java.io.Closeable;
import java.io.IOException;

import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.io.ObjectStreamSocket;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;
import org.pitest.pitclipse.reloc.guava.annotations.VisibleForTesting;

public class PitClient implements Closeable {

	private final int portNumber;
	private final SocketProvider socketProvider;
	private ObjectStreamSocket socket;

	public PitClient(int portNumber) {
		this(portNumber, new SocketProvider());
	}

	@VisibleForTesting
	PitClient(int portNumber, SocketProvider socketProvider) {
		this.portNumber = portNumber;
		this.socketProvider = socketProvider;
	}

	public void connect() {
		socket = socketProvider.connectTo(portNumber);
	}

	public void sendResults(PitResults results) {
		socket.write(results);
	}

	public PitRequest readRequest() {
		return socket.read();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}
}
