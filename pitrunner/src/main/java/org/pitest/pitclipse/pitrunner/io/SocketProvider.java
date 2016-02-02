package org.pitest.pitclipse.pitrunner.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketProvider {

	private static final int DEFAULT_TIMEOUT = 20000;

	public ObjectStreamSocket listen(int portNumber) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Listening on: " + serverSocket.getInetAddress() + ":" + portNumber);
			Socket connection = serverSocket.accept();
			return ObjectStreamSocket.make(connection);
		} catch (IOException e) {
			throw new SocketCreationException(e);
		} finally {
			ensureClosed(serverSocket);
		}
	}

	public ObjectStreamSocket connectTo(int portNumber) {
		try {
			InetAddress localhost = InetAddress.getByName(null);
			Socket socket = new Socket();
			System.out.println("Connecting to: " + localhost + ":" + portNumber);
			SocketAddress endpoint = new InetSocketAddress(localhost, portNumber);
			socket.connect(endpoint, DEFAULT_TIMEOUT);
			return ObjectStreamSocket.make(socket);
		} catch (Exception e) {
			throw new SocketCreationException(e);
		}
	}

	public int getFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new SocketCreationException(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void ensureClosed(ServerSocket serverSocket) {
		try {
			if (null != serverSocket) {
				serverSocket.close();
			}
		} catch (IOException e) {
			throw new SocketCreationException(e);
		}
	}

}
