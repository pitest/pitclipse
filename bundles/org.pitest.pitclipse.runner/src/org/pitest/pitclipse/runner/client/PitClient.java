package org.pitest.pitclipse.pitrunner.client;

import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.io.ObjectStreamSocket;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;
import org.pitest.pitclipse.reloc.guava.annotations.VisibleForTesting;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.base.Optional;

import java.io.Closeable;
import java.io.IOException;

public class PitClient implements Closeable {

    private final int portNumber;
    private final SocketProvider socketProvider;
    private Optional<ObjectStreamSocket> socket = Optional.absent();

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
        socket.transform(write(results));
    }

    private Function<ObjectStreamSocket, PitResults> write(final PitResults results) {
        return new Function<ObjectStreamSocket, PitResults>() {
            public PitResults apply(ObjectStreamSocket objectStreamSocket) {
                objectStreamSocket.write(results);
                return results;
            }
        };
    }

    public Optional<PitRequest> readRequest() {
        return socket.transform(read());
    }

    private Function<ObjectStreamSocket, PitRequest> read() {
        return new Function<ObjectStreamSocket, PitRequest>() {
            public PitRequest apply(ObjectStreamSocket objectStreamSocket) {
                return objectStreamSocket.read();
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (socket.isPresent()) {
            socket.get().close();
        }
    }
}