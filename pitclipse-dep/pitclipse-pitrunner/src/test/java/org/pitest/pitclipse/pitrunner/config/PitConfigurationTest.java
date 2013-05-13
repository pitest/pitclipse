package org.pitest.pitclipse.pitrunner.config;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.WORKSPACE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PitConfigurationTest {

	@Mock
	private PitConfigurationVisitor visitor;
	private PitConfiguration config;

	@Test
	public void noScopeDefinedDefaultsToProject() {
		givenNoExecutionScopeIsSupplied();
		whenTheVisitorIsCalled();
		thenTheProjectLevelVisitorMethodIsCalled();
	}

	@Test
	public void projectScopeDefined() {
		givenProjectLevelExecutionScopeIsSpecified();
		whenTheVisitorIsCalled();
		thenTheProjectLevelVisitorMethodIsCalled();
	}

	@Test
	public void workspaceScopeDefined() {
		givenWorkspaceLevelExecutionScopeIsSpecified();
		whenTheVisitorIsCalled();
		thenTheWorkspaceLevelVisitorMethodIsCalled();
	}

	private void thenTheWorkspaceLevelVisitorMethodIsCalled() {
		verify(visitor, only()).visitWorkspaceLevelConfiguration(config);
	}

	private void givenNoExecutionScopeIsSupplied() {
		config = PitConfiguration.builder().build();
	}

	private void givenProjectLevelExecutionScopeIsSpecified() {
		config = PitConfiguration.builder()
				.withExecutionMode(PROJECT_ISOLATION).build();
	}

	private void givenWorkspaceLevelExecutionScopeIsSpecified() {
		config = PitConfiguration.builder().withExecutionMode(WORKSPACE)
				.build();
	}

	private void whenTheVisitorIsCalled() {
		config.accept(visitor);
	}

	private void thenTheProjectLevelVisitorMethodIsCalled() {
		verify(visitor, only()).visitProjectLevelConfiguration(config);
	}

	@Before
	public void cleanup() {
		config = null;
	}
}
