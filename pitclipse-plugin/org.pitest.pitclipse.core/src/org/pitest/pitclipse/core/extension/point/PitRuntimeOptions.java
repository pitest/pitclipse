package org.pitest.pitclipse.core.extension.point;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions;

@Immutable
public class PitRuntimeOptions {

	private final int portNumber;
	private final PitOptions options;

	public PitRuntimeOptions(int portNumber, PitOptions options) {
		this.portNumber = portNumber;
		this.options = options;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public PitOptions getOptions() {
		return options;
	}
}
