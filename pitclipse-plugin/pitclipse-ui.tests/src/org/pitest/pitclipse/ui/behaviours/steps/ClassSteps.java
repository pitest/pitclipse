package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.assertTrue;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;
import org.pitest.pitclipse.ui.behaviours.pageobjects.TestClassContext;

public final class ClassSteps {

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

	public void createTestClass(String projectName, String packageName,
			String testClassName) {
		TestClassContext context = new TestClassContext(testClassName,
				packageName, projectName);
		INSTANCE.getBuildProgress().listenForBuild();
		INSTANCE.getPackageExplorer().selectProject(context.getProjectName());
		INSTANCE.getFileMenu().createJUnitTest(context);
		INSTANCE.getBuildProgress().waitForBuild();
		INSTANCE.getAbstractSyntaxTree().removeAllMethods(context);
		INSTANCE.getFileMenu().saveAll();
	}

}
