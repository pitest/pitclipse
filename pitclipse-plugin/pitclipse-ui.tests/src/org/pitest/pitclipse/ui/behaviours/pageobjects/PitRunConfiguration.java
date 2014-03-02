package org.pitest.pitclipse.ui.behaviours.pageobjects;

import static org.pitest.pitclipse.pitrunner.config.PitConfiguration.DEFAULT_AVOID_CALLS_TO_LIST;
import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;

import java.util.List;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class PitRunConfiguration {

	private final String name;
	private final List<String> projects;
	private final boolean runInParallel;
	private final boolean incrementalAnalysis;
	private final String excludedClasses;
	private final String excludedMethods;
	private final String avoidCallsTo;

	private PitRunConfiguration(String name, List<String> projects, boolean runInParallel, boolean incrementalAnalysis,
			String excludedClasses, String excludedMethods, String avoidCallsTo) {
		this.name = name;
		this.projects = projects;
		this.runInParallel = runInParallel;
		this.incrementalAnalysis = incrementalAnalysis;
		this.excludedClasses = excludedClasses;
		this.excludedMethods = excludedMethods;
		this.avoidCallsTo = avoidCallsTo;
	}

	public String getName() {
		return name;
	}

	public List<String> getProjects() {
		return projects;
	}

	public static class Builder {
		private String name;
		private List<String> projects = ImmutableList.of();
		private boolean runInParallel = false;
		private boolean incrementalAnalysis = false;
		private String excludedClasses = "";
		private String excludedMethods = "";
		private String avoidCallsTo = DEFAULT_AVOID_CALLS_TO_LIST;

		public PitRunConfiguration build() {
			return new PitRunConfiguration(name, projects, runInParallel, incrementalAnalysis, excludedClasses,
					excludedMethods, avoidCallsTo);
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

		public Builder withExcludedClasses(String excludedClasses) {
			this.excludedClasses = excludedClasses;
			return this;
		}

		public Builder withExcludedMethods(String excludedMethods) {
			this.excludedMethods = excludedMethods;
			return this;
		}

		public Builder withAvoidCallsTo(String avoidCallsTo) {
			this.avoidCallsTo = avoidCallsTo;
			return this;
		}
	}

	public boolean isRunInParallel() {
		return runInParallel;
	}

	public boolean isIncrementalAnalysis() {
		return incrementalAnalysis;
	}

	public String getExcludedClasses() {
		return excludedClasses;
	}

	public String getExcludedMethods() {
		return excludedMethods;
	}

	public String getAvoidCallsTo() {
		return avoidCallsTo;
	}
}
