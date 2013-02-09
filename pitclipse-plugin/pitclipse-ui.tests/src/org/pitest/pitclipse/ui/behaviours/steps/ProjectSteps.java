package org.pitest.pitclipse.ui.behaviours.steps;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import java.io.File;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.pitest.pitclipse.core.PitCoreActivator;

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
		File historyFile = PitCoreActivator.getDefault().getHistoryFile();
		if (historyFile.exists()) {
			assertTrue(historyFile.delete());
		}
	}

	@When("the dependent project $dependentProject is added to the classpath of $project")
	public void addToBuildPath(String dependentProject, String projectName) {
		INSTANCE.getPackageExplorer().selectProject(projectName);
		INSTANCE.getAbstractSyntaxTree().addProjectToClassPathOfProject(
				projectName, dependentProject);
	}
}
