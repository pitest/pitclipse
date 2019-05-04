package org.pitest.pitclipse.runner.results.summary;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.pitest.classinfo.ClassInfo;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.ClassLine;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.coverage.CoverageSummary;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.pitclipse.example.Foo;
import org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.SummaryResultWrapper;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pitest.mutationtest.DetectionStatus.KILLED;
import static org.pitest.mutationtest.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.runner.TestFactory.TEST_FACTORY;

class SummaryResultListenerTestData {
    public static ClassMutationResults anUncoveredMutationOnFoo() {
        return aClassMutationResultForFooWithStatus(NO_COVERAGE);
    }

    public static ClassMutationResults aCoveredMutationOnFoo() {
        return aClassMutationResultForFooWithStatus(KILLED);
    }

    public static SummaryResultWrapper aSummary() {
        return new SummaryResultWrapper(SummaryResult.EMPTY);
    }

    public static CoverageDatabase anEmptyCoverageDatabase() {
        return new StubbedCoverage();
    }

    public static CoverageDatabase fooHasNoLineCoverage() {
        return CoverageTestData.FOO_WITH_NO_COVERAGE.coverageDatabase;
    }

    public static CoverageDatabase fooHasFullLineCoverage() {
        return CoverageTestData.FOO_WITH_FULL_COVERAGE.coverageDatabase;
    }

    private static ClassMutationResults aClassMutationResultForFooWithStatus(DetectionStatus detectionStatus) {
        Location location = new Location(ClassName.fromClass(Foo.class), MethodName.fromString("doFoo"), "doFoo");
        MutationIdentifier id = new MutationIdentifier(location, 1, "SomeMutator");
        MutationDetails md = new MutationDetails(id, "org/pitest/pitclipse/example/Foo.java", TEST_FACTORY.aString(),
                9, TEST_FACTORY.aRandomInt());
        MutationStatusTestPair status = new MutationStatusTestPair(TEST_FACTORY.aRandomInt(), detectionStatus,
                "org.pitest.pitclipse.example.ExampleTest");
        MutationResult mutation = new MutationResult(md, status);
        return new ClassMutationResults(ImmutableList.of(mutation));
    }

    private static class CoverageTestData {

        public static final CoverageTestData FOO_WITH_FULL_COVERAGE = new CoverageTestData(Foo.class, fooInfo(1), 1);
        public static final CoverageTestData FOO_WITH_NO_COVERAGE = new CoverageTestData(Foo.class, fooInfo(1), 0);

        public final ClassName className;
        public final ClassInfo classInfo;
        public final int linesCovered;
        public final StubbedCoverage coverageDatabase;

        private CoverageTestData(Class<?> clazz, ClassInfo classInfo, int linesCovered) {
            this.className = ClassName.fromClass(clazz);
            this.classInfo = classInfo;
            this.linesCovered = linesCovered;
            this.coverageDatabase = new StubbedCoverage(this);
        }

        private static ClassInfo fooInfo(int totalLines) {
            ClassInfo info = mock(ClassInfo.class);
            when(info.getNumberOfCodeLines()).thenReturn(totalLines);
            return info;
        }
    }

    private static class StubbedCoverage implements CoverageDatabase {
        // DATABASE;

        private final Map<ClassName, ClassInfo> classInfo;
        private final Map<ClassName, Integer> classCoverage;

        private StubbedCoverage(CoverageTestData coverageTestData) {
            classInfo = ImmutableMap.of(coverageTestData.className, coverageTestData.classInfo);
            classCoverage = ImmutableMap.of(coverageTestData.className, coverageTestData.linesCovered);
        }

        private StubbedCoverage() {
            classInfo = ImmutableMap.of();
            classCoverage = ImmutableMap.of();
        }

        @Override
        public Collection<ClassInfo> getClassInfo(Collection<ClassName> classes) {
            return filter(transform(classes, classInfoLookup()), notNull());
        }

        @Override
        public int getNumberOfCoveredLines(Collection<ClassName> classes) {
            int total = 0;
            for (ClassName className : classes) {
                total += coverageFor(className);
            }
            return total;
        }

        private int coverageFor(ClassName className) {
            return Optional.fromNullable(classCoverage.get(className)).or(0);
        }

        @Override
        public Collection<TestInfo> getTestsForClass(ClassName clazz) {
            return ImmutableList.of();
        }

        @Override
        public Collection<TestInfo> getTestsForClassLine(ClassLine classLine) {
            return ImmutableList.of();
        }

        @Override
        public BigInteger getCoverageIdForClass(ClassName clazz) {
            return TEST_FACTORY.aRandomBigInteger();
        }

        @Override
        public Collection<ClassInfo> getClassesForFile(String sourceFile, String packageName) {
            return ImmutableList.of();
        }

        @Override
        public CoverageSummary createSummary() {
            return null;
        }

        private Function<ClassName, ClassInfo> classInfoLookup() {
            return new Function<ClassName, ClassInfo>() {
                @Override
                public ClassInfo apply(ClassName input) {
                    return classInfo.get(input);
                }
            };
        }
    }
}