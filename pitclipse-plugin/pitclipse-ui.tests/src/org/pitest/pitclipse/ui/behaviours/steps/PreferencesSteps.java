package org.pitest.pitclipse.ui.behaviours.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.pitest.pitclipse.core.PitExecutionMode.PROJECT_ISOLATION;
import static org.pitest.pitclipse.core.PitExecutionMode.WORKSPACE;
import static org.pitest.pitclipse.ui.behaviours.pageobjects.PageObjects.INSTANCE;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.pitest.pitclipse.core.PitExecutionMode;

public class PreferencesSteps {

	@When("the isolate tests at project scope preference is selected")
	public void testProjectsInIsolation() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(PROJECT_ISOLATION);
	}

	@Then("the project level scope preference is selected")
	public void projectScopePreferenceIsChosen() {
		assertEquals(PROJECT_ISOLATION, INSTANCE.getWindowsMenu()
				.getPitExecutionMode());
	}

	@When("the workspace level scope preference is selected")
	public void testProjectsInWorkspace() {
		INSTANCE.getWindowsMenu().setPitExecutionMode(WORKSPACE);
	}

	@Then("the workspace level scope preference is selected")
	public void workspacePreferenceIsChosen() {
		PitExecutionMode pitExecutionMode = INSTANCE.getWindowsMenu()
				.getPitExecutionMode();
		assertEquals(WORKSPACE, pitExecutionMode);
	}

	@Then("the mutation tests run in parallel preference is selected")
	public void runInParallelPreferenceIsChosen() {
		assertTrue(INSTANCE.getWindowsMenu().isPitRunInParallel());
	}

	@When("the mutation tests run in parallel preference is selected")
	public void setPreferenceToRunInParallel() {
		INSTANCE.getWindowsMenu().setPitRunInParallel(true);
	}

	@When("the mutation tests run in parallel preference is deselected")
	public void setPreferenceToNotRunInParallel() {
		INSTANCE.getWindowsMenu().setPitRunInParallel(false);
	}

	@When("the mutation tests use incremental analysis preference is selected")
	public void setPreferenceToRunIncrementalAnalysis() {
		INSTANCE.getWindowsMenu().setIncrementalAnalysisEnabled(true);
	}

	@When("the mutation tests use incremental analysis preference is deselected")
	public void setPreferenceToNotRunIncrementalAnalysis() {
		INSTANCE.getWindowsMenu().setIncrementalAnalysisEnabled(false);
	}

	@Then("the use incremental analysis preference is not selected")
	public void useIncrementalAnalysis() {
		assertFalse(INSTANCE.getWindowsMenu().isIncrementalAnalysisEnabled());
	}

	@When("the excluded classes preference is not set")
	public void setNoClassesAreExcluded() {
		INSTANCE.getWindowsMenu().setExcludedClasses("");
	}

	@When("the excluded classes preference is set to \"$excludedClasses\"")
	public void setExcludedClasses(String excludedClasses) {
		INSTANCE.getWindowsMenu().setExcludedClasses(excludedClasses);
	}

	@Then("the excluded classes preference is not set")
	public void noClassesAreExcluded() {
		String excludedClasses = INSTANCE.getWindowsMenu().getExcludedClasses();
		assertNotNull(excludedClasses);
		assertTrue(excludedClasses.isEmpty());
	}
}
