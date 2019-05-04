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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.pitclipse.runner.results.summary.SummaryResultListenerTestSugar.SummaryListenerFactory;

import static org.pitest.pitclipse.runner.results.MutationResultListenerLifecycle.using;

class SetupState {

    private final ImmutableList<ClassMutationResults> results;
    private final CoverageDatabase coverageDatabase;

    public SetupState(CoverageDatabase coverageDatabase, ImmutableList<ClassMutationResults> results) {
        this.coverageDatabase = coverageDatabase;
        this.results = results;
    }

    public Verification whenPitIsExecuted() {
        Optional<SummaryResult> result = using(SummaryListenerFactory.INSTANCE, coverageDatabase)
                .handleMutationResults(results);
        return new Verification(result);
    }
}
