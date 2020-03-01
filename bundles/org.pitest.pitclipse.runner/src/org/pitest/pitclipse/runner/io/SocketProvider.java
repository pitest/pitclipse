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

package org.pitest.pitclipse.runner.io;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Provides objects easing reading objects and writing objects to a given port. 
 */
public class SocketProvider {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int RETRY_COUNT = 100;

    /**
     * <p>Returns an object allowing to write objects to the given port.</p>
     * 
     * <p>This method blocks until a client accepts the connection.</p>
     * 
     * @param portNumber
     *          The number of port to listen for a client.
     *          
     * @return an object allowing to write and read objects from the given port
     */
    public ObjectStreamSocket listen(int portNumber) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            Socket connection = serverSocket.accept();
            return ObjectStreamSocket.make(connection);
        } catch (IOException e) {
            throw new SocketCreationException(e);
        } finally {
            ensureClosed(serverSocket);
        }
    }

    private long currentTime() {
        return  System.currentTimeMillis();
    }

    /**
     * <p>Returns an object allowing to write objects to the given port.</p>
     * 
     * <p>This method blocks until a server accepts the connection.</p>
     * 
     * @param portNumber
     *          The number of port to connect to a server.
     *          
     * @return an object allowing to write objects to the given port
     */
    public Optional<ObjectStreamSocket> connectTo(int portNumber) {
        long startInMillis = currentTime();
        Optional<ObjectStreamSocket> socket;
        do {
            socket = doConnect(portNumber);
            if (!socket.isPresent()) {
                try {
                    Thread.sleep(DEFAULT_TIMEOUT / RETRY_COUNT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        } 
        while (!socket.isPresent() && (currentTime() - startInMillis < DEFAULT_TIMEOUT));
        
        return socket;
    }

    private Optional<ObjectStreamSocket> doConnect(int portNumber) {
        try {
            InetAddress localhost = InetAddress.getByName(null);
            Socket socket = new Socket();
            SocketAddress endpoint = new InetSocketAddress(localhost, portNumber);
            socket.connect(endpoint, DEFAULT_TIMEOUT);
            return Optional.of(ObjectStreamSocket.make(socket));
        } catch (Exception e) {
            return Optional.absent();
        }
    }

    /**
     * Returns the number of a port that is not currently used
     * @return the number of a port that can be used
     */
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
                    System.out.println("Warning, did not close socket");
                    e.printStackTrace();
                }
            }
        }
    }

    private void ensureClosed(ServerSocket serverSocket) {
        try {
            if (null != serverSocket) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new SocketCreationException(e);
        }
    }

}
