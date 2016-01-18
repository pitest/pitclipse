package org.pitest.pitclipse.pitrunner.results;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class MutationResultListenerLifecycle<T, F extends ListenerFactory<T>> {

	private final F factory;
	private final CoverageDatabase coverageData;

	public static <T, F extends ListenerFactory<T>> MutationResultListenerLifecycle<T, F> using(F factory,
			CoverageDatabase coverageData) {
		return new MutationResultListenerLifecycle<T, F>(factory, coverageData);
	}

	public Optional<T> handleMutationResults(ImmutableList<ClassMutationResults> results) {
		RecordingDispatcher<T> recordingDispatcher = new RecordingDispatcher<T>();
		ListenerContext<T> context = new ListenerContext<T>(recordingDispatcher, coverageData);
		MutationResultListener listener = factory.apply(context);
		listener.runStart();
		for (ClassMutationResults r : results)
			listener.handleMutationResult(r);
		listener.runEnd();
		return recordingDispatcher.getResult();
	}

	private MutationResultListenerLifecycle(F factory, CoverageDatabase coverageData) {
		this.factory = factory;
		this.coverageData = coverageData;
	}

	private static class RecordingDispatcher<T> implements Dispatcher<T> {

		private Optional<T> result = Optional.absent();

		@Override
		public void dispatch(T result) {
			this.result = Optional.of(result);
		}

		public Optional<T> getResult() {
			return result;
		}
	}
}