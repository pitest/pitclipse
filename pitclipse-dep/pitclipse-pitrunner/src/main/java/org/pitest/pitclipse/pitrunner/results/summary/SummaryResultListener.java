package org.pitest.pitclipse.pitrunner.results.summary;

import java.math.BigDecimal;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.Dispatcher;

public class SummaryResultListener implements MutationResultListener {

	private SummaryResult result = SummaryResult.EMPTY;
	private final Dispatcher<SummaryResult> dispatcher;

	public SummaryResultListener(Dispatcher<SummaryResult> dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void runStart() {
		result = SummaryResult.EMPTY;
	}

	@Override
	public void handleMutationResult(ClassMutationResults results) {
		ClassSummary classSummary = ClassSummary.from(results);
		result = result.update(classSummary);
	}

	@Override
	public void runEnd() {
		dispatcher.dispatch(result);
	}
}

class ClassSummary {
	private final Coverage lineCoverage;

	private ClassSummary(Coverage lineCoverage, Coverage mutationCoverage) {
		this.lineCoverage = lineCoverage;
	}

	public static ClassSummary from(ClassMutationResults results) {
		int totalLines = 0;
		Coverage lineCoverage = Coverage.from(0, totalLines);
		Coverage mutationCoverage = Coverage.from(0, totalLines);

		return new ClassSummary(lineCoverage, mutationCoverage);
	}

	public Coverage getLineCoverage() {
		return lineCoverage;
	}

}

class Coverage {

	private static Coverage ZERO = new Coverage(BigDecimal.ZERO);

	private BigDecimal coverage;

	private Coverage(BigDecimal zero2) {
		// TODO Auto-generated constructor stub
	}

	public static Coverage from(int linesCovered, int totalLines) {
		if (linesCovered == 0 || totalLines == 0)
			return Coverage.ZERO;
		return new Coverage(BigDecimal.valueOf(linesCovered, 2).divide(BigDecimal.valueOf(totalLines)));
	}
}
