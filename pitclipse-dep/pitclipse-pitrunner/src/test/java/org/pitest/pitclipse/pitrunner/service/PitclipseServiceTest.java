package org.pitest.pitclipse.pitrunner.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.of;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.pitclipse.pitrunner.PitOptions;
import org.pitest.pitclipse.pitrunner.PitRequest;
import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.model.ModelBuilder;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;
import org.pitest.pitclipse.pitrunner.server.PitServer;
import org.pitest.pitclipse.pitrunner.server.PitServerProvider;
import org.pitest.pitclipse.pitrunner.server.PitServerTest;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class PitclipseServiceTest {
	private static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

	private static final List<String> CLASS_PATH = of(PitServerTest.class.getCanonicalName());

	private static final PitOptions OPTIONS = PitOptions.builder().withSourceDirectory(TMP_DIR)
			.withClassUnderTest(PitServerTest.class.getCanonicalName()).withClassesToMutate(CLASS_PATH).build();

	private static final List<String> PROJECTS = ImmutableList.of("Project X", "Project Y");

	private static final PitRequest REQUEST = PitRequest.builder().withPitOptions(OPTIONS).withProjects(PROJECTS)
			.build();

	private static final PitResults RESULTS = PitResults.builder().withHtmlResults(TMP_DIR).build();
	private static final MutationsModel MODEL = MutationsModel.EMPTY_MODEL;

	private static final int PORT = new Random().nextInt();

	@Mock
	private PitServerProvider serverProvider;
	@Mock
	private PitServer server;
	@Mock
	private ModelBuilder modelBuilder;

	private PitRequest request;

	private PitclipseService service;

	private MutationsModel model;

	private PitResults results;

	@Before
	public void setup() {
		request = null;
		service = new PitclipseService(serverProvider, modelBuilder);
	}

	@Test
	public void pitIsExecuted() {
		givenAPitRequest(REQUEST);
		andExpectedPitResult(RESULTS);
		andExpectedModel(MODEL);
		whenTheServiceIsCalled();
		thenPitIsCalled();
		andTheModelIsBuiltAndReturned();
	}

	private void givenAPitRequest(PitRequest request) {
		this.request = request;
	}

	private void andExpectedPitResult(PitResults results) {
		this.results = results;
		when(server.receiveResults()).thenReturn(results);
	}

	private void andExpectedModel(MutationsModel model) {
		when(modelBuilder.buildFrom(results)).thenReturn(model);
	}

	private void whenTheServiceIsCalled() {
		when(serverProvider.newServerFor(PORT)).thenReturn(server);
		model = service.analyse(PORT, request);
	}

	private void thenPitIsCalled() {
		verify(server).sendRequest(request);
	}

	private void andTheModelIsBuiltAndReturned() {
		verify(modelBuilder).buildFrom(results);
		assertThat(model, is(equalTo(MODEL)));
	}
}
