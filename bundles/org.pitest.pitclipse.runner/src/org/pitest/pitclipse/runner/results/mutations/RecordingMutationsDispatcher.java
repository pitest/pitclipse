package org.pitest.pitclipse.runner.results.mutations;

import com.google.common.base.Optional;

import org.pitest.pitclipse.runner.results.Mutations;

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
