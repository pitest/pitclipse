package org.pitest.pitclipse.pitrunner.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;

import org.junit.Before;
import org.junit.Test;

public class PitConfigurationTest {

	private static final String DEFAULT_MUTATORS = "DEFAULTS";
	private PitConfiguration config;

	@Test
	public void noScopeDefinedDefaultsToProject() {
		givenNoExecutionScopeIsSupplied();
		thenTheDefaultScopeIsProjectLevel();
	}

	@Test
	public void noMutatorsDefinedDefaultsToDefault() {
		givenNoMutatorsAreSupplied();
		thenTheDefaultMutatorsAreUsed();
	}

	private void givenNoExecutionScopeIsSupplied() {
		defaultConfig();
	}

	private void givenNoMutatorsAreSupplied() {
		defaultConfig();
	}

	private void thenTheDefaultScopeIsProjectLevel() {
		assertEquals(PROJECT_ISOLATION, config.getExecutionMode());
	}

	private void thenTheDefaultMutatorsAreUsed() {
		assertThat(config.getMutators(), is(equalTo(DEFAULT_MUTATORS)));
	}

	@Before
	public void cleanup() {
		config = null;
	}

	private void defaultConfig() {
		config = PitConfiguration.builder().build();
	}
}
