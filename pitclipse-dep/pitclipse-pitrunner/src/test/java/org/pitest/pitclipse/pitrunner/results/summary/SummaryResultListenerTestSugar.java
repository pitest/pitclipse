package org.pitest.pitclipse.pitrunner.results.summary;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.pitrunner.results.MutationResultListenerLifecycle.using;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.ListenerContext;
import org.pitest.pitclipse.pitrunner.results.ListenerFactory;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList.Builder;

class SummaryResultListenerTestSugar {

	static class CoverageState {
		private final CoverageDatabase coverageDatabase;

		private CoverageState(CoverageDatabase coverageDatabase) {
			this.coverageDatabase = coverageDatabase;
		}

		public SetupState and(ClassMutationResults first, ClassMutationResults... others) {
			final Builder<ClassMutationResults> r = ImmutableList.builder();
			r.add(first);
			if (null != others)
				r.add(others);
			return new SetupState(coverageDatabase, r.build());
		}

		public SetupState andNoMutations() {
			return new SetupState(coverageDatabase, ImmutableList.<ClassMutationResults> of());
		}

		static CoverageState given(CoverageDatabase coverageDatabase) {
			return new CoverageState(coverageDatabase);
		}
	}

	static class SetupState {

		private final ImmutableList<ClassMutationResults> results;
		private final CoverageDatabase coverageDatabase;

		public SetupState(CoverageDatabase coverageDatabase, ImmutableList<ClassMutationResults> results) {
			this.coverageDatabase = coverageDatabase;
			this.results = results;
		}

		public Verification whenPitIsExecuted() {
			Optional<SummaryResult> result = using(SummaryListenerFactory.INSTANCE, coverageDatabase)
					.handleMutationResults(results);
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

		public SummaryResultWrapper withCoverageOf(String className, int lineCov, int mutationCov) {
			Coverage lineCoverage = Coverage.from(lineCov, 100);
			Coverage mutationCoverage = Coverage.from(mutationCov, 100);
			ClassSummary classSummary = ClassSummary.from(className, lineCoverage, mutationCoverage);
			return new SummaryResultWrapper(result.update(classSummary));
		}

		public SummaryResult getResult() {
			return result;
		}
	}
}

enum SummaryListenerFactory implements ListenerFactory<SummaryResult> {
	INSTANCE;

	@Override
	public MutationResultListener apply(ListenerContext<SummaryResult> context) {
		return new SummaryResultListener(context.dispatcher, context.coverageData);
	}
}