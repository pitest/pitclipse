package org.pitest.pitclipse.pitrunner.results.summary;

import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.pitclipse.reloc.guava.base.Optional;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

import static org.pitest.pitclipse.pitrunner.results.MutationResultListenerLifecycle.using;

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
