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
import org.pitest.pitclipse.pitrunner.PitOptions;
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
		givenTheOptions(OPTIONS);
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

	private void givenTheOptions(PitOptions options) {
		context.setOptions(options);
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
		when(socketProvider.createClientSocket(context.getPortNumber())).thenReturn(connectionSocket);
		client.connect();
	}

	private void whenTheClientReceivesOptions() {
		when(connectionSocket.read()).thenReturn(context.getOptions());
		PitClient client = context.getPitClient();
		context.setTransmittedOptions(client.readOptions());
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
		assertThat(context.getTransmittedOptions(), areEqualTo(context.getOptions()));
	}

	private void thenTheClientConnectsOnThePort() {
		verify(socketProvider).createClientSocket(context.getPortNumber());
	}

	private void thenTheSocketIsClosed() throws IOException {
		verify(connectionSocket).close();
	}

}