package org.pitest.pitclipse.pitrunner;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PitServerTest extends AbstractPitRunnerTest {

	@Mock
	private SocketProvider socketProvider;

	@Mock
	private ServerSocket serverSocket;

	@Mock
	private Socket connectionSocket;

	private PitRunnerTestContext context;

	@Before
	public void setup() {
		context = new PitRunnerTestContext();
	}

	@Test
	public void serverStartsListener() throws IOException {
		givenThePortNumber(PORT);
		whenThePitServerIsStarted();
		thenTheServerListensOnThePort();
	}

	@Test
	public void serverReceivesOptions() throws Exception {
		givenThePortNumber(PORT);
		whenThePitServerIsStarted();
		thenTheServerListensOnThePort();
		givenTheOptions(OPTIONS);
		whenTheServerGetsOptions();
		thenTheOptionsAreReceived();
		givenTheResults(RESULTS);
		whenTheServerSendsResults();
		thenTheResultsAreSent();
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

	private void whenThePitServerIsStarted() throws IOException {
		PitServer server = new PitServer(context.getPortNumber(),
				socketProvider);
		context.setPitServer(server);
		when(socketProvider.createServerSocket(context.getPortNumber()))
				.thenReturn(serverSocket);
		when(serverSocket.accept()).thenReturn(connectionSocket);
		server.listen();
	}

	private void whenTheServerGetsOptions() throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(byteOutputStream).writeObject(context
				.getOptions());
		InputStream inputStream = new ByteArrayInputStream(
				byteOutputStream.toByteArray());
		when(connectionSocket.getInputStream()).thenReturn(inputStream);
		PitOptions results = context.getPitServer().readOptions();
		context.setTransmittedOptions(results);
	}

	private void whenTheServerSendsResults() throws Exception {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		when(connectionSocket.getOutputStream()).thenReturn(byteOutputStream);
		context.getPitServer().sendResults(context.getResults());
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				byteOutputStream.toByteArray());
		PitResults results = (PitResults) new ObjectInputStream(inputStream)
				.readObject();
		context.setTransmittedResults(results);
	}

	private void thenTheResultsAreSent() {
		assertThat(context.getTransmittedResults(), areEqualTo(RESULTS));
	}

	private void thenTheServerListensOnThePort() throws IOException {
		verify(socketProvider).createServerSocket(context.getPortNumber());
		verify(serverSocket).accept();
	}

	private void thenTheOptionsAreReceived() throws IOException {
		assertThat(context.getTransmittedOptions(), areEqualTo(OPTIONS));
	}
}
