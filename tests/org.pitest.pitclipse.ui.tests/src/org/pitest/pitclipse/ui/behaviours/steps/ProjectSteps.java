package org.pitest.pitclipse.ui.behaviours.steps;

import org.eclipse.core.resources.IProject;
import org.pitest.pitclipse.core.PitCoreActivator;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.PAGES;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ProjectSteps {

    @When("the user creates a project with name {word}")
    public void createJavaProject(String projectName) {
        PAGES.getFileMenu().newJavaProject(projectName);
        PAGES.getAbstractSyntaxTree().addJUnitToClassPath(projectName);
    }

    @Then("the project {word} exists in the workspace")
    public void verifyProjectExists(String projectName) {
        for (String project : PAGES.getPackageExplorer().getProjectsInWorkspace()) {
            if (projectName.equals(project)) {
                // Project does indeed exist
                return;
            }
        }
        fail("Project: " + projectName + " not found.");
    }

    @Given("an empty workspace")
    public void deleteAllProjects() {
        for (String project : PAGES.getPackageExplorer().getProjectsInWorkspace()) {
            PAGES.getAbstractSyntaxTree().deleteProject(project);
        }
        File historyFile = PitCoreActivator.getDefault().getHistoryFile();
        if (historyFile.exists()) {
            assertTrue(historyFile.delete());
        }
    }

    @When("the dependent project {word} is added to the classpath of {word}")
    public void addToBuildPath(String dependentProject, String projectName) {
        PAGES.getPackageExplorer().selectProject(projectName);
        PAGES.getAbstractSyntaxTree().addProjectToClassPathOfProject(projectName, dependentProject);
    }
}
