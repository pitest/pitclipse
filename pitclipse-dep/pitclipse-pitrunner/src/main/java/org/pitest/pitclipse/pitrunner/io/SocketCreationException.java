package org.pitest.pitclipse.pitrunner.io;


public final class SocketCreationException extends RuntimeException {
	private static final long serialVersionUID = 666278737722201018L;

	public SocketCreationException(Exception e) {
		super(e);
	}
}