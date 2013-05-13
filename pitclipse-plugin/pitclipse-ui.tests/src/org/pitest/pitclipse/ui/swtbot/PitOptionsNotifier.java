package org.pitest.pitclipse.ui.swtbot;

import java.util.concurrent.atomic.AtomicReference;

import org.pitest.pitclipse.pitrunner.PitOptions;

public enum PitOptionsNotifier {

	INSTANCE;

	private final AtomicReference<PitOptions> options = new AtomicReference<PitOptions>();

	public void setOptions(PitOptions options) {
		this.options.set(options);
	}

	public PitOptions getLastUsedOptions() {
		return options.get();
	}

}
