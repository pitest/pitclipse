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
