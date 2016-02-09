package org.pitest.pitclipse.pitrunner.results.summary;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.filter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;

import org.pitest.classinfo.ClassInfo;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.pitrunner.results.Dispatcher;
import org.pitest.pitclipse.reloc.guava.base.Predicate;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class SummaryResultListener implements MutationResultListener {

	private SummaryResult result = SummaryResult.EMPTY;
	private final Dispatcher<SummaryResult> dispatcher;
	private final CoverageDatabase coverage;

	public SummaryResultListener(Dispatcher<SummaryResult> dispatcher, CoverageDatabase coverage) {
		this.dispatcher = dispatcher;
		this.coverage = coverage;
	}

	@Override
	public void runStart() {
		result = SummaryResult.EMPTY;
	}

	@Override
	public void handleMutationResult(ClassMutationResults results) {
		List<ClassName> classUnderTest = ImmutableList.of(results.getMutatedClass());
		int coveredLines = coverage.getNumberOfCoveredLines(classUnderTest);
		for (ClassInfo info : coverage.getClassInfo(classUnderTest)) {
			ClassSummary classSummary = ClassSummary.from(results, info, coveredLines);
			result = result.update(classSummary);
		}
	}

	@Override
	public void runEnd() {
		dispatcher.dispatch(result);
	}
}

class ClassSummary implements Serializable {

	private static final long serialVersionUID = 6039947777282909605L;
	private final Coverage lineCoverage;
	private final String className;
	private final Coverage mutationCoverage;

	private ClassSummary(String className, Coverage lineCoverage, Coverage mutationCoverage) {
		this.className = className;
		this.lineCoverage = lineCoverage;
		this.mutationCoverage = mutationCoverage;
	}

	public static ClassSummary from(ClassMutationResults results, ClassInfo classInfo, int linesCovered) {
		Collection<MutationResult> mutations = results.getMutations();
		// int totalLines = resultsByLine.size();
		int totalMutations = mutations.size();
		// int linesCovered = countCoveredLines(resultsByLine);
		int survivedMutations = filter(mutations, DetectedMutations.INSTANCE).size();
		Coverage lineCoverage = Coverage.from(linesCovered, classInfo.getNumberOfCodeLines());
		Coverage mutationCoverage = Coverage.from(totalMutations - survivedMutations, totalMutations);

		return from(results.getMutatedClass().asJavaName(), lineCoverage, mutationCoverage);
	}

	public static ClassSummary from(String className, Coverage lineCoverage, Coverage mutationCoverage) {
		return new ClassSummary(className, lineCoverage, mutationCoverage);
	}

	public Coverage getLineCoverage() {
		return lineCoverage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((lineCoverage == null) ? 0 : lineCoverage.hashCode());
		result = prime * result + ((mutationCoverage == null) ? 0 : mutationCoverage.hashCode());
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
		ClassSummary other = (ClassSummary) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (lineCoverage == null) {
			if (other.lineCoverage != null)
				return false;
		} else if (!lineCoverage.equals(other.lineCoverage))
			return false;
		if (mutationCoverage == null) {
			if (other.mutationCoverage != null)
				return false;
		} else if (!mutationCoverage.equals(other.mutationCoverage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClassSummary [lineCoverage=" + lineCoverage + ", className=" + className + ", mutationCoverage="
				+ mutationCoverage + "]";
	}

	private static enum DetectedMutations implements Predicate<MutationResult> {
		INSTANCE;
		@Override
		public boolean apply(MutationResult m) {
			return !m.getStatus().isDetected();
		}
	}
}

class Coverage implements Serializable {

	private static final int PRECISION = 10;
	private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);
	private static final long serialVersionUID = 6511618552254606506L;

	private final int covered;
	private final int total;
	private final BigDecimal coverage;

	private Coverage(int covered, int total, BigDecimal coverage) {
		this.covered = covered;
		this.total = total;
		this.coverage = coverage;
	}

	public static Coverage from(int covered, int total) {
		if (covered == 0 || total == 0)
			return new Coverage(covered, total, BigDecimal.ZERO);
		else if (covered >= total)
			return new Coverage(covered, total, HUNDRED_PERCENT);
		else
			return new Coverage(covered, total, HUNDRED_PERCENT.multiply(BigDecimal.valueOf(covered, PRECISION))
					.divide(BigDecimal.valueOf(total, PRECISION), RoundingMode.HALF_EVEN));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coverage == null) ? 0 : coverage.hashCode());
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
		Coverage other = (Coverage) obj;
		if (coverage == null) {
			if (other.coverage != null)
				return false;
		} else if (!coverage.equals(other.coverage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Coverage [coverage=" + coverage + ", covered=" + covered + ", total=" + total + "]";
	}
}
