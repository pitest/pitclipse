package org.pitest.pitclipse.ui.swtbot;

public class PitResultsView {

	private final double totalCoverage;

	private PitResultsView(double totalCoverage) {
		this.totalCoverage = totalCoverage;
	}

	public double getTotalCoverage() {
		return totalCoverage;
	}

	public static final class Builder {

		private double totalCoverage = 0.0d;

		private Builder() {
		};

		public PitResultsView build() {
			return new PitResultsView(totalCoverage);
		}

		public Builder withTotalCoverage(double totalCoverage) {
			this.totalCoverage = totalCoverage;
			return this;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

}
