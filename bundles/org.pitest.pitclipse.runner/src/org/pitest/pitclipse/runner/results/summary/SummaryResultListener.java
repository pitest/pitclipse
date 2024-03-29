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

import java.util.Collections;
import java.util.List;

import org.pitest.classinfo.ClassInfo;
import org.pitest.classinfo.ClassName;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.pitclipse.runner.results.Dispatcher;

/**
 * <p>Listens for mutations results sent by PIT in order to sum them up.</p>
 * 
 * <p>Once PIT analysis ends, the results are dispatched as a {@link SummaryResult} instance.</p>
 * 
 * <p>Instances of this class are provided to PIT thanks to {@link SummaryResultListenerFactory}.</p>
 */
public class SummaryResultListener implements MutationResultListener {

    private SummaryResult result = SummaryResult.EMPTY;
    private final Dispatcher<SummaryResult> dispatcher;
    private final CoverageDatabase coverage;

    /**
     * Creates a new listener that will compute a summary of the whole PIT analysis.
     * 
     * @param dispatcher
     *          The dispatcher use to forward the summary at the end of the summary.
     * @param coverage
     *          The coverage computed from the tests.
     */
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
        List<ClassName> classUnderTest = Collections.singletonList(results.getMutatedClass());
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
