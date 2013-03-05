package org.pitest.pitclipse.pitrunner.io;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SocketProvider {

	private static final int DEFAULT_TIMEOUT = 1000;
	private static final int MAX_RETRIES = 20;

	public ServerSocket createServerSocket(int portNumber) {
		try {
			return new ServerSocket(portNumber);
		} catch (IOException e) {
			throw new SocketCreationException(e);
		}
	}

	public Socket createClientSocket(int portNumber) {
		int retryCount = 0;
		while (true) {
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				Socket socket = new Socket();
				SocketAddress endpoint = new InetSocketAddress(localhost,
						portNumber);
				socket.connect(endpoint, DEFAULT_TIMEOUT);
				return socket;
			} catch (Exception e) {
				if (retryCount < MAX_RETRIES) {
					try {
						sleep(DEFAULT_TIMEOUT);
					} catch (InterruptedException ie) {
						currentThread().interrupt();
					}
					retryCount++;
				} else {
					throw new SocketCreationException(e);
				}
			}
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

}
