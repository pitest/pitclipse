package org.pitest.pitclipse.runner.io;

import com.google.common.base.Optional;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.ReturnStatus.KABOOM;
import static org.pitest.pitclipse.runner.io.SocketProviderIntegrationTestFixture.ReturnStatus.SUCCESS;

class SocketProviderIntegrationTestFixture {

    static final String ECHO = "echo";

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    enum ReturnStatus {
        SUCCESS,
        KABOOM
    }

    private SocketProviderIntegrationTestFixture() {}
    
    static int aFreePort() { 
        return new SocketProvider().getFreePort(); 
    }
    
    static Optional<ObjectStreamSocket> connectTo(int port) { 
        return new SocketProvider().connectTo(port); 
    }
    
    static Future<ReturnStatus> listenOn(final int port) {
        Callable<ReturnStatus> socketTest = new Callable<ReturnStatus>() {
            public ReturnStatus call() throws Exception {
                System.out.println("Listening on port " + port);
                ObjectStreamSocket server = new SocketProvider().listen(port);
                try {
                    server.write(ECHO);
                    return SUCCESS;
                } catch (Exception e) {
                    return KABOOM;
                } finally {
                    server.close();
                }
            }
        };

        return EXECUTOR_SERVICE.submit(socketTest);
    }

    static <T> Future<T> readMessageFrom(final int port) {
        Callable<T> messageReader = new Callable<T>() {
            public T call() throws Exception {
                ObjectStreamSocket client = connectTo(port).get();
                try {
                    return client.read();
                } finally {
                    client.close();
                }
            }
        };

        return EXECUTOR_SERVICE.submit(messageReader);
    }
}