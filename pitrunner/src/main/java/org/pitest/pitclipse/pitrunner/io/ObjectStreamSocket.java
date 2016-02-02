package org.pitest.pitclipse.pitrunner.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.pitest.pitclipse.reloc.guava.annotations.VisibleForTesting;

public class ObjectStreamSocket implements Closeable {

	private final Socket underlyingSocket;
	private final ObjectInputStream inputStream;
	private final ObjectOutputStream outputStream;

	private ObjectStreamSocket(Socket underlyingSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
		this.underlyingSocket = underlyingSocket;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public static ObjectStreamSocket make(Socket underlyingSocket) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(underlyingSocket.getOutputStream());
			ObjectInputStream inputStream = new ObjectInputStream(underlyingSocket.getInputStream());
			return make(underlyingSocket, inputStream, outputStream);
		} catch (IOException e) {
			throw new StreamInitialisationException(e);
		}
	}

	@VisibleForTesting
	static ObjectStreamSocket make(Socket underlyingSocket, ObjectInputStream inputStream,
			ObjectOutputStream outputStream) {
		return new ObjectStreamSocket(underlyingSocket, inputStream, outputStream);
	}

	@SuppressWarnings("unchecked")
	public <T> T read() {
		try {
			return (T) inputStream.readObject();
		} catch (Exception e) {
			throw new ReadException(e);
		}
	}

	public <T> void write(T someObject) {
		try {
			outputStream.writeObject(someObject);
			outputStream.flush();
		} catch (Exception e) {
			throw new WriteException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			tryCloseStreams();
		} finally {
			closeSocket();
		}
	}

	private void tryCloseStreams() throws IOException {
		try {
			tryCloseOutputStream();
		} finally {
			tryCloseInputStream();
		}
	}

	private void tryCloseInputStream() throws IOException {
		inputStream.close();
	}

	private void tryCloseOutputStream() throws IOException {
		outputStream.close();
	}

	private void closeSocket() throws IOException {
		underlyingSocket.close();
	}

	public static final class StreamInitialisationException extends RuntimeException {
		private static final long serialVersionUID = 489374857284580542L;

		public StreamInitialisationException(IOException e) {
			super(e);
		}

	}

	public static final class ReadException extends RuntimeException {
		private static final long serialVersionUID = -7217167622171380199L;

		public ReadException(Exception e) {
			super(e);
		}
	}

	public static final class WriteException extends RuntimeException {
		private static final long serialVersionUID = -7517131322531593708L;

		public WriteException(Exception e) {
			super(e);
		}
	}
}
