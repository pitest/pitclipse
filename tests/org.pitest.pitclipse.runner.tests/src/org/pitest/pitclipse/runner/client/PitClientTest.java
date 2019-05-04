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

import com.google.common.base.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.runner.AbstractPitRunnerTest;
import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.PitRunnerTestContext;
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

import java.io.IOException;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PitClientTest extends AbstractPitRunnerTest {

    @Mock
    private SocketProvider socketProvider;

    @Mock
    private ObjectStreamSocket connectionSocket;

    private PitRunnerTestContext context;

    @Before
    public void setup() {
        context = new PitRunnerTestContext();
    }

    @Test
    public void clientConnectsToServer() {
        givenThePortNumber(PORT);
        whenThePitClientIsStarted();
        thenTheClientConnectsOnThePort();
    }

    @Test
    public void clientSendsOptionsAndReceivesResults() {
        givenThePortNumber(PORT);
        whenThePitClientIsStarted();
        thenTheClientConnectsOnThePort();
        givenTheRequest(REQUEST);
        whenTheClientReceivesOptions();
        thenTheOptionsAreReceived();
        givenTheResults(RESULTS);
        whenTheClientSendsResults();
        thenTheResultsAreSent();
    }

    @Test
    public void closingClientClosesTheSocket() throws IOException {
        givenThePortNumber(PORT);
        whenThePitClientIsStarted();
        whenTheClientIsClosed();
        thenTheSocketIsClosed();
    }

    private void givenTheRequest(PitRequest request) {
        context.setRequest(request);
    }

    private void givenThePortNumber(int port) {
        context.setPortNumber(port);
    }

    private void givenTheResults(PitResults results) {
        context.setResults(results);
    }

    private void whenThePitClientIsStarted() {
        PitClient client = new PitClient(context.getPortNumber(), socketProvider);
        context.setPitClient(client);
        when(socketProvider.connectTo(context.getPortNumber())).thenReturn(Optional.of(connectionSocket));
        client.connect();
    }

    private void whenTheClientReceivesOptions() {
        when(connectionSocket.read()).thenReturn(context.getRequest());
        PitClient client = context.getPitClient();
        context.setTransmittedRequest(client.readRequest().get());
    }

    private void whenTheClientSendsResults() {
        context.getPitClient().sendResults(context.getResults());
    }

    private void whenTheClientIsClosed() throws IOException {
        context.getPitClient().close();
    }

    private void thenTheResultsAreSent() {
        verify(connectionSocket).write(context.getResults());
    }

    private void thenTheOptionsAreReceived() {
        verify(connectionSocket).read();
        assertThat(context.getTransmittedRequest(), areEqualTo(context.getRequest()));
    }

    private void thenTheClientConnectsOnThePort() {
        verify(socketProvider).connectTo(context.getPortNumber());
    }

    private void thenTheSocketIsClosed() throws IOException {
        verify(connectionSocket).close();
    }

}
