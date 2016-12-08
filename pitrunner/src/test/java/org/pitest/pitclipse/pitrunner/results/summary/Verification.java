package org.pitest.pitclipse.pitrunner.results.summary;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.pitest.pitclipse.reloc.guava.base.Optional;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class Verification {
    private final Optional<SummaryResult> actualResult;

    public Verification(Optional<SummaryResult> actualResult) {
        this.actualResult = actualResult;
    }

    public void thenTheResultsAre(SummaryResult expectedResult) {
        assertThat(actualResult.isPresent(), is(equalTo(true)));
        assertThat(actualResult.get(), is(sameAs(expectedResult)));
    }

    private Matcher<SummaryResult> sameAs(final SummaryResult expected) {
        return new TypeSafeDiagnosingMatcher<SummaryResult>() {

            @Override
            public void describeTo(Description description) {
                description.appendValue(reflectionToString(expected));
            }

            @Override
            protected boolean matchesSafely(SummaryResult actual, Description mismatchDescription) {
                mismatchDescription.appendValue(reflectionToString(actual));
                return reflectionEquals(expected, actual);
            }
        };
    }

    public void thenTheResultsAre(SummaryResultListenerTestSugar.SummaryResultWrapper wrapper) {
        thenTheResultsAre(wrapper.getResult());
    }
}