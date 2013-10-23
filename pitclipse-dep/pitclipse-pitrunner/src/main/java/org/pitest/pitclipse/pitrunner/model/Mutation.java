package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

public class Mutation implements Visitable {

	private final String killingTest;
	private final int lineNumber;
	private final String mutatedMethod;
	private final String mutator;
	private final DetectionStatus status;

	private Mutation(String killingTest, int lineNumber, String mutatedMethod, String mutator, DetectionStatus status) {
		this.killingTest = killingTest;
		this.lineNumber = lineNumber;
		this.mutatedMethod = mutatedMethod;
		this.mutator = mutator;
		this.status = status;
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitMutation(this);
	}

	public String getKillingTest() {
		return killingTest;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getMutatedMethod() {
		return mutatedMethod;
	}

	public String getMutator() {
		return mutator;
	}

	public DetectionStatus getStatus() {
		return status;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String killingTest;
		private int lineNumber;
		private String mutatedMethod;
		private String mutator;
		private DetectionStatus status;

		private Builder() {
		}

		public Mutation build() {
			return new Mutation(killingTest, lineNumber, mutatedMethod, mutator, status);
		}

		public Builder withKillingTest(String killingTest) {
			this.killingTest = killingTest;
			return this;
		}

		public Builder withLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
			return this;
		}

		public Builder withMutatedMethod(String mutatedMethod) {
			this.mutatedMethod = mutatedMethod;
			return this;
		}

		public Builder withMutator(String mutator) {
			this.mutator = mutator;
			return this;
		}

		public Builder withStatus(DetectionStatus status) {
			this.status = status;
			return this;
		}
	}

}
