package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.assertTrue;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.pitest.pitclipse.ui.behaviours.Given;
import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;
import org.pitest.pitclipse.ui.behaviours.pageobjects.ConcreteClassContext;
import org.pitest.pitclipse.ui.behaviours.pageobjects.TestClassContext;

public final class ClassSteps {

	private ConcreteClassContext concreteClassContext = null;
	private TestClassContext testClassContext = null;

	@When("a class $className in package $packageName is created in project $projectName")
	public void createClass(String projectName, String packageName,
			String className) {
		INSTANCE.getBuildProgress().listenForBuild();
		INSTANCE.getPackageExplorer().selectProject(projectName);
		// Cannot use the Package explorer right click context menu
		// to create a class due to SWTBot bug 261360
		INSTANCE.getFileMenu().createClass(packageName, className);
		INSTANCE.getBuildProgress().waitForBuild();
	}

	@Then("package $packageName exists in project $projectName")
	public void verifyPackageExists(String projectName, String packageName) {
		assertTrue(
				"Package: " + packageName + " not found",
				INSTANCE.getPackageExplorer().doesPackageExistInProject(
						packageName, projectName));
	}

	@Then("class $className exists in package $packageName in project $projectName")
	public void verifyClassExists(String projectName, String packageName,
			String className) {
		assertTrue(
				"Class: " + className + " not found",
				INSTANCE.getPackageExplorer().doesClassExistInProject(
						className, packageName, projectName));
	}

	@When("a test class $testClassName in package $packageName is created in project $projectName")
	public void createTestClass(String projectName, String packageName,
			String testClassName) {
		testClassContext = new TestClassContext.Builder()
				.withClassName(testClassName).withPackageName(packageName)
				.withProjectName(projectName).build();
		INSTANCE.getBuildProgress().listenForBuild();
		INSTANCE.getPackageExplorer().selectProject(
				testClassContext.getProjectName());
		INSTANCE.getFileMenu().createJUnitTest(testClassContext);
		INSTANCE.getBuildProgress().waitForBuild();
		INSTANCE.getAbstractSyntaxTree().removeAllMethods(testClassContext);
		INSTANCE.getFileMenu().saveAll();
	}

	@When("a test case $testCase is created")
	public void createTestCase(String testCase) {
		testClassContext = new TestClassContext.Builder()
				.clone(testClassContext).withTestCase(testCase).build();
		INSTANCE.getBuildProgress().listenForBuild();
		INSTANCE.getPackageExplorer().openClass(testClassContext);
		INSTANCE.getAbstractSyntaxTree().addTestMethod(testClassContext);
		INSTANCE.getSourceMenu().organizeImports();
		INSTANCE.getFileMenu().saveAll();
		INSTANCE.getBuildProgress().waitForBuild();
	}

	@Given("the test $testClassName testing $classUnderTest in package $packageName in project $projectName is selected")
	public void selectTestClass(String projectName, String packageName,
			String testClassName, String classUnderTest) {
		testClassContext = new TestClassContext.Builder()
				.withClassUnderTest(classUnderTest)
				.withClassName(testClassName).withPackageName(packageName)
				.withProjectName(projectName)
				.withClassUnderTest(classUnderTest).build();

	}

	@Given("the class $testClassName in package $packageName in project $projectName is selected")
	public void selectClass(String projectName, String packageName,
			String className) {
		concreteClassContext = new ConcreteClassContext.Builder()
				.withProjectName(projectName).withPackageName(packageName)
				.withClassName(className).build();
	}

	@When("a method $method is created")
	public void createMethod(String method) {
		concreteClassContext = new ConcreteClassContext.Builder()
				.clone(concreteClassContext).withMethod(method).build();
		INSTANCE.getBuildProgress().listenForBuild();
		INSTANCE.getPackageExplorer().openClass(concreteClassContext);
		INSTANCE.getAbstractSyntaxTree().addMethod(concreteClassContext);
		INSTANCE.getSourceMenu().organizeImports();
		INSTANCE.getFileMenu().saveAll();
		INSTANCE.getBuildProgress().waitForBuild();
	}

}
