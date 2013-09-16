package org.pitest.pitclipse.pitrunner.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.client.PitClient.PitClientException;
import org.pitest.pitclipse.pitrunner.io.SocketCreationException;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

public class PitServer implements Closeable {

	public static final class PitServerException extends RuntimeException {

		private static final long serialVersionUID = -2686066795318283762L;

		public PitServerException(Exception e) {
			super(e);
		}

	}

	private final int port;
	private final SocketProvider socketProvider;
	private ServerSocket serverSocket;
	private Socket connection;
	private ObjectOutputStream objectOutputStream;

	public PitServer(int port, SocketProvider socketProvider) {
		this.port = port;
		this.socketProvider = socketProvider;
	}

	public PitServer(int port) {
		this(port, new SocketProvider());
	}

	public void listen() {
		serverSocket = socketProvider.createServerSocket(port);
		try {
			connection = serverSocket.accept();
			objectOutputStream = new ObjectOutputStream(
					connection.getOutputStream());
		} catch (IOException e) {
			throw new SocketCreationException(e);
		}
	}

	public void sendOptions(PitOptions options) {
		try {
			objectOutputStream.writeObject(options);
			objectOutputStream.flush();
		} catch (IOException e) {
			throw new PitClientException(e);
		}
	}

	@Override
	public void close() throws IOException {
		if (null != objectOutputStream) {
			try {
				objectOutputStream.close();
			} finally {
				objectOutputStream = null;
			}
		}

		if (null != connection) {
			try {
				connection.close();
			} finally {
				connection = null;
			}
		}

		if (null != serverSocket) {
			try {
				serverSocket.close();
			} finally {
				serverSocket = null;
			}
		}
	}

	public PitResults receiveResults() {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(connection.getInputStream());
			return (PitResults) stream.readObject();
		} catch (Exception e) {
			throw new PitServerException(e);
		} finally {
			if (null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new PitServerException(e);
				}
			}
		}
	}

}
