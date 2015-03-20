package org.pitest.pitclipse.pitrunner.results;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class MutationResultListenerLifecycle<T, D extends Dispatcher<? super T>, F extends ListenerFactory<T, D>> {

	private final F factory;

	public static <T, D extends Dispatcher<? super T>, F extends ListenerFactory<T, D>> MutationResultListenerLifecycle<T, D, F> using(
			F factory) {
		return new MutationResultListenerLifecycle<T, D, F>(factory);
	}

	public Optional<T> handleMutationResults(ImmutableList<ClassMutationResults> results) {
		RecordingDispatcher<T> recordingDispatcher = new RecordingDispatcher<T>();
		D dispatcher = (D) recordingDispatcher;
		MutationResultListener listener = factory.apply(dispatcher);
		listener.runStart();
		for (ClassMutationResults r : results)
			listener.handleMutationResult(r);
		listener.runEnd();
		return recordingDispatcher.getResult();
	}

	private MutationResultListenerLifecycle(F factory) {
		this.factory = factory;
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