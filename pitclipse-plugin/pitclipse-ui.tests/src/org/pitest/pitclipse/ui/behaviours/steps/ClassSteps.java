package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.assertTrue;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;

public final class ClassSteps {

	@When("a class $className in package $packageName is created in project $projectName")
	public void createClass(String className, String packageName,
			String projectName) {
		INSTANCE.getPackageExplorer().selectProject(projectName);
		// Cannot use the Package exlorer right click context menu
		// to create a class due to SWTBot bug 261360
		INSTANCE.getfileMenu().createClass(packageName, className);
	}

	@Then("package $packageName exists in project $projectName")
	public void verifyPackageExists(String packageName, String projectName) {
		assertTrue(
				"Package: " + packageName + " not found",
				INSTANCE.getPackageExplorer().doesPackageExistInProject(
						packageName, projectName));
	}

	@Then("class $className exists in package $packageName in project $projectName")
	public void verifyClassExists(String className, String packageName,
			String projectName) {
		assertTrue(
				"Package: " + packageName + " not found",
				INSTANCE.getPackageExplorer().doesClassExistInProject(
						className, packageName, projectName));
	}

}
