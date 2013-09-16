package org.pitest.pitclipse.pitrunner.server;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
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
		whenTheServerSendsOptions();
		thenTheOptionsAreReceived();
		givenTheResults(RESULTS);
		whenTheServerReceivesResults();
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
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		when(connectionSocket.getOutputStream()).thenReturn(byteOutputStream);
		context.setOutputStream(byteOutputStream);
		server.listen();
	}

	private void whenTheServerSendsOptions() throws IOException,
			ClassNotFoundException {

		PitServer server = context.getPitServer();
		server.sendOptions(context.getOptions());

		ByteArrayInputStream inputStream = new ByteArrayInputStream(context
				.getOutputStream().toByteArray());
		PitOptions sentOptions = (PitOptions) new ObjectInputStream(inputStream)
				.readObject();
		context.setTransmittedOptions(sentOptions);
	}

	private void whenTheServerReceivesResults() throws Exception {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(byteOutputStream).writeObject(context
				.getResults());
		ByteArrayInputStream byteIntputStream = new ByteArrayInputStream(
				byteOutputStream.toByteArray());
		when(connectionSocket.getInputStream()).thenReturn(byteIntputStream);
		PitResults results = context.getPitServer().receiveResults();
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
