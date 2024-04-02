/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.results.summary;

import static java.util.stream.Collectors.toSet;
import static org.pitest.mutationtest.DetectionStatus.KILLED;
import static org.pitest.mutationtest.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.runner.TestFactory.TEST_FACTORY;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.pitest.classinfo.ClassName;
import org.pitest.coverage.BlockLocation;
import org.pitest.coverage.ClassLine;
import org.pitest.coverage.ClassLines;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.pitclipse.example.Foo;
import org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.SummaryResultWrapper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
        Location location = new Location(ClassName.fromClass(Foo.class), "doFoo", "doFoo");
        MutationIdentifier id = new MutationIdentifier(location, 1, "SomeMutator");
        MutationDetails md = new MutationDetails(id, "org/pitest/pitclipse/example/Foo.java", TEST_FACTORY.aString(),
                9, TEST_FACTORY.aRandomInt());
        MutationStatusTestPair status = new MutationStatusTestPair(TEST_FACTORY.aRandomInt(), detectionStatus,
                "org.pitest.pitclipse.example.ExampleTest");
        MutationResult mutation = new MutationResult(md, status);
        return new ClassMutationResults(ImmutableList.of(mutation));
    }

    private static class CoverageTestData {

        public static final CoverageTestData FOO_WITH_FULL_COVERAGE = new CoverageTestData(Foo.class, 1, 1);
        public static final CoverageTestData FOO_WITH_NO_COVERAGE = new CoverageTestData(Foo.class, 1, 0);

        public final ClassName className;
        public final int linesCovered;
        public final int totalNumberOfLines;
        public final StubbedCoverage coverageDatabase;

        private CoverageTestData(Class<?> clazz, int totalNumberOfLines, int linesCovered) {
            this.className = ClassName.fromClass(clazz);
            this.linesCovered = linesCovered;
            this.totalNumberOfLines = totalNumberOfLines;
            this.coverageDatabase = new StubbedCoverage(this);
        }

    }

    private static class StubbedCoverage implements CoverageDatabase {
        // DATABASE;

        private final Map<ClassName, Integer> classInfo;
        private final Map<ClassName, Integer> classCoverage;

        private StubbedCoverage(CoverageTestData coverageTestData) {
            classInfo = ImmutableMap.of(coverageTestData.className, coverageTestData.totalNumberOfLines);
            classCoverage = ImmutableMap.of(coverageTestData.className, coverageTestData.linesCovered);
        }

        private StubbedCoverage() {
            classInfo = ImmutableMap.of();
            classCoverage = ImmutableMap.of();
        }

		@Override
		public ClassLines getCodeLinesForClass(ClassName clazz) {
			int totalNumberOfLines = classInfo.getOrDefault(clazz, -1);
			Set<Integer> linesNumber = Stream.iterate(1, x -> x + 1).limit(totalNumberOfLines).collect(toSet());
			return new ClassLines(clazz, linesNumber);
		}

		@Override
		public Set<ClassLine> getCoveredLines(ClassName clazz) {
			int expectedNbOfLinesCovered = classCoverage.getOrDefault(clazz, -1);
			
			Set<ClassLine> lines = new HashSet<>();
			for (int i = 0; i < expectedNbOfLinesCovered; ++i) {
				lines.add(new ClassLine(clazz, i));
			}
			return lines;
		}

		@Override
		public Collection<TestInfo> getTestsForBlockLocation(BlockLocation location) {
			throw new UnsupportedOperationException("the stub does not implement getTestsForBlockLocation");
		}

        @Override
        public Collection<TestInfo> getTestsForClass(ClassName clazz) {
            return ImmutableList.of();
        }

        @Override
        public BigInteger getCoverageIdForClass(ClassName clazz) {
            return TEST_FACTORY.aRandomBigInteger();
        }

        @Override
        public Collection<ClassLines> getClassesForFile(String sourceFile, String packageName) {
            return ImmutableList.of();
        }

    }
}
