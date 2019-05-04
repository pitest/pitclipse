package org.pitest.pitclipse.runner.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

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

    // TODO [Refactor] Consider returning a Connection object instead of mutating this
    public void connect() {
        socket = socketProvider.connectTo(portNumber);
    }

    public void sendResults(PitResults results) {
        socket.transform(write(results));
    }

    private Function<ObjectStreamSocket, PitResults> write(final PitResults results) {
        return objectStreamSocket -> {
            objectStreamSocket.write(results);
            return results;
        };
    }

    public Optional<PitRequest> readRequest() {
        return socket.transform(ObjectStreamSocket::read);
    }

    @Override
    public void close() throws IOException {
        if (socket.isPresent()) {
            socket.get().close();
        }
    }
}