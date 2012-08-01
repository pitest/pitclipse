package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String PROJECT_NAME = "SimpleProject";
	private static final String PACKAGE_NAME = "foo.bar";
	private static final String CLASS_NAME = "FooBar";

	private final ProjectSteps projectSteps = new ProjectSteps();
	private final ClassSteps classSteps = new ClassSteps();

	@Test
	public void test() {
		projectSteps.createJavaProject(PROJECT_NAME);
		projectSteps.verifyProjectExists(PROJECT_NAME);
		classSteps.createClass(CLASS_NAME, PACKAGE_NAME, PROJECT_NAME);
		classSteps.verifyPackageExists(PACKAGE_NAME, PROJECT_NAME);
		classSteps.verifyClassExists(CLASS_NAME, PACKAGE_NAME, PROJECT_NAME);
	}
}
