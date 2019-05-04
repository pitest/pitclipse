package org.pitest.pitclipse.runner.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.runner.PitOptions;
import org.pitest.pitclipse.runner.PitRequest;
import org.pitest.pitclipse.runner.PitResults;
import org.pitest.pitclipse.runner.server.PitServer;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PitCommunicatorTest {

    private static final PitOptions OPTIONS = PitOptions.builder()
            .withSourceDirectory(new File(System.getProperty("java.io.tmpdir"))).withClassUnderTest("Test Class")
            .build();

    protected static final PitRequest REQUEST = PitRequest.builder().withPitOptions(OPTIONS).build();

    private static final PitResults RESULTS = null;

    @Mock
    private PitServer server;

    @Mock
    private PitResultHandler handler;

    @Test
    public void runCommunicator() throws IOException {
        whenPitCommunicatorIsRun();
        thenTheServerIsCalled();
        thenTheResultsAreHandled();
    }

    @Test(expected = RuntimeException.class)
    public void clientIsClosedOnException() throws IOException {
        try {
            whenPitCommunicatorGetsAnError();
        } finally {
            thenTheServerIsCalled();
            thenResultsAreNotHandled();
        }
    }

    private void whenPitCommunicatorIsRun() {
        when(server.receiveResults()).thenReturn(RESULTS);
        PitCommunicator communicator = new PitCommunicator(server, REQUEST, handler);
        communicator.run();
    }

    private void whenPitCommunicatorGetsAnError() {
        when(server.receiveResults()).thenThrow(new RuntimeException("Boom"));
        PitCommunicator communicator = new PitCommunicator(server, REQUEST, handler);
        communicator.run();
    }

    private void thenTheServerIsCalled() throws IOException {
        verify(server).listen();
        verify(server).sendRequest(REQUEST);
        verify(server).receiveResults();
        verify(server).close();
        verifyNoMoreInteractions(server);
    }

    private void thenTheResultsAreHandled() {
        verify(handler).handle(RESULTS);
        verifyNoMoreInteractions(handler);
    }

    private void thenResultsAreNotHandled() {
        verifyZeroInteractions(handler);
    }

}
