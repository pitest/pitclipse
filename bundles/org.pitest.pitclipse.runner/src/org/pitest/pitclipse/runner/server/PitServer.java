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
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

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
