package org.pitest.pitclipse.pitrunner.client;

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
	public void clientConnectsToServer() throws IOException {
		givenThePortNumber(PORT);
		whenThePitClientIsStarted();
		thenTheClientConnectsOnThePort();
	}

	@Test
	public void clientSendsOptionsAndReceivesResults() throws IOException, ClassNotFoundException {
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
		when(socketProvider.connectTo(context.getPortNumber())).thenReturn(connectionSocket);
		client.connect();
	}

	private void whenTheClientReceivesOptions() {
		when(connectionSocket.read()).thenReturn(context.getRequest());
		PitClient client = context.getPitClient();
		context.setTransmittedRequest(client.readRequest());
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
