package org.pitest.pitclipse.pitrunner.results.summary;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.ListenerArguments;

@RunWith(MockitoJUnitRunner.class)
public class SummaryResultListenerFactoryTest {

    private final SummaryResultListenerFactory factory = new SummaryResultListenerFactory();

    @Test
    public void theListenerDescribesItselfSensibly() {
        assertThat(factory.description(), is(not(nullValue())));
        assertThat(factory.name(), is(equalTo("PITCLIPSE_SUMMARY")));
    }

    @Test
    public void factoryProducesExpectedListener() {
        assertThat(factory.getListener(someProperties(), someArgs()), is(instanceOf(SummaryResultListener.class)));
    }

    private ListenerArguments someArgs() {
        ListenerArguments args = mock(ListenerArguments.class);
        when(args.getCoverage()).thenReturn(mock(CoverageDatabase.class));
        return args;
    }

    private Properties someProperties() {
        return null;
    }
}
