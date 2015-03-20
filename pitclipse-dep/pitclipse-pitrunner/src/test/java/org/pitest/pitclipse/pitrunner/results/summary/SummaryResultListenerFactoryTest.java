package org.pitest.pitclipse.pitrunner.results.summary;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.pitest.mutationtest.ListenerArguments;

public class SummaryResultListenerFactoryTest {

	private final SummaryResultListenerFactory factory = new SummaryResultListenerFactory();

	@Test
	public void theListenerDescribesItselfSensibly() {
		assertThat(factory.description(), is(not(nullValue())));
		assertThat(factory.name(), is(equalTo("PITCLIPSE_SUMMARY")));
	}

	@Test
	public void factoryProducesExpectedListener() {
		assertThat(factory.getListener(someArgs()), is(instanceOf(SummaryResultListener.class)));
	}

	private ListenerArguments someArgs() {
		return null;
	}
}
