package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String PROJECT_NAME = "SimpleProject";
	private static final String PACKAGE_NAME = "foo.bar";

	private static final TestClassMetaData FOO_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withPackage(PACKAGE_NAME)
			.withClass("Foo").build();
	/*
	 * private static final TestClassMetaData BAR_META_DATA = TestClassMetaData
	 * .builder().withProject(PROJECT_NAME).withPackage(PACKAGE_NAME)
	 * .withClass("Bar").build();
	 */
	private final ProjectSteps projectSteps = new ProjectSteps();
	private final ClassSteps classSteps = new ClassSteps();
	private final PitclipseSteps pitSteps = new PitclipseSteps();

	@Test
	public void createAClassAndTestAndRunWithPit() {
		projectSteps.createJavaProject(PROJECT_NAME);
		projectSteps.verifyProjectExists(PROJECT_NAME);
		classSteps.createClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(), FOO_META_DATA.getClassName());
		classSteps.verifyPackageExists(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName());
		classSteps.verifyClassExists(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(), FOO_META_DATA.getClassName());
		classSteps.createTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		classSteps.verifyClassExists(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(100);

	}
}
