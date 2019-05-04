package org.pitest.pitclipse.runner.results.summary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.runner.results.ListenerContext;
import org.pitest.pitclipse.runner.results.ListenerFactory;

class SummaryResultListenerTestSugar {

    static class CoverageState {
        private final CoverageDatabase coverageDatabase;

        private CoverageState(CoverageDatabase coverageDatabase) {
            this.coverageDatabase = coverageDatabase;
        }

        public SetupState and(ClassMutationResults first, ClassMutationResults... others) {
            final Builder<ClassMutationResults> r = ImmutableList.builder();
            r.add(first);
            if (null != others) {
                r.add(others);
            }
            return new SetupState(coverageDatabase, r.build());
        }

        public SetupState andNoMutations() {
            return new SetupState(coverageDatabase, ImmutableList.<ClassMutationResults>of());
        }

        static CoverageState given(CoverageDatabase coverageDatabase) {
            return new CoverageState(coverageDatabase);
        }
    }

    static SummaryResult empty() {
        return SummaryResult.EMPTY;
    }

    static class SummaryResultWrapper {

        private final SummaryResult result;

        public SummaryResultWrapper(SummaryResult result) {
            this.result = result;
        }

        public SummaryResultWrapper withCoverageOf(String className, int linesCovered, int totalLines, int mutationsCovered, int totalMutations) {
            Coverage lineCoverage = Coverage.from(linesCovered, totalLines);
            Coverage mutationCoverage = Coverage.from(mutationsCovered, totalMutations);
            ClassSummary classSummary = ClassSummary.from(className, lineCoverage, mutationCoverage);
            return new SummaryResultWrapper(result.update(classSummary));
        }

        public SummaryResult getResult() {
            return result;
        }
    }
    
    enum SummaryListenerFactory implements ListenerFactory<SummaryResult> {
        INSTANCE;

        @Override
        public MutationResultListener apply(ListenerContext<SummaryResult> context) {
            return new SummaryResultListener(context.dispatcher, context.coverageData);
        }
    }
}