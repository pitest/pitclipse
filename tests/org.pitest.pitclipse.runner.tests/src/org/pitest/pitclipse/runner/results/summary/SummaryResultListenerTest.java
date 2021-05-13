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

import org.junit.Test;
import org.pitest.pitclipse.example.Foo;

import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.aCoveredMutationOnFoo;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.aSummary;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.anEmptyCoverageDatabase;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.anUncoveredMutationOnFoo;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.fooHasFullLineCoverage;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestData.fooHasNoLineCoverage;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.CoverageState.given;
import static org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.empty;

public class SummaryResultListenerTest {
    @Test
    public void noResultsReturnsAnEmptyResult() { // NOSONAR: assertions made by called methods
        given(anEmptyCoverageDatabase())
            .andNoMutations()
            .whenPitIsExecuted()
            .thenTheResultsAre(empty());
    }

    @Test
    public void anUncoveredMutationResultReturnsZeroLineCoverage() { // NOSONAR: assertions made by called methods
        given(fooHasNoLineCoverage())
                .and(anUncoveredMutationOnFoo())
                .whenPitIsExecuted()
                .thenTheResultsAre(
                        aSummary().withCoverageOf(Foo.class.getCanonicalName(), 0, 1, 0, 1)
            );
    }

    @Test
    public void aCoveredMutationResultReturnsLineCoverage() { // NOSONAR: assertions made by called methods
        given(fooHasFullLineCoverage())
                .and(aCoveredMutationOnFoo())
                .whenPitIsExecuted()
                .thenTheResultsAre(
                        aSummary().withCoverageOf(Foo.class.getCanonicalName(), 1, 1, 1, 1)
            );
    }
}
