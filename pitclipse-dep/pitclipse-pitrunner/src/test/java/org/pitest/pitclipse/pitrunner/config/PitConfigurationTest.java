package org.pitest.pitclipse.pitrunner.config;

import static org.junit.Assert.assertEquals;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;

import org.junit.Before;
import org.junit.Test;

public class PitConfigurationTest {

	private PitConfiguration config;

	@Test
	public void noScopeDefinedDefaultsToProject() {
		givenNoExecutionScopeIsSupplied();
		thenTheDefaultScopeIsProjectLevel();
	}

	private void givenNoExecutionScopeIsSupplied() {
		config = PitConfiguration.builder().build();
	}

	private void thenTheDefaultScopeIsProjectLevel() {
		assertEquals(PROJECT_ISOLATION, config.getExecutionMode());
	}

	@Before
	public void cleanup() {
		config = null;
	}
}
