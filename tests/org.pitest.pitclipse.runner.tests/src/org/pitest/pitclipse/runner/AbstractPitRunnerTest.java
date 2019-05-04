package org.pitest.pitclipse.runner;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.pitest.pitclipse.runner.server.PitServerTest;

import java.io.File;
import java.util.List;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

public abstract class AbstractPitRunnerTest {

    protected static final int PORT = 12345;

    protected static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    protected static final List<String> CLASS_PATH = ImmutableList.of(PitServerTest.class.getCanonicalName());

    protected static final PitResults RESULTS = PitResults.builder().withHtmlResults(TMP_DIR).build();

    protected static final PitOptions OPTIONS = PitOptions.builder().withSourceDirectory(TMP_DIR)
            .withClassUnderTest(PitServerTest.class.getCanonicalName()).withClassesToMutate(CLASS_PATH).build();

    private static final List<String> PROJECTS = ImmutableList.of("Project X", "Project Y");

    protected static final PitRequest REQUEST = PitRequest.builder().withPitOptions(OPTIONS).withProjects(PROJECTS)
            .build();

    protected Matcher<PitRequest> areEqualTo(final PitRequest results) {
        return new TypeSafeMatcher<PitRequest>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is equal to").appendValue(reflectionToString(results));
            }

            @Override
            protected boolean matchesSafely(PitRequest item) {
                return reflectionEquals(results, item);
            }
        };
    }

    protected Matcher<PitResults> areEqualTo(final PitResults expectedResult) {
        return new TypeSafeMatcher<PitResults>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is equal to").appendValue(reflectionToString(expectedResult));
            }

            @Override
            protected boolean matchesSafely(PitResults actualResult) {
                return new EqualsBuilder().append(expectedResult.getHtmlResultFile(), actualResult.getHtmlResultFile())
                        .isEquals();
            }
        };
    }

}
