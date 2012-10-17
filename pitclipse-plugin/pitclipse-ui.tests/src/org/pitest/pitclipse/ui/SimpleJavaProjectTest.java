package org.pitest.pitclipse.ui;

import org.junit.Test;
import org.pitest.pitclipse.ui.behaviours.steps.ClassSteps;
import org.pitest.pitclipse.ui.behaviours.steps.PitclipseSteps;
import org.pitest.pitclipse.ui.behaviours.steps.ProjectSteps;

public class SimpleJavaProjectTest extends AbstractPitclipseUITest {

	private static final String TREVOR_BROOKES_DO_MY_THING_TEST = "@Test public void tbTestCase1() {org.junit.Assert.assertEquals(10, new TrevorBrookes().doMyThing(5));}";
	private static final String TREVOR_BROOKES_DO_MY_THING = "public int doMyThing(int i) {return 2 * i;}";
	private static final String NORMA_JEAN_DO_MY_THING = "public int doMyThing(int i) {return i + 20;}";
	private static final String NORMA_JEAN_DO_MY_THING_TEST = "@Test public void njTestCase1() {org.junit.Assert.assertEquals(21, new NormaJean().doMyThing(1));}";
	private static final String COD_BOB = "public int doBob(int i) {return i;}";
	private static final String TROUT_BOB = "public int doBob(int i) {return i;}";
	private static final String COD_BOB_TEST = "@Test public void codTest() {org.junit.Assert.assertEquals(1, new Cod().doBob(1));}";
	private static final String TROUT_BOB_TEST = "@Test public void troutTest() {org.junit.Assert.assertEquals(1, new Trout().doBob(1));}";
	private static final String FROG_RIBBIT = "public int doRibbit() {return 0;}";
	private static final String FROG_RIBBIT_TEST = "@Test public void frogTest() {org.junit.Assert.assertEquals(0, new Frog().doRibbit());}";
	private static final String PROJECT_NAME = "SimpleProject";
	private static final String FOO_BAR_PACKAGE_NAME = "foo.bar";
	private static final String PLEBS_PACKAGE_NAME = "foo.bar.plebs";
	private static final String SLEBS_PACKAGE_NAME = "foo.bar.slebs";
	private static final String SEA_FISH_PACKAGE_NAME = "sea.fish";
	private static final String FRESHWATER_FISH_PACKAGE_NAME = "lake.fish";
	private static final String AMPHIBIAN_PACKAGE_NAME = "lake.amphibian";

	private static final TestClassMetaData FOO_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(FOO_BAR_PACKAGE_NAME).withClass("Foo").build();

	private static final TestClassMetaData BAR_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(FOO_BAR_PACKAGE_NAME).withClass("Bar").build();

	private static final TestClassMetaData NORMA_JEAN_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("NormaJean").build();

	private static final TestClassMetaData MARILYN_AT_FIRST_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("Marilyn").build();

	private static final TestClassMetaData MARILYN_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(SLEBS_PACKAGE_NAME).withClass("Marilyn").build();

	private static final TestClassMetaData TREVOR_BROOKES_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("TrevorBrookes").build();

	private static final TestClassMetaData BRUNO_AT_FIRST_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(PLEBS_PACKAGE_NAME).withClass("BrunoBrookes").build();

	private static final TestClassMetaData BRUNO_BROOKES_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(SLEBS_PACKAGE_NAME).withClass("BrunoBrookes").build();

	private static final TestClassMetaData COD_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(SEA_FISH_PACKAGE_NAME).withClass("Cod").build();

	private static final TestClassMetaData TROUT_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(FRESHWATER_FISH_PACKAGE_NAME).withClass("Trout")
			.build();

	private static final TestClassMetaData FROG_META_DATA = TestClassMetaData
			.builder().withProject(PROJECT_NAME).withSrcDir("src")
			.withPackage(AMPHIBIAN_PACKAGE_NAME).withClass("Frog").build();

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
		classSteps.createClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		classSteps.verifyClassExists(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		runTest(FOO_META_DATA, 0, 100, 100);

		// Scenario: Add an empty unit test
		classSteps.selectClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		classSteps
				.createMethod("@Test public void fooTest1() {Foo foo = new Foo();}");
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
		classSteps.selectClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		classSteps
				.createMethod("@Test public void fooTest2() {new Foo().doFoo(1);}");
		runTest(FOO_META_DATA, 1, 100, 0);

		// Scenario: Create a better test for doFoo
		classSteps.selectClass(FOO_META_DATA.getProjectName(),
				FOO_META_DATA.getPackageName(),
				FOO_META_DATA.getTestClassName());
		classSteps
				.createMethod("@Test public void fooTest3() {org.junit.Assert.assertEquals(2, new Foo().doFoo(1));}");
		runTest(FOO_META_DATA, 1, 100, 100);

		// Scenario: Run PIT testing at the package level
		runPackageTest(FOO_META_DATA, 1, 100, 100);

		// Scenario: Run PIT at the package root level
		runPackageRootTest(FOO_META_DATA, 1, 100, 100);

		// Scenario: Create class Bar & it's Test
		createClassAndTest(BAR_META_DATA);
		runTest(BAR_META_DATA, 1, 0, 0);

		// Scenario: Add a method doBar to class Bar and it's test
		classSteps.selectClass(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(), BAR_META_DATA.getClassName());
		classSteps.createMethod("public int doBar(int i) {return i - 1;}");
		classSteps.selectClass(BAR_META_DATA.getProjectName(),
				BAR_META_DATA.getPackageName(),
				BAR_META_DATA.getTestClassName());
		classSteps
				.createMethod("@Test public void barTestCase1() {org.junit.Assert.assertEquals(0, new Bar().doBar(1));}");
		runTest(BAR_META_DATA, 2, 50, 50);

		// Scenario: Run PIT testing at the package level
		runPackageTest(FOO_META_DATA, 2, 100, 100);

		// Scenario: Run PIT at the package root level
		runPackageRootTest(FOO_META_DATA, 2, 100, 100);
	}

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
	}

	private void createClassAndTest(TestClassMetaData metaData,
			String testMethod, String methodUnderTest) {
		createClassAndTest(metaData);
		classSteps.selectClass(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getTestClassName());
		classSteps.createMethod(testMethod);
		classSteps.selectClass(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getClassName());
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
		classSteps.selectClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getClassName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getClassName());
		classSteps.selectClass(NORMA_JEAN_META_DATA.getProjectName(),
				NORMA_JEAN_META_DATA.getPackageName(),
				NORMA_JEAN_META_DATA.getTestClassName());
		classSteps.renameClass(MARILYN_AT_FIRST_META_DATA.getTestClassName());
		runTest(MARILYN_AT_FIRST_META_DATA, 1, 100, 100);

		// Create another class
		createClassAndTest(TREVOR_BROOKES_META_DATA,
				TREVOR_BROOKES_DO_MY_THING_TEST, TREVOR_BROOKES_DO_MY_THING);

		runTest(TREVOR_BROOKES_META_DATA, 2, 50, 50);
		runPackageTest(TREVOR_BROOKES_META_DATA, 2, 100, 100);
		runPackageRootTest(TREVOR_BROOKES_META_DATA, 2, 100, 100);

		classSteps.selectClass(TREVOR_BROOKES_META_DATA.getProjectName(),
				TREVOR_BROOKES_META_DATA.getPackageName(),
				TREVOR_BROOKES_META_DATA.getClassName());
		classSteps.renameClass(BRUNO_AT_FIRST_META_DATA.getClassName());
		classSteps.selectClass(TREVOR_BROOKES_META_DATA.getProjectName(),
				TREVOR_BROOKES_META_DATA.getPackageName(),
				TREVOR_BROOKES_META_DATA.getTestClassName());
		classSteps.renameClass(BRUNO_AT_FIRST_META_DATA.getTestClassName());

		runTest(BRUNO_AT_FIRST_META_DATA, 2, 50, 50);
		runPackageTest(BRUNO_AT_FIRST_META_DATA, 2, 100, 100);
		runPackageRootTest(BRUNO_AT_FIRST_META_DATA, 2, 100, 100);

		// Refactor package and retest
		classSteps.selectPackage(MARILYN_AT_FIRST_META_DATA.getProjectName(),
				MARILYN_AT_FIRST_META_DATA.getPackageName());
		classSteps.renamePackage(MARILYN_META_DATA.getPackageName());
		runTest(MARILYN_META_DATA, 2, 50, 50);
		runTest(BRUNO_BROOKES_META_DATA, 2, 50, 50);
		runPackageTest(MARILYN_META_DATA, 2, 100, 100);
		runPackageRootTest(MARILYN_META_DATA, 2, 100, 100);
	}

	private void runTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runTest(metaData.getProjectName(), metaData.getPackageName(),
				metaData.getTestClassName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void runPackageTest(TestClassMetaData metaData, int classesTested,
			int totalCoverage, int mutationCoverage) {
		pitSteps.runPackageTest(metaData.getProjectName(),
				metaData.getPackageName());
		pitSteps.coverageReportGenerated(classesTested, totalCoverage,
				mutationCoverage);
	}

	private void runPackageRootTest(TestClassMetaData metaData,
			int classesTested, int totalCoverage, int mutationCoverage) {
		pitSteps.runPackageRootTest(metaData.getProjectName(),
				metaData.getSourceDir());
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
		classSteps.createClass(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getTestClassName());
		classSteps.verifyClassExists(metaData.getProjectName(),
				metaData.getPackageName(), metaData.getTestClassName());

	}

}
