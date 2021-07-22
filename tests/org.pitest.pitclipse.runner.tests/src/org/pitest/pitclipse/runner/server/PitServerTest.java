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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pitest.pitclipse.runner.AbstractPitRunnerTest;
import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.PitRunnerTestContext;
import org.pitest.pitclipse.runner.io.ObjectStreamSocket;
import org.pitest.pitclipse.runner.io.SocketProvider;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PitServerTest extends AbstractPitRunnerTest {

    @Mock
    private SocketProvider socketProvider;

    @Mock
    private ObjectStreamSocket objectSocket;

    private PitRunnerTestContext context;

    @Before
    public void setup() {
        context = new PitRunnerTestContext();
    }

    @Test
    public void serverStartsListener() {
        givenThePortNumber(PORT);
        whenThePitServerIsStarted();
        thenTheServerListensOnThePort();
    }

    @Test
    public void serverSendsOptions() {
        givenThePortNumber(PORT);
        whenThePitServerIsStarted();
        thenTheServerListensOnThePort();
        givenTheRequest(REQUEST);
        whenTheServerSendsOptions();
        thenTheOptionsAreSent();
        givenTheResults(RESULTS);
        whenTheServerReceivesResults();
        thenTheResultsAreSent();
    }

    @Test
    public void serverStopClosesSocket() throws IOException {
        givenThePortNumber(PORT);
        whenThePitServerIsStarted();
        thenTheServerListensOnThePort();
        whenTheServerIsStopped();
        thenTheUnderlyingConnectionIsClosed();
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

    private void whenThePitServerIsStarted() {
        PitServer server = new PitServer(context.getPortNumber(), socketProvider);
        context.setPitServer(server);
        when(socketProvider.listen(context.getPortNumber())).thenReturn(objectSocket);
        server.listen();
    }

    private void whenTheServerSendsOptions() {
        PitServer server = context.getPitServer();
        server.sendRequest(context.getRequest());
    }

    private void whenTheServerReceivesResults() {
        when(objectSocket.read()).thenReturn(context.getResults());
        PitResults results = context.getPitServer().receiveResults();
        context.setTransmittedResults(results);
    }

    private void whenTheServerIsStopped() throws IOException {
        PitServer server = context.getPitServer();
        server.close();
    }

    private void thenTheResultsAreSent() {
        assertThat(context.getTransmittedResults(), areEqualTo(RESULTS));
    }

    private void thenTheServerListensOnThePort() {
        verify(socketProvider).listen(context.getPortNumber());
    }

    private void thenTheOptionsAreSent() {
        verify(objectSocket).write(context.getRequest());
    }

    private void thenTheUnderlyingConnectionIsClosed() throws IOException {
        verify(objectSocket).close();
    }
}
