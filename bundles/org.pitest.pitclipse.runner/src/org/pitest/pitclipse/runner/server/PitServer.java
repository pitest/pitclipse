package org.pitest.pitclipse.pitrunner.server;

import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.io.ObjectStreamSocket;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

import java.io.Closeable;
import java.io.IOException;

public class PitServer implements Closeable {

    private final int port;
    private final SocketProvider socketProvider;
    private ObjectStreamSocket socket;

    public PitServer(int port, SocketProvider socketProvider) {
        this.port = port;
        this.socketProvider = socketProvider;
    }

    public PitServer(int port) {
        this(port, new SocketProvider());
    }

    public void listen() {
        socket = socketProvider.listen(port);
    }

    public void sendRequest(PitRequest request) {
        socket.write(request);
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IllegalStateException("Could not close socket", e);
        }
    }

    public PitResults receiveResults() {
        return socket.read();
    }
}