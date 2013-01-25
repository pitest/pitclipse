package org.pitest.pitclipse.core;

import static org.pitest.pitclipse.core.PitExecutionMode.PROJECT_ISOLATION;


public class PitConfiguration {

	private final PitExecutionMode executionMode;

	private PitConfiguration(PitExecutionMode executionMode) {
		this.executionMode = executionMode;
	}

	public void accept(PitConfigurationVisitor visitor) {
		switch (executionMode) {
		case PROJECT_ISOLATION:
			visitor.visitProjectLevelConfiguration();
			break;
		case WORKSPACE:
			visitor.visitWorkspaceLevelConfiguration();
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

	public static final class Builder {

		private PitExecutionMode executionMode = PROJECT_ISOLATION;

		private Builder() {
		}

		public Builder withExecutionMode(PitExecutionMode executionMode) {
			this.executionMode = executionMode;
			return this;
		}

		public PitConfiguration build() {
			return new PitConfiguration(executionMode);
		}

	}

}
