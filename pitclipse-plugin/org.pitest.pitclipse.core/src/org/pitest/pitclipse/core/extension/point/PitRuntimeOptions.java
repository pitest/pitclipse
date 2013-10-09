package org.pitest.pitclipse.core.extension.point;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.pitest.pitclipse.pitrunner.PitOptions;

import com.google.common.collect.ImmutableSet;

@Immutable
public class PitRuntimeOptions {

	private final int portNumber;
	private final PitOptions options;
	private final Set<String> projects;

	public PitRuntimeOptions(int portNumber, PitOptions options, Set<String> projects) {
		this.portNumber = portNumber;
		this.options = options;
		this.projects = ImmutableSet.copyOf(projects);
	}

	public int getPortNumber() {
		return portNumber;
	}

	public PitOptions getOptions() {
		return options;
	}

	public Set<String> getMutatedProjects() {
		return ImmutableSet.copyOf(projects);
	}
}
