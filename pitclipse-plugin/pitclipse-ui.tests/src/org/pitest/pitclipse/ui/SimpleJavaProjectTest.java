package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String PROJECT_NAME = "SimpleProject";
	private static final String PACKAGE_NAME = "foo.bar";
	private static final String FOO_BAR_CLASS_NAME = "FooBar";
	private static final String FOO_BAR_FULLY_QUALIFIED_NAME = "foo.bar.FooBar";
	private static final String A_METHOD = "";

	private final ProjectSteps projectSteps = new ProjectSteps();
	private final ClassSteps classSteps = new ClassSteps();

	@Test
	public void test() {
		projectSteps.createJavaProject(PROJECT_NAME);
		projectSteps.verifyProjectExists(PROJECT_NAME);
		classSteps.createClass(PROJECT_NAME, PACKAGE_NAME, FOO_BAR_CLASS_NAME);
		classSteps.verifyPackageExists(PROJECT_NAME, PACKAGE_NAME);
		classSteps.verifyClassExists(PROJECT_NAME, PACKAGE_NAME,
				FOO_BAR_CLASS_NAME);
	}
}
