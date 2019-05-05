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

package org.pitest.pitclipse.runner.server;

import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.client.PitClient;
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>A server used to communicate with a running PIT application.</p>
 *
 * <p>More specifically, it allows to:
 * <ul>
 *  <li>{@link #sendRequest(PitRequest) send} a {@link PitRequest request} 
 *  to the {@link PitClient PIT client} to parameterize and launch the analyze
 *  <li>{@link #receiveResults() receive} the results of PIT's analysis
 * </ul>
 * 
 * <p>This server is supposed to be launched from Eclipse side.</p>
 */
public class PitServer implements Closeable {

    private final int port;
    private final SocketProvider socketProvider;
    private ObjectStreamSocket socket;

    public PitServer(int port, SocketProvider socketProvider) {
        this.port = port;
        this.socketProvider = socketProvider;
    }

    /**
     * Creates a new server that uses given port to communicate
     * with a running PIT application.
     * 
     * @param port
     *          The port used to communicate with the PIT application.
     */
    public PitServer(int port) {
        this(port, new SocketProvider());
    }

    /**
     * Waits for the connection to be established with the PIT application.
     */
    public void listen() {
        socket = socketProvider.listen(port);
    }

    /**
     * <p>Sends given request to the PIT application in order
     * to start the analysis.</p>
     *  
     * <p>{@link #listen()} must have been called before.</p>
     * 
     * @param request
     *          The parameters to be used by the PIT analysis.
     */
    public void sendRequest(PitRequest request) {
        // FIXME Handle the case where listen() has not been called
        //       Making listen() return a Connection object would do the trick
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

    /**
     * <p>Receives the results of the PIT analysis.</p>
     * 
     * <p>This method blocks until results are made available by
     * the running PIT application.</p>
     * 
     * @return the results of PIT analysis
     */
    public PitResults receiveResults() {
        return socket.read();
    }
}
