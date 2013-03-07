package org.pitest.pitclipse.pitrunner.config;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.pitrunner.config.PitExecutionMode.WORKSPACE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PitConfigurationTest {

	@Mock
	private PitConfigurationVisitor visitor;

	@Test
	public void noScopeDefinedDefaultsToProject() {
		PitConfiguration config = PitConfiguration.builder().build();
		config.accept(visitor);
		verify(visitor, only()).visitProjectLevelConfiguration(config);
	}

	@Test
	public void projectScopeDefined() {
		PitConfiguration config = PitConfiguration.builder()
				.withExecutionMode(PROJECT_ISOLATION).build();
		config.accept(visitor);
		verify(visitor, only()).visitProjectLevelConfiguration(config);
	}

	@Test
	public void workspaceScopeDefined() {
		PitConfiguration config = PitConfiguration.builder()
				.withExecutionMode(WORKSPACE).build();
		config.accept(visitor);
		verify(visitor, only()).visitWorkspaceLevelConfiguration(config);
	}

}
