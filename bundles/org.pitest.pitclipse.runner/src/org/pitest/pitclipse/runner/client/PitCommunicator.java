package org.pitest.pitclipse.runner.client;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.server.PitServer;

public class PitCommunicator implements Runnable {

    private final PitRequest request;
    private final PitResultHandler resultHandler;
    private final PitServer server;

    public PitCommunicator(PitServer server, PitRequest request, PitResultHandler resultHandler) {
        this.server = server;
        this.request = request;
        this.resultHandler = resultHandler;
    }

    @Override
    public void run() {
        // TODO Enhance this to make objects immutable & use try-with-resource
        // (exceptions may be swallowed her)
        try {
            server.listen();
            server.sendRequest(request);
            resultHandler.handle(server.receiveResults());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            server.close();
        }
    }
}