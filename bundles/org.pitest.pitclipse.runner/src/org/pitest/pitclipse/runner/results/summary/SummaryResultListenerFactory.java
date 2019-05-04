package org.pitest.pitclipse.runner.results.summary;

import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.pitclipse.runner.results.Dispatcher;

import java.util.Properties;

public class SummaryResultListenerFactory implements MutationResultListenerFactory {
    @Override
    public String description() {
        return "Pitclipse summary result plugin";
    }

    @Override
    public MutationResultListener getListener(Properties properties, ListenerArguments args) {
        Dispatcher<SummaryResult> dispatcher = new Dispatcher<SummaryResult>() {
            @Override
            public void dispatch(SummaryResult result) {
                // NO OP
            }
        };
        return new SummaryResultListener(dispatcher, args.getCoverage());
    }

    @Override
    public String name() {
        return "PITCLIPSE_SUMMARY";
    }
}
