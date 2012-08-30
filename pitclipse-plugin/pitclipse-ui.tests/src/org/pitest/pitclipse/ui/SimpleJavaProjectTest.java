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
	private static final String NL = System.getProperty("line.separator");

	private static final TestClassMetaData BAR_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withPackage(PACKAGE_NAME)
			.withClass("Bar").build();

	private final ProjectSteps projectSteps = new ProjectSteps();
	private final ClassSteps classSteps = new ClassSteps();
	private final PitclipseSteps pitSteps = new PitclipseSteps();

	@Test
	public void createAClassAndTestAndRunWithPit() {
		// Scenario: Create a project
		projectSteps.createJavaProject(PROJECT_NAME);
		projectSteps.verifyProjectExists(PROJECT_NAME);

		// Scenario: Create class Foo & it's Test
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
		pitSteps.coverageReportGenerated(0, 100, 100);

		// Scenario: Add an empty unit test
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps.createTestCase("@Test" + NL + "public void testCase1() {"
				+ NL + "Foo foo = new Foo();" + NL + "}");
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(0, 100, 100);

		// Scenario: Add a method doFoo to class Foo
		classSteps.selectClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(), FOO_META_DATA.getClassName());
		classSteps.createMethod("public int doFoo(int i) {" + NL
				+ "return i + 1;" + NL + "}");
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(1, 50, 0);

		// Scenario: Create a bad test for doFoo
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test" + NL + "public void testCase2() {" + NL
						+ "Foo foo = new Foo();" + NL + "foo.doFoo(1);" + NL
						+ "}");
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(1, 100, 0);

		// Scenario: Create a better test for doFoo
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps.createTestCase("@Test" + NL + "public void testCase3() {"
				+ NL + "Foo foo = new Foo();" + NL
				+ "org.junit.Assert.assertEquals(2, foo.doFoo(1));" + NL + "}");
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(1, 100, 100);

		// Scenario: Create class Bar & it's Test
		classSteps.createClass(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(), BAR_META_DATA.getClassName());
		classSteps.verifyPackageExists(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName());
		classSteps.verifyClassExists(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(), BAR_META_DATA.getClassName());
		classSteps.createTestClass(FOO_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(),
				BAR_META_DATA.getTestClassName());
		classSteps.verifyClassExists(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(),
				BAR_META_DATA.getTestClassName());
		pitSteps.runTest(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(),
				BAR_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(1, 0, 0);

	}

}
