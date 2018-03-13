package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.classinfo.ClassInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResult;
import org.pitest.pitclipse.reloc.guava.base.MoreObjects;
import org.pitest.pitclipse.reloc.guava.base.Objects;
import org.pitest.pitclipse.reloc.guava.base.Predicate;

import java.io.Serializable;
import java.util.Collection;

import static org.pitest.pitclipse.reloc.guava.collect.Collections2.filter;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClassSummary that = (ClassSummary) o;

        return Objects.equal(className, that.className) &&
            Objects.equal(lineCoverage, that.lineCoverage) &&
            Objects.equal(mutationCoverage, that.mutationCoverage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lineCoverage, className, mutationCoverage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("className", className)
            .add("lineCoverage", lineCoverage)
            .add("mutationCoverage", mutationCoverage)
            .toString();
    }

    private enum DetectedMutations implements Predicate<MutationResult> {
        INSTANCE;
        @Override
        public boolean apply(MutationResult m) {
            return !m.getStatus().isDetected();
        }
    }
}
