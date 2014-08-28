package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.assertTrue;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.pitest.pitclipse.ui.behaviours.pageobjects.ConcreteClassContext;

public final class ClassSteps {

	private ConcreteClassContext concreteClassContext = null;

	@When("a class $className in package $packageName is created in project $projectName")
	public void classIsCreated(String className, String packageName, String projectName) {
		createClass(className, packageName, projectName);
	}

	@Given("a bad test for class $className in package $packageName is created in project $projectName")
	public void createClassWithBadTest(String className, String packageName, String projectName) {
		String testClass = className + "Test";
		createClass(testClass, packageName, projectName);
		selectClass(testClass, packageName, projectName);
		createMethod("@Test public void badTest() {" + className + " x = new " + className + "(); x.f(1);}");
		createClass(className, packageName, projectName);
		selectClass(className, packageName, projectName);
		createMethod("public int f(int i) {ArrayList<Object> pointless = new ArrayList<Object>(); if (pointless.size() == 1) return i + 1; else return 0;}");
	}

	@Given("a bad test for class $className is created in the default package in project $projectName")
	public void createClassWithBadTestInDefaultPackage(String className, String projectName) {
		String packageName = "";
		String testClass = className + "Test";
		createClass(testClass, packageName, projectName);
		selectClass(testClass, packageName, projectName);
		createMethod("@Test public void badTest() {" + className + " x = new " + className + "(); x.f(1);}");
		createClass(className, packageName, projectName);
		selectClass(className, packageName, projectName);
		createMethod("public int f(int i) {ArrayList<Object> pointless = new ArrayList<Object>(); if (pointless.size() == 1) return i + 1; else return 0;}");
	}

	@Then("package $packageName exists in project $projectName")
	public void verifyPackageExists(String packageName, String projectName) {
		assertTrue("Package: " + packageName + " not found",
				PAGES.getPackageExplorer().doesPackageExistInProject(packageName, projectName));
	}

	@Then("class $className exists in package $packageName in project $projectName")
	public void verifyClassExists(String className, String packageName, String projectName) {
		assertTrue("Class: " + className + " not found",
				PAGES.getPackageExplorer().doesClassExistInProject(className, packageName, projectName));
	}

	@Given("the class $testClassName in package $packageName in project $projectName is selected")
	public void selectClass(String className, String packageName, String projectName) {
		concreteClassContext = new ConcreteClassContext.Builder().withProjectName(projectName)
				.withPackageName(packageName).withClassName(className).build();
	}

	@When("a method \"$method\" is created")
	public void createMethod(String method) {
		concreteClassContext = new ConcreteClassContext.Builder().clone(concreteClassContext).withMethod(method)
				.build();
		PAGES.getBuildProgress().listenForBuild();
		PAGES.getPackageExplorer().openClass(concreteClassContext);
		PAGES.getAbstractSyntaxTree().addMethod(concreteClassContext);
		PAGES.getSourceMenu().organizeImports();
		PAGES.getSourceMenu().format();
		PAGES.getFileMenu().saveAll();
		PAGES.getBuildProgress().waitForBuild();
	}

	@When("the class is renamed to $newClassName")
	public void renameClass(String newClassName) {
		PAGES.getBuildProgress().listenForBuild();
		PAGES.getPackageExplorer().selectClass(concreteClassContext.getClassName(),
				concreteClassContext.getPackageName(), concreteClassContext.getProjectName());
		PAGES.getRefactorMenu().renameClass(newClassName);
		PAGES.getBuildProgress().waitForBuild();
	}

	@Given("the package $packageName in project $projectName is selected")
	public void selectPackage(String packageName, String projectName) {
		concreteClassContext = new ConcreteClassContext.Builder().withProjectName(projectName)
				.withPackageName(packageName).build();
	}

	@When("the package is renamed to $packageName")
	public void renamePackage(String packageName) {
		PAGES.getBuildProgress().listenForBuild();
		PAGES.getPackageExplorer().selectPackage(concreteClassContext);
		PAGES.getRefactorMenu().renamePackage(packageName);
		PAGES.getBuildProgress().waitForBuild();
	}

	private static void createClass(String className, String packageName, String projectName) {
		PAGES.getBuildProgress().listenForBuild();
		PAGES.getPackageExplorer().selectPackageRoot(projectName, "src");
		// Cannot use the Package explorer right click context menu
		// to create a class due to SWTBot bug 261360
		PAGES.getFileMenu().createClass(packageName, className);
		PAGES.getBuildProgress().waitForBuild();
	}
}
