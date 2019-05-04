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
