package org.pitest.pitclipse.pitrunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class PitRunnerTest {

	private static final String TEST_CLASS = PitOptionsTest.class.getCanonicalName();
	private static final List<String> CLASS_PATH = ImmutableList.of("org.pitest.pitclipse.pitrunner.*");
	private static final List<String> PROJECTS = ImmutableList.of("project1", "project2");
	private final PitRunner runner = new PitRunner();

	@Test
	public void runPIT() {
		PitRequest request = PitRequest.builder().withPitOptions(options()).withProjects(PROJECTS).build();
		PitResults results = runner.runPIT(request);
		assertThat(results, is(notNullValue()));
		assertThat(results.getHtmlResultFile(), is(aFileThatExists()));
		assertThat(results.getXmlResultFile(), is(aFileThatExists()));
		assertThat(results.getMutations(), is(notNullValue()));
	}

	private Matcher<File> aFileThatExists() {
		return new TypeSafeMatcher<File>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("file exists");
			}

			@Override
			protected boolean matchesSafely(File file) {
				return null != file && file.exists();
			}
		};
	}

	private PitOptions options() {
		File srcDir = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "java");

		PitOptions options = PitOptions.builder().withSourceDirectory(srcDir).withClassUnderTest(TEST_CLASS)
				.withClassesToMutate(CLASS_PATH).build();
		return options;
	}

}
