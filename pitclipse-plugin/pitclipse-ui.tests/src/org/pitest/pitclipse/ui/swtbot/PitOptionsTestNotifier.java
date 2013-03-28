package org.pitest.pitclipse.ui.swtbot;

import org.pitest.pitclipse.core.extension.point.PitRuntimeOptions;
import org.pitest.pitclipse.core.extension.point.ResultNotifier;

public class PitOptionsTestNotifier implements
		ResultNotifier<PitRuntimeOptions> {

	public void handleResults(PitRuntimeOptions runtimeOptions) {
		PitOptionsNotifier.INSTANCE.setOptions(runtimeOptions.getOptions());
	}

}
