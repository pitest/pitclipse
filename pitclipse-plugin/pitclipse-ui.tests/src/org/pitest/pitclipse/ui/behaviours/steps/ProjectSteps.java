package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.pitest.pitclipse.ui.behaviours.Then;
import org.pitest.pitclipse.ui.behaviours.When;

public class ProjectSteps {

	@When("the user creates a project with name $projectName")
	public void createJavaProject(String projectName) {
		INSTANCE.getFileMenu().newJavaProject(projectName);
	}

	@Then("the project $projectName exists in the workspace")
	public void verifyProjectExists(String projectName) {
		for (String project : INSTANCE.getPackageExplorer()
				.getProjectsInWorkspace()) {
			if (projectName.equals(project)) {
				// Project does indeed exist
				return;
			}
		}
		fail("Project: " + projectName + " not found.");
	}

}
