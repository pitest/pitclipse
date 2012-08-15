package org.pitest.pitclipse.ui.swtbot;

public class ResultsParser {

	public static final class Summary {

		private final int classes;
		private final double codeCoverage;
		private final double mutationCoverage;

		private Summary(int classes, double codeCoverage,
				double mutationCoverage) {
			this.classes = classes;
			this.codeCoverage = codeCoverage;
			this.mutationCoverage = mutationCoverage;
		}

		public int getClasses() {
			return classes;
		}

		public double getCodeCoverage() {
			return codeCoverage;
		}

		public double getMutationCoverage() {
			return mutationCoverage;
		}

	}

	private static final String SUMMARY_START = "<h3>Project Summary</h3>";
	private static final String SUMMARY_END = "</table>";

	private final String html;

	public ResultsParser(String html) {
		this.html = html;
	}

	private String getProjectSummary() {
		String summary = "";
		int startPos = html.indexOf(SUMMARY_START);
		if (startPos != -1) {
			int endPos = html.indexOf(SUMMARY_END, startPos);
			if (endPos != -1) {
				return html.substring(startPos, endPos + SUMMARY_END.length());
			}
		}
		return summary;
	}

	public Summary getSummary() {
		String summary = getProjectSummary();
		int classes = 0;
		double codeCoverage = 100;
		double mutationCoverage = 100;
		if (!summary.isEmpty()) {
			HtmlTable table = new HtmlTable(summary);
		}
		return new Summary(classes, codeCoverage, mutationCoverage);
	}

}
