package org.pitest.pitclipse.runner.results;

import org.pitest.coverage.CoverageDatabase;

public class ListenerContext<T> {
    public final Dispatcher<T> dispatcher;
    public final CoverageDatabase coverageData;

    public ListenerContext(Dispatcher<T> dispatcher, CoverageDatabase coverageData) {
        this.dispatcher = dispatcher;
        this.coverageData = coverageData;

    }
}