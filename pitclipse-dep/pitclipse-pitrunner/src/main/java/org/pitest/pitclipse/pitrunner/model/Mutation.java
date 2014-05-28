package org.pitest.pitclipse.pitrunner.model;

import org.pitest.pitclipse.pitrunner.results.DetectionStatus;

public class Mutation implements Visitable {

	private final String killingTest;
	private final int lineNumber;
	private final String mutatedMethod;
	private final String mutator;
	private final DetectionStatus status;
	private final String description;
	private final ClassMutations classMutations;

	private Mutation(ClassMutations classMutations, String killingTest, int lineNumber, String mutatedMethod,
			String mutator, DetectionStatus status, String description) {
		this.classMutations = classMutations;
		this.killingTest = killingTest;
		this.lineNumber = lineNumber;
		this.mutatedMethod = mutatedMethod;
		this.mutator = mutator;
		this.status = status;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public ClassMutations getClassMutations() {
		return classMutations;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder copyOf() {
		return new Builder().withDescription(description).withKillingTest(killingTest).withLineNumber(lineNumber)
				.withMutatedMethod(mutatedMethod).withMutator(mutator).withStatus(status);
	}

	public static class Builder {
		private String killingTest;
		private int lineNumber;
		private String mutatedMethod;
		private String mutator;
		private DetectionStatus status;
		private String description;
		private ClassMutations classMutations;

		private Builder() {
		}

		public Mutation build() {
			return new Mutation(classMutations, killingTest, lineNumber, mutatedMethod, mutator, status, description);
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

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withClassMutation(ClassMutations classMutations) {
			this.classMutations = classMutations;
			return this;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((killingTest == null) ? 0 : killingTest.hashCode());
		result = prime * result + lineNumber;
		result = prime * result + ((mutatedMethod == null) ? 0 : mutatedMethod.hashCode());
		result = prime * result + ((mutator == null) ? 0 : mutator.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mutation other = (Mutation) obj;
		if (killingTest == null) {
			if (other.killingTest != null)
				return false;
		} else if (!killingTest.equals(other.killingTest))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		if (mutatedMethod == null) {
			if (other.mutatedMethod != null)
				return false;
		} else if (!mutatedMethod.equals(other.mutatedMethod))
			return false;
		if (mutator == null) {
			if (other.mutator != null)
				return false;
		} else if (!mutator.equals(other.mutator))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mutation [killingTest=" + killingTest + ", lineNumber=" + lineNumber + ", mutatedMethod="
				+ mutatedMethod + ", mutator=" + mutator + ", status=" + status + "]";
	}

}
