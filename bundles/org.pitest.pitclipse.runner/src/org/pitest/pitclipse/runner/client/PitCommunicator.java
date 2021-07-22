/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.client;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.server.PitServer;

/**
 * <p>Handles the connection with a running PIT application.</p>
 * 
 * <p>More specifically, a {@code PitCommunicator}:
 * <ol>
 *  <li>Ensures that the given server is connected to a {@link PitClient}
 *  <li>Sends the request to the client so that the PIT analysis can start
 *  <li>Waits for analysis results
 * </ol>
 */
public class PitCommunicator implements Runnable {

    private final PitRequest request;
    private final PitResultHandler resultHandler;
    private final PitServer server;

    /**
     * Creates a new communicator to ease the connection with a running PIT application.
     * 
     * @param server
     *          The server used to communicate with the PIT application.
     * @param request
     *          The parameters of the PIT analysis to launch.
     * @param resultHandler
     *          The handler used to process PIT results.
     */
    public PitCommunicator(PitServer server, PitRequest request, PitResultHandler resultHandler) {
        this.server = server;
        this.request = request;
        this.resultHandler = resultHandler;
    }

    @Override
    public void run() {
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
