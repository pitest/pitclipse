package org.pitest.pitclipse.runner.results.mutations;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

import java.util.Properties;

public class MutationsResultListenerFactory implements MutationResultListenerFactory {

    @Override
    public String description() {
        return "Pitclipse mutation result plugin";
    }

    @Override
    public MutationResultListener getListener(Properties properties, ListenerArguments listenerArguments) {
        return new PitclipseMutationsResultListener(RecordingMutationsDispatcher.INSTANCE);
    }

    @Override
    public String name() {
        return "PITCLIPSE_MUTATIONS";
    }

}