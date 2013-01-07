package org.pitest.pitclipse.pitrunner;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
		} catch (IOException e) {
			throw new SocketCreationException(e);
		}
	}

	public PitOptions readOptions() {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(
					connection.getInputStream());
			return (PitOptions) inputStream.readObject();
		} catch (Exception e) {
			throw new PitServerException(e);
		}
	}

	public void close() throws IOException {
		if (null != connection) {
			connection.close();
			connection = null;
		}

		if (null != serverSocket) {
			serverSocket.close();
			serverSocket = null;
		}
	}

	public void sendResults(PitResults results) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					connection.getOutputStream());
			outputStream.writeObject(results);
		} catch (Exception e) {
			throw new PitServerException(e);
		}
	}

}
