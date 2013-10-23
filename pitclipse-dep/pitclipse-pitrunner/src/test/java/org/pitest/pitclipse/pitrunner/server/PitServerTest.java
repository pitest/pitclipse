package org.pitest.pitclipse.pitrunner.server;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.pitrunner.AbstractPitRunnerTest;
import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.PitRunnerTestContext;
import org.pitest.pitclipse.pitrunner.io.ObjectStreamSocket;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

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
