package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class ProjectSteps {

	@When("the user creates a project with name $projectName")
	public void createJavaProject(String projectName) {
		INSTANCE.getFileMenu().newJavaProject(projectName);
		INSTANCE.getAbstractSyntaxTree().addJUnitToClassPath(projectName);
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

	@Given("an empty workspace")
	public void deleteAllProjects() {
		for (String project : INSTANCE.getPackageExplorer()
				.getProjectsInWorkspace()) {
			INSTANCE.getAbstractSyntaxTree().deleteProject(project);
		}
	}

	public void addToBuildPath(String projectName, List<String> projects) {
		INSTANCE.getPackageExplorer().selectProject(projectName);
		for (String project : projects) {
			INSTANCE.getAbstractSyntaxTree().addProjectToClassPathOfProject(
					projectName, project);
		}
	}
}