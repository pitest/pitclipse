package org.pitest.pitclipse.pitrunner.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

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
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

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

	public void sendResults(PitResults results) {
		try {
			if (null == outputStream) {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
			}
			outputStream.writeObject(results);
			outputStream.flush();
		} catch (Exception e) {
			throw new PitClientException(e);
		}
	}

	public PitOptions readOptions() {
		try {
			if (null == inputStream) {
				inputStream = new ObjectInputStream(socket.getInputStream());
			}
			return (PitOptions) inputStream.readObject();
		} catch (Exception e) {
			throw new PitClientException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (null != inputStream) {
				inputStream.close();
			}
			if (null != outputStream) {
				outputStream.close();
			}
			if (null != socket) {
				socket.close();
			}
		} finally {
			inputStream = null;
			outputStream = null;
			socket = null;
		}
	}
}
