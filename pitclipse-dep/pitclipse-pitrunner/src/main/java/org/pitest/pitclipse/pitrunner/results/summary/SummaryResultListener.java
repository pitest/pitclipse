package org.pitest.pitclipse.pitrunner.results.summary;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map.Entry;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.Dispatcher;
import org.pitest.pitclipse.reloc.guava.base.Function;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableListMultimap;
import org.pitest.pitclipse.reloc.guava.collect.Multimap;
import org.pitest.pitclipse.reloc.guava.collect.Multimaps;

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
		Collection<MutationResult> mutations = results.getMutations();
		Multimap<Integer, MutationResult> resultsByLine = groupByLineNumber(mutations);
		int totalLines = resultsByLine.size();
		int totalMutations = mutations.size();
		int linesCovered = countCoveredLines(resultsByLine);
		int survivedMutations = countSurvivedMutations(resultsByLine);
		Coverage lineCoverage = Coverage.from(linesCovered, totalLines);
		Coverage mutationCoverage = Coverage.from(totalMutations - survivedMutations, totalMutations);

		return new ClassSummary(lineCoverage, mutationCoverage);
	}

	private static int countCoveredLines(Multimap<Integer, MutationResult> resultsByLine) {
		int linesCovered = 0;
		Collection<Entry<Integer, MutationResult>> e = resultsByLine.entries();
		for (Entry<Integer, MutationResult> entry : e) {

		}
		return linesCovered;
	}

	private static int countSurvivedMutations(Multimap<Integer, MutationResult> resultsByLine) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static ImmutableListMultimap<Integer, MutationResult> groupByLineNumber(Collection<MutationResult> mutations) {
		return Multimaps.index(mutations, new Function<MutationResult, Integer>() {
			@Override
			public Integer apply(MutationResult result) {
				return result.getDetails().getLineNumber();
			}
		});
	}

	public Coverage getLineCoverage() {
		return lineCoverage;
	}
}

class Coverage {

	private static Coverage ZERO = new Coverage(BigDecimal.ZERO);
	private static Coverage FULLY_COVERED = new Coverage(BigDecimal.valueOf(100));

	private final BigDecimal coverage;

	private Coverage(BigDecimal coverage) {
		this.coverage = coverage;
	}

	public static Coverage from(int linesCovered, int totalLines) {
		if (linesCovered == totalLines)
			return FULLY_COVERED;
		else if (linesCovered == 0 || totalLines == 0)
			return ZERO;
		else
			return new Coverage(BigDecimal.valueOf(linesCovered, 2).divide(BigDecimal.valueOf(totalLines)));
	}
}
