package org.pitest.pitclipse.pitrunner.config;

import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;

import javax.annotation.concurrent.Immutable;

@Immutable
public class PitConfiguration {
	public static final String DEFAULT_AVOID_CALLS_TO_LIST = "java.util.logging, org.apache.log4j, org.slf4j, org.apache.commons.logging";
	public static final String DEFAULT_MUTATORS = "DEFAULTS";

	private final PitExecutionMode executionMode;
	private final boolean parallelExecution;
	private final boolean incrementalAnalysis;
	private final String excludedClasses;
	private final String excludedMethods;
	private final String avoidCallsTo;
	private final String mutators;

	private PitConfiguration(PitExecutionMode executionMode, boolean parallelExecution, boolean incrementalAnalysis,
			String excludedClasses, String excludedMethods, String avoidCallsTo, String mutators) {
		this.executionMode = executionMode;
		this.parallelExecution = parallelExecution;
		this.incrementalAnalysis = incrementalAnalysis;
		this.excludedClasses = excludedClasses;
		this.excludedMethods = excludedMethods;
		this.avoidCallsTo = avoidCallsTo;
		this.mutators = mutators;
	}

	public static Builder builder() {
		return new Builder();
	}

	public PitExecutionMode getExecutionMode() {
		return executionMode;
	}

	public boolean isParallelExecution() {
		return parallelExecution;
	}

	public boolean isIncrementalAnalysis() {
		return incrementalAnalysis;
	}

	public static final class Builder {
		private PitExecutionMode executionMode = PROJECT_ISOLATION;
		private boolean parallelExecution = true;
		private boolean incrementalAnalysis = false;
		private String excludedClasses = "";
		private String excludedMethods = "";
		private String avoidCallsTo = DEFAULT_AVOID_CALLS_TO_LIST;
		private String mutators = DEFAULT_MUTATORS;

		private Builder() {
		}

		public Builder withExecutionMode(PitExecutionMode executionMode) {
			this.executionMode = executionMode;
			return this;
		}

		public Builder withParallelExecution(boolean parallelExecution) {
			this.parallelExecution = parallelExecution;
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

		public Builder withMutators(String mutators) {
			this.mutators = mutators;
			return this;
		}

		public PitConfiguration build() {
			return new PitConfiguration(executionMode, parallelExecution, incrementalAnalysis, excludedClasses,
					excludedMethods, avoidCallsTo, mutators);
		}

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

	public String getMutators() {
		return mutators;
	}

}
