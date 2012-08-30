package org.pitest.pitclipse.ui.swtbot;

public class PitResultsView {

	private final double totalCoverage;
	private final double mutationCoverage;
	private final int classesTested;

	public PitResultsView(int classesTested, double totalCoverage,
			double mutationCoverage) {
		this.classesTested = classesTested;
		this.totalCoverage = totalCoverage;
		this.mutationCoverage = mutationCoverage;
	}

	public int getClassesTested() {
		return classesTested;
	}

	public double getMutationCoverage() {
		return mutationCoverage;
	}

	public double getTotalCoverage() {
		return totalCoverage;
	}

	@Override
	public String toString() {
		return "PitResultsView [totalCoverage=" + totalCoverage
				+ ", mutationCoverage=" + mutationCoverage + ", classesTested="
				+ classesTested + "]";
	}

	public static final class Builder {

		private double totalCoverage = 0.0d;
		private double mutationCoverage = 0.0d;
		private int classesTested = 0;

		private Builder() {
		};

		public PitResultsView build() {
			return new PitResultsView(classesTested, totalCoverage,
					mutationCoverage);
		}

		public Builder withTotalCoverage(double totalCoverage) {
			this.totalCoverage = totalCoverage;
			return this;
		}

		public Builder withMutationCoverage(double mutationCoverage) {
			this.mutationCoverage = mutationCoverage;
			return this;
		}

		public Builder withClassesTested(int classesTested) {
			this.classesTested = classesTested;
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

}
