package org.pitest.pitclipse.pitrunner;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PitClient implements Closeable {

	public static final class PitClientException extends RuntimeException {

		private static final long serialVersionUID = -2686066795318283762L;

		public PitClientException(Exception e) {
			super(e);
		}

	}

	private final int portNumber;
	private final SocketProvider socketProvider;
	private Socket socket;

	public PitClient(int portNumber) {
		this(portNumber, new SocketProvider());
	}

	public PitClient(int portNumber, SocketProvider socketProvider) {
		this.portNumber = portNumber;
		this.socketProvider = socketProvider;
	}

	public void connect() {
		socket = socketProvider.createClientSocket(portNumber);
	}

	public void sendOptions(PitOptions options) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(
					socket.getOutputStream());
			stream.writeObject(options);
		} catch (IOException e) {
			throw new PitClientException(e);
		}
	}

	public void close() {
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
				throw new PitClientException(e);
			} finally {
				socket = null;
			}
		}
	}

	public PitResults receiveResults() {
		try {
			ObjectInputStream stream = new ObjectInputStream(
					socket.getInputStream());
			return (PitResults) stream.readObject();
		} catch (Exception e) {
			throw new PitClientException(e);
		}
	}

}
