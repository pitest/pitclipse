package org.pitest.pitclipse.pitrunner;

import static com.google.common.collect.ImmutableList.of;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import java.io.File;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.pitest.pitclipse.pitrunner.PitResults.PitResultsBuilder;
import org.pitest.pitclipse.pitrunner.server.PitServerTest;

public abstract class AbstractPitRunnerTest {

	protected static final int PORT = 12345;

	protected static final File TMP_DIR = new File(
			System.getProperty("java.io.tmpdir"));

	protected static final List<String> CLASS_PATH = of(PitServerTest.class
			.getCanonicalName());

	protected static final PitResults RESULTS = new PitResultsBuilder()
			.withHtmlResults(TMP_DIR).withXmlResults(TMP_DIR).build();

	protected static final PitOptions OPTIONS = PitOptions.builder()
			.withSourceDirectory(TMP_DIR)
			.withClassUnderTest(PitServerTest.class.getCanonicalName())
			.withClassesToMutate(CLASS_PATH).build();

	protected Matcher<PitOptions> areEqualTo(final PitOptions results) {
		return new TypeSafeMatcher<PitOptions>() {

			public void describeTo(Description description) {
				description.appendText("is equal to").appendValue(
						reflectionToString(results));
			}

			@Override
			protected boolean matchesSafely(PitOptions item) {
				return reflectionEquals(results, item);
			}
		};
	}

	protected Matcher<PitResults> areEqualTo(final PitResults results) {
		return new TypeSafeMatcher<PitResults>() {
			public void describeTo(Description description) {
				description.appendText("is equal to").appendValue(
						reflectionToString(results));
			}

			@Override
			protected boolean matchesSafely(PitResults item) {
				return reflectionEquals(results, item);
			}
		};
	}

}
