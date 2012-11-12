package org.pitest.pitclipse.ui;

import static com.google.common.collect.ImmutableList.of;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String FOO_DO_FOO_BAD_TEST = "@Test public void fooTest2() {new Foo().doFoo(1);}";
	private static final String FOO_DO_FOO = "public int doFoo(int i) {return i + 1;}";
	private static final String TREVOR_BROOKES_DO_MY_THING_TEST = "@Test public void tbTestCase1() {org.junit.Assert.assertEquals(10, new TrevorBrookes().doMyThing(5));}";
	private static final String TREVOR_BROOKES_DO_MY_THING = "public int doMyThing(int i) {return 2 * i;}";
	private static final String NORMA_JEAN_DO_MY_THING = "public int doMyThing(int i) {return i + 20;}";
	private static final String NORMA_JEAN_DO_MY_THING_TEST = "@Test public void njTestCase1() {org.junit.Assert.assertEquals(21, new NormaJean().doMyThing(1));}";
	private static final String COD_BOB = "public int doBob(int i) {return i;}";
	private static final String TROUT_BOB = "public int doBob(int i) {return i;}";
	private static final String COD_BOB_TEST = "@Test public void codTest() {org.junit.Assert.assertEquals(1, new Cod().doBob(1));}";
	private static final String TROUT_BOB_TEST = "@Test public void troutTest() {org.junit.Assert.assertEquals(1, new Trout().doBob(1));}";
	private static final String FROG_RIBBIT = "public int doRibbit() {return 1;}";
	private static final String FROG_RIBBIT_TEST = "@Test public void frogTest() {org.junit.Assert.assertEquals(1, new Frog().doRibbit());}";
	private static final String TEST_ALL = "public int doStuff(int i) {int j = new Foo().doFoo(i); int k = new Cod().doBob(i); int l = new Frog().doRibbit(); return j + k + l;}";
	private static final String TEST_ALL_TEST = "@Test public void testThings() {org.junit.Assert.assertEquals(4, new TestAll().doStuff(1));}";

	private static final String PROJECT_NAME = "SimpleProject";
	private static final String SECOND_PROJECT_NAME = "Project2";
	private static final String THIRD_PROJECT_NAME = "Project3";
	private static final String FOURTH_PROJECT_NAME = "Project4";
	private static final String FOO_BAR_PACKAGE_NAME = "foo.bar.foobar";
	private static final String PLEBS_PACKAGE_NAME = "foo.bar.plebs";
	private static final String SLEBS_PACKAGE_NAME = "foo.bar.slebs";
	private static final String SEA_FISH_PACKAGE_NAME = "sea.fish";
	private static final String FRESHWATER_FISH_PACKAGE_NAME = "lake.fish";
	private static final String AMPHIBIAN_PACKAGE_NAME = "lake.amphibian";
	private static final String SUITE_PACKAGE_NAME = "foo.bar.suite";

	private static final TestClassMetaData FOO_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(FOO_BAR_PACKAGE_NAME).withClass("Foo").build();

	private static final TestClassMetaData NORMA_JEAN_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("NormaJean").build();

	private static final TestClassMetaData MARILYN_AT_FIRST_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("Marilyn").build();

	private static final TestClassMetaData MARILYN_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(SLEBS_PACKAGE_NAME).withClass("Marilyn").build();

	private static final TestClassMetaData TREVOR_BROOKES_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("TrevorBrookes").build();

	private static final TestClassMetaData BRUNO_AT_FIRST_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("BrunoBrookes").build();

	private static final TestClassMetaData BRUNO_BROOKES_META_DATA = TestClassMetaData
			.builder().withProject(SECOND_PROJECT_NAME).withSrcDir("src")
			.withPackage(SLEBS_PACKAGE_NAME).withClass("BrunoBrookes").build();

	private static final TestClassMetaData COD_META_DATA = TestClassMetaData
			.builder().withProject(THIRD_PROJECT_NAME).withSrcDir("src")
			.withPackage(SEA_FISH_PACKAGE_NAME).withClass("Cod").build();

	private static final TestClassMetaData TROUT_META_DATA = TestClassMetaData
			.builder().withProject(THIRD_PROJECT_NAME).withSrcDir("src")
			.withPackage(FRESHWATER_FISH_PACKAGE_NAME).withClass("Trout")
			.build();

	private static final TestClassMetaData FROG_META_DATA = TestClassMetaData
			.builder().withProject(THIRD_PROJECT_NAME).withSrcDir("src")
			.withPackage(AMPHIBIAN_PACKAGE_NAME).withClass("Frog").build();

	private static final TestClassMetaData TEST_ALL_META_DATA = TestClassMetaData
			.builder().withProject(FOURTH_PROJECT_NAME).withSrcDir("src")
			.withPackage(SUITE_PACKAGE_NAME).withClass("TestAll").build();

	private final ProjectSteps projectSteps = new ProjectSteps();
	private final ClassSteps classSteps = new ClassSteps();
	private final PitclipseSteps pitSteps = new PitclipseSteps();

	@Test
	public void runTestsInDifferentPackages() {
		projectSteps.createJavaProject(PROJECT_NAME);
		// Create some classes & tests in other packages
		createClassAndTest(COD_META_DATA, COD_BOB_TEST, COD_BOB);
		createClassAndTest(TROUT_META_DATA, TROUT_BOB_TEST, TROUT_BOB);
		createClassAndTest(FROG_META_DATA, FROG_RIBBIT_TEST, FROG_RIBBIT);

		// Scenario: Run PIT at the package root level
		runPackageTest(COD_META_DATA, 3, 33, 33);
		runPackageTest(TROUT_META_DATA, 3, 33, 33);
		runPackageTest(FROG_META_DATA, 3, 33, 33);
		runPackageRootTest(COD_META_DATA, 3, 100, 100);
		runProjectTest(COD_META_DATA, 3, 100, 100);
	}

	private void createClassAndTest(TestClassMetaData metaData,
			String testMethod, String methodUnderTest) {
		createClassAndTest(metaData);
		classSteps.selectClass(metaData.getTestClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		classSteps.createMethod(testMethod);
		classSteps.selectClass(metaData.getClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		classSteps.createMethod(methodUnderTest);

	}

	@Test
	public void checkPITLaunchesAfterRefactoringClasses() {
		// Scenario: Create a project
		projectSteps.createJavaProject(PROJECT_NAME);
		// Scenario: Create Norma Jean
		createClassAndTest(NORMA_JEAN_META_DATA, NORMA_JEAN_DO_MY_THING_TEST,
				NORMA_JEAN_DO_MY_THING);
		runTest(NORMA_JEAN_META_DATA, 1, 100, 100);

		// Refactor class and retest
		classSteps.selectClass(NORMA_JEAN_META_DATA.getClassName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getProjectName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getClassName());
		classSteps.selectClass(NORMA_JEAN_META_DATA.getTestClassName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getProjectName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getTestClassName());
		runTest(MARILYN_AT_FIRST_META_DATA, 1, 100, 100);

		// Create another class
		createClassAndTest(TREVOR_BROOKES_META_DATA,
				TREVOR_BROOKES_DO_MY_THING_TEST, TREVOR_BROOKES_DO_MY_THING);

		runTest(TREVOR_BROOKES_META_DATA, 2, 50, 50);
		runPackageTest(TREVOR_BROOKES_META_DATA, 2, 100, 100);
		runPackageRootTest(TREVOR_BROOKES_META_DATA, 2, 100, 100);
		runProjectTest(TREVOR_BROOKES_META_DATA, 2, 100, 100);

		classSteps.selectClass(TREVOR_BROOKES_META_DATA.getClassName(),
				TREVOR_BROOKES_META_DATA.getPackageName(),
				TREVOR_BROOKES_META_DATA.getProjectName());
		classSteps.renameClass(BRUNO_AT_FIRST_META_DATA.getClassName());
		classSteps.selectClass(TREVOR_BROOKES_META_DATA.getTestClassName(),
				TREVOR_BROOKES_META_DATA.getPackageName(),
				TREVOR_BROOKES_META_DATA.getProjectName());
		classSteps.renameClass(BRUNO_AT_FIRST_META_DATA.getTestClassName());

		runTest(BRUNO_AT_FIRST_META_DATA, 2, 50, 50);
		runPackageTest(BRUNO_AT_FIRST_META_DATA, 2, 100, 100);
		runPackageRootTest(BRUNO_AT_FIRST_META_DATA, 2, 100, 100);
		runProjectTest(BRUNO_AT_FIRST_META_DATA, 2, 100, 100);

		// Refactor package and retest
		classSteps.selectPackage(MARILYN_AT_FIRST_META_DATA.getProjectName(),
				MARILYN_AT_FIRST_META_DATA.getPackageName());
		classSteps.renamePackage(MARILYN_META_DATA.getPackageName());
		runTest(MARILYN_META_DATA, 2, 50, 50);
		runTest(BRUNO_BROOKES_META_DATA, 2, 50, 50);
		runPackageTest(MARILYN_META_DATA, 2, 100, 100);
		runPackageRootTest(MARILYN_META_DATA, 2, 100, 100);
		runProjectTest(MARILYN_META_DATA, 2, 100, 100);
	}

	@Test
	public void multipleProjectsInAWorkspace() {
		projectSteps.createJavaProject(PROJECT_NAME);

		createClassAndTest(FOO_META_DATA, FOO_DO_FOO_BAD_TEST, FOO_DO_FOO);

		projectSteps.createJavaProject(SECOND_PROJECT_NAME);
		createClassAndTest(NORMA_JEAN_META_DATA, NORMA_JEAN_DO_MY_THING_TEST,
				NORMA_JEAN_DO_MY_THING);
		createClassAndTest(TREVOR_BROOKES_META_DATA,
				TREVOR_BROOKES_DO_MY_THING_TEST, TREVOR_BROOKES_DO_MY_THING);

		projectSteps.createJavaProject(THIRD_PROJECT_NAME);
		createClassAndTest(COD_META_DATA, COD_BOB_TEST, COD_BOB);
		createClassAndTest(TROUT_META_DATA, TROUT_BOB_TEST, TROUT_BOB);
		createClassAndTest(FROG_META_DATA, FROG_RIBBIT_TEST, FROG_RIBBIT);

		projectSteps.createJavaProject(FOURTH_PROJECT_NAME);
		projectSteps.addToBuildPath(FOURTH_PROJECT_NAME,
				of(PROJECT_NAME, SECOND_PROJECT_NAME, THIRD_PROJECT_NAME));
		createClassAndTest(TEST_ALL_META_DATA, TEST_ALL_TEST, TEST_ALL);

		runProjectTest(FOO_META_DATA, 1, 100, 0);
		runProjectTest(NORMA_JEAN_META_DATA, 2, 100, 100);
		runProjectTest(COD_META_DATA, 3, 100, 100);
		runProjectTest(TEST_ALL_META_DATA, 1, 100, 100);

		pitSteps.openPitConfig(TEST_ALL_META_DATA.getProjectName());
	}

	@Test
	public void projectConfiguration() {
		projectSteps.createJavaProject(PROJECT_NAME);
		createClassAndTest(FOO_META_DATA, FOO_DO_FOO_BAD_TEST, FOO_DO_FOO);
		runProjectTest(FOO_META_DATA, 1, 100, 0);
		pitSteps.openPitConfig(PROJECT_NAME);
	}

	private void runTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runTest(metaData.getTestClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void runPackageTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runPackageTest(metaData.getPackageName(),
				metaData.getProjectName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void runPackageRootTest(TestClassMetaData metaData,
			int classesTested, int totalCoverage, int mutationCoverage) {
		pitSteps.runPackageRootTest(metaData.getSourceDir(),
				metaData.getProjectName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void runProjectTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runProjectTest(metaData.getProjectName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void createClassAndTest(TestClassMetaData metaData) {
		classSteps.createClass(metaData.getClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		classSteps.verifyPackageExists(metaData.getPackageName(),
				metaData.getProjectName());
		classSteps.verifyClassExists(metaData.getClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		classSteps.createClass(metaData.getTestClassName(),
				metaData.getPackageName(), metaData.getProjectName());
		classSteps.verifyClassExists(metaData.getTestClassName(),
				metaData.getPackageName(), metaData.getProjectName());

	}

}
