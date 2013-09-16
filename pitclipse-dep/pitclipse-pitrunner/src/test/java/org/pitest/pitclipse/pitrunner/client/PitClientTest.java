package org.pitest.pitclipse.pitrunner.client;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.pitrunner.AbstractPitRunnerTest;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.PitRunnerTestContext;
import org.pitest.pitclipse.pitrunner.io.SocketProvider;

@RunWith(MockitoJUnitRunner.class)
public class PitClientTest extends AbstractPitRunnerTest {

	@Mock
	private SocketProvider socketProvider;

	@Mock
	private Socket connectionSocket;

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
	public void clientSendsOptionsAndReceivesResults() throws IOException,
			ClassNotFoundException {
		givenThePortNumber(PORT);
		whenThePitClientIsStarted();
		thenTheClientConnectsOnThePort();
		givenTheOptions(OPTIONS);
		whenTheClientReceivesOptions();
		thenTheOptionsAreSent();
		givenTheResults(RESULTS);
		whenTheClientSendsResults();
		thenTheResultsAreReceived();
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

	private void whenThePitClientIsStarted() throws IOException {
		PitClient client = new PitClient(context.getPortNumber(),
				socketProvider);
		context.setPitClient(client);
		when(socketProvider.createClientSocket(context.getPortNumber()))
				.thenReturn(connectionSocket);
		client.connect();
	}

	private void whenTheClientReceivesOptions() throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(byteOutputStream).writeObject(context
				.getOptions());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				byteOutputStream.toByteArray());
		when(connectionSocket.getInputStream()).thenReturn(inputStream);
		PitClient client = context.getPitClient();
		context.setTransmittedOptions(client.readOptions());
	}

	private void whenTheClientSendsResults() throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		when(connectionSocket.getOutputStream()).thenReturn(byteOutputStream);
		context.getPitClient().sendResults(context.getResults());
		PitResults transmittedResults = (PitResults) new ObjectInputStream(
				new ByteArrayInputStream(byteOutputStream.toByteArray()))
				.readObject();
		context.setTransmittedResults(transmittedResults);
	}

	private void thenTheResultsAreReceived() {
		assertThat(context.getTransmittedResults(), areEqualTo(RESULTS));
	}

	private void thenTheOptionsAreSent() throws IOException {
		assertThat(context.getTransmittedOptions(), areEqualTo(OPTIONS));
	}

	private void thenTheClientConnectsOnThePort() {
		verify(socketProvider).createClientSocket(context.getPortNumber());
	}

}
