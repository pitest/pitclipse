package org.pitest.pitclipse.core;

import static org.pitest.pitclipse.core.PitExecutionMode.PROJECT_ISOLATION;

public class PitConfiguration {

	private final PitExecutionMode executionMode;
	private final boolean parallelExecution;

	private PitConfiguration(PitExecutionMode executionMode,
			boolean parallelExecution) {
		this.executionMode = executionMode;
		this.parallelExecution = parallelExecution;
	}

	public void accept(PitConfigurationVisitor visitor) {
		switch (executionMode) {
		case PROJECT_ISOLATION:
			visitor.visitProjectLevelConfiguration(this);
			break;
		case WORKSPACE:
			visitor.visitWorkspaceLevelConfiguration(this);
			break;
		default:
			throw new IllegalArgumentException("Unexpected execution mode: "
					+ executionMode);
		}
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

	public static final class Builder {

		private PitExecutionMode executionMode = PROJECT_ISOLATION;
		private boolean parallelExecution = true;

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

		public PitConfiguration build() {
			return new PitConfiguration(executionMode, parallelExecution);
		}

	}

}
