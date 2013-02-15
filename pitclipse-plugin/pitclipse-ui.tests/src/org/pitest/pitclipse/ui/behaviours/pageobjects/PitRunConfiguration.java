package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;

import java.util.List;

public class PitRunConfiguration {

	private final String name;
	private final List<String> projects;
	private final boolean runInParallel;
	private final boolean incrementalAnalysis;

	private PitRunConfiguration(String name, List<String> projects,
			boolean runInParallel, boolean incrementalAnalysis) {
		this.name = name;
		this.projects = projects;
		this.runInParallel = runInParallel;
		this.incrementalAnalysis = incrementalAnalysis;
	}

	public String getName() {
		return name;
	}

	public List<String> getProjects() {
		return projects;
	}

	public static class Builder {
		private String name;
		private List<String> projects = of();
		private boolean runInParallel = false;
		private boolean incrementalAnalysis = false;

		public PitRunConfiguration build() {
			return new PitRunConfiguration(name, projects, runInParallel,
					incrementalAnalysis);
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withProjects(String... projects) {
			this.projects = copyOf(projects);
			return this;
		}

		public Builder withRunInParallel(boolean runInParallel) {
			this.runInParallel = runInParallel;
			return this;
		}

		public Builder withIncrementalAnalysis(boolean incrementalAnalysis) {
			this.incrementalAnalysis = incrementalAnalysis;
			return this;
		}
	}

	public boolean isRunInParallel() {
		return runInParallel;
	}

	public boolean isIncrementalAnalysis() {
		return incrementalAnalysis;
	}
}
