package org.pitest.pitclipse.pitrunner.client;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitResults;

@RunWith(MockitoJUnitRunner.class)
public class PitCommunicatorTest {

	private static final PitOptions OPTIONS = PitOptions
			.builder()
			.withSourceDirectory(new File(System.getProperty("java.io.tmpdir")))
			.withClassUnderTest("Test Class").build();

	private static final PitResults RESULTS = null;

	@Mock
	private PitClient client;

	@Mock
	private PitResultHandler handler;

	@Test
	public void runCommunicator() {
		whenPitCommunicatorIsRun();
		thenTheClientIsCalled();
		thenTheResultsAreHandled();
	}

	@Test(expected = RuntimeException.class)
	public void clientIsClosedOnException() {
		try {
			whenPitCommunicatorGetsAnError();
		} finally {
			thenTheClientIsCalled();
			thenResultsAreNotHandled();
		}
	}

	private void whenPitCommunicatorIsRun() {
		when(client.receiveResults()).thenReturn(RESULTS);
		PitCommunicator communicator = new PitCommunicator(client, OPTIONS,
				handler);
		communicator.run();
	}

	private void whenPitCommunicatorGetsAnError() {
		when(client.receiveResults()).thenThrow(new RuntimeException("Boom"));
		PitCommunicator communicator = new PitCommunicator(client, OPTIONS,
				handler);
		communicator.run();
	}

	private void thenTheClientIsCalled() {
		verify(client).connect();
		verify(client).sendOptions(OPTIONS);
		verify(client).receiveResults();
		verify(client).close();
		verifyNoMoreInteractions(client);
	}

	private void thenTheResultsAreHandled() {
		verify(handler).handle(RESULTS);
		verifyNoMoreInteractions(handler);
	}

	private void thenResultsAreNotHandled() {
		verifyZeroInteractions(handler);
	}

}
