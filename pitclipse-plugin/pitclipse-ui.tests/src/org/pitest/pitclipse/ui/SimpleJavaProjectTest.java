package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String PROJECT_NAME = "SimpleProject";
	private static final String FOO_BAR_PACKAGE_NAME = "foo.bar";
	private static final String PLEBS_PACKAGE_NAME = "foo.bar.plebs";
	private static final String SLEBS_PACKAGE_NAME = "foo.bar.slebs";

	private static final TestClassMetaData FOO_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME)
			.withPackage(FOO_BAR_PACKAGE_NAME).withClass("Foo").build();

	private static final TestClassMetaData BAR_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME)
			.withPackage(FOO_BAR_PACKAGE_NAME).withClass("Bar").build();

	private static final TestClassMetaData NORMA_JEAN_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME)
			.withPackage(PLEBS_PACKAGE_NAME).withClass("NormaJean").build();

	private static final TestClassMetaData MARILYN_AT_FIRST_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME)
			.withPackage(PLEBS_PACKAGE_NAME).withClass("Marilyn").build();

	private static final TestClassMetaData MARILYN_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME)
			.withPackage(SLEBS_PACKAGE_NAME).withClass("Marilyn").build();

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
		runTest(FOO_META_DATA, 0, 100, 100);

		// Scenario: Add an empty unit test
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test public void fooTest1() {Foo foo = new Foo();}");
		runTest(FOO_META_DATA, 0, 100, 100);

		// Scenario: Add a method doFoo to class Foo
		classSteps.selectClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(), FOO_META_DATA.getClassName());
		classSteps.createMethod("public int doFoo(int i) {return i + 1;}");
		pitSteps.runTest(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		pitSteps.coverageReportGenerated(1, 50, 0);

		// Scenario: Create a bad test for doFoo
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test public void fooTest2() {new Foo().doFoo(1);}");
		runTest(FOO_META_DATA, 1, 100, 0);

		// Scenario: Create a better test for doFoo
		classSteps.selectTestClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName(), FOO_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test public void fooTest3() {org.junit.Assert.assertEquals(2, new Foo().doFoo(1));}");
		runTest(FOO_META_DATA, 1, 100, 100);

		// Scenario: Create class Bar & it's Test
		createClassAndTest(BAR_META_DATA);
		runTest(BAR_META_DATA, 1, 0, 0);

		// Scenario: Add a method doBar to class Bar and it's test
		classSteps.selectClass(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(), BAR_META_DATA.getClassName());
		classSteps.createMethod("public int doBar(int i) {return i - 1;}");
		classSteps.selectTestClass(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(),
				BAR_META_DATA.getTestClassName(), BAR_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test public void barTestCase1() {org.junit.Assert.assertEquals(0, new Bar().doBar(1));}");
		runTest(BAR_META_DATA, 2, 50, 50);
	}

	@Test
	public void checkPITLaunchesAfterRefactoringClasses() {
		// Scenario: Create a project
		projectSteps.createJavaProject(PROJECT_NAME);
		// Scenario: Create Norma Jean
		createClassAndTest(NORMA_JEAN_META_DATA);
		classSteps.selectTestClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getTestClassName(),
				NORMA_JEAN_META_DATA.getClassName());
		classSteps
				.createTestCase("@Test public void njTestCase1() {org.junit.Assert.assertEquals(21, new NormaJean().doMyThing(1));}");
		classSteps.selectClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getClassName());
		classSteps.createMethod("public int doMyThing(int i) {return i + 20;}");
		runTest(NORMA_JEAN_META_DATA, 1, 100, 100);

		// Refactor class and retest
		classSteps.selectClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getClassName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getClassName());
		classSteps.selectClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getTestClassName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getTestClassName());
		runTest(MARILYN_AT_FIRST_META_DATA, 1, 100, 100);

		// Refactor package and retest
		classSteps.selectPackage(MARILYN_AT_FIRST_META_DATA.getProjectName(),
				MARILYN_AT_FIRST_META_DATA.getPackageName());
		classSteps.renamePackage(MARILYN_META_DATA.getPackageName());
		runTest(MARILYN_META_DATA, 1, 100, 100);
	}

	private void runTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runTest(metaData.getProjectName(), metaData.getPackageName(),
				metaData.getTestClassName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void createClassAndTest(TestClassMetaData metaData) {
		classSteps.createClass(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getClassName());
		classSteps.verifyPackageExists(metaData.getProjectName(),
				metaData.getPackageName());
		classSteps.verifyClassExists(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getClassName());
		classSteps.createTestClass(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getTestClassName());
		classSteps.verifyClassExists(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getTestClassName());

	}

}
