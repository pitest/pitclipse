package org.pitest.pitclipse.pitrunner.results.mutations;

import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.reloc.guava.base.Optional;

public enum RecordingMutationsDispatcher implements MutationsDispatcher {
	INSTANCE;

	private volatile Optional<Mutations> dispatchedMutations = Optional.absent();

	@Override
	public void dispatch(Mutations result) {
		dispatchedMutations = Optional.of(result);
	}

	public Mutations getDispatchedMutations() {
		return dispatchedMutations.or(new Mutations());
	}
}
