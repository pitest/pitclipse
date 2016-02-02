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
public class PitExecutionModeTest {

	@Mock
	private PitExecutionModeVisitor<Void> visitor;

	@Test
	public void projectVisitorIsInkvoked() {
		whenTheProjectExecutionModeIsVisited();
		thenTheProjectExecutionVisitorMethodIsInvoked();
	}

	@Test
	public void workspaceVisitorIsInkvoked() {
		whenTheWorkspaceExecutionModeIsVisited();
		thenTheWorkspaceExecutionVisitorMethodIsInvoked();
	}

	private void whenTheProjectExecutionModeIsVisited() {
		PROJECT_ISOLATION.accept(visitor);
	}

	private void whenTheWorkspaceExecutionModeIsVisited() {
		WORKSPACE.accept(visitor);
	}

	private void thenTheProjectExecutionVisitorMethodIsInvoked() {
		verify(visitor, only()).visitProjectLevelConfiguration();
	}

	private void thenTheWorkspaceExecutionVisitorMethodIsInvoked() {
		verify(visitor, only()).visitWorkspaceLevelConfiguration();
	}
}
