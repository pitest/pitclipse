package org.pitest.pitclipse.pitrunner.results.summary;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.pitrunner.results.MutationResultListenerLifecycle.using;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.Dispatcher;
import org.pitest.pitclipse.pitrunner.results.ListenerFactory;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

class SummaryResultListenerTestSugar {
	static SetupState given(ClassMutationResults first, ClassMutationResults... others) {
		final Builder<ClassMutationResults> r = ImmutableList.builder();
		r.add(first);
		if (null != others)
			r.add(others);
		return new SetupState(r.build());
	}

	static SetupState givenNoMutations() {
		return new SetupState(ImmutableList.<ClassMutationResults> of());
	}

	static class SetupState {

		private final ImmutableList<ClassMutationResults> results;

		public SetupState(ImmutableList<ClassMutationResults> results) {
			this.results = results;
		}

		public Verification whenPitIsExecuted() {
			Optional<SummaryResult> result = using(SummaryListenerFactory.INSTANCE).handleMutationResults(results);
			return new Verification(result);
		}
	}

	static class Verification {
		private final Optional<SummaryResult> actualResult;

		public Verification(Optional<SummaryResult> actualResult) {
			this.actualResult = actualResult;
		}

		public void thenTheResultsAre(SummaryResult expectedResult) {
			assertThat(actualResult.isPresent(), is(equalTo(true)));
			assertThat(actualResult.get(), is(sameAs(expectedResult)));
		}

		private Matcher<SummaryResult> sameAs(final SummaryResult expected) {
			return new TypeSafeMatcher<SummaryResult>() {
				@Override
				public void describeTo(Description description) {
					description.appendText("summaryResult").appendValue(reflectionToString(expected));
				}

				@Override
				protected boolean matchesSafely(SummaryResult actual) {
					return reflectionEquals(expected, actual);
				}
			};
		}

		public void thenTheResultsAre(SummaryResultWrapper wrapper) {
			thenTheResultsAre(wrapper.getResult());
		}
	}

	static SummaryResult empty() {
		return SummaryResult.EMPTY;
	}

	static class SummaryResultWrapper {

		private final SummaryResult result;

		public SummaryResultWrapper(SummaryResult result) {
			this.result = result;
		}

		public SummaryResultWrapper withCoverageOf(int coverage) {
			return new SummaryResultWrapper(new SummaryResult(coverage));
		}

		public SummaryResult getResult() {
			return result;
		}
	}
}

enum SummaryListenerFactory implements ListenerFactory<SummaryResult, Dispatcher<SummaryResult>> {
	INSTANCE;

	@Override
	public MutationResultListener apply(Dispatcher<SummaryResult> dispatcher) {
		return new SummaryResultListener(dispatcher);
	}

}