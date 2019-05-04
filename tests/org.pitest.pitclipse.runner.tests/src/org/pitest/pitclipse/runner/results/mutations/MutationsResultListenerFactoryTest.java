package org.pitest.pitclipse.runner.results.mutations;

import org.junit.Test;
import org.pitest.mutationtest.ListenerArguments;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MutationsResultListenerFactoryTest {

    private final MutationsResultListenerFactory factory = new MutationsResultListenerFactory();

    @Test
    public void theListenerDescribesItselfSensibly() {
        assertThat(factory.description(), is(not(nullValue())));
        assertThat(factory.name(), is(equalTo("PITCLIPSE_MUTATIONS")));
    }

    @Test
    public void factoryProducesExpectedListener() {
        assertThat(factory.getListener(someProperties(), someArgs()),
                is(instanceOf(PitclipseMutationsResultListener.class)));
    }

    private ListenerArguments someArgs() {
        return null;
    }
    
    private Properties someProperties() { 
        return null;
    }
}
