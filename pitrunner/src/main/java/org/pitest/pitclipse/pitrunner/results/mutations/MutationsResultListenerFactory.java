package org.pitest.pitclipse.pitrunner.results.mutations;

import java.util.Properties;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;

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
