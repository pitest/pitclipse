package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String PROJECT_NAME = "Simple project";

	private final ProjectSteps projectSteps = new ProjectSteps();

	@Test
	public void test() {
		projectSteps.createJavaProject(PROJECT_NAME);
		projectSteps.verifyProjectExists(PROJECT_NAME);
	}

}
