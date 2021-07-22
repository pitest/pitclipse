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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObjectStreamSocketTest {

    @Mock
    private Socket underlyingSocket;

    private ObjectStreamSocket objectSocket;
    private TestSerializableObject expectedObject;
    private TestSerializableObject actualObject;

    @Mock
    private ObjectOutputStream outputStream;

    @Mock
    private ObjectInputStream inputStream;

    @Before
    public void setup() throws IOException {
        expectedObject = null;
        actualObject = null;
    }

    @Test
    public void readingFromASocketReturnsAnObject() throws IOException {
        givenSomeTestObject();
        whenWeReadFromTheSocket();
        thenTheObjectWasReturned();
    }

    @Test
    public void writingToASocketSendsAnObject() throws IOException, ClassNotFoundException {
        givenSomeTestObject();
        whenWeWriteToTheSocket();
        thenTheObjectWasWritten();
    }

    @Test
    public void closingSocketClosesStreams() throws IOException {
        givenSomeTestObject();
        whenWeCloseTheSocket();
        thenTheStreamsWereClosed();
        thenTheSocketWasClosed();
    }

    @Test(expected = IOException.class)
    public void inputStreamAndSocketAreClosedIfAnErrorOccursClosingOutputStream() throws IOException {
        givenSomeTestObject();
        try {
            whenTheOutputStreamThrowsAnExceptionWhilstClosing();
        } finally {
            thenTheStreamsWereClosed();
            thenTheSocketWasClosed();
        }
    }

    @Test(expected = IOException.class)
    public void outputStreamAndSocketAreClosedIfAnErrorOccursClosingInputStream() throws IOException {
        givenSomeTestObject();
        try {
            whenTheInputStreamThrowsAnExceptionWhilstClosing();
        } finally {
            thenTheStreamsWereClosed();
            thenTheSocketWasClosed();
        }
    }

    private void givenSomeTestObject() {
        expectedObject = new TestSerializableObject("Some state");
    }

    private byte[] asBytes(TestSerializableObject someObject) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteStream).writeObject(someObject);
        return byteStream.toByteArray();
    }

    private void whenWeReadFromTheSocket() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(asBytes(expectedObject));
        OutputStream outputStream = new ByteArrayOutputStream();
        when(underlyingSocket.getInputStream()).thenReturn(inputStream);
        when(underlyingSocket.getOutputStream()).thenReturn(outputStream);
        objectSocket = ObjectStreamSocket.make(underlyingSocket);
        actualObject = objectSocket.read();
    }

    private void whenWeWriteToTheSocket() throws IOException, ClassNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(asBytes(expectedObject));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(underlyingSocket.getInputStream()).thenReturn(inputStream);
        when(underlyingSocket.getOutputStream()).thenReturn(outputStream);
        objectSocket = ObjectStreamSocket.make(underlyingSocket);
        objectSocket.write(expectedObject);
        actualObject = (TestSerializableObject) new ObjectInputStream(new ByteArrayInputStream(
                outputStream.toByteArray())).readObject();
    }

    private void whenWeCloseTheSocket() throws IOException {
        objectSocket = ObjectStreamSocket.make(underlyingSocket, inputStream, outputStream);
        objectSocket.close();
    }

    private void whenTheOutputStreamThrowsAnExceptionWhilstClosing() throws IOException {
        doThrow(new IOException()).when(outputStream).close();
        objectSocket = ObjectStreamSocket.make(underlyingSocket, inputStream, outputStream);
        objectSocket.close();
    }

    private void whenTheInputStreamThrowsAnExceptionWhilstClosing() throws IOException {
        doThrow(new IOException()).when(inputStream).close();
        objectSocket = ObjectStreamSocket.make(underlyingSocket, inputStream, outputStream);
        objectSocket.close();
    }

    private void thenTheObjectWasWritten() {
        verifyExpectations();
    }

    private void thenTheObjectWasReturned() {
        verifyExpectations();
    }

    private void thenTheStreamsWereClosed() throws IOException {
        verify(inputStream).close();
        verify(outputStream).close();
    }

    private void thenTheSocketWasClosed() throws IOException {
        verify(underlyingSocket).close();
    }

    private void verifyExpectations() {
        assertThat(actualObject, is(notNullValue()));
        assertThat(actualObject, is(equalTo(expectedObject)));
        assertThat(actualObject, is(not(sameInstance(expectedObject))));
    }

    private static class TestSerializableObject implements Serializable {
        private static final long serialVersionUID = 597388911067180305L;
        private final String state;

        public TestSerializableObject(String state) {
            this.state = state;
        }

        @Override
        public int hashCode() {
            return reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object that) {
            return reflectionEquals(this, that);
        }

        @SuppressWarnings("unused")
        public String getState() {
            return state;
        }
    }
}
