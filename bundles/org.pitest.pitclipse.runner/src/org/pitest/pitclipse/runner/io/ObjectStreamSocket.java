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

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Wraps a {@link Socket} in order to ease read an write operations involving objects.
 */
public class ObjectStreamSocket implements Closeable {

    private final Socket underlyingSocket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    private ObjectStreamSocket(Socket underlyingSocket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.underlyingSocket = underlyingSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Creates a new instance to ease read and write operations involving objects on the given socket.
     * 
     * @param underlyingSocket
     *          The socket on which objects must be read or written.
     *          
     * @return a new {@link ObjectStreamSocket}.
     */
    public static ObjectStreamSocket make(Socket underlyingSocket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(underlyingSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(underlyingSocket.getInputStream());
            return make(underlyingSocket, inputStream, outputStream);
        } catch (IOException e) {
            throw new StreamInitialisationException(e);
        }
    }

    /**
     * Only used by tests
     * 
     * @param underlyingSocket
     * @param inputStream
     * @param outputStream
     * @return
     */
    static ObjectStreamSocket make(Socket underlyingSocket, ObjectInputStream inputStream,
            ObjectOutputStream outputStream) {
        return new ObjectStreamSocket(underlyingSocket, inputStream, outputStream);
    }

    /**
     * Reads an object from the socket.
     * 
     * @param <T>
     *          The type of the object to read.
     * 
     * @return the object read
     * 
     * @throws ClassCastException if the object cannot be casted to &lt;T&gt;
     * @throws ReadException if an error occurs while reading the socket
     */
    @SuppressWarnings("unchecked")
    public <T> T read() {
        try {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            throw new ReadException(e);
        }
    }

    /**
     * Writes an object to the socket.
     * 
     * @param someObject
     *          The object to write.
     */
    public void write(Object someObject) {
        try {
            outputStream.writeObject(someObject);
            outputStream.flush();
        } catch (Exception e) {
            throw new WriteException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            tryCloseStreams();
        } finally {
            closeSocket();
        }
    }

    private void tryCloseStreams() throws IOException {
        try {
            tryCloseOutputStream();
        } finally {
            tryCloseInputStream();
        }
    }

    private void tryCloseInputStream() throws IOException {
        inputStream.close();
    }

    private void tryCloseOutputStream() throws IOException {
        outputStream.close();
    }

    private void closeSocket() throws IOException {
        underlyingSocket.close();
    }

    private static final class StreamInitialisationException extends RuntimeException {
        private static final long serialVersionUID = 489374857284580542L;

        public StreamInitialisationException(IOException e) {
            super(e);
        }

    }

    private static final class ReadException extends RuntimeException {
        private static final long serialVersionUID = -7217167622171380199L;

        public ReadException(Exception e) {
            super(e);
        }
    }

    private static final class WriteException extends RuntimeException {
        private static final long serialVersionUID = -7517131322531593708L;

        public WriteException(Exception e) {
            super(e);
        }
    }
}
